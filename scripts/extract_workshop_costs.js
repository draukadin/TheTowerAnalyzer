/**
 * Extracts workshop item costs from the obfuscated tower-workshop-calculator bundle.
 *
 * Source: https://tower-workshop-calculator.netlify.app/static/js/main.*.js
 *
 * Download the full (uncompressed) bundle first:
 *   curl "https://tower-workshop-calculator.netlify.app/static/js/main.<hash>.js" \
 *     -H "Accept-Encoding: identity" -o main.js
 *
 * Usage:
 *   node extract_workshop_costs.js <path-to-main.js>
 *
 * Outputs:
 *   workshop_costs.json       — coin (permanent upgrade) costs per level
 *   workshop_cash_costs.json  — cash (in-battle upgrade) costs per level
 *
 * File structure of the bundle:
 *   Lines 1-8:          _0x3d97 string decoder function
 *   Lines 9-22:         Array shuffle IIFE (corrects the string table order)
 *   Lines 37147-135710: _0x43be string array (98k entries)
 *   Line 9657:          Single key 'gVcUc' holding the entire base64 data payload
 *
 * Decryption algorithm (reconstructed from obfuscated code, lines ~30758-30787):
 *   Buffer.from(gVcUcString, 'base64') is XOR-decrypted with a rolling key:
 *     xorByte = (byte ^ key) & 255
 *     key     = 37 + ((xorByte * key) >> 1)
 *   Initial key = 54189
 *
 * Decrypted buffer layout (little-endian float64 doubles):
 *   for each category (0=Attack, 1=Defense, 2=Utility):
 *     categoryIndex  (double)
 *     itemCount      (double)
 *     for each item:
 *       itemIndex    (double)
 *       levelCount   (double)
 *       for each level 0..levelCount-1:
 *         cash       (double)  — in-battle gold upgrade cost
 *         coins      (double)  — permanent workshop coin upgrade cost
 *         value      (double)  — stat value at this level
 */

'use strict';
const fs = require('fs');
const path = require('path');

// ── Item name mapping (matches WorkshopSeeder sort_order) ───────────────────

const ITEM_NAMES = {
    0: ['Damage','Attack Speed','Critical Chance','Critical Factor','Range',
        'Damage / Meter','Multishot Chance','Multishot Targets','Rapid Fire Chance',
        'Rapid Fire Duration','Bounce Shot Chance','Bounce Shot Targets','Bounce Shot Range',
        'Super Critical Chance','Super Critical Mult','Rend Armor Chance','Rend Armor Mult'],
    1: ['Health','Health Regen','Defense %','Defense Absolute','Thorn Damage',
        'Lifesteal','Knockback Chance','Knockback Force','Orb Speed','Orbs',
        'Shockwave Size','Shockwave Frequency','Land Mine Chance','Land Mine Damage',
        'Land Mine Radius','Death Defy','Wall Health','Wall Rebuild'],
    2: ['Cash Bonus','Cash / Wave','Coin / Kill Bonus','Coin / Wave',
        'Free Attack Upgrade','Free Defense Upgrade','Interest / Wave','Free Utility Upgrade',
        'Recovery Amount','Max Amount','Package Chance','Enemy Attack Level Skip',
        'Enemy Health Level Skip'],
};

// ── Step 1: Build standalone decoder ────────────────────────────────────────

function buildDecoder(lines) {
    // _0x43be array: lines 37147-135710 (0-indexed: 37146-135709)
    const arrayPart   = lines.slice(37146, 135710).join('\n');
    // _0x3d97 decoder: lines 1-8 (0-indexed: 0-7)
    const decoderPart = lines.slice(0, 8).join('\n');
    // Shuffle body: lines 9-21 (0-indexed: 8-20)
    const shuffleBody = lines.slice(8, 21).join('\n');
    // Line 22 (0-indexed 21) contains shuffle close + app IIFE open; take only the close
    const shuffleClose = lines[21].split(', ((() => {')[0];

    const script = [arrayPart, decoderPart, shuffleBody, shuffleClose + ');',
                    'module.exports = _0x3d97;'].join('\n');

    const tmpFile = path.join(__dirname, '_decoder_tmp.js');
    fs.writeFileSync(tmpFile, script);
    const decoder = require(tmpFile);
    fs.unlinkSync(tmpFile);
    delete require.cache[require.resolve(tmpFile)];
    return decoder;
}

// ── Step 2: Reconstruct and decrypt the gVcUc payload ───────────────────────

function extractAndDecrypt(lines, decode) {
    const dataLine = lines[9656]; // line 9657, 0-indexed

    // Collect all string fragments (literals + decoded calls) from the gVcUc expression
    const gStart = dataLine.indexOf("'gVcUc':") + 8;
    const expr = dataLine.slice(gStart);
    const tokenRe = /'([^']*)'|"([^"]*)"|_0x581c14\(0x([0-9a-fA-F]+)\)/g;
    let m;
    const parts = [];
    while ((m = tokenRe.exec(expr)) !== null) {
        if      (m[1] !== undefined) parts.push(m[1]);
        else if (m[2] !== undefined) parts.push(m[2]);
        else                         parts.push(decode(parseInt(m[3], 16)));
    }

    const rawBuf = Buffer.from(parts.join(''), 'base64');

    // XOR decrypt with rolling key (initial key = 54189)
    let key = 54189;
    const decrypted = Buffer.alloc(rawBuf.length);
    for (let i = 0; i < rawBuf.length; i++) {
        const xorByte = (rawBuf[i] ^ key) & 255;
        key = 37 + ((xorByte * key) >> 1);
        decrypted[i] = xorByte;
    }
    return decrypted;
}

// ── Step 3: Parse double stream into structured data ────────────────────────

function parseDoubles(buf) {
    let pos = 0;
    const rd = () => { const v = buf.readDoubleLE(pos); pos += 8; return v; };
    const raw = {};
    while (pos < buf.length) {
        const cat      = rd();
        const count    = rd();
        raw[cat] = {};
        for (let i = 0; i < count; i++) {
            const idx        = rd();
            const levelCount = rd();
            const levels = {};
            for (let l = 0; l < levelCount; l++) {
                levels[l] = { cash: rd(), coins: rd(), value: rd() };
            }
            raw[cat][idx] = levels;
        }
    }
    return raw;
}

// ── Step 4: Map to named items and build output JSONs ────────────────────────

function buildOutputs(raw) {
    const coins = {}, cash = {};
    for (const [catStr, items] of Object.entries(raw)) {
        const names = ITEM_NAMES[parseInt(catStr)];
        if (!names) continue;
        for (const [idxStr, levels] of Object.entries(items)) {
            const name = names[parseInt(idxStr)];
            if (!name) continue;
            const maxLvl = Object.keys(levels).length - 1; // last entry is a 0-cost sentinel
            coins[name] = {};
            cash[name]  = {};
            for (let n = 1; n <= maxLvl; n++) {
                const lvl = levels[n - 1];
                if (lvl.coins > 0) coins[name][String(n)] = Math.round(lvl.coins);
                if (lvl.cash  > 0) cash[name][String(n)]  = Math.round(lvl.cash);
            }
        }
    }
    return { coins, cash };
}

// ── Main ─────────────────────────────────────────────────────────────────────

const bundlePath = process.argv[2];
if (!bundlePath) {
    console.error('Usage: node extract_workshop_costs.js <path-to-main.js>');
    process.exit(1);
}

console.log('Reading bundle...');
const src   = fs.readFileSync(bundlePath, 'utf8');
const lines = src.split('\n');
console.log(`  ${lines.length} lines, ${(src.length / 1e6).toFixed(1)} MB`);

console.log('Building string decoder...');
const decode = buildDecoder(lines);
console.log(`  OK — test decode: ${decode(0x7e5f)}`);

console.log('Extracting and decrypting gVcUc payload...');
const decrypted = extractAndDecrypt(lines, decode);
console.log(`  Decrypted ${decrypted.length} bytes (${decrypted.length / 8} doubles)`);

console.log('Parsing data structure...');
const raw = parseDoubles(decrypted);
const categories = Object.keys(raw).map(Number).sort();
categories.forEach(c => console.log(`  Category ${c}: ${Object.keys(raw[c]).length} items`));

console.log('Building output files...');
const { coins, cash } = buildOutputs(raw);

const outDir = path.dirname(bundlePath);
const coinsPath = path.join(outDir, 'workshop_costs.json');
const cashPath  = path.join(outDir, 'workshop_cash_costs.json');
fs.writeFileSync(coinsPath, JSON.stringify(coins));
fs.writeFileSync(cashPath,  JSON.stringify(cash));

const coinCount = Object.values(coins).reduce((s, v) => s + Object.keys(v).length, 0);
const cashCount = Object.values(cash).reduce((s, v)  => s + Object.keys(v).length, 0);
console.log(`  ${coinsPath}: ${coinCount} records`);
console.log(`  ${cashPath}: ${cashCount} records`);
console.log('Done.');
