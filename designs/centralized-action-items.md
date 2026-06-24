# Centralized Platform — Action Items

**Branch:** `feature/centralized-platform`
**Last reviewed:** 2026-06-21

---

## Done

### Android App
- [x] Native Kotlin app with Centralized and Legacy modes
- [x] Player ID setting (one-time paste from The Tower)
- [x] Region picker with bundled ingest URLs (US live; EU/AP stubbed pending stack deploys)
- [x] Run type spinner — Submit disabled until a real run type is selected, resets after submit
- [x] Dissonance sub-type spinner (conditional)
- [x] API key removed — player ID is the only required centralized setting

### iOS
- [x] Centralized shortcut (`Send Battle Report.shortcut`) — prompts for player ID, run type, optional Dissonance sub-type

### AWS Infrastructure (US East only)
- [x] CDK two-stack setup: `DataStack` (S3 + DynamoDB) and `IngestStack` (Lambda + API Gateway)
- [x] S3 bucket with tag-based lifecycle rule (`processed=true` → Glacier after 1 day)
- [x] DynamoDB `TowerAnalyzerPlayerVersion-{env}` table
- [x] Least-privilege IAM user for Spring Boot app in `DataStack`
- [x] API key auth removed from `IngestStack`

### Lambda — `POST /reports`
- [x] Payload size limit (15 KB)
- [x] First-line section header validation
- [x] Minimum section count (5)
- [x] S3 write: `<player_id>/<timestamp>_<runType>[_<dissonanceType>].txt`
- [x] API Gateway throttling (10 req/s, burst 20)

### Spring Boot Backend
- [x] `S3ReportFetcherService` — list, download, parse (injecting synthetic Tower Era line), tag `processed=true`, route via `ReportController`
- [x] `VersionHistoryController` — writes `current_version` to DynamoDB on version create (`syncVersionToDdb`)
- [x] `POST /{version}/sync-ddb` retry endpoint
- [x] `S3ReportRepository.deleteObject` — self-service duplicate deletion from Spring Boot UI
- [x] `dissonance_type` nullable column — populated from S3 filename subtype token
- [x] Named AWS profile support (`aws.profile` → `ProfileCredentialsProvider`)
- [x] AWS beans conditional on `aws.region` — legacy Drive users unaffected

---

## To Do

### 1. Credential-vending Lambda — `GET /credentials`
The Spring Boot app currently authenticates with a static long-lived IAM user key via named profile. The design calls for STS-vended temporary credentials scoped per player, IP-bound, and rate-limited.

- [x] Add `GET /credentials` resource to `IngestStack` API Gateway
- [x] Write Lambda handler (`credentials.mjs` or alongside `ingest-report.mjs`):
  - Validate `X-Player-Id` header (400 if missing)
  - Upsert player record in DynamoDB (`registered_at` on first call; leave existing record otherwise)
  - Rate-limit: reject 429 if >5 calls in rolling 1-hour window per player_id (track in DDB)
  - Call STS `AssumeRole` with session policy scoped to `<player_id>/*` on S3 and `LeadingKeys=[player_id]` on DDB, plus `aws:SourceIp` condition binding creds to caller's IP
  - Return `{ AccessKeyId, SecretAccessKey, SessionToken, Expiration }` (1-hour TTL)
- [x] Grant Lambda permission to call STS and read/write the DynamoDB table (rate-limit tracking + upsert)
- [x] Add `CREDENTIAL_ROLE_ARN` env var to Lambda (role it will assume)
- [x] Create the assumable IAM role in `DataStack` with the session-policy-scoped trust policy

### 2. Spring Boot — switch to vended credentials
- [x] Add `aws.api-gateway.region` to `AwsProperties` (values: `us`)
- [x] Add `aws.api-gateway.region` to `AwsProperties` (values: `eu`, `ap`); pending deployment of EU & AP stacks
- [x] Add a `CredentialVendingClient` (or Spring `@Bean`) that calls `GET /credentials` on startup and refreshes before expiry (within 5 min of `Expiration`)
- [x] Update `AwsConfig` to use the vended `SessionCredentialsProvider` instead of `ProfileCredentialsProvider` / `DefaultCredentialsProvider`
- [x] Remove `aws.profile` from `AwsProperties` and `AwsConfig` once vended creds are wired
- [x] Remove static IAM user (`AppUser`) from `DataStack` — no longer needed

### 3. Config split — `application.properties` vs `user.properties`
Currently `aws.region`, `aws.s3.bucket`, `aws.dynamodb.table` are set by users per CDK `CfnOutput` instructions. These are deployment constants users have no reason to change.

- [x] Move `aws.region`, `aws.s3.bucket`, `aws.dynamodb.table` into `application.properties` (bundled in jar)
- [x] Move us regional API Gateway ingest URLs into `application.properties` (`aws.api-gateway.url.us`)
- [x] Update CDK `CfnOutput` descriptions — bucket/table names are now for operator reference only, not user config
- [x] `user.properties` ends up with exactly two entries: `aws.player-id` and `aws.api-gateway.region`

### 4. Setup workflow — repurpose from Google Drive flow
- [x] Update `SetupController` / `SetupStateService` to collect player ID and region selection (US / Europe / Asia-Pacific) instead of Google Drive folder IDs and worksheet IDs
- [x] Write `aws.player-id` and `aws.api-gateway.region` to `user.properties` on completion
- [x] Update setup UI in `index.html` / `app.js` accordingly

### 5. Multi-region IngestStack deployments
The Android app already has stub URLs for EU and AP that will be activated once the stacks exist.

- [x] Deploy `IngestStack` to `eu-west-1` pointing at the central `us-east-2` S3 bucket
- [x] Deploy `IngestStack` to `ap-northeast-1` pointing at the central `us-east-2` S3 bucket
- [x] Deploy `GET /credentials` Lambda to all three regions (credential vending must be on the same regional endpoint as report ingest)
- [x] Update stub URLs in `Region.kt` (Android) with the real EU and AP API Gateway URLs once deployed
- [x] Update `application.properties` with the real EU and AP URLs

### 6. Minor / cleanup
- [x] Add `updated_at` attribute to DynamoDB `PutItemRequest` in `syncVersionToDdb` (currently only writes `player_id` and `current_version`; `updated_at` is in the schema)
- [x] `POST /{version}/sync-sheet` (`retrySync`) still calls Google Sheets — acceptable for legacy-mode users, but consider whether it should be a no-op or removed when AWS is configured — now a no-op in centralized mode (`aws.isConfigured()`); legacy users unaffected. Also gated `syncVersionCell` so version creates write only DynamoDB when AWS is configured.

### 7. Version-sync banner UI was Google-Sheet-specific
The create response previously returned `syncedToSheet`, and the front-end failure banner / retry button were hard-coded to Google Sheets even in centralized mode.

- [x] Rename create response field `syncedToSheet` → `synced`; add `syncTarget` (`"ddb"` | `"sheet"`) from `aws.isConfigured()`
- [x] Banner text in `app.js` now names the actual target (central DynamoDB vs Google Sheet cell B2)
- [x] Retry button posts to `/sync-ddb` in centralized mode, `/sync-sheet` in legacy mode

### 8. Database backup — S3 instead of Google Drive
The legacy flow (`BackupController.POST /api/backup/database`) copies the local SQLite DB
(`%APPDATA%\TheTowerAnalyzer\analyzer.db`) to a temp file and uploads it to a Google Drive
folder (`driveProperties.getBackupFolderId()`) via `GoogleDriveRepository.uploadFile`. In
centralized mode there is no Drive — the backup should go to the shared reports S3 bucket,
under the player's own prefix so it is writable with the player's vended credentials.

**What the existing infra already gives us:**
- The vended STS session policy (`infra/lambda/credentials.mjs`) already allows
  `s3:PutObject` / `s3:PutObjectTagging` / `s3:CopyObject` / `s3:GetObject` /
  `s3:ListBucket` on `arn:aws:s3:::<bucket>/<player_id>/*`. **One permission had to be
  added** (`s3:GetObjectTagging`): `listBackups` reads each object's `type` tag to flag the
  kept-forever latest, and reading tags is a distinct action from writing them. It was added
  to both the vended session policy (`credentials.mjs`) and the role identity policy
  (`data-stack.ts` `S3PlayerAccess`) — effective permission is the intersection of the two,
  so both must grant it. No new *role/Lambda resources*, just the one action.
- The bucket is SSE-S3 encrypted, so the backup is encrypted at rest automatically.
- `S3ReportFetcherService.listKeys` filters on `.endsWith(".txt")`, so a `.db` object under
  the player prefix is **never** picked up as a battle report — no ingest collision.

**Object key & retention**
- [x] Write backups to `<player_id>/backups/analyzer_<yyyy-MM-dd_HH-mm-ss>.db` (mirrors the
  legacy timestamped filename; the `backups/` segment keeps them visually separate from
  reports under the same prefix).
- [x] **Retention goal:** the single newest backup is kept in Standard **forever** (any age);
  every *older* backup is simply deleted 30 days after it was superseded. No Glacier tiering —
  the latest backup is almost always what a user restores, so cold-storing the rest isn't worth
  its cost/complexity (Glacier Instant Retrieval also carries a 90-day minimum-storage charge,
  which a 30-day expiry would trip). S3 lifecycle has no notion of "the newest object" — it acts
  purely on age + tag/prefix — so "always keep latest" must be enforced by the app via tags,
  with lifecycle handling only the expiry of the demoted ones. Implemented (app side) with a **two-tag scheme**:
  - On upload, tag the new object `type=backup-latest`.
  - In the same operation, **demote the previous latest** from `type=backup-latest` →
    `type=backup`. Do the demotion as an **in-place `CopyObject`** (same bucket + key) with
    `MetadataDirective=REPLACE` and `TaggingDirective=REPLACE` setting `type=backup` — **not** a
    plain `PutObjectTagging`. The copy rewrites the object, which **resets its `LastModified`/
    creation date**, so the 30-day expiry clock below starts at the moment of supersession
    (a bare re-tag would leave the original creation date, expiring from first upload instead).
    The self-copy needs a metadata/storage-class change to be legal, which `MetadataDirective=REPLACE`
    provides; `s3:CopyObject` + `s3:PutObjectTagging` are already vended and the bucket is
    un-versioned, so this is a straight in-place overwrite. Find the previous latest by listing
    `<player_id>/backups/` and reading tags; if more than one somehow carries `backup-latest`,
    demote all but the newest.
  - Never tag a backup `processed=true` — that maps to the existing 1-day-to-Glacier rule.
- [x] Added a **tag-based** lifecycle rule to `DataStack` `ReportsBucket` filtering
  `tagFilters: { type: 'backup' }` (i.e. demoted, non-latest backups only):
  `expiration` (delete) after 30 days, no transition. Objects tagged `type=backup-latest` are
  **not matched** by this rule, so the newest survives regardless of age. A prefix rule can't be
  used — the `backups/` segment is nested under the per-player prefix — so the tag is the only
  clean lever. Keep retention out of the per-request path. `cdk synth` confirms
  `ExpirationInDays: 30` filtered on tag `type=backup`.
- [x] **Expiry clock — exact by construction:** because demotion rewrites the object via
  `CopyObject` (above), its creation date is reset to the supersession moment, so lifecycle
  expiration fires exactly **30 days after a backup stopped being the latest** — regardless of
  how long it was the latest beforehand. "Kept for 30 days after superseded" is therefore
  literally true and safe to state verbatim in the UI.

**Backend**
- [x] Added a dedicated `S3BackupRepository` (conditional on `S3Client`, like `S3ReportRepository`)
  using `s3.putObject(PutObjectRequest, RequestBody.fromFile(...))` with `Tagging` `type=backup-latest`,
  plus in-place `CopyObject` demotion (`MetadataDirective`/`TaggingDirective=REPLACE` → `type=backup`),
  listing, and `getObject`-to-file. Orchestration lives in `S3BackupService` (per the "logic in
  service/" convention) with AccessDenied → re-vend-credentials retry mirroring `S3ReportFetcherService`.
- [x] Reworked `BackupController` to branch on `aws.isConfigured()`:
  - centralized → copy `analyzer.db` to a temp file, `putObject` to
    `<player_id>/backups/analyzer_<ts>.db`, return `{ "bucket", "key", "target": "s3" }`.
  - legacy → unchanged Drive path, return existing `{ "fileId", "fileName", "target": "drive" }`.
  - Injects `S3BackupService` via `ObjectProvider` so legacy users with no `S3Client` bean still
    start, and Drive users keep working.
- [x] Kept the endpoint at `POST /api/backup/database` (UI contract stays stable); only the
  response body and destination change.

**Restore — required in centralized mode (new vs. legacy)**
The Drive flow never needed restore because the folder was the user's own Drive — they could
download/replace by hand. Here the bucket is centralized and users have **no** direct S3
access, so without an in-app restore the backup is write-only and useless for recovery. The
vended session policy already grants `s3:ListBucket` (prefix-conditioned) and `s3:GetObject`
on `<player_id>/*`, so restore needs **no new permissions**.
- [x] `GET /api/backup/list` (centralized) — lists `<player_id>/backups/` objects with key,
  size, `LastModified`, and which one is `type=backup-latest`. Drives a restore picker in the UI.
- [x] `POST /api/backup/restore` with a `key` — validates the key is under the caller's
  `<player_id>/backups/` prefix (defense-in-depth even though vended creds already scope it),
  `getObject`s it to a **staging file** next to the DB (`analyzer.db.restore`).
- [x] **SQLite-in-use concern:** Hibernate/HikariCP holds `analyzer.db` open for the life of
  the process, so we cannot overwrite it live (WAL journal mode, open handles). Restore must
  **stage then swap on restart**: write `analyzer.db.restore`, and on next startup detect the
  staging file, move the current db aside, move the staging file into place, then continue boot.
  Hook this in `TowerAnalyzerApplication.main()` as a new `static applyStagedRestoreIfPresent()`
  called **immediately after** `installBundledDatabaseIfAbsent()` and **before**
  `SpringApplication.run()` — the existing static-method pattern is there precisely because the
  swap "must happen before any datasource bean is initialized, so it cannot live in a Spring
  component" (see the comment on `installBundledDatabaseIfAbsent`). Surface this as a
  restart-required operation in the UI ("Restore staged — restart the app to apply").
- [x] Legacy/Drive mode: no restore endpoint needed (users own the Drive folder); gate the
  restore UI on `aws.isConfigured()`.

**Front end**
- [x] Updated the backup button handler / success banner in `app.js` to read `target` and name
  the actual destination ("Backed up to cloud storage" vs "Backed up to Google Drive"), the
  same pattern used for the version-sync banner in item 7.
- [x] Added a restore panel (centralized only) in the Admin → Database card: lists backups from
  `GET /api/backup/list` (with a `latest` badge, timestamp, and size), lets the user pick one,
  calls `POST /api/backup/restore`, then shows the "restart to apply" notice. Gated naturally —
  the panel stays hidden when `/backup/list` returns 409 (legacy mode, no S3 backup service).
