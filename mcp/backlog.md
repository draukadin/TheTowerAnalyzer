# MCP Server Backlog

Planned endpoints and fields not yet implemented.

---

## Endpoints

### `get_gt_income_projection`
Given the player's current GT+ level, GT duration, GT cooldown, and kills-per-second (derived
from recent runs), compute projected Golden Tower income using the formula:

```
K  = Kps * d
B  = ((1 + 0.0003 * (Level + 1))^K) - 1
T  = Total run duration / GT CD
FI = T * Kps * d * income_per_mob * (1 + B)
```

At 100% GT uptime (perma-GT via MVN or generator module sub-stats):
```
FI = Total duration * Kps * income_per_mob * ((1 + 0.0003 * (Level + 1))^(Kps * d))
```

Should return: projected income, marginal value of +1 duration at current GT+ level, and a
comparison table across key duration milestones.

**Needed for**: advising whether to invest next stone in GT Duration vs. GT+ level vs. GT Cooldown.

---

### `get_sl_coverage_efficiency`
Given the player's current SL Angle level, Quantity level, and stone costs at those levels,
compute coverage-per-stone for the next Angle level vs. the next Quantity level. Effective
coverage = Angle × Quantity; each Angle level gained is worth `current_quantity` degrees.

Should return: current effective coverage, coverage gain from next Angle level, coverage gain
from next Quantity level, stone cost of each, and coverage-per-stone comparison.

**Needed for**: deciding whether to invest next stone in Angle vs. Quantity.

---

### `get_lab_plan`
Returns the current state of each lab slot: what's being researched, target level, coin cost, time remaining, and what's queued next once the current target is reached.

**Needed for**: lab prioritization advice, estimating time-to-completion, identifying idle slots.

---

### `get_card_details`
Given a `cardName`, returns full progression state for that card:
- Current star level and stat value at that level
- Stat value at every level (1–7) — backs questions like "what do I get at level 6?"
- Copies owned toward the next star, copies remaining to reach level 7
- Gem cost to reach level 7 from current level
- Mastery stone cost and whether mastery is unlocked
- Which presets the card is currently equipped in

**Needed for**: answering "how many more copies to reach level 7?", "how much does this card
improve at the next star?", "how many stones to unlock this mastery?".

---

### `get_cards_summary`
Aggregate view across all cards the player owns:
- Total gems needed to max all non-maxed cards
- Per-card breakdown: name, current level, copies to next level, copies to level 7, gems to level 7
- Total mastery stones needed to unlock all masteries for currently-equipped cards
- Cards not yet purchased (level 0), grouped by rarity, with gem cost to unlock

**Needed for**: answering "how many gems to max all my remaining cards?", gem budget planning,
identifying which cards to prioritize buying next.

---

### `skill_content_injection` (RAG / context injection layer)
Currently all skill files (top-level and references) are installed flat to `~/.claude/skills/`
via a glob pattern. A proper reference model would keep only entrypoint skills (`tta-uw.md`,
`tta-modules.md`) installed and inject reference file content into tool results or the system
prompt on demand — similar to RAG.

Requires: a service that reads reference files from the classpath, a trigger mechanism to decide
which references are relevant to a given query, and an injection point in the MCP response
pipeline.

**Needed for**: cleaner skill hierarchy where references are data-only and not exposed as
independently-triggerable skills.

---

---

## Skill File Backlog

### Lab category reference files
`tta-labs.md` is the strategic entrypoint covering stage-based priority across all lab
categories. Each category should eventually get its own `references/tta-labs-<category>.md`
with per-lab detail: exact level counts, unlock tiers, stat values per level, retirement
triggers, and build-specific nuances.

Categories to cover: Main, Attack, Defense, Utility, Ultimate Weapons, Cards, Perks,
Bots, Enemies, Modules, Battle Condition.

**Approach**: interview per-category the same way cards were done — source material or
user walkthrough one lab at a time.

---

### Lab DB descriptions — remaining categories

Two lab categories have unlock data populated but descriptions not yet added, pending
in-game access:

- **Cards mastery labs** (ids 107–137, 31 labs) — all unlock at T16W100. Descriptions
  deferred until player reaches that milestone and can verify in-game wording.
- **Battle Condition labs** (ids 198–217, 20 labs) — unlock T18W1000–T21W1000. Descriptions
  deferred until player unlocks these labs in-game.

Also pending: fix trailing space on Recharge Second Wind description (id 104).

---

## Missing Fields

### `get_tower_state`
- `run_active` (boolean) — whether a run is currently in progress. Needed before any reroll, level, or merge recommendation.
- `modules.effect_bans` — which sub-stats are currently banned per module type.
- `workshop.*` — per-stat values with contributor breakdown (workshop + relics + module substats + cap).

### `get_cards_state`
- `presets` section — which cards are equipped in each named preset. Needed for mid-run swap advice and preset comparison.
- `cards[].copies_toward_next` — copies accumulated toward the next star level. Needed for `get_card_details` progression calculations.
