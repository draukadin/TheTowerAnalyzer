---
name: tower-chain-lightning
description: >
  Expert knowledge on the Chain Lightning (CL) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Chain Lightning — including how it works, Shock
  debuff mechanics, stone investment priority, Smite (CL+), Chain Thunder, Lightning Amplifier
  for Scatters, Shock's interaction with Poison Swamp, or CL's role as the default early-game
  damage source before PS or SM come online.
---
# Chain Lightning (CL)

## What It Does

Each tower projectile has a [Chance]% chance to fire a lightning bolt that chains between
[Quantity] random nearby enemies, dealing [Damage]× tower damage to each. Chains jump randomly
— there is no targeting priority and no apparent range limit. One proc fires one bolt; the bolt
then jumps from enemy to enemy [Quantity] times.

CL is the default primary damage source in most builds until Poison Swamp or Smart Missiles
come online. It provides passive coverage every time the tower fires, with no activation
cooldown or placement requirement.

**UW+ — Smite**: Each CL hit has a chance equal to the CL Chance stat to deal bonus damage
equal to a multiple of the current wave's HP. Can activate up to 100 times per enemy. Because
Smite's proc rate is tied directly to the Chance stat, Chance investment scales both bolt
frequency and Smite procs simultaneously. Requires all 9 UWs to be purchased.

---

## Stats


| Stat     | Base |   Max   | Levels |
| -------- | :--: | :-----: | :----: |
| Damage   | ×2 | ×7,961 |   32   |
| Quantity |  1  |    5    |   5   |
| Chance   | 5.0% |  27.5%  |   16   |

---

## Labs

**Chain Lightning Shock** (1 level unlock): Each CL hit has a chance to apply the Shock debuff.
Shocked enemies take increased damage from **all sources** — not just CL. Starts at 5% chance
and ×1.10 damage multiplier. Unlocks Shock Chance and Shock Multiplier.

**Shock Chance** (30 levels, +0.50%/level): Starts at 2.5% at level 1, reaches 17.5% at
max (level 30).

**Shock Multiplier** (14 levels, +0.04/level, max ×1.66): Increases the bonus damage
multiplier applied to all damage sources against shocked enemies.

**Chain Thunder** (30 levels, +3%/level, max 90%, unlock: T16 W60 milestone): Enemies lose
damage output by 10% for every 6% of their health they've lost from CL damage, up to the lab's
max reduction. At max (90%), an enemy that has taken sufficient CL damage deals 90% less damage
to the tower — a substantial defensive bonus in deep runs where CL hits enemies repeatedly.

**Lightning Amplifier — Scatter** (30 levels, +1.25×/level, max ×37.5×, unlock: T16 W60
milestone): Each consecutive CL hit on a Scatter enemy adds a stacking additive damage bonus.
Scatters have high health and are among the most dangerous enemies in deep runs; this lab
makes CL specifically lethal against them.

---

## Dimension Core (DC) Module

The Dimension Core natural epic dramatically amplifies CL's Shock mechanic:

- **60% chance** that CL hits the initial target (the enemy whose contact triggered the proc)
- **Doubles Shock chance and Shock multiplier**
- **Stacking Shock**: If Shock is applied again to the same enemy, the Shock multiplier stacks
  additively up to a max of ×5 (epic) / ×10 (legendary) / ×15 (mythic) / ×20 (ancestral)

DC is one of the highest-value natural epics for CL builds. Doubling Shock chance and
multiplier makes the Shock debuff apply more frequently and hit harder, which amplifies every
other damage source in the build. The stacking Shock multiplier enables massive damage
multipliers on enemies that get hit by multiple CL chains. See
`references/tta-natural-epics.md` for the full DC description.

---

## Shock: Global Damage Amplifier

Shock is CL's most strategically impactful effect. Because Shock increases damage from **all
sources**, it amplifies the output of every other damage-dealing UW and the tower itself while
active on an enemy:

- **Poison Swamp**: Every PS tick on a shocked enemy is multiplied by the Shock multiplier.
  At max Shock Multiplier (×1.66), PS's already-exponential damage ramp compounds further.
- **Death Wave, Smart Missiles, Inner Land Mines**: All benefit from the Shock multiplier on
  any shocked enemy they hit.

Prioritizing the Shock labs (unlock + Shock Chance + Shock Multiplier) is high value for any
build that uses PS as a damage source, since Shock turns CL into a passive damage amplifier
for the entire build.

---

## Stone Investment Priority

**Quantity and Chance** — the two primary stats; interleave upgrades by buying whichever is
cheaper at any given point. Quantity (1 → 5 bolts, 5 levels) multiplies total coverage and
Shock application rate per proc. Chance (5% → 27.5%, 16 levels) determines how often procs
occur per shot and, because Smite's proc rate equals the Chance stat, also scales Smite hits.
Together these two stats determine almost all of CL's value.

**Damage** — invest in only the first 4–5 levels where cost-per-gain is cheap. Do not
prioritize maxing Damage — Quantity and Chance deliver far better ROI.

**Smite (CL+)** — Scales with wave HP so it becomes more meaningful at high
waves, but the proc rate is low relative to the investment cost. Do not prioritize early.

**Core module sub-stats** can increase CL Quantity, Chance, and Damage. Check
`references/tta-module-substats.md` for sub-stat values by rarity before recommending stone
investment — module sub-stats may cover part of what stones would otherwise need to provide.

Use `get_tower_state` for current CL levels and stones-to-max before making recommendations.
