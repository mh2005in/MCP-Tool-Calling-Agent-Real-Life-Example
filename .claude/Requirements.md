# Requirements Register

> **Single source of truth for *what* this product must do.**
> Companion documents: [Architecture.md](Architecture.md) (*how it is built*),
> [Plan.md](Plan.md) (*in what order*), [Delivery approach.md](Delivery%20approach.md) (*how work is executed*),
> [status dashboard.md](status%20dashboard.md) (*where each item stands*), [change.log.md](change.log.md) (*what changed*).

**Last updated:** 2026-08-24
**Last verified against code:** 2026-08-24 — [`progress/2026-08-24-verification-run.md`](progress/2026-08-24-verification-run.md)
**Per-requirement detail:** [`requirements/`](requirements/) — `<ID>.md` for anything too large for a register row
**Source corpus:** [`input/`](input/) — extracted, redacted, greppable (upstream: `C:\Users\mh200\Downloads\SoftwareForImmigrationConsultants\`)

| Source folder | Contents | Treatment here |
| --- | --- | --- |
| `HighLevelRequirement Completed/` | Stage 1 (domain), 2.0 (application-type deep dive), 2.1 (TODO 1–10), 2.2 (MCP/AI + access control), 2.3 (missing validation), 3.0 (security audit), 3.1 (Entra plan) | Baseline (`BL-*`) — re-verified against code; failures recorded as drift (`DR-*`) |
| `HighLevelRequirement Pending/` | Phase-4 Hardening Plan, Phase 5 Gap Analysis §4.1–4.17, Section 4.1 plan/backlog/XFA issue | Open requirements (`SEC-*`, `GAP-*`, `F41-*`) |
| `Runbooks/` | Entra auth config, MCP client pre-registration, MCP registration removal | Superseded by Keycloak — see `DR-11` and [ADR-001](design/ADR-001-keycloak-over-entra.md) |

---

## 0. Where the portfolio stands

**74 requirements. 20 delivered, 15 partial, 36 open, 1 blocked, 2 superseded.**

| Group | Meaning | Total | ✅ | 🟡 | ⬜ | ⛔ | ⏭ |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `BL-nn` | **Baseline** — delivered capability, re-verified against code | 12 | 11 | 1 | — | — | — |
| `DR-nn` | **Drift** — audit finding still open, or a rule the code violates | 15 | — | — | 13 | — | 2 |
| `F41-nn` | **Section 4.1** — IRCC form & package automation | 14 | 9 | 2 | 2 | 1 | — |
| `SEC-nn` | **Phase-4 hardening** — production-readiness pass (A1–D16) | 16 | — | 5 | 11 | — | — |
| `GAP-nn` | **Phase 5 gap analysis** — product domains §4.1–4.17 | 17 | — | 7 | 10 | — | — |
| | **Total** | **74** | **20** | **15** | **36** | **1** | **2** |

**Status vocabulary** (used identically in [status dashboard.md](status%20dashboard.md)):

| Status | Meaning | Bar for entering it |
| --- | --- | --- |
| ✅ `VERIFIED` | Implemented **and** confirmed present in code | A named file and symbol, read this verification run |
| 🟡 `PARTIAL` | Implemented in part | The residual work is **named**, not "mostly done" |
| ⬜ `OPEN` | Not started | — |
| ⛔ `BLOCKED` | Cannot proceed — external dependency or licensing decision | The blocker is a decision `D-n`, not a task |
| ⏭ `SUPERSEDED` | No longer applicable; a decision replaced it | The decision is recorded in [change.log.md](change.log.md) and an ADR |

> **`VERIFIED` means the code implements it, not that a test proves it.** With 6 backend/MCP tests and
> zero frontend tests (`DR-10`, `DR-14`), code-reading is the strongest evidence available. That is the
> reason the test foundation gates everything downstream rather than being scheduled around.

---

## 1. Product boundary (non-negotiable)

Derived from Stage 1 §1 and Stage 2.0 §1. **Every requirement below is subordinate to this.**

> The system may automate intake, document collection, tracking, reminders, summaries, and draft
> communications. It **must not** provide immigration advice, eligibility decisions, legal
> interpretation, or guarantee outcomes.

Enforcement rules that no requirement may weaken:

| Rule | Statement | Enforced today by |
| --- | --- | --- |
| **BR-1** | AI output is never presented as an eligibility or legal conclusion. Compliance calculators return *preliminary review* language, never "requirements met" | `AiBoundaryService`; `LmiaCalculatorService.preliminaryReviewStatus` (`BL-07`, `BL-10`) |
| **BR-2** | A licensed consultant approves any artefact before a client or IRCC sees it | `CasePackageService.approvePackage` acknowledgement gate (`F41-08`) |
| **BR-3** | Every checklist, rule, and form carries a source URL, version, and last-reviewed date | `ChecklistTemplate` governance fields (`BL-06`); `FormDefinition.sourceUrl`/`sourceSha256`/`editionLabel` |
| **BR-4** | Every PII read/write is audited with actor, subject, and timestamp | `CommonService.logAudit` — 18 call sites (`BL-12`); **coverage is incomplete** — `SEC-09` |
| **BR-5** | Client PII does not leave the tenant. No SaaS form-fill and no LLM egress of unmasked PII | Local Ollama tier; `DataMaskingService`; SaaS form-fill rejected in §7 |

**Two of the five are only partly enforced.** `BR-4` has no security-event coverage (`SEC-09`), and
`BR-5` holds for the LLM tier but rests on `DataMaskingService` having a complete
`SensitiveFieldRegistry` — which has never been tested (`DR-10`).

---

## 2. Delivered baseline (`BL-*`)

Capabilities the Completed corpus claimed, re-verified by inspecting code.

### 2.1 Verified

| ID | Requirement | Source | Evidence in code |
| --- | --- | --- | --- |
| `BL-01` | Consultant/client/case management with service types, lead and case status | 2.1 §10 | `entity/Client.java`, `Consultant.java`, `ImmigrationCase.java`; `LeadStatus`, `CaseStatus` |
| `BL-02` | 11 application types (adds PGWP, SUPER_VISA, PR_CARD_PRTD, PNP) plus subtype and applicant role | 2.1 §2.1–2.3 | `enums/ServiceType.java` — 11 + `OTHER`; `CaseSubtype`, `ApplicantRole` |
| `BL-03` | Rules-based checklist generation from conditional rules, driven by trigger questions | 2.1 §2.4–2.6 | `ChecklistGeneratorService` :46/:111, `ConditionalRule.triggerQuestionKey`, `IntakeQuestionTemplate.isTriggerQuestion`, `ConditionalRuleSeeder` — **promoted from `PARTIAL` 2026-08-24**, see `DR-01` / [ADR-003](design/ADR-003-trigger-question-as-flag.md) |
| `BL-04` | Structured intake templates per application type | 2.1 §3.1–3.13 | `config/IntakeQuestionSeeder.java` covers all 11 types; `IntakeQuestionTemplate` |
| `BL-06` | Governance fields on checklist templates: source URL, reviewer, rule version, approval gate | 2.1 §1.1–1.4 | `ChecklistTemplate` — `sourceUrl`, `lastReviewedDate`, `reviewedByConsultantId`, `ruleVersion`, `approvedForUse`, `approvedByConsultantId`, `approvedDate` |
| `BL-07` | AI boundary, masking, sanitisation, sensitive-field registry | 2.2 §A | `AiBoundaryService`, `DataMaskingService`, `DocumentMetadataSanitizer`, `SensitiveFieldRegistry` |
| `BL-08` | MCP tool surface with per-consultant scoping and audit | 2.2 §B | `McpApiController`, `McpDataService`, `McpToolAuditService`, 14 tools in `MCPServer/.../config/tools.json` |
| `BL-09` | Canadian workflow modules: travel, work, relationship timeline, recruitment evidence, candidate comparison | 2.1 §7 | `TravelHistoryService`, `WorkHistoryService`, `RelationshipTimelineService`, `RecruitmentService`, `entity/CandidateComparison.java` |
| `BL-10` | Compliance calculators corrected to IRCC rules, with preliminary-review language | 2.3 P0-1/3/4 | `TravelHistoryService` (`MIN_PR_DAYS=730`, `PRE_PR_CAP_DAYS=365`); `PoliceCertificateService` (`CONTINUOUS_DAYS_THRESHOLD=183`, `MINIMUM_AGE=18`, 10-year window); `LmiaCalculatorService.preliminaryReviewStatus` |
| `BL-11` | Server-authoritative intake validation against templates | 2.3 P0-2 | `IntakeService.submitIntake` resolves templates by key, rejects unknown keys, enforces required questions |
| `BL-12` | OAuth2 authentication with consultant/admin authorization and disabled-user cutoff | 3.0 Critical 1–5 | `SecurityConfig` (resource server, `/v1/**` authenticated), `ConsultantAccessService`, `AdminAccessService`, `DisabledUserFilter`, 26 × `@PreAuthorize` |

### 2.2 Partial — residual work named

| ID | Requirement | What is delivered | What is missing |
| --- | --- | --- | --- |
| `BL-05` | Seeded checklist templates per application type | `V2__seed_data.sql` covers all 11 types; 29–61 seed references each | **PNP has 3.** One application type is effectively unseeded → `DR-02` |

### 2.3 Prior audit findings — closure status

| Audit | Findings | Closed | Still open |
| --- | --- | ---: | --- |
| **2.3 Missing Validation** | 4 × P0, 4 × P1 | 7 of 8 | P1-8 upload security — partial, no malware scan (`SEC-08`) |
| **3.0 Security Audit** | 5 Critical, 4 High, 6 Medium | 6 of 15 | High-6 (`DR-09`, `DR-13`), High-7 (`DR-05`), High-8 (`SEC-08`), Med-10 (`DR-08`), Med-12 (`SEC-06`), Med-13 (`DR-06`), Med-15 (`DR-07`), standards (`DR-03`, `DR-04`) |

**All five Critical findings are genuinely fixed** — the API is an authenticated resource server,
identity derives from the principal, `mcpApiKey` is gone from code and schema (`V5`), and `logAudit`
has 18 call sites where the audit found zero.

---

## 3. Drift — open findings and rule violations (`DR-*`)

**Regressions and unfinished items found by re-verification**, not new feature requests. This is the
cheapest work in the register and clears first ([Plan.md](Plan.md) §3).
Full breakdown: [`plan/phase-0.5-drift.md`](plan/phase-0.5-drift.md).

### 3.1 High severity

| ID | Finding | Evidence | Acceptance |
| --- | --- | --- | --- |
| `DR-04` | Three controllers map `@RequestMapping("/api…")` under `server.servlet.context-path=/api`, resolving to `/api/api/…` — and so bypass the `/v1/**` security matcher | `AutomationController` :16, `PartyPortalController` :14, `WorkflowController` :17 | All three serve under `/v1/**`; a test asserts each route resolves and is authenticated; frontend `API_ENDPOINTS` updated in the same change |
| `DR-05` | `PartyProfile.accessToken` has no expiry, revocation, or rotation — permanent unauthenticated access to case data | `entity/PartyProfile.java` :34 | Token carries `expiresAt` and `revokedAt`; rotation endpoint exists; token is masked in every response; expired/revoked tokens 403 |
| `DR-08` | `spring.jpa.show-sql=true` and `format_sql=true` in the **main** profile — logs bound PII in every environment | `application.properties` :21-22 | Both `false` in the main profile; dev-only overrides live in `application-dev.properties` |
| `DR-10` | Backend/MCP test coverage is 6 files for 37 services and 17 controllers; the Section 4.1 §9 test plan is unwritten | `backend/src/test` (2), `MCPServer/src/test` (4) | See [`qa/DR-10-test-foundation.md`](qa/DR-10-test-foundation.md) — a regression test per `BL-*` claim, per `F41-*` delivered item, and a failing-without-the-fix test per drift item |
| `DR-14` | **Zero frontend test files.** `DR-10` counted backend and MCP only, so this was invisible | `find frontend/src -name "*.spec.ts"` → 0 | Spec coverage for `api.service`, the auth guards, and the four forms-package components |

### 3.2 Medium severity

| ID | Finding | Evidence | Acceptance |
| --- | --- | --- | --- |
| `DR-02` | PNP checklist templates near-empty — 3 seed references against 29–61 for every other type | `V2__seed_data.sql` | PNP seeded to parity; each template carries `sourceUrl` and `lastReviewedDate` per `BR-3` |
| `DR-03` | `@Column(..., length = 15)` on `Client.clientNumber`, `Consultant.consultantNumber`, `ImmigrationCase.caseNumber` — violates **CLAUDE.md §7** | `Client` :25, `Consultant` :24, `ImmigrationCase` :26 | The three annotations drop `length`; the generated-column definitions are confirmed unchanged |
| `DR-06` | CORS defined twice — global `CorsConfig` **and** 9 × `@CrossOrigin` | `config/CorsConfig.java` + 9 controllers | One definition. `@CrossOrigin` removed; origins come from `app.cors.allowed-origins` only |
| `DR-07` | No pagination anywhere — zero `Pageable` usages; every list and search endpoint is unbounded | backend-wide | Every collection endpoint accepts `Pageable` with a bounded default page size; the SPA paginates |
| `DR-09` | Insecure fallback `spring.datasource.password=${DB_PASSWORD:ChangeThisStrongPassword123!}` — live if the env var is unset | `application.properties` :15 | No literal default. Startup fails loudly when `DB_PASSWORD` is absent |
| `DR-13` | The same fallback is **also** in the dev profile. A fix touching only `DR-09`'s file leaves the hole open | `application-dev.properties` :5 | Fixed in the same change as `DR-09`; a grep for the literal returns nothing |

### 3.3 Low severity

| ID | Finding | Evidence | Acceptance |
| --- | --- | --- | --- |
| `DR-12` | `application.properties` :68-70 documents per-consultant MCP API keys (`format: mcp_<uuid>`). `V5__drop_consultant_mcp_api_key.sql` removed that column — the comment describes a schema that no longer exists | `application.properties` | Comment block removed |
| `DR-15` | `app.routes.ts` claims the `forms-package` route was "Replaced by the full `FormsPackageWorkspaceComponent` in Milestone 5". That component does not exist | `frontend/src/app/app.routes.ts` | Resolved by `F41-13`, or the comment is corrected to describe what the route actually loads |

### 3.4 Closed

| ID | Finding | Ruling |
| --- | --- | --- |
| `DR-01` | `TriggerQuestion` entity specified but never built | ⏭ `SUPERSEDED` — the capability is delivered as `IntakeQuestionTemplate.isTriggerQuestion` + `ConditionalRule.triggerQuestionKey` with repository finders and full evaluation in `ChecklistGeneratorService`. Functionally equivalent, simpler. [ADR-003](design/ADR-003-trigger-question-as-flag.md) |
| `DR-11` | `Runbooks/` and doc 3.1 document Microsoft Entra External ID; the stack runs Keycloak | ⏭ `SUPERSEDED` by decision `D-5`. [ADR-001](design/ADR-001-keycloak-over-entra.md), [Architecture.md](Architecture.md) §7 |

---

## 4. Section 4.1 — IRCC form & package automation (`F41-*`)

Source: `Section-4.1-Backlog.md`, `Section-4.1-IRCC-Form-Package-Automation-Implementation-Plan.md`,
`Section-4.1-XFA-Real-PDF-Onboarding-Issue.md`.

**This is the product's differentiator and it is far further along than the register previously
recorded.** Nine of fourteen items are delivered.

### 4.1 Milestones

| Milestone | Scope | Status |
| --- | --- | --- |
| M1 | Schema & content foundation (`V6__form_package_automation.sql`) | ✅ |
| M2 | Canonical data + mapping preview (`CanonicalApplicantDataService`, `FormMappingService`) | ✅ |
| M3 | PDF generation prototype, AcroForm (`PdfBoxFormEngine`, `SampleAcroFormSeeder`) | ✅ |
| M4 | Validation & readiness report (`PackageValidationService`) | ✅ |
| — | Manual filled-form upload (`DraftOrigin.UPLOADED`, `V9__case_form_draft_origin.sql`) | ✅ |
| **M5** | **Package assembly & approval (`CasePackageService`)** | ✅ **complete** — `F41-03`…`F41-10` all delivered |
| **M6** | **Admin governance + form inspection (`FormCatalogueService`)** | 🟡 catalogue UI done (`F41-11`); admin editors open (`F41-12`) |

### 4.2 Delivered

| ID | Requirement | Evidence |
| --- | --- | --- |
| `F41-03` | Persist validation issues as `PackageValidationIssue` rows tied to `CasePackage` | `CasePackageService.persistIssues()` :249; `buildReadinessFromPersisted()` :351; dedicated repository |
| `F41-04` | Approval gate: every REQUIRED `PackageProfileForm` needs a current non-superseded draft, else `ERROR REQUIRED_FORM_NOT_PROVIDED` | `PackageValidationService` :114 |
| `F41-05` | Suppress form-field validation noise for forms satisfied by manual upload | `PackageValidationService` :207 — `origin == DraftOrigin.UPLOADED` |
| `F41-06` | `createOrRefreshPackage` assembles index/manifest across generated **and** uploaded drafts, recording origin per form | `CasePackageService.buildIndex()` :204, `currentDrafts()` :337, `manifestEntry()` :440 |
| `F41-07` | Package zip bundling PDFs + index + manifest + readiness; enforce `app.forms.max-generated-package-bytes` | `CasePackageService.buildZip()` :272 |
| `F41-08` | `approvePackage` gated on zero unresolved ERRORs plus explicit acknowledgement including manual-form responsibility; status transitions + audit | `CasePackageService.approvePackage()` :157 |
| `F41-09` | Issue-resolution endpoints and UI for `DECISION` / `CLIENT_CONFIRMATION` / `UNRESOLVED_EVIDENCE` | `resolveIssue()` :135; `POST …/issues/{issueId}/resolve`; `package-approval.component.ts` :70 |
| `F41-10` | Secured package download endpoint | `getPackageForDownload()` :189; `GET …/packages/{id}/download` under `canAccessCase` |
| `F41-11` | Form-catalogue UI surfacing fillable-vs-manual and XFA/blocked status | `forms-catalogue.component.ts` :95-103; routed behind `adminConsultantGuard` |

### 4.3 Partial

| ID | Requirement | Delivered | Missing | Detail |
| --- | --- | --- | --- | --- |
| `F41-01` | Inspect and classify every source PDF; persist `supportsFill`, `supportsBarcode`, `status`, `sourceSha256` | The four fields exist on `FormDefinition`; `PdfBoxFormEngine` :38 detects `hasXfa`; inspection sets `BLOCKED` | The four-way classification (`STANDARD_ACROFORM`/`STATIC_XFA`/`DYNAMIC_XFA`/`NONE`) — **no such enum exists**; barcode detection never sets `supportsBarcode` | [`requirements/F41-01.md`](requirements/F41-01.md) |
| `F41-13` | `FormsPackageWorkspaceComponent` — tabbed workspace; case-detail entry point and status card | Functionality reachable: `MappingReviewComponent` embeds `ValidationReadinessComponent` and `PackageApprovalComponent` | The tabbed workspace shell; the case-detail entry point and status card; the stale route comment (`DR-15`) | [`requirements/F41-13.md`](requirements/F41-13.md) |

### 4.4 Open

| ID | Requirement | Note |
| --- | --- | --- |
| `F41-02` | Data-sheet fallback for `BLOCKED` forms — a printable mapped-values sheet the consultant transcribes into Adobe | The recommended zero-cost path, and the permanent answer if `D-1` resolves against licensing |
| `F41-12` | Admin editors: mapping-version field-by-field, package-profile composition, create-form | **Backends exist** — `FormCatalogueController` exposes mapping approval and package-profile CRUD. This is Angular work only |

### 4.5 Blocked

| ID | Requirement | Blocker |
| --- | --- | --- |
| `F41-14` | Automated XFA autofill (datasets injection + `saveIncremental`, `xfaDataPath` on field definition/mapping) | PoC 2026-06-29 proved PDFBox **cannot** re-save IRCC's encrypted + certified forms into an Adobe-valid file ([PDFBOX-3188](https://issues.apache.org/jira/browse/PDFBOX-3188), [PDFBOX-4286](https://issues.apache.org/jira/browse/PDFBOX-4286)). Needs an iText 7 / Aspose.PDF / Qoppa licence or Adobe AEM — **a procurement decision (`D-1`), not a coding task.** [ADR-002](design/ADR-002-xfa-engine-deferred.md), [`memory/pdfbox-cannot-save-ircc-forms.md`](memory/pdfbox-cannot-save-ircc-forms.md) |

### 4.6 Known data-model gaps (feed `GAP-09`)

- `ADDRESS_GAP_DETECTED` unimplemented — **no address-history entity exists**. `WORK_HISTORY_GAP_DETECTED` is implemented (`PackageValidationService` :191); the address equivalent has nothing to read.
- No "certified copy" flag on `Document` — `CERTIFIED_COPY_REQUIRED_UNRESOLVED` (:289) is informational only.
- `FORM_SOURCE_HASH_MISMATCH` is enforced at generation (`CaseFormGenerationService` :285) but not in the readiness layer.
- Audit detail is a compact JSON string via `CommonService.logAudit`; the plan asked for richer structured audit.
- Multi-DB parity: MySQL/MSSQL/Oracle frozen at **V2**; V3–V9 are PostgreSQL-only. See decision `D-4`.

---

## 5. Phase 4 — production hardening (`SEC-*`)

Source: `Phase-4-Hardening-Plan.pdf`. **Re-expressed for Keycloak** per decision `D-5` / `DR-11` — the
original text assumed Microsoft Entra. Control intent is unchanged; the provider is not.

**Nothing here is verified. Five are partial, eleven are open.** This is the gap between a working
demo and a system that may hold regulated client data.

### A. Identity

| ID | Original | Keycloak equivalent | Status | Evidence |
| --- | --- | --- | --- | --- |
| `SEC-01` | A1 MFA via Conditional Access | Keycloak OTP required-action in the browser flow; stricter policy for the `admin` role | ⬜ `OPEN` | — |
| `SEC-02` | A2 Step-up / sensitive-action re-auth | `acr`/`amr` claim check on destructive and outward actions; SPA re-auth with `prompt=login` | ⬜ `OPEN` | — |
| `SEC-03` | A3 Refresh-token handling | Confirm the SPA requests `offline_access`; silent renewal; clean routing on renewal failure | ⬜ `OPEN` | — |
| `SEC-04` | A4 Secrets to Key Vault + managed identity | Move DB password, mail credentials, and Keycloak client secrets out of `.env` into a secret store; define a rotation policy | ⬜ `OPEN` | blocked-adjacent to `DR-09`/`DR-13` |
| `SEC-05` | A5 Consent & app branding review | Realm client-scope review and login-theme branding | ⬜ `OPEN` | anonymous DCR is a dev posture |

### B. Backend

| ID | Requirement | Status | Evidence |
| --- | --- | --- | --- |
| `SEC-06` | B6 Rate limiting per user/IP on auth-sensitive and write endpoints; throttle repeated 401/403 | ⬜ `OPEN` | **0 implementations**; no bucket4j or resilience4j dependency |
| `SEC-07` | B7 Tenant-level authorization — scope every query by tenant; design now, enforce when multi-org | ⬜ `OPEN` | consultant scoping only — see `GAP-13` |
| `SEC-08` | B8 Per-document authorization, short-lived signed download URLs, malware scanning on upload | 🟡 `PARTIAL` | size limit and partial magic-byte checks exist; **0 malware-scan implementations** |
| `SEC-09` | B9 Security audit coverage — login, consultant enable/disable, role/permission changes, admin actions | 🟡 `PARTIAL` | 18 `logAudit` call sites, all domain events; **no security events** |
| `SEC-10` | B10 Restrict CORS origins to production domains; HSTS/CSP/secure headers; enforce HTTPS | 🟡 `PARTIAL` | origins are env-driven; `SecurityConfig` sets **no HSTS and no CSP**; CSRF disabled :64; see `DR-06` |

### C. MCP server

| ID | Requirement | Status | Evidence |
| --- | --- | --- | --- |
| `SEC-11` | C11 Transport context-extractor if the tool handler runs off the request thread | ⬜ `OPEN` | verify under real load |
| `SEC-12` | C12 Tool rate limits and per-tool quotas; confirm every call is audited | 🟡 `PARTIAL` | auditing wired (`McpToolAuditService`); no throttling |
| `SEC-13` | C13 Private networking / mTLS on the MCP→backend path; never expose `/v1/mcp/**` publicly | ⬜ `OPEN` | internal network only by convention, not enforced |

### D. Operations

| ID | Requirement | Status | Evidence |
| --- | --- | --- | --- |
| `SEC-14` | D14 Telemetry — instrument backend and MCP; dashboards and alerts on auth-failure spikes, 403s from disabled users, latency | ⬜ `OPEN` | — |
| `SEC-15` | D15 Dependency and code scanning in CI; security review on each release diff | 🟡 `PARTIAL` | gitleaks pre-commit hook only (CLAUDE.md §8); **no CI** |
| `SEC-16` | D16 Token/session revocation runbook combining Keycloak session revocation with `DisabledUserFilter` | ⬜ `OPEN` | Entra runbooks superseded — nothing replaces them |

**The plan's own recommended first wave:** `SEC-01` (MFA) + `SEC-04` (secrets) + `SEC-06` (rate limiting) + `SEC-14` (alerting).

---

## 6. Phase 5 — product gap portfolio (`GAP-*`)

Source: `Phase 5 Immigration-Consultation-Missing-Features-and-Recommendations.pdf` §4.1–4.17.
Priority and effort are the report's own. **`GAP-01` is `F41-*` above** and is not restated.

| ID | § | Domain | Pri | Effort | Status | What exists today |
| --- | --- | --- | --- | --- | --- | --- |
| `GAP-01` | 4.1 | IRCC form & package automation | P0 | XL | 🟡 `PARTIAL` | M1–M5 complete, M6 partial → `F41-*`. **The most advanced domain** |
| `GAP-02` | 4.2 | Billing, payments, trust accounting | P0 | L | ⬜ `OPEN` | no invoice, payment, or ledger entity |
| `GAP-03` | 4.3 | Electronic signatures & retainer automation | P0 | M | ⬜ `OPEN` | retainer date + document path only |
| `GAP-04` | 4.4 | Calendar, booking, deadline management | P1 | M | ⬜ `OPEN` | case deadline + `Reminder` entity only |
| `GAP-05` | 4.5 | Unified communications & correspondence record | P1 | L | ⬜ `OPEN` | reminder drafting + SMTP config |
| `GAP-06` | 4.6 | Secure full-service client & third-party portal | P1 | L | 🟡 `PARTIAL` | unauthenticated token portal (`PartyProfile`) + client checklist view; see `DR-05` |
| `GAP-07` | 4.7 | Production document management, OCR, evidence ops | P1 | L | 🟡 `PARTIAL` | upload, classification, expiry alerts; no OCR, no versions, no AV |
| `GAP-08` | 4.8 | Tasks, workflow automation, team collaboration | P1 | L | ⬜ `OPEN` | checklist items + reminders only |
| `GAP-09` | 4.9 | Rules, forms & immigration knowledge governance | P1 | M | 🟡 `PARTIAL` | `BL-06` for checklist templates; `FormMappingVersion` DRAFT→APPROVED→SUPERSEDED for forms. No two-person publication, no impact analysis, no rollback |
| `GAP-10` | 4.10 | Reporting, analytics, compliance exports, portability | P2 | M | ⬜ `OPEN` | operational dashboards only |
| `GAP-11` | 4.11 | Lead CRM, consultation conversion, referrals | P2 | M | 🟡 `PARTIAL` | `LeadStatus` + case pipeline component |
| `GAP-12` | 4.12 | Integrations, APIs, webhooks, marketplace | P2 | L | ⬜ `OPEN` | app + MCP APIs; no outbox, no webhooks |
| `GAP-13` | 4.13 | Multi-tenant SaaS, firm admin, subscriptions | P2 | XL | ⬜ `OPEN` | org dashboards + consultant scoping; see `SEC-07` |
| `GAP-14` | 4.14 | Security, privacy, compliance, resilience | P0 | L | 🟡 `PARTIAL` | → `SEC-*` — 5 partial, 11 open |
| `GAP-15` | 4.15 | Accessibility, localization, mobile, inclusive intake | P2 | L | ⬜ `OPEN` | — |
| `GAP-16` | 4.16 | AI governance & advanced assistance | P2 | M | 🟡 `PARTIAL` | `BL-07` is the safety boundary; no prompt/model versioning, no eval suites |
| `GAP-17` | 4.17 | Onboarding, data migration, support, product ops | P3 | M | ⬜ `OPEN` | — |

### 6.1 Minimum viable release per domain

Condensed from each section's "Minimum viable release". These are the **acceptance definitions** — a
domain is not delivered until its MVR is met.

- **`GAP-02`** Fee plan, invoices, hosted payment link, receipts, installment status, accounting export, role-controlled refunds, basic client ledger. *Trust reconciliation deferred pending licensed-practitioner validation (`D-6`).*
- **`GAP-03`** Generate retainer → send to two signers → webhook status → store signed copy and certificate → update lead status → create the first invoice milestone.
- **`GAP-04`** Firm calendar, case appointments, booking link, Microsoft 365 sync, configurable reminders, deadline ownership, overdue escalation.
- **`GAP-05`** Outbound email, templates, approval, delivery/bounce status, reply capture, attachments, case timeline, client communication preferences.
- **`GAP-06`** Authenticated portal, checklist/uploads, secure messages, appointment view, invoice/payment link, signature status, invitation/revocation, multilingual shell.
- **`GAP-07`** Object storage, preview/download, immutable versions, OCR, text search, malware scan, duplicate warning, access log, package export.
- **`GAP-08`** Tasks, assignments, due dates, comments, dependencies, queues, workflow templates, event triggers, manager workload dashboard.
- **`GAP-09`** Source-linked effective-dated templates and rules; two-person publication; change log; active-case impact report; rollback; regression fixtures.
- **`GAP-10`** Ten standard reports, CSV export, scheduled delivery, funnel and stage-aging dashboards, audit export, full matter archive export.
- **`GAP-11`** Lead form, qualification, booking/payment link, consultation record, pipeline, follow-up tasks, source attribution, one-click conversion.
- **`GAP-12`** Outbox events, signed webhooks, API credentials, Microsoft 365, one payment provider, one signature provider, accounting export, integration health page.
- **`GAP-13`** Firm tenant, scoped repositories and storage, firm admin, roles, invitations, plan entitlements, usage counters, tenant export, automated isolation tests.
- **`GAP-14`** Tenant/object authorization, MFA policy, secrets vault, malware scanning, complete security audit events, data export/deletion workflow, monitoring alerts, backup/restore test, incident runbooks.
- **`GAP-15`** Accessible design-system components, keyboard/screen-reader audit, English/French shell, mobile intake and uploads, autosave, resume, localization-ready templates.
- **`GAP-16`** Versioned prompts and models, grounded case summary, document-extraction review, regression evaluations, confidence display, consultant approval, AI audit export.
- **`GAP-17`** Setup wizard, CSV import, bulk document import, validation report, sample workspace, admin guide, user guide, release notes, support/status workflow.

---

## 7. Buy-versus-build policy

From Phase 5 §5. **Binding** — a requirement that proposes building something in the right-hand column
must first be challenged.

| Build in-house | Buy or integrate |
| --- | --- |
| Canadian workflow, canonical case data, checklist logic, package readiness, consultant approvals, case timeline, content governance | Card payments, e-signatures, email/SMS delivery, calendar transport, OCR, malware scanning, accounting, commodity storage |
| Provider-neutral adapters, audit, permissions, firm configuration, and the UX around external services | Infrastructure needing certification, network reach, specialized compliance, or large ongoing delivery operations |

**Already-settled procurement decisions**

| Decision | Ruling | Rationale |
| --- | --- | --- |
| SaaS form-fill API (Anvil, PDF.co) | **Rejected** | PII egress violates `BR-5`; AcroForm-only — cannot handle XFA |
| iText 7 under AGPL | **Rejected** | AGPL would force open-sourcing a commercial SaaS |
| Commercial XFA engine (iText 7 commercial / Aspose.PDF / Qoppa) | **Undecided — blocks `F41-14`** | The only path to a valid filled dynamic-XFA PDF short of Adobe AEM |

---

## 8. Measurement framework

From Phase 5 §7. **Every automation metric is paired with a quality guardrail so speed cannot hide defects.**

| Objective | Primary measures | Guardrails |
| --- | --- | --- |
| Reduce preparation effort | Hours per case; rekey events; document review time; form completion time | Material defect rate; consultant override rate; client correction rate |
| Improve conversion and cash flow | Lead response; booking/show rate; retainer conversion; days to payment; overdue balances | Refunds; disputes; consent complaints; failed payments |
| Increase case quality | First-pass approval; readiness warnings resolved; consistency defects found before filing | False positives; missed critical issues; outdated content usage |
| Improve client experience | Portal activation; intake completion; upload turnaround; response time | Abandonment; accessibility defects; unauthorized access; support burden |
| Scale firms safely | Cases per staff member; overdue work; cycle time; utilization; gross retention | Security incidents; isolation failures; restore failures |
| Operate AI responsibly | Accepted outputs; time saved; evaluation scores; grounded citations | Material errors; leakage; unreviewed external actions; cost per workflow |

**`GAP-01` targets:** field reuse above 70%; preparation time down 30–50%; under 1% mapping defects after approval; 100% of outputs linked to a form version and approver.

**None of these are instrumented today.** Capture a baseline **before** launching each feature. Report
median and percentile cycle times, not averages. Segment by program and firm size.

---

## 9. Open decisions

These block or reshape work and are **the user's to make**, not the delivery team's.

| # | Decision | Blocks | Default if unanswered | Needed by |
| --- | --- | --- | --- | --- |
| `D-1` | License a commercial XFA engine, or ship the data-sheet fallback permanently? | `F41-14` | Data-sheet fallback (`F41-02`); AcroForm fill only | Phase 1 |
| `D-2` | Single-firm deployment, multi-tenant SaaS, or both? | `GAP-13`, `SEC-07` | Design tenant-ready, enforce single-tenant | Phase 0 |
| `D-3` | Which 3–5 programs are the first commercial target? | `GAP-01` scope, `GAP-09` content load | Study Permit, Visitor Visa, Work Permit — the highest seeded coverage | Phase 0 |
| `D-4` | Revive multi-DB parity (MySQL/MSSQL/Oracle at V2) or declare PostgreSQL-only? | migration workload on every schema change | **PostgreSQL-only** — the stack ships Postgres | Phase 0 |
| `D-5` | Is Keycloak the production identity provider, or is Entra still the target? | `SEC-01`…`SEC-05` wording; runbook validity | ✅ **Decided** — Keycloak. [ADR-001](design/ADR-001-keycloak-over-entra.md) | — |
| `D-6` | Trust-accounting requirements — which provinces and arrangements apply? | `GAP-02` scope | Defer trust; ship operating-funds billing only | Phase 2 |

---

## 10. Traceability

Every requirement here must appear in exactly one row of [status dashboard.md](status%20dashboard.md),
be scheduled in exactly one phase of [Plan.md](Plan.md), and record its completion in
[change.log.md](change.log.md). The [`status-sync`](skills/status-sync/SKILL.md) skill checks these
three invariants.

**Two rules govern this file:**

1. **A status is a claim about code.** It is set by reading the source, never by reading a document that says something was done. The 2026-08-21 pass took the Section 4.1 backlog at its word and recorded nine delivered items as `OPEN`; the 2026-08-24 pass read `CasePackageService` and corrected them.
2. **A summary and its detail must not disagree.** The prior register said M5 was "code-complete" and its eight tasks were "outstanding" in the same table. When a contradiction like that appears, the code decides — then both halves get fixed.
