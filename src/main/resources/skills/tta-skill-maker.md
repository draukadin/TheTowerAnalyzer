---
name: tower-skill-maker
description: >
  Process and standards for creating new skill files for The Tower Analyzer. Use this skill
  whenever asked to create, rewrite, or audit a Tower skill file — including tta-cards.md,
  tta-uw.md, tta-modules.md, or any new topic area (guardian, workshop, labs, bots, etc.).
  Also use when reviewing an existing skill file for quality or consistency with established
  patterns. Do NOT use for answering game questions directly — this skill is about producing
  skill files, not game advice.
---

# Tower Skill File Standards

This skill defines how to produce skill files for The Tower Analyzer. Skill files are loaded
by Claude Code via `~/.claude/skills/` and provide strategic AI guidance on game topics.
They are NOT raw data stores — data lives in the DB and is served via MCP tools.

---

## File Locations

```
src/main/resources/skills/
  tta-<topic>.md          ← top-level skill files (installed to ~/.claude/skills/)
  references/
    tta-<topic>.md        ← reference files (data tables, lookup content, detail layers)
```

Top-level skill files are the entrypoints — they contain strategic guidance and reference
detail files where needed. Reference files contain data tables or deep detail that would
bloat the skill file. Both are installed to `~/.claude/skills/` via the
`classpath:skills/**/*.md` pattern in `ClaudeSkillsService`.

---

## Frontmatter Format

Every skill file must begin with YAML frontmatter:

```yaml
---
name: tower-<topic>
description: >
  One paragraph. First sentence: what this skill covers. Second: explicit trigger conditions
  (what questions invoke it). Third: also trigger for indirect questions like "..." that
  imply this topic. End with: what NOT to trigger for, if relevant.
---
```

The `description` field is what Claude Code uses to decide whether to load the skill.
Be specific about trigger phrases — vague descriptions cause missed triggers or false triggers.

---

## The Three Boundaries

### 1. Skill file vs reference file

**Skill file contains**: Strategic guidance — when to invest, what to prioritize, how
something interacts with builds, what to avoid and why, how to use MCP tools to personalize
advice. The "expert knowledge" a knowledgeable player would give.

**Reference file contains**: Data tables, per-level stat values, cost tables, lookup content
that the skill file needs to cite but that would bloat the strategic narrative if inlined.

Rule: if a table has more than ~4 rows of pure numbers with no strategic implication per row,
it belongs in a reference file.

### 2. Skill file vs DB

**DB contains**: Per-level stat values for upgradeable game entities (UW stats, card stats,
lab costs, module costs, workshop costs). Anything the MCP serves via `get_*` tools.

**Skill file contains**: What those stats mean strategically — not the numbers themselves.
Example: "Cooldown has 15 levels and floors at 50s" is strategic context. The actual
cost-per-level table is in the DB, served by `get_tower_state`.

If a player asks "how much does level 8 cost?", the answer comes from an MCP tool call,
not from a skill file table.

### 3. Stone investment vs lab investment

UW skill files must not mix these:
- **Stone investment sections** cover only UW stat upgrades (Damage, Cooldown, Quantity, etc.)
- **Labs sections** cover coin-funded research (separate system, separate budget)

Never list a lab under stone investment priority or vice versa.

---

## Standard Section Structure

Not every skill needs every section — use what fits the topic. Common sections:

```
## What It Does
Brief mechanic description. Include UW+ ability if applicable.

## Stats
| Stat | Base | Max | Levels |
Small stat summary table is acceptable here — it's strategic context, not a data dump.

## Labs
One paragraph per lab. What it does, when it matters, when it doesn't.

## Stone Investment Priority
Ordered guidance. Explain WHY each stat matters, not just what it is.
Never include labs here.

## [Mechanic Name]
Deep dives on non-obvious mechanics (e.g. Perma-BH, Consume, Shock debuff).
Only include when the mechanic has strategic implications that aren't obvious.

## Module Synergies
One paragraph per relevant module. What the module does to this topic,
when it changes the investment calculus, cross-reference to the module skill.

## Practical Use / Investment Ceiling
When to stop investing. What "done" looks like for this topic.

## Using the tower-analyzer MCP
Tool call table + personalization rules. See MCP Section Pattern below.
```

---

## MCP Section Pattern

Every skill file ends with a standardized MCP usage section:

```markdown
## Using the tower-analyzer MCP

Always retrieve live data before making personalized [topic] recommendations.

| Tool | When to call |
|------|-------------|
| `get_tower_state` | Check current [topic] levels before recommending upgrades |
| `get_currencies` | Check stone/gem/coin balance before recommending investment |
| `get_recent_runs` | Determine run type (farming/tournament/milestone) to contextualize advice |

### Personalization Rules

- **Never assume** current levels match any benchmark. Always verify via MCP.
- **[Topic-specific rule 1]**
- **[Topic-specific rule 2]**
```

Adapt the tool table to the topic. Only list tools that are genuinely relevant. Include
backlogged tools (marked as `planned`) if they would be the right tool once implemented.

---

## Cross-Reference Format

Reference other skill files with backtick paths:

```
See `references/tta-death-wave.md` for the full sync table.
See `tta-modules.md` for module investment strategy.
```

Use bare filenames when referencing a peer file in the same directory. Use
`references/filename.md` when referencing a file in the references subdirectory.

---

## Content Gathering Process

For a new skill file, gather content by interviewing the user topic-by-topic rather than
asking for everything at once. The process that works:

1. **Establish the mechanic first**: Ask how the system works before asking about strategy.
   Understanding the mechanic lets you ask better strategic questions.

2. **Go entity-by-entity**: For card-like systems with many items, go one item at a time.
   Ask: "When should this be used? When should it not be used? Any non-obvious interactions?"

3. **Identify the build-fit axis**: Most Tower content splits along farming / tournament /
   milestone. Establish this split early — it becomes the organizing principle for guidance.

4. **Probe for retirement triggers**: Many items that are valuable early become dead weight
   later. Ask: "Is there a point where you stop using this entirely? What triggers that?"

5. **Probe for traps**: Items that look strong but underperform. Ask: "Are there any of
   these that players commonly over-invest in or misuse?"

6. **Probe for interactions**: Ask about synergies with specific modules, UWs, or other
   cards/labs. Non-obvious interactions are the highest-value content in a skill file.

7. **Separate data from strategy**: When the user provides a stat table, note that it goes
   in a reference file or DB — don't inline it into the skill file narrative.

---

## Quality Checklist

Before committing a skill file, verify:

- [ ] Frontmatter `description` has specific trigger phrases, not just topic name
- [ ] No raw stat tables with per-level values (those go in references/ or DB)
- [ ] Stone investment and lab investment sections are separate and not mixed
- [ ] Each section contains strategic guidance, not just mechanic description
- [ ] Build-fit axis (farming / tournament / milestone) is explicit for each major item
- [ ] Module synergies section present if any modules interact with this topic
- [ ] MCP section present with relevant tools and personalization rules
- [ ] Cross-references use correct path format
- [ ] File is in the correct location (top-level skill vs references/)
- [ ] No content that belongs in the DB is written into the file

---

## Auditing an Existing Skill File

When asked to review a skill file for quality:

1. Check each item in the Quality Checklist above
2. Identify raw stat tables that should move to references/ or DB
3. Identify missing strategic content — sections that describe mechanics without
   telling the player when or why to care
4. Check that the build-fit axis is covered (farming vs tournament vs milestone)
5. Check for mixed stone/lab investment
6. Verify MCP section is present and tools are correct

Report findings as a concise list of issues with recommended fixes. Do not rewrite the
file without user confirmation.
