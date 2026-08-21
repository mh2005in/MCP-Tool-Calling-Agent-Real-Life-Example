# Operations — runbooks and operational procedures

**Related:** [`../../README.md`](../../README.md) is the source of truth for **how to run the stack** —
services, ports, env vars, setup. Do not restate it here (CLAUDE.md §12). **This folder holds
procedures for when something needs doing or has gone wrong.**

---

## What goes here

| Filename | For |
| --- | --- |
| `runbook-<slug>.md` | A repeatable operational procedure — revocation, restore, rotation, incident response |
| `deployment-YYYY-MM-DD.md` | A record of a real deployment: what shipped, what broke, what was learned |
| `incident-YYYY-MM-DD-<slug>.md` | An incident and its resolution |

---

## Runbooks owed

Every one of these is a tracked requirement that has no runbook yet.

| Runbook | Requirement | Why it matters |
| --- | --- | --- |
| **Token/session revocation** | `SEC-16` | Combine Keycloak session revocation (kills refresh tokens) with the app-level `DisabledUserFilter` (immediate cutoff). **The Entra runbooks in [`../input/runbooks/`](../input/runbooks/) are superseded** — this replaces them |
| **Backup and restore** | `GAP-14` | Phase 5 treats a **rehearsed** restore as a release gate, not an ops nicety. A backup never restored is not a backup |
| **Secret rotation** | `SEC-04` | What to rotate, in what order, and what breaks during the window |
| **Incident response** | `GAP-14` | Including breach assessment — this system holds regulated immigration PII |
| **Migration application** | — | **Flyway is disabled.** Migrations are applied manually against `immiauto_db`; a written-but-unapplied migration is not deployed |
| **Content release** | `GAP-09` | The operational half of the `content-governance` skill |

---

## Runbook template

```markdown
# Runbook — <name>

**When to run:** <the trigger>
**Owner:** <role>
**Requirement:** <ID>
**Last rehearsed:** YYYY-MM-DD  ← a runbook never rehearsed is a guess

## Preconditions
- [ ] <what must be true before starting>

## Steps
1. <command or action>
   - Expected: <what you should see>
   - If not: <what to do instead>

## Verification
<how you know it worked — not "it should work">

## Rollback
<how to undo, if it can be undone>

## Escalation
<who to involve, and when to stop trying>
```

---

## Rules

- **Never restate README content.** Link to it. Ports and service names live in one place, and duplication is how they drift (CLAUDE.md §12).
- **A runbook is rehearsed or it is fiction.** Record the rehearsal date. `GAP-14` requires restore *evidence*, not a restore *procedure*.
- **Steps state expected output.** "Run X" is not a step; "Run X, expect `healthy` for all seven services" is.
- **No real secrets, credentials, or infrastructure identifiers** (CLAUDE.md §8) — tenant IDs and client IDs included. Reference the env var, never the value. The Entra runbooks in [`../input/runbooks/`](../input/runbooks/) had to be redacted for exactly this.
- **A production-affecting procedure is confirmed before it is run**, not after.

## Deployment reality

The stack is `postgres` + `keycloak` + `backend` + `mcpserver` + `frontend` + `ollama` + `librechat`.
Per CLAUDE.md §12, a change is not done until it runs in the container — delegate verification to
`deploy-verify`.

Two things that catch people out:

- **Flyway is disabled** — migrations are manual.
- **Services address each other by compose service name**, never `localhost` or a published host port.

## Who writes here

`deploy-verify` (deployment records), `docs-sync` (keeping runbooks current), `security-reviewer`
(incident and revocation procedures).
