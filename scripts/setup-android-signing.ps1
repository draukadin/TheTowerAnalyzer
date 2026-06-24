# Retrieves the Android release signing keystore and passwords from AWS Secrets Manager
# and writes them to android/local.properties so Gradle can build a signed release AAB.
#
# Prerequisites:
#   - AWS CLI installed and on PATH
#   - Credentials configured with secretsmanager:GetSecretValue on the secret below
#
# Run once before each release build, or whenever local.properties is missing.
#
# Usage:
#   .\scripts\setup-android-signing.ps1
#   .\scripts\setup-android-signing.ps1 -Region eu-west-1

param(
    [string]$SecretId = "TheTowerAnalyzer/android/release-signing",
    [string]$Region   = "us-west-2"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot     = Split-Path $PSScriptRoot
$keystorePath = Join-Path $repoRoot "android\app\thetoweranalyzer-release.jks"
$localProps   = Join-Path $repoRoot "android\local.properties"

Write-Host "Fetching '$SecretId' from Secrets Manager ($Region)..."
$raw    = aws secretsmanager get-secret-value `
            --secret-id $SecretId `
            --region $Region `
            --query SecretString `
            --output text
$secret = $raw | ConvertFrom-Json

Write-Host "Writing keystore to $keystorePath"
$bytes = [Convert]::FromBase64String($secret.keystoreBase64)
[IO.File]::WriteAllBytes($keystorePath, $bytes)

# Preserve any existing content (e.g. sdk.dir written by Android Studio),
# stripping any previous signing block before appending the fresh one.
$existing = if (Test-Path $localProps) { Get-Content $localProps -Raw } else { "" }
$existing = $existing -replace "(?s)\r?\n# --- release signing.*", ""

$signingBlock = @"


# --- release signing (written by scripts/setup-android-signing.ps1 — do not commit) ---
signing.storeFile=$($keystorePath.Replace('\', '/'))
signing.storePassword=$($secret.keystorePassword)
signing.keyAlias=$($secret.keyAlias)
signing.keyPassword=$($secret.keyPassword)
"@

Set-Content $localProps -Value ($existing.TrimEnd() + $signingBlock) -Encoding utf8

Write-Host ""
Write-Host "Done. To build a signed AAB:"
Write-Host "  cd android"
Write-Host "  .\gradlew bundleRelease"