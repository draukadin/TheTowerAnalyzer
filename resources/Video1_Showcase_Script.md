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

In this video I'm going to show you everything the app does and why, if you're serious about tracking your Tower performance, this might be one of the most useful tools you add to your setup."

[NOTE: Use "The Tower Analyzer" (spaced) consistently throughout the recording — this matches the app's own header.]

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

"Let's start with Battle Reports — the core of the app. After fetching from Google Drive, every run you've submitted shows up in this sidebar.  They have been downloaded from your google drive, parsed and stored in a local database.  Each entry shows the run type, the wave you reached, your coin total, the cell count, the date, and how the run ended. At a glance you can already see patterns just scrolling through the list.

You can filter by run type at the top — so if you want to look at just your Tournament runs or just your Farming runs in isolation, one click gets you there.

[VISUAL: Click on the T10_Wave7,615 Farming run to open it. Land on the Stats tab.]

Click any report and you get three tabs: Stats, Diagnosis, and Compare. Let's go through each one.

Stats is the full breakdown of the run. Up top you get the headline numbers — coins earned, coins per hour, cells earned, cells per hour, total damage dealt, total enemies killed, and your Tower era. Below that are your records for the run — highest coins per minute, largest wave skip, most coins from a single skip, missile stack, golden combo.

[VISUAL: Scroll down to the Damage Breakdown tables.]

Then below that you get the full damage breakdown — every damage source ranked by contribution and percentage, and every kill source ranked the same way. And at the bottom, your defense and economy breakdown — what blocked damage and where your coins actually came from.

This is the kind of data The Tower just doesn't surface on its own. You finished a run at wave 7,615 — but do you know that Orbs accounted for 69% of your damage while Chain Lightning accounted for 20%? Or that Golden Tower was your second biggest coin source? You do now.

[VISUAL: Click the Diagnosis tab.]

The Diagnosis tab is where it gets really interesting. The app doesn't just show you numbers — it actually analyzes the run and tells you what's going on. Here it's flagged an Orb Layer Collapse at medium confidence: Orbs only accounted for 23.8% of kills, below the 25% threshold, with the primary AoE anchor failing to maintain coverage.

[VISUAL: Scroll down through the Secondary Observations list.]

Below the primary diagnosis there are secondary observations — 26 of them in this run. Kill share breakdown, enemy composition, damage intake split, wall regen architecture, blocking breakdown, regen breakdown. Each one is a specific, actionable insight derived from the data in the battle report about how the run actually played out.

This is the feature that really sets TheTowerAnalyzer apart. It's not just recording your data — it's reading your data and telling you what it means.  However, since a Battle Report isn't a time series report of when things happened there are some limitation to what can be derived from the data and the accuracy of the observations.

[VISUAL: Click the Compare tab. Show the comparison against T10_Wave7,618 — version 3.3.0.]

The Compare tab lets you put any two runs side by side with a delta column showing the difference for every field. Wave, coins, cells, damage, kills, records, every damage source — all compared directly. Here I'm comparing my most recent run against the previous one, and I can immediately see cells earned is down 880, coins per hour is up 390B and in both runs I was killed by a Fast — If we go down to Damage Taken we start to get a clue that maybe the Diagnosis of the Orb Collapse wasn't correct.  In the most recent run 1.42q damage taken.  In the previous run on version 3.3.0, 2.07q damage taken.  650T less despite a 3 wave difference.  This means the tower got hit really hard after the wall failed and didn't have a chance to recover.  If we look down a bit further we see that Life Steal is 0.0 while in the previous run life-steal healed 364.81T.  Tower health regen is also telling the same story.  0.0 healing in the latest run and 6.06T in the previous run.  This is actually starting to look like my tower failed because the boss took out the wall, took out most of the tower health and a fast got the final blow.  Since I don't watch my runs all the time I did not see what actually killed me.  But by comparing reports I can see that my latest upgrades have started preventing the chipping from multiple enemies and has revealed that I have a health pool issue since I am getting "killed by bosses".  That leads us nicely to the version tracker where we can verify if the above hypothesis is over the target.

[VISUAL: Show the Version Tracker sub-page briefly.]

And the Version Tracker sub-page maps all your runs to the version of your Tower configuration they were recorded against — so when you make a significant change to your build, you can cleanly separate the data before and after.

We were looking at versions 3.3.0 vs 3.3.5. and we can see what changed between the two versions.  A large improvement to EALS, Attack Speed lab up a level, Recovery Package chance doesn't really matter since I don't use GComp for farming runs, but I do for tournaments which is why that lab is going.  Attack Speed+ got 4 more levels back on version 3.3.3.  Chrono Field duration got another second which is getting me closer to pCF.  The Cannon and Core modules picked up a level.  The only two or three things that I can point to that indicate my tower's control of normal enemies have improved are the Attack Speed+ and Attack Speed lab improvements the EALS and Chrono Field.  

Now you are probably thinking the version tracking is a pain because you have to manually keep track of what changed.  If we jump over to the Labs section and mark that we have gotten EHLS to 20, Attack Speed to 72 and Chrono Field Duration to 22.  And in the Workshop+ we have enough coins to buy another level of Health+ and Health Regen+  And in the workshop lets buy 2 levels of EHLS.  Now go back to the Version Tracker.

[VISUAL: Point to the 'Process Pending Changes (3)' button in the top right of the Version Tracker page.]

That version history doesn't require any manual tracking either. See this button — Process Pending Changes? The app monitors your Tower state in the background and accumulates a running list of every change it detects when you update something in the labs, workshop, bots, modules, or guardian chips. When you've made a meaningful set of upgrades and you're ready to record them as a new version, you click this.

[VISUAL: Click Process Pending Changes. The modal opens showing three separate LAB Damage rows — 0→1, 1→2, 2→3.]

The modal shows every individual change it caught. Here it detected that I leveled up three labs the two Defense+ enhancements and the two levels of EHLS.  Since it does track every individual change EHLS has two entries.

[VISUAL: Consolidate the two rows into a single row showing 250→252.]

Before saving you can consolidate those down to a single row — EHLS went from 250 to 252, which is the only thing that matters for the history. Give it a version number, choose whether this is a Patch, Minor, or Major change depending on how significant the upgrades are, and click Add Version.
Since I haven't done anything that is a significant/structural upgrade like pCF or getting my DW/BH/GT in sync this is going to be a patch. And we go to 3.3.6 since the most current version is 3.3.5.

[VISUAL: Modal closes. Version Tracker now shows the new version entry in the list.]

The Google Sheet version updates automatically, the new entry is recorded in your history, and from this point every run you submit via tasker is tagged with this version. That's what makes the Compare tab meaningful over time — you always know exactly what build a given run was recorded against, and you can see cleanly what change and analyze if it helped or not."

---

### 3B — Upgrades: Workshop

[VISUAL: Click Upgrades in the nav. Show the Workshop tab with the full All view.]

**NARRATION:**

"The Upgrades section starts with Workshop — and this is something every Tower player will immediately recognize the value of.

Every single workshop upgrade is listed here across three categories: Attack, Defense, and Utility. For each one you can see the current level, the max, a progress bar showing how far along you are, the cost of the next upgrade, and the status. You can filter to just Attack, just Defense, or just Utility using the tabs at the top.

[VISUAL: Click the Utility tab to show the Utility upgrades — highlight Cash Bonus, Coin/Wave, Enemy Level Skip all maxed.]

In the Utility view you can clearly see which upgrades are maxed out — full green progress bars all the way across — and which ones still have a long way to go. Enemy Attack Level Skip sitting at level 258 out of 699 with 556,000 coins to the next level tells you exactly what kind of investment is ahead.

[VISUAL: Click Hide Maxed — the list collapses from the full Utility view down to just Enemy Attack Level Skip and Enemy Health Level Skip.]

And here's a small feature that I use constantly — Hide Maxed. One click and everything you've already completed disappears. You're left with only the upgrades that still need work. When you've got 48 items across the workshop and most of them are maxed, this cuts through the noise immediately and shows you exactly where your coins should be going.

[VISUAL: Click Workshop+ tab to show the Enhancements unlock progress view.]

The Workshop+ tab is where it gets even more interesting. This shows your Workshop Enhancement unlock progress — the second tier of upgrades that sit on top of the base workshop and get unlocked through a combination of lab research and coin investment.

[VISUAL: Show WorkshopEnhancementsBeforeLab — most cards showing 'Lab Not Yet' with 0 invested.]

This is what it looks like before you've done the required lab research. Most enhancements are showing Lab Not Yet — you can see the investment ranges, you know what's coming, but the path is blocked until the lab catches up.

[VISUAL: Transition to WorkshopEnhancementsAfterLab — showing 49.01B invested, Lab Done on most cards.]

And this is what it looks like after. 49 billion coins invested across 18 items, lab research completed on most enhancements, and now you can see exactly how far into each enhancement's range you've progressed and what remains. The before and after comparison makes the relationship between your lab progress and your workshop investment visible in a way the game itself never shows you."

---

### 3C — Upgrades: Ultimate Weapons

[VISUAL: Click Upgrades in the nav. Navigate to UW Tracker. Show the full card grid with all 9 UWs.]

**NARRATION:**

"Still in the Upgrades section — the UW Tracker. If you've ever tried to plan your stone investment across multiple Ultimate Weapons at the same time, you know how quickly that math gets out of hand.

Every UW gets its own card showing every stat — current level, your target level, stones invested so far, and stones remaining to hit your target. Up at the top you get the summary: 7 out of 9 UWs unlocked, 8,546 total stones invested, 4,462 stones left to reach all planned targets, and 346,676 to max everything.

[VISUAL: Point to the Chrono Field card showing Speed at level 80 with target 80, and the stones invested vs stones to max.]

The target system is what makes this actually useful. You set where you want each stat to be, and the app tells you exactly what it costs to get there — not just for one UW but across all of them simultaneously. Instead of juggling this in your head or a separate spreadsheet, you have one view that tells you your total stone budget."

---

### 3D — Modules

[VISUAL: Click Modules in the nav. Show the Modules sub-page with data.]

**NARRATION:**

"Modules tracks your full module inventory — every module you own, its level, stats, and where it fits in your build. I'll keep this one brief since the real value in the Modules section is on the next page.

[VISUAL: Navigate to Shard Rate.]

Shard Rate takes your actual battle report data — the same reports you just saw in the Battle Reports section — and calculates your real shard earning rate per module type. From that it projects exactly how long it'll take to reach any target level you set. No more guessing whether you're a week or three months away from your next module upgrade. The data from your runs tells you."

---

### 3E — Labs

[VISUAL: Click Labs in the nav. Show the Labs sub-page briefly.]

**NARRATION:**

"The Labs section is where the app starts doing some genuinely heavy lifting on your behalf. There are four sub-pages — Labs, Lab Planner, Cell Income, and Lab Speed. I'll spend a bit more time here because these are the ones I find myself coming back to the most.

[VISUAL: Navigate to Cell Income Tracker. Show the table with 49 runs analyzed over 30 days.]

Cell Income Tracker pulls from your battle reports and gives you a clear picture of your actual cell earning rate — not a theoretical number, your real rate from real runs. Over the last 30 days: 49 runs analyzed, 5.76K average cells per hour, 2.71 million total cells, 55.22K per run. Every run is listed in the table with its date, tier, wave, cells earned, cells per hour, and duration, plus a relative bar so you can immediately spot which runs were outliers.

[VISUAL: Navigate to Lab Speed Affordability.]

Lab Speed Affordability takes that cell income data and answers a question that comes up constantly: what speed multipliers can I actually afford to run without burning through my cell reserves?  The reason this matters is that cells are the one resource the game gives you no real visibility into. You know you're earning them, but you have no reliable way to project when you can increase your lab speed multipliers.

[VISUAL: Point to the Dead Time section showing 51.3% dead time.]

It starts with dead time — the percentage of calendar time when you aren't actively earning cells. Right now 51.3% of calendar time is dead time, which means farming is not running as efficiently as it could.

[VISUAL: Point to the Optimal Combination section showing x3/x3/x3/x2/x2.]

Below that it shows the optimal speed combination across all five lab slots — the highest multipliers you can sustain given your effective cell income. Right now that's x3/x3/x3/x2/x2, costing 2.73K cells per hour with a net of 95.15 cells per hour after the cost. It also shows the farming speeds — what the slots are set to while you're actively running — and flags that the farming configuration draws down reserves at 2.42K cells per hour above what you're earning, so if you enter your cells on hand it'll estimate when you'd run dry.

[VISUAL: Navigate to Lab Planner. Show the five-slot view.]

And Lab Planner is exactly what it sounds like. Each of your five lab slots gets its own column showing the full research queue — what lab, what levels, how much it costs, how long it takes, and the coins per day burn rate for that slot. Total time, total cost, and coins per day are summarized at the top of each slot.

[VISUAL: Point to Slot 3 with its long queue of 9 labs.]

Slot 3 has 9 labs queued taking 115 days at 13 hours. Slot 1 is 120 days. All five slots together are burning 106.60 billion coins per day. That's the kind of forward visibility that lets you actually plan around your lab investment rather than just queuing the next thing and hoping for the best."

---

### 3F — Collectibles

[VISUAL: Click Collectibles in the nav. Show the Relics sub-page.]

**NARRATION:**

"Collectibles is a lighter section but worth a quick mention, especially for anyone who's been playing long enough to accumulate relics.

The Relics page tracks every relic you own — which ones you have, their current levels, and the bonuses they provide. As the game introduces new relics over time, you can add them to the app. In the future I'll be exploring options for a way to get new relics added with just a button click.

[VISUAL: Navigate to Cosmetics.]

Cosmetics works the same way — tracks your cosmetic collection with bonus rates per item. If you're a completionist this is satisfying to have. If you're purely performance-focused, you'll mostly care about the bonus values rather than the collection side.  Similar to relics, I'll be exploring making updating this with new cosmetics with just a button click."

---

### 3G — Meta

[VISUAL: Click Meta in the nav. Show the Currencies sub-page.]

**NARRATION:**

"Last section — Meta. Three sub-pages, all quick hits.

Currencies tracks your gem, coin, stone, and shard balances. Each time you update your balances in the app and save, it records a timestamped snapshot — useful for tracking shard accumulation over time, which feeds into the shard rate projections under Modules to give you an estimate of when you'll have enough to reach your target module level.

[VISUAL: Navigate to Tier Fit.]

Tier personal best tracks your personal bests per tier for regular tiers and your dissonance runs.  The boost that is applied to tournaments is also calculated and shown here.

[VISUAL: Navigate to Battle Conditions.]

And Battle Conditions logs the modifiers that were active during tournaments — useful for understanding why a particular tournament run performed differently.  You can use this to figure out which battle conditions are a hard counter to your tower and then explore what labs or UW or module upgrades/sub-stats will help you mitigate that battle condition."

---

## SECTION 4 — REAL-WORLD VALUE
**Target time: 60–90 seconds**

---

[VISUAL: Return to the chart or screen that best illustrates the story you're about to tell.]

**NARRATION:**

"Let me give you a concrete example of what this actually looks like in practice.

I've been trying to figure out why I am going back and forth between Platinum and Champion.  By correlating the battle reports to the battle conditions in effect I was able to determine that Armored Enemies was giving me a hard time.  This prompted me to start devoting some development to Death Wave and the Armor Stripping lab.  It still needs more development, but now I know what I need to improve and I understand why I got 440 waves when before I had gotten 606 waves.  I still haven't solved the Armored enemies problem but the things I have been doing got a new personal best in Champion of 726 waves.

[VISUAL: Show the actual data that illustrates this story if you have it.]

Without this data, I probably would have kept researching labs that would only be helping my tower perform under optimal battle conditions instead of making improvements to the areas where my tower has been really under-performing.

That's what The Tower Analyzer is really about — it turns your gut feelings into something you can actually verify or highlight facets about your tower that are being neglected."

---

## SECTION 5 — ONE MORE THING: THE MCP SERVER
**Target time: 90–120 seconds**

---

[VISUAL: Open Claude Desktop. Have a conversation window ready.]

**NARRATION:**

"Before I wrap up, there's one more thing I want to show you — and this one is a bit different.

The Tower Analyzer ships with an MCP server. If you're not familiar with MCP, it's a protocol that lets AI assistants like Claude connect directly to external tools and data sources. This is powered by 16 tools the MCP server exposes — everything from recent run summaries and diagnosis data, to your full workshop and lab state, to shard rates, cell income, tier personal bests, tournament history, and more. Claude can pull exactly what it needs for a given question without flooding its context with data it doesn't need.

[VISUAL: Type a question into Claude Desktop — something like 'Based on my recent farming runs, what does my damage breakdown tell you about my build?' or 'What should I prioritize in my lab this week?']

So instead of asking Claude a generic question about The Tower and getting a generic answer, you can ask it about your Tower — and it answers with specifics pulled from your data in real time. The main The Tower Analyzer pipeline works completely without it — the MCP server is an optional layer on top.

I'm also working on creating skills that will give the AI agent specific expertise in how certain facets of the game work — modules, ultimate weapons, labs, bots, guardian chips, and more. That way instead of providing broadly correct evaluations based on general model training, it will be able to provide expert advice tailored to your tower. If you're interested in contributing, I'd be happy to take your input and incorporate it into an AI skill. It doesn't require any coding — it's just a text file with markdown formatting that tells the agent what the rules of the game are and how different facets interact. These rules and opinions help it give accurate, personalized advice because the skills define what optimal play looks like, and the MCP server gives it your tower's current state."

---

## SECTION 6 — CALL TO ACTION
**Target time: 30–45 seconds**

---

[VISUAL: Return to TheTowerAnalyzer in the browser.]

**NARRATION:**

"If you want to get TheTowerAnalyzer running for yourself, everything you need is in the description below — including a link to the full setup video that walks through the installation, the Google Drive configuration, make.com, and Tasker step by step.

There's also a Google Doc reference guide with screenshots for every step of the setup, which I'd recommend bookmarking — it's a lot easier than scrubbing through video when you're in the middle of configuring something.

[VISUAL: On-screen cards pointing to Video 2 and Video 3.]

If you found this useful, a like helps other Tower players find it. If you find a bug or have a feature request, the link to the GitHub issues page is in the description. And if you've got questions or you want to show off your own data once you get it set up, drop it in the comments — I'd love to see it."  

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
The Tower Analyzer automatically tracks and analyzes your battle report data so you can actually 
see your progression over time — no manual spreadsheets required.

In this video I walk through every feature of the app and show you what your Tower data 
looks like when it's all in one place.

🔧 Full setup guide (install, Google Drive, make.com, Tasker): https://youtu.be/v3OYoIjikTg
🤖 MCP server setup (Claude AI integration): TBD
📄 Step-by-step reference doc with screenshots: https://docs.google.com/document/d/13f9vEJNlJCbV6_4pJ_Q6r4jHB-ok3OSPMVXlYRhQ7zk/copy
📦 Get TheTowerAnalyzer: https://github.com/draukadin/TheTowerAnlyzer/releases/
🐛 Report an issue or request a feature: https://github.com/draukadin/TheTowerAnlyzer/issues

CHAPTERS:
0:00 — Introduction
[0:44] — How The Tower Analyzer works
[2:03] — Battle Reports: Stats, Diagnosis & Compare
[8:45] — Version Tracker & Pending Changes
[11:25] — Upgrades: Workshop & Workshop+
[14:55] — Upgrades: Ultimate Weapons
[16:30] — Modules & Shard Rate
[18:17] — Labs: Cell Income, Lab Speed & Lab Planner
[21:57] — Collectibles - Relics & Cosmetics
[24:11] — Meta - Currencies, Tier Personal Best & Battle Conditions 
[26:42] — MCP Server: Claude AI integration
[31:30] — How to get started
```

---

*End of Video 1 Script*
