# TheTowerAnalyzer — Video 2: Complete Setup Guide Script
**Version:** 1.0  
**Last Updated:** 2026-06-09  
**Estimated Runtime:** 25–35 minutes  
**Tone:** Calm, methodical, thorough. Viewer is in work mode.

> **HOW TO USE THIS SCRIPT**
> Lines marked [VISUAL] are screen recording cues.
> Lines marked [NOTE] are reminders to the presenter — do not read aloud.
> Text in [BRACKETS] are placeholders to fill in before recording.
> Section time estimates are targets, not hard limits.

---

## INTRO
**Target time: 2–3 minutes**

---

[VISUAL: Start on the TheTowerAnalyzer app running with data, then pull back to a clean desktop.]

**NARRATION:**

"Welcome to the full setup guide for TheTowerAnalyzer. If you came here from the showcase video, you've already seen what the app can do — now we're going to get it running for you from scratch.

This video is broken into four chapters. Chapter one covers downloading and installing the app and setting up the Google Cloud OAuth credentials. Chapter two covers the Google Drive folder structure, the version tracker spreadsheet, configuring the app, and the first launch. Chapter three covers importing and configuring the make.com scenario that processes your battle reports. And chapter four covers getting the Tasker task on your Android device and connecting everything together end to end.

Before we start, let me give you the full list of things you'll need:

[VISUAL: Show a simple list on screen as each item is read.]

One — a Windows PC. TheTowerAnalyzer is a Windows application.

Two — a Google account with Google Drive. If you use Gmail, you already have this.

Three — a Google Cloud account to set up OAuth credentials. This is free and uses the same Google account.

Four — a make.com account. The free tier is sufficient to get started.

Five — an Android device with Tasker installed. Tasker is a paid app on the Google Play Store — it's a few dollars and absolutely worth it.

Six — The Tower installed and running on that same Android device.

All the links referenced in this video are in the description below, including a Google Doc reference guide with screenshots of every step. I'd recommend having that open alongside this video — it'll save you a lot of pausing and rewinding.

Alright — let's get into it."

[VISUAL: Cut to Chapter 1 title card.]

---

## CHAPTER 1 — Installing TheTowerAnalyzer & Setting Up Google Cloud OAuth
**Target time: 8–10 minutes**

---

[VISUAL: Chapter 1 title card, then cut to a browser.]

**NARRATION:**

"Chapter one. We're going to download and install the app, then set up the Google Cloud credentials it needs to access your Google Drive and Google Sheets."

---

### 1A — Downloading and Installing the App

[VISUAL: Open a browser and navigate to https://github.com/draukadin/TheTowerAnlyzer/releases]

**NARRATION:**

"Head to the GitHub releases page — the link is in the description. You're looking for the latest release, which right now is version 0.0.1.

[VISUAL: Point to the installer file in the Assets section.]

Under the Assets section, download the installer. Once it's downloaded, run it.

[VISUAL: Run the installer, click through the steps.]

The installer will put the application files into C:\Program Files\TheTowerAnalyzer. Go ahead and click through the installation — the defaults are fine.

[VISUAL: Installation completes.]

Once the install finishes, don't launch the app yet — we need to set up the Google Cloud credentials first, otherwise it won't have permission to talk to your Google Drive."

---

### 1B — Setting Up Google Cloud OAuth Credentials

[VISUAL: Open a browser and navigate to https://console.cloud.google.com]

**NARRATION:**

"Open a browser and go to console.cloud.google.com. Sign in with the Google account whose Drive you want to use for TheTowerAnalyzer — this is important, make sure it's the right account.

Once you're in, we need to create a new project."

[VISUAL: Click 'Select a project' dropdown at the top, then 'New Project'.]

**NARRATION:**

"Click the project selector at the top of the page, then click New Project. Give it a name — something like TheTowerAnalyzer works fine. Leave the organization as-is and click Create.

[VISUAL: Project is created. Select it from the dropdown.]

Once it's created, a notification will pop up on the right side; Click on Select Project once creation finishes — you should see the project name in the top bar."

---

**Enabling the Required APIs**

[VISUAL: In the left sidebar, navigate to APIs & Services > Library.]

**NARRATION:**

"Now we need to enable two APIs — Google Drive and Google Sheets. In the left sidebar, click APIs and Services, then Library.

[VISUAL: Search for 'Google Drive API'.]

Search for Google Drive API. Click on it, then click Enable.

[VISUAL: API is enabled. Navigate back to Library.]

Go back to the Library and do the same thing for Google Sheets API — search for it, click it, click Enable.

[VISUAL: Google Sheets API enabled.]

"Now both APIs are now active for this project."

---

**Creating the OAuth Credentials**

[VISUAL: Navigate to APIs & Services > OAuth consent screen.]

**NARRATION:**

"Next we need to set up the OAuth consent screen. This is the screen that appears when the app asks for permission to access your Drive. In the sidebar, click OAuth consent screen.

[VISUAL: Select 'External' user type.]

Select External for the user type and click Create.

[VISUAL: Fill in the App name field.]

On the next screen, fill in the App name — again, TheTowerAnalyzer is fine. Set the user support email to your Google account. Scroll down and add your Google account as the developer contact email as well. You can leave everything else blank. Click Save and Continue.

[VISUAL: Scopes screen. Click 'Add or Remove Scopes'.]

Navigate to Data Access, Once there click Add or Remove Scopes. We need to add two scopes.

[VISUAL: Search for and select each scope as it's mentioned.]

In the filter type Drive and select https://www.googleapis.com/auth/drive Click on the filter again and under operators select OR and then type sheets and select https://www.googleapis.com/auth/spreadsheets  Now click the check boxes next to each Scope and then click Update.  These should now show up on the main part of the page.  The sheets will be under sensitive scopes and the drive will be under restricted scopes.  Click Save at the bottom to complete this step.

[VISUAL: Audience users screen.]

On the Audience Users screen, under Test users, click Add Users and add your Google account email. This is required while the app is in External testing mode — it means only accounts you explicitly add here can use the OAuth credentials. Click Save.  Your email should now show up in the User information section."

---

[VISUAL: Navigate to APIs & Services > Credentials.]

**NARRATION:**

"Now we can create the actual credentials. Click Clients in the sidebar, then click Create Client at the top.

[VISUAL: Application type dropdown.]

For Application type, select Desktop app. Give it a name — TheTowerAnalyzer Desktop is fine. Click Create.

[VISUAL: Credentials created dialog appears.]

A dialog will appear showing your client ID and client secret. Click Download JSON — this downloads the credentials file that the app needs.  If you ever lose this file you will need to create a new client as the data in the JSON is only available this one time.

[VISUAL: File downloads.]

This file will be named something like client_secret_[long string].json and should be in your Downloads folder."

---

**Chapter 1 Verification:**

**NARRATION:**

"Chapter 1 is complete when the app is installed, both Google APIs are enabled, your OAuth credentials are downloaded.

Don't launch the app yet — we need the Google Drive folders, otherwise the app won't know where to look for battle reports or where to maintain the version number or upload your database backups. We'll do the first launch at the end of Chapter 2."

[VISUAL: Chapter 2 title card.]

---

## CHAPTER 2 — Google Drive Folder Setup & Version Tracker Spreadsheet
**Target time: 5–6 minutes**

---

[VISUAL: Chapter 2 title card, then open Google Drive in a browser.]

**NARRATION:**

"Chapter two is about setting up the Google Drive structure that TheTowerAnalyzer and the make.com scenario rely on. We need to create one parent folder, two subfolders inside it, and one spreadsheet. Then we'll grab the IDs for the two folders and the spreadsheet and drop them into the app's config file.

Let's start in Google Drive."

---

### 2A — Creating the Parent Folder

[VISUAL: Google Drive open. Click 'New' > 'Folder'.]

**NARRATION:**

"First, let's create a parent folder to keep everything organized. Click New, then Folder, and name it TheTower — or whatever name makes sense to you. This is just for your own organization; the app doesn't care what it's called.

[VISUAL: TheTower folder created. Double-click to open it.]

Open that folder. Everything we create next goes inside here."

---

### 2B — Creating the Battle Reports Folder

[VISUAL: Inside the TheTower folder. Click 'New' > 'Folder'.]

**NARRATION:**

"Inside the TheTower folder, create a new subfolder. I called mine Battle Reports — but again, the name doesn't matter. The app tracks this folder by its ID, not its name, so call it whatever you like.  However, the make.com scenario will use the folder name, Spreadsheet name and sheet name for its configuration so keep it simple.  Open that folder in a new tab.  We will need it later.

---

### 2C — Creating the Database Backup Folder

[VISUAL: Navigate back into the TheTower parent folder. Click 'New' > 'Folder'.]

**NARRATION:**

"Go back into the TheTower folder and create a second subfolder. I called mine DbBackups — same deal, the name is just for you.  Also open that folder in a new tab."

---

### 2D — Creating the Version Tracker Spreadsheet

[VISUAL: Navigate back into the TheTower parent folder. Click 'New' > 'Google Sheets' > 'Blank spreadsheet'.]

**NARRATION:**

"Back in the TheTower folder, create a new Google Sheet. Click New, then Google Sheets, then Blank spreadsheet.

[VISUAL: New spreadsheet opens in a new tab.]

You can name the spreadsheet file whatever you want — the app tracks it by ID. But there are two things about the sheet setup that do matter, so pay close attention here.

[VISUAL: At the bottom of the spreadsheet, right-click the default Sheet1 tab.]

First — the sheet tab name. Right-click the Sheet1 tab at the bottom and rename it to exactly:

TowerVersionTracking

[VISUAL: Tab is renamed to TowerVersionTracking. Show it clearly.]

The spelling and capitalization here matters — the make.com scenario looks for a sheet with this exact name.

[VISUAL: Click on cell B2.]

Second — the seed value. Click on cell B2 and type:

1.0.0

[VISUAL: 1.0.0 is entered in cell B2.]

This is your starting Tower version. When the make.com scenario processes your very first battle report, it reads this cell to know what version to tag the file with. Without it, that first run has nothing to read and will error. After that, the application manages this value automatically — you never need to touch it again.

[VISUAL: Spreadsheet showing the TowerVersionTracking tab and 1.0.0 in B2.]

Now grab the spreadsheet ID. Look at the URL in your browser — the ID is the long string between /d/ and /edit.

[VISUAL: Highlight the spreadsheet ID in the URL bar.]

Copy it and save it with your two folder IDs. This is the value for sheets.ids.player-tracker."

---

### 2F — First Launch Setup Wizard & OAuth Authorization

[VISUAL: Launch TheTowerAnalyzer from the Start Menu or desktop shortcut.]

**NARRATION:**

"Now that the Google Drive is setup, we have our credentials, and the App is installed we're ready to launch the app for the first time.

Find TheTowerAnalyzer in the Start Menu or double click on the short cut icon on the desktop and run it.

[VISUAL: A console window appears showing Spring Boot startup logs.]

You'll see a console window appear — this is the application server starting up. TheTowerAnalyzer is a Spring Boot web application, which means it runs as a local server on your machine and you access it through a browser. The browser should open up automatically and take you into the setup wizard.

[VISUAL: Wait for the startup logs to settle, and the browser to open to localhost:8080.]

The one issue that could happen here is that something else is running on port 8080.  If that happens, the console window will close.  We can find out what happened by pressing "windows key + r" and enter "%appdata%/TheTowerAnalyzer" and press ok.  This will open up the location where the configuration and logs are stored.  Go into the logs folder and open the log.  You should something like this at the end of the logs

```code
***************************
APPLICATION FAILED TO START
***************************

Description:

Web server failed to start. Port 8080 was already in use.

Action:

Identify and stop the process that's listening on port 8080 or configure this application to listen on another port.
```

Go up a level from the logs folder back to TheTowerAnalyzer folder and open user.properties in a text editor.
Delete the # sign and white space in front of server.port=8080 and change the port to 8081.  Now try to re-run the application. 

[VISUAL: The Tower Analyzer Setup Wizard - Set up Google Credentials]

The setup wizard is a two step process and will handle populating the user.properties with your folder ids, sheet id and creating the oauth-credentials.json file for you.  To get started open up your downloads folder and open up the json file we downloaded after creating the credentials.  Copy the entire contents of the file into the text area and click Continue.

[VISUAL: The Tower Analyzer Setup Wizard - Configure Drive & Sheets]

Now go to one of the Google drive tabs and copy the id from the URL and paste it into the appropriate text box on the form.  Once all three are populated, click on Complete Setup.

[VISUAL: A Google sign-in or OAuth authorization screen appears.]

After completing the setup you will be immediately shown the Google Authorization Required pop up.  Click the Open Authorization Page button.  This will open up a new tab.  Select the google account you used to create the OAuth Client Id. 

[NOTE: Google may show an 'This app isn't verified' warning before the consent screen. Address this clearly — don't skip past it.]

You may see a warning that says this app isn't verified. That's expected — it appears because this OAuth project is one you created yourself in your own Google Cloud account, not a published app that's gone through Google's verification process. Click 'Continue' to proceed past it.


[VISUAL: Google shows the OAuth consent screen with the app name and the two scopes.]

Google will then show you a consent screen listing the permissions the app is requesting — access to your Drive files and your Sheets. This is the OAuth consent screen we configured in Chapter 1.


[VISUAL: User clicks through the warning and arrives at the consent screen. Clicks Allow.]

On the consent screen, click Allow. Google will authorize the app and you should be on this window that says Received verification code. You may now close this window.  Go ahead and close the window and return to The Tower Analyzer browser tab.  You should now see the main page of the app.  

[VISUAL: TheTowerAnalyzer loads fully in the browser. The Battle Reports screen is visible with the Fetch New Reports button.]

And there it is — the app is running and authorized. You can see the Battle Reports screen with the Fetch New Reports button. The app now has permission to read from and write to the Google Drive folders and spreadsheet you set up in this chapter.  Go ahead and click Fetch New Reports.  It should come back with no new reports found.

---

**Chapter 2 Verification:**

**NARRATION:**

"Chapter 2 is complete when: the TheTower folder exists in your Drive with both subfolders inside it, the Version Tracker spreadsheet has a tab named TowerVersionTracking with 1.0.0 in cell B2, all three IDs are in user.properties, and the app is running and authorized at localhost:8080 with no errors.  As one last verification step bring up the console and take a look at the logging.  The last line should be the Google OAuth URL and the ids should have logged the values as REPLACE_WITH_YOUR_*

Shut down the app by closing the console.  Now restart the console.  This time you should not see the setup wizard or the OAuth verification step in the
browser.  And in the console the REPLACE_WITH_YOUR_ values should be replaced with the actual values and no OAuth URL should be logged.

That's the entire local setup done. On to make.com."

[VISUAL: Chapter 3 title card.]

---

## CHAPTER 3 — make.com Scenario Setup
**Target time: 7–9 minutes**

---

[VISUAL: Chapter 3 title card, then open a browser.]

**NARRATION:**

"Chapter three. We're going to import the make.com scenario that processes your battle reports — this is the piece that sits in the middle of the pipeline, taking data from Tasker and putting it into your Google Drive in a format TheTowerAnalyzer can read."

---

### 3A — Importing the Scenario

[VISUAL: Naviagate to https://www.make.com]

If you don't already have a make.com account click on the Get started for free in the upper right.  Click on Sign up with Google and click on your account. And then click on Sign up for free again.  Answer the survey questions.

[VISUAL: Navigate to https://us2.make.com/public/shared-scenario/bGx6dMyjduy/the-tower-battle-report-processor]

In another tab go to the shared scenario called tower battle report processor.  You read about what the scenario does on this page.
It consists of three parts:
1. The Webhook - Takser will call this with an HTTP Post and send the contents of your clip board and the type of run to this.
2. Google Sheets - This will read the version from the B2 cell in the sheet we created earlier.
3. Google Drive - This take the information from the Webhook and Google Sheets step to create the file to upload to the battle reports folder

You should see that this scenario was created by me: Draukadin.

Click Use this scenario.

**NARRATION:**

"Go to the scenario link in the description. You'll land on a make.com page showing the shared scenario for The Tower Battle Report Processor.

[VISUAL: The shared scenario page is visible. Click the button to add it to your account — the exact button text may say 'Use this scenario' or 'Import'.]

Click the button to add this scenario to your account. If you're not already logged into make.com, it will ask you to log in or create a free account first.

[VISUAL: Scenario is added and opens in the make.com scenario editor.]

Once it's imported, the scenario editor will open and you'll see the full pipeline laid out visually."

---

### 3B — Connecting Your Google Account to make.com

[VISUAL: The scenario is open. There will be Google Drive and/or Google Sheets modules visible with connection warnings.]

**NARRATION:**

"Before the scenario can work, we need to connect it to your Google account. You'll see some of the modules are flagged — that's because they don't have a Google connection yet.

[VISUAL: Click on a Google Drive module.]

Click on one of the Google Drive modules. You'll see a Connection field — click Add to create a new connection.

[VISUAL: Google account authorization screen appears.]

make.com will open a Google authorization window. Sign in with the same Google account you've been using throughout this setup, and click Allow.

[VISUAL: Connection is created and named.]

Once authorized, the connection will appear. You may need to select this same connection for the other Google modules in the scenario — click each one and set them to use the connection you just created.

[VISUAL: All Google modules now show the connection without warnings.]"

---

### 3C — Configuring the Google Drive Folder in the Scenario

[VISUAL: Click on the module that uploads battle reports to Google Drive.]

**NARRATION:**

"Now we need to point the scenario at the correct Google Drive folder — the Battle Reports folder we created in Chapter 2.

[VISUAL: The module settings panel is open. Find the folder field.]

Use the folder picker to navigate to TheTower/BattleReports.

[VISUAL: Correct folder is selected.]

[NOTE: If the make.com scenario also reads from the VersionTracker spreadsheet to build the file name, cover that here — describe which module it is and how to point it at the correct spreadsheet ID.]

The scenario also reads the current version from your Version Tracker spreadsheet when naming the output files. Click on the Google Sheets module [DESCRIBE WHICH ONE], find the spreadsheet field, and select or enter your TheTower-VersionTracker spreadsheet."

---

### 3D — Getting the Webhook URL

[VISUAL: Click on the Webhook module — typically the first module in the scenario, on the far left.]

**NARRATION:**

"The last thing we need from make.com is the webhook URL. This is the address that Tasker will send battle report data to."

[VISUAL: Click the webhook module. The webhook URL is visible in the settings panel.]

Click on the webhook module at the start of the scenario.
Click on Create a Webhook 
Give it a name like TowerAnalyzerBattleReports
Click on Add API key and then click on Create a keychain
Give the key a name like TaskerBattleReportsApiKey
Google will generate the API key value - Save this somewhere safe we will need it for the Tasker setup
Under Advanced settings, Change Get request headers to Yes and save.
The webhook should now be listening for data and below that the webhook url should now be available copy that to your note pad  Save that with the API key; we will need it for the tasker setup

Leave this modal open.  If you close it we won't be able to finish setting it up.  If you do close it just re-open it and click on Detect new values
That will put it back into the Listening for data mode

Copy that URL and save it somewhere — you'll need it in Chapter 4 when we configure Tasker. Do not share this URL publicly — it's the open door to your scenario.  Also we have protected it with the API Key so especially don't share that value.

---

**Chapter 3 Verification:**

**NARRATION:**

"Chapter 3 is complete when: the scenario is imported, your Google account is connected, the Battle Reports folder and Version Tracker spreadsheet are configured in the scenario, you have the webhook URL copied, and the scenario is active.

Before we can test the scenario we need to get tasker setup.  We will do that next.

[VISUAL: Chapter 4 title card.]

---

## CHAPTER 4 — Tasker Setup
**Target time: 7–9 minutes**

---

[VISUAL: Chapter 4 title card, then switch to showing an Android device screen — either direct screen capture or a phone recording.]

**NARRATION:**

"Chapter four — Tasker. This is the piece that runs on your Android device and lets you submit battle reports to make.com with just a couple of taps.

Before we get into setup, let me quickly explain how this works day-to-day — because it's a really clean workflow once it's running.

When a run ends, you go into your battle history in The Tower and open the battle report. There's a Copy to Clipboard button right on that screen — tap it. Then go to a Tasker widget on your home screen and tap it. A dropdown menu appears asking you to categorize the run — Farming, Tournament, Milestone, Dissonance, or Event. You pick the right one, and Tasker takes the clipboard data, tags it with that category, and fires it off to make.com. That's it. Ten seconds, two taps, run logged.

Now let's get it set up.

If you don't have Tasker yet, it's available on the Google Play Store — search for Tasker by João Dias. It's a few dollars and is one of the most powerful automation apps on Android. The link is in the description."

---

### 4A — Getting the Tasker Task via QR Code

[VISUAL: On your PC, switch to TheTowerAnalyzer at localhost:8080. Click the gear icon in the top right to open the Admin page.]

**NARRATION:**

"Getting the Tasker task onto your phone is easier than you might expect — the app handles it for you.

Click the gear icon in the top right of TheTowerAnalyzer to open the Admin page.

[VISUAL: Admin page showing two sections — Database with a Backup Database button, and Setup with a Show Tasker QR Code button.]

You'll see two sections here. Under Setup, click Show Tasker QR Code.

[VISUAL: A modal appears titled 'Tasker Battle Report QR Code' displaying a QR code.]

A QR code appears. Pick up your Android device, open your camera, and point it at the screen.

[NOTE: Make sure Tasker is already installed on the phone before scanning. If it isn't installed the QR code won't know what app to open.]

[VISUAL: Phone camera reads the QR code. Tasker opens on the phone and prompts the user to import the task.]

When your phone reads the code, Tasker opens automatically and prompts you to import the task. Tap to confirm.

[VISUAL: Task appears in Tasker's task list.]

The task is now imported and ready to configure. Click Close on the QR code modal on your PC."

---

### 4B — Configuring the Webhook URL

[VISUAL: In Tasker, tap on the imported task to open it in the task editor.]

**NARRATION:**

"Tap on the task to open it in the editor. Near the top of the task you'll see a variable for the webhook URL — it'll be clearly labeled and have a placeholder value in it.

[VISUAL: Find the webhook URL variable and tap on it to edit.]

Tap on that variable and replace the placeholder with the webhook URL you copied from make.com in Chapter 3. Paste it in exactly — no extra spaces before or after.

[VISUAL: Webhook URL is pasted in. Save the task.]

Save the task. That's the only thing you need to configure — the rest of the task is already built and ready to go."

---

### 4C — Adding the Tasker Widget to Your Home Screen

[VISUAL: Long-press on the Android home screen to bring up the widget picker.]

**NARRATION:**

"Now we need to put the Tasker widget on your home screen — this is what you'll tap after each run to submit your battle report.

Long-press on your home screen to bring up the customization options, then tap Widgets.

[VISUAL: Scroll through the widget list to find Tasker.]

Scroll until you find Tasker in the widget list. Tasker has a few widget types — you want the Task shortcut widget.

[VISUAL: Long-press the Task shortcut widget and drag it to the home screen.]

Long-press it and drag it to wherever you want it on your home screen — somewhere easy to reach one-handed is ideal since you'll be tapping it right after finishing a run.

[VISUAL: A dialog appears asking which task to link to the widget.]

A dialog will appear asking which task to assign to this widget. Select the battle report task you just imported.

[VISUAL: Widget appears on the home screen.]

The widget is now on your home screen and linked to the task. Put it wherever makes sense for how you use your phone — some people keep it on their main home screen, others put it on a second page they swipe to after a run. Entirely up to you."

---

### Make.com setup

[VISUAL: Go back to Make.com and make sure the web hook is in detect new values mode]
Now that the task is ready to send a report lets make sure make.com is ready to process it for the first time.
Open up the Modal for the Webhook if it isn't already and make sure it is listening for new values

### 4D — How to Use It: The Day-to-Day Workflow

[VISUAL: Open The Tower. Navigate to the battle history screen.]

**NARRATION:**

"Let me walk you through the exact steps you'll use after every run so you know exactly what to expect the first time.

Step one — finish your run. When the run ends, tap the battle history button and open your most recent battle report.

[VISUAL: The battle report screen is open. Point to the Copy to Clipboard button.]

Step two — tap the Copy to Clipboard button. The Tower copies all your run data to your clipboard. You won't see much happen visually — it's just sitting in the background ready to go.

[VISUAL: Navigate back to the home screen. Tap the Tasker widget.]

Step three — tap your Tasker widget. A dropdown menu appears with five options:

[VISUAL: Show the dropdown with the five categories listed.]

Farming. Tournament. Milestone. Dissonance. Event.

Tap the one that matches the run you just finished.

[VISUAL: Category is selected. Brief confirmation or the menu closes.]

Tasker reads the clipboard data, tags it with the category you selected, and sends it straight to your make.com webhook. Done."

---

### 4E — Verify Data Structure Detection in make.com

[VISUAL: Switch to make.com in a browser on your PC. The webhook modal should still be open in Listening for Data mode.]

**NARRATION:**

"Now let's go back to make.com and confirm it received the test payload from Tasker.

If you still have the webhook modal open, you should see that it has detected new values — make.com reads the incoming data and maps out the structure so it knows what fields to work with in the rest of the scenario.

[VISUAL: The webhook modal shows the detected data structure — fields like the run category, the clipboard contents, etc.]

Take a moment to confirm the data looks right — you should see the run category you selected and the battle report data from your clipboard. If the modal timed out or you closed it, click back into the webhook module and choose Detect New Values to put it back into listening mode, then send another report from Tasker.

Once the data structure is confirmed, click Save to close the modal.

[VISUAL: Quick check — switch to Google Drive and open the Battle Reports folder.]

As a quick sanity check, open your Battle Reports folder in Google Drive. If you see a file in there already — make.com processed the test payload automatically and you're already one step ahead. If the folder is empty that's fine too — the scenario isn't active yet, so nothing has been written. Either outcome is normal at this stage."

---

### 4F — Activating the Scenario

[VISUAL: In the make.com scenario editor, find the active toggle in the bottom left corner.]

**NARRATION:**

"Now that make.com has detected the data structure and the scenario is fully configured, we need to turn it on.

Find the active toggle in the bottom left of the scenario editor and switch it on.

[VISUAL: Scenario toggle switches to active/on.]

With the scenario active, any time Tasker sends a battle report to the webhook, make.com will process it immediately — reading the version from your spreadsheet, building the file, and uploading it to your Battle Reports folder in Google Drive.

If you leave the scenario inactive, Tasker will send data and nothing will happen on the make.com side. Don't skip this step."

---

### 4G — End-to-End Test

[VISUAL: The Tower open. Ready to walk through the full live workflow.]

**NARRATION:**

"Let's do a live end-to-end test right now and confirm the whole pipeline is connected.

[VISUAL: Complete a short run in The Tower — or open an existing battle report from history if you just want to test the pipeline without playing a full run.]

Finish a run — or open an existing one from your history. Either works for the test.

[VISUAL: Battle report screen. Tap Copy to Clipboard.]

Tap Copy to Clipboard.

[VISUAL: Navigate to the home screen. Tap the Tasker widget. Select a category.]

Tap your widget. I'll select Farming for this test.

[VISUAL: Switch to make.com in a browser — either on the Android device or on your PC.]

Now let's check make.com. Open your scenario and look at the execution history — you should see a new run appear within a few seconds.

[VISUAL: make.com shows a successful execution.]

There it is — processed successfully. Now let's check Drive.

[VISUAL: Open Google Drive and navigate to the TheTower-BattleReports folder.]

Open your TheTower-BattleReports folder. You should see a new file with your battle data inside.

[VISUAL: File is visible in the folder.]

And finally — over on your PC, open TheTowerAnalyzer at localhost:8080.

[VISUAL: TheTowerAnalyzer open in a browser. Click the Fetch Reports button.]

Click the Fetch Reports button. The app will reach out to your Google Drive, pull down any battle reports it hasn't processed yet, and load them into the database.

[VISUAL: The new battle report appears in the app.]

If you can see that run in the app — you're done. Clipboard to widget to make.com to Google Drive to TheTowerAnalyzer. The whole pipeline is live."

[NOTE: Worth mentioning to viewers that reports are generated every 8–12 hours in The Tower, so they'll get into a natural rhythm of knowing when to expect new data. The Fetch Reports button puts them in control rather than the app polling constantly for something that rarely arrives.]

---

**Chapter 4 Verification:**

**NARRATION:**

"The setup is complete when: the widget is on your home screen, tapping it shows the five-category dropdown, make.com has detected the data structure from your test payload, the scenario is active, and a test run flows all the way through to TheTowerAnalyzer without any errors. From here on, every run you want to track is a clipboard copy and two taps away."

---

## OUTRO
**Target time: 1–2 minutes**

---

[VISUAL: Return to TheTowerAnalyzer running with data populated — same kind of shot as the end of Video 1.]

**NARRATION:**

"And that's it — you've got the full TheTowerAnalyzer pipeline up and running.

Just to recap what you've built: TheTowerAnalyzer installed and connected to your Google Drive. Two folders and a Version Tracker spreadsheet inside a TheTower folder in your Drive. A make.com scenario processing your battle data automatically. And a Tasker widget on your Android device that lets you categorize and submit a run in two taps.

From here, your day-to-day rhythm is simple: finish a run, copy the battle report to clipboard, tap your widget, pick a category. When you know a report has been posted to your Drive — which happens every 8 to 12 hours in The Tower — open TheTowerAnalyzer and hit Fetch Reports. Your data will be there waiting.

If you run into any issues, check the Google Doc reference guide in the description first — there's a troubleshooting section that covers the most common problems. If you're still stuck, drop a comment below and I'll do my best to help.

One last thing — once this pipeline is running, there's an optional MCP server that ships with TheTowerAnalyzer that lets Claude AI query your data directly. Your runs, your workshop, your labs, your modules — all of it available as context so Claude can give you advice that's specific to your Tower rather than generic. That's covered in a separate video — link in the description.

And if you haven't watched the showcase video yet — that's linked in the description and in the card on screen now. It's worth watching just to see all the ways you can use the data you're now collecting.

Good luck out there."

[VISUAL: Fade to end screen with Video 1 card and subscribe button.]

---

## PRODUCTION NOTES

- **Record Google Cloud steps carefully.** The OAuth setup is the most complex part of this video and the most likely place viewers get lost. Go slowly, pause on each screen, and zoom in on any fields you're filling out.
- **Show the %appdata% navigation trick.** Many viewers won't know they can type %appdata% directly into the File Explorer address bar — make sure that moment is clear on screen.
- **Paste the webhook URL visibly.** Don't just say "paste the URL" — show the paste happening so viewers can confirm they're putting it in the right field.
- **The end-to-end test is the payoff.** Don't rush it. Let each step of the pipeline show on screen in sequence — Tasker fires, make.com processes, Drive gets the file, app shows the data. That sequence is satisfying to watch and reassures viewers everything is wired up correctly.
- **Use chapters.** This video will be 25–35 minutes. Chapters in the description are not optional — viewers will need to jump back to specific sections when they get stuck.
- **Have a second device or split-screen setup ready** for Chapter 4 so you can show the Android device and the PC simultaneously during the end-to-end test.

### Suggested YouTube Description Template

```
Complete step-by-step setup guide for The Tower Analyzer — covers everything from installation to your first automatically tracked battle report.

📺 First time here? Watch the showcase video first: [LINK TO VIDEO 1]
🤖 Want Claude AI integration? MCP server setup: [LINK TO VIDEO 3]
📄 Reference guide with screenshots for every step: [LINK TO GOOGLE DOC]

LINKS:
📦 TheTowerAnalyzer (GitHub): https://github.com/draukadin/TheTowerAnlyzer/releases/tag/v0.0.1
⚙️ make.com scenario: https://us2.make.com/public/shared-scenario/bGx6dMyjduy/the-tower-battle-report-processor
📱 Tasker on Google Play: https://play.google.com/store/search?q=Tasker&c=apps
📦 Tasker Permissions Helper (GitHub): https://github.com/joaomgcd/Tasker-Permissions/releases

CHAPTERS:
0:00 — Introduction & prerequisites
[1:44] — Chapter 1: Installing the app & Google Cloud OAuth setup
[2:15] — Downloading & installing TheTowerAnalyzer
[3:30] — Creating a Google Cloud project
[4:45] — Enabling Google Drive & Sheets APIs
[5:44] - OAuth Consent Screen
[6:56] - Data Access/Scopes
[7:46] - Audiences/Test Users
[8:14] — Clients/Creating OAuth credentials 
[9:18] — Chapter 2: Google Drive, app configuration & first launch
[9:45] — Creating the TheTower folder structure
[10:27] — Creating the Version Tracker spreadsheet
[12:05] — First launch & Dealing with Port Conflict
[13:54] - App Internal Setup Wizard
[15:13] - Google Authorization & OAuth Consent Screen
[16:36] - Permission Verification
[17:15] - Log Verification
[18:11] — Chapter 3: make.com scenario setup
[19:15] — Importing the scenario
[20:10] — Google Drive Scenario Step Setup
[20:57] - Google Sheets Scenario Step Setup
[21:41] - Webhook Scenario Step Setup
[24:02] — Chapter 4: Tasker setup
[25:03] - Tasker Permission Helper Download
[25:30] - Install Tasker on Android
[27:06] - Setup Tasker Permissions
[28:02] — Getting the Tasker task via QR code
[29:09] — Configuring the webhook URL
[30:35] — Adding the widget to your home screen
[31:20] — Data structure detection & activating the scenario
[32:53] — End-to-end test
[34:41] - Outro
```

---

*End of Video 2 Script*
