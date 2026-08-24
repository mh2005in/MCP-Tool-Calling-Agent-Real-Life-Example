# Status Dashboard

> **Where every requirement stands, as of the last verification pass.**
> Definitions live in [Requirements.md](Requirements.md); schedule in [Plan.md](Plan.md).
> Regenerate with the **[`status-sync`](skills/status-sync/SKILL.md)** skill.

**Last verified:** 2026-08-24 — by direct code inspection, not by reading the requirement documents.
**Verification method:** entity/service/controller enumeration, targeted `grep` against each documented claim, migration and seed inspection, endpoint and component tracing, test-file count.
**Full run record:** [`progress/2026-08-24-verification-run.md`](progress/2026-08-24-verification-run.md)
**Code delta since the prior run:** **none** — the only commits are documentation. Every change below is a correction to how the code was read, not a change to the code.

---

## 1. Headline

| | |
| --- | --- |
| **Baseline health** | 🟢 **Strong.** All four P0 validation defects and all five Critical security findings from the signed-off audits are genuinely fixed in code. |
| **Biggest correction this run** | 🔵 **Section 4.1 Milestone 5 is complete, not outstanding.** Nine items previously recorded `OPEN` are delivered. Package assembly, the approval gate, the zip, issue resolution, and secured download all exist. |
| **Biggest exposure** | 🔴 **Test coverage — 6 backend/MCP files and 0 frontend files** for 37 services, 17 controllers, and 27 components. Every refactor from here is unverifiable, and the nine newly-confirmed `F41` items have no test proving they behave. |
| **Nearest value** | 🟡 **Section 4.1 needs five items, not thirteen** — `F41-01` classification, `F41-02` fallback, `F41-12` admin editors, `F41-13` workspace shell, and `GAP-09` governance. |
| **Hard blocker** | ⛔ **`F41-14`** — filling real IRCC forms is proven impossible with PDFBox. Needs a licensing decision (`D-1`), not engineering. |
| **Open decisions** | **5 open of 6** — `D-1`…`D-4`, `D-6`. `D-5` is decided. |

---

## 2. Portfolio at a glance

| Group | Total | ✅ Verified | 🟡 Partial | ⬜ Open | ⛔ Blocked | ⏭ Superseded |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `BL-*` Baseline | 12 | 11 | 1 | — | — | — |
| `DR-*` Drift | 15 | — | — | 13 | — | 2 |
| `F41-*` Section 4.1 | 14 | 9 | 2 | 2 | 1 | — |
| `SEC-*` Phase-4 hardening | 16 | — | 5 | 11 | — | — |
| `GAP-*` Phase 5 domains | 17 | — | 7 | 10 | — | — |
| **Total** | **74** | **20** | **15** | **36** | **1** | **2** |

```
Verified  ████████████                               20  (27%)
Partial   █████████                                  15  (20%)
Open      ██████████████████████                     36  (49%)
Blocked   ▌                                           1   (1%)
Superseded█                                           2   (3%)
```

### Movement since 2026-08-21

| | 2026-08-21 | 2026-08-24 | Cause |
| --- | ---: | ---: | --- |
| Total tracked | 70 | **74** | `DR-12`…`DR-15` added |
| Verified | 10 | **20** | `F41-03`…`F41-11` (9) + `BL-03` promoted |
| Partial | 13 | **15** | `F41-01`, `F41-13` reclassified from `OPEN`; `GAP` recount corrected |
| Open | 45 | **36** | 9 `F41` items were never open; 4 new `DR` items added; `DR-01` closed |
| Superseded | 1 | **2** | `DR-01` closed as an accepted deviation |

**No work was done between the two runs.** The delta is entirely the difference between trusting a
backlog document and reading `CasePackageService`.

---

## 3. Baseline (`BL-*`) — 11 verified, 1 partial

| ID | Capability | Status | Evidence found |
| --- | --- | --- | --- |
| `BL-01` | Client / case / consultant management | ✅ `VERIFIED` | `Client`, `Consultant`, `ImmigrationCase` entities; `LeadStatus`, `CaseStatus` |
| `BL-02` | 11 application types + subtype + applicant role | ✅ `VERIFIED` | `ServiceType` — all 11 (`PGWP`, `SUPER_VISA`, `PR_CARD_PRTD`, `PNP` present) + `OTHER`; `CaseSubtype`, `ApplicantRole` |
| `BL-03` | Rules-based checklist generation | ✅ `VERIFIED` | **Promoted from `PARTIAL`.** `ChecklistGeneratorService` :46/:111 evaluates rules by trigger key; `ConditionalRule.triggerQuestionKey`, `IntakeQuestionTemplate.isTriggerQuestion`, both repository finders, both seeders. `DR-01` closed |
| `BL-04` | Intake templates for all 11 types | ✅ `VERIFIED` | `IntakeQuestionSeeder` references every service type |
| `BL-05` | Seeded checklist templates | 🟡 `PARTIAL` | `V2__seed_data.sql` covers all types, but PNP has 3 references against 29–61 elsewhere → `DR-02` |
| `BL-06` | Template governance: source URL, reviewer, version, approval | ✅ `VERIFIED` | All 7 fields present on `ChecklistTemplate` |
| `BL-07` | AI boundary + masking + sanitisation | ✅ `VERIFIED` | `AiBoundaryService`, `DataMaskingService`, `DocumentMetadataSanitizer`, `SensitiveFieldRegistry` |
| `BL-08` | MCP tools, scoped and audited | ✅ `VERIFIED` | `McpApiController`, `McpDataService`, `McpToolAuditService`; 14 tools in `tools.json`; 11 MCP classes |
| `BL-09` | Canadian workflow modules | ✅ `VERIFIED` | Travel, work, relationship-timeline, recruitment, candidate-comparison services and entities |
| `BL-10` | Compliance calculators corrected | ✅ `VERIFIED` | `MIN_PR_DAYS=730`, `PRE_PR_CAP_DAYS=365`, `CONTINUOUS_DAYS_THRESHOLD=183`, `MINIMUM_AGE=18`, `preliminaryReviewStatus` |
| `BL-11` | Server-authoritative intake validation | ✅ `VERIFIED` | `IntakeService` resolves templates by key, rejects unknown keys, enforces required |
| `BL-12` | OAuth2 auth + authorization + revocation | ✅ `VERIFIED` | Resource server, `/v1/**` authenticated, `ConsultantAccessService`, `AdminAccessService`, `DisabledUserFilter`, 26 × `@PreAuthorize` |

> **Caveat that applies to every ✅ above.** These are code-verified, not behaviour-verified. Two
> backend tests exist. `BL-07`'s masking boundary and `BL-10`'s calculators are exactly the kind of
> logic that fails silently, and neither has a test.

---

## 4. Drift (`DR-*`) — 13 open 🔴

Ordered by severity. **This is Phase 0.5** and the cheapest work in the portfolio.
Breakdown: [`plan/phase-0.5-drift.md`](plan/phase-0.5-drift.md).

| ID | Finding | Sev | Status | Where |
| --- | --- | --- | --- | --- |
| `DR-04` | `AutomationController` :16, `PartyPortalController` :14, `WorkflowController` :17 map `/api` under context-path `/api` → `/api/api/…`, bypassing the `/v1/**` matcher | 🔴 High | ⬜ `OPEN` | 3 controllers — the backlog listed one |
| `DR-05` | Party-portal `accessToken` never expires and cannot be revoked | 🔴 High | ⬜ `OPEN` | `entity/PartyProfile.java` :34 |
| `DR-08` | `show-sql=true` + `format_sql=true` in the **main** profile — bound PII in logs everywhere | 🔴 High | ⬜ `OPEN` | `application.properties` :21-22 |
| `DR-10` | **6 test files** (2 backend, 4 MCP) for 37 services and 17 controllers; Section 4.1 §9 test plan unwritten | 🔴 High | ⬜ `OPEN` | `backend/src/test`, `MCPServer/src/test` |
| `DR-14` | **0 frontend test files** for 27 components and a 121-call API service | 🔴 High | ⬜ `OPEN` | `frontend/src` — **new 2026-08-24** |
| `DR-02` | PNP checklist templates near-empty — 3 seed references | 🟠 Med | ⬜ `OPEN` | `V2__seed_data.sql` |
| `DR-03` | `length = 15` on 3 number columns — violates CLAUDE.md §7 | 🟠 Med | ⬜ `OPEN` | `Client` :25, `Consultant` :24, `ImmigrationCase` :26 |
| `DR-06` | CORS defined twice — global config **and** 9 × `@CrossOrigin` | 🟠 Med | ⬜ `OPEN` | `CorsConfig` + 9 controllers |
| `DR-07` | Zero `Pageable` — no pagination on any list or search endpoint | 🟠 Med | ⬜ `OPEN` | backend-wide |
| `DR-09` | Insecure DB-password fallback active when `DB_PASSWORD` is unset | 🟠 Med | ⬜ `OPEN` | `application.properties` :15 |
| `DR-13` | The same fallback is **also** in the dev profile — fixing `DR-09` alone leaves it live | 🟠 Med | ⬜ `OPEN` | `application-dev.properties` :5 — **new 2026-08-24** |
| `DR-12` | Stale comment documents per-consultant MCP API keys that `V5` dropped | 🟡 Low | ⬜ `OPEN` | `application.properties` :68-70 — **new 2026-08-24** |
| `DR-15` | Route comment claims `FormsPackageWorkspaceComponent` replaced the route; it does not exist | 🟡 Low | ⬜ `OPEN` | `app.routes.ts` — **new 2026-08-24** |
| `DR-01` | `TriggerQuestion` entity specified but never built | — | ⏭ `SUPERSEDED` | Capability delivered as a flag + rule key — [ADR-003](design/ADR-003-trigger-question-as-flag.md) |
| `DR-11` | Entra docs and runbooks contradict the Keycloak stack | — | ⏭ `SUPERSEDED` | Resolved by `D-5` — [ADR-001](design/ADR-001-keycloak-over-entra.md) |

---

## 5. Section 4.1 — form & package automation (`F41-*`)

**Milestones:** M1 ✅ · M2 ✅ · M3 ✅ · M4 ✅ · manual upload ✅ · **M5 ✅ complete** · M6 🟡 catalogue done, editors open

### 5.1 Delivered — 9 items ✅

Each was recorded `OPEN` on 2026-08-21. Each is in the code.

| ID | Item | Evidence |
| --- | --- | --- |
| `F41-03` | Persist `PackageValidationIssue` rows | `CasePackageService.persistIssues()` :249; `buildReadinessFromPersisted()` :351 |
| `F41-04` | `REQUIRED_FORM_NOT_PROVIDED` approval gate | `PackageValidationService` :114 |
| `F41-05` | Suppress validation noise for manually uploaded forms | `PackageValidationService` :207 |
| `F41-06` | Index/manifest across generated + uploaded drafts | `buildIndex()` :204, `currentDrafts()` :337, `manifestEntry()` :440 |
| `F41-07` | Package zip with size enforcement | `buildZip()` :272 + `app.forms.max-generated-package-bytes` |
| `F41-08` | Gated `approvePackage` + acknowledgement + audit | `approvePackage()` :157 |
| `F41-09` | Issue-resolution endpoints and UI | `resolveIssue()` :135; `package-approval.component.ts` :70 |
| `F41-10` | Secured package download | `getPackageForDownload()` :189, guarded by `canAccessCase` |
| `F41-11` | Catalogue UI: fillable vs manual, XFA/blocked | `forms-catalogue.component.ts` :95-103, routed behind `adminConsultantGuard` |

### 5.2 Outstanding — 5 items

| ID | Item | Status | What is actually missing |
| --- | --- | --- | --- |
| `F41-01` | Inspect + classify forms | 🟡 `PARTIAL` | Fields and `hasXfa` detection exist. **No four-way technology enum** (`STANDARD_ACROFORM`/`STATIC_XFA`/`DYNAMIC_XFA`/`NONE`); `supportsBarcode` is never set. → [`requirements/F41-01.md`](requirements/F41-01.md) |
| `F41-13` | `FormsPackageWorkspaceComponent` | 🟡 `PARTIAL` | Functionality reachable — `MappingReviewComponent` embeds readiness + approval. Missing the tabbed shell, the case-detail entry point, and the status card. → [`requirements/F41-13.md`](requirements/F41-13.md) |
| `F41-02` | Data-sheet fallback for `BLOCKED` forms | ⬜ `OPEN` | Nothing built. The zero-cost path, and permanent if `D-1` resolves against licensing |
| `F41-12` | Admin editors (mapping version, package profile, create form) | ⬜ `OPEN` | **Backends exist.** This is Angular-only work |
| `F41-14` | Automated XFA autofill | ⛔ `BLOCKED` | Needs `D-1` |

> **⛔ The `F41-14` blocker, stated plainly.** The 2026-06-29 PoC injected values into IMM 5257's
> `xfa:data` successfully and verified them headlessly — then both outputs failed to open in Adobe.
> IRCC forms are encrypted, certified (DocMDP), and Reader-Extended; PDFBox has open defects writing
> encrypted incremental updates, and a full save breaks certification. **This is a procurement
> question (iText 7 commercial / Aspose.PDF / Qoppa / Adobe AEM), not an engineering one.**
> — [ADR-002](design/ADR-002-xfa-engine-deferred.md), [`memory/pdfbox-cannot-save-ircc-forms.md`](memory/pdfbox-cannot-save-ircc-forms.md)

### 5.3 The correctness gate that overrides schedule

`F41-01` is not a feature; it is the guard that stops a blank-but-"successful" PDF reaching a
consultant. `PdfBoxFormEngine` detects `hasXfa` today, but a static-XFA form and a dynamic-XFA form
are treated identically, and barcode forms are not detected at all. **Until the classification is
four-way and barcode-aware, "fillable" is a guess.**

---

## 6. Phase-4 hardening (`SEC-*`) — 0 verified, 5 partial, 11 open

| ID | Control | Status | Evidence |
| --- | --- | --- | --- |
| `SEC-01` | MFA | ⬜ `OPEN` | — |
| `SEC-02` | Step-up / re-auth on sensitive actions | ⬜ `OPEN` | — |
| `SEC-03` | Refresh-token handling | ⬜ `OPEN` | — |
| `SEC-04` | Secrets to a vault + rotation policy | ⬜ `OPEN` | `.env` today; see `DR-09`, `DR-13` |
| `SEC-05` | Consent and branding review | ⬜ `OPEN` | anonymous DCR is a dev posture |
| `SEC-06` | Rate limiting | ⬜ `OPEN` | **0 implementations**; no bucket4j / resilience4j dependency |
| `SEC-07` | Tenant-level authorization | ⬜ `OPEN` | consultant scoping only |
| `SEC-08` | Document authz + signed URLs + malware scan | 🟡 `PARTIAL` | size limit + partial magic bytes; **0 AV implementations** |
| `SEC-09` | Security audit event coverage | 🟡 `PARTIAL` | 18 `logAudit` sites, all domain events; no security events |
| `SEC-10` | CORS restriction + security headers + HTTPS | 🟡 `PARTIAL` | origins env-driven; **no HSTS, no CSP** in `SecurityConfig`; CSRF disabled :64; see `DR-06` |
| `SEC-11` | MCP transport context extractor | ⬜ `OPEN` | verify under load |
| `SEC-12` | MCP tool rate limits and quotas | 🟡 `PARTIAL` | audited, not throttled |
| `SEC-13` | Private networking / mTLS for MCP→backend | ⬜ `OPEN` | internal-only by convention, not enforced |
| `SEC-14` | Telemetry, dashboards, alerts | ⬜ `OPEN` | — |
| `SEC-15` | Dependency and code scanning in CI | 🟡 `PARTIAL` | gitleaks pre-commit only; **no CI pipeline** |
| `SEC-16` | Revocation runbook | ⬜ `OPEN` | Entra runbooks superseded, nothing replaces them |

**Recommended first wave** (the plan's own): `SEC-01` · `SEC-04` · `SEC-06` · `SEC-14`.

---

## 7. Phase 5 product domains (`GAP-*`) — 0 verified, 7 partial, 10 open

| ID | Domain | Pri | Status | What exists today |
| --- | --- | --- | --- | --- |
| `GAP-01` | IRCC form & package automation | P0 | 🟡 `PARTIAL` | **M1–M5 done, M6 partial** → `F41-*`. The most advanced domain in the portfolio |
| `GAP-02` | Billing, payments, trust accounting | P0 | ⬜ `OPEN` | no invoice/payment/ledger entity |
| `GAP-03` | E-signatures & retainer automation | P0 | ⬜ `OPEN` | retainer date + document path only |
| `GAP-04` | Calendar, booking, deadlines | P1 | ⬜ `OPEN` | case deadline + `Reminder` only |
| `GAP-05` | Unified communications | P1 | ⬜ `OPEN` | reminder drafting + SMTP config |
| `GAP-06` | Secure client & third-party portal | P1 | 🟡 `PARTIAL` | unauthenticated token portal + client checklist view; see `DR-05` |
| `GAP-07` | Document management, OCR, evidence ops | P1 | 🟡 `PARTIAL` | upload, classify, expiry; no OCR/versions/AV |
| `GAP-08` | Tasks, workflow, collaboration | P1 | ⬜ `OPEN` | checklist items + reminders only |
| `GAP-09` | Rules, forms & knowledge governance | P1 | 🟡 `PARTIAL` | `BL-06` checklist governance + `FormMappingVersion` lifecycle; no two-person publication, impact analysis, or rollback |
| `GAP-10` | Reporting, analytics, exports | P2 | ⬜ `OPEN` | operational dashboards only |
| `GAP-11` | Lead CRM & conversion | P2 | 🟡 `PARTIAL` | `LeadStatus` + case-pipeline component |
| `GAP-12` | Integrations, APIs, webhooks | P2 | ⬜ `OPEN` | app + MCP APIs; no outbox/webhooks |
| `GAP-13` | Multi-tenant SaaS & firm admin | P2 | ⬜ `OPEN` | org dashboards + consultant scoping |
| `GAP-14` | Security, privacy, resilience | P0 | 🟡 `PARTIAL` | → `SEC-*` |
| `GAP-15` | Accessibility, localization, mobile | P2 | ⬜ `OPEN` | — |
| `GAP-16` | AI governance & advanced assistance | P2 | 🟡 `PARTIAL` | `BL-07` boundary; no prompt/model versioning or evals |
| `GAP-17` | Onboarding, migration, support | P3 | ⬜ `OPEN` | — |

---

## 8. Codebase inventory

Counted 2026-08-24. **A count of zero is a finding.**

| Metric | Count | Δ vs 08-21 | Read |
| --- | ---: | :---: | --- |
| Backend entities | **32** | +10 | 21 core + 11 forms — healthy domain model |
| Backend controllers | 17 | — | 3 mis-routed (`DR-04`) |
| Backend services | **37** | +7 | 27 core + 8 forms + 2 security |
| Backend repositories | **31** | new | |
| MapStruct mappers | **17** | new | DTO↔entity per CLAUDE.md §4 |
| DTOs | **45** | new | |
| Enums | **17** | new | No `FormTechnology` — that gap is `F41-01` |
| PDF engine classes | 5 | — | `PdfFormEngine` SPI keeps the XFA escape hatch open |
| Angular components | **27** | +3 | 10 feature areas |
| Angular services | **2** | new | 121 HTTP calls through `api.service` |
| PostgreSQL migrations | V1–V9 | — | Current |
| MySQL / MSSQL / Oracle | V1–V2 | — | **Frozen** — decision `D-4` |
| MCP server classes / tools | 11 / 14 | new | |
| `@PreAuthorize` usages | **26** | +4 | Authorization present |
| `@Valid` usages | 22 | — | Input validation present |
| `logAudit` call sites | 18 | — | Audit wired; security events absent (`SEC-09`) |
| `Pageable` usages | **0** | — | 🔴 `DR-07` |
| Rate-limiting implementations | **0** | — | 🔴 `SEC-06` |
| Malware-scanning implementations | **0** | — | 🔴 `SEC-08` |
| Backend + MCP test files | **6** | — | 🔴 `DR-10` |
| **Frontend test files** | **0** | new | 🔴 `DR-14` — the single largest risk |

---

## 9. Open decisions blocking work

| # | Decision | Blocks | Needed by | Status |
| --- | --- | --- | --- | --- |
| `D-1` | License a commercial XFA engine, or make the data-sheet fallback permanent? | `F41-14` | Phase 1 | ⬜ Open |
| `D-2` | Single-firm, multi-tenant SaaS, or both? | `GAP-13`, `SEC-07` | Phase 0 | ⬜ Open |
| `D-3` | Which 3–5 programs are the first commercial target? | `GAP-01` scope, `GAP-09` load | Phase 0 | ⬜ Open |
| `D-4` | Revive multi-DB parity, or declare PostgreSQL-only? | every schema change | Phase 0 | ⬜ Open |
| `D-5` | Keycloak or Entra as production identity? | `SEC-01`…`SEC-05` | — | ✅ **Decided** — Keycloak |
| `D-6` | Which trust-accounting rules apply? | `GAP-02` scope | Phase 2 | ⬜ Open |

---

## 10. Next actions

1. **Build the test foundation** (`DR-10` + `DR-14`) — and note that this got *more* urgent, not less. Nine `F41` items just moved to `VERIFIED` on the strength of code-reading alone; no test proves any of them behave.
2. **Close the config-hygiene drift in one change** — `DR-08`, `DR-09`, `DR-13`, `DR-12` all live in two properties files. One slice.
3. **Fix `DR-04`** — three broken routes that also bypass the `/v1/**` security matcher.
4. **Fix `DR-05`** — permanent unauthenticated case access is the worst single exposure in the portfolio.
5. **Land `F41-01`** — the correctness gate. Section 4.1 is five items from done, and this is the one that protects trust.
6. **Answer `D-1`…`D-4`** — four decisions gate Phase 1 and Phase 2 scope.

---

## 11. How to refresh this dashboard

Run the **[`status-sync`](skills/status-sync/SKILL.md)** skill. It re-inspects the code, recounts the
inventory in §8, re-checks each `VERIFIED` claim, and reports any requirement missing from
[Plan.md](Plan.md) or [change.log.md](change.log.md).

**Do not update a status from a document.** Every ✅ in this file was confirmed by looking at code, and
that is the only thing that makes the dashboard worth reading.

**The 2026-08-24 run is the argument for that rule, in both directions.** The 2026-08-21 pass proved
documents overstate progress — it found 11 open items inside work marked complete. This pass proved
they also *understate* it: nine delivered `F41` items were sitting in the register as `OPEN` because
a backlog document listed them as remaining tasks and nobody opened `CasePackageService`. A status
copied from a document is wrong in whichever direction the document was wrong.
