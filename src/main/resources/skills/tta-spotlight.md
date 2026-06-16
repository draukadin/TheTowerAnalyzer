---
name: tower-spotlight
description: >
  Expert knowledge on the Spotlight (SL) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Spotlight — including how the rotating beam works,
  angle vs. quantity investment priority, Spotlight Missiles lab, Light Range (SL+) scaling,
  BH synergy, coin bonus mechanics, or stone efficiency comparisons between angle and quantity.
---

# Spotlight (SL)

## What It Does

Projects one or more focused damage-multiplier cones that rotate continuously around the tower.
Any enemy inside a cone takes a multiplied version of all damage dealt to it — SL does not deal
damage directly, it amplifies damage from other sources. The beam sweeps 360° passively with no
activation cooldown.

**UW+ — Light Range**: Boosts SL's damage multiplier by a value derived from your tower's
damage-per-meter stat. Scales with all sources of range — workshop Tower Range, Range lab,
module sub-stats, and relics. Players with heavy range investment get significantly more value
from Light Range. Requires all 9 UWs to be purchased.

---

## Stats

| Stat | Base | Max | Levels |
|------|:----:|:---:|:------:|
| Multiplier | ×8.0 | ×43.0 | 26 |
| Angle | 30° | 90° | 61 |
| Quantity | ×1 | ×4 | 4 |

**Angle has 61 levels — the longest single-stat upgrade track in the game. Budget stones
accordingly and set realistic intermediate targets rather than planning to max it quickly.**

---

## Labs

**Spotlight Missiles** (18 levels, milestone unlock): After a delay, the tower fires missiles
from the tower. Missile damage uses Smart Missiles (SM) damage stat if SM is unlocked; otherwise
uses a base of 14× damage. The in-game display shows missiles fire every 20 seconds, but the
actual rate is every 19 seconds once the first level is unlocked. Higher lab levels increase
missile frequency.

**Spotlight Coin Bonus**: Enemies killed while inside a SL cone earn bonus coins. Enemies do not
need to be killed by Spotlight itself — just present inside the cone when they die. This
interacts directly with BH and GT: enemies clustered in BH and killed inside the SL sweep earn the
BH Coin Bonus, GT Coin Bonus while GT is active and the SL Coin Bonus simultaneously.

---

## Coverage: Angle × Quantity

SL's effective coverage is the product of Angle and Quantity. Understanding how they interact
is essential for efficient stone spending.

**How multiple beams are spaced:**

| Quantity | Total Coverage (at 35°/beam) |
|:--------:|:----------------------------:|
| 1 | 35° |
| 2 | 70° |
| 3 | 105° |
| 4 | 140° |

With 2 beams, one always points opposite the other. With 3, the beams are arranged at top, left,
and bottom — not equally at 120° — leaving one orthogonal direction uncovered. With 4, beams are spaced
90° apart.

**The efficiency multiplier**: Each Angle level you buy increases coverage on all beams. With
2 beams, buying 1° of Angle gives 2° of effective coverage. With 4 beams, it gives 4°. This
makes Angle upgrades increasingly stone-efficient the more beams you have.

The flip side: buying a new beam (Quantity) multiplies your current Angle's effective coverage
in one purchase. Whether that's more stone-efficient than buying Angle levels depends on the
stone costs at each level. Use `get_sl_coverage_efficiency` (planned) to compare coverage-per-stone
for the next Quantity level vs. the next Angle level at your current investment.

**Short-term Angle target: 40–45°.** This gives the beam enough sweep width that, when BH
clusters enemies, the rotating cone is reliably covering the cluster for meaningful dwell time.
Below 35° the window of overlap with a BH cluster is narrow enough to miss frequently.

---

## Stone Investment Priority

**Angle** — the primary stat. Wider cone means more time over any given enemy cluster per
rotation, and more enemies caught simultaneously. The 61-level track means Angle competes for
stones across the entire game. Set intermediate targets (40–45° first) rather than treating it
as an all-or-nothing investment.

**Quantity** — evaluate based on coverage-per-stone. Because each additional beam multiplies the
effective value of all future Angle upgrades, buying Quantity early can dramatically improve
stone efficiency over the long run. The first Quantity purchase (1→2 beams) doubles effective
coverage at the cost of one level. If that cost is lower than buying equivalent Angle levels,
Quantity wins.

**Multiplier** — direct amplification of all damage dealt to enemies in the cone. Solid value
but lower priority than Angle since coverage determines whether the multiplier applies at all.

**Light Range (SL+)** — priority scales with total range investment. Players with high workshop
Tower Range, Range lab levels, and range-boosting module sub-stats or relics get much more value
per SL+ level than early-game players. Check range sources via `get_tower_state` and
`get_workshop_state` before assessing SL+ priority.

Use `get_tower_state` for current SL levels and stones-to-max before making investment
recommendations.

---

## Module Synergies

**Om Chip (OC)** — Core module that causes Spotlight to snap instantly to a Boss when one
enters the field, rather than waiting for the rotating beam to sweep to it. The Boss also
reflects the light to nearby enemies, increasing damage dealt to them by ×2 (Epic) → ×15
(Ancestral).

OC effectively gives SL 100% uptime on Bosses regardless of current beam angle — eliminating
the coverage lottery for the highest-priority target. The reflected light bonus additionally
amplifies damage to all enemies near the Boss. SL is a supporting UW rather than a primary
damage source, but OC significantly increases its contribution against Bosses in any build that
runs SL. See `references/tta-natural-epics.md` for full scaling values.

---

## BH Synergy

BH + SL is the primary damage combo for the economy trio builds and beyond:

1. BH clusters enemies into a tight group on the path
2. SL's rotating cone sweeps over the cluster, applying the damage multiplier to all enemies
   in the group simultaneously
3. Enemies killed in the SL cone trigger SL Coin Bonus; enemies in BH trigger BH Coin Bonus;
   GT multiplies coin income while active — all three stack simultaneously for multiplicative
   coin output. DW kills also stack: enemies killed by DW while inside a SL cone receive the
   SL Coin Bonus on top of DW's own Kill Wall and coin bonuses.

Wider SL Angle increases dwell time over the BH cluster per rotation, and Quantity increases
how often a beam sweeps through the cluster position. Both dimensions improve the reliability
of the BH+SL overlap.
