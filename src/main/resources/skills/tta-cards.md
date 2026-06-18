---
name: tower-cards
description: >
  Expert knowledge on the Cards system in The Tower: Idle Tower Defense. Use this skill
  whenever the user asks anything about cards — including which cards to equip, when to swap
  cards mid-run, farming vs. tournament loadouts, card mastery priorities, card slot unlocks,
  gem spending on cards, locked card rules, preset management, or how specific cards interact
  with builds. Also trigger for questions about specific card names (e.g. "Death Ray", "Wave
  Skip", "Demon Mode", "Berserker", "Free Upgrades", "Energy Shield", "Intro Sprint"), or
  indirect questions like "what cards should I run for farming", "should I use Death Ray",
  or "when do I swap my preset mid-tournament".
---

# Cards

Cards are equippable items that provide passive or active effects. Each costs 20 Gems to
purchase and can be leveled to 7 stars by collecting duplicate copies. Cards are organized
into **presets** — saved loadouts that can be switched mid-run, subject to locked card rules.

For raw stat tables (per-level values for all 31 cards), see `references/tta-card-stats.md`.
For card mastery strategy, see `references/tta-card-mastery.md`.
For card slot unlock strategy and gem spending priority, see `references/tta-card-slots.md`.

---

## The Core Decision: Farming vs. Tournament vs. Milestone

**Farming runs** prioritize Coins Per Hour (CPH) and wave depth. CPH increases as you reach
higher waves within a tier — the goal is to survive long enough to reach peak spawn rates
while keeping coin multipliers stacked. Economy cards (Coins, Enemy Balance, Critical Coin,
Wave Accelerator) are the foundation. Cards that kill enemies outside your coin windows
(Death Ray) or slow enemy throughput (Slow Aura in early waves) actively hurt CPH.

**Tournament and milestone runs** prioritize waves cleared. Coins are irrelevant to ranking.
CC, survivability, and burst damage replace economy cards. The loadout shifts heavily toward
cards that keep you alive longer and kill bosses faster.

**Preset management**: Most players run one farming preset and one or two tournament presets.
Switching presets mid-run is only allowed when the new preset's locked cards exactly match
the current locked cards. When any boss or fleet enemy is alive, all cards lock and cannot
be changed until all bosses and fleets are dead.

**Mid-run preset swap example**: Swap Free Upgrades for Land Mine Stun mid-tournament once
all workshop upgrades are complete — Free Upgrades stops providing value the moment there is
nothing left to upgrade, and the stun buys time to kill enemies before they reach the wall.

---

## Common Cards

**Damage** — Always-in for virtually every build. Early game it also accelerates **DW health
bonus stacking** — higher damage means enemies die faster to the Death Wave, building the
health bonus sooner. The only deliberate exclusion is **devo (de-evolved) orb builds**, where
tower damage is intentionally nerfed so enemies survive long enough to be killed by orbs inside
the BH during the GT/DW/BH coin window. Equipping Damage in a devo build undermines the entire
strategy.

**Attack Speed** — A proc and control generator: more shots means more knockback procs, more
CL procs, and more triggers of workshop abilities (crit, super crit, rapid fire, bounce shot).
Whether it helps or hurts depends entirely on what is killing your enemies:

- **eHP orb builds**: Valuable — knockback keeps enemies at the orb line.
- **eHP with OA + pBH**: Often excluded for elites specifically. You want elites to reach the
  wall and die fast before they pile up and hit simultaneously. Knocking them back keeps too
  many on screen at once, risking the spawn cap (120 normals / 20 elites / 10 bosses) and
  losing CPH.
- **Devo orb builds**: Exclude — knockback disrupts the controlled kill setup.
- **Thorns builds**: Exclude — you want enemies reaching the tower faster, not pushed back.
- **Spawn cap awareness**: Keeping enemies alive too long to wait for multipliers costs spawns.
  Killing enemies without multipliers is better than hitting the cap and losing new spawns.

Identify your primary kill source first, then decide whether attack speed helps or hurts.

**Health** — Mandatory for **eHP and hybrid builds**. Never run in **Glass Cannon (GC)**:
GC's base health is so low that any hit is fatal — the card provides no buffer against a
one-shot, making the slot wasted.

**Health Regen** — Only valuable with the **Wormhole Redirector (WHR) module** (which
converts Package Max Recovery into regen) or in a **Wall build** (where regen sustains the
wall for deep runs). Without one of those two anchors, base regen values are negligible and
the slot is wasted. Never run in GC builds.

**Range** — **Tournament and milestone only**. Extra range gives more time for bullets,
projectiles, and UWs to kill enemies before they reach the tower. In **farming, exclude it**:
larger range spreads enemy spawns and reduces overlap with your BH coin window, directly
hurting CPH. Manage range per run type via the range lab slider — slide to minimum before
farming runs, max it for tournament and milestone pushes.

**Cash** — Essential **early game** when workshop upgrades are the bottleneck. Drop it once
your essential workshop stats (Damage, Health, Defense, Enemy Level Skips) are complete — the
card becomes dead weight with nothing left to buy. Has a niche late-game interaction with the
**Project Funding (PF) module**, which scales damage with the digit count of your cash balance
— a higher cash total from the Cash card pushes the digit count higher and amplifies PF's
damage bonus.

**Coins** — Farming staple, always paired with **Enemy Balance**. The multiplier affects all
earned coins and scales with global modifiers. Drop in **tournaments** (irrelevant to wave
count ranking) and during **milestone pushes** where survival takes priority.

**Slow Aura** — Tournament and milestone CC tool. Stacks with CF slow and the −40% Enemy
Speed perk. In **farming**, slowing enemies keeps them alive longer, which reduces enemy
throughput and hurts CPH. In **devo builds**, it disrupts enemy pathing and ruins the setup.
In **early game**, the percentage reduction is too small to matter — better to use a
damage or economy card.

**Critical Chance** — Pairs with **Critical Coin** for a farming coin combo: more crits means
more Critical Coin procs. Also has late-game value for UW damage, especially **Chain Lightning**
which procs frequently and benefits significantly from crit chance. Weak without Critical Coin
equipped. Low priority in eHP builds or tournaments where survivability cards are more valuable.
**Exceeding 100% crit chance is not wasted** — going over 100% provides an additive crit damage
bonus on top of the crit factor, so there is no reason to stop at 99%.

**Enemy Balance** — Farming and early-game staple. More enemies means more kill opportunities,
more coin drops, and faster **DW health bonus accumulation** (more targets = more Death Wave
hits per wave). Drop it during **milestone pushes** if the extra enemy volume overwhelms your
CC, and in **late-game tournaments** if your workshop is already maxed and you need the slot
for defense.

**Extra Defense** — Useful early through mid-game and in **tournaments without perks**, where
the non-perk defense cap is 73% and every percentage point matters. Retire it permanently once
your defense reaches the **98% hard cap** through labs (Def% lab level 23+), standard perks,
and module sub-stats — beyond that, equipping it provides zero benefit.

**Fortress** — Early-game scaffolding only. Defense Absolute scales terribly against enemy
damage in the late game and is permanently outclassed by percentage-based defense, orbs, and
lifesteal builds. Retire it in mid-to-late game and do not run it in GC builds.

---

## Rare Cards

**Free Upgrades** — Farming and idle-game staple across thousands of waves. Free upgrades save
enormous amounts of cash on expensive workshop stats. The **mastery effect** — locking specific
stats from receiving free upgrades — makes the card viable longer by giving control over which
stats get upgraded. Drop it in **devo builds** (premature stat boosts ruin the kill setup), in
**late-game push runs** where you need total stat control, and once the workshop is complete.
The typical mid-tournament swap: replace Free Upgrades with Land Mine Stun once all upgrades
are done.

**Extra Orb** — Farming card. Primary value is positioning the extra orb to pass through the
center of your **BH** to kill clustered enemies and multiply coin gains. Use the **Orb Adjuster
lab** (unlocked T6W50, adjusts distance from 60m to 1.3× tower range) to tune the position.
Retire when workshop orbs or BH Damage alone handle the killing. Not worth a slot in
high-tier push runs where survival cards are needed.

**Plasma Cannon** — Fires once at a boss, stripping up to 54% of its HP. Core value: reducing
boss HP so Thorns can finish them in fewer hits. Use when **bosses are the kill threat** —
tournaments, milestones, Wall builds where late-run bosses batter through. Drop when elites
are the primary threat, when your UWs or Thorns already kill bosses comfortably, or during
standard farming where bosses aren't the run-ender. **Important**: Plasma Cannon is a locked
card — you cannot add or remove it mid-run. Decide before the run starts.

**Critical Coin** — Pairs with Critical Chance. Whether it's worth equipping depends on your
kill source: the card only procs on **basic enemies** killed by critical damage, not tanks or
elites (which provide ×8 base coins and are the primary income source). Critically important:
**"critical damage" is broader than weapon crits** — Thorns kills and Orb kills both qualify
as critical damage for this card. This means the card is valuable even without high crit
chance or crit factor, as long as thorns or orbs are killing enemies. In **eHP / Blender
builds** (wall + orbs) where orbs sweep through all enemy types indiscriminately, Critical
Coin is a key part of the coin strategy. In **devo orb builds**, you want to keep tanks alive
for multiplier stacking — killing them off for coin procs undermines the strategy, so Critical
Coin is less valuable there. In tournament, drop it for survivability cards.

**Wave Skip** — Accelerates CPH by compressing run time and grants ×1.10 on the previous
wave's cash and coins per skip. **Strong when UWs like GT or BH are active** — a skip during
a coin window earns the multiplied amount. Mid-tournament use: swap the card in just before a
dangerous boss wave to skip it, then swap back out immediately. **Key downsides**: skipped
waves do not trigger free upgrades, do not roll for recovery packages, do not trigger Enemy
Level Skips, and UW cooldowns do not reduce during skipped time. Avoid in devo builds
and when relying on DW health bonus accumulation early in the run.

**Intro Sprint** — Rapidly advances through low-yield early waves (bosses every wave, no coins
during sprint) to reach profitable wave depths faster. Marginal value without Card Mastery.
At **Card Mastery** the sprint can reach up to 1,800 waves, letting you skip directly to
consistent elite spawns for cell farming. Not for tournaments — you lose early cash and the
upgrades that determine survivability. **Important**: bosses do not drop modules or reroll dice
during sprint. Active abilities (Demon Mode, Nuke) cannot be triggered during the sprint phase.

**Land Mine Stun** — Survival and CC card, primarily for **eHP and tournament runs**. Core
value: interrupting **Vampires** — a stunned Vampire cannot apply its regen-blocking aura,
allowing your tower to recover health or lifesteal again. Also useful for general CC alongside
shockwave and Extra Orb. Avoid in farming builds where stunning enemies prematurely kills them
outside the BH/GT/DW coin window.

**Recovery Package Chance** — Two distinct use cases: (1) **GC module synergy** — the Galaxy
Compressor reduces UW cooldowns each time a package is collected; more packages means faster
UW activation cycles; (2) **module shard farming** — once the Recovery Package Mastery is
unlocked, packages have a chance to drop common modules, and higher package chance directly
increases daily shard income. Also valuable in late-game tournaments where frequent package
pickups sustain Overhealth. Low priority without either of those systems active.

---

## Epic Cards

**Death Ray** — **Farming trap.** Fires indiscriminately and kills enemies before they enter
the BH or align with GT/DW/BH coin multipliers, directly depressing CPH. Also steals kills
needed for the DW health bonus early in the run. Use it **late in a run** when CPH no longer
matters and survival is the only goal, or in **tournaments** where clearing swarms before they
reach the tower is the priority. Never equip at run start during a farming or devo setup.

**Energy Net** — Immobilizes a boss for up to 4.3s (mastery: ×2–×20 damage multiplier while
trapped and for 10s after). Niche: primarily valuable in **GC push runs and milestone pushes**
where orbs can be positioned to hit the immobilized boss repeatedly. Falls off once **elites
become the primary threat** rather than bosses — the net does nothing against elites. Not worth
a farming slot.

**Super Tower** — 15s damage burst (×2.5–×5.0) on a 30s cooldown. For **bullet damage builds
in tournaments and push runs**. The value jump comes at **Card Mastery**, which extends 35%
of the Super Tower bonus to UWs and reduces its cooldown — at that point it becomes a
meaningful tournament damage tool. Without mastery, weak in early game and irrelevant in
farming or tank builds.

**Second Wind** — Revives once per run at half health and creates an invincibility shield for
up to 40s. **Wall interaction**: when Second Wind triggers, the wall is also fully restored if
unlocked — a significant survival boost for wall-based builds. Tournament and milestone safety
net. **Recharge Second Wind lab** (T14W60, 7 levels) enables multiple triggers per run:

| Level | Waves to Recharge |
|------:|------------------:|
| 1 | 2,000 |
| 2 | 1,500 |
| 3 | 1,250 |
| 4 | 1,000 |
| 5 | 750 |
| 6 | 550 |
| 7 | 400 |

**Interaction with Energy Shield**: Energy Shield procs first on a fatal hit; Second Wind only
activates if the Energy Shield has no charges. Running both is only worth it if you are
deliberately stacking death-prevention layers. In long farming runs the second chance typically
just delays the inevitable — not worth the slot.

**Demon Mode** — Grants 300× projectile damage and invincibility, once per run. **Manual
trigger only** — useless AFK. Use for **milestone pushes and tournaments** by activating just
before a lethal boss hit. The **Recharge Demon Mode lab** (T14W60, 7 levels) enables multiple activations per run
and significantly increases its value for active players:

| Level | Waves to Recharge |
|------:|------------------:|
| 1 | 1,500 |
| 2 | 1,250 |
| 3 | 1,000 |
| 4 | 750 |
| 5 | 550 |
| 6 | 400 |
| 7 | 300 | **Event mission combo**: in a defense dissonance run (no orbs or thorns),
equip Second Wind + Demon Mode + Nuke — let Second Wind trigger, activate Demon Mode, then
trigger Nuke just before Demon Mode ends to clear up to 120 stacked enemies.

**Energy Shield** — Absorbs one fatal hit; replenishes after 8–20 min (real time). Core
survival card for **GC builds** in tournaments and milestones where any hit is fatal. The
**Extra Hit lab** (T4W300) adds multi-charge capability — the card is marginal without it.
**Conflict with Wave Skip / Wave Accelerator**: the shield replenishes on real time, not wave
count. Accelerating waves means facing bosses with an uncharged shield. In eHP builds where
basic enemies frequently trigger the shield, it gets wasted on non-fatal hits — not worth
running there.

**Wave Accelerator** — Reduces inter-wave cooldown by up to 54%, increasing total waves per
hour. **Farming CPH staple** — less downtime, more enemy spawns, UWs spend more time
actively farming. Drop in **tournaments**: the same acceleration that helps farming now leaves
too little time for Energy Shield to recharge, the Wall to rebuild, and DW to accumulate its
health bonus between waves.

**Berserker** — Increases tower damage based on total damage absorbed this run, capped at ×8
(×500 cap with mastery after Death Defy triggers). Ramps naturally as bosses deal inevitable
damage in tournaments, making it a **tournament essential for GC and hybrid builds**. In
builds that avoid getting hit (or where enemies die to Thorns before dealing damage), the
multiplier never builds — skip it. A common approach: leave it unequipped early in long
farming runs when hits are rare, swap it in once boss damage naturally starts maxing the
multiplier.

**Ultimate Crit** — Gives UWs a chance to crit at your tower's Critical Factor. Low priority
until UWs are well-developed and the workshop crit factor is meaningful. Shines brightest in
**late-game CL builds** where CL's high proc frequency generates frequent crit opportunities.
Tournament value in builds where UW damage is a primary kill mechanism. Never drop a
survivability card for this in farming.

**Nuke** — Destroys up to 100% of on-screen enemies (at max level). **Manual panic button**
for milestone pushes and deep tournament runs. The late-game unlock that makes it passive:
the Smart Nuke automation triggers it on a killing blow, giving 15s of invincibility. Useless
in farming — one-shot manual use doesn't justify the slot. Not effective at low levels (25%
clear at level 1). The **Recharge Nuke lab** (T14W60, 7 levels) enables multiple activations per run:

| Level | Waves to Recharge |
|------:|------------------:|
| 1 | 1,500 |
| 2 | 1,250 |
| 3 | 1,000 |
| 4 | 750 |
| 5 | 550 |
| 6 | 400 |
| 7 | 300 |

See Demon Mode event mission combo above.

**Area of Effect** — Increases AoE damage and radius for ILM, PS, SM, Flame Bot, and Land
Mine. The **PS radius increase** is the primary value — a larger swamp holds more enemies in
the damage field longer. Most useful in **late-game tournaments** with developed PS and ILM,
especially once Card Mastery is unlocked. Negligible impact in farming and weak before the
relevant UWs are well-leveled.

---

## Farming Loadout Principles

- Economy core: Coins, Enemy Balance, Critical Coin, Critical Chance, Wave Accelerator
- Coin amplifiers: Cash (early), Extra Orb (positioned through BH), Wave Skip
- Survivability floor: Free Upgrades (idle), Extra Defense (until 98% cap), Recovery Package
  Chance (once GC module or mastery active)
- Exclude: Death Ray, Range, Slow Aura, tournament-only survivability cards

## Tournament / Milestone Loadout Principles

- Survivability core: Energy Shield, Second Wind, Berserker, Extra Defense (for non-perk cap),
  Plasma Cannon (if bosses are the kill threat), Land Mine Stun (Vampire counter)
- Damage: Damage, Attack Speed, Super Tower (mastery), Ultimate Crit (late game)
- CC: Slow Aura, Energy Net (boss control niche)
- Drop: Coins, Critical Coin, Enemy Balance, Cash, Wave Accelerator, Death Ray

---

## Using the tower-analyzer MCP

Always retrieve live data before making personalized card recommendations.

| Tool | When to call |
|------|-------------|
| `get_card_details(cardName)` | Deep dive on a single card: stat at every star level, copies to next star and to max, gem cost to max, mastery stone cost and unlock status, which presets it's equipped in |
| `get_cards_state` (sections: `["cards", "slots"]`) | Check which cards the player owns, their star level, and slot count |
| `get_cards_state` (sections: `["presets"]`) | Check configured presets and equipped cards per preset |
| `get_currencies` | Before advising on gem spending (card purchases, slot unlocks) |
| `get_lab_costs` | Card mastery lab costs per level |
| `get_lab_slots` | Current lab queue and available lab capacity |
| `get_recent_runs` | Determine run type to contextualize loadout advice |

### Personalization Rules

- **Never assume equipped = optimal.** Check `get_cards_state` first — players often have
  suboptimal cards equipped due to gem constraints or milestone locks.
- **Build archetype drives loadout.** Establish whether the player is farming, in a
  tournament, or pushing a milestone before making any card recommendations.
- **Mastery advice requires verified equip status.** A mastery only activates when the card
  is equipped — confirm before recommending stone investment.
- **Milestone-locked cards can block purchases.** If a player reports they cannot buy more
  cards, check whether the milestone-gated cards (Recovery Package Chance T2W750, Land Mine
  Stun T7W250, Nuke T11W10, Ultimate Crit T14W50, Area of Effect T20W80) are unlocked before
  concluding all cards are maxed.
- **Gem budget is shared.** Card slot unlocks, card copies, and module draws all compete for
  gems. Always call `get_currencies` before recommending gem expenditure.
