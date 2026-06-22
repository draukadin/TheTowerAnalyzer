# TheTowerAnalyzer — Centralized Platform Design

**Created:** 2026-06-21
**Status:** Draft
**Scope:** Android app, centralized AWS backend, web app — replacing the per-user Tasker + make.com + Google Drive + local Windows app pipeline

---

## Problem Statement

The current setup requires each user to independently configure:

- Google Cloud OAuth credentials
- Google Drive folder structure and a version-tracking spreadsheet
- A make.com account with an imported and wired scenario
- Tasker on Android (paid app) with a task imported via QR code and manually configured

This is a 35-minute setup process (Video 2) that is a significant barrier to entry. The goal is to replace all of it with:

- A native Android app (install and paste player ID — done)
- An updated iOS Shortcut (add player ID variable — done)
- A centralized AWS backend (no per-user cloud config)
- A web app with Google Sign-In (replaces the local Windows-only Spring Boot app)

---

## Current Architecture

```
The Tower (Android)
  → Copy to Clipboard
  → Tasker task (Android)
      → Dialog: pick run type (Farming / Tournament / Milestone / Dissonance / Event)
      → HTTP POST to user's make.com webhook
            headers: x-make-apikey, Content-Type: text/plain
            query:   ?runType=<selected>
            body:    clipboard text (battle report)

make.com Scenario
  → Webhook receives POST
  → Google Sheets: read current tower version from cell B2 (TowerVersionTracking sheet)
  → Google Drive: create file named <version>_<era>_<runType>_<timestamp>.txt
                  upload to user's Battle Reports folder

TheTowerAnalyzer (local Windows Spring Boot app, localhost:8080)
  → Fetch Reports button → reads files from Google Drive Battle Reports folder
  → BattleHistoryParser: parses report text, extracts version + era from filename
  → Persists to local SQLite DB (%APPDATA%\TheTowerAnalyzer\analyzer.db)
  → User queries data via local web UI

VersionHistoryController (local Spring Boot)
  → User records tower upgrades as versioned entries (labs, UWs, modules, workshop)
  → On version create: writes new version to Google Sheet cell B2
  → make.com picks up new version for subsequent battle report filenames
```

### iOS Path (current)

- `.shortcut` file imported into Apple Shortcuts
- Same flow as Tasker: reads clipboard, picks run type, POSTs to user's make.com webhook
- Shortcut guide: `resources/mac/Send Farming Battle Report - Shortcuts Guide.md`

---

## Target Architecture (MVP)

The guiding principle: eliminate the per-user cloud setup (make.com + Google Drive + Google Sheets) while keeping all compute and the primary database local. Only the thin shared infrastructure that cannot be local moves to AWS.

```
The Tower (Android/iOS)
  → Copy to Clipboard

Android App                                          iOS Shortcut (updated)
  Settings (one-time): paste Player ID                Settings (one-time): paste player_id variable
  → Pick run type                                      → Same POST as Android app
  → HTTP POST ──────────────────────────────────────────────────┐
                                                                 ▼
                                                      AWS API Gateway
                                                                 │
                                                                 ▼
                                                          AWS Lambda (thin)
                                                        • Validate player_id header
                                                        • Write raw report text to S3
                                                          s3://bucket/<player_id>/<timestamp>_<runType>.txt
                                                        • No parsing — no DB writes

Local Spring Boot App (updated)
  "Fetch Reports" button
    → List objects: s3://bucket/<player_id>/          (only unprocessed reports)
    → Download each report text file
    → Parse with BattleHistoryParser (unchanged)
    → Look up current version from local SQLite version_history
    → Persist to local SQLite battle_history with version stamp
    → Move processed file to Glacier storage class

  "Save Version" (VersionHistoryController)
    → Write to local SQLite (unchanged)
    → Also write player_id → version to DynamoDB   (replaces Google Sheets cell write)

DynamoDB (single table)
  PK: player_id
  Attributes: current_version, updated_at
  Purpose: durable cross-device version record; future use by web app or Lambda if needed

S3 Bucket
  Active prefix:    <player_id>/<filename>.txt       (Standard storage — listed on fetch)
  Archived prefix:  <player_id>/processed/<filename> (Glacier — not listed, long-term retention)
  Lifecycle rule:   auto-transition processed/ to Glacier after 1 day
```

**What stays local (unchanged):** SQLite database, all parsing logic, all analytics queries, labs/workshop/module/UW data, MCP server.

**What moves to AWS (minimal):** raw report text files (S3) + current version per player (DynamoDB) + report ingestion endpoint (API Gateway + Lambda).

---

## What Gets Eliminated for Users

| Eliminated                                | Replaced By                              |
| ----------------------------------------- | ---------------------------------------- |
| Google Drive folder structure             | S3 (managed centrally, zero user config) |
| Google Sheets version tracker spreadsheet | DynamoDB (managed centrally)             |
| make.com account + scenario setup         | API Gateway + Lambda (centralized)       |
| Tasker (paid Android app)                 | Free native Android app                  |
| Google Cloud project + OAuth credentials  | Not needed in MVP                        |

**Deferred (not in MVP):**
- S3 + CloudFront web app — revisit based on adoption
- Full centralized RDS database — revisit based on adoption and cost analysis

---

## Components

### 1. Android App

**Purpose:** Replace Tasker. Lower barrier from "install paid app + import task + configure webhook" to "install free app + paste player ID."

**Core features:**

- Settings screen: Player ID (one-time paste from The Tower's copy-to-clipboard), mode toggle (Legacy / Centralized), legacy fields (webhook URL + API key) for transition period
- Main screen: pick run type → read clipboard → POST report
- Optional: clipboard listener (`ClipboardManager.OnPrimaryClipChangedListener`) to detect when a report is copied and surface a notification/button automatically

**Tech:** Kotlin, Android Studio

**Request format (centralized mode):**

```
POST https://<api-gateway>/reports?runType=<type>
Headers:
  X-Player-Id: <player_id>
  Content-Type: text/plain
Body: <battle report text>
```

**Transition (legacy mode):**

```
POST https://hook.us2.make.com/<api_url>?runType=<type>
Headers:
  x-make-apikey: <api_key>
  Content-Type: text/plain
Body: <battle report text>
```

---

### 2. iOS Shortcut (updated)

Add a third variable at the top alongside `api_key` and `api_url`:

- `player_id` — paste once from The Tower, sent as a header on every POST

Distribute a separate `.shortcut` file for centralized mode. No good mechanism to surface a helpful error from within a Shortcut if player ID is missing — document it prominently in the setup guide as the first thing to check.

---

### 3. AWS Backend (MVP — thin)

**Entry point:** API Gateway
**Processing:** Lambda (Python or Node.js — lightweight, no parsing logic, no cold start concerns)
**Storage:** S3 (report files) + DynamoDB (version index)

#### Lambda responsibilities (MVP)

1. **Validate `X-Api-Key` header** — reject 401 if missing or does not match the shared static key
2. **Validate `X-Player-Id` header** — reject 400 if missing or empty
3. **Validate payload structure** (see below) — reject 400 if checks fail
4. Construct S3 key: `<player_id>/<UTC-timestamp>_<runType>.txt`
5. Write raw report body to S3

No parsing logic beyond structural validation. All full parsing stays in the local Spring Boot app using the existing `BattleHistoryParser`.

#### Payload validation

Player IDs are semi-public (used for in-game stone gifting), so any player's ID could be used to submit a payload to another player's S3 folder. A malformed report that passes S3 write but fails `BattleHistoryParser` causes runtime exceptions in the victim's local app. Validation guards against this.

Checks (in order, fail fast):

1. **Size bounds** — body must be non-empty and under 50 KB (battle reports are small text files; anything larger is not a valid report)
2. **First line is a known section header** — `BattleHistoryParser` expects the first line to be a recognized section header. Lambda maintains a hardcoded set of valid header strings (derived from the `SectionHeader` enum). Reject if first line is not in the set.
3. **Minimum section count** — body must contain at least N recognized section header lines. Prevents a single valid header line from passing check 2 while the rest is garbage.

**Deferred:** Validation of a correctly-structured report with valid-format but bogus data values (e.g. fake wave counts). Requires the attacker to reverse-engineer the exact report format and is significantly harder to exploit meaningfully.

#### S3 structure

```
bucket/
  <player_id>/
    2026-06-21T14-32-00Z_Farming.txt      ← active, listed on fetch
    2026-06-20T09-15-00Z_Tournament.txt
  <player_id>/processed/
    2026-06-19T22-00-00Z_Milestone.txt    ← Glacier, not listed
```

Lifecycle rule: objects under `*/processed/` transition to Glacier Instant Retrieval after 1 day.

#### DynamoDB table (MVP)

| Attribute        | Type   | Notes                              |
|------------------|--------|------------------------------------|
| `player_id` (PK) | String | From `X-Player-Id` header          |
| `current_version`| String | e.g. `3.4.2`                       |
| `updated_at`     | String | ISO-8601 timestamp                 |

Written by the local Spring Boot app's `VersionHistoryController` when the user records a new version (replaces the Google Sheets cell write). Read by future Lambda or web app if needed.

---

### 4. Local Spring Boot App (updated)

The local app is **not replaced** in the MVP — it gains two new integration points:

**Fetch Reports (updated):**
- `ListObjectsV2` on `s3://bucket/<player_id>/` (active prefix only)
- Download each file not yet in local SQLite
- Parse with existing `BattleHistoryParser` (unchanged)
- Look up current version from local SQLite `version_history`
- Persist to local SQLite with version stamp
- Move processed file: copy to `<player_id>/processed/<filename>`, delete original

**VersionHistoryController (updated):**
- On version create: write to local SQLite (unchanged) + write to DynamoDB (replaces Google Sheets `syncVersionCell()` call)
- `retrySync` endpoint updated to retry DynamoDB write instead of Sheets write

**New config in `user.properties`:**
```
aws.region=us-east-1
aws.s3.bucket=<bucket-name>
aws.player-id=<player_id>
```

AWS credentials via standard credential chain (environment, `~/.aws/credentials`, or IAM role).

---

### 5. Web App — DEFERRED

Full web app (React / Next.js / S3 + CloudFront) deferred pending adoption data and cost analysis. The local Spring Boot app remains the primary UI.

When revisited, the web app would add:
- Google Sign-In (player_id association)
- Battle report history view (querying the user's S3 or a future centralized DB)
- Version history management UI

---

## Version Tracking (Clarification)

The version number (e.g. `3.4.2`) is **not** the Tower game version. It is a **personal tower changelog** maintained by each user — a semantic versioning scheme they apply to their own tower's upgrade history. Every time they upgrade a lab, UW, module, or workshop item they record it as a new version entry with change details.

Battle reports are stamped with the user's current version at fetch/parse time (not at submission time), since the local app handles all parsing. DynamoDB holds the current version as a durable cross-device record for future use.

---

## Player ID

- The Tower provides a globally unique player ID with a "copy to clipboard" button in-game
- Used as the S3 folder prefix and DynamoDB partition key — the sole cross-system identifier
- One-time setup: Android app settings, iOS Shortcut variable, and `user.properties` on the local app
- No auth association required in MVP (deferred to web app phase)

---

## Transition Strategy

The Android app ships with two modes. Users on the legacy path (make.com) keep working unchanged until they choose to migrate.

| Mode        | Endpoint                    | Auth                 | Config required       |
| ----------- | --------------------------- | -------------------- | --------------------- |
| Legacy      | User's make.com webhook URL | x-make-apikey header | webhook URL + API key |
| Centralized | AWS API Gateway             | X-Player-Id header   | Player ID only        |

Legacy mode can be sunset once the centralized path is verified and the user base has migrated. No data migration needed — local SQLite is unchanged, only the report source shifts from Google Drive to S3.

---

## Out of Scope (MVP)

- Full centralized RDS database — deferred
- Web app (S3 + CloudFront) — deferred
- JWT / Google Sign-In auth — deferred to web app phase
- MCP server changes — separate concern
- Admin tooling

---

## Decisions

1. **Web app hosting:** AWS (Amplify / S3 + CloudFront) when built — keep everything in one ecosystem. **Deferred.**

2. **Persistence:** MVP uses S3 (reports) + DynamoDB (version index) + local SQLite (everything else). Full RDS migration deferred pending adoption and cost analysis.

3. **Lambda runtime:** Python or Node.js — thin drop-box function, no parsing logic, no cold start concerns at this scope.

   **Rollout phases:**
   - **Phase 1 (private beta):** Centralized endpoint not exposed in public Android app build. Developer uses a dev build to validate the full pipeline: Android → API Gateway → Lambda → S3 → local app fetch → SQLite.
   - **Phase 2 (production):** Centralized mode enabled in public release after Phase 1 verified.

4. **Auth / rate limiting:** Shared static API key (`X-Api-Key` header) validated by Lambda — same key for all users, distributed with the Android app and baked into the iOS Shortcut as a variable. Consistent with how the current per-user make.com API key works, just centralized. API Gateway rate limiting per IP provides abuse containment. Key can be rotated if leaked (requires app update). JWT + per-user throttling deferred to web app phase when a proper login flow exists.

5. **iOS centralized Shortcut:** Separate `.shortcut` file for centralized mode. Document player ID as first thing to check in the troubleshooting guide.

6. **Cost profile (MVP):**
   - **API Gateway:** ~$3.50/million calls. At 2 reports/day × 100 users = 6,000 calls/month → effectively free.
   - **Lambda:** Well within free tier at this volume.
   - **S3 Standard:** Negligible for text files. Glacier archival further reduces cost over time.
   - **DynamoDB:** Free tier covers one row per user at this scale indefinitely.
   - **Total estimated AWS cost at <100 users: < $1/month.**
   - No RDS = no fixed $30–50/month baseline cost. This is the key advantage of the MVP approach vs. full centralization.
