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

For full banner pull rates, pity systems, the Ancestral duplicate protection rule, natural
epic targeting, and gem budget strategy, see `references/tta-modules-buying.md`.

**Gacha draws**: 20 gems (x1) or 200 gems (x10). Pull rates:

- Common: 68.5%
- Rare: 29%
- Epic: 2.5%
- Epic pity: guaranteed after 150 pulls without one; Rare pity: guaranteed every 10 pulls

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

Leveling costs scale steeply — see `references/tta-module-level-costs.md` for the full table. Notable inflection points: costs jump hard at level 61 (25M coins), 101 (8B coins), 141 (500B coins), and 171+ (510T+ coins).

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


| Step                    | Input           | Materials Needed                                                    |
| ----------------------- | --------------- | ------------------------------------------------------------------- |
| Epic → Epic+           | 1 natural epic  | + 1 epic (any, same type)                                           |
| Epic+ → Legendary      | 1 epic+         | + 2x epic+ fodder (any, same type)                                  |
| Legendary → Legendary+ | 1 legendary     | + 1 epic+ (any, same type)                                          |
| Legendary+ → Mythic    | 1 legendary+    | + 1 legendary+ fodder (any, same type)                              |
| Mythic → Mythic+       | 1 mythic        | + 1 legendary+ fodder (any, same type)                              |
| Mythic+ → Ancestral    | 1 mythic+       | + 1 epic+ (any, same type)                                          |
| Ancestral → 1★        | 1 ancestral     | + 1**natural** epic+ (gacha-pulled epic merged to epic+, same type) |
| 1★ → 2★              | 1 ancestral 1★ | + 1**natural** epic+ (same type)                                    |
| 2★ → 3★              | 1 ancestral 2★ | + 1**natural** epic+ (same type)                                    |
| 3★ → 4★              | 1 ancestral 3★ | + 1**natural** epic+ (same type)                                    |
| 4★ → 5★              | 1 ancestral 4★ | + 1**natural** epic+ (same type)                                    |

**Important**: Ancestral star upgrades require **natural** (gacha-pulled) epics merged to epic+. Fodder epic+ built from Rares do NOT qualify. Each star costs one natural epic pull of the same type — making Ancestral 5★ extremely gem-intensive (5 additional natural epics beyond the keeper itself).

**Fodder pipeline ends at Mythic+**: Once your natural epic reaches Mythic+, the fodder merge path is complete. The Mythic+ → Ancestral step and all star upgrades only require natural epics.

### Fodder Upgrade Path

Fodders (built from Rares) are the merge material that fuels the natural epic upgrade chain.
The name of the resulting fodder is determined by whichever module is selected first in the merge.


| Step                    | Input                                  | Materials Needed | Result                                          |
| ----------------------- | -------------------------------------- | ---------------- | ----------------------------------------------- |
| Rare → Rare+           | 3x rare of the**same name**            | —               | Rare+                                           |
| Rare+ → Epic           | 3x rare+ (any name, same type)         | —               | Epic (no unique ability, name = first selected) |
| Epic → Epic+           | 1 epic + 1 epic (any, same type)       | —               | Epic+ fodder                                    |
| Epic+ → Legendary      | 3x epic+ (any name, same type)         | —               | Legendary fodder                                |
| Legendary → Legendary+ | 1 legendary + 1 epic+ (any, same type) | —               | Legendary+ fodder                               |

**Legendary+ fodder** is the material needed for Legendary+→Mythic and Mythic→Mythic+ upgrades on your natural epic.

### Fodder Investment Warning

Fodder modules are **disposable merge material**. Do not invest significant shards into them.
However, you need a sustained supply of Rares to build the fodder pipeline — Rares are obtained
from boss drops (0.5% base, improvable via lab) and gacha pulls. The boss drop lab for Rare
modules is a meaningful long-term investment for this reason.

---

## Module Shattering System

### Inventory Cap

Total module inventory is capped at **300 modules** across all types. Managing this cap
is an active part of the game — pulling on the banner without clearing inventory space
will block you once you hit 300.

### Shard Values

Shard yield per module depends on the **Shatter Shards lab** level (5 levels total,
expensive in coins and research time):

| Rarity | Lab Lv 0 | Lab Lv 1 | Lab Lv 5 |
|--------|----------|----------|----------|
| Common | 5 shards | 6 shards | 10 shards |
| Rare | 10 shards | 12 shards | 20 shards |

Each lab level adds 20% to the base shard yield. Prioritize completing this lab before
shattering large numbers of modules — the compounding difference is significant over time.

### Shattering a Merged Epic = Same Value as Its Rares

A fodder epic built from rares returns the **same total shard value** as shattering all
the rares that went into it. Merging does not create or destroy shard value — it only
compresses inventory slots. This makes merging rares up to epic a useful strategy when
approaching the 300-module inventory cap: you preserve the shard value in fewer slots,
freeing room for more pulls without discarding anything.

**After epic, you cannot shatter directly.** To shatter a module at epic or above, you
must first **unmerge** it (costs gems), which restores the component modules. Factor in
the unmerge cost when deciding whether to merge-to-store vs. shatter-now.

### What to Shatter

**Commons**: Shatter immediately. No merge value, no shard benefit from holding. If the
Shatter Shards lab is not yet at level 5, consider waiting — but commons will accumulate
fast enough that clearing them is generally better than capping out.

**Rares**: Hold as fodder pipeline material until you have a healthy surplus. Once your
pipeline is stable or you do not yet have a natural epic to push, rares can be shatted
or merged-to-epic for inventory compression. When the Shatter Shards lab is complete,
rares at 20 shards each become meaningful shard income.

**Stockpiling rares as epics**: while researching the Shatter Shards lab, merge excess
rares up to epic to compress inventory. Once the lab completes, shatter at
the improved rate.

---

## Sub-Stat System

Every module has sub-effect slots unlocked at level milestones. Sub-stats are **type-specific** (Cannon subs boost attack stats, Armor subs boost defense stats, etc.).

**Sub-stat rarity** is independent of the module's rarity, but the **maximum possible rarity** of a sub-stat equals the module's current rarity. Roll chances:

- Common: 46.2% | Rare: 40% | Epic: 10% | Legendary: 2.5% | Mythic: 1.0% | Ancestral: 0.3%

**Rerolling sub-stats** costs reroll shards (dropped by bosses):

- 0 locks: 10 shards | 1 lock: 40 | 2 locks: 160 | 3 locks: 500 | 4 locks: 1000 | 5 locks: 1600

**Locking strategy**: Lock desirable sub-stats before rerolling to protect them. Cost increases per lock, so prioritize locking your best sub first, then decide if the second slot is worth the cost jump.

**Auto-ban**: Spotlight Angle subs are auto-banned when angle is maxed via workshop/lab. Chrono Field Duration/Cooldown subs are auto-banned when those stats are maxed. This removes those slots from roll pool for free.

For full sub-stat tables by type, see `references/tta-module-substats.md`.

---

## Module Lab System

Module labs are unlocked progressively from Tier 4 through Tier 19. For full cost tables and detailed unlock info, see `references/tta-module-labs.md`.

**Lab infrastructure**: Start with 1 slot (T1 W30). Up to 5 slots total (4,900 gems total). Speed up with Elite Cells — boost multiple labs at 1.5x simultaneously rather than one lab at high multiplier for best cell efficiency.

**Labs Coin Discount** (T1 W30): Reduces coin cost of all labs. Low priority — research time, not coins, is usually the bottleneck.

For full lab data and unlock tiers, see `references/tta-module-labs.md`.

### Priority Rationale

- **Reroll Shards**: sub-stat optimization bottleneck — start early and run continuously
- **Module Shard/Coin Cost**: invest immediately on unlock; 163K+ shards needed to reach level 161 and coin costs above level 101 are brutal. After each lab level, restore the module to level 1 (all shards and coins are fully refunded) and re-upgrade at the discounted rate to immediately recoup the savings.
- **Rare Drop Chance**: triples base rate (0.5% → 1.5%), critical for sustaining the fodder pipeline
- **Unmerge Module**: trivial cost, crucial safety net — get it immediately
- **Core Effect Bans L1**: largest pool (7 slots), cheapest first level — highest priority first ban lab
- **Shatter Shards**: queue passively when slots are available; not urgent
- **Assist Module Labs**: strictly endgame (Quadrille coin costs)

### Lab Priority Order

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

**Always check `get_tower_state` before assuming anything about equipped modules.** The `modules` section returns the full inventory including `presets` assignments per module. Fetch the live data — do not ask the player unless the tool call fails. If knowing the current loadout matters for advice (e.g., "what should I swap?"), retrieve it from the tool response first.

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

If a required data point is unavailable from MCP tools, state what's missing, ask the user to provide it, and make the recommendation conditional on their answer.

### Tool Usage Guide

| Tool | Call when |
|---|---|
| `get_tower_state` | Need module loadout, rarity, stars, substats, or presets |
| `get_currencies` | Before any reroll, leveling, or merge affordability question |
| `get_shard_rates` | Projecting time to a target level |
| `get_recent_runs` | Establishing farming/tournament/milestone context without asking the user |
| `get_cell_income` | Advising on lab speed boosts |
| `get_lab_state` | Checking current level of any lab before making priority recommendations |

**`get_tower_state` response shape** — `modules` is grouped by type (`Cannon`, `Armor`, `Generator`, `Core`). Each entry includes: `id`, `code`, `name`, `owned`, `rarity`, `stars`, `level`, `ability_values` (map of rarity → effect value), `substats` (array with `slot`, `key`, `rarity`, `locked`), `copies` (array of copy rarities), `shattered_epics`, `presets` (array of `{ preset, slot }` assignments — use this to determine which preset a module is equipped in).

### Critical Personalization Rules

**Never assume what is capped for one player is capped for another.** Stats like Crit Chance, Defense, or Death Defy reach their caps at different points depending on each player's relics, lab levels, workshop levels, and equipped sub-stats. Always verify the player's current value of a stat before recommending a ban or treating it as a ban candidate.

**Sub-stat caps are not permanent.** A stat may be capped today partly because of a sub-stat currently equipped. If the player rerolls that slot, the cap disappears and the stat becomes relevant again. Before recommending a ban on a stat that "seems capped," confirm whether the cap is coming from permanent sources (relics, labs, workshop) or from a sub-stat that could be lost on a future reroll. If the cap depends on a sub-stat, do NOT recommend banning — and recommend locking that sub-stat instead.

**Reroll recommendations require a shard check first.** Always call `get_currencies` before advising a reroll campaign. Check: how many reroll shards do they have, how many slots need improvement, and how many they plan to lock. Lock costs are 0 locks=10, 1=40, 2=160, 3=500, 4=1000, 5=1600 shards. If their stockpile isn't sufficient to finish the campaign without running dry, recommend farming more shards first.

---

## Reference Files

- `references/tta-module-substats.md` — Full sub-stat tables for all 4 module types across all rarities
- `references/tta-natural-epics.md` — Complete natural epic roster (all 24) with full effect scaling across Epic → Ancestral
- `references/tta-module-level-costs.md` — Full shard and coin cost table from level 1 to 200
- `references/tta-module-labs.md` — All module labs with unlock tiers, full cost tables, and priority guidance
- `references/strategy.md` — Extended strategic notes, build synergies, and situational advice

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
