import * as cdk from 'aws-cdk-lib/core';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

export interface DataStackProps extends cdk.StackProps {
  environment: 'dev' | 'prod';
}

export class DataStack extends cdk.Stack {
  readonly bucketName: string;
  readonly versionTableName: string;
  readonly versionTableArn: string;
  readonly credentialRoleArn: string;

  constructor(scope: Construct, id: string, props: DataStackProps) {
    super(scope, id, props);

    const env = props.environment;

    const reportsBucket = new s3.Bucket(this, 'ReportsBucket', {
      bucketName: `tower-analyzer-reports-${env}-${this.account}`,
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
      encryption: s3.BucketEncryption.S3_MANAGED,
      lifecycleRules: [
        {
          // Tag-based rule - processed objects live at <player_id>/processed/<file>
          // so a top-level prefix wouldn't match nested paths.
          tagFilters: { processed: 'true' },
          transitions: [
            {
              storageClass: s3.StorageClass.GLACIER_INSTANT_RETRIEVAL,
              transitionAfter: cdk.Duration.days(1),
            },
          ],
        },
        {
          // Database backups (Action item 8). The newest backup is tagged
          // type=backup-latest (kept forever, not matched here); superseded backups are
          // demoted to type=backup, which this rule expires 30 days later. Demotion
          // rewrites the object via CopyObject, resetting its creation date, so expiry
          // fires exactly 30 days after a backup stopped being the latest. Tag-based
          // because backups live nested at <player_id>/backups/ (no top-level prefix match).
          tagFilters: { type: 'backup' },
          expiration: cdk.Duration.days(30),
        },
      ],
      removalPolicy: cdk.RemovalPolicy.RETAIN,
    });

    const versionTable = new dynamodb.Table(this, 'PlayerVersionTable', {
      tableName: `TowerAnalyzerPlayerVersion-${env}`,
      partitionKey: { name: 'player_id', type: dynamodb.AttributeType.STRING },
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
      removalPolicy: cdk.RemovalPolicy.RETAIN,
    });

    // Role assumed programmatically by the credential-vending Lambda's execution role
    // via sts:AssumeRole. The role's permissions are the superset; the session policy
    // passed at AssumeRole time restricts each credential to a single player's prefix + IP.
    // Trusts the account root so any IAM principal in this account can assume it -
    // access is controlled by the sts:AssumeRole permission on the Lambda execution role.
    const credentialVendingRole = new iam.Role(this, 'CredentialVendingRole', {
      roleName: `tower-analyzer-credential-vending-${env}`,
      assumedBy: new iam.AccountRootPrincipal(),
      description: 'Assumed programmatically by the credential-vending Lambda execution role via sts:AssumeRole',
    });

    credentialVendingRole.addToPolicy(new iam.PolicyStatement({
      sid: 'S3PlayerAccess',
      actions: ['s3:ListBucket', 's3:GetObject', 's3:PutObject', 's3:DeleteObject', 's3:CopyObject', 's3:GetObjectTagging', 's3:PutObjectTagging'],
      resources: [reportsBucket.bucketArn, `${reportsBucket.bucketArn}/*`],
    }));

    credentialVendingRole.addToPolicy(new iam.PolicyStatement({
      sid: 'DDBPlayerAccess',
      actions: ['dynamodb:PutItem', 'dynamodb:GetItem'],
      resources: [versionTable.tableArn],
    }));

    this.bucketName = reportsBucket.bucketName;
    this.versionTableName = versionTable.tableName;
    this.versionTableArn = versionTable.tableArn;
    this.credentialRoleArn = credentialVendingRole.roleArn;

    new cdk.CfnOutput(this, 'ReportsBucketName', {
      value: reportsBucket.bucketName,
      description: 'S3 bucket name - operator reference only; value is bundled in application.properties',
      exportName: `TowerAnalyzer-${env}-ReportsBucketName`,
    });
    new cdk.CfnOutput(this, 'PlayerVersionTableName', {
      value: versionTable.tableName,
      description: 'DynamoDB table name - operator reference only; value is bundled in application.properties',
      exportName: `TowerAnalyzer-${env}-PlayerVersionTableName`,
    });
  }
}
