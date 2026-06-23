# Centralized Platform — Production Readiness

> Last updated: 2026-06-23

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
- [ ] Verify icon renders correctly in launcher (round + square) on a physical device

### iOS Shortcut Icon
- [ ] Assign a glyph + color to the shortcut (open in Shortcuts app → Edit → icon)
- [ ] Use `magnifyingglass` glyph with navy (`#2e3a5c`) background — matches the magnifier from the desktop and Android icon

### Play Store Assets
- [ ] Feature graphic: 1024×500 px (required, shown in Play Store search results)
- [ ] Phone screenshots: 2–8 screenshots (min 320 px wide; show submit + settings screens)
- [ ] Short description: ≤ 80 characters
- [ ] Full description: up to 4 000 characters (explain what the app does, no Google Drive / Tasker needed)

---

## 2. Android — Play Store Release

### Account & Signing
- [ ] Confirm Google Play Developer account exists ($25 one-time fee if not yet registered)
- [ ] Generate release signing keystore:
  ```
  keytool -genkey -v -keystore thetoweranalyzer-release.jks \
    -alias thetoweranalyzer -keyalg RSA -keysize 2048 -validity 10000
  ```
- [ ] Store keystore + passwords securely (losing the keystore = cannot update the app ever)
- [ ] Add signing config to `android/app/build.gradle.kts` release build type
  (read credentials from `local.properties` or env vars — never hard-code in source)

### Build & Verify
- [ ] Confirm `BuildConfig.DEV_ENDPOINT` is empty string in release variant
  (check `android/app/build.gradle.kts` `buildTypes { release { ... } }`)
- [ ] Build signed AAB: `./gradlew bundleRelease`
- [ ] Install release APK on a physical device and run the full submit flow end-to-end
- [ ] Verify no dev endpoint appears in the installed app settings

### Play Store Listing
- [ ] Create new app in Google Play Console (package: `com.pphi.thetoweranalyzer`)
- [ ] Upload signed AAB
- [ ] Set app category: Tools (or Productivity)
- [ ] Add feature graphic, screenshots, descriptions
- [ ] Set content rating (complete IARC questionnaire — likely Everyone)
- [ ] Complete Data Safety form:
  - Data collected: Player ID (entered by user), battle report text (sent to AWS S3)
  - Data encrypted in transit: Yes (HTTPS)
  - User can request deletion: Yes (delete reports from the analyzer UI)
- [ ] Link privacy policy URL (see Section 8)

### Release Track
- [ ] Publish to Internal Testing track first (up to 100 testers, no review delay)
- [ ] Validate with real testers across US / EU / AP regions
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
- [ ] Choose distribution method:
  - **Option A (simplest):** Share via iCloud link — open Shortcuts app → long-press shortcut
    → Share → Copy iCloud Link. Anyone with the link can import directly.
  - **Option B:** Host `.shortcut` file on a web page; tapping the link on iOS prompts import.
- [ ] Add the distribution link to setup documentation
- [ ] Test that a fresh iOS device can import and run the shortcut from that link

---

## 4. Regional Endpoint Validation (End-to-End)

Run the full flow for each region using a real device or the Shortcuts app.

### US East — Ohio (`us-east-2`)
- [ ] Submit a Farming report → S3 object appears under `<player_id>/reports/`
- [ ] Spring Boot fetches, parses, and tags the object as `status=processed`
- [ ] DynamoDB `current_version` and `updated_at` updated correctly
- [ ] Repeat with a Dissonance report; verify `dissonance_type` stored in DB

### EU West — Ireland (`eu-west-1`)
- [ ] Same end-to-end flow as US East
- [ ] Verify EU API Gateway Lambda forwards to the central `us-east-2` S3 bucket
- [ ] Confirm throttling: 6th submit in an hour returns HTTP 429

### AP Northeast — Tokyo (`ap-northeast-1`)
- [ ] Same end-to-end flow as US East
- [ ] Confirm throttling works in AP region

### Credential Vending
- [ ] STS credentials obtained successfully from each region's `/credentials` endpoint
- [ ] Verify IP-bound session policy: request from a different IP is rejected
- [ ] Verify rate limit: 6th `/credentials` call in 1 hour returns 429
- [ ] Verify credentials auto-refresh 5 minutes before expiry (check Spring Boot logs)

---

## 5. Database Backup & Restore

### Second-Instance Backup (First Instance Already Tested)
- [ ] Configure a second player ID in `user.properties`
- [ ] Run a backup → S3 key `<player_id_2>/backups/analyzer_<ts>.db` created
- [ ] Confirm player 1's backups are NOT visible in player 2's backup list
  (session policy S3 prefix isolation working)

### Restore Flow
- [ ] Trigger restore from backup list UI
- [ ] Confirm `analyzer.db.restore` is staged on disk
- [ ] Restart the app → verify `applyStagedRestoreIfPresent()` swaps the file on startup
- [ ] Confirm data from the restored backup is present in the UI
- [ ] Test restoring an older backup (not just the latest)

### Retention Verification
- [ ] After a second backup: confirm previous backup demoted to `type=backup` tag
- [ ] Confirm `type=backup-latest` tag on newest backup only
- [ ] Verify S3 lifecycle rule targets `type=backup` tag (30-day expiry)

---

## 6. Security & Infrastructure

### Android Release
- [x] Verify ProGuard / R8 is enabled for release builds (shrink + obfuscate)
- [x] Confirm no AWS endpoint URLs, player IDs, or secrets are hard-coded in source
  (production API Gateway URLs in `Region.kt` are intentional — they are public endpoints
  protected by the STS credential layer, not secrets; dev endpoint absent from release APK)

### AWS
- [ ] S3 bucket: Block Public Access enabled, SSE-S3 or SSE-KMS encryption at rest
- [ ] DynamoDB: encryption at rest enabled
- [ ] Lambda execution role: scoped to only `sts:AssumeRole` + `logs:*`
- [ ] Credential-vending session policy: confirm S3 prefix is player-scoped, not bucket-wide
- [ ] API Gateway: verify burst + rate throttle limits are set per-stage in all three regions
- [ ] Confirm vended credentials cannot read another player's S3 prefix
  (`aws iam simulate-principal-policy` with a cross-player key path)

### CDK / Infrastructure
- [x] Tag all CDK stacks with `Project=TheTowerAnalyzer` (app-level) + `Env=dev` (us-west-2) / `Env=prod` (all other stacks)
- [ ] Run `cdk diff` against each deployed stack — confirm no unexpected drift
- [ ] Confirm all three regional stacks (US, EU, AP) are deployed and healthy

---

## 7. Monitoring & Operations

- [ ] CloudWatch alarm: Lambda error rate > 5% in any region → email alert
- [ ] CloudWatch alarm: API Gateway 5xx rate > 1% → email alert
- [ ] AWS Billing alert: estimated monthly cost > $10 → email alert
- [ ] Verify Lambda logs are captured in CloudWatch; set log retention to 30 days
- [ ] Document a brief runbook:
  - How to check if a regional Lambda is down (CloudWatch → Log Groups)
  - How to manually tag an S3 report as `status=processed` if Spring Boot missed it
  - How to revoke a player's credentials (delete their DynamoDB row)

---

## 8. Privacy Policy & Documentation

### Privacy Policy (Required for Play Store)
- [ ] Write a simple privacy policy covering:
  - What is collected: Player ID (user-provided), battle report text
  - Where it is stored: AWS S3 (`us-east-2`), DynamoDB (`us-east-2`)
  - Retention: reports kept until processed then tagged; backups per lifecycle rules
  - Contact email
- [ ] Host at a stable URL — options:
  - GitHub Pages (free, stable)
  - Simple `privacy.html` served from the Spring Boot app at `/privacy`

### Setup Documentation
- [ ] Review and finalize `resources/Video2_Setup_Script.md` for the centralized flow
- [ ] Write user FAQ:
  - "Where is my data stored?" → AWS S3 (encrypted, your player prefix only)
  - "How do I change my region?" → Android: Settings screen; iOS: delete `/Shortcuts/TowerAnalyzer Region.txt` from iCloud Drive, then re-run the shortcut
  - "What if I get a new phone?" → Re-install the app or shortcut, enter the same Player ID, select the same region
  - "What if my submission fails?" → Check your Player ID is correct; note the throttle limit is 5 submits/hour
  - "Can I still use Tasker / make.com?" → Yes, Legacy mode remains fully supported
- [ ] Update `Send Farming Battle Report - Shortcuts Guide.md` to reference the new centralized shortcut as the primary method

---

## 9. Pre-Launch Checklist

- [ ] All sections 1–8 complete and verified
- [ ] Merge `feature/centralized-platform` → `master`
- [ ] Tag release: `git tag v2.0.0-centralized`
- [ ] Android: promote Internal Testing AAB to Production track in Play Console
- [ ] iOS: publish shortcut distribution link
- [ ] Notify existing users (community post / Discord / wherever your user base is)
- [ ] Monitor CloudWatch dashboards for the first 48 hours post-launch
