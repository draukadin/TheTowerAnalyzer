# TheTowerAnalyzer — Frequently Asked Questions

---

## General

**What is TheTowerAnalyzer?**  
A Windows desktop app that collects your Tower battle reports and lets you track your
progress over time — version history, lab completion, workshop investment, run statistics.
You submit reports from your phone (Android app or iOS shortcut) and view them on your PC.

**Do I need a Google account or make.com account?**  
Not for centralized mode (the new default). You just need your Player ID from The Tower.
Legacy mode (Google Drive + make.com + Tasker) remains fully supported if you prefer it.

---

## Data & Privacy

**Where is my data stored?**  
Battle reports and database backups are stored in AWS S3 (US East region, encrypted at rest).
Your Player ID is used as your storage prefix — only you can read or write to it.
DynamoDB stores your current version number and registration timestamp.

Full details: [https://draukadin.github.io/TheTowerAnlyzer/privacy.html](https://draukadin.github.io/TheTowerAnlyzer/privacy.html)

**Can anyone else see my reports?**  
No. Each player's data is isolated by an AWS session policy scoped to their Player ID.
Credentials are also IP-bound — they only work from the IP address that requested them.

**How long is my data kept?**  
- Battle reports: kept indefinitely (until you delete them).
- Database backups: the latest backup is kept indefinitely; older backups are automatically
  deleted after 30 days.
- To request full deletion, email draukadin@gmail.com with your Player ID.

---

## Setup

**What is my Player ID?**  
Open The Tower → Settings → your ID is shown below your username with a copy button next to it.
It looks like `BA7C430BB386C792` — 16 uppercase hex characters.

**Which region should I choose?**  
Pick the endpoint closest to you for lowest latency:
- 🇺🇸 US East (Ohio) — North America
- 🇮🇪 EU West (Ireland) — Europe, Middle East, Africa
- 🇯🇵 AP Northeast (Tokyo) — Asia Pacific, Australia

Choosing a different region than intended doesn't break anything — your reports always
end up in the same central storage regardless of which endpoint you submit through.

**How do I change my region later?**  
- **Android:** Settings → select a different region → Save.
- **iOS:** Delete `/Shortcuts/TowerAnalyzer Region.txt` from iCloud Drive, then run
  the shortcut — the region picker will appear again.
- **Desktop:** Edit `%APPDATA%\TheTowerAnalyzer\user.properties` and change
  `aws.api-gateway.region` to `us`, `eu`, or `ap`.

**What if I get a new phone?**  
Install the Android app or iOS shortcut again and enter the same Player ID. All your
historical data is still in the cloud — the desktop app will continue to see it.

---

## Submitting Reports

**What if my submission fails?**  
Check that your Player ID in the app or shortcut matches the one in the desktop app's
`user.properties`. A mismatch means the desktop app won't find reports submitted under
the wrong ID. Also check that you have an active network connection.

**Can I submit multiple reports in a row?**  
Yes. Submit as many as you like back-to-back. The API Gateway enforces an overall
rate limit to protect the service, but normal usage will never come close to it.

**The shortcut/app submitted successfully but the report isn't showing up in the desktop app.**  
Click **Fetch Reports** — the desktop app doesn't poll automatically; you trigger it manually.
If it still doesn't appear, check that the Player ID and region in the mobile app match
what's configured in `user.properties` on your PC.

---

## Legacy Mode (Google Drive + make.com + Tasker)

**Can I still use the old Tasker + make.com setup?**  
Yes — Legacy mode is fully supported. The desktop app processes reports from Google Drive
exactly as before. You can run both modes at the same time if you want, though there's no
reason to once centralized is working.

**I'm on iOS and used the old make.com shortcut. Should I switch?**  
Yes, the new **Send Battle Report** shortcut replaces the old make.com one. No make.com
account, no webhook URL, no API key — just install and enter your Player ID. See
`Send Farming Battle Report - Shortcuts Guide.md` for migration notes.

---

## Backups & Restore

**How do I back up my database?**  
In the desktop app, click **Settings → Backup Database**. The backup is uploaded to your
cloud storage automatically.

**How do I restore a backup?**  
In the desktop app, go to **Settings → Restore** and choose a backup from the list.
The app stages the restore file on disk. Restart the app — it swaps the database on startup
before anything else loads.

**Are older backups kept forever?**  
Only the most recent backup is kept indefinitely. All previous backups are automatically
deleted 30 days after they were superseded. If you need to retain a specific backup
longer, download it from the app before it expires.