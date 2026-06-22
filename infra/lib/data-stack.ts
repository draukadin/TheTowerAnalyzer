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

    // IAM user for the local Spring Boot app — least-privilege read from S3, write to DDB.
    // After deploy: aws iam create-access-key --user-name <AppUserName output>
    const appUser = new iam.User(this, 'AppUser', {
      userName: `tower-analyzer-app-${env}`,
    });

    appUser.addToPolicy(new iam.PolicyStatement({
      sid: 'S3ReadAndTag',
      actions: ['s3:ListBucket', 's3:GetObject', 's3:PutObjectTagging', 's3:DeleteObject'],
      resources: [reportsBucket.bucketArn, `${reportsBucket.bucketArn}/*`],
    }));

    appUser.addToPolicy(new iam.PolicyStatement({
      sid: 'DDBVersionWrite',
      actions: ['dynamodb:PutItem'],
      resources: [versionTable.tableArn],
    }));

    this.bucketName = reportsBucket.bucketName;
    this.versionTableName = versionTable.tableName;

    new cdk.CfnOutput(this, 'AppUserName', {
      value: appUser.userName,
      description: 'IAM user for Spring Boot app — run: aws iam create-access-key --user-name <value>',
      exportName: `TowerAnalyzer-${env}-AppUserName`,
    });

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
