#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib/core';
import { DataStack } from '../lib/data-stack';
import { IngestStack } from '../lib/ingest-stack';
import { MonitoringStack } from '../lib/monitoring-stack';

const app = new cdk.App();
const ACCOUNT = '611434859239';
const ALERT_EMAIL = 'draukadin+tower-analyzer-admin@gmail.com';

cdk.Tags.of(app).add('Project', 'TheTowerAnalyzer');

// ── Dev (us-west-2) ───────────────────────────────────────────────────────────
const devData = new DataStack(app, 'TowerAnalyzer-Dev-Data', {
  environment: 'dev',
  env: { account: ACCOUNT, region: 'us-west-2' },
});
cdk.Tags.of(devData).add('Env', 'dev');

const devIngest = new IngestStack(app, 'TowerAnalyzer-Dev-Ingest-USW2', {
  environment: 'dev',
  centralBucketName: devData.bucketName,
  versionTableName: devData.versionTableName,
  versionTableArn: devData.versionTableArn,
  credentialRoleArn: devData.credentialRoleArn,
  dataRegion: 'us-west-2',
  env: { account: ACCOUNT, region: 'us-west-2' },
});
cdk.Tags.of(devIngest).add('Env', 'dev');

// ── Prod monitoring (AWS Budgets — account-level, deployed to us-east-2) ─────
const monitoring = new MonitoringStack(app, 'TowerAnalyzer-Prod-Monitoring', {
  alertEmail: ALERT_EMAIL,
  env: { account: ACCOUNT, region: 'us-east-2' },
});
cdk.Tags.of(monitoring).add('Env', 'prod');

// ── Prod (us-east-2 primary) ──────────────────────────────────────────────────
const prodData = new DataStack(app, 'TowerAnalyzer-Prod-Data', {
  environment: 'prod',
  env: { account: ACCOUNT, region: 'us-east-2' },
});
cdk.Tags.of(prodData).add('Env', 'prod');

const prodIngestUS = new IngestStack(app, 'TowerAnalyzer-Prod-Ingest-USE2', {
  environment: 'prod',
  centralBucketName: prodData.bucketName,
  versionTableName: prodData.versionTableName,
  versionTableArn: prodData.versionTableArn,
  credentialRoleArn: prodData.credentialRoleArn,
  dataRegion: 'us-east-2',
  alertEmail: ALERT_EMAIL,
  env: { account: ACCOUNT, region: 'us-east-2' },
});
cdk.Tags.of(prodIngestUS).add('Env', 'prod');

const prodIngestEU = new IngestStack(app, 'TowerAnalyzer-Prod-Ingest-EUC1', {
  environment: 'prod',
  centralBucketName: prodData.bucketName,
  versionTableName: prodData.versionTableName,
  versionTableArn: prodData.versionTableArn,
  credentialRoleArn: prodData.credentialRoleArn,
  dataRegion: 'us-east-2',
  alertEmail: ALERT_EMAIL,
  env: { account: ACCOUNT, region: 'eu-west-1' },
});
cdk.Tags.of(prodIngestEU).add('Env', 'prod');

const prodIngestAP = new IngestStack(app, 'TowerAnalyzer-Prod-Ingest-APN1', {
  environment: 'prod',
  centralBucketName: prodData.bucketName,
  versionTableName: prodData.versionTableName,
  versionTableArn: prodData.versionTableArn,
  credentialRoleArn: prodData.credentialRoleArn,
  dataRegion: 'us-east-2',
  alertEmail: ALERT_EMAIL,
  env: { account: ACCOUNT, region: 'ap-northeast-1' },
});
cdk.Tags.of(prodIngestAP).add('Env', 'prod');