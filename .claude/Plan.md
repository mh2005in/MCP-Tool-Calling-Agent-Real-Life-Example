# Delivery Plan

> **The order in which [Requirements.md](Requirements.md) gets built, and why that order.**
> *What* to build is in Requirements.md. *How* work is executed is in [Delivery approach.md](Delivery%20approach.md).
> *Where things stand* is in [status dashboard.md](status%20dashboard.md).

**Last updated:** 2026-08-21
**Horizon:** 18 months, five phases, anchored on the Phase 5 roadmap (§6) with a Phase 0.5 inserted for drift.
**Phase and slice breakdowns:** [`plan/`](plan/) — `phase-<n>-<slug>.md`, `slice-<REQ-ID>.md`

---

## 1. Sequencing principle

Phase 5 §6 states the constraint plainly:

> Form automation has the greatest strategic value, but it depends on canonical data, governed
> content, secure document generation, and audit controls. Discovery and prototypes should begin in
> Phase 0 while commercial workflow features are delivered in parallel.

This plan honours that with one adjustment: **Section 4.1 is already at M5/M6**, well ahead of where
the 18-month roadmap assumes. So form automation is *finished* early rather than started late, and
the freed capacity goes to the foundations it was supposed to wait for.

Three rules govern the ordering:

1. **Drift before features.** An audit finding that is still open is cheaper to fix now than after ten more features are built on top of it. Phase 0.5 exists for exactly this.
2. **Structural before additive.** The outbox and the tenant boundary get harder to retrofit with every entity added. They precede the domains that depend on them.
3. **Nothing ships without its guardrail.** Phase 5 §7: every automation metric pairs with a quality or safety measure. A phase does not exit until both are instrumented.

---

## 2. Phase map

| Phase | Window | Outcome | Requirements | Exit criteria |
| --- | --- | --- | --- | --- |
| **0.5 Drift** | Weeks 1–4 | The code matches its own audits | `DR-01`…`DR-11` | All 11 drift items closed or explicitly accepted; backend test count above 60 files |
| **0 Foundation** | Months 1–3 | Safe commercial baseline | `SEC-01`…`SEC-16`, `GAP-14`, outbox, tenant design | Isolation and restore tests pass; critical events audited; architecture decisions approved |
| **1 Finish 4.1** | Months 2–5 | Form automation is genuinely done | `F41-01`…`F41-13`, `GAP-09` | Selected programs produce repeatable, version-linked, consultant-approved packages with measured error rates |
| **2 Retain & collect** | Months 4–8 | Complete lead-to-retainer workflow | `GAP-03`, `GAP-02`, `GAP-04`, `GAP-11`, `GAP-06` (shell) | A firm can book, retain, invoice, collect payment, and onboard a client with **no duplicate entry** |
| **3 Prepare & collaborate** | Months 7–12 | Primary workspace for active matters | `GAP-08`, `GAP-06` (full), `GAP-07`, `GAP-05` | Staff and clients complete most preparation work without parallel spreadsheets, inbox searches, or shared drives |
| **4 Scale & intelligence** | Months 11–18 | Commercial multi-firm platform | `GAP-13`, `GAP-10`, `GAP-12`, `GAP-15`, `GAP-16`, `GAP-17` | Multiple unrelated firms operate with tested isolation, exportability, support processes, and unit economics |

Phases overlap deliberately — Phase 1 runs alongside Phase 0 because Section 4.1 work is largely
independent of the security and event foundations.

---

## 3. Phase 0.5 — Drift (weeks 1–4)

**Why first.** Eleven findings from audits that were already signed off as complete. Two are High
severity with PII exposure (`DR-08`) or broken routing (`DR-04`). None require a design decision.
Clearing them costs days and removes the risk of building the next phase on a cracked base.

| Week | Work | Requirements | Agent / skill |
| --- | --- | --- | --- |
| 1 | Routing + config hygiene: fix the three `/api/api/…` controllers; move `show-sql` to a dev-only profile; remove the insecure DB-password fallback; consolidate CORS to the global config | `DR-04`, `DR-06`, `DR-08`, `DR-09` | `backend-feature`, `security-reviewer` |
| 1 | Remove `@Column(length = 15)` from the three number columns; confirm the generated-column definitions still hold | `DR-03` | `db-migration`, `backend-feature` |
| 2 | Party-portal token expiry, revocation, rotation; mask the token in responses | `DR-05` | `backend-feature`, `security-reviewer` |
| 2 | Pagination on every list and search endpoint, backend and frontend | `DR-07` | `backend-feature`, `frontend-feature` |
| 3 | Seed the missing PNP checklist templates; decide and document the `TriggerQuestion` deviation | `DR-02`, `DR-01` | `db-migration`, `requirements-analyst` |
| 3–4 | **Test foundation** — the Section 4.1 §9 test plan plus regression coverage for every `BL-*` claim. This is the largest item in the phase | `DR-10` | `test-author` |

**Exit criteria**

- All 11 `DR-*` items are `VERIFIED` or carry a written acceptance in [change.log.md](change.log.md).
- Backend test count exceeds 60 files, including a failing-without-the-fix test for every drift item (CLAUDE.md §9).
- `deploy-verify` reports a healthy stack after each merge.

**Risk.** `DR-10` (tests) is genuinely large and will want to slip. It must not — every later phase
assumes a suite that can catch regressions. If capacity is short, cut `DR-02` and `DR-01` instead.

---

## 4. Phase 0 — Foundation (months 1–3)

**Goal:** a baseline safe enough to put regulated data in front of a paying firm.

### 4.1 Security hardening — the plan's own first wave

Phase-4's recommended opening, restated for Keycloak:

| Order | Work | Requirement |
| --- | --- | --- |
| 1 | MFA — Keycloak OTP required action, stricter for `admin` | `SEC-01` |
| 2 | Secrets out of `.env` into a secret store; rotation policy | `SEC-04` |
| 3 | Rate limiting on auth-sensitive and write endpoints; throttle repeated 401/403 | `SEC-06` |
| 4 | Telemetry and alerting — auth-failure spikes, 403s from disabled users, OBO failures, latency | `SEC-14` |

Then the remainder: `SEC-02` step-up auth, `SEC-03` refresh handling, `SEC-05` branding, `SEC-08`
document authorization + malware scanning + signed URLs, `SEC-09` security audit events, `SEC-10`
headers and HTTPS, `SEC-11`–`SEC-13` MCP hardening, `SEC-15` CI scanning, `SEC-16` revocation runbook.

### 4.2 Structural foundations

| Work | Why now | Requirement |
| --- | --- | --- |
| **Transactional outbox + versioned domain events**, idempotent consumers, retries, dead-letter | Five later domains depend on reliable "when X, do Y". Building it after the fifth integration means rewriting five | `GAP-12` (foundation) |
| **Tenant model designed, structurally unavoidable in repositories and storage** — enforced single-tenant | Retrofitting a tenant boundary across 22 entities costs multiples of designing it in | `GAP-13`, `SEC-07` |
| **Backup, point-in-time recovery, and a rehearsed restore** | Phase 5 treats restore evidence as a release gate, not an ops nicety | `GAP-14` |
| **Canonical data model extended** — address history, family, organization, provenance | Unblocks `ADDRESS_GAP_DETECTED` and cross-form validation | `GAP-01` §5.4 |

### 4.3 Decisions to close in this phase

`D-1` (XFA engine), `D-2` (deployment model), `D-3` (first programs), `D-4` (multi-DB). Each blocks
downstream scope; all four should be answered before Phase 2 starts.

**Exit criteria:** isolation and restore tests pass; critical events audited; the first 3–5 programs
selected; architecture decisions recorded in [change.log.md](change.log.md).

---

## 5. Phase 1 — Finish Section 4.1 (months 2–5)

**Runs in parallel with Phase 0.** The dependency between them is weak; the teams are different.

| Stage | Work | Requirements |
| --- | --- | --- |
| 1.1 | **Inspect and classify.** Every candidate form triaged; `supportsFill`, `supportsBarcode`, `status`, `sourceSha256`, technology classification persisted. Dynamic-XFA and barcode forms marked `BLOCKED` | `F41-01` |
| 1.2 | **AcroForm quick win.** Enable fill for forms that inspect as standard or static AcroForm — target IMM 5476, 5475, 5708, 5709, 5710 — each verified by a round-trip fixture | `F41-01` |
| 1.3 | **Data-sheet fallback.** Mapped-values sheet for `BLOCKED` forms, so the workflow stays useful where filling is impossible | `F41-02` |
| 1.4 | **M5 completion.** Persist validation issues, required-form approval gate, manual-upload noise suppression, index/manifest assembly, package zip, gated approval, issue resolution, secured download | `F41-03`…`F41-10` |
| 1.5 | **M6 UI.** Form-catalogue status surfacing, admin editors, tabbed `FormsPackageWorkspaceComponent` | `F41-11`…`F41-13` |
| 1.6 | **Content governance.** Source registry, effective dating, draft→review→approve→publish→retire with separation of duties, impact analysis, rollback, regression packs | `GAP-09` |

**The correctness gate that overrides schedule:** no dynamic-XFA form is ever marked fillable, and
generation never emits a blank-but-"successful" PDF. Phase 5 §4.1 calls this a trust failure, not a
missing feature. **`F41-01` ships before `F41-14` is even considered.**

`F41-14` (automated XFA autofill) stays `BLOCKED` pending decision `D-1`. It is not scheduled.

**Exit criteria:** selected programs produce repeatable, version-linked, consultant-approved packages;
mapping-defect rate measured and under 1%; 100% of outputs linked to a form version and approver.

---

## 6. Phase 2 — Retain & collect (months 4–8)

**Goal:** close the commercial workflow. A firm cannot switch to this platform while money and
signatures live elsewhere.

| Order | Domain | Rationale | Requirement |
| --- | --- | --- | --- |
| 1 | **E-signatures** | Smallest P0, unblocks lead→client conversion, and its webhook is the first real outbox consumer | `GAP-03` |
| 2 | **Billing and payments** | Integrate the processor; build fee plans, invoices, ledger views, and role-separated controls. **Trust accounting deferred** pending `D-6` | `GAP-02` |
| 3 | **Calendar and deadlines** | Deadlines as *controlled records* carrying source, calculation inputs, owner, reviewer, and audit history — not ordinary calendar events | `GAP-04` |
| 4 | **Lead CRM** | Win on zero-rekey conversion, not marketing automation | `GAP-11` |
| 5 | **Portal shell** | Authenticated account, multilingual scaffold; full portal lands in Phase 3 | `GAP-06` |

**Exit criteria:** a firm can book, retain, invoice, collect payment, and onboard a client **without
duplicate entry**. That single sentence is the test.

---

## 7. Phase 3 — Prepare & collaborate (months 7–12)

**Goal:** the platform becomes where the work actually happens.

| Order | Domain | Note | Requirement |
| --- | --- | --- | --- |
| 1 | **Tasks and workflow** | Start with opinionated templates, **not** a visual no-code builder. Instrument usage, then expose only what customers repeatedly need | `GAP-08` |
| 2 | **Full client portal** | Authenticated access for sensitive data; keep the token flow only for narrowly scoped uploads. Fixes `DR-05` permanently | `GAP-06` |
| 3 | **Document management and OCR** | Immutable originals separate from derived previews, OCR text, redactions, and packages. **Never overwrite the evidentiary original** | `GAP-07` |
| 4 | **Unified communications** | Provider adapters kept separate from message records so vendors can change without losing history | `GAP-05` |

**Exit criteria:** staff and clients complete most preparation work without parallel spreadsheets,
inbox searches, or shared drives.

---

## 8. Phase 4 — Scale & intelligence (months 11–18)

**Goal:** multiple unrelated firms, safely.

| Order | Domain | Requirement |
| --- | --- | --- |
| 1 | **Multi-tenant SaaS and firm administration** — enforce the boundary designed in Phase 0; automated cross-tenant tests **before** onboarding unrelated firms | `GAP-13` |
| 2 | **Reporting and exports** — define a governed metric dictionary before building charts; use a reporting replica | `GAP-10` |
| 3 | **Integrations and webhooks** — signed webhooks, API credentials, health dashboard, on the Phase 0 outbox | `GAP-12` |
| 4 | **Accessibility and localization** — WCAG 2.2 AA, English/French baseline, mobile-first | `GAP-15` |
| 5 | **AI governance** — use-case registry, versioned prompts/models, evaluation suites, grounded citations, cost controls | `GAP-16` |
| 6 | **Onboarding and migration** — treat migration as product; reversible pilot on copied data before any cutover | `GAP-17` |

**Exit criteria:** multiple unrelated firms operate with tested isolation, exportability, support
processes, and unit economics.

---

## 9. Critical path

```
DR-10 (tests) ──► everything (no safe refactor without a suite)
        │
        ├─► SEC-01/04/06/14 ──► GAP-14 ──► any regulated-data release
        │
        ├─► outbox ──► GAP-03 ──► GAP-02 ──► GAP-04 ──► GAP-05 ──► GAP-08 ──► GAP-12
        │
        ├─► tenant design ──► GAP-13 ──► multi-firm commercialization
        │
        └─► F41-01 ──► F41-02 ──► F41-03…F41-10 ──► F41-11…F41-13 ──► GAP-01 complete
                 └─► D-1 ──► F41-14 (BLOCKED, unscheduled)
```

**Three things gate the most downstream work:** the test suite (`DR-10`), the outbox (`GAP-12`
foundation), and the tenant boundary (`GAP-13`). All three are Phase 0/0.5. That is not an accident.

---

## 10. Risks

Phase 5 §8, plus what the code audit added.

| Risk | Impact | Mitigation | Owner phase |
| --- | --- | --- | --- |
| **Silent wrong PDF output** — imperfect XFA detection produces a blank "successful" form | Correctness and trust failure; a consultant files a blank form | Strict classification + fill→reopen verification; `BLOCKED` by default | 1 |
| **Building on 6 tests** | Every refactor is unverifiable | `DR-10` is Phase 0.5 and non-negotiable | 0.5 |
| **Outdated forms or rules** | Incorrect packages, loss of trust | Effective-dated content, authoritative sources, two-person review, impact analysis, regression fixtures | 1 |
| **Over-automating legal judgment** | Professional and regulatory harm | **BR-1/BR-2** — human approval, grounded explanations, explicit limitations, no autonomous filing or advice | all |
| **Weak tenant isolation** | Severe privacy incident | Structural scoping designed in Phase 0, enforced and tested in Phase 4 | 0, 4 |
| **Integration fragility** | Missed messages, payments, signatures | Outbox, idempotency, retries, dead-letter, health dashboard, reconciliation | 0, 2 |
| **Trying to match every incumbent** | Diffused roadmap, slow differentiation | Anchor on the golden path: booked consultation → signed retainer → paid invoice → portal intake → documents → generated form → readiness report → consultant approval | all |
| **XFA licensing never decided** | `F41-14` blocks indefinitely; expectations drift | Ship `F41-02` as the permanent answer unless `D-1` resolves otherwise | 1 |
| **Multi-DB dialects rot further** | Every schema change taxes four dialects or silently skips three | Close `D-4` in Phase 0 | 0 |

---

## 11. First 90 days

Phase 5's own recommendation, reconciled with the code audit's findings.

1. **Clear the drift** (`DR-01`…`DR-11`) — nothing else starts on a cracked base.
2. **Build the test foundation** (`DR-10`) — the Section 4.1 §9 plan plus regression cover for every `BL-*` claim.
3. **Interview 8–12 target firms** across solo, small, and mid-sized practices; quantify volume, programs, current tools, rekeying time, error sources, payment flow, and migration constraints.
4. **Select the first 3–5 programs** (`D-3`) by case volume, form stability, willingness to pay, and workflow differentiation — *not* technical ease.
5. **Ship the security first wave** — `SEC-01`, `SEC-04`, `SEC-06`, `SEC-14`.
6. **Land the outbox and the tenant design** — the two structural moves that get more expensive every month.
7. **Prototype the golden path end to end**: booked consultation → signed retainer → paid invoice → portal intake → documents → generated form → readiness report → consultant approval.
8. **Choose vendors** for signature, payment, calendar, communication, storage, OCR, and malware using a documented security and data-residency scorecard.

---

## 12. How this plan is maintained

- Every requirement in [Requirements.md](Requirements.md) appears in exactly one phase here. The `status-sync` skill verifies it.
- A phase exits only when its exit criteria are met **and** its guardrail metrics are instrumented — not when its tasks are closed.
- Re-sequencing is a decision: record it in [change.log.md](change.log.md) with the reason.
- Capture baseline metrics **before** launching each feature (Phase 5 §7), or the success measures are unfalsifiable.
