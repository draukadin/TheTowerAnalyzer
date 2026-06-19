# MCP Server Backlog

Planned endpoints and fields not yet implemented.

---

## Endpoints

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

## Skill File Backlog

### Lab DB descriptions — remaining categories

Two lab categories have unlock data populated but descriptions not yet added, pending
in-game access:

- **Cards mastery labs** (ids 107–137, 31 labs) — all unlock at T16W100. Descriptions
  deferred until player reaches that milestone and can verify in-game wording.
- **Battle Condition labs** (ids 198–217, 20 labs) — unlock T18W1000–T21W1000. Descriptions
  deferred until player unlocks these labs in-game.

---

## Missing Fields

### `get_tower_state`
- `workshop.*` — per-stat values with contributor breakdown (workshop + relics + module substats + cap).
