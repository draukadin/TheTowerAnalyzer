---
name: tower-modules
description: >
  Expert knowledge on The Tower: Idle Tower Defense module system. Use this skill
  whenever the user asks anything about modules in The Tower — including which modules
  to use or avoid, natural epics vs fodder, how to obtain modules, module leveling,
  merging, shattering, sub-stats, the module lab system, or strategic recommendations
  for specific builds or game stages. Also use when the user asks about specific module
  names (e.g. "Dimension Core", "Project Funding", "DC", "PF"), tier lists, or how
  modules interact with builds like Glass Cannon, farming, or tournament runs. Trigger
  even if the user doesn't say "module" explicitly — questions about "best cannon" or
  "how do I sync my UWs" likely need this skill.
---

# The Tower: Module System Expert Skill

## Overview

Modules are equippable upgrades unlocked at Tier 2 Wave 90. There are 4 types:
- **Cannon** — attack/damage
- **Armor** — defense/health
- **Generator** — utility/economy
- **Core** — ultimate weapons (UWs)

One module of each type can be equipped (a second "assist" slot unlocks at Tier 19 Wave 50 for 1000 stones each, providing a unique Epic-quality effect and 1% main/sub effect bonus — improved with stones and labs).

---

## Natural Epics vs. Fodder — The Most Important Distinction

**Natural Epic**: A module drawn directly as Epic (or higher) from the gacha. It has a **unique ability** that defines its strategic identity. This is what you build around.

**Fodder**: Any Common or Rare module, OR a Rare that was merged up to Epic. Fodders **never receive a unique ability**, even if merged to Epic rarity. Their purpose is to be merge material for natural epics, or to be shattered for shards.

**Core rule**: Never invest significant shard levels into a fodder. Reserve shards for natural epics you intend to keep. A fodder can be leveled slightly to use as temporary filler, but treat it as disposable.

**RNG reality**: You can own all modules simultaneously. The acquisition system is pure RNG, so you may end up with a lower-tier natural epic at high rarity before you pull the one you actually want. It is legitimate to run a B-tier natural epic at Ancestral while waiting to pull an S-tier one — always a natural epic over a high-level fodder.

---

## Acquiring Modules

**Gacha draws**: 20 gems (x1) or 200 gems (x10). Pull rates:
- Common: 68.5%
- Rare: 29%
- Epic: 2.5%
- Bad luck protection: guaranteed Epic after 150 purchases without one

**Boss drops**: Bosses can drop reroll shards and modules:
- Reroll shards: 15% drop chance
- Common module: 2% base (improvable via lab)
- Rare module: 0.5% base (improvable via lab)
- 5-wave cooldown between drops (20-wave cooldown in tournaments)
- Shard quantity scales with tier (Tier 1 = 1 shard, Tier 10 = 32, Tier 15 = 60)

**Module tickets**: 10 tickets given free at unlock. Additional ticket sources not yet determined.

---

## Module Leveling System

Modules are leveled using **type-specific shards** and **coins**. Leveling increases the **main stat only** (damage, health, coin bonus, or UW damage depending on type).

Key level milestones unlock additional sub-effect slots. Max level is determined by rarity:
- Common: lowest max level
- Rare → Legendary+: higher cap
- Epic → Ancestral (5 stars): highest cap, up to level 200

Leveling costs scale steeply — see `references/leveling-costs.md` for the full table. Notable inflection points: costs jump hard at level 61 (25M coins), 101 (8B coins), 141 (500B coins), and 171+ (510T+ coins).

**Modules can be fully reset** ("restore" button) at no cost — all shards and coins returned. Reroll shards and gems are NOT returned on restore.

---

## Module Merging System

Merging increases a module's rarity, raising its main stat, stat scaling, and max level cap. **Merging does not reset current level.**

**Merge materials** use two icons:
- **Module icon**: must be the exact same module at the shown rarity
- **Wildcard (orb icon)**: any module of the same type at the shown rarity

**Critical rule**: Rare modules keep their original name forever. A merged Rare-to-Epic will never match a natural Epic of the same name and will never gain a unique ability. It can still serve as wildcard merge material for a natural epic later.

### Natural Epic Upgrade Path

This is the path for upgrading the module you actually want to keep and use:

| Step | Input | Materials Needed |
|------|-------|-----------------|
| Epic → Epic+ | 1 natural epic | + 1 epic (any, same type) |
| Epic+ → Legendary | 1 epic+ | + 2x epic+ fodder (any, same type) |
| Legendary → Legendary+ | 1 legendary | + 1 epic+ (any, same type) |
| Legendary+ → Mythic | 1 legendary+ | + 1 legendary+ fodder (any, same type) |
| Mythic → Mythic+ | 1 mythic | + 1 legendary+ fodder (any, same type) |
| Mythic+ → Ancestral | 1 mythic+ | + 1 epic+ (any, same type) |
| Ancestral → 1★ | 1 ancestral | + 1 **natural** epic+ (gacha-pulled epic merged to epic+, same type) |
| 1★ → 2★ | 1 ancestral 1★ | + 1 **natural** epic+ (same type) |
| 2★ → 3★ | 1 ancestral 2★ | + 1 **natural** epic+ (same type) |
| 3★ → 4★ | 1 ancestral 3★ | + 1 **natural** epic+ (same type) |
| 4★ → 5★ | 1 ancestral 4★ | + 1 **natural** epic+ (same type) |

**Important**: Ancestral star upgrades require **natural** (gacha-pulled) epics merged to epic+. Fodder epic+ built from Rares do NOT qualify. Each star costs one natural epic pull of the same type — making Ancestral 5★ extremely gem-intensive (5 additional natural epics beyond the keeper itself).

**Fodder pipeline ends at Mythic+**: Once your natural epic reaches Mythic+, the fodder merge path is complete. The Mythic+ → Ancestral step and all star upgrades only require natural epics.

### Fodder Upgrade Path

Fodders (built from Rares) are the merge material that fuels the natural epic upgrade chain.
The name of the resulting fodder is determined by whichever module is selected first in the merge.

| Step | Input | Materials Needed | Result |
|------|-------|-----------------|--------|
| Rare → Rare+ | 3x rare of the **same name** | — | Rare+ |
| Rare+ → Epic | 3x rare+ (any name, same type) | — | Epic (no unique ability, name = first selected) |
| Epic → Epic+ | 1 epic + 1 epic (any, same type) | — | Epic+ fodder |
| Epic+ → Legendary | 3x epic+ (any name, same type) | — | Legendary fodder |
| Legendary → Legendary+ | 1 legendary + 1 epic+ (any, same type) | — | Legendary+ fodder |

**Legendary+ fodder** is the material needed for Legendary+→Mythic and Mythic→Mythic+ upgrades on your natural epic.

### Fodder Investment Warning
Fodder modules are **disposable merge material**. Do not invest significant shards into them.
However, you need a sustained supply of Rares to build the fodder pipeline — Rares are obtained
from boss drops (0.5% base, improvable via lab) and gacha pulls. The boss drop lab for Rare
modules is a meaningful long-term investment for this reason.

---

## Module Shattering System

Shattering destroys a module and returns shards:
- Common: 5 shards
- Rare: 10 shards
- After merging: shards returned reflect the cumulative value of all modules consumed in the merge chain

**Strategic use**: Shatter Commons immediately — they cannot be merged and have no use as fodder. For Rares, it's a judgment call: you need a sustained supply of Rares to build the fodder pipeline (Rare→Rare+→Epic→Epic+→Legendary+) that feeds natural epic upgrades. Only shatter Rares once you have a healthy surplus or don't yet have a natural epic to push toward Legendary+.

---

## Sub-Stat System

Every module has sub-effect slots unlocked at level milestones. Sub-stats are **type-specific** (Cannon subs boost attack stats, Armor subs boost defense stats, etc.).

**Sub-stat rarity** is independent of the module's rarity, but the **maximum possible rarity** of a sub-stat equals the module's current rarity. Roll chances:
- Common: 46.2% | Rare: 40% | Epic: 10% | Legendary: 2.5% | Mythic: 1.0% | Ancestral: 0.3%

**Rerolling sub-stats** costs reroll shards (dropped by bosses):
- 0 locks: 10 shards | 1 lock: 40 | 2 locks: 160 | 3 locks: 500 | 4 locks: 1000 | 5 locks: 1600

**Locking strategy**: Lock desirable sub-stats before rerolling to protect them. Cost increases per lock, so prioritize locking your best sub first, then decide if the second slot is worth the cost jump.

**Auto-ban**: Spotlight Angle subs are auto-banned when angle is maxed via workshop/lab. Chrono Field Duration/Cooldown subs are auto-banned when those stats are maxed. This removes those slots from roll pool for free.

For full sub-stat tables by type, see `references/substats.md`.

---

## Module Lab System

Module labs are unlocked progressively from Tier 4 through Tier 19. For full cost tables and detailed unlock info, see `references/labs.md`.

**Lab infrastructure**: Start with 1 slot (T1 W30). Up to 5 slots total (4,900 gems total). Speed up with Elite Cells — boost multiple labs at 1.5x simultaneously rather than one lab at high multiplier for best cell efficiency.

**Labs Coin Discount** (T1 W30): Reduces coin cost of all labs. Low priority — research time, not coins, is usually the bottleneck.

### Acquisition Labs (T4 W70)
- **Common Drop Chance** (10 levels, +1% max): Raises base 2% → 3%. Cheap early investment.
- **Reroll Shards** (100 levels, +100 shards): Long grind but reroll shards are the sub-stat optimization bottleneck. Start early and run continuously.
- **Daily Mission Shards** (50 levels, +50/mission): Reliable passive shard income.

### Cost Reduction Labs (T10 W40)
- **Module Shard Cost** (30 levels, -30%): High priority — invest immediately on unlock. With 163K+ shards needed to reach level 161, every percent off adds up.
- **Module Coin Cost** (30 levels, -30%): Pair with shard cost. Coin costs above level 101 are brutal (8B/level); above 141 they're catastrophic (500B/level).
- **Rare Drop Chance** (10 levels, +1% max): Triples base rate (0.5% → 1.5%). Critical for the fodder pipeline — Rares from boss drops feed the entire Epic→Legendary→Mythic merge chain.

### Utility Labs
- **Unmerge Module** (T8 W20, 1 level, 10M coins, 2 days): Unlocks the ability to unmerge modules, recovering the primary module and epic+ material (or shards for wildcard merges). Get it immediately — trivial cost, provides crucial safety net.
- **Shatter Shards** (T16 W40, 5 levels, +100% shards from shattering): Level 1 is ~37 days/10T coins — reasonable to queue passively. Full 5 levels takes ~382 days total. Not urgent but worth running when lab slots are available.

### Effect Ban Labs (T10 W40)
Ban specific sub-stats from appearing in rerolls permanently. Dramatically improves sub-stat optimization efficiency.
- Cannon: 4 ban slots (levels take 37d → 204d each)
- Armor: 4 ban slots (same cost curve as Cannon)
- Generator: 3 ban slots (62d → 204d)
- **Core: 7 ban slots** — largest pool, most bans available. Level 1 is cheap (50B, 16d) — highest priority first ban lab.

### Assist Module Labs (T19 W50)
- 30 levels, +1% to assist module main effect and sub-effects per level (max +30%)
- Costs are in **Quadrille coins** (0.25Q → 7.5Q) — strictly endgame progression
- As of v27.1, assist module multiplier stats are multiplicative

### Lab Priority Order (Quick Reference)
Early (T4-T8): Common Drop Chance → Reroll Shards → Daily Mission Shards → Unmerge Module
Mid (T10-T15): Module Shard Cost → Module Coin Cost → Rare Drop Chance → Core Effect Bans L1
Late (T16+): Effect Bans all types → Continue Reroll Shards → Shatter Shards → Assist Module Labs

---

## Strategic Module Recommendations

> This is a **late/endgame ranking**. Early and mid game players should equip whatever natural epic they have, prioritizing any natural epic over a leveled fodder.

### Before Giving Any Recommendation — Ask First

**Always establish run type before providing module advice.** Do not give a broad dump covering both farming and tournament — pick the relevant context and tailor the response to it. If the run type cannot be inferred from `get_recent_runs` or the conversation, ask directly:

> "Are you asking about your farming loadout, tournament loadout, or milestone runs?"

Once run type is established, only discuss modules relevant to that context. A tier list comment about farming value is noise if the player is asking about tournaments, and vice versa.

**Always check `get_tower_state` before assuming anything about equipped modules.** The `modules` section now returns the full inventory including `equipped_slot`. Fetch the live data — do not ask the player unless the tool call fails. If knowing the current loadout matters for advice (e.g., "what should I swap?"), retrieve it from the tool response first.

> "What modules are you currently running in each slot?"

For detailed strategic notes and build interactions, see `references/strategy.md`.

### Quick Reference Tier List (v27, Late/Endgame)

**S+ Tier**
- DC (Dimension Core) — best core for farming, milestone, and most tournament runs; greatest damage increase mod in the game

**S Tier**
- OA (Orbital Augment) — CC and % health damage; one of the only mods that damages fleets; excellent across farming, tournament, and milestone

**A Tier**
- PH (Pulsar Harvester) — highest damage potential of generator mods but takes time to ramp
- PF (Project Funding) — massive damage multiplier with full uptime if cash digit count is high
- ACP (Anti-Cube Portal) — near-permanent 25x damage increase with shockwave
- DP (Death Penalty) — CC is everything endgame; 15% one-shot chance vs fleets at Ancestral; great as assist mod
- BHD (Black Hole Digestor) — best farming mod in the game for coins

**B Tier**
- MH (Magnetic Hook) — synergizes with AOE mastery + assist mods for near-perma fleet stun
- SD (Space Displacer) — enables major stall/CC setups with the right build
- SH (Singularity Harness) — second-best farming mod; bot range + Flame Bot double damage
- AS (Amplifying Strike) — near-perma 5x damage multiplier with Enemy Balance Mastery card
- HC (Harmony Conductor) — mediocre alone; strong in CC builds; can stun overcharge enemies
- GC (Galaxy Compressor) — niche value for Smart Missiles and perma Black Hole setups

**C Tier**
- OC (Om Chip) — situational; useful with CF+ for boss-focused damage; ~16 activations per run
- AD (Astral Deliverance) — only viable in specific bounce-web strategies
- PC (Primordial Collapse) — reduces overcharge damage in Legends tournament; extra BH coverage in farming

**D Tier**
- BA (Being Annihilator) — minimal value; supercrit chance already >50% endgame
- NMP (Negative Mass Projector) — some CC use cases but outclassed by OA
- MVN (Multiverse Nexus) — poor man's UW sync for farming; increases DW cooldown (makes DW worse); only situational use is slowing enemy death to hit orbs
- SF (Sharp Fortitude) — best EHP mod but outshined by PC for countering overcharge
- SR (Shrink Ray) — very limited use case
- RB (Restorative Bonus) — early game or niche web/KB strategy use only; not a lategame pick
- HB (Havoc Bringer) — no reason to use in current meta; possibly marginal in AD+bounce+range builds

**F Tier**
- WHR (Wormhole Redirector) — useless at endgame

---

## Build Context Notes

**Glass Cannon (endgame meta)**: Destroy enemies as fast as possible before they reach the tower. Prioritizes damage output. Top picks: DC, OA, AS or DP (cannon), PF or PH (generator).

**Farming runs**: Maximizing coin income. BHD is the best farming generator. SH is second best. MVN has a niche use here — slowing enemy death to ensure orb hits before kill. DC is best core for farming.

**Tournament runs**: Conditions vary. GC shines when UW cooldown reduction matters. DP is strong because CC (especially fleet one-shots) is king. Check whether any battle condition directly counters or amplifies a module's effect before locking in a loadout. No modules are hard-banned in tournaments — it's situational.

**CC builds**: OA + SD + MH + HC + DP + perma BH can create near-perma fleet stun. CC is increasingly the dominant endgame strategy.

**UW sync**: MVN forces DW/GT/BH to fire together but averages the cooldowns (net negative at Epic/Legendary). Only becomes neutral at Mythic and beneficial at Ancestral. Don't use MVN to replace proper manual sync or a Dimension Core setup.

---

## Using the tower-analyzer MCP

Always call the relevant tools before making personalized recommendations. Generic advice is fine for general questions, but for reroll, ban, or leveling recommendations, live data is required.

### Tool Usage Guide

**`get_currencies`** — Call this whenever:
- Recommending a reroll strategy ("should I reroll now or wait?")
- Assessing leveling feasibility (do they have enough shards to reach a target level?)
- Evaluating if a merge is affordable
- **Reroll shard threshold check**: Before recommending any reroll campaign, verify the player has enough reroll shards to realistically pursue their target. Rerolling without enough shards often means stripping a good sub-stat and not having enough to recover. If shards are low, recommend farming first.
- **Currency freshness check**: After calling `get_currencies`, cross-check shard balances against the player's apparent progression level. If balances seem implausibly low (e.g., a high-tier player with near-zero shards of all types), the snapshot may be stale and not reflect the latest run state. Flag this: "Your shard balances may not have updated yet — are these current?" before making leveling or reroll recommendations.
- **Shard-to-level feasibility check**: Compare available type-specific shards against the leveling cost table (see `references/leveling-costs.md`). State explicitly whether they have enough shards to reach the target level, and if not, how many more are needed and roughly how long that will take based on `get_shard_rates`.

**`get_shard_rates`** — Call this when projecting time-to-milestone or advising on leveling priority. Tells you how fast they're accumulating shards and when they'll hit target levels.

**`get_lab_plan`** — Call this when advising on lab prioritization. Shows current lab slots, what's running, and upcoming transitions.

**`get_cell_income`** — Call this when advising on lab speed boosts. Tells you if they can afford to boost and whether it's sustainable.

**`get_recent_runs`** — Call this to establish run context without asking the user directly:
- Is a run currently in progress? (affects what module actions are available)
- What run type is the player doing — farming, tournament, milestone?
- What tier are they running? (relevant for shard drop rates and strategy advice)
- Use `runType` filter (e.g., "Tournament", "Farming") to get relevant history when advising on loadout for a specific mode

**`get_tower_state`** — Call this for context on UW stats, module inventory, and workshop state. Returns:
- `version` — current tower era
- `ultimate_weapons` — full UW state (all 9 UWs with per-stat level, value, stones invested/to-max/to-target)
- `modules` — module inventory grouped by type (`Cannon`, `Armor`, `Generator`, `Core`). Each entry includes: `id`, `code`, `name`, `owned`, `rarity`, `stars`, `level`, `equipped_slot`, `ability_values` (map of rarity → effect value), `substats` (array with `slot`, `key`, `rarity`, `locked`), `copies` (array of copy rarities), `shattered_epics`, `presets` (preset assignments)
- `health_plus_level`, `wall_health_plus_level` — workshop enhancement levels

Call this whenever you need to know the player's module loadout, owned natural epics, rarity/star counts, sub-stats, or copies. Workshop stats, relic data, and lab levels are not yet present — see the API Improvement Backlog section.

### Critical Personalization Rules

**Never assume what is capped for one player is capped for another.** Stats like Crit Chance, Defense, or Death Defy reach their caps at different points depending on each player's relics, lab levels, workshop levels, and equipped sub-stats. Always verify the player's current value of a stat before recommending a ban or treating it as a ban candidate.

**Sub-stat caps are not permanent.** A stat may be capped today partly because of a sub-stat currently equipped. If the player rerolls that slot, the cap disappears and the stat becomes relevant again. Before recommending a ban on a stat that "seems capped," confirm whether the cap is coming from permanent sources (relics, labs, workshop) or from a sub-stat that could be lost on a future reroll. If the cap depends on a sub-stat, do NOT recommend banning — and recommend locking that sub-stat instead.

**Reroll recommendations require a shard check first.** Always call `get_currencies` before advising a reroll campaign. The decision tree is:
1. How many reroll shards do they have?
2. How many sub-stat slots need improvement, and how many are they planning to lock?
3. Given lock costs (0 locks=10, 1=40, 2=160, 3=500, 4=1000, 5=1600 shards), is their current stockpile sufficient to realistically achieve the target, or will they run dry mid-campaign and risk being stuck with a worse outcome than they started with?
4. If shards are insufficient, recommend farming more first (boss drops, daily missions, reroll shard lab progress) before beginning.

---

## Reference Files

- `references/substats.md` — Full sub-stat tables for all 4 module types across all rarities
- `references/natural-epics.md` — Complete natural epic roster (all 24) with full effect scaling across Epic → Ancestral
- `references/leveling-costs.md` — Full shard and coin cost table from level 1 to 200
- `references/labs.md` — All module labs with unlock tiers, full cost tables, and priority guidance
- `references/strategy.md` — Extended strategic notes, build synergies, and situational advice

---

## API Improvement Backlog

This section tracks data that would improve personalized recommendations but is not yet available via the tower-analyzer MCP. When `get_tower_state` or other tools return incomplete data for a recommendation, check this list first — if the gap is already known, note it to the user and ask them to provide the value manually. If it's a new gap, add it here.

### Known Gaps in `get_tower_state`

**Current status**: Endpoint live and working. Confirmed fields: `version`, `run_active` (pending), `cf` (duration, cooldown, lab_duration), `bh` (duration, cooldown), `gt` (duration, cooldown), `health_plus_level`, `wall_health_plus_level`, `cf_stones_remaining`.

**UW fields still missing**: `cf.speed_reduction`, `bh.size`, `bh.extra_bh_lab_unlocked`, `gt.bonus`, `dw.*`, `sm.*`, `spotlight.*`, `ilm.*`, `ps.*`, `cl.*` — all UWs beyond CF/BH/GT not yet in response.

**Entire sections not yet present**:
- `run_active` boolean — needed before any reroll/level/merge recommendation
- `workshop.*` — per-stat values with contributor breakdown (workshop + relics + module substats + cap)
- `modules.effect_bans` — which stats are currently banned per type (must ask player)
- `labs.*` — current levels of all module-relevant labs

**Resolved gaps** (now available via `modules` in `get_tower_state`):
- `modules.equipped` ✅ — `equipped_slot` field on each module entry
- `modules.natural_epics` ✅ — full inventory with `rarity`, `stars`, `owned` per module

### Known Gaps in Other Tools

**`get_currencies` — reroll shards confirmed as single shared pool**
- Reroll shards are consumed from one pool regardless of which module type is rerolled. No per-type breakdown needed. ✅ resolved.

**`get_recent_runs` — no `run_in_progress` flag**
- Most recent run date can hint at whether a run is active but is not definitive. A `run_active` boolean on `get_tower_state` is the correct fix.

### Workaround Protocol

When a required data point is missing from MCP responses:
1. State clearly what data is needed and why it affects the recommendation
2. Ask the user to provide it directly (e.g., "What's your current Crit Chance including relics and labs?")
3. Make the recommendation conditional if the user cannot provide the value (e.g., "If your Crit Chance is already above 100% from other sources, ban it; if not, keep it")
4. Note the gap as a candidate for the next `get_tower_state` or inventory endpoint improvement

---

## Run State Constraints

The game locks certain module actions when a run is in progress. Always determine whether the player is currently in a run before recommending any of these actions.

### Locked During a Run
- **Module rerolls** — cannot reroll sub-stats on any module while a run is active
- **Module level upgrades** — cannot spend shards to level up modules during a run
- **Merging equipped modules** — cannot merge a module that is currently equipped

### Available During a Run
- **Merging unequipped modules** — fodder merges and building the merge pipeline can be done anytime, even mid-run, as long as the module being merged is not currently equipped

If the player asks to reroll or level up a module and a run is in progress, advise them to wait until the run ends. If they want to do productive module work mid-run, redirect to fodder merging as the available action.

---

## Farming vs. Tournament Module Strategy

Modules serve fundamentally different purposes depending on the game mode. Always establish context before making recommendations — the optimal loadout for farming is often wrong for tournaments and vice versa.

### Farming Runs
**Goal**: Maximize coin income and elite cell income per run.

**Why it matters**:
- Coins fund workshop upgrades, lab research, and module leveling
- Elite cells fund lab speed boosts (1.5x, 2x, 3x, 4x, 5x, 6x, 7x, 8x multipliers for 1h/8h/24h durations)
- Boosting multiple labs at moderate multiplier (e.g., 1.5x across all slots) is more cell-efficient than one lab at high multiplier

**Module priorities for farming**:
- Generator: BHD (best coin farming mod) or SH (second best)
- Core: DC for damage ceiling; MVN at Ancestral only if specifically targeting GT/BH/DW sync for coin spikes
- Cannon: AS for near-perma 5x damage (more kills = more coin opportunities)
- Armor: OA for CC keeping enemies alive long enough to hit orbs, and reaching fleets

### Tournament Runs
**Goal**: Maximize waves completed to achieve highest possible placement.

**Why it matters**:
- Tournament placement determines stone income
- Stones are used to: upgrade Ultimate Weapons, unlock assist module slots (1000 stones each), and unlock card masteries
- Stone income is one of the most important long-term progression currencies — tournament placement directly gates how fast UWs and assist slots develop

**Module priorities for tournaments**:
- Vary significantly by tournament type and active battle conditions
- CC modules (DP, OA, MH, HC, SD) become more valuable as waves increase — fleets are the primary threat at high waves
- GC shines in tournaments with UW-heavy strategies (Smart Missiles, perma BH)
- Always check battle conditions before locking a tournament loadout — a condition can neutralize or amplify any module's effectiveness
- DP is particularly valuable in tournaments: 15% Ancestral one-shot chance vs. fleets, and strong as an assist module

### Key Strategic Difference
In farming, you can afford to let enemies live longer (orb hits, coin income). In tournaments, you want enemies dead as fast as possible before they overwhelm you. This means some modules that are mediocre in farming (DP) become excellent in tournaments, and some farming staples (BHD, MVN) lose their value entirely in tournament context.

When a player asks for module advice, always ask or infer: **farming run, tournament run, or milestone run?** before recommending a loadout.
