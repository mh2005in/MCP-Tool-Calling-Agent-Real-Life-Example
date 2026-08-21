# Requirements Register

> **Single source of truth for *what* this product must do.**
> Companion documents: [Architecture.md](Architecture.md) (*how it is built*),
> [Plan.md](Plan.md) (*in what order*), [Delivery approach.md](Delivery%20approach.md) (*how work is executed*),
> [status dashboard.md](status%20dashboard.md) (*where each item stands*), [change.log.md](change.log.md) (*what changed*).

**Last updated:** 2026-08-21
**Per-requirement detail:** [`requirements/`](requirements/) — `<ID>.md` for anything too large for a register row
**Source corpus:** [`input/`](input/) — extracted, redacted, greppable (upstream: `C:\Users\mh200\Downloads\SoftwareForImmigrationConsultants\`)

| Source folder | Contents | Treatment here |
| --- | --- | --- |
| `HighLevelRequirement Completed/` | Stage 1 (domain), 2.0 (application-type deep dive), 2.1 (TODO 1–10), 2.2 (MCP/AI + access control), 2.3 (missing validation), 3.0 (security audit), 3.1 (Entra plan) | Baseline (`BL-*`) — re-verified against code 2026-08-21; failures recorded as drift (`DR-*`) |
| `HighLevelRequirement Pending/` | Phase-4 Hardening Plan, Phase 5 Gap Analysis §4.1–4.17, Section 4.1 plan/backlog/XFA issue | Open requirements (`SEC-*`, `GAP-*`, `F41-*`) |
| `Runbooks/` | Entra auth config, MCP client pre-registration, MCP registration removal | Superseded by Keycloak — see `DR-11` |

---

## 1. Requirement ID scheme

| Prefix | Meaning | Count |
| --- | --- | --- |
| `BL-nn` | **Baseline** — delivered capability, re-verified against code | 12 |
| `DR-nn` | **Drift** — audit finding still open, or a rule the code violates | 11 |
| `F41-nn` | **Section 4.1** — IRCC form & package automation backlog | 14 |
| `SEC-nn` | **Phase-4 hardening** — production-readiness pass (A1–D16) | 16 |
| `GAP-nn` | **Phase 5 gap analysis** — product domains §4.1–4.17 | 17 |

**Status vocabulary** (used identically in [status dashboard.md](status%20dashboard.md)):

| Status | Meaning |
| --- | --- |
| `VERIFIED` | Implemented **and** confirmed present in code on 2026-08-21 |
| `PARTIAL` | Implemented in part; residual work named |
| `OPEN` | Not started |
| `BLOCKED` | Cannot proceed — external dependency or licensing decision required |
| `SUPERSEDED` | No longer applicable; a decision replaced it |

---

## 2. Product boundary (non-negotiable)

Derived from Stage 1 §1 and Stage 2.0 §1. **Every requirement below is subordinate to this.**

> The system may automate intake, document collection, tracking, reminders, summaries, and draft
> communications. It **must not** provide immigration advice, eligibility decisions, legal
> interpretation, or guarantee outcomes.

Enforcement rules that no requirement may weaken:

- **BR-1** — AI output is never presented as an eligibility or legal conclusion. Compliance calculators return *preliminary review* language, never "requirements met".
- **BR-2** — A licensed consultant approves any artefact before a client or IRCC sees it.
- **BR-3** — Every checklist, rule, and form carries a source URL, version, and last-reviewed date.
- **BR-4** — Every PII read/write is audited with actor, subject, and timestamp.
- **BR-5** — Client PII does not leave the tenant. No SaaS form-fill and no LLM egress of unmasked PII.

---

## 3. Baseline — delivered and re-verified (`BL-*`)

Re-verification performed 2026-08-21 by inspecting the code, not the documents.

| ID | Requirement | Source | Evidence in code | Status |
| --- | --- | --- | --- | --- |
| `BL-01` | Consultant/client/case management with service types, lead and case status | 2.1 §10 | `entity/Client.java`, `Consultant.java`, `ImmigrationCase.java`; `LeadStatus`, `CaseStatus` | `VERIFIED` |
| `BL-02` | 11 application types (adds PGWP, SUPER_VISA, PR_CARD_PRTD, PNP) plus subtype and applicant role | 2.1 §2.1–2.3 | `enums/ServiceType.java` (11 + OTHER), `CaseSubtype.java`, `ApplicantRole.java` | `VERIFIED` |
| `BL-03` | Rules-based checklist generation from conditional rules | 2.1 §2.4–2.6 | `ChecklistGeneratorService`, `entity/ConditionalRule.java`, `config/ConditionalRuleSeeder.java` | `PARTIAL` — see `DR-01` |
| `BL-04` | Structured intake templates per application type | 2.1 §3.1–3.13 | `config/IntakeQuestionSeeder.java` covers all 11 types; `IntakeQuestionTemplate` | `VERIFIED` |
| `BL-05` | Seeded checklist templates per application type | 2.1 §4.1–4.10 | `V2__seed_data.sql` — all 11 types present | `PARTIAL` — see `DR-02` |
| `BL-06` | Governance fields on checklist templates: source URL, reviewer, rule version, approval gate | 2.1 §1.1–1.4 | `ChecklistTemplate`: `sourceUrl`, `lastReviewedDate`, `reviewedByConsultantId`, `ruleVersion`, `approvedForUse`, `approvedByConsultantId`, `approvedDate` | `VERIFIED` |
| `BL-07` | AI boundary, masking, sanitisation, sensitive-field registry | 2.2 §A | `AiBoundaryService`, `DataMaskingService`, `DocumentMetadataSanitizer`, `SensitiveFieldRegistry` | `VERIFIED` |
| `BL-08` | MCP tool surface with per-consultant scoping and audit | 2.2 §B | `McpApiController`, `McpDataService`, `McpToolAuditService`, `MCPServer/src/main/resources/config/tools.json` | `VERIFIED` |
| `BL-09` | Canadian workflow modules: travel, work, relationship timeline, recruitment evidence, candidate comparison | 2.1 §7 | `TravelHistoryService`, `WorkHistoryService`, `RelationshipTimelineService`, `RecruitmentService`, `entity/CandidateComparison.java` | `VERIFIED` |
| `BL-10` | Compliance calculators corrected to IRCC rules, with preliminary-review language | 2.3 P0-1/3/4 | `TravelHistoryService` (`MIN_PR_DAYS=730`, `PRE_PR_CAP_DAYS=365`); `PoliceCertificateService` (`CONTINUOUS_DAYS_THRESHOLD=183`, `MINIMUM_AGE=18`, 10-year window); `LmiaCalculatorService` (`preliminaryReviewStatus`) | `VERIFIED` |
| `BL-11` | Server-authoritative intake validation against templates | 2.3 P0-2 | `IntakeService.submitIntake` resolves templates by key, rejects unknown keys, enforces required questions | `VERIFIED` |
| `BL-12` | OAuth2 authentication with consultant/admin authorization and disabled-user cutoff | 3.0 Critical 1–5 | `SecurityConfig` (resource server, `/v1/**` authenticated), `ConsultantAccessService`, `AdminAccessService`, `DisabledUserFilter`, 22 × `@PreAuthorize` | `VERIFIED` |

### 3.1 Section 4.1 milestones already delivered

| Milestone | Scope | Status |
| --- | --- | --- |
| M1 | Schema & content foundation (`V6__form_package_automation.sql`) | `VERIFIED` |
| M2 | Canonical data + mapping preview (`CanonicalApplicantDataService`, `FormMappingService`) | `VERIFIED` |
| M3 | PDF generation prototype, AcroForm (`PdfBoxFormEngine`, `SampleAcroFormSeeder`) | `VERIFIED` |
| M4 | Validation & readiness report (`PackageValidationService`) | `VERIFIED` |
| — | Manual filled-form upload (`DraftOrigin.UPLOADED`, `V9__case_form_draft_origin.sql`) | `VERIFIED` |
| M5 | Package assembly & approval (`CasePackageService`) | `PARTIAL` — code-complete; `F41-03`…`F41-10` outstanding |
| M6 | Admin governance + form inspection (`FormCatalogueService`) | `PARTIAL` — core done; admin editor UIs deferred (`F41-11`…`F41-13`) |

---

## 4. Drift — open audit findings and rule violations (`DR-*`)

**Regressions and unfinished items found during the 2026-08-21 re-verification**, not new feature
requests. This is the cheapest work in the register and should be cleared first.

| ID | Finding | Evidence | Source | Severity |
| --- | --- | --- | --- | --- |
| `DR-01` | `TriggerQuestion` entity was specified but never built; its role is served by `IntakeQuestionTemplate` + `ConditionalRule` | no `*Trigger*` file exists | 2.1 §2.6 | Low — **document the deviation or build it** |
| `DR-02` | PNP checklist templates are near-empty (3 references vs 29–61 for every other type) | `V2__seed_data.sql` | 2.1 §4 | Medium |
| `DR-03` | `@Column(length = 15)` on `Client.clientNumber`, `Consultant.consultantNumber`, `ImmigrationCase.caseNumber` — violates **CLAUDE.md §7** ("never add length constraints") | 3 entity files | 3.0 Coding standards | Medium |
| `DR-04` | Three controllers map `@RequestMapping("/api")` under `server.servlet.context-path=/api`, resolving to `/api/api/…`: `AutomationController`, `PartyPortalController`, `WorkflowController` | controller files | 3.0 Standards; 4.1 backlog §0 | **High** — broken routes; the backlog lists only `WorkflowController` |
| `DR-05` | `PartyProfile.accessToken` has no expiry, revocation, or rotation | `entity/PartyProfile.java` | 3.0 High-7; §4.6 | **High** |
| `DR-06` | CORS defined twice — global `CorsConfig` **and** 9 × `@CrossOrigin` on controllers | `config/CorsConfig.java` + controllers | 3.0 Medium-13 | Medium |
| `DR-07` | No pagination anywhere — zero `Pageable` usages; list/search endpoints unbounded | backend-wide | 3.0 Medium-15 | Medium |
| `DR-08` | `spring.jpa.show-sql=true` and `format_sql=true` in the **main** profile — logs bound PII in every environment | `application.properties:21-22` | 3.0 Medium-10 | **High** (PII) |
| `DR-09` | Insecure fallback `spring.datasource.password=${DB_PASSWORD:ChangeThisStrongPassword123!}` — live if the env var is unset | `application.properties:15` | 3.0 High-6 | Medium |
| `DR-10` | Test coverage is 6 files total (2 backend, 4 MCP) for ~30 services and 17 controllers; the Section 4.1 §9 test plan is unwritten | `backend/src/test`, `MCPServer/src/test` | 4.1 backlog §6; CLAUDE.md §9 | **High** |
| `DR-11` | `Runbooks/` and doc 3.1 document Microsoft Entra External ID; the stack actually runs Keycloak | `docker-compose.yml`, `README.md` | 3.1, Runbooks | `SUPERSEDED` — see [Architecture.md](Architecture.md) §7 |

---

## 5. Section 4.1 — IRCC form & package automation (`F41-*`)

Source: `Section-4.1-Backlog.md`, `Section-4.1-IRCC-Form-Package-Automation-Implementation-Plan.md`,
`Section-4.1-XFA-Real-PDF-Onboarding-Issue.md`.

### 5.1 XFA / real-form onboarding

| ID | Requirement | Status | Notes |
| --- | --- | --- | --- |
| `F41-01` | Inspect and classify every source PDF as `STANDARD_ACROFORM` / `STATIC_XFA` / `DYNAMIC_XFA` / `NONE`; persist `supportsFill`, `supportsBarcode`, `status`, `sourceSha256` | `OPEN` | Deterministic rules in XFA issue §5. **No dynamic-XFA form may ever be marked fillable** |
| `F41-02` | Data-sheet fallback for `BLOCKED` forms — printable mapped-values sheet the consultant transcribes into Adobe | `OPEN` | The recommended zero-cost path; PoC exists in `pdf-xfa-poc/` |
| `F41-14` | Automated XFA autofill (datasets injection + `saveIncremental`, `xfaDataPath` on field definition/mapping) | `BLOCKED` | PoC 2026-06-29 proved PDFBox **cannot** re-save IRCC's encrypted + certified forms into an Adobe-valid file (PDFBOX-3188/4286). Needs an iText 7 / Aspose.PDF / Qoppa licence or Adobe AEM — **a procurement decision, not a coding task** |

### 5.2 Milestone 5 — package assembly & approval

| ID | Requirement | Status |
| --- | --- | --- |
| `F41-03` | Persist validation issues as `PackageValidationIssue` rows tied to `CasePackage`, replacing the transient `ValidationIssueDto` | `OPEN` |
| `F41-04` | Approval gate: every REQUIRED `PackageProfileForm` must have a current non-superseded draft, else `ERROR REQUIRED_FORM_NOT_PROVIDED` | `OPEN` |
| `F41-05` | Suppress form-field validation noise for forms satisfied by manual upload | `OPEN` |
| `F41-06` | `createOrRefreshPackage` assembles index/manifest across generated **and** uploaded drafts, recording origin per form | `OPEN` |
| `F41-07` | Package zip bundling PDFs + index + manifest + readiness; enforce `app.forms.max-generated-package-bytes` | `OPEN` |
| `F41-08` | `approvePackage` gated on zero unresolved ERRORs plus explicit acknowledgement including manual-form responsibility; status transitions + audit | `OPEN` |
| `F41-09` | Issue-resolution endpoints and UI for `DECISION` / `CLIENT_CONFIRMATION` / `UNRESOLVED_EVIDENCE` | `OPEN` |
| `F41-10` | Secured package download endpoint | `OPEN` |

### 5.3 Milestone 6 — admin governance, inspection, UI

| ID | Requirement | Status |
| --- | --- | --- |
| `F41-11` | Form-catalogue UI surfacing fillable-vs-manual and XFA/blocked status | `OPEN` |
| `F41-12` | Admin editors: mapping-version field-by-field, package-profile composition, create-form | `OPEN` — backends exist |
| `F41-13` | `FormsPackageWorkspaceComponent` — tabbed workspace replacing the `forms-package` route; case-detail entry point and status card | `OPEN` |

### 5.4 Known data-model gaps (feed `GAP-09`)

- `ADDRESS_GAP_DETECTED` unimplemented — **no address-history entity exists**.
- No "certified copy" flag on `Document` — `CERTIFIED_COPY_REQUIRED_UNRESOLVED` is informational only.
- `FORM_SOURCE_HASH_MISMATCH` enforced at generation but not in the readiness layer.
- Audit detail is a compact JSON string via `CommonService.logAudit`; the plan asked for richer structured audit.
- Multi-DB parity: MySQL/MSSQL/Oracle frozen at **V2**; V3–V9 are PostgreSQL-only. See decision `D-4`.

---

## 6. Phase 4 — production hardening (`SEC-*`)

Source: `Phase-4-Hardening-Plan.pdf`. **Re-expressed for Keycloak** per decision `D-5` / `DR-11` — the
original text assumed Microsoft Entra. Control intent is unchanged; the provider is not.

### A. Identity

| ID | Original | Keycloak equivalent | Status |
| --- | --- | --- | --- |
| `SEC-01` | A1 MFA via Conditional Access | Keycloak OTP required-action in the browser flow; stricter policy for the `admin` role | `OPEN` |
| `SEC-02` | A2 Step-up / sensitive-action re-auth | `acr`/`amr` claim check on destructive and outward actions; SPA re-auth with `prompt=login` | `OPEN` |
| `SEC-03` | A3 Refresh-token handling | Confirm the SPA requests `offline_access`; silent renewal; clean routing on renewal failure | `OPEN` |
| `SEC-04` | A4 Secrets to Key Vault + managed identity | Move DB password, mail credentials, and Keycloak client secrets out of `.env` into a secret store; define a rotation policy | `OPEN` — see `DR-09` |
| `SEC-05` | A5 Consent & app branding review | Realm client-scope review and login-theme branding | `OPEN` |

### B. Backend

| ID | Requirement | Status |
| --- | --- | --- |
| `SEC-06` | B6 Rate limiting per user/IP on auth-sensitive and write endpoints; throttle repeated 401/403 | `OPEN` — zero implementations found |
| `SEC-07` | B7 Tenant-level authorization — scope every query by tenant; design now, enforce when multi-org | `OPEN` — see `GAP-13` |
| `SEC-08` | B8 Per-document authorization, short-lived signed download URLs, malware scanning on upload | `PARTIAL` — size limit and partial magic-byte checks exist; **no AV scan** |
| `SEC-09` | B9 Security audit coverage — login, consultant enable/disable, role/permission changes, admin actions | `PARTIAL` — 18 `logAudit` call sites; security events not covered |
| `SEC-10` | B10 Restrict CORS origins to production domains; HSTS/CSP/secure headers; enforce HTTPS | `PARTIAL` — see `DR-06` |

### C. MCP server

| ID | Requirement | Status |
| --- | --- | --- |
| `SEC-11` | C11 Transport context-extractor if the tool handler runs off the request thread | `OPEN` — verify under real load |
| `SEC-12` | C12 Tool rate limits and per-tool quotas; confirm every call is audited | `PARTIAL` — auditing wired; no throttling |
| `SEC-13` | C13 Private networking / mTLS on the MCP→backend path; never expose `/v1/mcp/**` publicly | `OPEN` |

### D. Operations

| ID | Requirement | Status |
| --- | --- | --- |
| `SEC-14` | D14 Telemetry — instrument backend and MCP; dashboards and alerts on auth-failure spikes, 403s from disabled users, latency | `OPEN` |
| `SEC-15` | D15 Dependency and code scanning in CI; security review on each release diff | `PARTIAL` — gitleaks pre-commit hook only (CLAUDE.md §8) |
| `SEC-16` | D16 Token/session revocation runbook combining Keycloak session revocation with `DisabledUserFilter` | `OPEN` |

**The plan's own recommended first wave:** `SEC-01` (MFA) + `SEC-04` (secrets) + `SEC-06` (rate limiting) + `SEC-14` (alerting).

---

## 7. Phase 5 — product gap portfolio (`GAP-*`)

Source: `Phase 5 Immigration-Consultation-Missing-Features-and-Recommendations.pdf` §4.1–4.17.
Priority and effort are the report's own. **`GAP-01` is `F41-*` above** and is not restated.

| ID | § | Domain | Pri | Effort | Strategy | Status |
| --- | --- | --- | --- | --- | --- | --- |
| `GAP-01` | 4.1 | IRCC form & package automation | P0 | XL | Build orchestration; license PDF technology | `PARTIAL` → `F41-*` |
| `GAP-02` | 4.2 | Billing, payments, trust accounting | P0 | L | Integrate payments; build immigration billing | `OPEN` |
| `GAP-03` | 4.3 | Electronic signatures & retainer automation | P0 | M | Integrate a signature provider | `OPEN` |
| `GAP-04` | 4.4 | Calendar, booking, deadline management | P1 | M | Integrate calendars; build deadline controls | `OPEN` |
| `GAP-05` | 4.5 | Unified communications & correspondence record | P1 | L | Integrate channels; build the case timeline | `OPEN` |
| `GAP-06` | 4.6 | Secure full-service client & third-party portal | P1 | L | Build as a core experience | `PARTIAL` — token portal only; see `DR-05` |
| `GAP-07` | 4.7 | Production document management, OCR, evidence ops | P1 | L | Build workflow; integrate storage/OCR/AV | `PARTIAL` |
| `GAP-08` | 4.8 | Tasks, workflow automation, team collaboration | P1 | L | Build the domain workflow engine incrementally | `OPEN` |
| `GAP-09` | 4.9 | Rules, forms & immigration knowledge governance | P1 | M | Build governance as a product capability | `PARTIAL` — `BL-06` covers checklist templates only |
| `GAP-10` | 4.10 | Reporting, analytics, compliance exports, portability | P2 | M | Build standard metrics; integrate BI | `OPEN` |
| `GAP-11` | 4.11 | Lead CRM, consultation conversion, referrals | P2 | M | Build a focused immigration sales workflow | `PARTIAL` — `LeadStatus` only |
| `GAP-12` | 4.12 | Integrations, APIs, webhooks, marketplace | P2 | L | Build the integration platform; partner for commodity | `OPEN` |
| `GAP-13` | 4.13 | Multi-tenant SaaS, firm admin, subscriptions | P2 | XL | Design now; phase with commercialization | `OPEN` — see `SEC-07` |
| `GAP-14` | 4.14 | Security, privacy, compliance, resilience | P0 | L | Build controls and evidence continuously | `PARTIAL` → `SEC-*` |
| `GAP-15` | 4.15 | Accessibility, localization, mobile, inclusive intake | P2 | L | Build into the design system and content lifecycle | `OPEN` |
| `GAP-16` | 4.16 | AI governance & advanced assistance | P2 | M | Extend the existing safety boundary | `PARTIAL` — `BL-07` is the boundary; no prompt/model versioning or eval suites |
| `GAP-17` | 4.17 | Onboarding, data migration, support, product ops | P3 | M | Productize repeatable onboarding | `OPEN` |

### 7.1 Minimum viable release per domain

Condensed from each section's "Minimum viable release". These are the **acceptance definitions** — a
domain is not delivered until its MVR is met.

- **`GAP-02`** Fee plan, invoices, hosted payment link, receipts, installment status, accounting export, role-controlled refunds, basic client ledger. *Trust reconciliation deferred pending licensed-practitioner validation.*
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

## 8. Buy-versus-build policy

From Phase 5 §5. **Binding** — a requirement that proposes building something in the right-hand column
must first be challenged.

| Build in-house | Buy or integrate |
| --- | --- |
| Canadian workflow, canonical case data, checklist logic, package readiness, consultant approvals, case timeline, content governance | Card payments, e-signatures, email/SMS delivery, calendar transport, OCR, malware scanning, accounting, commodity storage |
| Provider-neutral adapters, audit, permissions, firm configuration, and the UX around external services | Infrastructure needing certification, network reach, specialized compliance, or large ongoing delivery operations |

**Already-settled procurement decisions**

| Decision | Ruling | Rationale |
| --- | --- | --- |
| SaaS form-fill API (Anvil, PDF.co) | **Rejected** | PII egress; AcroForm-only — cannot handle XFA |
| iText 7 under AGPL | **Rejected** | AGPL would force open-sourcing a commercial SaaS |
| Commercial XFA engine (iText 7 commercial / Aspose.PDF / Qoppa) | **Undecided — blocks `F41-14`** | The only path to a valid filled dynamic-XFA PDF short of Adobe AEM |

---

## 9. Measurement framework

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

Capture a baseline **before** launching each feature. Report median and percentile cycle times, not averages. Segment by program and firm size.

---

## 10. Open decisions

These block or reshape work and are **the user's to make**, not the delivery team's.

| # | Decision | Blocks | Default if unanswered |
| --- | --- | --- | --- |
| `D-1` | License a commercial XFA engine, or ship the data-sheet fallback permanently? | `F41-14` | Data-sheet fallback (`F41-02`); AcroForm fill only |
| `D-2` | Single-firm deployment, multi-tenant SaaS, or both? | `GAP-13`, `SEC-07` | Design tenant-ready, enforce single-tenant |
| `D-3` | Which 3–5 programs are the first commercial target? | `GAP-01` scope, `GAP-09` content load | Study Permit, Visitor Visa, Work Permit (highest seeded coverage) |
| `D-4` | Revive multi-DB parity (MySQL/MSSQL/Oracle at V2) or declare PostgreSQL-only? | migration workload on every schema change | **PostgreSQL-only** — the stack ships Postgres |
| `D-5` | Is Keycloak the production identity provider, or is Entra still the target? | `SEC-01`…`SEC-05` wording; runbook validity | Keycloak — recorded as `DR-11`, applied throughout |
| `D-6` | Trust-accounting requirements — which provinces and arrangements apply? | `GAP-02` scope | Defer trust; ship operating-funds billing only |

---

## 11. Traceability

Every requirement here must appear in exactly one row of [status dashboard.md](status%20dashboard.md),
be scheduled in exactly one phase of [Plan.md](Plan.md), and record its completion in
[change.log.md](change.log.md). The `status-sync` skill checks these three invariants.
