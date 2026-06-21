import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import { SecretsManagerClient, GetSecretValueCommand } from '@aws-sdk/client-secrets-manager';

const s3 = new S3Client({});
const sm = new SecretsManagerClient({});

const BUCKET = process.env.REPORTS_BUCKET;
const API_KEY_SECRET_ARN = process.env.API_KEY_SECRET_ARN;

// Cached at cold start — avoids a Secrets Manager call on every invocation
let cachedApiKey = null;

async function getApiKey() {
  if (cachedApiKey) return cachedApiKey;
  const result = await sm.send(new GetSecretValueCommand({ SecretId: API_KEY_SECRET_ARN }));
  cachedApiKey = result.SecretString;
  return cachedApiKey;
}

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
  return { statusCode, body: JSON.stringify({ message }) };
}

export const handler = async (event) => {
  // Auth
  const apiKey = event.headers?.['x-api-key'] ?? event.headers?.['X-Api-Key'];
  const expectedKey = await getApiKey();
  if (!apiKey || apiKey !== expectedKey) {
    return respond(401, 'Unauthorized');
  }

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

  await s3.send(new PutObjectCommand({
    Bucket: BUCKET,
    Key: key,
    Body: body,
    ContentType: 'text/plain',
  }));

  return respond(200, 'Report received');
};
