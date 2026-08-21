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

Every `VERIFIED` in the dashboard was confirmed by looking at the code. That is the only reason the
dashboard is trustworthy — the 2026-08-21 audit found **11 open items inside work that documents had
already marked complete**, four of them High severity. One backlog entry named a single mis-routed
controller; there were three.

A count of zero is a finding. Zero pagination, zero rate limiters, zero malware scanners were each
findings.

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

## Where things stand (2026-08-21)

| | |
| --- | --- |
| Baseline | 🟢 All four P0 validation defects and all five Critical security findings genuinely fixed |
| Biggest exposure | 🔴 **6 test files** for ~30 services and 17 controllers (`DR-10`) |
| Nearest value | 🟡 Section 4.1 at M5/M6 — 13 scoped items from done |
| Hard blocker | ⛔ `F41-14` — needs a licensing decision (`D-1`), not engineering |
| Open decisions | 6 · `D-1`…`D-6` — four should close before Phase 2 |

70 requirements: 10 verified · 13 partial · 45 open · 1 blocked · 1 superseded.

---

## Conventions

- **Absolute dates** (`YYYY-MM-DD`), never relative.
- **Requirement IDs everywhere** — `BL-` baseline · `DR-` drift · `F41-` Section 4.1 · `SEC-` hardening · `GAP-` Phase 5 domain.
- **Three traceability invariants:** every requirement appears in exactly one dashboard row, exactly one plan phase, and — if `VERIFIED` — has a changelog entry. The `status-sync` skill checks them.
- **No real PII, secrets, or infrastructure identifiers** anywhere in this folder (CLAUDE.md §8) — that includes tenant IDs and client IDs, which had to be redacted from [`input/runbooks/`](input/runbooks/).
