---
name: tower-uw
description: >
  Expert knowledge on Ultimate Weapons (UWs) in The Tower: Idle Tower Defense. Use this skill
  whenever the user asks anything about UWs — including upgrade costs, damage/stat scaling,
  which UWs to prioritize, how to invest stones, UW+ abilities, synergies with modules and
  workshop, or build archetypes (glass cannon, farming, tournament). Also trigger for questions
  about specific UW names (e.g. "Chrono Field", "CF+", "Death Wave", "Black Hole", "Golden Tower",
  "Smart Missiles", "Chain Lightning", "Poison Swamp", "Inner Land Mines", "Spotlight"),
  UW leveling strategy, stone budgeting, UW interactions, or how UWs fit into late/endgame min-maxing.
  Trigger even for indirect questions like "what should I upgrade next", "how do I sync my UWs",
  or "is my GT worth maxing" — these almost always require UW expertise.
---

# The Tower: Ultimate Weapons (UW) Expert Skill

## Overview

Ultimate Weapons are powerful tower abilities unlocked and upgraded using **Stones** (earned primarily from tournament placement). Each UW has:
- A **base weapon** with 3 upgradeable stats (each with their own cost tables)
- A **UW+ ability** — an enhanced effect unlocked with additional stone investment

Stones are one of the most progression-gating currencies in the game. Investing them correctly is critical.

### Purchasing UWs
When you buy a UW, the game presents **3 random options** (equal probability for each UW). You are not guaranteed to see the UW you want. You can close the menu without purchasing — the same 3 choices will remain until you buy one. Plan stone budgets accordingly: you may need to buy an off-priority UW before the one you want appears.

**UW+ unlock requirement**: All 9 UWs must be unlocked before any UW+ ability can be purchased. UW+ abilities can then be bought in any order, with each purchase increasing the cost of the next one.

For full stat scaling tables, see `references/uw-stats.md`.

---

## The 8 Ultimate Weapons

### Chain Lightning (CL)
**What it does**: Fires a lightning bolt that chains between enemies, dealing scaling damage to each target hit.

**Base stats**:
- **Damage**: x2 → x7,961 (32 levels)
- **Quantity**: x1 → x5 (5 levels) — number of simultaneous CL bolts
- **Chance**: 5.0% → 27.5% (16 levels) — proc chance per shot

**UW+ — Smite**: Each CL hit has a 0.05% → 0.60% chance (scaled by CL's base Chance stat) to deal bonus damage equal to a % of the current wave's HP. Capped at 100 hits per enemy.

**Strategic notes**:
- CL's value is almost entirely in **Quantity** — more simultaneous bolts = dramatically more coverage
- Damage multiplier matters but Quantity is the leverage point early
- Smite (CL+) has niche value — it scales with wave HP rather than tower damage, which can be strong at high waves, but the proc rate is low; don't over-invest early
- CL is a **support DPS** UW, not a primary damage source at endgame

---

### Smart Missiles (SM)
**What it does**: Fires a volley of missiles at enemies, each dealing high single-target damage.

**Base stats**:
- **Damage**: x10 → x3,021 (31 levels)
- **Quantity**: x5 → x20 (16 levels) — missiles per volley
- **Cooldown**: 180s → 20s (16 levels)

**UW+ — Cover Fire**: Reduces cooldown by an additional 2s–13s after firing SM (additive with base cooldown). At max, Cover Fire adds 2s of reduced CD (on top of the base 20s CD). Enables near-permanent SM uptime at high investment.

**Strategic notes**:
- **SM is widely considered useless by top-end players** — it does not scale competitively at endgame and is generally not worth significant stone investment
- If you do invest, Cooldown is the most impactful stat — without CD reduction SM rarely fires often enough to matter
- Cover Fire (SM+) enables near-permanent SM uptime at max investment, but the damage output still underperforms other UWs
- GC (Galaxy Compressor) module can reduce SM CD further, but stones and modules are better spent elsewhere
- SM is a trap for newer players — it looks strong early but falls off hard at endgame

---

### Death Wave (DW)
**What it does**: Sends a radial wave outward from the tower that deals damage to all enemies in its path.

**Base stats**:
- **Damage**: x2 → x9,119 (33 levels) — highest raw damage ceiling of all UWs
- **Quantity**: x1 → x5 (5 levels) — waves per activation
- **Cooldown**: 300s → 50s (28 levels)

**UW+ — Kill Wall**: On DW activation, additionally sends a wall that multiplies kill counts (x3 → x108 multiplier). Kill Wall is a **farming-critical** ability — each "kill" from KW counts for orb hit chances and coin rewards.

**Strategic notes**:
- DW has the **highest raw damage** of any UW — essential for milestone/wave push runs
- Long base cooldown (300s) means CD reduction is a priority investment
- **Kill Wall is S-tier for farming** — it multiplies the number of coin-eligible kill events per wave
- DW+KW is one of the core farming engines; the "kills" it generates hit orbs and multiply coin income
- DW Quantity is less impactful per stone than Cooldown — reduce CD first
- Syncs naturally with GT and BH in farming runs (see UW Sync section)

---

### Chrono Field (CF)
**What it does**: Creates a field around the tower that slows all enemies within range.

**Base stats**:
- **Duration**: 5s → 40s (36 levels)
- **–Speed**: 20% → 75% slow (12 levels) — enemy speed reduction
- **Cooldown**: 180s → 60s (13 levels)

**UW+ — Chrono Loop (CF+)**: Exerts a **tangential rotational force** on enemies within CF range, causing them to spiral around the tower rather than approach linearly. Also applies a **hidden speed slow** that stacks on top of the base –Speed stat.

**CF+ Rotation Rates**:
| CF+ Level | Radians/2s | Degrees/s | Full Orbit Time |
|-----------|-----------|-----------|----------------|
| 0 | 0.10 | 2.86°/s | 125.7s |
| 5 | 0.35 | 10.03°/s | 36s |
| 10 | 0.60 | 17.19°/s | 21s |
| 13 (Max) | 0.75 | 21.49°/s | 16.8s |

**CF+ Hidden Slow** (applied to all enemies in CF range, separate from base –Speed):
| CF+ Level | Hidden Slow | Effective Speed Multiplier |
|-----------|------------|---------------------------|
| 0 | 2.5% | 0.975× |
| 5 | 25% | 0.75× |
| 10 | 50% | 0.50× (enemies at half speed) |
| 13 (Max) | 65% | 0.35× (enemies at ~1/3 speed) |

**Formula**: `newEnemySpeed = enemySpeed × (1 + (CF+Level × -0.05))`

**Strategic notes**:
- CF is the **premier CC (crowd control) UW** — slowing enemies gives your tower more shots per enemy
- CF+ rotation effect is especially powerful at **low enemy speeds** — enemies with low speed can enter near-permanent orbit
- The hidden slow from CF+ does **not suffer diminishing returns** — each level is worth investing
- Duration and –Speed are both priority stats; CD reduction is secondary
- CF synergizes with OC (Om Chip), SD (Space Displacer), and MH (Magnetic Hook) for extended CC chains
- At max CF+ + high –Speed + slow enemy: enemies can orbit indefinitely, making wave progression essentially zero

---

### Inner Land Mines (ILM)
**What it does**: Places mines around the tower interior that detonate on enemy contact for high damage.

**Base stats**:
- **Damage**: x10 → x3,021 (32 levels)
- **Quantity**: x3 → x6 (4 levels) — active mines at a time
- **Cooldown**: 200s → 50s (17 levels)

**UW+ — Charged Mines**: Mines continuously charge over time, gaining bonus damage at 0.50/s → 50.90/s charge rate. Fully charged mines deal significantly amplified damage.

**Strategic notes**:
- ILM is a **mid-tier DPS** UW — solid damage but placement-dependent
- Quantity (more mines) is important for coverage but only 4 levels total
- Charged Mines at high investment creates massive burst windows, especially against boss waves
- ILM is not a meta-defining UW but fills a role in builds that lack single-target burst
- Cooldown reduction gives more mine placement cycles per run

---

### Golden Tower (GT)
**What it does**: Temporarily transforms the tower into a "Golden Tower," massively multiplying all damage output for the duration.

**Base stats**:
- **Multiplier**: x5.0 → x21.0 (21 levels) — damage multiplier during GT window
- **Duration**: 15s → 53s (39 levels) — how long the golden state lasts
- **Cooldown**: 300s → 100s (22 levels)

**UW+ — Golden Combo**: After GT ends, each enemy killed grants bonus cash and coins equal to x% (0.03% → 0.45%). A post-GT income window that rewards high kill counts immediately after the GT window closes.

**Strategic notes**:
- GT is a **S-tier farming UW** — the **coin multiplier** during GT windows is enormous
- Duration and Cooldown are the most critical stats — more GT uptime = dramatically more coins and wave clearing
- Multiplier matters but Duration/Cooldown investment has better ROI early
- **GT syncs with DW and BH** in farming: activate all three simultaneously to stack bonuses
- Golden Combo (GT+) generates a post-GT income burst — the more enemies killed in the window after GT ends, the more coins; pairs well with DW Kill Wall which generates large kill counts
- **GT has no effect in tournaments** — tournaments are about clearing waves, not earning coins; GT's multiplier is irrelevant outside of farming

---

### Poison Swamp (PS)
**What it does**: Creates a toxic area that deals sustained damage-over-time to all enemies within it.

**Base stats**:
- **Damage**: x10 → x3,021 (32 levels)
- **Duration**: 30s → 100s (15 levels)
- **Cooldown**: 125s → 50s (17 levels)

**UW+ — Death Creep**: Enemies that die in the Poison Swamp have a 120% → 1110% increased chance to spawn as a "Death Creep" (an additional kill credit that generates coins/orb hits). At max, 1110% bonus spawn chance.

**Strategic notes**:
- PS has the **shortest base cooldown** (125s) of the area-effect UWs — high natural uptime
- PS Duration investment is key — longer swamps = more enemies get full DoT ticks
- Death Creep (PS+) is a **farming multiplier** — stacks with DW Kill Wall for massive coin output
- PS + DW + Kill Wall is a potent farming trio: KW generates kills, PS generates Death Creep kills, all converting to coins
- PS is lower priority than DW/GT for tournament play but valuable in farming-optimized builds

---

### Black Hole (BH)
**What it does**: Creates a gravitational singularity that pulls enemies toward it and deals damage.

**Base stats**:
- **Size**: 30m → 70m (21 levels) — radius of the BH pull field
- **Duration**: 15s → 38s (24 levels)
- **Cooldown**: 200s → 50s (17 levels)

**UW+ — Consume**: At the end of each BH activation, deals damage equal to 0.05% → 0.75% of the current wave's HP to every enemy that was affected by the BH. A burst nuke tied to wave HP scaling, hitting the entire clustered group simultaneously.

**Strategic notes**:
- BH is **S-tier for CC** — pulls enemies off their path and clusters them for AoE damage
- Size is critical — larger BH captures more enemies and enables more AoE stacking
- BH + DW combo is meta: BH clusters enemies → DW wave hits the cluster for massive damage
- BH Consume (BH+) is a wave-HP-scaling nuke that fires at the end of each BH window, hitting every enemy in the cluster — extremely powerful when BH pulls in large groups, especially at high waves where wave HP is high
- BH Duration investment amplifies Consume: longer BH = more enemies pulled in before the end-of-activation nuke fires
- Perma-BH (via GC module + CD reduction) is a tournament-viable strategy: near-permanent enemy clustering

---

### Spotlight (SL)
**What it does**: Projects a focused beam that multiplies damage dealt to any enemy caught in its cone.

**Base stats**:
- **Multiplier**: x8.0 → x43.0 (26 levels) — damage multiplier for targets in spotlight
- **Angle**: 30° → 90° (61 levels) — cone width
- **Quantity**: x1 → x4 (4 levels) — number of simultaneous spotlights

**UW+ — Light Range**: Boosts SL's damage multiplier by x times your damage-per-meter stat. The bonus scales with your tower range — longer range = larger Light Range bonus. Makes SL's damage increasingly powerful as range is upgraded.

**Strategic notes**:
- SL is a **focused burst** UW — the damage multiplier is excellent but limited to the cone
- Angle is critical for coverage — more degrees = more enemies hit simultaneously
- Quantity (multiple beams) is very expensive but transformative — x4 spotlights with wide angle is devastating
- **SL synergizes exceptionally well with BH** — BH clusters enemies into SL's cone
- In endgame builds, SL + BH is a top-tier combo: BH clusters, SL amplifies all damage dealt to the cluster
- **SL+ Light Range** scales with tower range — players who have invested heavily in range get significantly more value from it; this makes SL+ priority dependent on range investment
- Angle has 61 upgrade levels — the longest upgrade track of any single stat in the game; budget carefully

---

## Stone Investment Strategy

### General Priority Framework

**Early game** (limited stones): Focus on getting DW, BH, and GT to functional levels. These three have the highest combined impact on run progression.

**Mid game**: Lower DW and BH cooldowns to create reliable activation windows. Invest in CF –Speed and Duration for CC.

**Late/endgame**: Max-out high-ROI stats (DW damage, GT duration, BH size), then pursue UW+ investments that match your build.

### UW Priority Tier List (Stones)

**S Tier** (invest heavily):
- **DW** — highest damage ceiling, Kill Wall is critical for farming
- **GT** — massive coin multiplier during GT windows; high uptime = highest coin output; **farming only** — no effect in tournaments
- **BH** — best CC, enables cluster combos, essential for farming and tournament
- **CF** — essential CC; CF+ rotation is game-changing at high levels

**A Tier** (solid investment):
- **SL** — high burst damage when combined with BH; angle is the key stat

**B Tier** (situational):
- **PS** — good farming support with Death Creep; less impactful in tournaments
- **SM** — considered useless by most top-end players; falls off hard at endgame; invest only if you have excess stones
- **ILM** — solid but outclassed; Charged Mines is niche

**C Tier / Low Priority**:
- **CL** — functional early but doesn't scale well vs. other UWs at endgame; Smite (CL+) has weak ROI

### UW+ Investment Priority

1. **Kill Wall (DW+)** — highest farming impact; transforms DW into a coin engine
2. **CF+ (Chrono Loop)** — rotation + hidden slow; enormously increases CC effectiveness
3. **Consume (BH+)** — wave-HP-scaling nuke on the entire BH cluster; scales extremely well at high waves
4. **Golden Combo (GT+)** — post-GT income burst per kill; pairs well with Kill Wall's high kill counts
5. **Death Creep (PS+)** — farming multiplier; pairs with Kill Wall
6. **Light Range (SL+)** — scales with tower range; priority increases with range investment
7. **Smite (CL+)** — wave-HP damage on CL proc; niche but scales with wave HP at high waves
8. **Charged Mines (ILM+)** — niche; only invest after core UWs are developed
9. **Cover Fire (SM+)** — low priority given SM's weak endgame scaling

---

## UW Synergies

### Core Combos

**BH + DW**: The defining combo. BH clusters enemies into a tight group → DW fires and hits the entire cluster. At max investment this is the highest single-event damage in the game. Timing matters: activate BH first, wait ~3s for enemies to cluster, then fire DW. BH+ Consume then fires a wave-HP nuke on the cluster at the end of BH's activation, adding a third damage event to the same group.

**GT + DW + BH (Triple Sync)**: Fire all three in the same activation window. GT multiplies all damage including DW damage. BH clusters enemies for DW. This triple window is the primary coin spike in farming runs.

**CF + BH**: CF slows enemies + BH pulls them. Combined CC is devastating. CF+ rotation can hold enemies in orbit while BH drags them toward the cluster point.

**SP + BH**: BH clusters enemies into SL's cone. SL multiplier applies to all clustered enemies hit — enormous burst output.

**SM + GC Module**: Galaxy Compressor module reduces UW cooldowns. SM + GC can enable near-permanent SM firing, making SM one of the highest sustained DPS options in the game.

**PS + DW Kill Wall**: Kill Wall generates kill credits → PS Death Creep generates more kills from those → multiplicative coin output.

### UW Sync (Manual)

UWs do not auto-sync. Players must manually activate them in sequence to align activation windows. Key principle: **slower cooldown UWs set the rhythm**, faster ones fill in between.

DW has the longest cooldown (300s base) — it's the anchor. GT and BH have similar cooldowns (300s and 200s base). Reducing all three to similar CDs enables consistent sync windows.

**MVN Module Warning**: Multiverse Nexus module forces DW/GT/BH to sync but averages their cooldowns — this is net negative at Epic/Legendary rarity. Only use MVN at Ancestral and only if you don't have better core options.

---

## Build Archetypes

Build archetypes describe how the tower is configured. Run types (farming, tournament, milestone) describe what you're trying to accomplish in a given run. A single archetype can be used across multiple run types — for example, Glass Cannon is the primary archetype for farming runs above T14 but can also be used in tournaments.

### Glass Cannon (GC)
**Philosophy**: Kill enemies as fast as possible before they reach the tower. Maximize damage output and coin generation simultaneously.

**When used**: The dominant farming build above T14. Also viable in tournaments where burst damage clears waves efficiently.

**UW priority**: GT (duration, CD) → BH (size, CD) → DW (damage, CD, Kill Wall) → CF (duration, –Speed) → SL (angle, multiplier)

**Core combo**: Sync GT + DW + BH. GT multiplies coins from kills during its window; BH clusters enemies for DW; DW's Kill Wall floods the kill-event count for maximum coin output. CF slows stragglers to keep them in range.

**Module synergy**: DC (Dimension Core), PF (Project Funding), AS (Amplifying Strike), OA (Orbital Augment).

---

### Effective Health Pool (eHP)
**Philosophy**: Survive incoming damage by maximizing the tower's effective health through raw health, damage reduction (Def%), and regeneration. Enemies are allowed to reach and hit the tower; the tower outlasts them rather than killing them before contact. Orbs are the primary kill mechanism. This is the dominant early-game build (T1–T9) but has a low progress ceiling — most players eventually transition to Glass Cannon for higher tiers.

**Strengths**:
- Cheap to start; no UWs required to begin
- Makes early tier milestones and relics easier to unlock
- Strong at T1–T9 where health scales well

**Weaknesses**:
- Difficulties killing protectors, especially early on
- Low progress ceiling — falls off at higher tiers
- Can struggle to vampire enemies

**When used**: Standard build for early game. Players should plan to transition out of eHP eventually and invest accordingly — avoid over-committing to health-only upgrades that won't matter at endgame.

**UWs for eHP**:
- **SL (Spotlight)** — raises damage to kill protectors faster; also provides a coin boost
- **BH (Black Hole)** — deals % of enemy health over time, very effective against protectors; at max investment BH can deal 84% of enemy HP per activation
- **DW (Death Wave)** — very useful for eHP; adds kill capability the build otherwise lacks
- **CF (Chrono Field)** — best chip damage reduction in the game; highly recommended but stone/lab cost is steep. If you're approaching the transition to a damage build, consider investing in CF early to begin its long labs. CF is the best UW for reducing hits taken and improving the effectiveness of all other survivability upgrades.
- GT and BH are preferred early unlocks that significantly speed up progress even in eHP builds

**Key survival diagnostics**:
- Getting 1-shot by bosses → need more health
- Dying from multiple boss hits in a row → need more lifesteal (attack speed, damage, crit factor)
- Dying from regular enemies → need more CC (attack speed, orbs)
- Protectors letting enemies through → need more damage

**Workshop focus**: Maximize cheap/short defense upgrades first so Free Upgrades hits Health more often during rounds. Keep Health and Damage at roughly 1:1 or 1:2 ratio in favor of health. Eventually unlock Enemy Attack Level Skip (EALS) and Enemy Health Level Skip (EHLS) — expensive but important: EALS improves survival, EHLS makes protectors killable for longer.

**Key labs**:
- Game speed (max), Lab speed (max)
- Attack Speed (max) — factors into knockback and DPS; knockback reduces hits taken on all builds
- Health (lvl 40–50) — don't over-invest; you'll spec out of this eventually
- Def% (lvl 23) — hits 98% hard cap combined with max Standard Bonus Perk Lab + Def% card + Def% module sub-stat
- Orb Speed (max), BH Damage (max), GT Duration (max)

**Module synergy**: Wormhole Redirector significantly helps survivability in eHP. No modules are strictly required to start.

### Hybrid Build
**Philosophy**: Invest equally into damage and health-related stats, achieving a balanced build that can both survive hits and kill enemies efficiently. Neither pure glass cannon nor pure tank — the tower can take some punishment while still dealing meaningful damage.

**When used**:
- **Farming**: The standard farming build for T1–T11. T12 Hybrid farming is still viable with a well-developed wall, but from T12 onward it is generally advisable to transition to Glass Cannon for farming.
- **Tournaments**: Used at all levels including by top players. Unlike farming, where GC eventually dominates, Hybrid remains the go-to tournament archetype because the balance of survivability and damage is more important in tournament conditions than raw kill speed.

**Strengths**:
- More forgiving than GC — the tower can absorb hits if enemies get through
- Scales well across a wide tier range
- Effective in tournaments where consistent wave clearing matters more than maximum burst

**Weaknesses**:
- Lower damage ceiling than GC in late farming
- Lower survivability ceiling than eHP at early tiers
- Requires balancing two upgrade tracks simultaneously (health and damage)

**UW priority**: GT (duration, CD) → BH (size, CD) → DW (damage, CD, Kill Wall) → CF (duration, –Speed) → SL (angle, multiplier)

**Core combo**: Same GT + BH + DW sync as GC, but the tower is built to survive the hits that a pure GC build would not. CF is more valuable in Hybrid than in GC because slowing enemies reduces incoming damage, complementing the health investment.

**Module synergy**: DC for damage ceiling, OA for fleet CC and % health damage, BHD for coin farming, armor modules for survivability.

---


These are not archetypes — they are the goals of a particular run. The same tower build can be used across run types, though the optimal UW activation strategy may differ:

- **Farming run**: Goal is maximizing coins. GC is the dominant farming archetype above T14. UW toggling strategy focuses on maximizing GT + DW + BH sync windows and controlling enemy death timing for orb hits and Kill Wall credits.
- **Tournament run**: Goal is clearing as many waves as possible for stone income. GC or eHP depending on tier and battle conditions. CC (BH, CF) becomes increasingly important at high waves where fleet enemies dominate.
- **Milestone run**: Goal is reaching a specific wave target. Similar priorities to tournament — survivability and CC matter more as waves get higher.

---

## Unlock Costs Reference

### Base UW Unlock Costs (Stones)
| UW Level | Cost |
|----------|------|
| 0 | 0 |
| 1 | 5 |
| 2 | 50 |
| 3 | 150 |
| 4 | 300 |
| 5 | 800 |
| 6 | 1,250 |
| 7 | 1,750 |
| 8 | 2,400 |
| 9 | 3,000 |

### UW+ Unlock Costs (Stones)
| UW+ Level | Cost |
|-----------|------|
| 0 | 0 |
| 1 | 500 |
| 2 | 625 |
| 3 | 750 |
| 4 | 975 |
| 5 | 1,250 |
| 6 | 1,650 |
| 7 | 2,200 |
| 8 | 2,900 |
| 9 | 3,800 |

For full per-level stat scaling and per-upgrade costs for every UW stat, see `references/uw-stats.md`.

---

## Run Mechanics

### Upgrading UWs During a Run
**UWs cannot be upgraded with stones while a run is in progress.** All stone investment must happen between runs. If a player asks about upgrading a UW and a run is active, advise them to wait until the run ends.

### Toggling UWs On/Off
During a run, each UW can be individually toggled on or off. This is a deliberate control mechanism — players choose which UWs are active based on what the current situation demands.

**Toggle limit**: UWs can only be toggled on/off **40 times total per run**. This is a hard cap — use toggles deliberately.

**CD reset on toggle**: Toggling a UW off and back on **resets its cooldown to the full base value**. This is a cost, not a benefit. If a UW has 30 seconds remaining on its CD and you toggle it off/on, it will restart from its full cooldown (e.g. 180s). Avoid unnecessary toggling when a UW is close to firing.

**Auto-fire**: UWs activate automatically when their cooldown reaches zero. The cooldown timer resets immediately even if the UW is still actively firing — so a new CD begins while the current activation is still running.

**Why you might turn a UW off**:
- **Enemies are dying too fast**: If enemies are being killed before they reach DW range or get pulled into BH, those UWs aren't generating kills/coins. Turning off high-damage UWs temporarily lets enemies accumulate, so DW and BH can tag more of them and generate more kill events (orb hits, Kill Wall credits, Death Creep procs).
- **Preventing unwanted interference**: A UW firing at the wrong time can disrupt a sync window or waste its CD outside of a GT multiplier window.
- **Deliberate staging**: Letting enemies stack up before enabling a UW maximizes the number of targets hit when it fires.

The key principle: **UW activation timing directly affects coin generation**. In farming runs especially, controlling when and whether each UW fires is as important as the UWs' raw stats.

---

## Using the tower-analyzer MCP

When giving personalized UW advice, call the relevant tools first:

**`get_tower_state`** — Returns current UW stats (confirmed working: CF duration/cooldown, BH duration/cooldown, GT duration/cooldown). Use to verify current investment level before recommending upgrades. Note: many UW fields are still missing from the API (SM, DW, ILM, CL, PS, SP stats). When data is absent, ask the user directly.

**`get_currencies`** — Always check stone balance before recommending investment paths. Stones are the hardest gate in UW progression.

**`get_recent_runs`** — Determines whether the player is in a run (locks certain actions) and what run type they're doing (farming vs. tournament affects UW priority significantly).

### Personalization Rules

- **Never assume** a player's current UW levels match any "typical" benchmark. Levels vary enormously based on tournament placement history.
- **Always ask or verify** which UWs are unlocked and at what level before giving a specific stone budget recommendation.
- **Stone budgets are zero-sum** — every stone invested in one UW is not in another. When a player asks "what should I upgrade," give a clear priority order and explain the tradeoff.
- If the player says they're farming: bias toward GT/DW/BH/PS+KW. If tournament: bias toward BH/CF/SM. If both: establish which is currently the bottleneck.
