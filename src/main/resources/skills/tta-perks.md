---
name: tower-perks
description: >
  Expert knowledge on the Perk system in The Tower: Idle Tower Defense. Use this skill
  whenever the user asks about perks — which perks to pick, which to ban, how many Ban
  Perk bans to get, the perk wave requirement formula, standard vs. trade-off vs. UW
  perks, or perk strategy for farming vs. milestone/dissonance runs. Also use for
  questions about auto-pick ranking order, Standard Perk Bonus lab value, or Improve
  Trade-off Perks lab. Do NOT use for full UW mechanic detail beyond the perk bonus value
  (see tta-uw.md and UW reference files). Perks are only available during farming runs —
  they are NOT available in tournament mode.
---

# Perks

## How Perks Work

Perks are special bonuses that activate during a farming run starting at wave 200. Each
time you earn a perk, you choose one from a randomly generated pool and it remains active
for the rest of that run. Perks reset at the start of every new run.

**Perks are farming-only.** They are not available in tournament runs.

### Perk Pool Size

You start with 2 choices per perk unlock. The **Perk Option Quantity** lab (max 2 levels)
adds +1 choice per level, raising the maximum to 4 choices. Having 4 choices significantly
reduces RNG — max this lab as soon as it unlocks (T4W80).

It is possible for all choices in a draw to be trade-off perks even if other perk types
are available.

### Wave Requirements

The base waves required to earn each perk increases as your total perk count grows:

| Perks selected so far | Base waves required |
|----------------------|---------------------|
| 0–19 | 200 |
| 20–29 | 250 |
| 30–39 | 300 |
| 40+ | 350 |

These base values are reduced by the **Waves Required** lab (−1 wave per level) and the
**Perk Wave Requirement** standard perk (−20% per pick, max 3 picks).

**Waves Required formula** (apply to each base breakpoint separately):

```
Waves = floor((Base − Waves_Required_lab_levels) × (1 − PWR_perk_picks × 0.20 × (1 + Standard_Perk_Bonus / 100)))
```

- Round **down** (floor) after applying the formula.
- Calculate the floored result for the first perk at each base breakpoint, then multiply
  by the number of perks in that range.
- Call `get_perk_wave_cost` rather than computing this manually.

### First Perk Choice

The **First Perk Choice** lab (1 level, T2W250) guarantees your selected perk appears as
the top choice in the first perk draw of every run. **Auto Pick Perk always selects the
top choice**, so this lab determines what auto-pick takes at wave 200. Configure your
first perk choice to whatever is highest priority for your current run type — typically
Perk Wave Requirement for wave-depth runs or a coin bonus for farming.

### Auto Pick Ranking

The **Auto Pick Ranking** lab (max 32 levels, T4W50) lets you rank perks in priority
order. When auto-pick fires, it selects the highest-ranked perk available in the current
draw. Each lab level adds one more rankable slot. Call `get_perk_settings` to see the
current ranking and ban list.

---

## Standard Perks

Standard perks can be picked multiple times per run up to their quantity limit.

**Additive perks** (Defense%, Perk Wave Requirement):
```
Total Bonus = Base Value × Quantity × (1 + Standard Perk Bonus / 100)
```

**Multiplicative perks** (Coin Bonus, Health, Damage, etc.):
```
Total Bonus = (1 + Base Value × Quantity) × (1 + Standard Perk Bonus / 100)
```

| Perk | Base Value | Max Picks |
|------|-----------|-----------|
| Max Health ×1.20 | ×1.20 | 5 |
| Damage ×1.15 | ×1.15 | 5 |
| All Coin Bonuses ×1.15 | ×1.15 | 5 |
| Defense Absolute ×1.15 | ×1.15 | 5 |
| Cash Bonus ×1.15 | ×1.15 | 5 |
| Health Regen ×1.75 | ×1.75 | 5 |
| Interest ×1.50 | ×1.50 | 5 |
| Land Mine Damage ×3.50 | ×3.50 | 5 |
| Free Upgrade Chance for All +5.0% | +5.0% | 5 |
| Defense Percent +4.00 | +4.00% | 5 |
| Bounce Shot +2 | +2 | 3 |
| Perk Wave Requirement −20.00% | −20% | 3 |
| Orbs +1 | +1 | 2 |
| Unlock a Random Ultimate Weapon | — | 1 |
| Increase Max Game Speed +1.00 | +1 speed | 1 |

**Standard Perk Bonus lab** (max 25 levels, T2W150) amplifies all standard perks. Each
level adds meaningful multiplier to every standard perk picked for the rest of your
account lifetime — finish this lab.

**Unlock a Random Ultimate Weapon**: awards a UW you don't currently own in a
semi-upgraded state. The temporary UW benefits from applicable module sub-stats (e.g.,
+damage multi ILM applies) but does not unlock lab research for that weapon. When all UWs
are owned, this perk is removed from the pool entirely.

---

## Ultimate Weapon Perks

UW perks only appear if the corresponding UW is unlocked. A UW unlocked mid-run via
"Unlock a Random UW" immediately adds its UW perk to the available pool for that run.
Each UW perk can only be picked once per run.

| Perk | Effect |
|------|--------|
| 4 More Smart Missiles | +4 missiles per volley |
| Swamp Radius ×1.5 | Multiplies swamp AoE radius |
| +1 Wave on Death Wave | Adds one wave to Death Wave |
| Extra Set of Inner Mines | Doubles ILM mine sets |
| Golden Tower Bonus ×1.5 | Multiplies GT coin bonus (does NOT apply to GT bonus module sub-stat) |
| Chain Lightning Damage ×2 | Doubles chain damage |
| Chrono Field Duration +5s | Adds 5 seconds to CF duration |
| Black Hole Duration +12.0s | Adds 12 seconds to BH duration |
| Spotlight Damage Bonus ×1.5 | Multiplies SL damage bonus |

---

## Trade-Off Perks

Trade-off perks offer a significant upside paired with a significant downside. Each can be
picked once per run. The positive side is amplified by the **Improve Trade-Off Perks** lab
(+1% per level, max 10 levels). The ranged enemy distance perk is
the one exception — the lab does **not** improve its positive side.

| Perk | Upside | Downside |
|------|--------|----------|
| Tower Damage ×1.50 | ×1.50 damage | Bosses have ×8 health |
| Coins ×1.80 | ×1.80 all coins | Tower max health −70% |
| Enemies −50% Health | Enemies die faster | Health Regen and Lifesteal −90% |
| Enemies Damage −50% | Much less incoming damage | Tower Damage −50% |
| Ranged Enemy Distance Reduced | Ranged enemies get closer before shooting | Tower takes ×3 damage from ranged enemies |
| Enemies Speed −40% | Enemies move slower | Enemies Damage ×2.5 |
| Cash Per Wave ×12.00 | Massive wave cash | Enemy kills give no cash |
| Health Regen ×8.00 | Extreme regen | Tower max health −60% |
| Boss Health −70% | Bosses die quickly | Boss speed +50% |
| Lifesteal ×2.50 | Strong lifesteal | Knockback force −70% |

---

## Labs That Affect Perks

| Lab | Max Levels | Effect |
|-----|-----------|--------|
| Unlock Perks | 1 | Unlocks the perk system |
| Waves Required | 100 | −1 wave per level from each base breakpoint |
| Standard Perks Bonus | 25 | Amplifies all standard perk values |
| Perk Option Quantity | 2 | +1 choice per level (max 4 choices) |
| First Perk Choice | 1 | Guarantees your pick appears top-choice at wave 200 |
| Ban Perks | 8 | +1 banned perk per level |
| Improve Trade-Off Perks | 10 | +1% to trade-off perk upside per level |
| Auto Pick Perks | 1 | Enables auto-pick (always takes top-ranked perk) |
| Auto Pick Ranking | 32 | Adds one rankable perk slot per level |

Call `get_lab_state` with `category: "Perks"` for current player levels on all of these.

---

## Strategy by Run Type

### Farming Runs (CPH Optimization)

Goal: maximize coins per hour. Wave depth is not the objective — you're farming at a
comfortable depth you can sustain.

**Priority picks:**
1. **Perk Wave Requirement** — more total perks per run compounds everything below
2. **All Coin Bonuses ×1.15** — direct CPH multiplier, stack all 5
3. **Cash Bonus ×1.15** — stacks independently with coin bonuses
4. **Damage ×1.15** — faster kills = more coin opportunities per hour
5. **Trade-off: Coins ×1.80 / Health −70%** — take it if your health buffer allows; a
   significant CPH boost. Skip if health is already marginal.
6. **Game Speed +1** — higher game speed = more waves per hour

**Trade-offs to evaluate:**
- **×1.80 coins / −70% health**: usually worth it in a well-established farming run;
  avoid if health is the limiting factor
- **Cash Per Wave ×12 / no kill cash**: only valuable in specific wave-milestone-cash
  builds; usually skip unless kill cash is negligible to your income

**Perks of lower farming value** (candidates to ban for farming focus):
- Interest — the in-game perk ROI is low; a slot spent here is a slot not spent on coins
- Land Mine Damage — useful only for mine-focused builds; skip otherwise
- Bounce Shot — niche; skip unless running a bounce-dependent setup
- Perks that improve survivability you don't need (health, defense) when your farming
  depth is already comfortable

---

### Milestone and Dissonance Runs (Wave Depth)

Goal: reach a specific wave number 4500 for milestone and 5000 for dissonance. Cash still matter 
for upgrades mid-run but survivability and boss management are the primary concern.

**Priority picks:**
1. **Perk Wave Requirement** — mandatory; more perks = more total power to push waves
2. **Max Health ×1.20** — stacks up to 5×; survivability compounds with each pick
3. **Defense Percent +4.00** — each pick adds 4% defense (amplified by Standard Perk
   Bonus); stack multiple picks on deep pushes
4. **Defense Absolute ×1.15** — secondary defense layer; valuable when enemy damage
   scaling is the wall
5. **Health Regen ×1.75** — sustain between bosses; stacks well
6. **Trade-off: Enemies Damage −50% / Tower Damage −50%**: cuts incoming damage
   dramatically; worth the damage trade on a survival-first push
7. **Trade-off: Boss Health −70% / Boss Speed +50%**: bosses die fast and move faster —
   excellent for runs where bosses are the kill floor, provided your damage can handle
   the speed

**Trade-offs to avoid on wave-depth runs:**
- **Coins ×1.80 / Health −70%**: a health penalty on a survival run can be fatal. Only
  take this if your health pool is already deep enough to absorb it.
- **Enemies −50% Health / Regen −90%**: eliminating regen is dangerous if you rely on
  sustained healing. Skip unless health pool is large and enemies are the bottleneck.
- **Ranged Enemy Distance Reduced / ×3 ranged damage**: taking ×3 damage from ranged
  enemies is punishing on deep pushes. Avoid.

**UW perks on wave-depth runs:** take any UW perk that extends survivability windows —
Chrono Field Duration (+5s), Black Hole Duration (+12s) are high value because they buy
you more time to clear waves. Damage UW perks (Chain Lightning ×2, SM +4) are secondary
unless damage is the bottleneck.

---

## Ban Perks Strategy

**Ban Perks lab** Each level adds one permanent ban —
perks you select never appear in your draw pool for any future run. Bans cannot be
changed once set, so choose what to ban deliberately.

**General banning philosophy:**
- Ban perks with consistently low ROI across all run types first.
- Perks that are valuable in one niche but useless in your actual runs are prime ban
  candidates.
- Resist banning trade-off perks unless they are actively harmful in your typical run —
  they add strategic depth and their frequency is naturally capped at 1 per run.

**High-priority ban candidates (low ROI across all contexts):**
- **Interest** — weak passive income; other economy perks provide better value per pick
- **Land Mine Damage ×3.50** — only relevant for mine-centric builds; negligible for
  most players
- **Bounce Shot +2** — niche; only valuable for blender-bounce setups

**Context-dependent ban candidates:**
- **Enemies Speed −40% / Enemies Damage ×2.5** — the damage multiplier can be lethal
  on deep runs; consider banning if this trade-off frequently kills your wave-depth runs
- **Free Upgrade Chance +5.0%** — useful early game, diminishing value late game when
  upgrades are infrequent; eventual ban candidate

**Do not ban:**
- **Perk Wave Requirement** — universally valuable; reduces waves to next perk, giving
  you more total perks per run for free
- **All Coin Bonuses** — top CPH perk in farming; always want it in the pool
- **Trade-off perks (generally)** — situational but strategic; each only appears once
  per run so they don't crowd out other picks
- **UW perks** — auto-filtered when the UW is not unlocked; no reason to permanently
  ban them

**Targeting 4–5 bans short-term is appropriate.** At level 5 (5 bans) you have one ban
remaining before the current target. Levels 6–8 are available if a 6th ban becomes
clearly justified — e.g., if a specific perk is consistently hurting your dissonance
runs and you'd rather have the draw slot. Don't rush to 8 just to fill slots; ban with
intent.

---

## Using the tower-analyzer MCP

| Tool | When to call |
|------|-------------|
| `get_perk_settings` | Get current ban list and auto-pick ranking order |
| `get_perk_wave_cost` | Compute wave cost per perk across all four base breakpoints given current lab levels and PWR picks; optionally count perks reachable at a target wave |
| `get_lab_state` with `category: "Perks"` | Check current levels of all perk labs (Waves Required, Standard Perks Bonus, Ban Perks, etc.) |
| `get_lab_costs` | Get coin cost and time for next Ban Perks or Waves Required levels |
| `get_recent_runs` | Confirm whether the user is farming or pushing — drives which perk strategy applies |
