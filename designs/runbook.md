# TheTowerAnalyzer — Operations Runbook

> Prod account: `611434859239`  
> Regions: `us-east-2` (US), `eu-west-1` (EU), `ap-northeast-1` (AP)  
> S3 bucket: `tower-analyzer-reports-prod-611434859239`  
> DynamoDB table: `TowerAnalyzerPlayerVersion-prod`

---

## 1. Check if a regional Lambda is down

**Signs:** CloudWatch alarm fires, users in a region report 502/500 errors.

```bash
# Tail recent log events for the ingest Lambda in a given region
aws logs tail /aws/lambda/TowerAnalyzerIngestReport-prod \
  --region <us-east-2|eu-west-1|ap-northeast-1> \
  --since 1h --follow

# Same for the credential-vending Lambda
aws logs tail /aws/lambda/TowerAnalyzerCredentialVending-prod \
  --region <us-east-2|eu-west-1|ap-northeast-1> \
  --since 1h --follow
```

**What to look for:**
- `PermanentRedirect` or `NoSuchBucket` → S3 region misconfiguration (check `BUCKET_REGION` env var)
- `AccessDenied` on STS → credential-vending role ARN wrong or IAM policy gap
- `Task timed out` → Lambda timeout too short or downstream is slow

**Force a Lambda redeploy** (picks up any env-var fix):
```bash
cd infra
npx cdk deploy TowerAnalyzer-Prod-Ingest-<USE2|EUC1|APN1>
```

---

## 2. Manually tag a report as `status=processed`

Use this when Spring Boot fetched a report but crashed before tagging it, leaving the object in an unprocessed state (Spring Boot will retry it endlessly otherwise).

```bash
# Find the unprocessed object key first
aws s3 ls s3://tower-analyzer-reports-prod-611434859239/<player-id>/ \
  --region us-east-2 --recursive

# Check its current tags
aws s3api get-object-tagging \
  --bucket tower-analyzer-reports-prod-611434859239 \
  --key <full-object-key> \
  --region us-east-2

# Apply the processed tag manually
aws s3api put-object-tagging \
  --bucket tower-analyzer-reports-prod-611434859239 \
  --key <full-object-key> \
  --region us-east-2 \
  --tagging '{"TagSet":[{"Key":"status","Value":"processed"}]}'
```

---

## 3. Revoke a player's credentials

Use this if a player ID is compromised or a player requests full data deletion.

**Step 1 — Delete the DynamoDB record** (stops new credential vends):
```bash
aws dynamodb delete-item \
  --table-name TowerAnalyzerPlayerVersion-prod \
  --key '{"player_id":{"S":"<PLAYER-ID>"}}' \
  --region us-east-2
```

**Step 2 — Wait for existing STS sessions to expire** (up to 1 hour — sessions are non-revocable without a permissions boundary change).

**Step 3 (optional) — Delete the player's S3 data**:
```bash
aws s3 rm s3://tower-analyzer-reports-prod-611434859239/<PLAYER-ID>/ \
  --recursive --region us-east-2
```

---

## 4. Look up a player's DynamoDB record

```bash
aws dynamodb get-item \
  --table-name TowerAnalyzerPlayerVersion-prod \
  --key '{"player_id":{"S":"<PLAYER-ID>"}}' \
  --region us-east-2
```

Fields: `player_id`, `current_version`, `updated_at`, `registered_at`.

---

## 5. CloudWatch alarm reference

| Alarm name | Region | Threshold | Likely cause |
|---|---|---|---|
| `TowerAnalyzer-prod-IngestErrors-<region>` | US/EU/AP | ≥ 5 errors / 5 min | S3 write failure, Lambda crash |
| `TowerAnalyzer-prod-CredentialErrors-<region>` | US/EU/AP | ≥ 5 errors / 5 min | STS AssumeRole failure, DDB write failure |
| `TowerAnalyzer-prod-Api5xx-<region>` | US/EU/AP | ≥ 3 5xx / 5 min | Lambda throttle, Lambda crash |
| `TowerAnalyzer-MonthlyBilling` | account | > $10 / month | Unexpected traffic or resource leak |