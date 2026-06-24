# TheTowerAnalyzer — Centralized Mode Setup Guide

> **New users:** Centralized mode is the recommended way to get started. No Google account,
> no make.com, no webhooks — just an app or shortcut and your Player ID.
>
> **Legacy users:** If you are already running the Google Drive + make.com + Tasker pipeline
> it continues to work unchanged. See `Video2_Setup_Script.md` for that flow.

---

## What You Need Before You Start

- Your **Player ID** from The Tower  
  *(In-game: Settings → copy the ID shown below your username)*
- **Android users:** a phone capable of installing apps from the Play Store
- **iOS users:** an iPhone running iOS 16 or later with the Shortcuts app installed

---

## Android Setup

### Step 1 — Install the app

Search for **TheTowerAnalyzer** on the Google Play Store and install it.  
*(Direct link in the video/post description.)*

### Step 2 — Enter your Player ID and region

Open the app and tap the **Settings cog** in the top right.

- **Player ID** — paste the ID you copied from The Tower.
- **Region** — choose the endpoint closest to you:
  - 🇺🇸 US East (Ohio) — default for North America
  - 🇮🇪 EU West (Ireland) — Europe, Middle East, Africa
  - 🇯🇵 AP Northeast (Tokyo) — Asia Pacific, Australia

Tap **Save**.

### Step 3 — Submit your first battle report

1. In The Tower, finish a run and open the battle report from battle history.
2. Tap **Copy to Clipboard**.
3. Switch to TheTowerAnalyzer.
4. Select your run type (and dissonance sub-type if applicable).
5. Tap **Submit**.

You'll get a confirmation immediately. The desktop app will pick up the report the next
time you click **Fetch Reports**.

---

## iOS Setup

### Step 1 — Install the shortcut

Tap the iCloud link from the setup video description or community post.  
Safari recognises the `.shortcut` extension and opens the Shortcuts app automatically.  
Tap **Add Shortcut**.

### Step 2 — Enter your Player ID

Open the Shortcuts app, find **Send Battle Report**, and tap **•••** to edit it.  
Replace the placeholder `PLAYER_ID` value at the top with your actual Player ID.  
Tap **Done**.

### Step 3 — First run (region picker)

Run the shortcut once from the Shortcuts app. It will ask you to pick your region —
choose the one closest to you (US, EU, or AP). The shortcut saves this choice to
iCloud Drive so you won't be asked again.

### Step 4 — Day-to-day workflow

1. In The Tower, finish a run and tap **Copy to Clipboard** on the battle report.
2. Run the **Send Battle Report** shortcut (Home Screen icon, widget, or Shortcuts app).
3. Pick your run type.
4. A notification confirms the report was received.

**Tip:** Add the shortcut to your Home Screen for one-tap access.  
*(Shortcuts app → long-press Send Battle Report → Add to Home Screen)*

---

## Verifying It Works

After submitting a report, open the TheTowerAnalyzer desktop app and click
**Fetch Reports**. Your run should appear within a few seconds — the desktop app
polls your cloud storage and processes any reports it hasn't seen yet.

If the report doesn't appear, check:

1. The Player ID in the app/shortcut matches the one in the desktop app's `user.properties`.
2. The region in the app/shortcut matches `aws.api-gateway.region` in `user.properties`.
3. The desktop app is running and able to reach AWS (check the console for errors).

---

## Changing Your Region

- **Android:** Settings → select a different region → Save.
- **iOS:** In iCloud Drive, delete the file at `Shortcuts/TowerAnalyzer Region.txt`,
  then run the shortcut — the region picker will appear again.