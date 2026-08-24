# ADR-004 — PostgreSQL is the only supported dialect; MySQL, MSSQL, and Oracle are frozen

**Status:** Proposed — needs decision `D-4`
**Date:** 2026-08-24
**Decision:** `D-4`
**Requirements:** affects every future schema change

## Context

`backend/src/main/resources/db/migration/` carries four dialect folders. They have diverged:

| Version | Scope | PostgreSQL | MySQL / MSSQL / Oracle |
| --- | --- | --- | --- |
| V1–V2 | Core schema + seed data | ✅ | ✅ |
| V3–V5 | App users, MCP audit log, drop MCP API key | ✅ | ❌ |
| V6–V9 | Form & package automation, seed, sample fill, draft origin | ✅ | ❌ |

**Seven of nine migrations are PostgreSQL-only.** The other three dialects have been frozen at V2
since the forms domain — the product's differentiator — was built. A MySQL deployment would be missing
app users, the MCP audit log, and the entire form and package automation subsystem.

The application is not dialect-neutral either. Two conventions in CLAUDE.md §7 are Postgres-specific:

- `id uuid NOT NULL DEFAULT gen_random_uuid()` — `gen_random_uuid()` is a Postgres function.
- Human-facing identifiers via `GENERATED ALWAYS AS (...) STORED` — supported differently or not at all elsewhere.

`docker-compose.yml` runs `postgres`. `application.properties` hardcodes
`org.hibernate.dialect.PostgreSQLDialect` and `spring.datasource.driver-class-name=org.postgresql.Driver`.
[README.md](../../README.md) documents a Postgres stack. **The project is already PostgreSQL-only in
every respect except three folders of stale SQL.**

## Options considered

| Option | Pros | Cons | Verdict |
| --- | --- | --- | --- |
| **Declare PostgreSQL-only; retire the three folders** | Every future migration is one file. Removes the standing question of whether a schema change "should" be backported. Honest about what is deployable | Closes the door on a MySQL/Oracle deployment without a new decision and a real porting effort | ✅ **Proposed** |
| Backfill V3–V9 across three dialects and keep parity | Genuine multi-DB support | Seven migrations × three dialects, including the entire forms schema — then a tax on *every* future change. `gen_random_uuid()` and `GENERATED ALWAYS AS … STORED` need per-dialect equivalents, and the entity mapping assumes DB generation. No customer is asking for it | Rejected unless a customer requires it |
| Leave as-is | No work | The worst option: the folders *look* like support. Someone will eventually assume MySQL works, deploy it, and find the forms domain missing | ❌ Rejected — ambiguity is the cost |

## Decision

**Proposed: PostgreSQL is the only supported dialect.** This ADR is `Proposed`, not `Accepted`,
because `D-4` is the user's call — it forecloses a deployment option and is therefore not a delivery-team
decision (Requirements.md §9).

## Consequences

**If accepted**

- Every future migration is one file in `db/migration/postgresql/`. The [`db-migration`](../agents/db-migration.md) agent writes one dialect.
- The three stale folders are removed, or moved under a clearly-labelled historical path. **Leaving them in place is not an option** — their existence is the problem.
- The install PDFs for MySQL, MSSQL, and Oracle in those folders are retired from the migration tree.
- `README.md` states PostgreSQL as a requirement rather than an option.

**If rejected** — parity is funded, and the backfill is scheduled as real work in Phase 0 before any
further schema change lands. Seven migrations × three dialects, plus per-dialect equivalents for
UUID defaults and generated columns, plus a test matrix that runs against all four.

**Either way, the ambiguity closes.** The current state — three folders that imply support the project
does not provide — is the only outcome that must not persist.

## Blast radius

| Touched | Change |
| --- | --- |
| `backend/src/main/resources/db/migration/{mysql,mssql,oracle}/` | Removed or archived |
| [README.md](../../README.md) | PostgreSQL stated as required |
| [CLAUDE.md](../../CLAUDE.md) §7 | Note that the GUID and generated-column conventions are Postgres-specific by decision |
| [`../agents/db-migration.md`](../agents/db-migration.md) | Drop any instruction to consider other dialects |
| [Architecture.md](../Architecture.md) §2.4 | Table updated to reflect the decision |
