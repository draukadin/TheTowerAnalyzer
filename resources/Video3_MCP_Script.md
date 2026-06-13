# TheTowerAnalyzer — Video 3: MCP Server Setup Script
**Version:** 1.0
**Last Updated:** 2026-06-09
**Estimated Runtime:** 15–20 minutes
**Tone:** Calm and methodical for setup sections. More energetic and conversational for the demo section — this is where the payoff lives.
**Prerequisite:** Viewer has completed Video 2 setup. TheTowerAnalyzer is running at localhost:8080 with populated data.

> **HOW TO USE THIS SCRIPT**
> Lines marked [VISUAL] are screen recording cues.
> Lines marked [NOTE] are reminders to the presenter — do not read aloud.
> Text in [BRACKETS] are placeholders to fill in before recording.
> Section time estimates are targets, not hard limits.

---

## INTRO
**Target time: 90 seconds**

---

[VISUAL: Split screen or cut between TheTowerAnalyzer running in a browser and Claude Desktop open on the other side.]

**NARRATION:**

"This is Video 3 in the TheTowerAnalyzer series, and it covers the MCP server — the piece that lets Claude AI talk directly to your Tower data.

If you've watched the showcase video, you saw a quick demo of what this looks like in action. This video is the setup guide: how to get Node.js configured, how to locate and set up the server file from the GitHub repository, and how to wire it into Claude Desktop so Claude has live access to your TheTowerAnalyzer data.

Before we start, two things worth saying upfront.

First — this is an optional feature. TheTowerAnalyzer works completely without it. If you just want the data tracking and analysis pipeline, Videos 1 and 2 are everything you need. This is an additional layer for people who want to take it further.

Second — this setup is slightly more technical than the main pipeline. We're going to be working in a terminal and editing a JSON config file. If that sounds unfamiliar, don't worry — I'll go through every step slowly and show exactly what you're typing and why. There's nothing here that requires programming experience.

Alright — let's get into it."

[VISUAL: Cut to Chapter 1 title card.]

---

## CHAPTER 1 — Prerequisites & What We're Building
**Target time: 3–4 minutes**

---

[VISUAL: Chapter 1 title card, then a simple diagram or text list showing the architecture.]

**NARRATION:**

"Before we touch anything, let me explain what the MCP server actually is and how it fits into the picture.

TheTowerAnalyzer is a Spring Boot web application running locally on your PC at localhost:8080. It has a full API that exposes all your Tower data — your runs, your workshop, your labs, your modules, everything.

The MCP server is a small Node.js application, bundled in the same GitHub repository, that sits between Claude Desktop and the TheTowerAnalyzer API. When you ask Claude a question, Claude calls the MCP server, the MCP server calls the TheTowerAnalyzer API, distills the response down to what's relevant, and hands it back to Claude as context.

[VISUAL: Simple flow diagram: Claude Desktop → MCP Server (Node.js) → TheTowerAnalyzer API (localhost:8080) → Your Data]

The distillation step is important — the MCP server doesn't just dump raw API responses at Claude. Each tool is designed to return a focused, compact version of the data, and Claude can request only the sections it needs for a given question. That keeps the context window clean and the responses fast.

The MCP server exposes 16 tools covering every major part of the game:

[VISUAL: Show a clean list of tool categories as they're read.]

Currency balances. Shard earning rates and time-to-milestone projections. Cell income. Lab speed affordability analysis. Recent run summaries with diagnosis. Your full tower state — UWs, modules, relics. Lab levels and targets. Workshop and Workshop+ state. Cards, bots, and guardian. Tournament history. Tier personal bests. Version history and pending configuration changes. Cosmetics.

That's a complete picture of your Tower — available to Claude as live context whenever you need it.

For this setup you'll need:

[VISUAL: Show prerequisites list.]

One — TheTowerAnalyzer installed and running. If you haven't done Video 2 yet, do that first.

Two — Node.js installed on your PC. We'll cover this in Chapter 2.

Three — Claude Desktop installed and logged in. The link is in the description.

Let's start."

[VISUAL: Cut to Chapter 2 title card.]

---

## CHAPTER 2 — Installing Node.js
**Target time: 3–4 minutes**

---

[VISUAL: Chapter 2 title card, then open a browser.]

**NARRATION:**

"Chapter 2 — Node.js. The MCP server is a Node.js application, so we need Node.js installed before we can run it.

[VISUAL: Navigate to https://nodejs.org]

Go to nodejs.org. You'll see two download options — LTS and Current. Download the LTS version — that stands for Long Term Support, and it's the stable version recommended for most use cases.

[VISUAL: Download the LTS installer. Run it.]

Run the installer. The defaults are fine — click through and let it complete.

[VISUAL: Installation completes. Open a terminal — Windows Terminal, PowerShell, or Command Prompt.]

Once it's installed, let's verify it worked. Open a terminal — Windows Terminal, PowerShell, or Command Prompt all work.

[VISUAL: Type 'node --version' and press Enter.]

Type: node --version

And press Enter. You should see a version number — something like v20 or v22. As long as you see a version number, Node.js is installed correctly.

[VISUAL: Also run 'npm --version'.]

Also run: npm --version

npm is the Node package manager — it installs the dependencies the MCP server needs. You should see a version number there too.

[NOTE: If either command returns 'not recognized' or similar, Node.js didn't install correctly or needs a system restart to be recognized. Tell viewers to close and reopen the terminal first, then restart the PC if it still doesn't work.]

If both commands return version numbers, Chapter 2 is done."

[VISUAL: Chapter 3 title card.]

---

## CHAPTER 3 — Getting and Configuring the MCP Server
**Target time: 4–5 minutes**

---

[VISUAL: Chapter 3 title card, then open a browser and navigate to the GitHub releases page.]

**NARRATION:**

"Chapter 3 — getting the MCP server file and installing its dependencies.

[VISUAL: Navigate to https://github.com/draukadin/TheTowerAnlyzer/releases/tag/v0.0.1-beta2]

Go back to the TheTowerAnalyzer GitHub releases page. The MCP server is bundled in the repository alongside the main app.

[VISUAL: Point to where server.js is located — either in the release assets or in the repo itself.]

[NOTE: Clarify here exactly where server.js ships — is it in the release assets as a separate download, or does the viewer need to clone/download the full repository to get it? Update this section accordingly before recording.]

Download or locate server.js. We're going to put it in a dedicated folder so it's easy to find and reference later.

[VISUAL: Create a folder — e.g., C:\Users\[username]\TheTowerAnalyzer-MCP — and place server.js inside it.]

Create a folder somewhere convenient — I'll use C:\Users\[YOUR USERNAME]\TheTowerAnalyzer-MCP. Move server.js into that folder."

---

**Installing Dependencies**

[VISUAL: Open a terminal and navigate to the folder containing server.js.]

**NARRATION:**

"Now we need to install the Node.js dependencies the server requires. Open your terminal and navigate to the folder where you put server.js.

[VISUAL: Type 'cd C:\Users\[username]\TheTowerAnalyzer-MCP' and press Enter.]

Type: cd followed by the path to your folder. Press Enter.

[VISUAL: Type 'npm init -y' and press Enter.]

Now type: npm init -y

This creates a package.json file which Node.js needs to manage the project. The -y flag accepts all the defaults so you don't have to answer any prompts.

[VISUAL: npm init completes. A package.json file appears in the folder.]

[VISUAL: Type 'npm install @modelcontextprotocol/sdk' and press Enter.]

Now install the MCP SDK — the library the server uses to communicate with Claude Desktop. Type:

npm install @modelcontextprotocol/sdk

[VISUAL: npm installs the package. A node_modules folder appears.]

Wait for that to finish. You'll see a node_modules folder appear in your directory — that's normal, that's where the installed packages live.

[VISUAL: Open package.json in Notepad or a text editor.]

One more step before we leave this folder. Open package.json in Notepad or any text editor. Find the line that says:

'type': 'commonjs'   — or if it's not there, we need to add it.

[NOTE: The server.js uses ES module syntax (import statements). Verify whether 'type': 'module' needs to be added to package.json for it to run correctly — update this section based on what actually works in your environment before recording.]

Add or confirm the line: 'type': 'module'

Save and close package.json.

[VISUAL: Chapter 4 title card.]

---

## CHAPTER 4 — Configuring Claude Desktop
**Target time: 4–5 minutes**

---

[VISUAL: Chapter 4 title card.]

**NARRATION:**

"Chapter 4 — wiring the MCP server into Claude Desktop. This is where we tell Claude Desktop that the tower-analyzer MCP server exists and how to start it.

If you don't have Claude Desktop installed yet, download it from claude.ai/download — link is in the description. Install it and log in with your Anthropic account before continuing."

---

**Locating the Claude Desktop Config File**

[VISUAL: Open File Explorer. Navigate to %appdata%\Claude.]

**NARRATION:**

"Claude Desktop stores its MCP configuration in a JSON file. Open File Explorer and type %appdata%\Claude in the address bar, then press Enter.

[VISUAL: The Claude folder opens. Look for claude_desktop_config.json.]

You should see a file called claude_desktop_config.json. If it doesn't exist yet, we'll create it in a moment.

[VISUAL: Right-click the file and open with Notepad — or create a new file with that name if it doesn't exist.]

Open it with Notepad or any plain text editor."

---

**Adding the MCP Server Configuration**

[VISUAL: claude_desktop_config.json open in Notepad.]

**NARRATION:**

"The config file uses JSON format. We need to add an entry that tells Claude Desktop the name of our MCP server, how to start it, and where to find the server.js file.

[VISUAL: Show the config being typed or already typed on screen.]

If the file is empty or new, enter the following. If it already has content, add the mcpServers section carefully — JSON is sensitive to missing commas and brackets.

[VISUAL: Display the config block clearly on screen — large enough to read:]

{
  \"mcpServers\": {
    \"tower-analyzer\": {
      \"command\": \"node\",
      \"args\": [\"C:\\\\Users\\\\[YOUR USERNAME]\\\\TheTowerAnalyzer-MCP\\\\server.js\"]
    }
  }
}

[NOTE: Replace [YOUR USERNAME] with the actual Windows username in your demo. Double-check the path separators — in JSON on Windows, backslashes need to be doubled like C:\\\\Users\\\\...]

The key things here: the name tower-analyzer matches what the server registers itself as. The command is node — that's the Node.js runtime. And the args is the full path to your server.js file.

[VISUAL: Config is saved.]

Make sure the path matches exactly where you put server.js. Save the file."

---

**Restarting Claude Desktop**

[VISUAL: Close Claude Desktop completely — right-click the system tray icon and quit, don't just close the window.]

**NARRATION:**

"Now we need to fully restart Claude Desktop so it picks up the new configuration. Don't just close the window — find the Claude icon in your system tray, right-click it, and choose Quit.

[VISUAL: Claude Desktop is quit. Relaunch it from the Start Menu or desktop shortcut.]

Now relaunch it. When it starts up, it will read the config file and attempt to connect to the MCP server.

[VISUAL: Claude Desktop open. Look for the MCP tools indicator — typically a hammer icon or tools count in the interface.]

Look for the MCP tools indicator in the interface — Claude Desktop shows connected tools, and you should see the tower-analyzer tools listed. If you see them, the connection is working.

[NOTE: Describe exactly where in the Claude Desktop UI the connected tools appear — the hammer icon in the bottom left of the conversation, or the tools list in settings — update before recording based on current Claude Desktop UI.]"

---

**Chapter 4 Verification:**

**NARRATION:**

"Chapter 4 is complete when Claude Desktop is running and the tower-analyzer MCP tools are showing as connected. If they're not showing, the most common cause is a path error in the config file — double-check that the path to server.js is exact and that backslashes are doubled.

Also make sure TheTowerAnalyzer itself is running at localhost:8080 before testing — the MCP server calls the API, so if the app isn't running, the tools will return errors even if the connection to Claude Desktop is working."

[VISUAL: Chapter 5 title card.]

---

## CHAPTER 5 — Live Demo
**Target time: 4–5 minutes**

---

[VISUAL: Chapter 5 title card. Then: TheTowerAnalyzer running in one window, Claude Desktop open and ready in another.]

**NARRATION:**

"Chapter 5 — the demo. Let's actually use this and see what it can do.

Make sure TheTowerAnalyzer is running at localhost:8080 before any of these queries — Claude needs the API to be live to pull data.

I'm going to walk through a few different types of questions to show you the range of what's possible."

---

**Demo Query 1 — Recent Run Analysis**

[VISUAL: In Claude Desktop, type: 'Look at my last three farming runs and tell me if there are any patterns in the diagnosis findings.']

**NARRATION:**

"Let's start with something practical. I'll ask Claude to look at my recent farming runs and identify patterns in the diagnosis.

[VISUAL: Claude calls get_recent_runs with sections including diagnosis. Response comes back with specific findings.]

Claude pulls the last three farming runs including their diagnosis sections and looks for common threads. Notice it's not giving generic advice — it's reading the actual diagnosis findings from my actual runs and identifying what keeps coming up. If the same issue is appearing across multiple runs, that's a signal worth acting on.

[VISUAL: Point out specific details in Claude's response that come from the actual data — wave numbers, specific diagnosis findings, named damage sources.]"

---

**Demo Query 2 — Lab Prioritization**

[VISUAL: In Claude Desktop, type: 'Given my current cell income and lab state, what should I be working on in my labs?']

**NARRATION:**

"Now let's ask something that requires Claude to combine data from multiple sources.

[VISUAL: Claude calls get_cell_income and get_lab_state. Response synthesizes both into a recommendation.]

Claude is pulling my cell income rate and my current lab levels simultaneously, then reasoning about what makes sense to prioritize given both my income and how far along I am in each lab category. This is the kind of analysis that would take a lot of manual work to do yourself — and the answer is personalized to your specific state, not a generic tier list.

[VISUAL: Point out how Claude references specific lab names and cell numbers from the actual data.]"

---

**Demo Query 3 — Shard Planning**

[VISUAL: In Claude Desktop, type: 'How long will it take me to hit level 161 on my modules at my current shard rate? Which module type is furthest behind?']

**NARRATION:**

"Shard planning is one of my favorite uses for this. Let's ask about time-to-milestone projections.

[VISUAL: Claude calls get_shard_rates with a target level of 161. Response shows projections per module type.]

Claude pulls the shard earning rate for each module type from my last 30 days of battle reports and projects how long it'll take to hit level 161 for each one. It can immediately tell me which module type is the bottleneck and by how much. That's actionable information I can use to think about whether I want to adjust how I'm farming.

[VISUAL: Point out specific module types and timeframes in Claude's response.]"

---

**Demo Query 4 — Version Drafting Workflow**

[VISUAL: In Claude Desktop, type: 'Check my pending changes and help me draft a version history entry.']

**NARRATION:**

"This last one is a workflow I use regularly whenever I make significant changes to my Tower setup.

[VISUAL: Claude calls get_pending_changes. Response shows a list of recorded changes since the last version entry — things like workshop upgrades, module swaps, lab completions.]

The app tracks changes to your Tower configuration automatically in the background. When you're ready to cut a new version, you ask Claude to review the pending changes and help you decide what type of version bump it warrants — patch for minor tweaks, minor for meaningful changes, major for significant rebuilds — and draft a summary.

[VISUAL: Claude suggests a version bump type and drafts a summary entry.]

This means your version history is always accurate and well-documented, which makes the Compare tab in your battle reports much more meaningful — you always know exactly what configuration a given run was recorded against.

[VISUAL: Point out the version history in TheTowerAnalyzer as a reference point.]"

---

## OUTRO
**Target time: 60 seconds**

---

[VISUAL: Claude Desktop and TheTowerAnalyzer side by side.]

**NARRATION:**

"That's the MCP server setup and a taste of what it can do. The 16 tools it exposes cover essentially every data-bearing part of the game — you can ask Claude about any combination of your runs, your upgrades, your labs, your economy, and your progression, and it'll answer with your actual numbers rather than generic advice.

A few practical notes for using it day to day:

TheTowerAnalyzer needs to be running at localhost:8080 for any MCP queries to work. It doesn't need to be open in a browser — just the server running in the background is enough.

Claude Desktop also needs to be restarted any time you change the config file or move server.js.

And if a query comes back with an error, the most common cause is that the Spring Boot server isn't running. Check your taskbar for the TheTowerAnalyzer console window.

Links to all three videos and the Google Doc reference guide are in the description.

Good luck out there."

[VISUAL: Fade to end screen with Video 1 and Video 2 cards.]

---

## PRODUCTION NOTES

- **Record the demo with real, populated data.** Generic or sparse data makes the demo flat. The more runs and the more complete the workshop/lab state, the more compelling Claude's responses will be.
- **Pick demo queries that show range.** The four demo queries above are deliberately different types: pattern analysis, multi-source synthesis, projection math, and workflow assistance. Each one demonstrates something the previous one doesn't.
- **Show Claude's tool calls.** Claude Desktop shows which tools are being called in real time. Let that be visible — it demystifies what's happening and shows viewers the MCP server is actually doing something.
- **Don't script Claude's responses.** The demo responses will be different every time. That's fine — the point is to show the capability, not to script a perfect answer. If a response is good, let it breathe. If it misses something, that's an honest moment too.
- **Chapter 3 Node.js setup note.** Verify the exact steps for package.json configuration in your environment before recording — specifically whether 'type': 'module' is needed and whether there are any Windows-specific path issues to flag.

### Suggested YouTube Description Template

```
Set up the TheTowerAnalyzer MCP server so Claude AI can query your Tower data directly —
your runs, your workshop, your labs, your modules — all as live context.

📺 Watch the showcase first: [LINK TO VIDEO 1]
🔧 Main setup guide (install, Google Drive, make.com, Tasker): [LINK TO VIDEO 2]
📄 Reference guide with screenshots: [LINK TO GOOGLE DOC]

LINKS:
📦 TheTowerAnalyzer (GitHub): https://github.com/draukadin/TheTowerAnlyzer/releases/tag/v0.0.1-beta2
🤖 Claude Desktop: https://claude.ai/download
🟢 Node.js: https://nodejs.org

CHAPTERS:
0:00 — Introduction & what the MCP server does
[X:XX] — Chapter 1: Architecture overview & prerequisites
[X:XX] — Chapter 2: Installing Node.js
[X:XX] — Chapter 3: Getting server.js & installing dependencies
[X:XX] — Chapter 4: Configuring Claude Desktop
[X:XX] — Chapter 5: Live demo
```

---

*End of Video 3 Script*
