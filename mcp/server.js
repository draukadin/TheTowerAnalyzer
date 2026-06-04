import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { CallToolRequestSchema, ListToolsRequestSchema } from '@modelcontextprotocol/sdk/types.js';

const BASE_URL = 'http://localhost:8080';

// Module levels — update when modules level up
const MODULE_LEVELS = {
  cannonLevel: 151,
  armorLevel: 149,
  generatorLevel: 150,
  coreLevel: 150,
  shardCostDiscountLevel: 30,
};

const server = new Server(
  { name: 'tower-analyzer', version: '1.0.0' },
  { capabilities: { tools: {} } }
);

server.setRequestHandler(ListToolsRequestSchema, async () => ({
  tools: [
    {
      name: 'get_currencies',
      description: 'Get current coin, gem, stone, shard and other currency balances',
      inputSchema: { type: 'object', properties: {} },
    },
    {
      name: 'get_shard_rates',
      description: 'Get shard earning rates and time-to-milestone projections per module type',
      inputSchema: {
        type: 'object',
        properties: {
          dataSource: {
            type: 'string',
            enum: ['BATTLE_REPORTS', 'SNAPSHOTS'],
            description: 'Data source for rate calculation (default: BATTLE_REPORTS)',
          },
          targetLevel: {
            type: 'integer',
            description: 'Target module level for projection (default: 161)',
          },
          windowDays: {
            type: 'integer',
            description: 'Rolling window in days (default: 30)',
          },
        },
      },
    },
    {
      name: 'get_cell_income',
      description: 'Get elite cell earning rate and lab speed affordability',
      inputSchema: {
        type: 'object',
        properties: {
          windowDays: {
            type: 'integer',
            description: 'Rolling window in days (default: 30)',
          },
        },
      },
    },
    {
      name: 'get_recent_runs',
      description: 'Get recent battle run summaries',
      inputSchema: {
        type: 'object',
        properties: {
          limit: { type: 'integer', description: 'Number of runs to return (default: 5)' },
          runType: { type: 'string', description: 'Filter by run type (e.g. Farming, Tournament)' },
        },
      },
    },
    {
      name: 'get_tower_state',
      description: 'Get current tower version, UW stats, workshop enhancements and planning context',
      inputSchema: { type: 'object', properties: {} },
    },
    {
      name: 'get_lab_plan',
      description: 'Get current lab slot status and upcoming transitions',
      inputSchema: { type: 'object', properties: {} },
    },
  ],
}));

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args = {} } = request.params;

  try {
    switch (name) {
      case 'get_currencies':
        return distillCurrencies(await fetchApi('/api/player-tracker/currencies'));

      case 'get_shard_rates': {
        const dataSource = args.dataSource ?? 'BATTLE_REPORTS';
        const targetLevel = args.targetLevel ?? 161;
        const days = args.windowDays ?? 30;
        const params = new URLSearchParams({
          dataSource,
          targetLevel,
          days,
          ...MODULE_LEVELS,
        });
        return distillShardRates(await fetchApi(`/api/analysis/shards?${params}`), dataSource);
      }

      case 'get_cell_income': {
        const days = args.windowDays ?? 30;
        return distillCellIncome(await fetchApi(`/api/analysis/cells?days=${days}`));
      }

      case 'get_recent_runs': {
        const limit = args.limit ?? 5;
        const runType = args.runType ? `&runType=${encodeURIComponent(args.runType)}` : '';
        return distillRecentRuns(await fetchApi(`/api/reports?limit=${limit}${runType}`));
      }

      case 'get_tower_state': {
        const [stateData, labData] = await Promise.all([
          fetchApi('/api/player-tracker/state'),
          fetchApi('/api/player-tracker/labs'),
        ]);
        return distillTowerState(stateData, labData);
      }

      case 'get_lab_plan':
        return distillLabPlan(await fetchApi('/api/player-tracker/labs'));

      default:
        throw new Error(`Unknown tool: ${name}`);
    }
  } catch (e) {
    return { content: [{ type: 'text', text: `Error: ${e.message}` }], isError: true };
  }
});

// -------------------------------------------------------------------------
// HTTP
// -------------------------------------------------------------------------

async function fetchApi(path) {
  let res;
  try {
    res = await fetch(`${BASE_URL}${path}`);
  } catch (e) {
    throw new Error(`Cannot reach Spring Boot at ${BASE_URL} — is it running? (${e.message})`);
  }
  if (!res.ok) throw new Error(`HTTP ${res.status} from ${path}`);
  return res.json();
}

// -------------------------------------------------------------------------
// Distillation
// -------------------------------------------------------------------------

function distillCurrencies(d) {
  return result({
    coins_T: round(d.coins.raw / 1e12, 2),
    gems: d.gems,
    stones: d.stones,
    medals: d.medals,
    elite_cells_K: round(d.eliteCells.raw / 1e3, 2),
    cannon_shards: d.cannonShards,
    armor_shards: d.armorShards,
    generator_shards: d.generatorShards,
    core_shards: d.coreShards,
    reroll_shards: d.reRollShards,
    bits: d.bits,
    tokens: d.tokens,
  });
}

function distillShardRates(d, dataSource) {
  const proj = d.projections;
  return result({
    source: dataSource,
    runs_analyzed: d.runsAnalyzed,
    cannon:    shardSummary(d.averages.cannon,    d.stdDev.cannon,    proj.cannon),
    armor:     shardSummary(d.averages.armor,     d.stdDev.armor,     proj.armor),
    generator: shardSummary(d.averages.generator, d.stdDev.generator, proj.generator),
    core:      shardSummary(d.averages.core,      d.stdDev.core,      proj.core),
  });
}

function shardSummary(rate, std, proj) {
  return {
    rate: round(rate, 2),
    std_dev: round(std, 2),
    next_level_days: round(proj.hoursToNextLevel / 24, 1),
    to_target_days: round(proj.hoursToTargetLevel / 24, 1),
  };
}

function distillCellIncome(d) {
  return result({
    runs_analyzed: d.runsAnalyzed,
    avg_cells_per_hour: round(d.averageCellsPerHour, 2),
    std_dev: round(d.dataPoints.map(p => p.cellsPerHour).reduce((acc, v, _, arr) => {
      const mean = d.averageCellsPerHour;
      return acc + Math.pow(v - mean, 2) / arr.length;
    }, 0) ** 0.5, 2),
  });
}

function distillRecentRuns(runs) {
  return result(runs.slice(0, 10).map(r => {
    const durationHours = r.realTimeSeconds / 3600;
    return {
      id: r.id,
      date: r.battleDate,
      type: r.runType,
      tier: r.tier,
      wave: r.wave,
      version: r.towerEra,
      coins_T: round(r.coinsPerHour * durationHours / 1e12, 2),
      cph_T: round(r.coinsPerHour / 1e12, 2),
      cells_K: round(r.cellsEarned / 1e3, 2),
      killed_by: r.killedBy ?? null,
    };
  }));
}

function distillTowerState(d, labData) {
  const version = parseVersion(d.versionHistory);
  const labSlots = parseLabSlots(labData.labPlanning);

  const cf = findUw(d.ultimateWeapons, 'Chrono Field');
  const bh = findUw(d.ultimateWeapons, 'Black Hole');
  const gt = findUw(d.ultimateWeapons, 'Golden Tower');

  const cfLabDuration = labSlots.find(s => s.slot === 1)?.fromLevel ?? null;

  const healthPlus     = extractEnhancement(d.workshop, 'Health +');
  const wallHealthPlus = extractEnhancement(d.workshop, 'Wall Health +');

  return result({
    version,
    cf: cf ? {
      duration:     uwStat(cf, 'Duration'),
      cooldown:     uwStat(cf, 'Cooldown'),
      lab_duration: cfLabDuration,
    } : null,
    bh: bh ? {
      duration: uwStat(bh, 'Duration'),
      cooldown: uwStat(bh, 'Cooldown'),
    } : null,
    gt: gt ? {
      duration: uwStat(gt, 'Duration'),
      cooldown: uwStat(gt, 'Cooldown'),
    } : null,
    health_plus_level:      healthPlus,
    wall_health_plus_level: wallHealthPlus,
    cf_stones_remaining:    cf ? uwStonesPending(cf, 'Duration') + uwStonesPending(cf, 'Cooldown') : null,
  });
}

/** Find an unlocked UW by name from the DB-backed JSON array. */
function findUw(uwArray, name) {
  const uw = uwArray.find(w => w.name === name);
  return (uw && uw.unlocked) ? uw : null;
}

/** Get the current value of a named stat from a UW object. */
function uwStat(uw, label) {
  return uw.stats.find(s => s.label === label)?.currentValue ?? null;
}

/** Get total stones remaining to max a named stat. */
function uwStonesPending(uw, label) {
  return uw.stats.find(s => s.label === label)?.stonesToMax ?? 0;
}

function distillLabPlan(d) {
  const slots = parseLabSlots(d.labPlanning);

  const leanSlots = slots.map(({ slot, current_lab, level, days_remaining }) => ({
    slot, current_lab, level, days_remaining,
  }));

  const nextSlot = slots
    .filter(s => s.days_remaining != null)
    .sort((a, b) => a.days_remaining - b.days_remaining)[0];

  const next_transition = nextSlot ? {
    slot:     nextSlot.slot,
    days:     nextSlot.days_remaining,
    next_lab: nextSlot.next_lab,
  } : null;

  return result({ slots: leanSlots, next_transition });
}

// -------------------------------------------------------------------------
// Markdown parsers
// -------------------------------------------------------------------------

/**
 * Parse all non-separator table rows from a markdown table string.
 * Returns each row as an array of trimmed cell strings.
 */
function mdRows(text) {
  return text.split('\n')
    .filter(l => l.includes('|') && !l.match(/^\s*\|[\s:|-]+\|/))
    .map(l => l.split('|').slice(1, -1).map(c => c.trim()));
}

/** Return the version string from the first data row of the version-history table. */
function parseVersion(vhText) {
  const rows = mdRows(vhText).filter(r => r[0] && r[0] !== 'Version');
  return rows[0]?.[0] ?? 'unknown';
}

/** Look up a named enhancement's current level from the workshop markdown. */
function extractEnhancement(workshopText, label) {
  const enhSection = workshopText.split('### Enhancements')[1] ?? '';
  const row = mdRows(enhSection).find(r => r[0] === label);
  return row ? parseInt(row[1]) : null;
}

/**
 * Parse all 5 lab slots from the lab-planning markdown.
 * Each slot entry carries an internal `fromLevel` used by distillTowerState
 * to populate cf.lab_duration; that field is stripped before the lean output.
 */
function parseLabSlots(labText) {
  const slots = [];
  const slotRe = /### Lab Slot (\d+) Planning\n([\s\S]*?)(?=\n### Lab Slot \d+ Planning|$)/g;
  let m;

  while ((m = slotRe.exec(labText)) !== null) {
    const slotNum  = parseInt(m[1]);
    const slotText = m[2];

    // Data rows: column 0 is a pure integer (the start level)
    const dataRows = mdRows(slotText).filter(r => /^\d+$/.test(r[0]));

    if (!dataRows.length) {
      slots.push({ slot: slotNum, current_lab: null, level: null, days_remaining: null, next_lab: null, fromLevel: null });
      continue;
    }

    const curr = dataRows[0];
    const next = dataRows[1];

    slots.push({
      slot:          slotNum,
      current_lab:   curr[2],
      level:         `${curr[0]}→${curr[1]}`,
      days_remaining: parseDurationDays(curr[4]),
      next_lab:      next ? `${next[2]} ${next[0]}→${next[1]}` : null,
      fromLevel:     parseInt(curr[0]),
    });
  }

  return slots;
}

/** Parse "62d  1h 52m" → integer days (rounded). */
function parseDurationDays(str) {
  if (!str) return null;
  const d = parseInt((str.match(/(\d+)d/) ?? [0, 0])[1]);
  const h = parseInt((str.match(/(\d+)h/) ?? [0, 0])[1]);
  const mn = parseInt((str.match(/(\d+)m/) ?? [0, 0])[1]);
  return Math.round(d + h / 24 + mn / 1440);
}

// -------------------------------------------------------------------------
// Helpers
// -------------------------------------------------------------------------

function result(data) {
  return { content: [{ type: 'text', text: JSON.stringify(data, null, 2) }] };
}

function round(n, decimals) {
  const factor = Math.pow(10, decimals);
  return Math.round(n * factor) / factor;
}

// -------------------------------------------------------------------------
// Start
// -------------------------------------------------------------------------

const transport = new StdioServerTransport();
await server.connect(transport);
