import * as cdk from 'aws-cdk-lib/core';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';
import * as path from 'path';

export interface IngestStackProps extends cdk.StackProps {
  environment: 'dev' | 'prod';
  /** Name of the central S3 bucket owned by DataStack (may be in a different region) */
  centralBucketName: string;
  /** DynamoDB table name — passed as env var to credentials Lambda */
  versionTableName: string;
  /** Full DynamoDB table ARN — embedded in the STS session policy */
  versionTableArn: string;
  /** ARN of the IAM role the credentials Lambda will AssumeRole into */
  credentialRoleArn: string;
  /** AWS region where S3 and DynamoDB live (us-east-2) — DDB client in credentials Lambda uses this */
  dataRegion: string;
}

export class IngestStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: IngestStackProps) {
    super(scope, id, props);

    const env = props.environment;

    // Import the central S3 bucket by name — cross-region import, no CDK ref needed
    const reportsBucket = s3.Bucket.fromBucketName(this, 'ReportsBucket', props.centralBucketName);

    const ingestFn = new lambda.Function(this, 'IngestReportFn', {
      functionName: `TowerAnalyzerIngestReport-${env}`,
      runtime: lambda.Runtime.NODEJS_22_X,
      handler: 'ingest-report.handler',
      code: lambda.Code.fromAsset(path.join(__dirname, '../lambda')),
      environment: {
        REPORTS_BUCKET: props.centralBucketName,
      },
      timeout: cdk.Duration.seconds(10),
      memorySize: 128,
    });

    reportsBucket.grantPut(ingestFn);

    const credentialsFn = new lambda.Function(this, 'CredentialVendingFn', {
      functionName: `TowerAnalyzerCredentialVending-${env}`,
      runtime: lambda.Runtime.NODEJS_22_X,
      handler: 'credentials.handler',
      code: lambda.Code.fromAsset(path.join(__dirname, '../lambda')),
      environment: {
        REPORTS_BUCKET: props.centralBucketName,
        PLAYER_VERSION_TABLE: props.versionTableName,
        PLAYER_VERSION_TABLE_ARN: props.versionTableArn,
        CREDENTIAL_ROLE_ARN: props.credentialRoleArn,
        DATA_REGION: props.dataRegion,
      },
      timeout: cdk.Duration.seconds(10),
      memorySize: 128,
    });

    // Upsert player record in DDB on first credential vend
    credentialsFn.addToRolePolicy(new iam.PolicyStatement({
      sid: 'DDBUpsertPlayer',
      actions: ['dynamodb:UpdateItem'],
      resources: [props.versionTableArn],
    }));

    // Assume the per-player credential vending role
    credentialsFn.addToRolePolicy(new iam.PolicyStatement({
      sid: 'STSAssumeCredentialRole',
      actions: ['sts:AssumeRole'],
      resources: [props.credentialRoleArn],
    }));

    const api = new apigateway.RestApi(this, 'ReportApi', {
      restApiName: `TowerAnalyzerReportApi-${env}`,
      description: `Battle report ingest endpoint (${env} / ${this.region})`,
      deployOptions: {
        throttlingRateLimit: 10,
        throttlingBurstLimit: 20,
      },
      defaultCorsPreflightOptions: {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: ['POST', 'GET'],
        allowHeaders: ['Content-Type', 'X-Player-Id'],
      },
    });

    const reports = api.root.addResource('reports');
    reports.addMethod('POST', new apigateway.LambdaIntegration(ingestFn));

    const credentials = api.root.addResource('credentials');
    credentials.addMethod('GET', new apigateway.LambdaIntegration(credentialsFn));

    new cdk.CfnOutput(this, 'ApiEndpoint', {
      value: `${api.url}reports`,
      description: 'Battle report ingest URL - set in Android app and iOS Shortcut',
      exportName: `TowerAnalyzer-${env}-ApiEndpoint-${this.region}`,
    });

    new cdk.CfnOutput(this, 'CredentialsEndpoint', {
      value: `${api.url}credentials`,
      description: 'Credential-vending URL - set as aws.api-gateway.url.* in application.properties',
      exportName: `TowerAnalyzer-${env}-CredentialsEndpoint-${this.region}`,
    });
  }
}
