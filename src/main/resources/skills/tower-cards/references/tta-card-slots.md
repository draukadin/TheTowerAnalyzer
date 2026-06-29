---
name: tower-card-slots
description: >
  Strategic guidance on card slot unlocks and gem spending priority in The Tower: Idle Tower
  Defense. Consulted by tta-cards.md. Covers how many slots exist, unlock currency (gems vs.
  keys), cost escalation, when to buy card slots vs. card copies, the event mission gem
  strategy for buying cards, and how to prioritize gems across slots, relics, and modules.
  Use get_cards_state (sections: ["slots"]) to check the player's current slot count and
  owned status before making recommendations.
---

# Card Slots

## Slot Structure

There are 28 total card slots: 22 purchased with Gems and 6 purchased with Keys.

| Range | Currency | Cost Range | Notes |
|-------|----------|------------|-------|
| Slots 1–16 | Gems | 0 → 3,500 | Affordable; slot 1 is free |
| Slots 17–22 | Gems | 4,500 → 10,000 | Expensive late-game investment |
| Slots 23–28 | Keys | 10 → 45 Keys | Separate currency; independent budget |

Per-slot costs are in the DB — use `get_cards_state` (sections: `["slots"]`) for current
unlock cost and owned status for each slot.

---

## Why Slots Matter

More card slots means fewer compromises per run:

- **Farming**: hold a full economy core (Enemy Balance, Coins, Wave Skip, Critical Coin,
  Critical Chance) without sacrificing survivability cards.
- **Tournament**: swap in situational cards (Energy Shield, Second Wind, Death Ray, Plasma
  Cannon) without displacing farming staples.
- **General**: every additional slot is a full card effect active every run.

**Early-to-mid game target**: 14–15 slots. This gives enough room for a farming preset and
a tournament preset without constant hard tradeoffs.

---

## Buying Cards: The Event Mission Rule

Cards can be purchased at any time from the shop for 20 Gems each. However, **each event
includes a mission to buy 80 cards**, worth a significant medal payout. The most gem-efficient
approach:

- **Hold off on card purchases until the event mission is active.** Buying 80 cards outside
  an event spends 1,600 Gems on cards alone. Buying 80 cards during the mission earns both
  the cards and the event medals for the same 1,600 Gems.
- **Exception**: if a daily mission requires buying a card, complete it — the daily medal
  value justifies the spend.
- **Once you own all cards**: the 80-card event mission auto-completes and awards medals for
  free. No gems required.

**Reserve rule**: always keep at least **1,600 Gems in reserve** so the event mission can
be completed in full the moment it activates. Gems above that threshold are available for
other spending.

---

## Weekly Gem Store

### Relics

Each week the relic store offers two rare relics at **600 Gems each** and two epic relics
at **1,200 Gems each**. Relics are a **known-commodity purchase** — you can see exactly what
you are getting before buying. The only RNG is which relics appear that week. If you already
own all four offered relics, you keep your gems.

Evaluate each relic against your current bottleneck. A relic that directly addresses your
weakest stat (defense, lab speed, relevant UW bonus) is worth taking; a duplicate or
off-archetype relic is not.

### Modules

Modules purchased from the gem store involve **pure RNG** — you draw from a pool and are
unlikely to receive exactly what you need on any given draw. Unlike relics, you do not know
what you are getting before spending. Treat module gem purchases as a long-term compounding
investment, not a targeted item purchase.

A dedicated `tta-modules.md` skill covers module investment strategy in detail, including
the gem draw mechanics.

---

## Gem Spending Priority

**1. Maintain the 1,600 Gem event reserve.** Never let your gem balance drop below this —
the event mission opportunity cost of missing it exceeds any single purchase.

**2. Unlock 5 lab slots first.** Lab slots are purchased with gems and enable permanent
parallel research. Ensuring all 5 labs are running at all times is higher priority than
any card or slot investment because it compounds every day.

**3. Apply the 80/20 rule until common and rare cards are maxed.** Spend approximately 80%
of available gems (above the 1,600 reserve) on cards and card slots, and 20% on modules.
Rationale: until your card roster is developed, the marginal gain per gem on cards/slots
exceeds module RNG expected value. Once common and rare cards are maxed, this balance shifts
toward modules and relics.

**4. Only buy a slot if you have a card to fill it.** Do not unlock an empty slot in
anticipation — every gem spent on an empty slot is opportunity cost on a module or relic.
Unlock a slot when you have a specific card you want to run but no room to equip it.

**5. After the reserve and lab slots, spend above-reserve gems on relics and modules.** When
a high-value relic appears in the weekly store, take it. Use remaining gems on module draws
for long-term compounding.

---

## Key Slots (Slots 23–28)

Key slots require Keys — a currency earned separately from gems (primarily through events
and achievements). Key slot costs run 10–45 Keys across the six slots. Treat the key budget
as independent from gem decisions; buying key slots does not compete with gem spending.

---

## Using the tower-analyzer MCP

| Tool | When to call |
|------|-------------|
| `get_cards_state` (sections: `["slots"]`) | Check current slot ownership and next unlock cost |
| `get_currencies` | Check gem and key balances before recommending a purchase |
| `get_cards_state` (sections: `["cards"]`) | Identify unleveled cards that benefit from copy investment |

Always call `get_currencies` before any gem-spend recommendation — verify the 1,600 Gem
event reserve is maintained after the proposed purchase.
