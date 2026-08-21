# Plan — phase detail and scheduling

**Summaries live at:** [`../Plan.md`](../Plan.md) (phases, sequencing, critical path, risks) and
[`../Delivery approach.md`](../Delivery%20approach.md) (how work is executed). **This folder holds the
working detail** — phase breakdowns, slice plans, and the record of re-sequencing.

---

## What goes here

| Filename | For |
| --- | --- |
| `phase-<n>-<slug>.md` | A phase broken into slices with owners, dependencies, and dates — e.g. `phase-0.5-drift.md` |
| `slice-<REQ-ID>.md` | The plan for one vertical slice, when it's large enough to need one |
| `resequencing-<date>.md` | A record of why the plan changed |

---

## Phase breakdown template

```markdown
# Phase <n> — <name>

**Window:** <from ../Plan.md>
**Outcome:** <one sentence>
**Exit criteria:** <from ../Plan.md — copied so this file stands alone>

## Slices
| # | Requirement | Slice | Depends on | Agent(s) | Status |

## Dependencies out of phase
<what this phase needs from elsewhere, and what waits on it>

## Risks specific to this phase
| Risk | Impact | Mitigation |

## Exit checklist
- [ ] Every scheduled requirement VERIFIED or with a written acceptance
- [ ] Exit criteria met
- [ ] Guardrail metrics instrumented (Phase 5 §7)
- [ ] `release-gate` skill run and passed
```

---

## Re-sequencing is a decision

Moving a requirement between phases is not bookkeeping. Record it:

```markdown
# Re-sequencing — YYYY-MM-DD

## What moved
| Requirement | From | To | Why |

## What this delays
<downstream effects — be honest about them>

## Who decided
```

Then update [`../Plan.md`](../Plan.md) and [`../change.log.md`](../change.log.md) in the same change.

---

## The three constraints the plan is built around

Repeated here because phase files must not violate them:

1. **Drift before features.** Phase 0.5 exists because an audit finding left open is cheaper now than after ten features are built on it.
2. **Structural before additive.** The outbox (`GAP-12`) and the tenant boundary (`GAP-13`) get harder to retrofit with every entity added.
3. **Nothing ships without its guardrail.** Every automation metric pairs with a quality or safety measure, so speed cannot hide defects.

**Three items gate the most downstream work** — the test suite (`DR-10`), the outbox, and the tenant
boundary. All three are Phase 0/0.5. Reordering any of them earlier is fine; later needs an explicit
decision.

---

## Rules

- **Every requirement belongs to exactly one phase.** The `status-sync` skill checks it. Note that `BL-*` are already delivered and correctly unscheduled, and `Plan.md` writes contiguous runs as ranges — expand them before calling an ID unscheduled.
- **A phase exits on its exit criteria, not on its task list.** Closing every task while missing an exit criterion means the phase is not done.
- **Capture baseline metrics before a feature launches**, or its success measure is unfalsifiable.

## Who writes here

The `feature-slice` skill (slice plans), `requirements-analyst` (dependency analysis), `docs-sync`
(keeping [`../Plan.md`](../Plan.md) in sync).
