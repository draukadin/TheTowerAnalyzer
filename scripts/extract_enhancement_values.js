'use strict';
const fs = require('fs'), path = require('path');

const ENHANCEMENT_ITEMS = {
    0: ['Damage +', 'Rend Armor Mult +', 'Critical Factor +', 'Damage / Meter +', 'Super Crit Multi +', 'Attack Speed +'],
    1: ['Health +', 'Health Regen +', 'Defense Absolute +', 'Land Mine Damage +', 'Wall Health +', 'Orb Size +'],
    2: ['Cash Bonus +', 'Coin Bonus +', 'Cells / Kill Bonus +', 'Free Upgrades +', 'Recovery Package +', 'Enemy Level Skips +'],
};

const lines = fs.readFileSync(path.join(__dirname, 'main.cbe71c97.js'), 'utf8').split('\n');

// Build decoder
const tmpFile = path.join(__dirname, '_enh_tmp.js');
fs.writeFileSync(tmpFile, [
    lines.slice(37146, 135710).join('\n'),
    lines.slice(0, 8).join('\n'),
    lines.slice(8, 21).join('\n'),
    lines[21].split(', ((() => {')[0] + ');',
    'module.exports = _0x3d97;'
].join('\n'));
const decode = require(tmpFile);
fs.unlinkSync(tmpFile);

// The second payload is at key 'KdfAi' on line 9718
const dataLine = lines[9717];
const kStart = dataLine.indexOf("'KdfAi':") + 8;
if (kStart < 8) { console.error("'KdfAi' key not found on line 9718"); process.exit(1); }
const expr = dataLine.slice(kStart);
const tokenRe = /'([^']*)'|"([^"]*)"|_0x581c14\(0x([0-9a-fA-F]+)\)/g;
let m;
const parts = [];
while ((m = tokenRe.exec(expr)) !== null) {
    if (m[1] !== undefined) parts.push(m[1]);
    else if (m[2] !== undefined) parts.push(m[2]);
    else parts.push(decode(parseInt(m[3], 16)));
}
// The expression ends at the next top-level key; truncate at closing quote before ','
// Actually just take all string-producing tokens since tokenRe stops at non-matching chars
const rawBuf = Buffer.from(parts.join(''), 'base64');
console.log('Raw buffer length:', rawBuf.length, '  doubles:', rawBuf.length / 8);

// Initial key: -0x8da4 + -0x11b00 + 0x27c51
const initKey = (-0x8da4) + (-0x11b00) + 0x27c51;
console.log('Initial XOR key:', initKey);

let key = initKey;
const dec = Buffer.alloc(rawBuf.length);
for (let i = 0; i < rawBuf.length; i++) {
    const xb = (rawBuf[i] ^ key) & 255; key = 37 + ((xb * key) >> 1); dec[i] = xb;
}

// Parse: each entry has { coins, value } only (no cash for enhancements)
let pos = 0;
const rd = () => { const v = dec.readDoubleLE(pos); pos += 8; return v; };
const raw = {};
while (pos < dec.length) {
    const cat   = rd();
    const count = rd();
    raw[cat] = {};
    for (let i = 0; i < count; i++) {
        const idx = rd(), lc = rd();
        const levels = {};
        for (let l = 0; l < lc; l++) {
            levels[l] = { coins: rd(), value: rd() };
        }
        raw[cat][idx] = levels;
    }
    console.log(`Category ${cat}: ${count} items, pos after: ${pos}/${dec.length}`);
}

// Map to named items and build output JSON
const values = {}, costs = {};
for (const [catStr, items] of Object.entries(raw)) {
    const cat   = parseInt(catStr);
    const names = ENHANCEMENT_ITEMS[cat];
    if (!names) { console.warn('Unknown category:', cat); continue; }
    for (const [idxStr, levels] of Object.entries(items)) {
        const name   = names[parseInt(idxStr)];
        if (!name) { console.warn('Unknown item index', idxStr, 'in cat', cat); continue; }
        // levels[n].value = stat multiplier AT level n; levels[maxLvl] is sentinel (0 cost).
        const maxLvl = Object.keys(levels).length - 1;
        values[name] = {};
        costs[name]  = {};
        for (let n = 1; n <= maxLvl; n++) {
            // Cost to go from n-1 → n lives in levels[n-1]
            if (levels[n - 1].coins > 0) costs[name][String(n)] = Math.round(levels[n - 1].coins);
        }
        // Value at level n lives in levels[n]; store n=0 (base) through n=maxLvl
        for (let n = 0; n <= maxLvl; n++) {
            values[name][String(n)] = parseFloat(levels[n].value.toFixed(6));
        }
    }
}

const valuesPath = path.join(__dirname, 'enhancement_values.json');
const costsPath  = path.join(__dirname, 'enhancement_costs.json');
fs.writeFileSync(valuesPath, JSON.stringify(values));
fs.writeFileSync(costsPath,  JSON.stringify(costs));

const vCount = Object.values(values).reduce((s, v) => s + Object.keys(v).length, 0);
const cCount = Object.values(costs).reduce((s, v)  => s + Object.keys(v).length, 0);
console.log(`\n${valuesPath}: ${vCount} records`);
console.log(`${costsPath}: ${cCount} records`);

// Spot-check: Damage + first 5 levels and last 3
const dmgPlus = values['Damage +'];
if (dmgPlus) {
    const keys = Object.keys(dmgPlus).map(Number).sort((a,b) => a-b);
    console.log('\nDamage + samples:');
    [1,2,3,4,5,...keys.slice(-3)].forEach(n => console.log(`  [${n}] = ${dmgPlus[String(n)]}`));
}
