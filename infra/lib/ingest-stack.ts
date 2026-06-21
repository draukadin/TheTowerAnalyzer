import * as cdk from 'aws-cdk-lib/core';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import { Construct } from 'constructs';
import * as path from 'path';

export interface IngestStackProps extends cdk.StackProps {
  environment: 'dev' | 'prod';
  /** Name of the central S3 bucket owned by DataStack (may be in a different region) */
  centralBucketName: string;
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

    const api = new apigateway.RestApi(this, 'ReportApi', {
      restApiName: `TowerAnalyzerReportApi-${env}`,
      description: `Battle report ingest endpoint (${env} / ${this.region})`,
      deployOptions: {
        throttlingRateLimit: 10,
        throttlingBurstLimit: 20,
      },
      defaultCorsPreflightOptions: {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: ['POST'],
        allowHeaders: ['Content-Type', 'X-Player-Id'],
      },
    });

    const reports = api.root.addResource('reports');
    reports.addMethod('POST', new apigateway.LambdaIntegration(ingestFn));

    new cdk.CfnOutput(this, 'ApiEndpoint', {
      value: `${api.url}reports`,
      description: 'Battle report ingest URL — set in Android app and iOS Shortcut',
      exportName: `TowerAnalyzer-${env}-ApiEndpoint-${this.region}`,
    });
  }
}
