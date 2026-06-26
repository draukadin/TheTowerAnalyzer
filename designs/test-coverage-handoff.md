# Test Coverage Handoff — TheTowerAnalyzer

## What's done

**Unit tests (Tier 1)** — 309 tests across all `service/`, `model/`, `jackson/`, `parser/`,
`analyzers/`, `util/` packages. JaCoCo gate at 90% INSTRUCTION coverage passes cleanly.
`mvn test` → BUILD SUCCESS.

**Integration tests (Tier 2)** — 58/59 IT tests pass (`mvn verify`). One failure remains
(details below).

---

## One remaining failure — `ReportControllerIT.fetchReports_emptyFolder_returns0Processed`

**Root cause:** `application.properties` hardcodes `aws.region=us-east-2` and
`aws.s3-bucket=tower-analyzer-reports-prod-611434859239`. The `application-test.properties`
doesn't override these. When `user.properties` on the developer's machine also sets
`aws.player-id`, `AwsProperties.isConfigured()` returns `true` in the test context, so
`ReportController.fetchReports()` takes the S3 path and processes the 3 rows inserted by
`@BeforeAll`, returning `processed: 3` instead of the expected `0`.

**Fix:** Add the following to `src/test/resources/application-test.properties`:

```properties
aws.region=
aws.s3-bucket=
aws.player-id=
```

Also remove `@MockBean protected S3ReportFetcherService s3ReportFetcherService` from
`BaseIntegrationTest.java` — it was added as a workaround but worsens the problem by
injecting a non-null `S3ReportFetcherService` into `ReportController`, making the S3 branch
appear eligible even when it shouldn't be.

With those two changes, `AwsProperties.isConfigured()` returns `false` in tests, the
controller takes the Drive path, and the `@MockBean protected ReportFetcherService
reportFetcherService` stub (`when(reportFetcherService.processReports()).thenReturn(0)`)
takes effect → `processed: 0` → test passes.

---

## Files modified (not yet committed)

| File | Change |
|---|---|
| `pom.xml` | JaCoCo 0.8.12 plugin, 90% instruction gate, exclusions for web/config/db/reporter/repository + external-service classes |
| `src/test/resources/application-test.properties` | Test datasource, stub props for drive/cells; missing: aws overrides (see fix above) |
| `src/test/java/com/pphi/tower/BaseIntegrationTest.java` | Base class: MockMvc, JdbcTemplate, MockBeans for Drive/Sheets/ReportFetcher/S3 |
| `src/test/java/com/pphi/tower/fixtures/` | `BattleHistoryFixtures`, `RunRowFixtures`, `TowerNumberFactory` |
| `src/test/java/com/pphi/tower/model/` | `ModuleLevelTableTest`, `ScaleSuffixTest`, `TowerNumberTest`, `TowerEraTest`, `battlediagnostics/DiagnosisResultTest` |
| `src/test/java/com/pphi/tower/jackson/` | `BattleHistoryDeserializerTest`, `TowerNumberSerializerTest`, `TowerNumberDeserializerTest` |
| `src/test/java/com/pphi/tower/parser/` | `BattleHistoryParserTest` |
| `src/test/java/com/pphi/tower/analyzers/` | `BattleDiagnosticTest`, `RunComparisonTest` |
| `src/test/java/com/pphi/tower/service/` | `CellIncomeServiceTest`, `ShardAnalysisServiceTest`, `ReportFetcherServiceTest`, `DiagnosticServiceTest`, `ComparisonServiceTest`, `GtIncomeServiceTest` |
| `src/test/java/com/pphi/tower/web/` | 17 IT classes covering every controller |

---

## Step 5: Playwright E2E (Tier 3) — complete

57 tests across 8 spec files in `src/test/playwright/`. All use `page.route()` mocking — no live
backend calls. Server must be running on `localhost:8080` before running Playwright (`mvn spring-boot:run`).
Shared helpers in `src/test/playwright/helpers/mock-api.ts`:

- `mockApi(page, pattern, body, status?)` — one-liner stub for any endpoint
- `goToApp(page)` — mocks setup/auth/init endpoints and navigates to `/`

Root cause discovered during fix: `AwsConfig` used `@ConditionalOnProperty(name="aws.region")` which
fires even when the property is blank (Spring treats "" as present). Changed to
`@ConditionalOnExpression("!'${aws.region:}'.isEmpty()")`. `BaseIntegrationTest` no longer needs
`@MockBean S3ReportFetcherService` because `S3ReportFetcherService` is already
`@ConditionalOnBean(S3Client.class)`.

Run E2E: `npx playwright test` (with Spring Boot running on :8080)

| Spec | Tests | Views covered |
|---|---|---|
| `navigation.spec.ts` | 8 | Nav group toggle, active-state CSS, correct section shown |
| `reports.spec.ts` | 7 | Fetch button → POST /fetch; sidebar list; stats/diagnosis/compare tabs; delete modal → DELETE call |
| `labs.spec.ts` | 5 | Lab search; state PUT on level change; Lab Planner slot plans; Cell Income CPH; Lab Speed stat cards |
| `upgrades.spec.ts` | 5 | Workshop PUT level; Cards PUT star-level + preset selector; Bots unlock toggle; Guardian add-chip modal |
| `modules.spec.ts` | 3 | Module state PUT; Effect Bans ban PUT; Shard Rate from analysis endpoint |
| `collectibles.spec.ts` | 5 | Relics add modal + POST; Cosmetics add modal with multi-type conditional fields |
| `meta.spec.ts` | 6 | Currencies view; Tier PB view; Battle Conditions / Tournament view |
| `admin.spec.ts` | 6 | QR modal opens/closes; backup POST; restore flow (S3 mode vs 409) |
