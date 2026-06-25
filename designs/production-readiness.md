# Centralized Platform — Production Readiness

> Last updated: 2026-06-24

This checklist covers everything needed to take the `feature/centralized-platform`
branch from feature-complete to production-shipped. Work roughly top-to-bottom;
some items in different sections can be done in parallel.

---

## 0. Code Fixes (Required Before Launch)

- [x] **DynamoDB initial version record — do not default to 1.0.0 when runs exist**
  When a player sets up centralized mode for the first time, the credential-vending
  Lambda upserts a DynamoDB player record. If `current_version` is not yet set it
  defaults to `1.0.0`, but the user may already have hundreds of runs in their local
  SQLite database. On first write the Spring Boot version-sync path should seed
  `current_version` from the highest version present in the local `runs` table instead
  of hardcoding `1.0.0`.
  - Locate the upsert / put-item call in the version-sync service (`VersionHistoryController`
    or the DynamoDB write path in `S3ReportFetcherService`)
  - Before writing, query `SELECT MAX(version) FROM runs` and use that value when the
    DynamoDB record does not yet exist
  - Add a test: given an existing runs table with a known max version, first-time sync
    must write that version, not `1.0.0`

---

## 1. App Assets (Do First — Blocks Play Store & Shortcut)

### Android App Icons
- [x] Design launcher icon — adapted from `tower-analyzer-icon.svg` (tower + magnifier, navy/amber/blue palette)
- [x] Export adaptive icon: `ic_launcher_foreground.xml` (vector) + `ic_launcher_background` = `#eef0f7` in `colors.xml`
  (`minSdk = 26` = adaptive icons cover all supported devices; no rasterized PNG fallbacks needed)
- [x] Verify icon renders correctly in launcher (round + square) on a physical device

### iOS Shortcut Icon
- [x] Shortcut icon is personal to each user's device and cannot be pre-configured in the
  `.shortcut` file — not a launch requirement. On your own device: Edit → icon →
  `magnifyingglass` glyph, navy (`#2e3a5c`) background.

### Play Store Assets
- [x] Feature graphic: 1024×500 px — see `designs/play-store-feature-graphic.svg` / `.png`
- [x] Phone screenshots: 2–8 screenshots (min 320 px wide; show submit + settings screens)
- [x] Short description: ≤ 80 characters — see `designs/play-store-listing.md`
- [x] Full description: up to 4 000 characters — see `designs/play-store-listing.md`
  (update [SETUP_VIDEO_URL] placeholder with the new centralized-flow video before submitting)

---

## 2. Android — Play Store Release

### Account & Signing
- [x] Confirm Google Play Developer account exists ($25 one-time fee if not yet registered)
- [x] Generate release signing keystore:
  ```
  keytool -genkey -v -keystore thetoweranalyzer-release.jks \
    -alias thetoweranalyzer -keyalg RSA -keysize 2048 -validity 10000
  ```
- [x] Base64-encode the keystore and store as JSON in AWS Secrets Manager:
  `TheTowerAnalyzer/android/release-signing` (us-west-2) → `{ keystoreBase64, keystorePassword, keyAlias, keyPassword }`
  then delete the local `.jks` file (Secrets Manager is the only copy)
- [x] Signing config wired into `android/app/build.gradle.kts` — reads from `local.properties`,
  gracefully skips if absent (debug builds unaffected)
- [x] `scripts/setup-android-signing.ps1` — pulls secret, decodes keystore, writes `local.properties`;
  run once before each release build (`.\gradlew bundleRelease`)

### Build & Verify
- [x] Confirm `BuildConfig.DEV_ENDPOINT` is empty string in release variant
  (verified in `android/app/build/generated/source/buildConfig/release/.../BuildConfig.java`)
- [x] Build signed AAB: `./gradlew bundleRelease`
  (requires full JDK — use `JAVA_HOME=C:\Users\pphi\.jdks\ms-21.0.10`; IntelliJ JBR lacks `jlink`)
- [x] Install release APK on a physical device and run the full submit flow end-to-end
- [x] Verify no dev endpoint appears in the installed app settings

### Play Store Listing
- [x] Create new app in Google Play Console (package: `com.pphi.thetoweranalyzer`)
- [x] Upload signed AAB
- [x] Set app category: Tools (or Productivity)
- [x] Add feature graphic, screenshots, descriptions
- [x] Set content rating (complete IARC questionnaire — likely Everyone)
- [x] Complete Data Safety form:
  - Data collected: Player ID (entered by user), battle report text (sent to AWS S3)
  - Data encrypted in transit: Yes (HTTPS)
  - User can request deletion: Yes (delete reports from the analyzer UI)
- [x] Link privacy policy URL: `https://draukadin.github.io/TheTowerAnalyzer/privacy.html`

### Release Track
- [x] Publish to Internal Testing track first (up to 100 testers, no review delay)
- [x] Added self as internal tester; installed app from Google Play Store
- [x] Validate end-to-end submit flow on Play Store-installed version — 3 Farming reports processed successfully; Tournament pending
- [ ] Promote to Production when stable

---

## 3. iOS Shortcut — Validation & Distribution

### Device Testing (Required — do on physical iPhone)
- [ ] Install `resources/mac/Send Battle Report.shortcut` on a real iOS device
- [ ] Verify region picker appears on first run and writes cache to iCloud Drive
  (`/Shortcuts/TowerAnalyzer Region.txt`)
- [ ] Test each run type: Farming, Tournament, Milestone, Event
- [ ] Test Dissonance with each sub-type: Attack, Defense, Utility, Ultimate Weapon
- [ ] Confirm confirmation notification appears after successful submit
- [ ] Verify region picker does NOT re-appear on subsequent runs (cache working)
- [ ] Test on iOS 16, 17, and 18 if possible

### Error Cases
- [ ] Airplane mode: confirm graceful failure message (not a crash)
- [ ] Empty clipboard: verify behavior (shortcut should warn or fail gracefully)
- [ ] Invalid / missing Player ID: verify API returns a useful error and it surfaces in the shortcut

### Distribution
- [x] Distribution method: **Option A — iCloud link** (open Shortcuts app → long-press shortcut
  → Share → Copy iCloud Link). Permanent, one-tap import, no infrastructure needed.
- [ ] Install shortcut on first iPhone: open the raw `.shortcut` file URL from the GitHub repo
  in Safari on iPhone (iOS recognises the extension and opens Shortcuts), or AirDrop from a Mac
- [ ] Generate iCloud link: long-press shortcut → Share → Copy iCloud Link (public snapshot,
  no Apple account required to import; if the shortcut is ever updated a new link is needed)
- [ ] Add the iCloud link to setup documentation
- [ ] Validate on a second iPhone: tap the iCloud link in Safari → confirm import prompt appears
  → run a submit end-to-end to verify the shortcut works from a clean install

---

## 4. Regional Endpoint Validation (End-to-End)

Run the full flow for each region using a real device or the Shortcuts app.

### US East — Ohio (`us-east-2`)
- [x] Submit a Farming report → S3 object appears under `<player_id>/reports/`
- [x] Spring Boot fetches, parses, and tags the object as `status=processed`
- [x] DynamoDB `current_version` and `updated_at` updated correctly
- [x] Repeat with a Dissonance report; verify `dissonance_type` stored in DB

### EU West — Ireland (`eu-west-1`)
- [x] Same end-to-end flow as US East
- [x] Verify EU API Gateway Lambda forwards to the central `us-east-2` S3 bucket
- [x] Throttling: API Gateway stage throttle (rate 10 req/s, burst 20) — no per-player
  hourly quota implemented (not required at this scale)

### AP Northeast — Tokyo (`ap-northeast-1`)
- [x] Same end-to-end flow as US East
- [x] Throttling: same API Gateway stage throttle as US/EU (deployed via same `IngestStack`)

### Credential Vending
- [x] STS credentials obtained successfully from each region's `/credentials` endpoint
- [x] IP-pinning removed from session policy — caused `s3:ListBucket` denial when Spring Boot
  called S3 from a different network path than the credential-vending call; player-ID prefix
  scoping is the isolation boundary (see `credentials.mjs` commit 9eae2d0)
- [x] Rate limiting: API Gateway stage throttle applies; no per-player hourly quota
  (credentials fetched once per hour by design — Spring Boot auto-refreshes 5 min before expiry)
- [x] Verify credentials auto-refresh 5 minutes before expiry (check Spring Boot logs)

---

## 5. Database Backup & Restore

### Second-Instance Backup (First Instance Already Tested)
- [x] Configure a second player ID in `user.properties`
- [x] Run a backup → S3 key `<player_id_2>/backups/analyzer_<ts>.db` created
- [x] Confirm player 1's backups are NOT visible in player 2's backup list
  (session policy S3 prefix isolation working)

### Restore Flow
- [x] Trigger restore from backup list UI
- [x] Confirm `analyzer.db.restore` is staged on disk
- [x] Restart the app → verify `applyStagedRestoreIfPresent()` swaps the file on startup
- [x] Confirm data from the restored backup is present in the UI
- [x] Test restoring an older backup (not just the latest)

### Retention Verification
- [x] After a second backup: confirm previous backup demoted to `type=backup` tag
- [x] Confirm `type=backup-latest` tag on newest backup only
- [x] Verify S3 lifecycle rule targets `type=backup` tag (30-day expiry)

---

## 6. Security & Infrastructure

### Android Release
- [x] Verify ProGuard / R8 is enabled for release builds (shrink + obfuscate)
- [x] Confirm no AWS endpoint URLs, player IDs, or secrets are hard-coded in source
  (production API Gateway URLs in `Region.kt` are intentional — they are public endpoints
  protected by the STS credential layer, not secrets; dev endpoint absent from release APK)

### AWS
- [x] S3 bucket: Block Public Access enabled, SSE-S3 or SSE-KMS encryption at rest
  (`data-stack.ts` — `BLOCK_ALL` + `S3_MANAGED`; deployed to prod)
- [x] DynamoDB: encryption at rest enabled
  (AWS-owned key default — always on; `SSEDescription` is null = not customer-managed, not unencrypted)
- [x] Lambda execution role: scoped to minimum required permissions
  (`credentialsFn`: `dynamodb:UpdateItem` + `sts:AssumeRole` + logs;
   `ingestFn`: `s3:PutObject` + `dynamodb:GetItem` + logs — GetItem added to read Tower Era version from DDB)
- [x] Credential-vending session policy: S3 prefix is player-scoped, not bucket-wide
  (`credentials.mjs` — `Resource: "${BUCKET}/${playerId}/*"`); IP-pinning removed (see Section 4)
- [x] API Gateway: verify burst + rate throttle limits are set per-stage in all three regions
  (`ingest-stack.ts` — rate 10 / burst 20; same class deployed to US/EU/AP)
- [x] Confirm vended credentials cannot read another player's S3 prefix
  (live test: BA7C430BB386C792 credentials → `GetObject` on C8253AD53F82B595 prefix →
  `AccessDenied: no session policy allows the s3:GetObject action`)

### CDK / Infrastructure
- [x] Tag all CDK stacks with `Project=TheTowerAnalyzer` (app-level) + `Env=dev` (us-west-2) / `Env=prod` (all other stacks)
- [x] Run `cdk diff` against each deployed stack — confirm no unexpected drift
- [x] Confirm all three regional stacks (US, EU, AP) are deployed and healthy

---

## 7. Monitoring & Operations

- [x] CloudWatch alarm: Lambda error count ≥ 5 in 5 min in any region → email alert
  (ingest + credential Lambda, all three regions; SNS subscriptions confirmed)
- [x] CloudWatch alarm: API Gateway 5xx count ≥ 3 in 5 min → email alert
  (all three regions; SNS subscriptions confirmed)
- [x] AWS Billing alert: estimated monthly cost > $10 → email alert
  (AWS Budgets — `TowerAnalyzer-Monthly`; direct email, no SNS subscription needed)
- [x] Lambda log retention set to 30 days in all regions (`logRetention` on both functions)
- [x] Document a brief runbook — see `designs/runbook.md`:
  - How to check if a regional Lambda is down (CloudWatch → Log Groups)
  - How to manually tag an S3 report as `status=processed` if Spring Boot missed it
  - How to revoke a player's credentials (delete their DynamoDB row)

---

## 8. Privacy Policy & Documentation

### Privacy Policy (Required for Play Store)
- [x] Write a simple privacy policy covering:
  - What is collected: Player ID (user-provided), battle report text
  - Where it is stored: AWS S3 (`us-east-2`), DynamoDB (`us-east-2`)
  - Retention: reports/DDB/latest backup retained indefinitely (email request to delete);
    older backups auto-deleted after 30 days via S3 lifecycle rule
  - Developer access disclosure (debugging/service improvement)
  - Contact email: draukadin@gmail.com
- [x] Hosted on GitHub Pages: `https://draukadin.github.io/TheTowerAnlyzer/privacy.html`
  (`docs/privacy.html` — enable GitHub Pages on the repo → docs folder of master branch)

### Setup Documentation
- [x] Centralized setup guide written: `resources/Centralized_Setup_Guide.md`
  (covers Android app and iOS shortcut; Video2 script remains as-is for legacy flow)
- [x] Write user FAQ: `resources/User_FAQ.md`
- [x] Update `Send Farming Battle Report - Shortcuts Guide.md` — marked as legacy,
  added header directing new users to the centralized shortcut

---

## 9. Pre-Launch Checklist

- [x] Merge `feature/centralized-platform` → `master`
  (done early to unblock GitHub Pages privacy policy URL and allow internal testers
  to use the mobile app with the live Spring Boot backend)
- [x] Enable GitHub Pages: repo Settings → Pages → source = `master` / `/docs`
  → confirm `https://draukadin.github.io/TheTowerAnlyzer/privacy.html` is live
- [ ] Tag release: `git tag v1.1.0` (minor bump — new centralized path added, legacy mode preserved, no breaking changes)
- [ ] Android: promote Internal Testing AAB to Production track in Play Console
- [ ] iOS: publish shortcut distribution link
- [ ] Notify existing users (community post / Discord / wherever your user base is)
- [ ] Monitor CloudWatch dashboards for the first 48 hours post-launch
