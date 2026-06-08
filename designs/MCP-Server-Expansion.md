# MCP Server Expansion Design

**Goal:** Expose all database-backed data via MCP tools, with section flags so the AI agent can request only what's relevant and avoid flooding its context window.

---

## Design Principles

1. **One tool per domain** — mirrors the backend controller structure; each tool is independently callable.
2. **Section flags over separate tools** — large domains use a `sections` string-array parameter. The caller lists only what it needs; omitting it returns a sensible default subset.
3. **Distill, don't proxy** — every tool flattens and abbreviates the API response. Keys stay short; large numbers are scaled (e.g. `coins_T`, `cells_K`). Raw payloads are never returned.
4. **Read-only** — all tools are GET-only. Mutations stay in the REST API.

---

## Section Flags Pattern

Tools that have large or multi-faceted responses expose a `sections` parameter:

```json
{
  "name": "sections",
  "type": "array",
  "items": { "type": "string", "enum": ["<section-a>", "<section-b>", ...] },
  "description": "Sections to include. Defaults to [...]. Pass only what you need."
}
```

If `sections` is omitted, the tool returns its **default subset** (marked below with `*`).

---

## Existing Tools — Changes

### `get_tower_state`
`sections` replaces the old `includeRelicDetails` boolean.

| Section | Default | Description |
|---|---|---|
| `uw`* | yes | Ultimate weapon stat blocks |
| `modules_active`* | yes | Preset-assigned modules only; substats as `{key, rarity}` (compact) |
| `modules_all` | no | All 24 modules with full substats — use for swap analysis |
| `relic_summary`* | yes | Owned/total counts by relic type |
| `relic_details` | no | Full owned relic list with bonus stats |

### `get_lab_state`
No structural change. Existing `hideMaxed` + `category` params are sufficient.

### `get_recent_runs`
Add `sections` for detail level. Default `limit` is **3**.

| Section | Default | Description |
|---|---|---|
| `summary`* | yes | Per-run: date, type, tier, wave, coins_T, cph_T, cells_K, killed_by |
| `diagnosis` | no | Include `killed_by` analysis note from `/diagnosis` endpoint |

> Note: the backend `/api/reports` has no `limit` query parameter — the limit is applied client-side via `.slice(0, limit)`.

---

## New Tools

### `get_workshop_state`

```
GET /api/workshop/...  (multiple endpoints)
```

| Section | Default | Description |
|---|---|---|
| `items`* | yes | All items: name, category, level, max_level, is_plus |
| `unlock_progress`* | yes | Workshop+ unlock progress summary |
| `discounts` | no | Active discount multipliers |
| `presets` | no | Preset slot assignments (regular + plus) |
| `spend` | no | Cumulative Workshop+ category spend |

**Additional params:** `category` — filter to `ATTACK`, `DEFENSE`, or `UTILITY`.

---

### `get_cards_state`

```
GET /api/cards/...
```

| Section | Default | Description |
|---|---|---|
| `cards`* | yes | Card collection: name, star_level, copies_owned, mastery_level |
| `slots`* | yes | Slot unlock status (slots 1–28) |
| `presets` | no | Preset slot assignments |

---

### `get_bots_state`

```
GET /api/bots/...
```

| Section | Default | Description |
|---|---|---|
| `bots`* | yes | Bot unlock state + stat levels (current/max) |
| `presets` | no | Preset stat targets and unlock state per bot |

---

### `get_guardian_state`

```
GET /api/guardian/...
```

| Section | Default | Description |
|---|---|---|
| `chips`* | yes | Chip acquisition state + stat levels |
| `slots`* | yes | Slot unlock state (slots 1–10) |
| `presets` | no | Preset chip assignments and stat targets |

---

### `get_tournament_history`

```
GET /api/tournaments/...
```

| Section | Default | Description |
|---|---|---|
| `tournaments`* | yes | All tournaments: date, league, conditions |
| `conditions`* | yes | All known battle conditions (name, acronym, category) |

**Additional params:** `league` — filter to `SILVER`, `GOLD`, `PLATINUM`, `CHAMPION`, or `LEGENDS`.

---

### `get_tier_pbs`

```
GET /api/tier-pb/
```

No sections needed — response is compact. Returns all tier PBs with wave, attack/defense/utility/UW dissonance waves, and dissonance calculation result.

---

### `get_version_history`

```
GET /api/versions/
```

| Section | Default | Description |
|---|---|---|
| `versions`* | yes | Version list: version string, type (patch/minor/major), summary |
| `changes` | no | Per-version change entries |

**Additional params:** `limit` — cap number of entries (default: **10**).

---

### `get_lab_speed_affordability`

```
GET /api/analysis/lab-speed
```

Exposes the affordability analysis that is currently not reachable via MCP. Takes `days` (default 30), `cellsOnHand` (optional), and `safetyBuffer` (optional). Returns lab speed upgrade tiers and how many cells each would cost vs. current income.

---

### `get_cosmetics`

```
GET /api/cosmetics/
```

Returns cosmetic ownership grouped by category, with total owned vs total available, plus the per-item bonus stat for each category.

No sections needed — response is compact.

---

### `get_pending_changes`

```
GET /api/versions/pending
```

Returns all unprocessed player-state change records captured since the last version entry was created. Used by the AI agent to draft the next version history entry: review the changes, suggest a bump type (patch/minor/major), discuss with the user, then call `POST /api/versions` via the REST API to confirm.

No sections needed — the list is always compact.

---

### `get_lab_slots`

```
GET /api/lab-slots
```

Returns all 5 lab slot configurations. For each slot: cell-speed multiplier, ordered queue of planned labs (from → to, cost, duration), total queue cost/duration, and the current coins-per-day burn rate (computed from the first queued plan minus the already-paid current step).

No sections needed — always returns all 5 slots.

**Distilled shape per slot:**
```json
{
  "slot": 2,
  "speed_mult": 4.0,
  "queue_coins_T": 1.247,
  "queue_days": 18.3,
  "coins_per_day_T": 0.038,
  "plans": [
    { "lab": "Attack Speed", "from": 66, "to": 68, "coins_T": 0.421, "days": 7.0 }
  ]
}
```

Slots with no plans return `plans: []` and `coins_per_day_T: null`.

---

## Summary of All Tools (After Expansion)

| Tool | Backend Source | Sections? |
|---|---|---|
| `get_currencies` | `/api/player-tracker/currencies` | No |
| `get_shard_rates` | `/api/analysis/shards` | No |
| `get_cell_income` | `/api/analysis/cells` | No |
| `get_lab_speed_affordability` | `/api/analysis/lab-speed` | No |
| `get_recent_runs` | `/api/reports` | Yes |
| `get_tower_state` | `/api/player-tracker/state` | Yes |
| `get_lab_state` | `/api/player-tracker/lab-state` | No (existing filters) |
| `get_workshop_state` | `/api/workshop/...` | Yes |
| `get_cards_state` | `/api/cards/...` | Yes |
| `get_bots_state` | `/api/bots/...` | Yes |
| `get_guardian_state` | `/api/guardian/...` | Yes |
| `get_tournament_history` | `/api/tournaments/...` | Yes |
| `get_tier_pbs` | `/api/tier-pb/` | No |
| `get_version_history` | `/api/versions/` | Yes |
| `get_pending_changes` | `/api/versions/pending` | No |
| `get_cosmetics` | `/api/cosmetics/` | No |
| `get_lab_slots` | `/api/lab-slots` | No |

---

## Implementation Notes

- **Multi-fetch tools** (e.g. `get_workshop_state` with `presets`) should make parallel `Promise.all` fetches only for the requested sections.
- Keep the existing `distill*` helper pattern — one pure function per tool that transforms the API response into the compact MCP payload.
- The `sections` array should be validated; unknown section names return an error listing valid options.
- `SHARD_COST_DISCOUNT_LEVEL` hardcoded constant in `server.js` should be replaced by reading the lab state for the "Shard Cost" lab level dynamically.

### Threading & Parallel Fetch Concerns

The SQLite connection pool currently has **1 thread**, which means concurrent DB reads from parallel HTTP fetches can time out. Two mitigations to consider before implementing multi-fetch tools:

1. **Spring cache (already in place):** All reads are cached and evicted on writes. Once a tool's endpoints are warm, parallel fetches will be served from cache without hitting the DB thread — so most parallel calls in practice will be safe.

2. **Increase the read thread pool size:** For cold-cache scenarios (first call after a write eviction, or on startup), the HikariCP pool is already set to **5** in `application.properties`. SQLite supports concurrent reads in WAL mode, so this headroom is only useful once WAL is enabled (see below).

**WAL mode was not previously configured.** It has been enabled by adding `spring.datasource.hikari.connection-init-sql=PRAGMA journal_mode=WAL` to `application.properties`. This runs once per connection on pool creation and persists for the lifetime of the database file. With WAL active and pool size at 5, parallel reads from multi-section MCP tools are safe. The cold-cache risk is now mitigated.

Until confirmed in a test run, multi-fetch tools should still be designed to **fall back to sequential fetches** if a parallel call returns a timeout/503, rather than failing the entire tool call.
