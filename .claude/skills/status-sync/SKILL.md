---
name: status-sync
description: >-
  Refresh the status dashboard from the code, recount the inventory, and verify the
  three traceability invariants between Requirements, Plan, and change log. Use when
  the dashboard is stale, after a batch of work lands, before a phase gate, or when
  someone asks where the project actually stands.
---

# Status sync

Regenerates `.claude/status dashboard.md` **from the code**, and checks that the register, the plan,
and the change log still agree with each other.

> ### The rule this skill exists to enforce
>
> **Never update a status from a document.** Every `VERIFIED` in the dashboard was confirmed by
> looking at code — that is the only reason the dashboard is worth reading.
>
> The 2026-08-21 audit found **11 open items inside work that documents had already marked complete**,
> four of them High severity. One backlog entry named a single mis-routed controller; there were three.

---

## 1 · Recount the inventory

Mechanical. Run them; do not adjust the previous numbers.

```bash
# Domain size
ls backend/src/main/java/com/immiauto/entity/*.java | wc -l
ls backend/src/main/java/com/immiauto/controller/*.java | wc -l
ls backend/src/main/java/com/immiauto/service/*.java | wc -l
find frontend/src/app/features -name "*.component.ts" | wc -l
ls backend/src/main/resources/db/migration/postgresql/V*.sql | wc -l

# Control coverage — a count of zero is a finding
grep -rn "@PreAuthorize" backend/src/main/java/com/immiauto/controller/*.java | wc -l
grep -rn "@Valid" backend/src/main/java --include=*.java | wc -l
grep -rn "logAudit" backend/src/main/java --include=*.java | wc -l
grep -rn "Pageable" backend/src/main/java --include=*.java | wc -l
grep -rniE "bucket4j|ratelimit|rate-limit" backend/src/main --include=* | wc -l
grep -rniE "clamav|malware|virusscan|antivirus" backend/src/main | wc -l

# The single most telling number
find backend/src/test MCPServer/src/test -name "*.java" | wc -l
```

Update §8 of the dashboard with the real numbers.

## 2 · Re-verify every `VERIFIED` claim

Take each `VERIFIED` row and confirm its evidence still holds. Code moves; a claim verified in July
may be false in August.

```bash
# Example: BL-10 — compliance calculators
grep -nE "MIN_PR_DAYS|PRE_PR_CAP_DAYS" backend/src/main/java/com/immiauto/service/TravelHistoryService.java
grep -nE "CONTINUOUS_DAYS_THRESHOLD|MINIMUM_AGE" backend/src/main/java/com/immiauto/service/PoliceCertificateService.java
grep -n "preliminaryReviewStatus" backend/src/main/java/com/immiauto/service/LmiaCalculatorService.java
```

**A claim whose evidence has disappeared becomes a new `DR-*` drift item.** That is a finding worth
surfacing loudly, not a number to quietly edit down.

## 3 · Re-check the open drift list

Confirm each `DR-*` still reproduces — some may have been fixed in passing:

```bash
grep -rn '@RequestMapping("/api")' backend/src/main/java/com/immiauto/controller/*.java  # DR-04
grep -nE "expir|revok" backend/src/main/java/com/immiauto/entity/PartyProfile.java        # DR-05
grep -rn "@CrossOrigin" backend/src/main/java/com/immiauto/controller/*.java | wc -l      # DR-06
grep -n "show-sql" backend/src/main/resources/application.properties                      # DR-08
grep -rn "length" backend/src/main/java/com/immiauto/entity/*.java                        # DR-03
```

A drift item that no longer reproduces moves to `VERIFIED` **with the evidence**, and gets a
`change.log.md` entry.

## 4 · Verify the three traceability invariants

Every requirement in `.claude/Requirements.md` must satisfy all three:

| # | Invariant | Failure means |
| --- | --- | --- |
| 1 | Appears in exactly **one** row of `status dashboard.md` | Untracked, or double-counted |
| 2 | Scheduled in exactly **one** phase of `Plan.md` | Nobody owns it, or two phases both assume the other did it |
| 3 | If `VERIFIED`, has an entry in `change.log.md` | Delivered with no record of when or why |

Extract and compare the ID sets:

```bash
cd .claude
T=$(mktemp -d)
grep -oE '`(BL|DR|F41|SEC|GAP)-[0-9]+`' Requirements.md      | tr -d '`' | sort -u > "$T/req.txt"
grep -oE '`(BL|DR|F41|SEC|GAP)-[0-9]+`' "status dashboard.md" | tr -d '`' | sort -u > "$T/dash.txt"
grep -oE '`(BL|DR|F41|SEC|GAP)-[0-9]+`' Plan.md               | tr -d '`' | sort -u > "$T/plan.txt"

echo "In Requirements but not the dashboard:"; comm -23 "$T/req.txt" "$T/dash.txt"
echo "In the dashboard but not Requirements:"; comm -13 "$T/req.txt" "$T/dash.txt"
echo "In Requirements but unscheduled:";       comm -23 "$T/req.txt" "$T/plan.txt"
```

### Two expected exemptions on invariant 2

The unscheduled list is **not** a straight failure list. Two categories legitimately appear in it:

1. **`BL-*` baseline requirements.** They are already delivered, so they have no future phase. Expect all of them in the output; that is correct.
2. **IDs covered by range notation.** `Plan.md` writes contiguous runs as `` `F41-03` ``…`` `F41-10` `` and `` `SEC-11` ``–`` `SEC-13` ``, so the grep sees only the endpoints. **Expand ranges by hand before treating a middle ID as unscheduled.**

Filter the baseline out, then check the remainder against the ranges:

```bash
grep -v '^BL-' "$T/req.txt" > "$T/req-active.txt"
comm -23 "$T/req-active.txt" "$T/plan.txt"
```

A genuine failure is an active requirement that is neither listed nor inside a stated range.

## 5 · Recompute the roll-up

Update the portfolio table and the bar chart in §2 of the dashboard from the recounted statuses. The
totals must reconcile with §3–§7 — if they do not, the detail is right and the summary is wrong.

## 6 · Refresh the headline and next actions

§1 and §10 of the dashboard should reflect what a reader most needs to know **now**:

- What is the biggest current exposure?
- What is the nearest available value?
- What is genuinely blocked, and on what decision?
- Which three things should happen next?

Do not carry these forward unchanged out of habit — they are the part of the dashboard people
actually read.

## 6b · Write the records

The dashboard shows the present; the records show the direction.

| What | Where |
| --- | --- |
| This run — what was checked, evidence, what changed | `.claude/progress/verification-<YYYY-MM-DD>.md` |
| A dated status snapshot, so trend is visible | `.claude/progress/snapshot-<YYYY-MM-DD>.md` |
| Any status change | `.claude/change.log.md` |
| New drift | `.claude/Requirements.md` §4 and `.claude/requirements/<ID>.md` |

Two numbers to carry forward every run — they are what tell you whether the project is getting
healthier:

| Metric | 2026-08-21 baseline |
| --- | --- |
| Open drift items (`DR-*`) | 10 of 11 |
| Test files | 6 |

## 7 · Update the dates

Set **Last verified** in the dashboard to today's date, and state the verification method. A dashboard
without a verification date is a dashboard nobody can trust.

---

## Output

```
# Status sync — <date>

## Inventory changes
| Metric | Was | Now | Note |

## Status changes
| ID | Was | Now | Evidence |

## New drift found
| ID | Finding | Severity | Evidence |

## Traceability
- Requirements missing a dashboard row: <list or none>
- Requirements missing a plan phase: <list or none>
- VERIFIED without a changelog entry: <list or none>

## Files updated
- .claude/status dashboard.md
- .claude/change.log.md  (if statuses changed)
- .claude/Requirements.md (if new drift found)
```

---

## Rules

- **Verify in code, never from a document.** This is the entire point.
- **A count of zero is a finding**, not an empty result — zero `Pageable`, zero rate limiters, and zero malware scanners were each findings in the last audit.
- **A `VERIFIED` claim whose evidence vanished is new drift.** Surface it; do not silently downgrade the row.
- **Never mark something verified you have not seen in the code.**
- **Ask before editing** the register (CLAUDE.md §3).
- **Report honestly.** A dashboard showing more red than last time, backed by evidence, is more useful than one showing green.
