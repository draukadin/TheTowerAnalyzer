---
name: tower-golden-tower
description: >
  Expert knowledge on the Golden Tower (GT) Ultimate Weapon in The Tower: Idle Tower Defense.
  Use this skill whenever the user asks about Golden Tower — including how it works, GT labs,
  Golden Combo mechanics, generator module sub-stats for GT, permanent Golden Tower, stone
  investment priority, activation timing, or how GT fits into farming builds.
  Also trigger for questions about GT income, GT+ level value, or GT duration vs. cooldown tradeoffs.
  Note: GT has no effect in tournaments — redirect tournament questions to other UWs.
---

# Golden Tower (GT)

## What It Does

Turns the tower golden for a period of time. While active, all cash and coin income from enemy
kills is multiplied by GT's bonus multiplier. After the GT window ends, Golden Combo (GT+) applies
an additional bonus to kills made during the window.

---

## Labs

**Golden Tower Bonus** (25 levels, +0.15 per level): Increases the GT coin/cash multiplier
by up to +3.75 total. Each lab level directly scales all income earned during every GT window.

**Golden Tower Duration** (20 levels, +1s per level): Increases GT duration by up to +20s total.
Duration is the most important lab investment — see the Duration section below.

---

## Stat Investment Priority

**Duration** — becomes the highest-ROI stat once Golden Combo (GT+) is unlocked. Before GT+ is
available, Duration investment has diminishing returns and should be deprioritized. After GT+ is
unlocked, each additional second of duration is worth more than the last because Golden Combo
scales exponentially with kills. Use `get_gt_income_projection` to compare duration investment
at your current GT+ level before spending stones.

**Duration sync with BH**: There is a perk that adds +12s to Black Hole duration. If BH duration
is set to GT duration − 12s before that perk unlocks, the two will match once the perk is
researched — keeping GT and BH active for the same duration simultaneously and maximizing the
time both bonuses stack multiplicatively. Target BH duration = GT duration − 12s as an
intermediate milestone.

**Cooldown** — floors at 100s (max 20 levels, -10s per level from 300s base). Reduces time
between GT windows. Do not upgrade GT cooldown in isolation — GT is part of the DW/BH sync
system. See `tta-death-wave.md` for full sync strategy.

**Multiplier** — solid but lower ROI than Duration early. Invest after Duration is at a
comfortable level.

**Golden Combo (GT+)** — requires all 9 UWs to be unlocked first. See Golden Combo section below.

Use `get_tower_state` for current GT levels and stones-to-max before making recommendations.

---

## Generator Module Sub-Stats

Generator module sub-stats can increase GT's multiplier, duration, and reduce its cooldown.
With sufficient generator sub-stat investment, **permanent Golden Tower is achievable** — GT's
duration exceeds or matches its cooldown, keeping the tower in the golden state continuously.
When advising on generator module rerolls for farming-focused players, GT duration and cooldown
sub-stats are high-value targets.

---

## Golden Combo (GT+)

After each GT activation ends, Golden Combo pays out a bonus multiplier on cash and coins for
each enemy killed during that GT window:

```
Bonus multiplier = ((1 + 0.0003 × (Level + 1))^Kills) - 1
```

**Critical rule**: Golden Combo only counts enemies that **spawned during the GT activation**.
Enemies already on screen when GT activates are excluded from the kill count. This means the
payout depends heavily on how many new enemies spawn and are killed within the window.

**Implication for activation timing**: There is value in letting the previous wave clear before
GT activates, so the GT window opens into a fresh wave of spawning enemies rather than
already-present ones.

**DW Kill Wall synergy**: Kill Wall credits count as kills for Golden Combo purposes. Firing DW
immediately after GT ends floods the post-window kill count and generates large Golden Combo
payouts. This is the primary GT+/DW+/Kill Wall farming combo.

---

## Why Duration Is Exponentially Valuable

The Golden Combo formula is exponential in kills, and kills scale linearly with duration.
This means:

- Doubling duration more than doubles income (at meaningful GT+ levels)
- Each additional second of duration is worth more than the previous one as GT+ level increases
- At GT+10, 80s duration generates roughly 231% more income than 60s duration

The marginal value of +1 duration increases with both GT+ level and current duration. This is
why Duration investment should be prioritized over Multiplier upgrades at most stages, and why
the Duration lab (up to +20s) is high priority.

For precise projections at your GT+ level and duration, use `get_gt_income_projection`.

---

## Activation Timing

1. Let the current wave clear or thin out first — Golden Combo only counts enemies that spawn
   during the window, so opening GT into a fresh spawn maximizes kill count.
2. Activate GT first to open the income multiplier window.
3. Activate DW inside the GT window so DW's Kill Wall kill credits earn the GT multiplier.
4. Activate BH inside the GT window to cluster enemies for DW and maximize kills per second.
5. After GT ends, if DW is available, fire it immediately to flood Golden Combo with Kill Wall
   kill credits.

---

## Tournament

**GT generally has no effect in tournaments** — tournament scoring is based on waves cleared,
not coins earned, and GT's multiplier only applies to cash and coin income.

**One niche exception**: GT's cash bonus increases the digit count of your current cash, which
directly amplifies the Project Funding (PF) generator module. PF provides a massive damage
multiplier with full uptime when cash digit count is high. In a PF-based tournament build, GT
activation can meaningfully increase damage output by pushing the cash total to a higher digit
count. This is a niche interaction rather than a general-purpose tournament investment.

---

## Sync With DW and BH

GT's cooldown floors at 100s (cannot be reduced further). For full sync strategy with DW and BH,
see `tta-death-wave.md`. The short version: GT fires every other DW/BH cycle at the 50s/50s/100s
end-state, and all three align every 100s at max investment.

---

## Module Synergies

**Multiverse Nexus (MVN)** — Core module that automatically synchronizes DW, GT, and BH,
replacing manual sync entirely. The shared cooldown is the average of the three UW cooldowns
adjusted by the module's rarity offset (Epic +20s penalty → Ancestral −10s net reduction). With
MVN, all three fire together every activation without any cooldown alignment investment. See
`tta-death-wave.md` for the full rarity table.

**Galaxy Compressor (GC)** — Generator module that reduces all UW cooldowns (except PS) each
time a recovery package is collected (10s at Epic → 20s at Ancestral). In farming runs with
frequent packages, GC meaningfully increases GT activation frequency — supplementing stone CD
investment and increasing income windows per run.
