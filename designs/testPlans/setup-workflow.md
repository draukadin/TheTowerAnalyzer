# Test Plan — Setup Workflow (Action Item #4)

**Branch:** `feature/centralized-platform`
**Date:** 2026-06-22
**Scope:** `SetupStateService`, `OAuthStateService`, `SetupController`, `index.html` wizard, `app.js` wizard JS

---

## 1. SetupStateService

### 1.1 `currentStep()` — no player ID

**Precondition:** `AwsProperties.playerId` is `null` or blank  
**Expected:** `currentStep()` returns `Step.CONFIG`  
**Also verify:** `isComplete()` returns `false`

### 1.2 `currentStep()` — player ID set

**Precondition:** `AwsProperties.playerId` is a non-blank string (e.g. `"abc123"`)  
**Expected:** `currentStep()` returns `Step.COMPLETE`  
**Also verify:** `isComplete()` returns `true`

### 1.3 Enum values

**Verify:** `Step` has exactly two values — `CONFIG` and `COMPLETE`. The old `CREDENTIALS` value must not exist.

---

## 2. OAuthStateService — AWS bypass

### 2.1 `init()` skips OAuth when AWS is configured

**Precondition:** `AwsProperties.isConfigured()` returns `true` (player ID is set)  
**Expected:** `init()` logs the skip message and returns immediately; no `CompletableFuture` is started; the Google OAuth flow is never accessed.  
**How to verify:** Check app startup logs — should see `"AWS centralized mode — Google OAuth not required."` and no OAuth URL printed.

### 2.2 `getStatus()` returns AUTHENTICATED when AWS is configured

**Precondition:** `AwsProperties.isConfigured()` returns `true`  
**Call:** `GET /api/auth/status`  
**Expected:** `{ "status": "authenticated" }` (no Google credential required)

### 2.3 `getStatus()` returns PENDING when AWS is not configured

**Precondition:** `AwsProperties.playerId` is blank (legacy / unconfigured)  
**Call:** `GET /api/auth/status`  
**Expected:** `{ "status": "pending" }` (normal Google OAuth path, unaffected)

---

## 3. SetupController — `GET /api/setup/status`

### 3.1 Status when player ID is blank

**Precondition:** `user.properties` does not exist or has blank `aws.player-id`  
**Expected:** `{ "step": "config" }`

### 3.2 Status when player ID is set

**Precondition:** `user.properties` contains a non-blank `aws.player-id`  
**Expected:** `{ "step": "complete" }`

---

## 4. SetupController — `POST /api/setup/config`

### 4.1 Happy path — US region

**Request body:** `{ "playerId": "abc123", "apiGatewayRegion": "us" }`  
**Expected response:** `200 { "step": "complete" }`  
**Side effects:**
- `user.properties` written to `%APPDATA%\TheTowerAnalyzer\user.properties`
- File contains exactly two lines: `aws.player-id=abc123` and `aws.api-gateway.region=us`
- Live `AwsProperties` bean updated (subsequent `GET /api/setup/status` returns `complete`)

### 4.2 Happy path — EU region

**Request body:** `{ "playerId": "player1", "apiGatewayRegion": "eu" }`  
**Expected:** `200 { "step": "complete" }` ; `user.properties` contains `aws.api-gateway.region=eu`

### 4.3 Happy path — AP region

**Request body:** `{ "playerId": "player1", "apiGatewayRegion": "ap" }`  
**Expected:** `200 { "step": "complete" }` ; `user.properties` contains `aws.api-gateway.region=ap`

### 4.4 Blank player ID

**Request body:** `{ "playerId": "", "apiGatewayRegion": "us" }`  
**Expected:** `400 { "error": "Player ID is required." }`

### 4.5 Null player ID

**Request body:** `{ "apiGatewayRegion": "us" }`  
**Expected:** `400 { "error": "Player ID is required." }`

### 4.6 Invalid region

**Request body:** `{ "playerId": "abc", "apiGatewayRegion": "ca" }`  
**Expected:** `400 { "error": "Region must be one of: us, eu, ap." }`

### 4.7 Null / missing region

**Request body:** `{ "playerId": "abc" }`  
**Expected:** `400 { "error": "Region must be one of: us, eu, ap." }`

### 4.8 `user.properties` directory creation

**Precondition:** `%APPDATA%\TheTowerAnalyzer\` does not exist  
**Expected:** Directory is created; `user.properties` is written successfully

---

## 5. Wizard UI — `index.html` / `app.js`

### 5.1 Step indicator — initial render (step 1 active)

On `showSetupWizard()`:
- Dot 1 is accent-coloured with label `"1"` and text `"Player Setup"` in accent colour
- Dot 2 is muted with label `"2"` and text `"Claude MCP"` in muted colour
- Step 1 content is visible; step 2 content is hidden
- **Verify no third dot or separator exists in the DOM**

### 5.2 Step indicator — step 2 active

After `goToWizardStep(2)`:
- Dot 1 shows green checkmark (`✓`)
- `"Player Setup"` label is green
- Dot 2 is accent-coloured with text `"2"`; `"Claude MCP"` label is in accent colour
- Step 1 content is hidden; step 2 content is visible

### 5.3 Player ID validation — empty field

Click **Continue →** with the Player ID field empty:
- Error div shown: `"Player ID is required."`
- Wizard does not advance to step 2
- Button remains enabled

### 5.4 Player ID submission — success

Enter a non-blank Player ID, leave region on US (default), click **Continue →**:
- Button shows `"Saving…"` and is disabled during the request
- On `200` response, wizard advances to step 2 (Claude MCP)

### 5.5 Player ID submission — server error

With the server returning `400` or `500`:
- Error div shows the message from the response (or generic `"An error occurred."`)
- Button is re-enabled and text resets to `"Continue →"`

### 5.6 Player ID submission — network failure

With the server unreachable:
- Error div shows `"Could not reach the server."`
- Button is re-enabled

### 5.7 Region radio default

On initial render: **United States** radio is checked; **Europe** and **Asia-Pacific** are unchecked.

### 5.8 Region radio — EU selection

Select **Europe**, enter a valid Player ID, submit:
- POST body contains `"apiGatewayRegion": "eu"`

### 5.9 Region radio — AP selection

Select **Asia-Pacific**, enter a valid Player ID, submit:
- POST body contains `"apiGatewayRegion": "ap"`

### 5.10 Old elements absent

Verify the following IDs do not exist in the rendered DOM:
- `wizDot3`, `wizInd3`, `wizLabel3`, `wizardStep3`
- `credentialsJson`, `credentialsBtn`, `credentialsError`
- `backupFolderId`, `battleReportsFolderId`, `playerTrackerSheetId`

---

## 6. End-to-end — fresh install flow

**Precondition:** `%APPDATA%\TheTowerAnalyzer\user.properties` does not exist (or `aws.player-id` is blank); app is running.

1. Open `http://localhost:8080` — setup wizard appears at step 1
2. `GET /api/setup/status` returns `{ "step": "config" }`
3. Enter a valid Player ID and select a region; click **Continue →**
4. `POST /api/setup/config` returns `200 { "step": "complete" }`
5. Wizard advances to step 2 (Claude MCP)
6. Complete or skip MCP step; wizard closes and `checkAuthAndInit()` is called
7. `GET /api/auth/status` returns `{ "status": "authenticated" }` (AWS path — no Google prompt)
8. `init()` fires; main app loads
9. Restart the app — wizard does not reappear; app loads directly to main view

---

## 7. Regression — legacy Google Drive path

Verify that users without `aws.player-id` set (who have Google OAuth credentials) still flow through:
- `GET /api/setup/status` → `{ "step": "config" }` — but this path is now AWS-only; the old credentials step is gone. (Legacy Drive users are expected to be migrated or unsupported on this branch.)

> **Note:** The `POST /api/setup/credentials` endpoint has been removed. If any client still calls it, it will receive a `404`.
