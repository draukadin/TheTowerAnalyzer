# TheTowerAnalyzer — Claude Code Instructions

## Architecture

Run `/architecture` (or invoke the `architecture` skill) at the start of any coding session to load a full map of the codebase — packages, persistence, external integrations, MCP layer, and how the three components (Spring Boot server, MCP server, SPA front end) fit together.

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3, Spring Data JPA, SQLite (`%APPDATA%\TheTowerAnalyzer\analyzer.db`)
- **MCP server**: Node.js ESM (`mcp/server.js`) — thin HTTP adapter, no business logic
- **Front end**: Single-page app at `src/main/resources/index.html` — vanilla JS, no build step
- **Build**: Maven (`pom.xml`), runs on `localhost:8080`

## Factory Database

`src/main/resources/analyzer.db` is a pre-seeded SQLite file bundled in the JAR. On first launch, `TowerAnalyzerApplication.installBundledDatabaseIfAbsent()` copies it to `%APPDATA%\TheTowerAnalyzer\analyzer.db` before Spring starts, so new installs skip the seeding delay.

When `DatabaseInitializer` or any `db/*Seeder.java` changes, regenerate and commit it:

```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd" package -Pgenerate-factory-db
git add src/main/resources/analyzer.db
git commit -m "chore: regenerate factory database"
```

The `generate-factory-db` profile boots the app with `-Dspring.profiles.active=seed` (no web server, no APPDATA writes), writes `target/factory.db`, then copies it over the checked-in resource.

## Key Conventions

- One `@RestController` per domain entity in `web/`
- Business logic lives in `service/`, never in controllers
- Static reference data (labs, workshop, perks, etc.) is seeded at startup by `db/*Seeder.java`
- `TowerNumber` is the domain type for all in-game numeric values; use its custom Jackson serializer/deserializer
- User config and sheet IDs are external to the jar (`%APPDATA%\TheTowerAnalyzer\user.properties`)
