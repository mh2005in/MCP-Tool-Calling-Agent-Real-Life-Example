# Design — architecture decisions and technical designs

**Summary lives at:** [`../Architecture.md`](../Architecture.md) — as-built topology, layering, target
architecture, the architectural rules, the debt register. **This folder holds the reasoning** behind
individual decisions and the designs for work not yet built.

---

## What goes here

### Architecture Decision Records — `ADR-nnn-<slug>.md`

One per decision that constrains future work. A decision that only affects one file is a code
comment; a decision that shapes what everything after it can do is an ADR.

Write one when: choosing between technologies, committing to a pattern, accepting a constraint,
rejecting an option, or reversing an earlier decision.

### Technical designs — `<REQ-ID>-design.md`

For requirements too large to design inside the implementation. Anything `XL`, anything crossing
three or more modules, anything with a "how do we even" question.

---

## ADR template

```markdown
# ADR-nnn — <decision, stated as the outcome>

**Status:** Proposed | Accepted | Superseded by ADR-nnn
**Date:** YYYY-MM-DD
**Requirements:** <IDs>

## Context
<the forces — what makes this a real choice rather than an obvious one>

## Options considered
| Option | Pros | Cons | Verdict |

## Decision
<what was chosen, in one sentence>

## Consequences
<what this makes easy, what it makes hard, what it forecloses>

## Reversal
<what it would take to undo — some decisions are cheap to reverse, some aren't. Say which.>
```

---

## Decisions already recorded elsewhere

These predate the folder and live in [`../Architecture.md`](../Architecture.md) §7 and
[`../change.log.md`](../change.log.md). **Do not duplicate them here** — link instead.

| Decision | Where | Status |
| --- | --- | --- |
| `D-5` Keycloak over Microsoft Entra External ID | [`../Architecture.md`](../Architecture.md) §7 | Accepted 2026-08-21 |
| `D-1` Commercial XFA engine vs data-sheet fallback | [`../change.log.md`](../change.log.md) | **Open — blocks `F41-14`** |
| SaaS form-fill APIs rejected (PII egress) | [`../Requirements.md`](../Requirements.md) §8 | Settled |
| iText 7 under AGPL rejected (licence) | [`../Requirements.md`](../Requirements.md) §8 | Settled |

New ADRs start at `ADR-001`.

---

## Rules

- **An ADR records the reasoning, not just the outcome.** The options rejected, and why, are the part worth keeping — a year later nobody remembers what else was on the table.
- **Never delete a superseded ADR.** Mark it `Superseded by ADR-nnn` and leave it. The reasoning that led somewhere wrong is still evidence.
- **A decision that overrides an architectural rule** in [`../Architecture.md`](../Architecture.md) §6 needs an ADR — not a workaround.
- **Cross-link both ways:** the ADR names its requirement IDs; the requirement file links the ADR.
- **No real PII, secrets, or infrastructure identifiers** in examples (CLAUDE.md §8) — tenant IDs and client IDs included.

## Who writes here

`requirements-analyst` and `security-reviewer` (proposals and constraints), `backend-feature`
(technical designs), `docs-sync` (keeping [`../Architecture.md`](../Architecture.md) in sync when an
ADR lands).
