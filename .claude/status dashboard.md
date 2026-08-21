# Status Dashboard

> **Where every requirement stands, as of the last verification pass.**
> Definitions live in [Requirements.md](Requirements.md); schedule in [Plan.md](Plan.md).
> Regenerate with the **`status-sync`** skill.

**Last verified:** 2026-08-21 — by direct code inspection, not by reading the requirement documents.
**Verification method:** entity/service/controller enumeration, targeted `grep` against each documented claim, migration and seed inspection, test-file count.
**Verification runs and dated snapshots:** [`progress/`](progress/) — the trend matters more than any single reading.

---

## 1. Headline

| | |
| --- | --- |
| **Baseline health** | 🟢 **Strong.** All four P0 validation defects and all five Critical security findings from the signed-off audits are genuinely fixed in code. |
| **Biggest exposure** | 🔴 **Test coverage — 6 files** for ~30 services and 17 controllers. Every refactor from here is unverifiable. |
| **Nearest value** | 🟡 **Section 4.1 is at M5/M6.** Thirteen scoped items separate it from a finished, differentiating feature. |
| **Hard blocker** | ⛔ **`F41-14`** — filling real IRCC forms is proven impossible with PDFBox. Needs a licensing decision (`D-1`), not engineering. |
| **Open decisions** | **6** — `D-1`…`D-6`. Four should close before Phase 2. |

---

## 2. Portfolio at a glance

| Group | Total | ✅ Verified | 🟡 Partial | ⬜ Open | ⛔ Blocked | ⏭ Superseded |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `BL-*` Baseline | 12 | 10 | 2 | — | — | — |
| `DR-*` Drift | 11 | — | — | 10 | — | 1 |
| `F41-*` Section 4.1 | 14 | — | — | 13 | 1 | — |
| `SEC-*` Phase-4 hardening | 16 | — | 5 | 11 | — | — |
| `GAP-*` Phase 5 domains | 17 | — | 6 | 11 | — | — |
| **Total** | **70** | **10** | **13** | **45** | **1** | **1** |

```
Verified  ██████                                     10  (14%)
Partial   ████████                                   13  (19%)
Open      ███████████████████████████                45  (64%)
Blocked   ▌                                           1   (1%)
Superseded▌                                           1   (1%)
```

---

## 3. Baseline re-verification (`BL-*`)

The Completed folder claimed these were delivered. **Confirmed against code 2026-08-21.**

| ID | Capability | Status | Evidence found |
| --- | --- | --- | --- |
| `BL-01` | Client / case / consultant management | ✅ `VERIFIED` | `Client`, `Consultant`, `ImmigrationCase` entities; `LeadStatus`, `CaseStatus` |
| `BL-02` | 11 application types + subtype + applicant role | ✅ `VERIFIED` | `ServiceType` has all 11 (`PGWP`, `SUPER_VISA`, `PR_CARD_PRTD`, `PNP` present) + `OTHER`; `CaseSubtype`, `ApplicantRole` exist |
| `BL-03` | Rules-based checklist generation | 🟡 `PARTIAL` | `ChecklistGeneratorService`, `ConditionalRule`, `ConditionalRuleSeeder` — but no `TriggerQuestion` entity (`DR-01`) |
| `BL-04` | Intake templates for all 11 types | ✅ `VERIFIED` | `IntakeQuestionSeeder` references every service type |
| `BL-05` | Seeded checklist templates | 🟡 `PARTIAL` | `V2__seed_data.sql` covers all types, but PNP has 3 references vs 29–61 elsewhere (`DR-02`) |
| `BL-06` | Template governance: source URL, reviewer, version, approval | ✅ `VERIFIED` | All 7 fields present on `ChecklistTemplate` |
| `BL-07` | AI boundary + masking + sanitisation | ✅ `VERIFIED` | `AiBoundaryService`, `DataMaskingService`, `DocumentMetadataSanitizer`, `SensitiveFieldRegistry` |
| `BL-08` | MCP tools, scoped and audited | ✅ `VERIFIED` | `McpApiController`, `McpDataService`, `McpToolAuditService`, `tools.json` |
| `BL-09` | Canadian workflow modules | ✅ `VERIFIED` | Travel, work, relationship-timeline, recruitment, candidate-comparison services and entities |
| `BL-10` | Compliance calculators corrected | ✅ `VERIFIED` | `MIN_PR_DAYS=730`, `PRE_PR_CAP_DAYS=365`, `CONTINUOUS_DAYS_THRESHOLD=183`, `MINIMUM_AGE=18`, `preliminaryReviewStatus` |
| `BL-11` | Server-authoritative intake validation | ✅ `VERIFIED` | `IntakeService` resolves templates by key, rejects unknown keys, enforces required |
| `BL-12` | OAuth2 auth + authorization + revocation | ✅ `VERIFIED` | Resource server, `/v1/**` authenticated, `ConsultantAccessService`, `AdminAccessService`, `DisabledUserFilter`, 22 × `@PreAuthorize` |

### 3.1 Prior audit findings — closure check

| Audit | Findings | Closed | Still open |
| --- | ---: | ---: | --- |
| **2.3 Missing Validation** | 4 × P0, 4 × P1 | 7 | P1-8 upload security — **partial**, no malware scan (`SEC-08`) |
| **3.0 Security Audit** | 5 Critical, 4 High, 6 Medium | 6 | High-6 (`DR-09`), High-7 (`DR-05`), High-8 (`SEC-08`), Med-10 (`DR-08`), Med-12 (`SEC-06`), Med-13 (`DR-06`), Med-15 (`DR-07`), standards (`DR-03`, `DR-04`) |

**All five Critical findings are genuinely fixed** — the API is authenticated, identity derives from
the principal, `mcpApiKey` is gone from code and schema (`V5`), and `logAudit` has 18 call sites where
the audit found zero.

---

## 4. Drift — open (`DR-*`) 🔴

Ordered by severity. **This is Phase 0.5** and the cheapest work in the portfolio.

| ID | Finding | Sev | Status | Where |
| --- | --- | --- | --- | --- |
| `DR-04` | `AutomationController`, `PartyPortalController`, `WorkflowController` map `/api` under context-path `/api` → `/api/api/…` | 🔴 High | ⬜ `OPEN` | 3 controllers — backlog listed only one |
| `DR-05` | Party-portal `accessToken` never expires and cannot be revoked | 🔴 High | ⬜ `OPEN` | `entity/PartyProfile.java` |
| `DR-08` | `show-sql=true` + `format_sql=true` in the **main** profile — bound PII in logs everywhere | 🔴 High | ⬜ `OPEN` | `application.properties:21-22` |
| `DR-10` | **6 test files total** (2 backend, 4 MCP); Section 4.1 §9 test plan unwritten | 🔴 High | ⬜ `OPEN` | `backend/src/test`, `MCPServer/src/test` |
| `DR-02` | PNP checklist templates near-empty | 🟠 Med | ⬜ `OPEN` | `V2__seed_data.sql` |
| `DR-03` | `@Column(length = 15)` on 3 number columns — violates CLAUDE.md §7 | 🟠 Med | ⬜ `OPEN` | `Client`, `Consultant`, `ImmigrationCase` |
| `DR-06` | CORS defined twice — global config **and** 9 × `@CrossOrigin` | 🟠 Med | ⬜ `OPEN` | `CorsConfig` + controllers |
| `DR-07` | Zero `Pageable` — no pagination on any list or search endpoint | 🟠 Med | ⬜ `OPEN` | backend-wide |
| `DR-09` | Insecure DB-password fallback active when `DB_PASSWORD` is unset | 🟠 Med | ⬜ `OPEN` | `application.properties:15` |
| `DR-01` | `TriggerQuestion` entity specified but never built | 🟡 Low | ⬜ `OPEN` | design deviation — document or build |
| `DR-11` | Entra docs and runbooks contradict the Keycloak stack | — | ⏭ `SUPERSEDED` | resolved by `D-5` |

---

## 5. Section 4.1 — form & package automation (`F41-*`)

**Milestones:** M1 ✅ · M2 ✅ · M3 ✅ · M4 ✅ · manual upload ✅ · M5 🟡 code-complete · M6 🟡 core done

| ID | Item | Status | Note |
| --- | --- | --- | --- |
| `F41-01` | Inspect + classify forms; persist `supportsFill`/`status`/`sourceSha256` | ⬜ `OPEN` | **Correctness gate** — must land before any XFA work |
| `F41-02` | Data-sheet fallback for `BLOCKED` forms | ⬜ `OPEN` | The recommended zero-cost path |
| `F41-03` | Persist `PackageValidationIssue` rows | ⬜ `OPEN` | M5 T1 |
| `F41-04` | `REQUIRED_FORM_NOT_PROVIDED` approval gate | ⬜ `OPEN` | M5 T2 |
| `F41-05` | Suppress validation noise for manually uploaded forms | ⬜ `OPEN` | M5 T3 |
| `F41-06` | Index/manifest across generated + uploaded drafts | ⬜ `OPEN` | M5 T4 |
| `F41-07` | Package zip with size enforcement | ⬜ `OPEN` | M5 T5 |
| `F41-08` | Gated `approvePackage` + acknowledgement + audit | ⬜ `OPEN` | M5 T6 |
| `F41-09` | Issue-resolution endpoints and UI | ⬜ `OPEN` | M5 T7 |
| `F41-10` | Secured package download | ⬜ `OPEN` | M5 T8 |
| `F41-11` | Catalogue UI: fillable vs manual, XFA/blocked | ⬜ `OPEN` | M6 T11 |
| `F41-12` | Admin editors (mapping version, package profile, create form) | ⬜ `OPEN` | backends exist |
| `F41-13` | `FormsPackageWorkspaceComponent` tabbed workspace | ⬜ `OPEN` | M5 T9 |
| `F41-14` | Automated XFA autofill | ⛔ `BLOCKED` | **PDFBox cannot re-save encrypted+certified IRCC forms.** Needs `D-1` |

> **⛔ The `F41-14` blocker, stated plainly.** The 2026-06-29 PoC injected values into IMM 5257's
> `xfa:data` successfully and verified them headlessly — then both outputs failed to open in Adobe.
> IRCC forms are encrypted, certified (DocMDP), and Reader-Extended; PDFBox has open defects writing
> encrypted incremental updates, and a full save breaks certification. **This is a procurement
> question (iText 7 commercial / Aspose.PDF / Qoppa / Adobe AEM), not an engineering one.**

---

## 6. Phase-4 hardening (`SEC-*`)

| ID | Control | Status | Evidence |
| --- | --- | --- | --- |
| `SEC-01` | MFA | ⬜ `OPEN` | — |
| `SEC-02` | Step-up / re-auth on sensitive actions | ⬜ `OPEN` | — |
| `SEC-03` | Refresh-token handling | ⬜ `OPEN` | — |
| `SEC-04` | Secrets to a vault + rotation policy | ⬜ `OPEN` | `.env` today; see `DR-09` |
| `SEC-05` | Consent and branding review | ⬜ `OPEN` | — |
| `SEC-06` | Rate limiting | ⬜ `OPEN` | **zero** implementations found |
| `SEC-07` | Tenant-level authorization | ⬜ `OPEN` | consultant scoping only |
| `SEC-08` | Document authz + signed URLs + malware scan | 🟡 `PARTIAL` | size limit + partial magic bytes; **no AV** |
| `SEC-09` | Security audit event coverage | 🟡 `PARTIAL` | 18 `logAudit` sites; security events absent |
| `SEC-10` | CORS restriction + security headers + HTTPS | 🟡 `PARTIAL` | see `DR-06` |
| `SEC-11` | MCP transport context extractor | ⬜ `OPEN` | verify under load |
| `SEC-12` | MCP tool rate limits and quotas | 🟡 `PARTIAL` | audited, not throttled |
| `SEC-13` | Private networking / mTLS for MCP→backend | ⬜ `OPEN` | — |
| `SEC-14` | Telemetry, dashboards, alerts | ⬜ `OPEN` | — |
| `SEC-15` | Dependency and code scanning in CI | 🟡 `PARTIAL` | gitleaks pre-commit only |
| `SEC-16` | Revocation runbook | ⬜ `OPEN` | Entra runbooks superseded |

**Recommended first wave** (the plan's own): `SEC-01` · `SEC-04` · `SEC-06` · `SEC-14`.

---

## 7. Phase 5 product domains (`GAP-*`)

| ID | Domain | Pri | Status | What exists today |
| --- | --- | --- | --- | --- |
| `GAP-01` | IRCC form & package automation | P0 | 🟡 `PARTIAL` | M1–M4 done; M5/M6 partial → `F41-*` |
| `GAP-02` | Billing, payments, trust accounting | P0 | ⬜ `OPEN` | no invoice/payment/ledger entities |
| `GAP-03` | E-signatures & retainer automation | P0 | ⬜ `OPEN` | retainer date + document path only |
| `GAP-04` | Calendar, booking, deadlines | P1 | ⬜ `OPEN` | case deadline + reminders only |
| `GAP-05` | Unified communications | P1 | ⬜ `OPEN` | reminder drafting + mail config |
| `GAP-06` | Secure client & third-party portal | P1 | 🟡 `PARTIAL` | token portal; see `DR-05` |
| `GAP-07` | Document management, OCR, evidence ops | P1 | 🟡 `PARTIAL` | upload, classify, expiry; no OCR/versions/AV |
| `GAP-08` | Tasks, workflow, collaboration | P1 | ⬜ `OPEN` | checklist items + reminders only |
| `GAP-09` | Rules, forms & knowledge governance | P1 | 🟡 `PARTIAL` | `BL-06` covers checklist templates |
| `GAP-10` | Reporting, analytics, exports | P2 | ⬜ `OPEN` | operational dashboards only |
| `GAP-11` | Lead CRM & conversion | P2 | 🟡 `PARTIAL` | `LeadStatus` on cases |
| `GAP-12` | Integrations, APIs, webhooks | P2 | ⬜ `OPEN` | app + MCP APIs; no outbox/webhooks |
| `GAP-13` | Multi-tenant SaaS & firm admin | P2 | ⬜ `OPEN` | org dashboards + consultant scoping |
| `GAP-14` | Security, privacy, resilience | P0 | 🟡 `PARTIAL` | → `SEC-*` |
| `GAP-15` | Accessibility, localization, mobile | P2 | ⬜ `OPEN` | — |
| `GAP-16` | AI governance & advanced assistance | P2 | 🟡 `PARTIAL` | `BL-07` boundary; no prompt/model versioning or evals |
| `GAP-17` | Onboarding, migration, support | P3 | ⬜ `OPEN` | — |

---

## 8. Codebase inventory

Counted 2026-08-21.

| Metric | Count | Read |
| --- | ---: | --- |
| Backend entities | 22 (+11 forms) | Healthy domain model |
| Backend controllers | 17 | 3 mis-routed (`DR-04`) |
| Backend services | 30 (+8 forms, +5 PDF) | Substantial |
| Angular feature components | 24 across 10 areas | Broad coverage |
| PostgreSQL migrations | V1–V9 | Current |
| MySQL / MSSQL / Oracle migrations | V1–V2 | **Frozen** — decision `D-4` |
| `@PreAuthorize` usages | 22 | Authorization present |
| `@Valid` usages | 22 | Input validation present |
| `logAudit` call sites | 18 | Audit wired |
| `Pageable` usages | **0** | 🔴 `DR-07` |
| Rate-limiting implementations | **0** | 🔴 `SEC-06` |
| Malware-scanning implementations | **0** | 🔴 `SEC-08` |
| **Test files** | **6** | 🔴 `DR-10` — the single largest risk |

---

## 9. Open decisions blocking work

| # | Decision | Blocks | Needed by |
| --- | --- | --- | --- |
| `D-1` | License a commercial XFA engine, or make the data-sheet fallback permanent? | `F41-14` | Phase 1 |
| `D-2` | Single-firm, multi-tenant SaaS, or both? | `GAP-13`, `SEC-07` | Phase 0 |
| `D-3` | Which 3–5 programs are the first commercial target? | `GAP-01` scope, `GAP-09` load | Phase 0 |
| `D-4` | Revive multi-DB parity, or declare PostgreSQL-only? | every schema change | Phase 0 |
| `D-5` | Keycloak or Entra as production identity? | `SEC-01`…`SEC-05` | ✅ **Decided** — Keycloak |
| `D-6` | Which trust-accounting rules apply? | `GAP-02` scope | Phase 2 |

---

## 10. Next actions

1. **Close Phase 0.5 drift** — start with `DR-04`, `DR-08`, `DR-05` (all High, all small).
2. **Build the test foundation** (`DR-10`) — nothing after this is safely refactorable without it.
3. **Answer `D-1`…`D-4`** — four decisions gate Phase 1 and Phase 2 scope.
4. **Ship the security first wave** — `SEC-01`, `SEC-04`, `SEC-06`, `SEC-14`.
5. **Land `F41-01`** — the correctness gate that stops blank "successful" PDFs reaching a consultant.

---

## 11. How to refresh this dashboard

Run the **`status-sync`** skill. It re-inspects the code, recounts the inventory in §8, re-checks each
`VERIFIED` claim, and reports any requirement missing from [Plan.md](Plan.md) or
[change.log.md](change.log.md).

**Do not update a status from a document.** Every ✅ in this file was confirmed by looking at code, and
that is the only thing that makes the dashboard worth reading — the 2026-08-21 audit found 11 open
items inside work that documents had already marked complete.
