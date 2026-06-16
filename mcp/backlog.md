# MCP Server Backlog

Planned endpoints and fields not yet implemented.

---

## Endpoints

### `get_module_leveling_cost`
Given `fromLevel` and `toLevel`, returns the total shards and coins needed to level a module between those two levels. Backed by the `module_level_cost` table (seeded from `ModuleLevelTable`).

**Needed for**: leveling feasibility checks, shard-to-target projections, affordability questions.

---

### `get_lab_plan`
Returns the current state of each lab slot: what's being researched, target level, coin cost, time remaining, and what's queued next once the current target is reached.

**Needed for**: lab prioritization advice, estimating time-to-completion, identifying idle slots.

---

## Missing Fields

### `get_tower_state`
- `run_active` (boolean) — whether a run is currently in progress. Needed before any reroll, level, or merge recommendation.
- `modules.effect_bans` — which sub-stats are currently banned per module type.
- `workshop.*` — per-stat values with contributor breakdown (workshop + relics + module substats + cap).
