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
- [x] Centralized shortcut (`Send Battle Report (Centralized).shortcut`) — prompts for player ID, run type, optional Dissonance sub-type

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
- [ ] Add `aws.api-gateway.region` to `AwsProperties` (values: `eu`, `ap`); pending deployment of EU & AP stacks
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
- [ ] Update `SetupController` / `SetupStateService` to collect player ID and region selection (US / Europe / Asia-Pacific) instead of Google Drive folder IDs and worksheet IDs
- [ ] Write `aws.player-id` and `aws.api-gateway.region` to `user.properties` on completion
- [ ] Update setup UI in `index.html` / `app.js` accordingly

### 5. Multi-region IngestStack deployments
The Android app already has stub URLs for EU and AP that will be activated once the stacks exist.

- [ ] Deploy `IngestStack` to `eu-west-1` pointing at the central `us-east-2` S3 bucket
- [ ] Deploy `IngestStack` to `ap-northeast-1` pointing at the central `us-east-2` S3 bucket
- [ ] Deploy `GET /credentials` Lambda to all three regions (credential vending must be on the same regional endpoint as report ingest)
- [ ] Update stub URLs in `Region.kt` (Android) with the real EU and AP API Gateway URLs once deployed
- [ ] Update `application.properties` with the real EU and AP URLs

### 6. Minor / cleanup
- [ ] Add `updated_at` attribute to DynamoDB `PutItemRequest` in `syncVersionToDdb` (currently only writes `player_id` and `current_version`; `updated_at` is in the schema)
- [ ] `POST /{version}/sync-sheet` (`retrySync`) still calls Google Sheets — acceptable for legacy-mode users, but consider whether it should be a no-op or removed when AWS is configured
