---
name: requirement-intake
description: >-
  Normalise a new or updated requirement document into the register — extract it,
  reconcile it against the code, assign IDs, write acceptance criteria, and place
  it in the plan. Use when a document lands in HighLevelRequirement Pending/, when
  a requirement arrives by any other route, or when an existing requirement needs
  re-baselining against reality.
---

# Requirement intake

Turns prose into tracked, verifiable requirements. **An unnormalised requirement has no ID, no
acceptance criteria, and no place in the plan** — so it cannot be scheduled, tested, or reported on.

Upstream corpus: `C:\Users\mh200\Downloads\SoftwareForImmigrationConsultants\`

| Folder | Meaning |
| --- | --- |
| `HighLevelRequirement Completed/` | Baseline — **claims**, to be verified, not trusted |
| `HighLevelRequirement Pending/` | Open work |
| `Runbooks/` | Superseded — they describe Entra; the stack runs Keycloak (`D-5`) |

---

## 1 · Extract

PDFs need `-layout`. These documents are table-heavy and become meaningless without it:

```bash
pdftotext -layout "<source>.pdf" "<scratch>/<name>.txt"
```

Markdown sources are read directly and **win over their generated PDF twins** — for example
`Section-4.1-Backlog.md` is the source of truth over `Section-4.1-Backlog.pdf`.

Get the outline before reading in full:

```bash
grep -nE "^\s*(#|[0-9]+\.[0-9. ]*[A-Z]|Section)" "<extracted>.txt" | head -60
```

---

## 2 · Decompose

Break the document into **discrete, checkable** assertions. The test: could someone confirm or refute
this by reading code?

| ❌ Not checkable | ✅ Checkable |
| --- | --- |
| "Improve intake validation" | "`IntakeService` resolves questions from server-side templates by key and rejects unknown keys" |
| "Add security" | "Every list and search endpoint accepts a `Pageable` and returns a bounded page" |
| "Forms should work" | "No form with `formTechnology = DYNAMIC_XFA` has `supportsFill = true`" |

---

## 3 · Reconcile against the code

**Before writing a single requirement, check whether it already exists.** This is the step that
matters most.

Delegate to the `requirements-analyst` agent, or check directly:

```bash
find backend/src/main/java -name "*<Concept>*"
grep -rn "<specificConstant>" backend/src/main/java --include=*.java
grep -rn "<pattern>" backend/src/main/java --include=*.java | wc -l   # zero is a finding
```

Four outcomes:

| Finding | Action |
| --- | --- |
| Already implemented | Record as `BL-*` `VERIFIED`, with the evidence. **Do not schedule work.** |
| Partly implemented | Record as `PARTIAL` and **name precisely what is missing** |
| Not implemented | New requirement, `OPEN` |
| Implemented but wrong, or contradicts a "complete" claim | **`DR-*` drift item** — the code wins as a statement of fact; the contradiction becomes a decision |

> The 2026-08-21 audit found 11 open items inside work already signed off. One backlog entry named a
> single mis-routed controller; there were three. **Verify, always.**

---

## 4 · Assign IDs

From the existing scheme in `.claude/Requirements.md` §1:

| Prefix | For |
| --- | --- |
| `BL-nn` | Delivered and verified baseline |
| `DR-nn` | Drift — an open finding or a rule the code violates |
| `F41-nn` | Section 4.1 form & package automation |
| `SEC-nn` | Phase-4 hardening |
| `GAP-nn` | Phase 5 product domain |

Take the next free number. **Check for a duplicate before minting one.**

---

## 5 · Write acceptance criteria

Every requirement needs a condition under which it becomes `VERIFIED`. Borrow the source document's
own "Minimum viable release" where it has one — Phase 5 supplies one per domain, and those are the
acceptance definitions.

Good criteria are observable:

> `F41-01` is `VERIFIED` when every onboarded form has an inspection-derived `supportsFill`, `status`,
> `sourceSha256`, and technology classification; **no dynamic-XFA form is marked fillable**; and a
> round-trip fixture proves each AcroForm-compatible form fills correctly.

---

## 6 · Check the product boundary

Every requirement is subordinate to `.claude/Requirements.md` §2. **Flag immediately** anything that
would have the system:

- decide eligibility, interpret law, or guarantee an outcome (`BR-1`);
- send an artefact to a client or IRCC without consultant approval (`BR-2`);
- carry a rule without a source URL, version, and review date (`BR-3`);
- touch PII without an audit record (`BR-4`);
- move unmasked PII outside the tenant (`BR-5`).

**This overrides scope and schedule.** A requirement that breaches the boundary is escalated, not
implemented.

---

## 7 · Classify effort and dependencies

- **Priority and effort:** use the source document's own rating where it has one (Phase 5 gives P0–P3 and S/M/L/XL).
- **Dependencies:** what must exist first? Note anything gated by the outbox (`GAP-12`), the tenant boundary (`GAP-13`), or the test foundation (`DR-10`).
- **Blockers:** if it needs a decision or a purchase, mark it `BLOCKED` and add the decision to §10 — as `F41-14` is blocked on `D-1`.
- **Buy vs build:** check §8. A requirement proposing to build card payments, e-signatures, message delivery, OCR, malware scanning, or accounting must be challenged first.

---

## 8 · Place it in the plan

Add it to exactly **one** phase in `.claude/Plan.md`, and one row in `.claude/status dashboard.md`.
Those are two of the three traceability invariants the `status-sync` skill checks.

---

## 9 · Record the intake

| What | Where |
| --- | --- |
| The extracted source, if new | `.claude/input/<folder>/` — **re-run the PII scan first** (see [`input/README.md`](../../input/README.md)) |
| Per-requirement detail | `.claude/requirements/<ID>.md` |
| The intake itself | `.claude/progress/verification-<YYYY-MM-DD>.md` |
| A constraint or dead end found while reconciling | `.claude/memory/<slug>.md` |
| An `AUDIT` entry | `.claude/change.log.md` — what was ingested, IDs created, what was already implemented, what drift surfaced |

**Adding a document to [`input/`](../../input/) means scanning it first.** The Entra runbooks
contained a real tenant ID and four client IDs that had to be redacted before they could be
committed (CLAUDE.md §8).

---

## Output

```
## Source
<document, date, extraction method>

## New requirements
| ID | Requirement | Pri | Effort | Status | Phase |

## Already implemented (no work needed)
| ID | Requirement | Evidence |

## Drift found
| ID | Finding | Severity | Evidence |

## Blocked / needs a decision
| ID | Blocked on | Decision ref |

## Boundary concerns
<anything touching BR-1..BR-5, or nothing>

## Files to update
- .claude/Requirements.md — <sections>
- .claude/Plan.md — <phase>
- .claude/status dashboard.md — <rows>
- .claude/change.log.md — AUDIT entry
```

---

## Rules

- **Never trust a "Completed" marking.** Verify in code.
- **Never invent a requirement** no source asks for. A recommendation of your own is fine — label it as yours.
- **Never assume** — when a document admits two readings, ask which (CLAUDE.md §3).
- **Ask before editing** the register (CLAUDE.md §3).
- **When a document and the code disagree**, that is a finding to surface, not a discrepancy to quietly resolve.
