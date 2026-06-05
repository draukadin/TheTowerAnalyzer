import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { CallToolRequestSchema, ListToolsRequestSchema } from '@modelcontextprotocol/sdk/types.js';

const BASE_URL = 'http://localhost:8080';

// Module shard-cost discount level — update when the discount lab levels up
const SHARD_COST_DISCOUNT_LEVEL = 30;

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
      description: 'Get current tower version, UW stats, module inventory and relic summary. Set includeRelicDetails=true to get full relic list.',
      inputSchema: {
        type: 'object',
        properties: {
          includeRelicDetails: { type: 'boolean', default: false },
        },
      },
    },
    {
      name: 'get_lab_plan',
      description: 'Get current lab slot status and upcoming transitions',
      inputSchema: { type: 'object', properties: {} },
    },
    {
      name: 'get_lab_state',
      description: 'Get all lab levels, targets, and effective speed/cost multipliers (Labs Speed lab + owned relics)',
      inputSchema: {
        type: 'object',
        properties: {
          hideMaxed: {
            type: 'boolean',
            description: 'Exclude labs already at max level (default: true)',
          },
          category: {
            type: 'string',
            description: 'Filter to a specific category (e.g. "Main", "Attack", "Defense")',
          },
        },
      },
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
        const stateData = await fetchApi('/api/player-tracker/state');
        const modules = stateData.modules ?? [];
        const moduleLevels = {
          cannonLevel:    modules.find(m => m.type === 'Cannon'    && m.owned)?.level ?? 0,
          armorLevel:     modules.find(m => m.type === 'Armor'     && m.owned)?.level ?? 0,
          generatorLevel: modules.find(m => m.type === 'Generator' && m.owned)?.level ?? 0,
          coreLevel:      modules.find(m => m.type === 'Core'      && m.owned)?.level ?? 0,
          shardCostDiscountLevel: SHARD_COST_DISCOUNT_LEVEL,
        };
        const params = new URLSearchParams({
          dataSource,
          targetLevel,
          days,
          ...moduleLevels,
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
        const includeRelicDetails = args.includeRelicDetails ?? false;
        const [stateData, labData] = await Promise.all([
          fetchApi(`/api/player-tracker/state?includeRelicDetails=${includeRelicDetails}`),
          fetchApi('/api/player-tracker/labs'),
        ]);
        return distillTowerState(stateData, labData, includeRelicDetails);
      }

      case 'get_lab_plan':
        return distillLabPlan(await fetchApi('/api/player-tracker/labs'));

      case 'get_lab_state': {
        const hideMaxed = args.hideMaxed !== false;
        const category = args.category ?? null;
        return distillLabState(await fetchApi('/api/player-tracker/lab-state'), hideMaxed, category);
      }

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

function distillTowerState(d, labData, includeRelicDetails = false) {
  const version = d.versionHistory ? parseVersion(d.versionHistory) : null;
  const labSlots = labData?.labPlanning ? parseLabSlots(labData.labPlanning) : [];

  const cfLabDuration = labSlots.find(s => s.slot === 1)?.fromLevel ?? null;

  const healthPlus     = d.workshop ? extractEnhancement(d.workshop, 'Health +') : null;
  const wallHealthPlus = d.workshop ? extractEnhancement(d.workshop, 'Wall Health +') : null;

  const ultimateWeapons = Object.fromEntries(
    (d.ultimateWeapons ?? []).map(uw => {
      if (!uw.unlocked) return [uw.name, { unlocked: false }];

      const stats = Object.fromEntries(uw.stats.map(s => {
        const entry = {
          level:           s.currentLevel,
          max_level:       s.maxLevel,
          value:           s.currentValue,
          stones_invested: s.stonesInvested,
          stones_to_next:  s.stonesToNext ?? null,
          stones_to_max:   s.stonesToMax,
        };
        if (s.targetLevel > 0) entry.target_level = s.targetLevel;
        if (s.stonesToTarget > 0) entry.stones_to_target = s.stonesToTarget;
        return [s.label, entry];
      }));

      const uwEntry = {
        unlocked:         true,
        uw_plus_unlocked: uw.uwPlusUnlocked,
        stones_to_max:    uw.stats.reduce((sum, s) => sum + (s.stonesToMax ?? 0), 0),
        stats,
      };

      // Attach CF-specific lab duration
      if (uw.name === 'Chrono Field') uwEntry.lab_duration = cfLabDuration;

      return [uw.name, uwEntry];
    })
  );

  const modules = distillModules(d.modules ?? []);
  const { summary: relicSummary, owned: relicOwned } = distillRelics(d.relics ?? []);

  const out = {
    version,
    ultimate_weapons: ultimateWeapons,
    modules,
    relic_summary: relicSummary,
  };

  if (d.includeRelicDetails) {
    out.relics_owned = relicOwned;
  }

  return result(out);
}

function distillModules(moduleList) {
  const byType = {};
  for (const m of moduleList) {
    if (!byType[m.type]) byType[m.type] = [];
    const entry = {
      id:              m.id,
      code:            m.code,
      name:            m.name,
      owned:           m.owned,
      rarity:          m.rarity,
      stars:           m.stars,
      level:           m.level,
      ability_values:  m.abilityValues,
      substats:        m.substats.map(s => ({ slot: s.slot, key: s.key, rarity: s.rarity, locked: s.locked })),
      copies:          m.copies,
      shattered_epics: m.shatteredEpics,
      presets:         m.presets.map(p => ({ preset: p.preset, slot: p.slot })),
    };
    byType[m.type].push(entry);
  }
  return byType;
}

function distillRelics(relicList) {
  const owned = relicList.filter(r => r.owned);
  const totalByType = {};
  const ownedByType = {};
  for (const r of relicList) {
    totalByType[r.type] = (totalByType[r.type] ?? 0) + 1;
  }
  for (const r of owned) {
    if (!ownedByType[r.type]) ownedByType[r.type] = [];
    ownedByType[r.type].push({
      name:       r.name,
      rarity:     r.rarity,
      bonus_stat: r.bonusStat,
      bonus_value: r.bonusValue,
    });
  }
  const summary = Object.keys(totalByType).sort().map(type => ({
    type,
    owned: ownedByType[type]?.length ?? 0,
    total: totalByType[type],
  }));
  return { summary, owned: ownedByType };
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

function distillLabState(d, hideMaxed, category) {
  const m = d.multipliers;
  let labs = d.labs ?? [];

  if (hideMaxed) labs = labs.filter(l => l.currentLevel < l.maxLevel);
  if (category)  labs = labs.filter(l => l.category === category);

  const byCategory = {};
  for (const l of labs) {
    if (!byCategory[l.category]) byCategory[l.category] = [];
    const entry = { name: l.name, level: l.currentLevel, max: l.maxLevel };
    if (l.targetLevel != null) entry.target = l.targetLevel;
    byCategory[l.category].push(entry);
  }

  return result({
    speed_multiplier: round(m.speedMult, 3),
    coin_discount:    round(1 - m.costMult, 3),
    labs: byCategory,
  });
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
