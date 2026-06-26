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

  // Register player on first call; leave existing record unchanged
  await ddb.send(new UpdateItemCommand({
    TableName: TABLE,
    Key: { player_id: { S: playerId } },
    UpdateExpression: 'SET registered_at = if_not_exists(registered_at, :now)',
    ExpressionAttributeValues: { ':now': { S: new Date().toISOString() } },
  }));

  const listBucketStatement = {
    Effect: 'Allow',
    Action: 's3:ListBucket',
    Resource: `arn:aws:s3:::${BUCKET}`,
    Condition: {
      // Allow listing the player's own prefix and the shared tournament prefix.
      StringLike: { 's3:prefix': [`${playerId}/*`, 'tournaments/*'] },
    },
  };

  const s3ObjectStatement = {
    Effect: 'Allow',
    Action: ['s3:GetObject', 's3:PutObject', 's3:DeleteObject', 's3:CopyObject', 's3:GetObjectTagging', 's3:PutObjectTagging'],
    Resource: `arn:aws:s3:::${BUCKET}/${playerId}/*`,
  };

  // Shared tournament CSV store — read/write by any player, no delete/copy/tagging needed.
  const s3TournamentStatement = {
    Effect: 'Allow',
    Action: ['s3:GetObject', 's3:PutObject'],
    Resource: `arn:aws:s3:::${BUCKET}/tournaments/*`,
  };

  const ddbStatement = {
    Effect: 'Allow',
    Action: ['dynamodb:PutItem', 'dynamodb:GetItem'],
    Resource: TABLE_ARN,
    Condition: {
      'ForAllValues:StringEquals': { 'dynamodb:LeadingKeys': [playerId] },
    },
  };

  const sessionPolicy = JSON.stringify({
    Version: '2012-10-17',
    Statement: [listBucketStatement, s3ObjectStatement, s3TournamentStatement, ddbStatement],
  });

  const sessionName = `player-${playerId.replace(/[^\w+=,.@-]/g, '_')}`.slice(0, 64);

  let assumed;
  try {
    assumed = await sts.send(new AssumeRoleCommand({
      RoleArn: ROLE_ARN,
      RoleSessionName: sessionName,
      DurationSeconds: 3600,
      Policy: sessionPolicy,
    }));
  } catch (err) {
    console.error('AssumeRole failed', err);
    return respond(500, { message: 'Failed to vend credentials' });
  }

  const c = assumed.Credentials;
  return respond(200, {
    AccessKeyId: c.AccessKeyId,
    SecretAccessKey: c.SecretAccessKey,
    SessionToken: c.SessionToken,
    Expiration: c.Expiration,
  });
};