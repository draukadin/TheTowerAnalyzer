---
name: tower-modules-buying
description: >
  Strategic guidance on buying modules from the gem store banner in The Tower: Idle Tower
  Defense. Covers the Standard Banner, Lucky Banner, pull rates, pity systems, the natural
  epic mechanic, what to shatter vs. keep, the Ancestral 5* duplicate protection rule, and
  how to budget gems for module pulls. Consulted by tta-modules.md. Use get_currencies to
  check gem balance before recommending pulls. Do NOT use for module leveling, merging, or
  sub-stat strategy — those are in the main tta-modules.md skill.
---

# Module Buying

## The Two Banners

### Standard Banner

Always available. Base pull rates:

| Rarity | Drop Rate |
|--------|-----------|
| Epic | 2.5% |
| Rare | 29.0% |
| Common | 68.5% |

### Lucky Banner

A limited-time banner that appears periodically with improved rates:

| Rarity | Standard | Lucky |
|--------|----------|-------|
| Epic | 2.5% | 4.0% |
| Rare | 29.0% | 32.0% |
| Common | 68.5% | 64.0% |

The Lucky Banner is the better pull option whenever it is active. The 4% epic rate is a
**60% relative improvement** over the 2.5% standard rate — significantly better expected
value per gem. Consider stockpiling gems and holding off on Standard Banner pulls in
anticipation of the Lucky Banner appearing, especially for players who are gem-conscious
or farming a specific epic. The tradeoff is delaying short-term progression while the
banner is unavailable — evaluate based on current gem balance and how urgently the target
module is needed.

Both banners share the same pricing: **20 gems (x1)** or **200 gems (x10)**. Use x10
pulls whenever possible — same rate per pull, fewer clicks.

---

## Module Pool Sizes

| Rarity | Pool | Per Type | Chance of Specific Module |
|--------|------|----------|--------------------------|
| Epic | 24 total (6 per type × 4 types) | 6 | 4.17% of epic drops |
| Rare | 16 total (4 per type × 4 types) | 4 | 6.25% of rare drops |
| Common | 8 total (2 per type × 4 types) | 2 | 12.5% of common drops |

The four module types are: **Armor**, **Cannon**, **Generator**, **Core**.

---

## Pity Systems

**Epic pity**: Guaranteed epic if 150 pulls pass with no epic. The pity counter resets
after each epic drop (natural or pity). Worst-case cost to guarantee any epic:
**150 pulls = 3,000 gems**.

**Rare pity**: Guaranteed at least one rare every 10 pulls. Fires automatically — you
will never go 10 consecutive pulls without a rare.

---

## What You're Actually Hunting: Natural Epics

Not all epics are equal. **Natural epics** — epics that dropped from pulls rather than
crafted through merging — carry a **unique special stat bonus** that merged copies do not
have. Examples:

- **+1 Black Hole** (a second simultaneous BH)
- **Double Chain Lightning shock chance**
- Other per-module unique bonuses depending on the specific epic

These bonuses are the primary reason to pull on the banner. A natural epic with its special
stat is meaningfully stronger than a merged copy of the same module.

**What this means for your strategy**: if your target module has a valuable natural epic
bonus, pulling for it is more efficient than merging your way to a copy — a merged epic
lacks the bonus entirely. Before sinking resources into the merge chain for a specific
module, check whether the natural epic bonus matters for your build.

---

## What to Do with Commons and Rares

**Commons**: Shatter immediately. They cannot be meaningfully merged upward and clutter
inventory. Enable auto-shatter if available.

**Rares**: Keep only as merging fodder. Their only value is in the merge chain that
produces the fodder needed to upgrade modules from Legendary+ to Mythic+. The merge math
is covered in `tta-modules.md`. Specific rare modules are interchangeable for this purpose.

---

## The Ancestral 5* Duplicate Protection Rule

Once you max a specific epic to **Ancestral 5\***, the game substantially lowers the drop
rate of that exact module on future pulls. The freed-up percentage redistributes to the
other epics you have not yet maxed.

**The catch — two-copy builds**: some modules are valuable in two different configurations.
For example, you may want one copy with sub-stats optimized for farming and a separate copy
with sub-stats optimized for tournament. As soon as the first copy reaches Ancestral 5\*,
the rate on that specific module contracts sharply — working against you when you need a
second copy to reroll independently. If you know you will need two copies of the same
module, plan for this before the first copy reaches 5\*.

**Long-term benefit**: across a full collection, the Ancestral 5\* rule concentrates odds
toward modules you still need. The more epics you have at 5\*, the better your effective
odds on each remaining target. The mechanic is a net positive for collection completion —
the two-copy scenario is the primary case where it works against you.

---

## Expected Costs

For **any epic** (not a specific module):
- Average: ~40 pulls (~800 gems on Standard; ~25 pulls / ~500 gems on Lucky Banner)
- Worst case (pity): 150 pulls = 3,000 gems

For a **specific epic** from a full pool (24 epics, none at Ancestral 5\*):
- Standard Banner: 2.5% × 4.17% ≈ 0.104% per pull → expected ~960 pulls (~19,200 gems)
- Lucky Banner: 4.0% × 4.17% ≈ 0.167% per pull → expected ~600 pulls (~12,000 gems)
- Pity guarantees an epic every ≤150 pulls but does **not** guarantee a specific epic —
  you still need to hit the right one from within the pool
- As you Ancestral 5\* more epics, odds on your remaining targets improve materially

This is why modules are a **long-term compounding investment**, not a targeted purchase.
Budget accordingly — do not expect to farm a specific natural epic quickly.

---

## Gem Budget for Module Pulls

Module pulls compete with card slots, card copies, and relics for gems. Priority order
from `references/tta-card-slots.md`:

1. Maintain 1,600 gem event reserve (for the 80-card event mission)
2. Unlock all 5 lab slots (highest compounding return)
3. 80/20 rule until common and rare cards are maxed: 80% on cards/slots, 20% on modules
4. After cards are maxed: shift remaining gems toward modules and relics

**Relics vs. modules**: relics are a known-commodity purchase — you see exactly what you
are getting before spending. Modules are pure RNG. When a high-value relic appears in the
weekly store, take it before pulling modules.

**Lucky Banner timing**: if the Lucky Banner is not currently active, consider holding
above-reserve gems rather than spending them on Standard Banner pulls. The 60% improvement
in epic rate makes the wait worthwhile for players who are not urgently blocked.

---

## Using the tower-analyzer MCP

| Tool | When to call |
|------|-------------|
| `get_currencies` | Check gem balance before recommending pulls |
| `get_tower_state` (sections: `["modules"]`) | Check which modules are owned and their Ancestral 5\* status before discussing pull priority |

Before advising on pull priority, verify which epics the player already has at Ancestral
5\* — the protection rule means their effective odds on remaining modules may be
significantly better than the base rates suggest. Also check whether any target module has
a two-copy use case before the first copy reaches 5\*.
