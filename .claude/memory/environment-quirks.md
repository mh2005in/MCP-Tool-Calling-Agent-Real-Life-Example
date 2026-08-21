# Environment quirks that cost time

**Type:** environment
**Learned:** 2026-08-21
**Related:** [`../operations/README.md`](../operations/README.md), [`../input/README.md`](../input/README.md), `DR-04`, `D-4`

## The facts

### Flyway is disabled — migrations are applied by hand

`backend/src/main/resources/db/migration/postgresql/` holds V1–V9, but nothing applies them
automatically. **A migration that is written is not a migration that is deployed.** Apply it manually
against `immiauto_db` and say so in the handoff, or the next person debugs a missing column.

### Only PostgreSQL is live

`mysql/`, `mssql/`, and `oracle/` are **frozen at V2** — no V3–V9. Writing all four dialects creates a
false impression of parity. Write PostgreSQL, state that the others are frozen. Decision `D-4` is open
on whether to revive or formally drop them.

### `pdftotext` needs `-layout`

The requirement corpus is table-heavy. Without `-layout`, tables collapse into unreadable column soup
and the requirement text becomes worthless:

```bash
pdftotext -layout "<source>.pdf" "<target>.txt"
```

### Context path `/api` doubles up

`server.servlet.context-path=/api`, so a controller mapping `@RequestMapping("/api")` resolves to
`/api/api/…` — and escapes the `/v1/**` security matchers. Three controllers have this bug today
(`DR-04`): `AutomationController`, `PartyPortalController`, `WorkflowController`. **Never copy that
pattern** — use `ApiPaths` constants under `/v1`.

### Services address each other by compose service name

`postgres:5432`, `backend:8080`, `keycloak:8080` — **never** `localhost` and never a published host
port. The browser and each token's issuer use the *public* Keycloak URL; the backend and MCP fetch
signing keys over the *internal* URL. Mixing them produces an issuer mismatch that fails JWT
validation in a way that looks like a key problem.

### Build tooling paths

Java `C:\Program Files\Java\jdk-24.0.2`, Maven `C:\Program Files\apache-maven-3.9.16`. If either is
missing, ask for the correct path and update CLAUDE.md §1 rather than guessing.

## Why it matters

Each of these produces a failure that looks like something else — a missing column looks like a code
bug, an issuer mismatch looks like a key problem, a doubled context path looks like a routing typo,
and a `-layout`-less extraction looks like a corrupt source document.
