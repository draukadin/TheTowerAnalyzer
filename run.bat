@echo off
setlocal

set CONFIG_DIR=%APPDATA%\TheTowerAnalyzer
set CONFIG_FILE=%CONFIG_DIR%\config.properties
set JAR=%~dp0target\the-tower-analyzer.jar

if not exist "%CONFIG_DIR%" mkdir "%CONFIG_DIR%"

if not exist "%CONFIG_FILE%" (
    echo Config file not found. Creating template at:
    echo   %CONFIG_FILE%
    echo.
    echo Edit the file, set your GEMINI_API_KEY, then re-run.
    (echo GEMINI_API_KEY=your-gemini-api-key-here) > "%CONFIG_FILE%"
    pause
    exit /b 1
)

for /f "usebackq eol=# tokens=1,2 delims==" %%A in ("%CONFIG_FILE%") do (
    set "%%A=%%B"
)

if not exist "%JAR%" (
    echo ERROR: JAR not found. Run: mvn package -DskipTests
    pause
    exit /b 1
)

java -jar "%JAR%"
