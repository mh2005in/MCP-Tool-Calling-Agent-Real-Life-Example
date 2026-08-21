---
name: feature-slice
description: >-
  Drive a requirement end to end as a vertical slice — migration, entity, mapper,
  service, controller, Angular, tests, docs, deployed and verified. Use when
  starting work on any requirement from .claude/Requirements.md, or when a change
  spans more than one layer. Covers the twelve steps and the definition of done.
---

# Vertical slice (the unit of delivery)

A slice is **one requirement taken all the way**: migration → entity → repository → mapper → DTO →
service → controller → Angular → tests → docs → deployed → verified.

> A slice that stops at "the backend compiles" is not delivered.

Steps 9–11 (tests, docs, verify) are where slices quietly fail. Skipping them produces exactly the
drift that Phase 0.5 exists to clean up — eleven findings inside work already signed off as complete.

---

## Before you start

Confirm three things:

1. **The requirement has an ID** in `.claude/Requirements.md`. If not, run the `requirement-intake` skill first — an unnormalised requirement has no acceptance criteria and no place in the plan.
2. **You know its acceptance criteria.** "Make forms better" is not a slice.
3. **You are on a branch**, `mh/<kebab-name>` (CLAUDE.md §11). For isolated work, use the `worktree` skill.

---

## The twelve steps

### 1 · Understand

Read the requirement, its acceptance criteria, and its phase in `.claude/Plan.md`. Read any upstream
source document it derives from.

### 2 · Assess impact

CLAUDE.md §3: *always evaluate the impact of a change on other modules and plan accordingly.*

| Layer | Ask |
| --- | --- |
| Database | New table, column, index, or seed? |
| Backend | New entity, service, endpoint? Does an endpoint already do this? |
| Frontend | Does a contract move? Which components consume it? |
| MCP | Does the tool surface change? (`tools.json`) |
| Docker | New service, port, or env var? |
| Docs | README (§14), CLAUDE.md, `.env.example`, `deploy-verify` |

**If an existing endpoint already does what is being asked, ask before creating a new one** (CLAUDE.md §6).

### 3 · Propose and ask

CLAUDE.md §3: **describe the change and ask permission before proceeding.** Use the question tool.
Do not assume — if the requirement admits two readings, ask which.

### 4 · Migrate — `db-migration`

Next free `V<n>__<name>.sql` under `db/migration/postgresql/`. GUID PK, generated identifier columns,
**no length constraints**. Flyway is disabled — **the migration must be applied manually**, so say so.

### 5 · Model — `backend-feature`

Entity → repository → MapStruct mapper → DTO. Bottom-up.

### 6 · Serve — `backend-feature`

Business rules in the service layer. **Check `CommonService` and `CommonUtil` first** (CLAUDE.md §5) —
if the method exists elsewhere, *move* it there, then call it from both places.

### 7 · Expose — `backend-feature`

Controller with `@PreAuthorize` and `@Valid`. DTOs only. `ApiPaths` constants under `/v1` — **never**
map `/api` under a context path of `/api` (that is `DR-04`, present in three controllers already).

### 8 · Consume — `frontend-feature`

`API_ENDPOINTS` entry → service method → standalone component → route. **Never hardcode a URL** — the
SPA reads `window.__env` so host changes need no rebuild.

### 9 · Test — `test-author`

Alongside the code. **A bug fix ships with a test that fails without the fix** (CLAUDE.md §9). Cover
the happy path, the boundaries, and authorization — cross-case access must return 404.

### 10 · Document — `docs-sync`

Records first, then the code documentation:

| What | Where |
| --- | --- |
| The slice plan, if it needed one | `.claude/plan/slice-<REQ-ID>.md` |
| What shipped, verified, and **deferred** | `.claude/progress/slice-<REQ-ID>-<YYYY-MM-DD>.md` |
| A design decision that constrains future work | `.claude/design/ADR-nnn-<slug>.md` |
| Something learned that will bite again | `.claude/memory/<slug>.md` |

Then:

- **README.md** if setup, running, stack, or architecture changed (CLAUDE.md §14).
- **CLAUDE.md** if a convention changed — and §16 if an agent, skill, or hook changed.
- **`.env.example`** for any new config, wired through the §15 chain.
- **`deploy-verify`** if a service name, port, or health endpoint moved (CLAUDE.md §12 — the most-forgotten one).
- **`status dashboard.md`** and **`change.log.md`**.

### 11 · Verify — `deploy-verify`

CLAUDE.md §12: **a change is not done until it runs in the container.** Rebuild the affected
service(s), confirm `docker compose ps` is healthy, exercise the affected endpoint.

```bash
docker compose up -d --build
```

### 12 · Review — `security-reviewer`

Required if the slice touches authentication, authorization, PII, uploads, tenancy, or AI output.

---

## Definition of done

All ten, or the slice is not finished:

- [ ] Acceptance criteria met
- [ ] Tests alongside the code; bug fix has a failing-without-the-fix test (§9)
- [ ] Build, lint, and tests pass (§11)
- [ ] Runs in the Docker stack, verified by `deploy-verify` (§12)
- [ ] README accurate for a first-time reader (§14)
- [ ] New config wired `.env` → `.env.example` → compose → consumer (§15)
- [ ] Any agent/skill/hook encoding changed parameters updated in the **same** change (§16)
- [ ] No secrets or real PII in the diff; staged diff scanned (§8)
- [ ] `status dashboard.md` and `change.log.md` updated
- [ ] Guardrail metric instrumented, not just the success metric

---

## Orchestration

Launch independent agents in one batch; sequence the dependent ones.

```
                    ┌── db-migration ──┐
requirements-analyst│                  ├── test-author ── deploy-verify ── docs-sync
    (if unclear)    ├── backend-feature┤                       │
                    └── frontend-feature                 security-reviewer
                                                    (auth · PII · uploads · tenancy)
```

`db-migration`, `backend-feature`, and `frontend-feature` can run in parallel **once the contract is
agreed**. If it is not agreed, run backend first — the frontend consumes what it produces.

---

## The rules a slice must not break

- **Ask before changing** (§3). **Never assume** — ask instead.
- **No new pattern where one exists** (§10). If the existing pattern is wrong, propose changing it rather than adding a parallel one.
- **Reuse before writing** (§5).
- **No length constraints on entity fields** (§7).
- **No hardcoded host, port, or URL** (§15).
- **No secrets, no real PII** — in code, tests, fixtures, seed data, logs, commit messages, or PR bodies (§8).
- **Don't commit or push unless asked** (§11). Author as `mh2005in`; never add Claude as author or co-author.
- **The product boundary holds.** No eligibility decisions, no legal interpretation, no outcome guarantees, no artefact reaching a client without consultant approval, no unmasked PII leaving the tenant.

---

## When a slice cannot finish

Say so explicitly rather than narrowing it silently. Finish every part that is not blocked, then
report exactly what was left out and why. **Scaling the work down is the user's call.**
