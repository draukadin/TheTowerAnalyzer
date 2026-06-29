---
name: tower-poison-swamp
description: >
  Expert knowledge on the Poison Swamp (PS) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Poison Swamp — including how it works, PS labs,
  damage ramp mechanics, Swamp Rend, stun mechanics and decay, perma-PS strategy, CF+ dependency,
  Death Creep (PS+), Harmony Conductor synergy, farming vs. tournament use, or stone investment.
  Also trigger for questions about whether PS is ready to use or how to prioritize its labs.
---

# Poison Swamp (PS)

## What It Does

Creates a toxic area around the tower that deals escalating damage-over-time to all enemies that
enter it. Once an enemy is poisoned, it continues taking damage even after it leaves the swamp.
All poisoned enemies receive a permanent 25% movement speed slow — no labs required for this.

PS is a dual-threat UW: it is simultaneously one of the best CC sources and one of the highest
damage ceilings in the game. However, it is **far less stone-intensive and far more lab-intensive
than any other UW**. Budget months of lab slots, not just coins.

**Placement note**: PS spawns off-center from the wall. One side barely extends past the wall;
the other side extends further based on the Swamp Size lab. PS does not have full 360° coverage
of the path — CF+ is required to rotate enemies into PS for reliable full coverage.

---

## Stats

| Stat | Base | Max | Levels |
|------|:----:|:---:|:------:|
| Damage | ×10 | ×3,021 | 32 |
| Duration | 30s | 100s | 15 |
| Cooldown | 125s | 50s | 17 |

PS has the shortest base cooldown (125s) of any area UW. Getting to perma-PS requires ~1,768
stones with no Core sub-stats; ~2,198 stones provides resilience against UW Duration battle
conditions.

---

## Labs

Lab investment for PS is the heaviest in the game. Each of the following labs rivals or exceeds
the length of the CF Duration lab (the prior benchmark for longest labs):

**Swamp Size** (30 levels, +0.04/level, max +1.2): Extends PS radius. Controls how far out
from the wall enemies can be hit. Larger size increases overlap with CF pull area and increases
the chance enemies rotating via CF+ will pass through PS.

**Poison Swamp Stun** (1 level unlock): Enables stun on entry into PS. Starts at 5% chance,
1s duration. Unlocks Swamp Stun Chance and Swamp Stun Time. Enemies can only be stunned once
per entry into a swamp, but can be re-stunned on re-entry into the same swamp or on entering a
different PS.

**Swamp Stun Chance** (30 levels, +2.5%/level): Starts at 7.5% (level 1), max 80%. Requires
Poison Swamp Stun unlock. Same lab length as CF Duration.

**Swamp Stun Time** (30 levels, +0.3s/level): Starts at 1.3s (level 1), max 10s. Requires
Poison Swamp Stun unlock. Same lab length as CF Duration.

**Swamp Rend - Basic Enemies** (30 levels, +3%/level, max 90%, unlock: T16 W50 milestone):
When basic enemies are hurt by PS, damage is increased by a percent of currently applied rend.
At level 17 (51%) with Rend Max at 20, each tick gets a ×10 damage multiplier. See Swamp Rend
section. Approximately twice as long as CF Duration lab.

**Swamp Rend - Additional Enemies** (6 levels, unlock: T16 W50 milestone): Expands Swamp Rend
to additional enemy types:

| Level | Enemy Type Added |
|:-----:|:----------------|
| 1 | Ranged |
| 2 | Fast |
| 3 | Tank |
| 4 | Protector |
| 5 | Boss |
| 6 | Vampire |

Fleets, Rays, and Scatters are not affected by Swamp Rend at any level.

---

## Damage Ramp Mechanic

PS deals damage every game second. The critical mechanic: **each tick does more damage than
the last**, ramping up as long as the enemy remains poisoned.

Base ramp (no PS+):

| Tick | Multiplier | Cumulative (×base) |
|:----:|:----------:|:------------------:|
| 1 | 1.0× | 1.0× |
| 2 | 1.5× | 2.5× |
| 3 | 3.0× | 5.5× |
| 4 | 4.5× | 10.0× |
| 10 | — | ~68.5× |
| 20 | — | ~286× |

Note: there is a documented quirk on tick 2 — it ramps to 1.5× rather than the expected 2.5×
before resuming the pattern. This is a known behavior.

The implication: **keeping enemies in or near PS longer is exponentially more valuable than
dealing more raw tooltip damage.** Any CC that delays enemies — stun, CF slow, CF+ orbit,
shockwave — directly multiplies PS's effective output.

---

## PS+ — Death Creep

PS+ increases the per-tick damage ramp rate. At PS+0, it adds +70% per tick, bringing the
base 150%-per-tick ramp to 220% per tick:

| PS+ Level | Per-Tick Ramp |
|:---------:|:-------------:|
| Base (no PS+) | 150% |
| PS+0 | 220% |

At PS+0 the ramp sequence becomes: 100, 220, 440, 660, 880, 1100... — substantially faster
compounding than the base ramp. PS+ is not required to use PS effectively, but once raw damage
is high enough, PS+ levels become high ROI.

---

## Swamp Rend

Swamp Rend converts the enemy's current Rend stacks into a flat damage multiplier applied to
every PS tick. This requires:
1. Rend Armor (workshop) unlocked
2. Rend Max Multi lab (separate, fairly long) to increase max rend stacks
3. Swamp Rend - Basic Enemies lab (30 levels, T16 W50 unlock)
4. Swamp Rend - Additional Enemies lab levels for non-basic enemy types

**Formula**: `Rend multiplier = Swamp Rend % × current enemy Rend stacks`

Example: Rend Max at 20, Swamp Rend Basic at level 17 (51%) → ×10 multiplier per PS tick.
Combined with the base damage ramp, cumulative damage after 20 ticks becomes approximately
2,900× tooltip damage instead of 286×.

Enemies hit by basic tower projectiles accumulate rend stacks rapidly. Rend reaches max stacks
quickly in most encounters. Higher workshop Rend investment accelerates time-to-max-rend,
which is especially relevant in tournaments where early ramp matters.

**Chain Lightning interaction**: PS damage is not %-max-health damage, so it IS amplified by
Chain Lightning's Shock debuff — an additional hidden multiplier on top of all PS scaling.

---

## CC: Slow and Stun

**Slow**: All poisoned enemies permanently move at 75% speed (25% reduction). This applies
immediately on first PS contact and persists indefinitely.

**Stun**: On each entry into a PS, the enemy has a chance to be stunned. Stun chance and
duration scale with labs (max 80% chance, max 10s duration). Key rule: **one stun proc per
entry, but re-entry into the same swamp or a different swamp resets the eligibility**. This
makes repeated PS contact via CF+ orbit, shockwave knockback, or multiple simultaneous swamps
extremely valuable.

**Stun Decay (patch mechanic)**: Each second an enemy is stunned, the next stun it receives
is 0.5% shorter (multiplicative, per enemy). This is gradual — it does not hard-cap stuns —
but extended runs against the same enemy will slowly reduce stun duration over time.

**Shockwave synergy**: Shockwave can knock enemies out of PS while they are stunned. If
shockwave cooldown is reduced to 10s and max stun duration is 10s, enemies are repeatedly
knocked out and re-enter PS — triggering a new 80% stun chance each time. This creates a
near-permanent stun loop for enemies in range.

---

## Perma-PS and Multiple Swamps

Perma-PS is achieved when PS cooldown ≤ PS duration, keeping at least one swamp active at
all times. Stone requirements:
- **~1,768 stones**: 1× perma-PS (no Core sub-stats)
- **~2,198 stones (+430)**: resilience against UW Duration battle conditions

**Why run multiple PS?** Two reasons:
1. PS spawns off-center — one side barely clears the wall. Multiple PS cover different angles,
   increasing the percentage of enemies that pass through a swamp.
2. Multiple swamps can stun the same enemy independently — an enemy that passes through two
   separate swamps gets a new stun roll for each, bypassing the single-stun-per-entry limit.

With CF+, enemies rotate into whichever PS is in their orbital path, making multiple-swamp
coverage increasingly effective as CF+ level rises.

---

## CF+ Dependency

PS is not viable as a primary damage source without CF+. Because PS does not cover the full
path, enemies that approach from uncovered angles will not be poisoned — and even a 20% miss
rate is enough for unkilled enemies to reach the tower. CF+6 is cited as a minimum threshold
before PS can be relied upon as a main damage source.

The synergy compounds: CF+ slows enemies further (stacking with PS's 25% slow), which
increases how long they remain in the swamp and how many times they orbit through it. Higher
CF+ level = more stun procs per enemy = more damage ticks = exponentially higher cumulative
PS output.

---

## Harmony Conductor Module

The Harmony Conductor (HC) module adds a chance for poisoned enemies to **miss-attack** the
tower. Boss chance is halved compared to regular enemies. Combined with PS's permanent 25%
slow, poisoned enemies move slower and miss more often — reducing damage taken without relying
on wall HP.

---

## Farming vs. Tournament

**Farming**: Leave PS off until enemy health is approximately 6,000× your tooltip PS damage —
below that threshold PS kills enemies too fast rather than letting the damage ramp accumulate.
Also disable stun if PS size is large enough that enemies are stunned before reaching BH,
which would prevent BH from clustering them for DW and SL combos.

**Tournament**: Turn everything on. PS CC (slow + stun + HC miss chance) combined with
exponential damage ramp is tournament-viable across most conditions. Performance drops in fast
enemy speed battle conditions because CF+ cannot rotate enemies quickly enough to maintain
coverage — but PS remains strong in slow-speed tournaments and is at its best in GC farms and
milestone runs where enemy speeds are low.
