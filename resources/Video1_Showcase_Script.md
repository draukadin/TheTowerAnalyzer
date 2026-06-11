# TheTowerAnalyzer — Video 1: Showcase Script
**Version:** 1.0  
**Last Updated:** 2026-06-09  
**Estimated Runtime:** 8–12 minutes  
**Tone:** Energetic, visual, benefit-focused. No setup instructions — pure showcase.

> **HOW TO USE THIS SCRIPT**
> Lines marked [VISUAL] are screen recording cues.
> Lines marked [NOTE] are reminders to the presenter — do not read aloud.
> Text in [BRACKETS] are placeholders to fill in before recording.
> Section time estimates are targets, not hard limits.

---

## SECTION 1 — HOOK
**Target time: 60–90 seconds**

---

[VISUAL: Start on a black screen or a still of The Tower gameplay. Fade in.]

**NARRATION:**

"If you've been playing The Tower for any length of time, you already know the grind. Wave after wave, run after run — and at some point you start wondering: am I actually getting better? Is my build improving? Or am I just spinning my wheels?

The problem is, The Tower doesn't give you great tools to answer that question. Your battle reports scroll by, after 30 runs your older battle reports drop off, and unless you're manually tracking everything in a spreadsheet — which, let's be honest, nobody wants to do — that data just disappears.

That's exactly the problem TheTowerAnalyzer was built to solve.

In this video I'm going to show you everything the app does and why, if you're serious about your Tower progression, this might be one of the most useful tools you add to your setup."

[VISUAL: Transition to the app running with real data loaded.]

---

## SECTION 2 — APP OVERVIEW
**Target time: 60–90 seconds**

---

**NARRATION:**

"Before we dive into the features, let me give you a quick picture of how The Tower Analyzer actually works — because it's not just a standalone app.

What you're looking at is a web application that runs locally on your PC and connects to your Google Drive. Every time you finish a run in The Tower, you copy your battle report to your clipboard, tap a Tasker widget on your home screen, and pick a category for the run — Farming, Tournament, Milestone, Dissonance, or Event. Tasker sends that data to a make.com pipeline, which processes it and stores it in your Google Drive. TheTowerAnalyzer then pulls that data down and turns it into everything you're about to see.

The setup is a one-time thing — and there's a separate video that walks through every step if you want to get this running yourself. Link is in the description and at the end of this video.

Now let's talk about what's actually in here.

[VISUAL: Point to the top navigation bar showing all six sections.]

The app is organized into six sections across the top: Labs, Upgrades, Modules, Battle Reports, Collectibles, and Meta. Each one covers a different part of the game. Let's go through them."

---

## SECTION 3 — FEATURE DEEP DIVE
**Target time: 6–9 minutes**

> [NOTE: This is the bulk of the video. For each section navigate into it, show the sub-pages,
> and land on the most visually compelling screen with real data loaded. Explain what it shows,
> then hit the 'so what' — why does this matter for gameplay? Don't rush.]

---

### 3A — Battle Reports

[VISUAL: Click Battle Reports in the nav. Show the populated sidebar with 79 reports loaded.]

**NARRATION:**

"Let's start with Battle Reports — the core of the app. After fetching from Google Drive, every run you've submitted shows up in this sidebar. Each entry shows the run type, the wave you reached, your coin total, the cell count, the date, and how the run ended. At a glance you can already see patterns just scrolling through the list.

You can filter by run type at the top — so if you want to look at just your Tournament runs or just your Farming runs in isolation, one click gets you there.

[VISUAL: Click on the T10_Wave7,651 Farming run to open it. Land on the Stats tab.]

Click any report and you get three tabs: Stats, Diagnosis, and Compare. Let's go through each one.

Stats is the full breakdown of the run. Up top you get the headline numbers — coins earned, coins per hour, cells earned, cells per hour, total damage dealt, total enemies killed, and your Tower era. Below that are your records for the run — highest coins per minute, largest wave skip, most coins from a single skip, missile stack, golden combo.

[VISUAL: Scroll down to the Damage Breakdown tables.]

Then below that you get the full damage breakdown — every damage source ranked by contribution and percentage, and every kill source ranked the same way. And at the bottom, your defense and economy breakdown — what blocked damage and where your coins actually came from.

This is the kind of data The Tower just doesn't surface on its own. You finished a run at wave 7,651 — but do you know that Orbs accounted for 1% of your damage while Chain Lightning accounted for 14%? Or that Golden Tower was your second biggest coin source? You do now.

[VISUAL: Click the Diagnosis tab.]

The Diagnosis tab is where it gets really interesting. The app doesn't just show you numbers — it actually analyzes the run and tells you what's going on. Here it's flagged an Orb Layer Collapse at medium severity: Orbs only accounted for 24.9% of kills, well below the 25% threshold, with the primary AoE anchor failing to maintain coverage.

[VISUAL: Scroll down through the Secondary Observations list.]

Below the primary diagnosis there are secondary observations — 26 of them in this run. Kill share breakdown, enemy composition, damage intake split, wall regen architecture, blocking breakdown, regen breakdown. Each one is a specific, actionable insight derived from the data in the battle report about how the run actually played out.

This is the feature that really sets TheTowerAnalyzer apart. It's not just recording your data — it's reading your data and telling you what it means.

[VISUAL: Click the Compare tab. Show the comparison against T10_Wave7,618 — version 3.3.0.]

The Compare tab lets you put any two runs side by side with a delta column showing the difference for every field. Wave, coins, cells, damage, kills, records, every damage source — all compared directly. Here I'm comparing my most recent run against the previous one, and I can immediately see cells earned is up 300, coins per hour is up 5,388, but my Smart Missiles damage is down 39 — which combined with the Orb collapse diagnosis starts to tell a story about what changed between those two runs.

[VISUAL: Show the Version Tracker sub-page briefly.]

And the Version Tracker sub-page maps all your runs to the version of your Tower configuration they were recorded against — so when you make a significant change to your build, you can cleanly separate the data before and after."

---

### 3B — Upgrades: Workshop

[VISUAL: Click Upgrades in the nav. Show the Workshop tab with the full All view.]

**NARRATION:**

"The Upgrades section starts with Workshop — and this is something every Tower player will immediately recognize the value of.

Every single workshop upgrade is listed here across three categories: Attack, Defense, and Utility. For each one you can see the current level, the max, a progress bar showing how far along you are, the cost of the next upgrade, and the status. You can filter to just Attack, just Defense, or just Utility using the tabs at the top.

[VISUAL: Click the Utility tab to show the Utility upgrades — highlight Cash Bonus, Coin/Wave, Enemy Level Skip all maxed.]

In the Utility view you can clearly see which upgrades are maxed out — full green progress bars all the way across — and which ones still have a long way to go. Enemy Attack Level Skip sitting at level 258 out of 999 with 556,000 coins to the next level tells you exactly what kind of investment is ahead.

[VISUAL: Click Hide Maxed — the list collapses from the full Utility view down to just Enemy Attack Level Skip and Enemy Health Level Skip.]

And here's a small feature that I use constantly — Hide Maxed. One click and everything you've already completed disappears. You're left with only the upgrades that still need work. When you've got 48 items across the workshop and most of them are maxed, this cuts through the noise immediately and shows you exactly where your coins should be going.

[VISUAL: Click Workshop+ tab to show the Enhancements unlock progress view.]

The Workshop+ tab is where it gets even more interesting. This shows your Workshop Enhancement unlock progress — the second tier of upgrades that sit on top of the base workshop and get unlocked through a combination of lab research and coin investment.

[VISUAL: Show WorkshopEnhancementsBeforeLab — most cards showing 'Lab Not Yet' with 0 invested.]

This is what it looks like before you've done the required lab research. Most enhancements are showing Lab Not Yet — you can see the investment ranges, you know what's coming, but the path is blocked until the lab catches up.

[VISUAL: Transition to WorkshopEnhancementsAfterLab — showing 49.01B invested, Lab Done on most cards.]

And this is what it looks like after. 49 billion coins invested across 18 items, lab research completed on most enhancements, and now you can see exactly how far into each enhancement's range you've progressed and what remains. The before and after comparison makes the relationship between your lab progress and your workshop investment visible in a way the game itself never shows you."

---

### 3C — Modules

[VISUAL: Click Modules in the nav. Show the Modules sub-page with data.]

**NARRATION:**

"The Modules section has two sub-pages. The main Modules view shows [DESCRIBE WHAT'S DISPLAYED — your module inventory, levels, stats, etc.].

[VISUAL: Navigate to Shard Rate.]

Shard Rate is the one that'll save you a lot of mental math. [DESCRIBE WHAT SHARD RATE SHOWS — expected shard income, time-to-upgrade estimates, etc.]

[VISUAL: Point out a specific data point or calculation.]

[PERSONAL INSIGHT — e.g., 'I used to just guess at whether I had enough shards to hit my next upgrade target. This just tells me.']."

---

### 3D — Labs

[VISUAL: Click Labs in the nav. Show the Labs sub-page.]

**NARRATION:**

"Labs is where it gets interesting for anyone trying to optimize their research. The main Labs view shows [DESCRIBE WHAT'S SHOWN].

[VISUAL: Navigate to Lab Planner.]

Lab Planner is the standout here. [DESCRIBE WHAT LAB PLANNER DOES — planning lab upgrade order, projecting outcomes, etc.]

[VISUAL: Navigate to Cell Income.]

Cell Income tracks [DESCRIBE WHAT THIS SHOWS — lab cell generation rates, income over time, etc.].

[VISUAL: Navigate to Lab Speed.]

And Lab Speed shows [DESCRIBE]. Taken together, these three give you a much clearer picture of how to prioritize your research than just eyeballing it in-game."

---

### 3E — Collectibles

[VISUAL: Click Collectibles in the nav. Show the Relics sub-page.]

**NARRATION:**

"Collectibles covers the two major collect-and-upgrade systems in The Tower. The Relics page shows [DESCRIBE WHAT'S DISPLAYED — your relic inventory, levels, progress, etc.].

[VISUAL: Navigate to Cosmetics.]

Cosmetics is [DESCRIBE — tracking cosmetic progress, completion status, etc.]. [PERSONAL TAKE on whether this is more of a completionist tracker or has gameplay relevance]."

---

### 3F — Meta

[VISUAL: Click Meta in the nav. Show the Currencies sub-page.]

**NARRATION:**

"The Meta section is probably the one that surprises people the most when they first see it. Currencies shows [DESCRIBE — tracking your various currency balances over time, income rates, etc.].

[VISUAL: Navigate to Tier Fit.]

Tier Fit is genuinely one of my favorite things in this app. [DESCRIBE WHAT TIER FIT DOES — evaluating how well your current build fits your tier, identifying mismatches, etc.]

[VISUAL: Navigate to Battle Conditions.]

And Battle Conditions shows [DESCRIBE — what modifiers or conditions were active during your runs, how they affected performance, etc.].

[PERSONAL INSIGHT — What does having this meta-level view change about how you approach the game?]"

---

## SECTION 4 — REAL-WORLD VALUE
**Target time: 60–90 seconds**

---

[VISUAL: Return to the chart or screen that best illustrates the story you're about to tell.]

**NARRATION:**

"Let me give you a concrete example of what this actually looks like in practice.

[TELL A SPECIFIC STORY — something like: 'A few weeks ago I was convinced my module setup was optimal. I'd been running the same configuration for a while and I felt like my waves had plateaued. I pulled up TheTowerAnalyzer, looked at my trend data, and compared my last 20 runs against the 20 before that. What I found was that my average wave had actually dropped by about [X] waves since I made a workshop change I thought was an upgrade.']

[VISUAL: Show the actual data that illustrates this story if you have it.]

Without this data, I probably would have kept running that configuration for another month. Instead I had a clear signal to go back and re-evaluate.

That's what TheTowerAnalyzer is really about — it turns your gut feelings into something you can actually verify."

---

## SECTION 5 — ONE MORE THING: THE MCP SERVER
**Target time: 90–120 seconds**

---

[VISUAL: Open Claude Desktop. Have a conversation window ready.]

**NARRATION:**

"Before I wrap up, there's one more thing I want to show you — and this one is a bit different.

TheTowerAnalyzer ships with an MCP server. If you're not familiar with MCP, it's a protocol that lets AI assistants like Claude connect directly to external tools and data sources. What that means here is that Claude can query your TheTowerAnalyzer data directly — your actual runs, your actual workshop levels, your actual lab state — and use all of it as context when you ask it questions.

[VISUAL: Type a question into Claude Desktop — something like 'Based on my recent farming runs, what does my damage breakdown tell you about my build?' or 'What should I prioritize in my lab this week?']

So instead of asking Claude a generic question about The Tower and getting a generic answer, you can ask it about your Tower — and it answers with specifics pulled from your data in real time.

[VISUAL: Claude responds with specific insights drawn from the user's actual data — wave numbers, lab levels, module names, diagnosis findings — not generic advice.]

This is powered by 16 tools the MCP server exposes — everything from recent run summaries and diagnosis data, to your full workshop and lab state, to shard rates, cell income, tier personal bests, tournament history, and more. Claude can pull exactly what it needs for a given question without flooding its context with data it doesn't need.

[VISUAL: Show one more example — something like asking Claude to help draft a version history entry based on pending changes.]

There's even a workflow for tracking changes to your Tower configuration over time and having Claude help you draft version history entries — so you always know exactly what changed between your builds and when.

Setting up the MCP server requires a bit of Node.js configuration, so I've dedicated a separate video to it — link is in the description. If you're already comfortable with developer tools, it's not a long setup. And if you're not, the main TheTowerAnalyzer pipeline works completely without it — the MCP server is an optional layer on top."

---

## SECTION 6 — CALL TO ACTION
**Target time: 30–45 seconds**

---

[VISUAL: Return to TheTowerAnalyzer in the browser.]

**NARRATION:**

"If you want to get TheTowerAnalyzer running for yourself, everything you need is in the description below — including a link to the full setup video that walks through the installation, the Google Drive configuration, make.com, and Tasker step by step.

There's also a Google Doc reference guide with screenshots for every step of the setup, which I'd recommend bookmarking — it's a lot easier than scrubbing through video when you're in the middle of configuring something.

[VISUAL: On-screen cards pointing to Video 2 and Video 3.]

If you found this useful, a like helps other Tower players find it. And if you've got questions or you want to show off your own data once you get it set up, drop it in the comments — I'd love to see it."

[VISUAL: Fade out or cut to end screen with Video 2 and Video 3 cards.]

---

## PRODUCTION NOTES

- **Screen recording:** Record at 1920x1080 minimum. Make sure the app has real, populated data before recording — empty screens kill the energy of a showcase video.
- **Real data matters:** The more runs you have logged, the more compelling the charts will look. Aim for at least 50–100 runs in the dataset before recording.
- **Cursor:** Use a cursor highlight tool so viewers can easily follow where you're pointing on screen.
- **Pacing:** The script reads faster than you'll actually present it once you're showing things on screen. Don't rush the feature sections — let the data breathe.
- **Music:** Low-key background music during feature sections helps maintain energy. Cut it for the hook and CTA so those land with more weight.
- **Chapters (YouTube):** Even for a shorter video, adding 2–3 timestamp chapters in the description improves viewer retention and searchability.

### Suggested YouTube Description Template

```
TheTowerAnalyzer automatically tracks and analyzes your battle report data so you can actually 
see your progression over time — no manual spreadsheets required.

In this video I walk through every feature of the app and show you what your Tower data 
looks like when it's all in one place.

🔧 Full setup guide (install, Google Drive, make.com, Tasker): [LINK TO VIDEO 2]
🤖 MCP server setup (Claude AI integration): [LINK TO VIDEO 3]
📄 Step-by-step reference doc with screenshots: [LINK TO GOOGLE DOC]
📦 Get TheTowerAnalyzer: https://github.com/draukadin/TheTowerAnlyzer/releases/tag/v0.0.1-beta2

CHAPTERS:
0:00 — Introduction
[X:XX] — How TheTowerAnalyzer works
[X:XX] — Battle Reports: Stats, Diagnosis & Compare
[X:XX] — Upgrades: Workshop & Workshop+
[X:XX] — Modules
[X:XX] — Labs
[X:XX] — Collectibles
[X:XX] — Meta
[X:XX] — Real-world example
[X:XX] — MCP Server: Claude AI integration
[X:XX] — How to get started
```

---

*End of Video 1 Script*
