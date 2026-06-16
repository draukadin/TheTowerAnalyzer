---
name: tower-black-hole
description: >
  Expert knowledge on the Black Hole (BH) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Black Hole — including how it works, BH labs,
  Consume mechanics, stone investment priority, perma-BH strategy, BH coverage with Extra Black
  Hole or Primordial Collapse module, cooldown sync with DW and GT, or how BH fits into farming
  and milestone builds.
---

# Black Hole (BH)

## What It Does

Spawns a Black Hole that pulls all nearby enemies toward the tower. Enemies caught in the BH
cannot leave until it deactivates or they die. The BH fires every [Cooldown] seconds, lasts for
[Duration] seconds, and has a pull radius of [Size] meters.

**UW+ — Consume**: At the end of each BH activation, deals a multiple of the current wave's HP
to every enemy that was affected during that activation. Enemies that are killed inside the BH
count as affected. Requires all 9 UWs to be purchased before it can be unlocked.

---

## Labs

**Black Hole Damage** (10 levels): BH deals damage equal to a % of each enemy's total HP per
second to every enemy inside the pull field. Protectors do not reduce this damage. Each second
an enemy stays in the BH it takes another tick — longer Duration means more ticks before Consume
fires, making Damage lab and Duration investment synergistic.

**Black Hole Coin Bonus**: Extra coins are earned for enemies killed within the BH area. Enemies
do not need to be killed by the BH itself, nor do they need to be affected by its pull — just
within its radius when killed.

**Extra Black Hole** (1 level unlock): A second BH spawns whenever BH is active, always on the
opposite side of the tower with identical stats. This increases path coverage but is not a factor
in achieving perma-BH — Extra BH and perma-BH are independent goals.

**Black Hole Disable Ranged Enemies** (1 level unlock, T14 W20): Ranged enemies inside BH cannot
fire while pulled in. Reduces incoming damage from ranged threats in deep runs.

---

## Stats

| Stat | Base | Max | Levels |
|------|:----:|:---:|:------:|
| Size | 30m | 70m | 21 |
| Duration | 15s | 38s | 24 |
| Cooldown | 200s | 50s | 15 |

BH base cooldown is 200s — already at the first triple-sync point; DW and GT need 10 cooldown
levels each to match it. See `tta-death-wave.md` for the full sync table.

---

## Stone Investment Priority

**Size** — larger pull radius captures more enemies on the path. More enemies affected means more
Damage lab ticks, more Consume targets, and more BH Coin Bonus procs. No sync implications.

**Size interacts with tower range**: BH only needs to be large enough to cover the tower's
effective attack range — enemies outside range won't be shot anyway. Players with lower range
(from fewer workshop or Range lab levels) can reach full coverage with fewer Size levels, freeing stones
for other stats. Use `get_tower_state` and `get_workshop_state` to check current range before
recommending a Size target.

**Duration** — each additional second gives the Damage lab one more tick on every held enemy, and
gives more time for enemies to enter the field and be tagged as "affected" before Consume fires.
After Consume (BH+) is unlocked, Duration provides compounding value — longer window, more enemies
affected, larger Consume payout.

**Duration sync target**: There is a perk that adds +12s to BH duration. Setting BH duration to
GT duration − 12s before that perk unlocks means the two will match once the perk is researched,
keeping BH and GT active for the same window simultaneously. See `tta-golden-tower.md`.

**Cooldown** — reduces time between activations; floors at 50s. Do not upgrade cooldown in
isolation — BH is part of the DW/GT sync system. See `tta-death-wave.md` for full sync strategy.

Use `get_tower_state` for current BH levels and stones-to-max before making recommendations.

---

## Consume (BH+)

Consume fires once at the **end** of each BH activation and hits every enemy that was affected
during that window. An enemy is considered affected if it entered the BH pull field at any point,
including enemies that were killed inside the BH before it ended. Damage scales with the current
wave's HP total — Consume hits harder in later waves.

**Duration amplifies Consume**: A longer window gives more time for enemies to enter the pull
radius and be tagged as affected. It also gives the Damage lab more time to weaken enemies before
Consume's final hit. Duration is the primary lever for amplifying Consume's effective output.

**Timing with DW**: The typical combo is BH first to cluster enemies, then DW inside the BH
window. Consume fires at the END of BH — plan timing so BH ends after DW has hit the cluster;
Consume then cleans up survivors. All three ideally fire within the GT window for multiplicative
coin stacking. See `tta-death-wave.md` for activation sequence details.

---

## Perma-BH

Perma-BH (pBH) is achieved when BH cooldown is reduced enough — via UW cooldown levels plus
Generator module cooldown sub-stats — that a new activation begins before or as the previous
one ends. When CD ≤ Duration, BH is essentially always active.

Benefits at perma-BH:
- Enemies spend most of the path inside BH instead of walking freely
- BH Damage ticks continuously
- Near-constant Consume procs as each window ends

**Extra BH lab is not required for perma-BH.** They solve different problems: Extra BH increases
simultaneous path coverage; perma-BH eliminates downtime between single-BH activations.

Generator module cooldown sub-stats can contribute significantly to CD reduction, making perma-BH
achievable before fully maxing the UW cooldown stat. Use `get_tower_state` to compare current CD
and Duration before recommending the path to pBH.

---

## Coverage: Extra BH and PC Module

The Extra Black Hole lab and Primordial Collapse (PC) module each add one additional simultaneous
Black Hole:

| Source | BHs Added | Notes |
|--------|:---------:|:------|
| BH UW | 1 | Base |
| Extra Black Hole lab | +1 | Spawns opposite side of tower |
| Primordial Collapse (PC) module | +1 | Third BH |
| **Total** | **3** | ~120° separation around the tower |

Three-BH coverage is especially effective on curved paths or in runs where enemy clustering across
the full path length is critical.

---

## Sync With DW and GT

BH's cooldown floors at 50s (15 levels). At max investment, DW and BH fire every 50s; GT fires
every 100s. All three align every 100s.

For full sync strategy, sub-100s trade-offs, and whether to save-and-jump vs. accept drift, see
`tta-death-wave.md`.
