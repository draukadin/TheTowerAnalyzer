import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import { DynamoDBClient, GetItemCommand } from '@aws-sdk/client-dynamodb';

const s3 = new S3Client({ region: process.env.BUCKET_REGION });
const ddb = new DynamoDBClient({ region: process.env.BUCKET_REGION });

const BUCKET = process.env.REPORTS_BUCKET;
const VERSION_TABLE = process.env.VERSION_TABLE;

// Mirrors SectionHeader enum display names from BattleHistoryParser
const SECTION_HEADERS = new Set([
  'Battle Report',
  'Records',
  'Damage',
  'Damage Taken',
  'Bonus Health Gained',
  'Health Regenerated',
  'Damage Blocked',
  'Utility',
  'Counts',
  'Enemies Hit By',
  'Killed With Effect Active',
  'Total Enemies',
  'Coins',
  'Cash',
  'Currencies',
  'Enemies Destroyed By',
]);

const MIN_SECTION_COUNT = 5;
const MAX_BODY_BYTES = 15_000;

function respond(statusCode, message) {
  return {
    statusCode,
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ message }),
  };
}

async function getCurrentVersion(playerId) {
  if (!VERSION_TABLE) return null;
  try {
    const result = await ddb.send(new GetItemCommand({
      TableName: VERSION_TABLE,
      Key: { player_id: { S: playerId } },
    }));
    return result.Item?.current_version?.S ?? null;
  } catch {
    return null;
  }
}

function injectTowerEra(body, version) {
  const lines = body.split('\n');
  lines.splice(1, 0, `Tower Era\t${version ?? '1.0.0'}`);
  return lines.join('\n');
}

export const handler = async (event) => {
  // Player ID
  const playerId = event.headers?.['x-player-id'] ?? event.headers?.['X-Player-Id'];
  if (!playerId || playerId.trim() === '') {
    return respond(400, 'Missing X-Player-Id header');
  }

  // Payload size
  const body = event.body ?? '';
  const bodyBytes = Buffer.byteLength(body, 'utf8');
  if (bodyBytes === 0) {
    return respond(400, 'Empty body');
  }
  if (bodyBytes > MAX_BODY_BYTES) {
    return respond(400, 'Payload too large');
  }

  // First line must be a known section header
  const lines = body.split('\n');
  const firstLine = lines[0].trim();
  if (!SECTION_HEADERS.has(firstLine)) {
    return respond(400, 'Invalid report format');
  }

  // Minimum section header count
  const sectionCount = lines.filter(l => SECTION_HEADERS.has(l.trim())).length;
  if (sectionCount < MIN_SECTION_COUNT) {
    return respond(400, 'Invalid report format');
  }

  // Determine run type (and optional dissonance sub-type) from query string
  const runType = event.queryStringParameters?.runType ?? 'Unknown';
  const dissonanceType = event.queryStringParameters?.dissonanceType;

  // Build S3 key: <playerId>/<timestamp>_<runType>[_<dissonanceType>].txt
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  const typeSuffix = (runType === 'Dissonance' && dissonanceType) ? `_${dissonanceType}` : '';
  const key = `${playerId}/${timestamp}_${runType}${typeSuffix}.txt`;

  const version = await getCurrentVersion(playerId);
  const enrichedBody = injectTowerEra(body, version);

  await s3.send(new PutObjectCommand({
    Bucket: BUCKET,
    Key: key,
    Body: enrichedBody,
    ContentType: 'text/plain',
  }));

  return respond(200, 'Report received');
};
