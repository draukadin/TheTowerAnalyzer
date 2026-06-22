import { DynamoDBClient, UpdateItemCommand } from '@aws-sdk/client-dynamodb';
import { STSClient, AssumeRoleCommand } from '@aws-sdk/client-sts';

const ddb = new DynamoDBClient({ region: process.env.DATA_REGION });
const sts = new STSClient({});

const BUCKET = process.env.REPORTS_BUCKET;
const TABLE = process.env.PLAYER_VERSION_TABLE;
const TABLE_ARN = process.env.PLAYER_VERSION_TABLE_ARN;
const ROLE_ARN = process.env.CREDENTIAL_ROLE_ARN;

function respond(statusCode, body) {
  return { statusCode, body: JSON.stringify(body) };
}

export const handler = async (event) => {
  const playerId = (event.headers?.['x-player-id'] ?? event.headers?.['X-Player-Id'] ?? '').trim();
  if (!playerId) {
    return respond(400, { message: 'Missing X-Player-Id header' });
  }

  const callerIp = event.requestContext?.identity?.sourceIp;

  // Register player on first call; leave existing record unchanged
  await ddb.send(new UpdateItemCommand({
    TableName: TABLE,
    Key: { player_id: { S: playerId } },
    UpdateExpression: 'SET registered_at = if_not_exists(registered_at, :now)',
    ExpressionAttributeValues: { ':now': { S: new Date().toISOString() } },
  }));

  const ipCondition = callerIp ? { IpAddress: { 'aws:SourceIp': callerIp } } : undefined;

  const listBucketStatement = {
    Effect: 'Allow',
    Action: 's3:ListBucket',
    Resource: `arn:aws:s3:::${BUCKET}`,
    Condition: {
      StringLike: { 's3:prefix': [`${playerId}/*`] },
      ...(ipCondition ?? {}),
    },
  };

  const s3ObjectStatement = {
    Effect: 'Allow',
    Action: ['s3:GetObject', 's3:PutObject', 's3:DeleteObject', 's3:CopyObject', 's3:PutObjectTagging'],
    Resource: `arn:aws:s3:::${BUCKET}/${playerId}/*`,
  };
  if (ipCondition) s3ObjectStatement.Condition = ipCondition;

  const ddbStatement = {
    Effect: 'Allow',
    Action: ['dynamodb:PutItem', 'dynamodb:GetItem'],
    Resource: TABLE_ARN,
    Condition: {
      'ForAllValues:StringEquals': { 'dynamodb:LeadingKeys': [playerId] },
      ...(ipCondition ?? {}),
    },
  };

  const sessionPolicy = JSON.stringify({
    Version: '2012-10-17',
    Statement: [listBucketStatement, s3ObjectStatement, ddbStatement],
  });

  const sessionName = `player-${playerId.replace(/[^\w+=,.@-]/g, '_')}`.slice(0, 64);

  const assumed = await sts.send(new AssumeRoleCommand({
    RoleArn: ROLE_ARN,
    RoleSessionName: sessionName,
    DurationSeconds: 3600,
    Policy: sessionPolicy,
  }));

  const c = assumed.Credentials;
  return respond(200, {
    AccessKeyId: c.AccessKeyId,
    SecretAccessKey: c.SecretAccessKey,
    SessionToken: c.SessionToken,
    Expiration: c.Expiration,
  });
};