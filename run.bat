@echo off
setlocal

set APP_DIR=%APPDATA%\TheTowerAnalyzer
set USER_PROPERTIES_FILE=%APP_DIR%\user.properties
set OAUTH_CREDENTIALS_FILE=%APP_DIR%\oauth-credentials.json
set JAR=%~dp0target\the-tower-analyzer.jar

if not exist "%APP_DIR%" mkdir "%APP_DIR%"

:: --- First-run: create user.properties template ---
if not exist "%USER_PROPERTIES_FILE%" (
    echo Creating user.properties template at:
    echo   %USER_PROPERTIES_FILE%
    (
        echo # Google Drive / Sheets OAuth 2.0 client secret
        echo # oauth-credentials.json = OAuth 2.0 client secret ^(used by both Google Drive and Sheets^)
        echo drive.oauth-credentials-file=%APP_DIR%\oauth-credentials.json
        echo drive.tokens-dir=%APP_DIR%\tokens
        echo drive.application-name=TheTowerAnalyzer
        echo.
        echo # Google Drive folder IDs - replace with your own values
        echo drive.backup-folder-id=REPLACE_WITH_YOUR_BACKUP_FOLDER_ID
        echo drive.battle-reports-folder-id=REPLACE_WITH_YOUR_BATTLE_REPORTS_FOLDER_ID
        echo.
        echo # Google Sheets sheet IDs - replace with your own values
        echo sheets.ids.player-tracker=REPLACE_WITH_YOUR_PLAYER_TRACKER_SHEET_ID
        echo.
        echo # Optional: change the port if 8080 is already in use on your machine
        echo # server.port=8080
    ) > "%USER_PROPERTIES_FILE%"
    echo.
    echo Edit the file above and replace all REPLACE_WITH_... placeholders with
    echo your actual Google Drive folder IDs and Sheets sheet IDs, then rerun.
    pause
    exit /b 1
)

:: --- Load user.properties into environment ---
for /f "usebackq eol=# tokens=1,2 delims==" %%A in ("%USER_PROPERTIES_FILE%") do (
    set "%%A=%%B"
)

:: --- Check credential file ---
if not exist "%OAUTH_CREDENTIALS_FILE%" (
    echo ERROR: oauth-credentials.json not found at:
    echo   %OAUTH_CREDENTIALS_FILE%
    echo.
    echo This is the OAuth 2.0 client secret used by both Google Drive and Sheets.
    echo To obtain it:
    echo   1. Go to https://console.cloud.google.com/
    echo   2. Open your project and go to APIs ^& Services ^> Credentials
    echo   3. Create an OAuth 2.0 Client ID ^(Desktop app^)
    echo   4. Download the JSON and save it as: %OAUTH_CREDENTIALS_FILE%
    pause
    exit /b 1
)

:: --- Check JAR ---
if not exist "%JAR%" (
    echo ERROR: JAR not found. Run: mvn package -DskipTests
    pause
    exit /b 1
)

java -jar "%JAR%" %*
