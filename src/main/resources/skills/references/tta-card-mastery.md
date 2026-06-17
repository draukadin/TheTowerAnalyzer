---
name: tower-card-mastery
description: >
  Strategic guidance on Card Mastery in The Tower: Idle Tower Defense — which masteries to
  unlock first, priority order by build type, category breakdowns, and research cost planning.
  Consulted by tta-cards.md. Use get_cards_state to verify which masteries are unlocked before
  making recommendations.
---

# Card Mastery

## Card vs. Mastery Distinctions

Two masteries are easily confused with their base card:

**Berserker card** ramps a damage multiplier (up to ×8) based on total damage absorbed during
the run — it builds passively as bosses and enemies hit you, making it valuable in tournament
for GC/hybrid builds where taking hits is inevitable. **Berserker mastery** is different: it
triggers a ×500 damage cap for up to 300s specifically after **Death Defy** activates. In
tournament, Death Defy is heavily nerfed and its activation rate is too low to rely on.
Berserker mastery is primarily a farming card mastery, not a tournament one.

**Slow Aura card** reduces enemy **movement speed** — enemies walk toward your tower more
slowly. **Slow Aura mastery** reduces enemy **attack speed** — enemies fire projectiles less
frequently. These are completely different mechanics. The mastery effect is the same as the
Nuke mastery (both reduce attack speed) and neither affects Fleet enemies, which are immune
to attack speed reduction.

---

## Prerequisites

- Every card must be maxed (7 stars) **except Area of Effect** (milestone-locked, unlocks
  very late — most players won't have it when they first reach mastery eligibility)
- Card Mastery Milestone: Tier 16, Wave 100

## Research Costs

Each mastery has 9 research levels. Base cost = **1 quadrillion coins × level** (level 5 =
5Q, level 9 = 9Q). The **Lab Coin Discount** lab reduces these costs — a sweet spot of ~20%
reduction is recommended before investing heavily. Going higher is worthwhile but research
time increases significantly past that point.

**Minimum income threshold before starting**: ~600–700 trillion coins/day. Below that,
mastery research will bottleneck immediately and provide poor return on stone investment.

---

## The Five Categories

Masteries group into five categories. Priority within each category depends on build type
(farming vs. GC vs. eHP vs. tournament). Compare within the same category first — there is
no useful comparison between Recovery Package and Damage since they solve different problems.

1. **General Buff** — benefits every build universally
2. **Economy** — coins, CPH, run efficiency
3. **Damage (GC)** — damage output for glass cannon and tournament
4. **EHP** — survivability and tankiness
5. **Control** — CC, enemy management

---

## Priority Tier List

### Top 6 (broad consensus across build types)

**1. Recovery Package** — highest priority first unlock regardless of build. Every recovery
package delivered has a chance (0.4% at unlock → 4% at max) to drop a common module. At
~840 packages per 1,000 waves with 84% package drop chance, that's ~320 common module drops
per hour. Common modules shatter for 5–10 shards. This compounds over time: higher module
level raises the base multiplier on every sub-stat, and the gap between players is largely
determined by module level. Starting this early prevents falling behind a gap that is very
hard to close.

**2. Damage** — linear scaling (×1.4 at level 1, ×5 at max). First 2–3 levels have high
ROI relative to research cost. Helps the GC transition and tournament performance without
requiring the full research investment that Super Tower demands. Recommended over Super Tower
as a starting damage mastery because it provides meaningful value at early levels.

**3. Wave Accelerator OR Demon Mode** (build-dependent):
- **Wave Accelerator** if transitioning to or already running GC: doubles spawn rate
  acceleration, reaching maximum enemy spawn rate at ~3,000 waves instead of ~6,000. This
  enables shorter, more efficient farming runs and is essential for making GC farming
  competitive with EHP in coins per hour.
- **Demon Mode** if staying in EHP and actively playing (not AFK): needs the Recharge Demon
  Mode lab (T14W60, recharges every 300 waves) for farming value. For tournament, recharge
  isn't needed since most runs end before 300 waves. Also requires the vault automation unlock
  for AFK farming. Without full setup, Demon Mode is weaker than Damage.

**4. Super Tower** — best damage mastery overall at high investment. Applies 35% of the Super
Tower bonus to UW damage, which also amplifies Spotlight's damage multiplier (reaching
200–250× SL multiplier when maxed). **Critical caveat**: value scales sharply with research
level. At low levels the 15s window / 30s cooldown gap leaves you unprotected for 2/3 of the
time. Near-max levels (6s gap) it becomes very strong; at full max it is 100% uptime. Do not
prioritize Super Tower if your coin income cannot sustain getting close to max research
quickly.

**5. Intro Sprint** — pairs directly with Wave Accelerator. At mastery, extends sprint up to
~1,800 waves, skipping directly to the wave depth where enemy spawns are near-maximum and
most perks are already unlocked. Together, WA + Intro Sprint enable very short runs (~3,000–
4,500 waves) that are still highly profitable because peak spawn rate is reached early. Also
useful for tournament (sprint through the first 300–400 waves where you have control, then
switch to a regular run).

**6. Enemy Balance + Cash** (treat as a pair, unlock soon after the top 5):
- **Enemy Balance**: chance for a second identical elite to spawn alongside the first.
  More elites = more elite cells = faster lab research. Diminishing returns on lab speed at
  very high cell counts (going from ×4 to ×5 lab speed is only 25% faster), but it is a
  compounding long-term benefit.
- **Cash**: chance for elites to drop reroll dice on death. Synergizes directly with Enemy
  Balance — more elites means more reroll dice drops. At 500 stones this is one of the
  cheapest masteries. Prioritize at module level 161+ when you need frequent rerolls for
  sub-stats and assist modules.

---

## Category Breakdowns

### General Buff

**Recovery Package** — see Top 6 above. Unlock first.

**Enemy Balance** — solid, not priority. Useful for compounding cell income and reroll dice
(via Cash mastery synergy). The diminishing returns on lab speed at high cell counts mean it
is "good not urgent." Unlock it as part of the Enemy Balance + Cash pair.

**Cash** — cheap (500 stones), niche before you need frequent module rerolls. Before module
level 161, reroll shards are less critical. After that point, the reroll dice from this
mastery become consistently valuable. Unlock together with Enemy Balance.

---

### Economy

**Wave Accelerator** — by far the best economy mastery. See Top 6 above.

**Intro Sprint** — pairs with Wave Accelerator. See Top 6 above.

**Extra Orb** — 750 stones. The mastery adds a coin bonus (×1.04→×1.40) on all enemies
tagged by an orb. Solid value but requires good orb coverage — if only ~50% of enemies are
being tagged, the effective gain is halved. Before unlocking, verify orb coverage by checking
whether the Extra Orb module has orb speed sub-stats and whether the orb range is tuned
correctly via the Orb Adjuster lab (T6W50). One of the later economy masteries to unlock.

**Coins** — +3% per level, +30% at max. Stable, unconditional coin boost. At 1,250 stones it
is the most expensive economy mastery. Lower priority than Wave Accelerator and Intro Sprint
despite being a clean multiplier — the CPH gains from run efficiency masteries exceed the
direct coin % at most stages.

**Wave Skip** — chance to double wave skip. Slightly less effective than Coins in practice
even at a lower stone cost. Not recommended as a priority mastery.

**Critical Coin** — chance to double the coin drop on a basic enemy kill (base card drops 1
coin; mastery upgrades to 2-coin drop chance). The majority of coin income comes from elites
and tanks (×8 base coins), not basic enemies — this mastery's impact on total income is
limited. Not strongly recommended.

**Free Upgrades** — lets you lock specific stats from receiving free upgrades (1 stat per
level, up to 10). Niche — primarily useful for specific farming methods that require precise
stat control. Diminished value with modern hybrid builds where locking health vs. attack is
less critical than it once was. Situational; do not prioritize.

---

### Damage (GC)

**Super Tower** — best damage mastery when near-maxed. See Top 6 above.

**Damage** — linear ×4 at max; best early-level ROI of any damage mastery. See Top 6 above.

**Demon Mode** — slightly better than Damage in raw numbers at full investment, but requires
full setup (Recharge Demon Mode lab + vault automation unlock) to realize farming value.
Without those, it is worse than Damage for most players. Unlock after full setup is in place.

**Ultimate Crit** — doubles effective UW crit chance (3% base → ~6.3% at max), multiplying
UW damage output by roughly ×2. Secondary benefit: reduces damage spikes and run variance —
a boss that you need to crit twice to kill on a 3% chance is much more reliably handled at
6%. Recommended after the top damage masteries are established.

**Energy Net** — underrated tournament mastery. Mastery applies ×2→×20 damage multiplier to
bosses while trapped in the net AND for 10 seconds after the trap ends (14.3s total window at
max net duration). Highly effective against battle conditions that boost or speed bosses.
Releases pressure on the wall (bosses are the first thing that destroys walls in tournament,
making everything else more dangerous). Has less value in farming where bosses aren't the
bottleneck.

**Berserker** — ×500 damage cap for 30–300s after Death Defy triggers. Strong on paper, but
Death Defy activation is heavily nerfed in tournaments, making this unreliable for competitive
play. More of a farming card mastery — if farming with 40% Death Defy chance it can activate
regularly. Currently considered the lowest value of the damage masteries for most active builds.

**Attack Speed** — +3% per level, +30% at max. More shots means more CL procs, more knockback,
more workshop ability triggers. Decent all-rounder but not top priority.

**Range** — multiplies damage-per-meter bonus up to ×3 at max. **Strong synergy with
Spotlight+**: SL+ scales with damage-per-meter, so range mastery amplifies SL+ significantly
more than the raw number suggests. If SL+ is unlocked, prefer Range over Attack Speed.

**Plasma Cannon** — mastery applies a fraction of Plasma Cannon's damage to elites (5%→50%).
Not recommended — CL Scatter research handles scatters, Slow Aura / Nuke manage rays, and
Swamp Rend handles vampires. The mastery adds little on top of existing elite management tools.

**Critical Chance** — increases crit chance, super crit chance, and super crit factor. Least
damage value per stone of the damage masteries. Not a priority.

**Death Ray** — mastery makes Death Ray partially pierce protector shields (5%→50% damage
through shield). Not recommended — Death Ray is frequently nerfed in tournament battle
conditions, and protectors are less of a bottleneck than other threats at late game.

---

### EHP

**Health** — ×1.2→×3.0 HP multiplier at max (300% additional health, tripling the pool).
Primary use: surviving overchargers in Legend tournament above wave ~900, where overchargers
can chain-shot through shield layers. Best EHP mastery for tournament survivability.

**Extra Defense** — +0.7% defense per level, +7% at max. Useful when approaching the defense
cap via relics and module sub-stats — pushes players from ~80–85% into a more comfortable
range. Less impactful once the 98% hard cap is reachable through other means.

**Health Regen** — good for wall-based builds where sustained regen keeps the wall alive.
Less useful for GC tournament builds where the wall is typically destroyed by ranged shots
rather than sustained overcharger pressure. Do not prioritize over Health for tournament.

**Fortress** — reduces wall rebuild time (−10s→−100s at max). Useful against the Saboteur
enemy, which destroys walls when CF is down. There is a minimum wall rebuild time (believed
to be ~37s, not confirmed). Situational — relevant once wall rebuild time is already reduced
to a range where Fortress pushes it meaningfully lower.

**Second Wind** — increases HP regen for 400 waves after Second Wind triggers. Generally the
weakest EHP mastery for tournament. If stacking regen for EHP farming, it pairs with Health
Regen, but Health Regen mastery alone is typically better value.

---

### Control

Generally the lowest priority category. Only invest here after establishing the top 6 and
your primary damage or economy masteries.

**Slow Aura** — reduces enemy attack speed (not movement speed). Primarily counters Rays,
which shoot faster at higher tiers and are harder to kill before they fire. Does not affect
Fleet enemies (immune to attack speed reduction). Preferred over Nuke mastery because it
provides continuous passive attack speed reduction without depending on Nuke activation
timing.

**Nuke** — same effect as Slow Aura mastery (reduces enemy attack speed after Nuke use).
Prefer Slow Aura unless you already run Nuke automation via the vault and have Recharge Nuke
researched, in which case the masteries become comparable. Fleets are immune.

**Land Mine Stun** — 2.5%→25% chance that a stunned enemy misses an attack on your tower.
Situational: potentially useful in a strong EHP build farming at very high waves (e.g., T18)
where reducing incoming damage matters. Not worth the stones for GC builds. 25% miss chance
is unreliable as a primary survival mechanism.

**Energy Shield mastery** — blasts nearby enemies back on shield activation, destroys
projectiles, resets Ray charge times. Considered one of the weakest mastery unlocks in the
current meta. Very situational; do not prioritize.

---

## Using the tower-analyzer MCP

| Tool | When to call |
|------|-------------|
| `get_cards_state` (sections: `["cards"]`) | Verify which masteries are unlocked and which cards are maxed before recommending unlock order |
| `get_currencies` | Check stone balance before recommending mastery unlock (stone cost varies per mastery) |
| `get_lab_costs` | Retrieve mastery research coin costs per level |
| `get_lab_slots` | Check whether mastery research slots are active or idle |

Always verify which masteries are already unlocked before giving a priority recommendation.
A player who already has Recovery Package and Wave Accelerator needs different advice than one
starting from scratch.
