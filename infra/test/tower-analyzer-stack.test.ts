import * as cdk from 'aws-cdk-lib/core';
import { Template } from 'aws-cdk-lib/assertions';
import { DataStack } from '../lib/data-stack';
import { IngestStack } from '../lib/ingest-stack';

const ACCOUNT = '611434859239';

// ── DataStack ─────────────────────────────────────────────────────────────────

test('DataStack: S3 bucket has tag-based Glacier lifecycle rule', () => {
  const app = new cdk.App();
  const stack = new DataStack(app, 'TestDataStack', {
    environment: 'dev',
    env: { account: ACCOUNT, region: 'us-west-2' },
  });
  const template = Template.fromStack(stack);

  template.hasResourceProperties('AWS::S3::Bucket', {
    LifecycleConfiguration: {
      Rules: [{ Status: 'Enabled', TagFilters: [{ Key: 'processed', Value: 'true' }] }],
    },
  });
});

test('DataStack: DynamoDB table has player_id partition key and pay-per-request billing', () => {
  const app = new cdk.App();
  const stack = new DataStack(app, 'TestDataStack', {
    environment: 'dev',
    env: { account: ACCOUNT, region: 'us-west-2' },
  });
  const template = Template.fromStack(stack);

  template.hasResourceProperties('AWS::DynamoDB::Table', {
    KeySchema: [{ AttributeName: 'player_id', KeyType: 'HASH' }],
    BillingMode: 'PAY_PER_REQUEST',
  });
});

test('DataStack: dev and prod DynamoDB table names are distinct', () => {
  const app = new cdk.App();
  const devStack = new DataStack(app, 'DevDataStack', {
    environment: 'dev',
    env: { account: ACCOUNT, region: 'us-west-2' },
  });
  const prodStack = new DataStack(app, 'ProdDataStack', {
    environment: 'prod',
    env: { account: ACCOUNT, region: 'us-east-2' },
  });

  Template.fromStack(devStack).hasResourceProperties('AWS::S3::Bucket', {
    BucketName: `tower-analyzer-reports-dev-${ACCOUNT}`,
  });
  Template.fromStack(prodStack).hasResourceProperties('AWS::S3::Bucket', {
    BucketName: `tower-analyzer-reports-prod-${ACCOUNT}`,
  });
  Template.fromStack(devStack).hasResourceProperties('AWS::DynamoDB::Table', {
    TableName: 'TowerAnalyzerPlayerVersion-dev',
  });
  Template.fromStack(prodStack).hasResourceProperties('AWS::DynamoDB::Table', {
    TableName: 'TowerAnalyzerPlayerVersion-prod',
  });
});

// ── IngestStack ───────────────────────────────────────────────────────────────

test('IngestStack: Lambda configured with correct runtime and handler', () => {
  const app = new cdk.App();
  const stack = new IngestStack(app, 'TestIngestStack', {
    environment: 'dev',
    centralBucketName: 'tower-analyzer-reports-dev-611434859239',
    env: { account: ACCOUNT, region: 'us-west-2' },
  });
  const template = Template.fromStack(stack);

  template.hasResourceProperties('AWS::Lambda::Function', {
    Runtime: 'nodejs22.x',
    Handler: 'ingest-report.handler',
  });
});

test('IngestStack: API Gateway POST /reports method exists', () => {
  const app = new cdk.App();
  const stack = new IngestStack(app, 'TestIngestStack', {
    environment: 'dev',
    centralBucketName: 'tower-analyzer-reports-dev-611434859239',
    env: { account: ACCOUNT, region: 'us-west-2' },
  });
  const template = Template.fromStack(stack);

  // POST on /reports + OPTIONS (CORS preflight) + CDK root ANY method = 3
  template.resourceCountIs('AWS::ApiGateway::Method', 3);
});

test('IngestStack: per-region Secrets Manager secret created', () => {
  const app = new cdk.App();
  const stack = new IngestStack(app, 'TestIngestStack', {
    environment: 'dev',
    centralBucketName: 'tower-analyzer-reports-dev-611434859239',
    env: { account: ACCOUNT, region: 'us-west-2' },
  });
  const template = Template.fromStack(stack);

  template.hasResourceProperties('AWS::SecretsManager::Secret', {
    Name: 'TowerAnalyzerApiKey-dev-us-west-2',
  });
});
