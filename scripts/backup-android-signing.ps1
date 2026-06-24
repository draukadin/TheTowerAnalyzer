# Retrieves the Android release signing keystore and credentials from AWS Secrets Manager
# and writes them to an encrypted USB drive (or any destination folder) as a cold backup.
#
# Prerequisites:
#   - AWS CLI installed and on PATH
#   - Credentials configured with secretsmanager:GetSecretValue on the secret below
#   - Destination drive should be BitLocker-encrypted before running this script
#
# Usage:
#   .\scripts\backup-android-signing.ps1 -Destination E:\TheTowerAnalyzer-keys
#   .\scripts\backup-android-signing.ps1 -Destination E:\TheTowerAnalyzer-keys -Region eu-west-1

param(
    [Parameter(Mandatory)]
    [string]$Destination,
    [string]$SecretId = "TheTowerAnalyzer/android/release-signing",
    [string]$Region   = "us-west-2"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# Warn if destination does not look like a removable or external drive.
$drive = Split-Path -Qualifier $Destination
$driveInfo = Get-PSDrive -Name $drive.TrimEnd(':') -ErrorAction SilentlyContinue
if (-not $driveInfo) {
    Write-Warning "Could not resolve drive '$drive'. Ensure the USB drive is connected."
}

if (-not (Test-Path $Destination)) {
    New-Item -ItemType Directory -Path $Destination | Out-Null
}

Write-Host "Fetching '$SecretId' from Secrets Manager ($Region)..."
$raw    = aws secretsmanager get-secret-value `
            --secret-id $SecretId `
            --region $Region `
            --query SecretString `
            --output text
$secret = $raw | ConvertFrom-Json

# 1. Decoded keystore (.jks)
$jksPath = Join-Path $Destination "thetoweranalyzer-release.jks"
Write-Host "Writing keystore → $jksPath"
$bytes = [Convert]::FromBase64String($secret.keystoreBase64)
[IO.File]::WriteAllBytes($jksPath, $bytes)

# 2. Full secret JSON (contains all passwords; keep this file as sensitive as the keystore)
$jsonPath = Join-Path $Destination "thetoweranalyzer-release-secret.json"
Write-Host "Writing credentials → $jsonPath"
$raw | Out-File -FilePath $jsonPath -Encoding utf8

# 3. README so the backup is self-explanatory years from now
$readmePath = Join-Path $Destination "README.txt"
$timestamp  = Get-Date -Format "yyyy-MM-dd HH:mm:ss UTC" -AsUTC
@"
The Tower Analyzer — Android Release Signing Backup
=====================================================
Created : $timestamp
Secret  : $SecretId
Region  : $Region

Files
-----
thetoweranalyzer-release.jks
  The Java KeyStore used to sign Play Store releases.
  Alias    : $($secret.keyAlias)
  Password : see thetoweranalyzer-release-secret.json -> keystorePassword / keyPassword

thetoweranalyzer-release-secret.json
  The raw JSON stored in AWS Secrets Manager. Contains:
    keystoreBase64   — base64-encoded keystore (same bytes as the .jks above)
    keystorePassword — keystore password
    keyAlias         — key alias
    keyPassword      — key password

To restore to Secrets Manager
------------------------------
  aws secretsmanager put-secret-value `
    --secret-id $SecretId `
    --region $Region `
    --secret-string file://thetoweranalyzer-release-secret.json

To rebuild local.properties for a release build
-------------------------------------------------
  Run .\scripts\setup-android-signing.ps1 (retrieves directly from Secrets Manager).
  Or manually set signing.* properties in android\local.properties using the values above.

WARNING: These files grant full signing authority over the Play Store app.
         Keep this drive encrypted (BitLocker) and physically secured.
         Never copy these files to a networked location.
"@ | Out-File -FilePath $readmePath -Encoding utf8

Write-Host ""
Write-Host "Backup complete:"
Write-Host "  $jksPath"
Write-Host "  $jsonPath"
Write-Host "  $readmePath"
Write-Host ""
Write-Host "Verify the drive is BitLocker-encrypted before storing it."