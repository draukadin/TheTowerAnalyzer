# Developer Guide

## Building the Application

### Prerequisites

- JDK 21+
- Maven 3.8+

### Build the fat JAR

```
mvn package -DskipTests
```

Output: `target/the-tower-analyzer.jar`

---

## Creating a Windows Installer

The installer is a self-contained `.exe` that bundles a JRE — no Java required on the target machine.

### Additional Prerequisites (local builds only)

- **WiX Toolset 3.x** — required by `jpackage` to produce `.exe`/`.msi` on Windows
  - Install via Chocolatey: `choco install wixtoolset`
  - Or download from https://wixtoolset.org/releases/

### Build the installer locally

```
mvn verify -DskipTests -Ppackage-installer
```

Output: `target/installer/TheTowerAnalyzer-1.0.0.exe`

To override the version number:

```
mvn package -DskipTests -Ppackage-installer -Dinstaller.version=1.2.0
```

### Build via GitHub Actions (CI)

The workflow in `.github/workflows/release.yml` builds the installer on a Windows runner and publishes it as a GitHub Release.

**Trigger a release by pushing a version tag:**

```
git tag v1.2.0
git push origin v1.2.0
```

This will:
1. Build the fat JAR
2. Run `jpackage` to produce `TheTowerAnalyzer-1.2.0.exe`
3. Create a GitHub Release with the installer attached

**Trigger a manual build** (no release created) via GitHub Actions → select the `Release` workflow → click `Run workflow` → enter a version number.

---

## Optional: App Icon

To add a custom icon to the installer and installed app:

1. Place a `app.ico` file in `src/main/resources/`
2. Add the following argument to the `jpackage` call in both `pom.xml` (the `package-installer` profile) and `.github/workflows/release.yml`:

```
--icon src/main/resources/app.ico
```

---

## Running Locally (without installer)

```
mvn package -DskipTests
run.bat
```

The first launch creates `%APPDATA%\TheTowerAnalyzer\user.properties`. Fill in the placeholder values and place your credential file there, then rerun.

The app starts a local server at http://localhost:8080 (or whichever port you set in `user.properties`).

### Google credential setup

Only one credential file is needed: `oauth-credentials.json`. It is used for both Google Drive and Google Sheets.

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Navigate to **APIs & Services → Credentials**
3. Create an **OAuth 2.0 Client ID** (type: Desktop app)
4. Download the JSON and save it as `%APPDATA%\TheTowerAnalyzer\oauth-credentials.json`

On first use of a Google-integrated feature, a browser window will open to complete the OAuth consent flow. The resulting token is cached in `%APPDATA%\TheTowerAnalyzer\tokens\` and reused on subsequent runs.

### Changing the port

If port 8080 conflicts with another application on your machine, uncomment and set `server.port` in `user.properties`:

```properties
server.port=9090
```
