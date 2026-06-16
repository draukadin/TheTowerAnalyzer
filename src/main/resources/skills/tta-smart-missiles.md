---
name: tower-smart-missiles
description: >
  Expert knowledge on the Smart Missiles (SM) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Smart Missiles — including how it works, SM labs,
  Missile Amplifier stacking, Cover Fire (SM+) independent cooldown, Missile Barrage, Spotlight
  Missiles interaction, stone investment philosophy, or when SM becomes worth pursuing.
---

# Smart Missiles (SM)

## What It Does

Fires a volley of missiles at enemies, each dealing high single-target damage. Missiles home in
on targets and despawn if they don't connect within the despawn window. SM has no crowd control
effect and no economy (coin/cell) benefit — it is a pure damage UW.

**UW+ — Cover Fire**: Triggers an additional SM volley on an independent cooldown separate from
the main SM cooldown. At unlock the Cover Fire cooldown is 13s; at max investment it is 2s.
Cover Fire fires continuously alongside the main SM cycle, effectively acting as a second,
faster-firing missile salvo. Requires all 9 UWs to be purchased.

---

## Stats

| Stat | Base | Max | Levels |
|------|:----:|:---:|:------:|
| Damage | ×10 | ×3,021 | 31 |
| Quantity | 5 | 20 | 16 |
| Cooldown | 180s | 20s | 16 |

---

## Labs

**Missile Despawn Time** (20 levels, +1s/level): Increases how long missiles remain active
before despawning on a miss. More relevant in situations where enemies are moving erratically
(CF+ orbit, shockwave knockback) and missiles may miss their initial lock.

**Missile Amplifier** (25 levels, +1.5/level after first level, starts at 2.5, max 38.5):
Each missile that hits a target applies a stacking additive damage bonus to the next missile
hitting the same target. Multiple missiles from the same volley hitting the same target compound
rapidly. This is SM's primary damage-scaling mechanism against bosses and high-health elites
that survive long enough to take multiple hits from one volley.

**Missiles Explosion** (1 level unlock, starting radius 0.30): Missiles deal AoE damage on
impact. Unlocks Missile Radius.

**Missiles Radius** (20 levels, +0.05/level, max 1.30, requires Missile Explosion): Increases
AoE explosion radius. Converts SM from a pure single-target weapon into one that can splash
nearby grouped enemies.

**Missile Barrage** (1 level unlock): Once per run, manually activate a burst of 20 missiles
all fired simultaneously. Unlocks Missile Barrage Quantity.

**Missile Barrage Quantity** (6 levels, +5/level): Increases the Barrage missile count. Starts
at 25 missiles at level 1 (base 20 + 5), reaches 50 at level 6. Save Barrage for a high-value
boss wave.

**Recharge Missile Barrage** (7 levels, unlock: T14 W60 milestone): Allows Missile Barrage to
recharge after a set number of waves instead of being once-per-run. Reduces the recharge wave
count by 250 per level (150 for the last two levels):

| Level | Waves to Recharge |
|:-----:|:-----------------:|
| 1 | 1500 |
| 2 | 1250 |
| 3 | 1000 |
| 4 | 750 |
| 5 | 500 |
| 6 | 350 |
| 7 | 200 |

---

## Spotlight Missiles Interaction

The SL Spotlight Missiles lab fires missiles from the tower periodically. When SM is unlocked,
those missiles use SM's damage stat rather than the base 14× fallback. Investing in SM Damage
therefore directly buffs the Spotlight Missiles output — this is one of the few indirect reasons
to care about SM Damage even if you're not focusing on SM as a primary weapon.

---

## Investment Philosophy

**SM is a late-game endgame investment, not an early or mid-game priority.**

At full investment SM can shred bosses — Missile Amplifier stacks damage rapidly on single
targets taking multiple hits, and Cover Fire's 2s independent cooldown adds a near-constant
stream of volleys on top of the main cycle. At that point, SM is a legitimate boss-killing tool.

The problem is the cost to get there: **SM requires over 24,000 stones to fully max**, with no
CC, no economy benefit, and no synergy that helps the rest of the build. Earlier alternatives
give dramatically better ROI:

- **Chain Lightning (CL)** and **Poison Swamp (PS)** both provide better damage returns earlier
  for far fewer stones
- **Card masteries** have better stone ROI than pushing SM toward max
- **Perma-SM via GC module** is technically achievable but is effectively the last investment
  any player would make — better options exist at every stage before it

**When to start investing in SM**: Only after DW, GT, BH, CF, and SL are at comfortable levels
and you have stones to spare with no higher-priority target. SM is the UW to invest in last
among the non-trivial UWs.

Use `get_tower_state` for current SM levels and stones-to-max before making any SM investment
recommendation. Confirm the player has no higher-priority use for those stones first.
