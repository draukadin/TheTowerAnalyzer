# Module Leveling Cost Milestones

Costs are pre-lab-discount. The Module Shard Cost and Module Coin Cost labs each reduce these by up to 30%.

For precise range calculations (e.g. shards needed from level 83 to 120), use `get_module_leveling_cost(fromLevel, toLevel)`.

## Key Inflection Points

| Level | Shards/level | Coins/level | Cumulative shards | Cumulative coins | Notes |
|------:|-------------:|------------:|------------------:|-----------------:|:------|
| 1     | 0            | 0           | 0                 | 0                | Slots 1–2 open |
| 41    | —            | —           | —                 | —                | **Slot 3 unlocks** |
| 61    | 250          | 25M         | 4,840             | 71M              | First major coin wall |
| 71    | 350          | 100M        | 7,090             | 371M             | |
| 81    | 500          | 350M        | 10,590            | 1.65B            | |
| 101   | 1,000        | 8B          | 23,590            | 16.3B            | **Slot 4 unlocks**; shard wall + coin cliff |
| 121   | 1,800        | 32B         | 47,390            | 200B             | |
| 141   | 3,000        | 500B        | 88,590            | 808B             | **Slot 5 unlocks**; assist module leveling pause point |
| 161   | 5,000        | 10T         | 163,590           | 20.8T            | Standard target for main module |
| 171   | 6,250        | 510T        | 220,460           | 2.87q            | |

## Strategic Notes

- **Level 41** unlocks the 3rd sub-stat slot — first meaningful progression target for new modules.
- **Level 61** is the first major coin wall (25M/level vs. 3M before it).
- **Level 101** unlocks the 4th sub-stat slot; also the shard wall (1K/level) and coin cliff (8B/level). Most players feel this inflection most acutely.
- **Level 141** unlocks the 5th sub-stat slot and is the **assist module leveling pause point** — after reaching 141 on the assist module, reinvest in the main module (non-linear main stat scaling makes the main module more efficient per shard beyond this point).
- **Level 161** is the standard target for the main module — the `get_shard_rates` projection uses this as its default.
- Beyond 161, both shard and coin costs increase per level with no further step-function breaks.
