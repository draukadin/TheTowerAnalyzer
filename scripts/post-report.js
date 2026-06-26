'use strict';
// Post a battle report to the ingest Lambda for dev/prod testing.
//
// Usage:
//   node post-report.js <report-file> [--env dev|prod]
//
// runType and dissonanceType are inferred from the filename:
//   2026-06-24T17-43-02-226Z_Farming.txt        → runType=Farming
//   2026-06-24T17-43-02-226Z_Dissonance_Attack.txt → runType=Dissonance, dissonanceType=Attack

const fs   = require('fs');
const path = require('path');

const PLAYER_ID = 'BA7C430BB386C792';

const ENDPOINTS = {
  dev: 'https://9g1lg3jas3.execute-api.us-west-2.amazonaws.com/prod/reports',
  prod:  'https://REPLACE_WITH_PROD_ENDPOINT/prod/reports',
};

// ── CLI args ────────────────────────────────────────────────────────────────
const args     = process.argv.slice(2);
const filePath = args.find(a => !a.startsWith('--'));
const envIdx   = args.indexOf('--env');
const env      = envIdx !== -1 ? args[envIdx + 1] : 'dev';

if (!filePath) {
  console.error('Usage: node post-report.js <report-file> [--env dev|prod]');
  process.exit(1);
}

if (!ENDPOINTS[env]) {
  console.error(`Unknown env "${env}". Use dev or prod.`);
  process.exit(1);
}

// ── Infer run type from filename ─────────────────────────────────────────────
// Format: <timestamp>_<RunType>[_<DissonanceType>].txt
const filename      = path.basename(filePath);
const nameParts     = filename.replace('.txt', '').split('_').slice(1); // drop timestamp segment
const runType       = nameParts[0] ?? 'Unknown';
const dissonanceType = (runType === 'Dissonance' && nameParts[1]) ? nameParts[1] : null;

// ── Build URL ────────────────────────────────────────────────────────────────
const url = new URL(ENDPOINTS[env]);
url.searchParams.set('runType', runType);
if (dissonanceType) url.searchParams.set('dissonanceType', dissonanceType);

// ── Send ─────────────────────────────────────────────────────────────────────
const body = fs.readFileSync(filePath, 'utf8');

console.log(`POST ${url}`);
console.log(`env=${env}  player=${PLAYER_ID}  runType=${runType}${dissonanceType ? `  dissonanceType=${dissonanceType}` : ''}`);

fetch(url.toString(), {
  method: 'POST',
  headers: {
    'Content-Type': 'text/plain',
    'X-Player-Id':  PLAYER_ID,
  },
  body,
})
  .then(r => r.json().then(data => ({ status: r.status, data })))
  .then(({ status, data }) => console.log(`${status}`, data))
  .catch(err => { console.error('Error:', err.message); process.exit(1); });