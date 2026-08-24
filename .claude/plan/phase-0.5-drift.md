# Phase 0.5 — Drift

**Window:** Weeks 1–5
**Outcome:** The code matches its own audits, and the suite can prove it.
**Exit criteria:** All 13 open `DR-*` items `VERIFIED` or carrying a written acceptance in [change.log.md](../change.log.md); backend test count above 60 files; frontend spec count above 0; `deploy-verify` healthy after each merge.

> **Why this phase exists.** Thirteen findings sit inside work that was already signed off as
> complete. Five are High severity. None require a design decision. Every one of them gets more
> expensive once another phase is built on top of it.

---

## Slices

Grouped so related findings ship together — four separate slices for four lines in two properties
files would mean four rebuild-and-verify cycles for one edit's worth of change.

| # | Requirements | Slice | Depends on | Agent(s) | Status |
| --- | --- | --- | --- | --- | --- |
| 1 | `DR-08`, `DR-09`, `DR-12`, `DR-13` | **Config hygiene** — SQL logging off in the main profile; DB-password fallback removed from **both** profiles; stale MCP-API-key comment deleted | — | `backend-feature`, `security-reviewer` | ⬜ |
| 2 | `DR-04`, `DR-06` | **Routing + CORS** — three controllers moved under `/v1/**`; CORS consolidated to `CorsConfig`, 9 × `@CrossOrigin` removed; frontend `API_ENDPOINTS` updated in the same change | — | `backend-feature`, `frontend-feature` | ⬜ |
| 3 | `DR-03` | **Length constraints** — drop `length = 15` from the three number columns; confirm the generated-column definitions still hold | — | `db-migration`, `backend-feature` | ⬜ |
| 4 | `DR-05` | **Party-portal token lifecycle** — expiry, revocation, rotation; token masked in every response | 1 | `backend-feature`, `security-reviewer` | ⬜ |
| 5 | `DR-07` | **Pagination** — `Pageable` on every list and search endpoint, bounded default page size, SPA paginates | 2 | `backend-feature`, `frontend-feature` | ⬜ |
| 6 | `DR-02` | **PNP seed parity** — PNP checklist templates seeded to match the other ten types, each with `sourceUrl` and `lastReviewedDate` | — | `db-migration` | ⬜ |
| 7 | `DR-10`, `DR-14` | **Test foundation** — the largest item in the phase. See [`../qa/DR-10-test-foundation.md`](../qa/DR-10-test-foundation.md) | runs alongside 1–6 | `test-author` | ⬜ |
| 8 | `DR-15` | **Stale route comment** — resolved by `F41-13`, or corrected in place if `F41-13` has not landed | — | `frontend-feature` | ⬜ |

### Suggested week map

| Week | Slices |
| --- | --- |
| 1 | 1, 2, 3 — all small, all independent |
| 2 | 4, 5 |
| 3 | 6, and slice 7 begins in earnest |
| 3–5 | 7 — test foundation |
| 5 | 8, exit checklist |

---

## Dependencies out of phase

**What this phase needs from elsewhere:** nothing. Every item is self-contained, which is why it goes first.

**What waits on it:**

| Waiting | Why |
| --- | --- |
| All of Phase 0 and Phase 1 | Slice 7 is the only thing that makes a refactor safe. [Plan.md](../Plan.md) §9 puts it at the head of the critical path |
| `SEC-04` (secrets to a vault) | Slice 1 removes the insecure fallback; `SEC-04` moves the real value. Doing them in the wrong order leaves a window where neither holds |
| `GAP-06` (full portal) | Slice 4 is the interim fix for `DR-05`; the authenticated portal in Phase 3 makes it permanent |
| `F41-13` | Slice 8 |

---

## Risks specific to this phase

| Risk | Impact | Mitigation |
| --- | --- | --- |
| **Slice 7 slips** — it is large, unglamorous, and every other slice looks more urgent | Every later phase proceeds without a regression net. This is the portfolio's top risk | Non-negotiable phase content. **If capacity is short, cut slice 6 — not slice 7.** Start with characterisation tests for the nine `F41` items, which are the largest body of working untested code |
| **Slice 2 breaks the frontend silently** — moving three controllers changes their paths | Broken UI with no test to catch it | Frontend `API_ENDPOINTS` update is *inside* slice 2, not a follow-up. `deploy-verify` exercises each moved endpoint |
| **Slice 5 changes every list response shape** | Every consuming component breaks at once | Sequence after slice 2 so routing has settled. Consider an envelope that keeps the array at a stable key |
| **Slice 4 is a partial fix by design** | A token with an expiry is still an unauthenticated bearer credential | Record the residual risk in [change.log.md](../change.log.md) and point at `GAP-06` |
| **Fixing `DR-09` without `DR-13`** | The dev profile keeps the insecure default; the finding looks closed and is not | They are one slice. A `grep` for the literal must return nothing before the slice closes |

---

## Exit checklist

- [ ] Every one of the 13 open `DR-*` items is `VERIFIED` or has a written acceptance in [change.log.md](../change.log.md)
- [ ] Backend test count above 60 files
- [ ] Frontend spec count above 0 — `api.service`, auth guards, four forms-package components
- [ ] A failing-without-the-fix test exists for every drift item fixed (CLAUDE.md §9)
- [ ] Characterisation tests cover `F41-03`…`F41-11` — the nine items promoted to `VERIFIED` on code-reading alone
- [ ] `grep` for `ChangeThisStrongPassword` returns nothing
- [ ] `grep` for `@CrossOrigin` returns nothing
- [ ] `grep` for `Pageable` returns a non-zero count
- [ ] `deploy-verify` reports a healthy stack after each merge
- [ ] [status dashboard.md](../status%20dashboard.md) regenerated from code via [`status-sync`](../skills/status-sync/SKILL.md)
- [ ] Guardrail metrics instrumented — for this phase, that is test count and coverage trend, recorded in [`../qa/`](../qa/)

---

## Note on what "done" means here

Phase 0.5 closes when the findings are fixed **and demonstrated**. A drift item marked `VERIFIED`
because someone edited a file and the build passed is exactly the failure mode that created this
phase: eleven items were signed off as complete in June and were still open in August.

Each fix ships with the test that fails without it. That test is the evidence — not the diff.
