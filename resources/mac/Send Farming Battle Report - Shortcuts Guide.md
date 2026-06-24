# Send Farming Battle Report — Apple Shortcuts Guide (Legacy)

> **New users:** Use the **Send Battle Report** shortcut instead — it works with the
> centralized AWS backend and requires no make.com account or webhook URL.
> Install via the iCloud link in the setup guide (`resources/Centralized_Setup_Guide.md`).
>
> This guide covers the original make.com shortcut for users already on the legacy
> Google Drive + make.com pipeline who want an iOS equivalent of the Tasker task.

Replicates the Tasker task of the same name. Reads a battle report from the clipboard, asks which run type it is, then POSTs it to your make.com webhook.

---

## Before You Start

You need two values from your make.com scenario. Open the shortcut after importing and set these as Text actions at the top (or hard-code them directly into the relevant steps below):

| Variable | Where to find it |
|---|---|
| `api_key` | Your make.com API key |
| `api_url` | The suffix of your webhook URL (everything after `https://hook.us2.make.com/`) |

---

## Shortcut Steps

### Step 1 — Set Variables
**Action:** `Text`
- Content: *(paste your make.com API key)*
- **Action:** `Set Variable` → name it **`api_key`**

**Action:** `Text`
- Content: *(paste your make.com webhook URL suffix)*
- **Action:** `Set Variable` → name it **`api_url`**

> These two steps replace the `%api_key` and `%api_url` Tasker variables.

---

### Step 2 — Get Clipboard
**Action:** `Get Clipboard`

This retrieves the battle report text you copied from The Tower.

- Store the result: **`Set Variable`** → name it **`battle_report`**

---

### Step 3 — Choose Run Type
**Action:** `Choose from List`
- **Items (enter one per line):**
  - Farming
  - Tournament
  - Milestone
  - Dissonance
  - Event
- **Prompt:** `Select Run Type`

- Store the result: **`Set Variable`** → name it **`run_type`**

> This replicates the Tasker "Dialog / Pick from List" action. The user taps their choice and it's stored for use in the next step.

---

### Step 4 — Build the Webhook URL
**Action:** `Text`
- Content:
  ```
  https://hook.us2.make.com/[api_url]?runType=[run_type]
  ```
  - Tap `[api_url]` and replace with the **`api_url`** variable
  - Tap `[run_type]` and replace with the **`run_type`** variable

- **Action:** `Set Variable` → name it **`webhook_url`**

---

### Step 5 — Send HTTP POST
**Action:** `Get Contents of URL`
- **URL:** select the **`webhook_url`** variable
- **Method:** `POST`
- **Headers:**
  - `x-make-apikey` → select the **`api_key`** variable
  - `Content-Type` → `text/plain`
- **Request Body:** `File`
  - Select the **`battle_report`** variable

> "File" body type is the closest equivalent to Tasker's raw plain-text POST body. Alternatively, use **Text** body type with the `battle_report` variable — either works with make.com.

---

### Step 6 — Show Confirmation
**Action:** `Show Notification`  
*(or use `Show Result` for a simpler in-app alert)*
- **Title:** `Report Sent!`
- **Body:** `🚀 ` + **`run_type`** variable + ` Report Sent to Cloud!`

> Replicates the Tasker Flash/Toast notification at the end.

---

## Optional: Add a Home Screen Icon

1. In the Shortcuts app, tap the shortcut's **`...`** (edit) button
2. Tap the icon area at the top to customise colour/glyph
3. Tap **Add to Home Screen** to get a one-tap launcher

---

## Triggering the Shortcut

Unlike Tasker (which can be triggered by events), Shortcuts on iOS requires a manual trigger or one of:

- **Home Screen icon** — tap after copying the battle report
- **Share Sheet** — add `ActionExtension` type so it appears when you share text from The Tower
- **Siri** — say "Send Farming Report"
- **Back Tap** (Settings → Accessibility → Touch → Back Tap) — double/triple tap the back of your phone

The most natural workflow is: copy the report in-game → tap the Home Screen icon → pick run type → done.
