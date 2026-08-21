---
name: release-gate
description: >-
  Run the acceptance checklist before declaring a phase complete or cutting a
  release — security controls, test evidence, docs accuracy, deployment health,
  and the product boundary. Use at a phase gate from .claude/Plan.md, before a
  release, or when someone asks whether the system is ready to hold real client data.
---

# Release gate

Phase 5 §4.14 states the standard:

> **Treat security and privacy as release acceptance criteria.**

A gate is not a status meeting. It is a set of checks that either pass with evidence or fail. **A
check without evidence is a fail.**

---

## When to run

- Exiting a phase in `.claude/Plan.md`
- Before a release or a deployment carrying real client data
- When asked whether the system is ready for a firm to use

---

## Gate 1 · Requirement completeness

- [ ] Every requirement scheduled for this phase is `VERIFIED` or has a **written acceptance** of why it is not, in `.claude/change.log.md`
- [ ] The phase's exit criteria in `.claude/Plan.md` are met
- [ ] Guardrail metrics are instrumented — not just success metrics (Phase 5 §7). *Speed that hides defects is not progress.*
- [ ] Baseline metrics were captured **before** the features launched, or the success measures are unfalsifiable

## Gate 2 · Security (`SEC-*`, `GAP-14`)

Run the `security-reviewer` agent over the full diff since the last gate.

- [ ] No Critical or High finding is open
- [ ] Authentication required on every business endpoint; no route escapes the `/v1/**` matchers
- [ ] Identity derives from the authenticated principal, never a path variable
- [ ] Nested resources scoped by both parent and child id, returning 404 on mismatch
- [ ] No secrets in the diff or in git history; no insecure fallback that goes live when an env var is unset
- [ ] No PII in logs — **including SQL parameter logging** (`DR-08`)
- [ ] Uploads: size limit, content-signature verification, sanitised filenames, malware scanning or an explicit accepted gap
- [ ] Rate limiting on auth-sensitive and write endpoints (`SEC-06`)
- [ ] CORS restricted; security headers present; HTTPS enforced
- [ ] `/v1/mcp/**` not publicly exposed (`SEC-13`)
- [ ] Audit events cover login, role change, consultant enable/disable, admin actions (`SEC-09`)

## Gate 3 · Privacy and compliance (PIPEDA)

- [ ] Every PII read and write is audited with actor, subject, and timestamp (`BR-4`)
- [ ] Masking applied at the AI/MCP boundary before any response leaves (`BR-5`)
- [ ] No unmasked PII leaves the tenant — no SaaS form-fill, no external LLM
- [ ] Data export and deletion workflows exist for the data held
- [ ] Retention and disposition are defined for documents and audit records
- [ ] No real PII in the repository — code, tests, fixtures, seed data, or documentation

## Gate 4 · Product boundary (`BR-1`, `BR-2`)

**This gate blocks a release on its own.** The boundary is what makes the product lawful to sell to a
regulated profession.

- [ ] No output presents an eligibility decision, legal interpretation, or outcome guarantee
- [ ] Compliance calculators use **preliminary-review** language, never "requirements met"
- [ ] A consultant approves before any artefact reaches a client or IRCC
- [ ] Client-facing checklists carry the review disclaimer
- [ ] Generated content is labelled as generated, with limitations, reviewer, and approval status
- [ ] **No dynamic-XFA form is marked fillable**; generation never emits a blank-but-"successful" PDF

> Phase 5 §4.1 calls a blank "successful" PDF a **correctness and trust failure, not a missing
> feature**. A consultant filing a blank form on the system's assurance is the worst outcome this
> product can produce.

## Gate 5 · Test evidence (CLAUDE.md §9)

- [ ] Build, lint, and the full suite pass — **with the actual output recorded**
- [ ] Every bug fix in this phase has a test that fails without its fix
- [ ] No test was deleted or weakened to make the suite pass
- [ ] Authorization tests cover every nested resource: cross-case returns 404, cross-consultant denied
- [ ] Unit tests are fast and offline; live-dependency tests are in the separate suite
- [ ] Regression fixtures exist for every onboarded form

```bash
find backend/src/test MCPServer/src/test -name "*.java" | wc -l
```

Record the number. It is the clearest single indicator of whether the codebase is safely changeable.

## Gate 6 · Documentation (CLAUDE.md §14)

- [ ] **A first-time reader following `README.md` alone can clone and run the stack** — the actual test
- [ ] Ports, env vars, prerequisites, and commands are current
- [ ] New services, dependencies, and architecture changes are documented
- [ ] `.env.example` covers every new variable
- [ ] `deploy-verify` matches the real service names, ports, and health endpoints (CLAUDE.md §12)
- [ ] CLAUDE.md §16 lists every current agent, skill, and hook
- [ ] `status dashboard.md` and `change.log.md` are current

## Gate 7 · Deployment (CLAUDE.md §12)

Run `deploy-verify`. Verify from a **clean** stack, not an already-warm one.

```bash
docker compose down -v
docker compose up -d --build
docker compose ps
```

- [ ] Every service reaches healthy from cold
- [ ] Migrations applied — **Flyway is disabled, so this is a manual step that is easy to miss**
- [ ] Auth flow works end to end: login → token → API call
- [ ] MCP tools reachable and audited
- [ ] The phase's headline endpoints exercised successfully

## Gate 8 · Operational readiness (`SEC-14`, `SEC-16`, `GAP-14`)

- [ ] Monitoring and alerting live for auth-failure spikes, 403s from disabled users, and latency
- [ ] Backup configured; **a restore has actually been rehearsed** — Phase 5 treats restore evidence as a release gate
- [ ] Incident and revocation runbooks exist and are current for Keycloak (not the superseded Entra runbooks)
- [ ] Dependency and code scanning run in CI (`SEC-15`)

---

## Output

```
# Release gate — <phase / release>  <date>

## Verdict
PASS  ·  PASS WITH ACCEPTED RISK  ·  FAIL

## Gate results
| Gate | Result | Evidence |
| --- | --- | --- |
| 1 Requirements  | ✅/❌ | |
| 2 Security      | ✅/❌ | |
| 3 Privacy       | ✅/❌ | |
| 4 Boundary      | ✅/❌ | |
| 5 Tests         | ✅/❌ | <count + suite output> |
| 6 Documentation | ✅/❌ | |
| 7 Deployment    | ✅/❌ | <compose ps output> |
| 8 Operations    | ✅/❌ | |

## Blocking failures
<what must be fixed before release>

## Accepted risks
<item · why accepted · who accepted · when revisited>

## Evidence
<commands run and their actual output>
```

---

## Recording the gate

| What | Where |
| --- | --- |
| The gate record, with evidence | `.claude/qa/gate-<phase>-<YYYY-MM-DD>.md` |
| A status snapshot at the gate | `.claude/progress/snapshot-<YYYY-MM-DD>.md` |
| The outcome and any accepted risks | `.claude/change.log.md` |
| Phase exit against its criteria | `.claude/plan/phase-<n>-<slug>.md` |

**An accepted risk needs a named accepter and a revisit date** in the change log. "We'll fix it later"
is not an acceptance — it is an undocumented decision that nobody owns.

**Check [`.claude/qa/`](../../qa/) for the previous gate** before running this one. A risk accepted
last gate whose revisit date has passed is a finding for this gate.

## Rules

- **Evidence, not assertion.** Paste the real output. A gate passed on a claim is a gate not run.
- **Gate 4 blocks alone.** A boundary breach is not tradeable against schedule.
- **An accepted risk needs a named accepter and a revisit date**, recorded in `change.log.md`. "We'll fix it later" is not an acceptance.
- **Never weaken a check to make a gate pass.** Change the code or accept the risk explicitly.
- **Report failures plainly.** A failed gate reported honestly is worth more than a passed one that was not really run.
