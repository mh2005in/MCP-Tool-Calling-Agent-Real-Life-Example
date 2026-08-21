# Progress — status snapshots and work records

**Summaries live at:** [`../status dashboard.md`](../status%20dashboard.md) (where every requirement
stands) and [`../change.log.md`](../change.log.md) (what changed, when, why). **This folder holds the
records behind them** — dated snapshots, verification runs, and per-slice completion reports.

---

## What goes here

| Filename | For |
| --- | --- |
| `snapshot-YYYY-MM-DD.md` | A dated status snapshot, so trend is visible — is the drift list shrinking? |
| `verification-YYYY-MM-DD.md` | A `status-sync` or audit run: what was checked, what the evidence was, what changed |
| `slice-<REQ-ID>-YYYY-MM-DD.md` | A completed slice: what shipped, what was verified, what was deferred |

---

## Why snapshots matter

A dashboard shows the present. Snapshots show the **direction**, which is the thing that actually
tells you whether the project is healthy. Two numbers worth tracking every time:

| Metric | 2026-08-21 baseline |
| --- | --- |
| Open drift items (`DR-*`) | **10 of 11** |
| Test files | **6** |

```bash
find backend/src/test MCPServer/src/test -name "*.java" | wc -l
```

If the test count is not climbing during Phase 0.5, the phase is not working regardless of what else closes.

---

## Verification record template

```markdown
# Verification — YYYY-MM-DD

**Scope:** <full audit | a requirement group | one slice>
**Method:** <how — code inspection, greps, test run>

## Status changes
| ID | Was | Now | Evidence (file:line or count) |

## New drift found
| ID | Finding | Severity | Evidence |

## Claims that no longer hold
<any previously VERIFIED requirement whose evidence has disappeared — this is new drift, and it
matters more than anything else in the report>

## Inventory
| Metric | Was | Now |
```

---

## Slice completion template

```markdown
# <REQ-ID> — completed YYYY-MM-DD

## Shipped
| File | Change |

## Verified
- Tests: <count, and the actual runner output>
- Deploy: <deploy-verify result>
- Security review: <result, or n/a with the reason>

## Deferred
<what was in scope and did not ship, and why — scaling down is the user's call, so it must be visible>

## Follow-ups created
<new requirement IDs, if any>
```

---

## Rules

- **Statuses come from code, never from a document.** Every `VERIFIED` in the dashboard was confirmed by looking at the code. That is the only reason it is worth reading — the 2026-08-21 audit found 11 open items inside work documents had already marked complete.
- **A count of zero is a finding.** Zero pagination, zero rate limiters, zero malware scanners were each findings.
- **A `VERIFIED` claim whose evidence has vanished is new drift.** Surface it loudly; do not quietly downgrade the row.
- **Record deferrals.** Work that was in scope and did not ship is the most useful line in a completion report.
- **Never rewrite history.** A snapshot is what was true that day; correct it with a new entry, not an edit.
- **No real PII or secrets** in evidence quotes — cite `file:line` and redact values (CLAUDE.md §8).

## Who writes here

The `status-sync` skill (verification runs, snapshots), `docs-sync` (reconciliation), the
`feature-slice` skill (slice completions), `deploy-verify` and `security-reviewer` (their results,
when a slice records them).
