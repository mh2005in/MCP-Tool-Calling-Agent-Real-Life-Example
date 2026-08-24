# `.claude/` — project tracking and automation

Everything needed to understand, plan, execute, and verify work on this platform.

> **[README.md](../README.md)** is how to *run* the project. **[CLAUDE.md](../CLAUDE.md)** is how we
> *work on* it. **This folder** is what we're building, in what order, and where it stands.

---

## Start here

| I want to… | Read |
| --- | --- |
| Know what must be built | [Requirements.md](Requirements.md) |
| Know where things stand | [status dashboard.md](status%20dashboard.md) |
| Know what to do next | [Plan.md](Plan.md) |
| Know how the system is built | [Architecture.md](Architecture.md) |
| Know how work gets executed | [Delivery approach.md](Delivery%20approach.md) |
| Know what changed and why | [change.log.md](change.log.md) |

---

## Layout

Six documents at root are the **summaries** — the thing you read first. Eight folders hold the
**detail** behind them.

```
.claude/
├── Requirements.md          ← WHAT     70 requirements, boundary, decisions
├── Architecture.md          ← HOW      as-built + target, debt register
├── Plan.md                  ← WHEN     5 phases + Phase 0.5, critical path
├── Delivery approach.md     ← HOW WE WORK   slice procedure, definition of done
├── status dashboard.md      ← WHERE    per-requirement status, from code
├── change.log.md            ← HISTORY  changes and decisions
│
├── requirements/   per-requirement detail — <ID>.md
├── design/         ADRs and technical designs — ADR-nnn-<slug>.md
├── plan/           phase breakdowns, slice plans, re-sequencing records
├── progress/       dated snapshots, verification runs, slice completions
├── qa/             test plans, review findings, release-gate records
├── operations/     runbooks, deployment records, incidents
├── input/          the source requirement corpus (extracted, redacted, greppable)
├── memory/         durable project facts — gotchas, constraints, dead ends
│
├── agents/         8 agents
└── skills/         6 skills
```

**Each folder has a README** stating its purpose, file-naming convention, templates, and which agents
write to it. Read that before adding a file.

---

## The rule that makes this worth reading

> **Statuses come from code, never from a document.**

Every `VERIFIED` in the dashboard was confirmed by looking at the code. Two verification passes have
now proved the rule necessary **in both directions**:

- **2026-08-21** — documents *overstate* completeness. Found 11 open items inside work already marked complete, four of them High severity. One backlog entry named a single mis-routed controller; there were three.
- **2026-08-24** — documents *understate* it too. Found **9 delivered items recorded as `OPEN`**, because a backlog listed them as remaining tasks and nobody opened `CasePackageService`. The register called Milestone 5 "code-complete" and its tasks "outstanding" in adjacent rows of the same table.

A status copied from a document is wrong in whichever direction that document was wrong. Neither
optimism nor pessimism is the safe default.

A count of zero is a finding. Zero pagination, zero rate limiters, zero malware scanners, and zero
frontend tests were each findings. **A count carried forward from memory is not a count** — "22
entities" and "30 services" were both wrong by roughly a third.

---

## Automation

### Agents — work that deserves its own context

| Agent | Model | For | Writes |
| --- | --- | --- | --- |
| [`requirements-analyst`](agents/requirements-analyst.md) | opus | Reconcile a requirement against the code; find drift | — |
| [`backend-feature`](agents/backend-feature.md) | opus | Backend vertical slice | code |
| [`frontend-feature`](agents/frontend-feature.md) | sonnet | The Angular half of a slice | code |
| [`db-migration`](agents/db-migration.md) | sonnet | V-numbered SQL migrations | SQL |
| [`test-author`](agents/test-author.md) | sonnet | Tests, incl. failing-without-the-fix | tests |
| [`security-reviewer`](agents/security-reviewer.md) | opus | Audit a diff against controls and the boundary | — |
| [`docs-sync`](agents/docs-sync.md) | sonnet | Reconcile docs with the code | docs |
| [`deploy-verify`](agents/deploy-verify.md) | sonnet | Rebuild, redeploy, confirm health | — |

### Skills — procedures loaded on demand

| Skill | Invoke when |
| --- | --- |
| [`requirement-intake`](skills/requirement-intake/SKILL.md) | A new requirement document needs normalising |
| [`feature-slice`](skills/feature-slice/SKILL.md) | Starting any vertical slice |
| [`content-governance`](skills/content-governance/SKILL.md) | Publishing or retiring governed immigration content |
| [`release-gate`](skills/release-gate/SKILL.md) | Before a phase gate or release |
| [`status-sync`](skills/status-sync/SKILL.md) | Refreshing the dashboard from the code |
| [`worktree`](skills/worktree/SKILL.md) | Creating or cleaning up a git worktree |

**Every agent and skill records its output here.** Each one's "Recording your work" section names its
folder and which root doc it updates. That obligation is what keeps this folder from going stale.

---

## Where things stand (2026-08-24)

| | |
| --- | --- |
| Baseline | 🟢 All four P0 validation defects and all five Critical security findings genuinely fixed |
| Biggest correction | 🔵 **Section 4.1 Milestone 5 is complete** — 9 items tracked as `OPEN` are delivered |
| Biggest exposure | 🔴 **6 backend/MCP test files and 0 frontend** for 37 services, 17 controllers, 27 components (`DR-10`, `DR-14`) |
| Nearest value | 🟡 Section 4.1 needs **5 items**, not 13 — `F41-01`, `F41-02`, `F41-12`, `F41-13`, `GAP-09` |
| Hard blocker | ⛔ `F41-14` — needs a licensing decision (`D-1`), not engineering |
| Open decisions | 5 of 6 open · `D-1`…`D-4`, `D-6`. `D-5` decided (Keycloak) |

**74 requirements: 20 verified · 15 partial · 36 open · 1 blocked · 2 superseded.**
Evidence: [`progress/2026-08-24-verification-run.md`](progress/2026-08-24-verification-run.md).

---

## Conventions

- **Absolute dates** (`YYYY-MM-DD`), never relative.
- **Requirement IDs everywhere** — `BL-` baseline · `DR-` drift · `F41-` Section 4.1 · `SEC-` hardening · `GAP-` Phase 5 domain.
- **Three traceability invariants:** every requirement appears in exactly one dashboard row, exactly one plan phase, and — if `VERIFIED` — has a changelog entry. The `status-sync` skill checks them.
- **No real PII, secrets, or infrastructure identifiers** anywhere in this folder (CLAUDE.md §8) — that includes tenant IDs and client IDs, which had to be redacted from [`input/runbooks/`](input/runbooks/).
