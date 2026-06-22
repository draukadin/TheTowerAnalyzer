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
          // Tag-based rule — processed objects live at <player_id>/processed/<file>
          // so a top-level prefix wouldn't match nested paths.
          tagFilters: { processed: 'true' },
          transitions: [
            {
              storageClass: s3.StorageClass.GLACIER_INSTANT_RETRIEVAL,
              transitionAfter: cdk.Duration.days(1),
            },
          ],
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

    // Role assumed by the credential-vending Lambda to issue per-player STS sessions.
    // The role's permissions are the superset; the session policy passed at AssumeRole
    // time restricts each set of credentials to a single player's prefix + IP.
    const credentialVendingRole = new iam.Role(this, 'CredentialVendingRole', {
      roleName: `tower-analyzer-credential-vending-${env}`,
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
      description: 'Assumed by the credential-vending Lambda; session policy scopes each credential to one player',
    });

    credentialVendingRole.addToPolicy(new iam.PolicyStatement({
      sid: 'S3PlayerAccess',
      actions: ['s3:ListBucket', 's3:GetObject', 's3:PutObject', 's3:DeleteObject', 's3:CopyObject', 's3:PutObjectTagging'],
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
      description: 'S3 bucket — set aws.s3.bucket in user.properties',
      exportName: `TowerAnalyzer-${env}-ReportsBucketName`,
    });
    new cdk.CfnOutput(this, 'PlayerVersionTableName', {
      value: versionTable.tableName,
      description: 'DynamoDB table — set aws.dynamodb.table in user.properties',
      exportName: `TowerAnalyzer-${env}-PlayerVersionTableName`,
    });
  }
}
