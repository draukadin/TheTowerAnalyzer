---
name: tower-inner-land-mines
description: >
  Expert knowledge on the Inner Land Mines (ILM) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Inner Land Mines — including how mines are placed,
  rotation mechanics, Charged Mines (ILM+), Chrono Jump lab, blast radius, stun mechanics,
  Space Displacer and Magnetic Hook module interactions, stone investment priority, or how ILM
  fits into CC chains with CF and PS.
---

# Inner Land Mines (ILM)

## What It Does

Places mines around the tower that detonate on enemy contact, dealing high burst damage and
optionally stunning nearby enemies. Mines are placed fresh each cooldown cycle. Undetonated
mines persist across cooldown resets and continue accumulating charge — they are not replaced
until the new set spawns.

**UW+ — Charged Mines**: Each mine independently accumulates a damage charge every second.
Charge is not reset until that specific mine detonates. Other mines keep their individual charge
when one mine fires. Mines that survive multiple cooldown cycles without detonating can reach
enormous charge multipliers. Requires all 9 UWs to be purchased.

---

## Stats

| Stat | Base | Max | Levels |
|------|:----:|:---:|:------:|
| Damage | ×10 | ×3,021 | 32 |
| Quantity | 3 | 6 | 4 |
| Cooldown | 200s | 50s | 17 |

---

## Mine Placement

**Base configuration**: 5 mines spawn in a fixed pentagon formation around the tower. They do
not move until the Rotation Speed lab is researched.

**With Rotation Speed lab**: Mines rotate continuously around the tower, bringing them into
contact with enemies on any part of the path rather than only enemies that walk directly through
a fixed mine position.

**Perk — Extra Mine Layer**: Adds 10 additional mines, 2 branching off each of the original
5 mines at 30° to either side, creating a denser inner ring.

**Space Displacer (SD) module mines**: SD's CC ability causes a portion of workshop landmines
to spawn as Inner Land Mines, forming a full circle around the tower just inside maximum tower
range. These are a separate pool from UW mines with their own max quantity — they do not count
against the UW Quantity stat. Both sets can be active simultaneously, giving overlapping coverage
at different distances from the tower.

---

## Labs

**Inner Mine Blast Radius** (20 levels, +0.10/level, max +2.00): Increases the explosion radius
when a mine detonates. Larger radius hits more enemies per detonation and increases the
effective coverage of each mine's position.

**Inner Mine Rotation Speed** (20 levels, +0.80/level, max +16.00): Makes mines rotate around
the tower. Without this lab, mines are fixed and only detonate on direct contact. With it, mines
sweep the path continuously — effectively turning ILM from a passive trap into an active AoE.
This is the most impactful lab for ILM's general utility.

**Inner Mine Stun** (1 level unlock): Mine explosions stun nearby enemies for 2.5 seconds.
The same stun decay mechanic that applies to PS stun applies here — each second an enemy is
stunned makes its next stun 0.5% shorter (per enemy, multiplicative).

**Inner Land Mine — Chrono Jump** (10 levels, +5s charge/level, max +50s per interaction,
requires ILM+): When an enemy is hit by an ILM, the charge on that mine is boosted by
(lab level in seconds) × (number of times this enemy has previously hit an ILM). This directly
amplifies Charged Mines when enemies make repeated contact with mines — which CF+ orbital
rotation and shockwave knockback enable by pushing enemies back into mine paths.

---

## Charged Mines (ILM+)

Each mine accumulates a damage multiplier every second it remains undetonated. Key properties:

- **Per-mine charge**: Each mine tracks its own charge independently. Detonating one mine does
  not reset any other mine's charge.
- **No reset on cooldown**: When the cooldown expires and new mines are placed, any surviving
  undetonated mines from the previous cycle keep their accumulated charge.
- **No charge cap**: Charge accumulates indefinitely until detonation.

The implication: mines that survive without being triggered become progressively more powerful
over time. A mine that has charged for 200s at a high charge rate deals vastly more damage than
a freshly placed mine. Situations that keep enemies from reaching certain mines (e.g., enemies
being clustered to one side by BH or stunned in place by PS) allow those untouched mines to
charge up for devastating detonations when enemies eventually reach them.

**Chrono Jump amplification**: With the Chrono Jump lab, each subsequent time an enemy contacts
a mine, that mine receives a large charge boost multiplied by the contact count. CF+ rotating
enemies through the mine field repeatedly compounds this rapidly.

---

## Module Synergies

**Space Displacer (SD)**: Adds a full circle of ILMs at tower range (separate from UW mines).
With CF slow, enemies spend more time in the mine field, increasing detonation chances and
allowing Chrono Jump to compound on repeated contacts.

**Magnetic Hook (MH)**: Fires ILMs directly at Bosses as they enter tower range, and at 25%
of Elites. CF slow increases the time available for the fired mine to travel to the target and
detonate before the enemy reaches the wall. See `tta-chrono-field.md` for full context.

---

## Practical Use and Investment Ceiling

**ILM's primary role is stunning bosses in tournaments**, not sustained DPS. It works well
alongside Space Displacer (SD) mines for coverage. Don't over-invest — ILM does not need to be
maxed to fulfill this role.

**Cooldown target: ~120–150s** is sufficient for most use cases. The goal is to have mines
reset in time for boss waves in Legends or higher-level tournaments. Reducing below 120s
provides diminishing returns for this purpose; the remaining stone cost to reach 50s max is not
worth it for most players.

## Stone Investment Priority

**Quantity** — only 4 levels (3 → 6 mines). Max it early; cheap relative to other investments.

**Cooldown** — bring down to ~120–150s to align with boss wave intervals. Do not prioritize
pushing to max (50s) — the practical benefit plateaus around 120s. The Galaxy Compressor (GC)
Generator module reduces all UW cooldowns (except PS) per recovery package collected, which
can help reach this target without additional stone investment.

**Rotation Speed lab** — enables mines to sweep the path rather than sitting fixed. Important
for increasing detonation frequency and SD mine synergy.

**Blast Radius lab** — larger explosion radius increases stun coverage per detonation, which is
the primary tournament value.

**ILM+ (Charged Mines)** and **Chrono Jump** — high-ceiling endgame investment. Only relevant
once the foundational setup (Quantity, CD target, labs) is complete and you are actively
pursuing ILM as a damage source rather than just a stun tool.

**Damage** — lower priority; the stun is the primary value in most builds, not raw damage.

Use `get_tower_state` for current ILM levels before making recommendations.
