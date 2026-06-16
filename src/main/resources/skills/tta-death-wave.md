---
name: tower-death-wave
description: >
  Expert knowledge on the Death Wave (DW) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Death Wave — including how it works, Kill Wall,
  stone investment priority, cooldown sync with Golden Tower and Black Hole, DW labs,
  Core module sub-stats for DW, or how DW fits into farming and milestone builds.
  Also trigger for questions about DW cooldown reduction strategy or syncing DW with GT and BH.
---

# Death Wave (DW)

## What It Does

Sends [Quantity] effect waves then unleashes a Death Wave dealing [Damage] × Tower Damage
(scales with all Crit) every [Cooldown] seconds.

**UW+ — Kill Wall**: On DW activation, sends a wall that multiplies kill counts (x3 → x108 at max).
Each "kill" from Kill Wall counts for orb hit chances and coin/cell rewards — making Kill Wall
the primary reason DW is a farming-critical UW.

Kill Wall and all UW+ abilities require owning all 9 Ultimate Weapons before they can be unlocked.

---

## Enhancements

**Labs** that amplify Death Wave:
- **Death Wave Health** — on kill/mark, increases Tower Max Health (max 12.5× base)
- **Death Wave Coin Bonus** — on kill/hit, grants bonus coins
- **Death Wave Cells Bonus** — on kill/hit, grants bonus Elite Cells (max 3×)
- **Death Wave Damage Amplifier** — on hit, amplifies DW damage taken (max +50×/wave)
- **Death Wave Armor Stripping** — on hit, reduces enemy armor (max 10 stacks)

**Core module sub-stats** that enhance DW: Damage, Quantity, Cooldown.
See `references/tta-module-substats.md` for values by rarity.

---

## Stone Investment Priority

**Quantity** — each additional effect wave tags more elites, which increases Elite Cell drops
(your lab speed boost fuel) and tags more enemies for the Death Wave Health lab bonus
(+0.05% Tower Max Health per enemy tagged, up to 12.5× base when lab is maxed). Upgrade freely,
no sync implications.

**Damage** — a stronger wall kills more enemies, which grows the bonus health pool faster via the
same +0.05% per kill mechanic. Upgrade freely, no sync implications.

**Kill Wall (UW+)** — requires owning all 9 UWs first. Each Kill Wall "kill" counts for orb hit
chances and coin/cell rewards, making it the primary driver of farming income. Prioritize if
maximizing coins per hour or cells per hour is the player's primary goal.

**Cooldown — do not upgrade in isolation.** See Cooldown Sync Strategy below.

Use `get_tower_state` to see current DW levels, stat values, and stones-to-max for each stat
before making investment recommendations.

---

## Cooldown Sync Strategy

DW, GT, and BH each provide a coin multiplier bonus on activation. When they fire simultaneously
those multipliers stack multiplicatively — which is the core reason sync matters for farming income.

Each cooldown upgrade reduces cooldown by 10s per level for all three UWs:

| UW | Base | Max | Levels | Reduction/level |
|----|:----:|:---:|:------:|:---------------:|
| Death Wave | 300s | 50s | 25 | -10s |
| Golden Tower | 300s | 100s | 20 | -10s |
| Black Hole | 200s | 50s | 15 | -10s |

**Sync points:**
- **300s (base)** — DW and GT sync naturally. BH is at 200s and out of phase.
- **200s** — first triple sync point. Requires 10 levels of cooldown on DW and GT to match BH's base.
- **100s** — triple sync. GT is floored here (max 20 levels). Requires 20 levels on DW, 20 on GT, 10 on BH.
- **Below 100s** — GT cannot follow. Only DW + BH remain in sync; GT fires every other DW/BH cycle.
- **50s (max)** — DW + BH fire every 50s; GT fires every 100s. All three align every 100s.

**The sub-100s decision is non-trivial.** At an intermediate cooldown like 90s/90s/100s, DW and BH
drift in and out of sync with GT throughout the run — the multiplicative coin stacking becomes
inconsistent. Three options to consider:

1. **Save and jump**: accumulate enough stones to reduce DW and BH from 100s to 50s in a single
   upgrade session, skipping the drift zone entirely and landing immediately at the stable 50s/50s/100s
   end state. Best for players who prioritize sync integrity and can afford to wait.

2. **Accept the drift**: reduce cooldown gradually even through the out-of-sync zone because reaching
   permanent Black Hole (BH cooldown low enough to proc continuously) is more valuable than
   maintaining sync during the transition. The time cost of waiting for stones may outweigh the
   sync benefit.

3. **Split sync**: take one of DW or BH to 50s while leaving the other at 100s. This preserves a
   1:1 sync between GT and the 100s UW, and a 1:2 relationship between those two and the 50s UW.
   A stable partial sync rather than full drift.

When advising on cooldown investment, always check GT and BH cooldown levels via `get_tower_state`
first. Establish the player's priority (CPH, permanent BH, or sync integrity) before recommending
a cooldown target — the right answer depends on what they value most.
