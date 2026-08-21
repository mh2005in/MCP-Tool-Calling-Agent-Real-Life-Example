# Requirements — per-requirement detail

**Summary lives at:** [`../Requirements.md`](../Requirements.md) — the register: IDs, one-line
statements, statuses, decisions. **This folder holds the detail** that would bury the register.

---

## What goes here

One file per requirement that needs more than a table row — typically anything `L`/`XL` effort, or
anything where the acceptance criteria need spelling out.

| Filename | For |
| --- | --- |
| `<ID>.md` | e.g. `F41-01.md`, `GAP-02.md`, `SEC-06.md` |
| `<ID>-<slug>.md` | when one requirement needs several documents, e.g. `GAP-02-fee-model.md` |

**Not every requirement needs a file.** A drift item fixed in an afternoon lives fine as a register
row. Create a file when someone would otherwise have to re-derive the analysis.

## Template

```markdown
# <ID> — <title>

**Status:** OPEN | PARTIAL | VERIFIED | BLOCKED | SUPERSEDED
**Priority / Effort:** P0–P3 / S-M-L-XL
**Phase:** <from ../Plan.md>
**Source:** <file in ../input/, with section>
**Depends on:** <IDs>
**Blocks:** <IDs>

## Requirement
<what must be true — checkable, not aspirational>

## Acceptance criteria
- [ ] <observable condition>

## Current state
<what exists today, with file:line evidence — not what a document claims>

## Design notes
<link to ../design/ if there's an ADR>

## Open questions
<genuine ambiguities needing a user decision>
```

## Rules

- **Acceptance criteria are observable.** "`IntakeService` rejects unknown question keys" — not "intake validation is improved".
- **Current state cites code**, with `file:line`. A "Completed" marking in a source document is a claim, not evidence — the 2026-08-21 audit found 11 open items inside signed-off work.
- **Status here and in [`../Requirements.md`](../Requirements.md) must agree.** The `status-sync` skill checks it.
- **Every requirement belongs to exactly one phase** in [`../Plan.md`](../Plan.md).
- **No real PII or secrets** in examples or sample payloads (CLAUDE.md §8).

## Who writes here

`requirements-analyst` (analysis, drift), the `requirement-intake` skill (new requirements),
`docs-sync` (status reconciliation).
