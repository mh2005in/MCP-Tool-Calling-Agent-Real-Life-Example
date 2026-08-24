# Verification run — 2026-08-24

> **Method:** direct code inspection. Every status below was read out of the source tree, not copied
> from a document. Counts are `find`/`grep` results against `backend/`, `MCPServer/`, and `frontend/`.

**Scope:** full portfolio re-verification ahead of a rewrite of the five root tracking documents.
**Baseline:** the 2026-08-21 run, recorded in [change.log.md](../change.log.md).
**Code delta since then:** none. `git log --since=2026-08-20` shows two commits, both documentation
(`d66d49c`, `7c6890a`). **Every correction below is a reading error in the prior run, not a regression.**

---

## 1. Headline corrections

| # | Prior reading | Verified reading | Why it mattered |
| --- | --- | --- | --- |
| 1 | `F41-03`…`F41-11` are `OPEN` (9 items) | **All nine are delivered** | The register simultaneously called M5 "code-complete" and its tasks "outstanding". The code settles it: they are built |
| 2 | `DR-01` — `TriggerQuestion` "never built" | **Capability delivered**, entity shape differs | A false open item; it also held `BL-03` at `PARTIAL` |
| 3 | 22 entities · 30 services · 22 `@PreAuthorize` | **32 · 37 · 26** | Inventory understated by roughly a third |
| 4 | `DR-10` — "6 test files" | **6 backend+MCP, and 0 frontend** | The frontend gap was invisible because it was never counted |
| 5 | `GAP-*` tallied 6 partial / 11 open | **7 partial / 10 open** | Arithmetic error in the prior dashboard |

---

## 2. Portfolio counts

| Group | Total | Verified | Partial | Open | Blocked | Superseded |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `BL-*` | 12 | 11 | 1 | — | — | — |
| `DR-*` | 15 | — | — | 13 | — | 2 |
| `F41-*` | 14 | 9 | 2 | 2 | 1 | — |
| `SEC-*` | 16 | — | 5 | 11 | — | — |
| `GAP-*` | 17 | — | 7 | 10 | — | — |
| **Total** | **74** | **20** | **15** | **36** | **1** | **2** |

Four requirements are new this run: `DR-12`…`DR-15` (§5).

---

## 3. Codebase inventory

| Metric | 2026-08-21 | 2026-08-24 | Note |
| --- | ---: | ---: | --- |
| Backend entities | 22 (+11) | **32** | 21 in `entity/` + 11 in `entity/forms/` |
| Backend controllers | 17 | **17** | unchanged |
| Backend services | 30 (+8, +5) | **37** | 27 `service/` + 8 `service/forms/` + 2 `security/` |
| Backend repositories | — | **31** | not previously counted |
| MapStruct mappers | — | **17** | not previously counted |
| DTOs | — | **45** | not previously counted |
| Enums | — | **17** | not previously counted |
| PDF engine classes | 5 | **5** | `PdfFormEngine` SPI + PDFBox impl + 3 result types |
| Angular components | 24 / 10 areas | **27** | includes `app.component` and 2 auth components |
| Angular services | — | **2** | `api.service`, `auth.service` — 121 HTTP calls |
| PostgreSQL migrations | V1–V9 | **V1–V9** | unchanged |
| MySQL / MSSQL / Oracle | V1–V2 | **V1–V2** | still frozen |
| MCP server classes | — | **11** | 14 tools in `tools.json` |
| `@PreAuthorize` | 22 | **26** | |
| `@Valid` | 22 | **22** | |
| `logAudit` call sites | 18 | **18** | |
| `@CrossOrigin` | 9 | **9** | duplicates the global `CorsConfig` |
| `Pageable` usages | 0 | **0** | |
| Rate-limiting implementations | 0 | **0** | no bucket4j or resilience4j on either classpath |
| Malware-scan implementations | 0 | **0** | |
| Backend test files | 2 | **2** | |
| MCP test files | 4 | **4** | |
| **Frontend spec files** | not counted | **0** | new finding — `DR-14` |

---

## 4. Evidence for the `F41` correction

Each item below was `OPEN` in the prior dashboard. The evidence is the code that implements it.

| ID | Requirement | Evidence |
| --- | --- | --- |
| `F41-03` | Persist `PackageValidationIssue` rows | `entity/forms/PackageValidationIssue.java`; `repository/forms/PackageValidationIssueRepository.java`; `CasePackageService.persistIssues()` :249; `buildReadinessFromPersisted()` :351 |
| `F41-04` | `REQUIRED_FORM_NOT_PROVIDED` approval gate | `PackageValidationService` :114 — emits `ValidationSeverity.ERROR` |
| `F41-05` | Suppress field noise for uploaded forms | `PackageValidationService` :207 — `current.getOrigin() == DraftOrigin.UPLOADED` |
| `F41-06` | Index/manifest across generated + uploaded | `CasePackageService.buildIndex()` :204, `currentDrafts()` :337, `manifestEntry()` :440 |
| `F41-07` | Package zip + size enforcement | `CasePackageService.buildZip()` :272; `app.forms.max-generated-package-bytes` = 104857600 |
| `F41-08` | Gated approve + acknowledgement + audit | `CasePackageService.approvePackage()` :157 — blocks on `countByCasePackageIdAndSeverityAndResolvedFalse(ERROR)`, requires `acknowledged`, audits `PACKAGE_APPROVED` |
| `F41-09` | Issue-resolution endpoints and UI | `CasePackageService.resolveIssue()` :135; `POST /packages/{id}/issues/{issueId}/resolve`; `package-approval.component.ts` `resolve()` :70 |
| `F41-10` | Secured package download | `CasePackageService.getPackageForDownload()` :189; `GET /packages/{id}/download` under `@PreAuthorize("@consultantAccess.canAccessCase(#caseId)")` |
| `F41-11` | Catalogue UI: fillable vs manual | `forms-catalogue.component.ts` :95-103 — `Blocked (manual upload)` / `Auto-fill` badges; routed at `/consultant/:id/form-catalogue` behind `adminConsultantGuard` |

### Still genuinely incomplete

| ID | Status | What is missing |
| --- | --- | --- |
| `F41-01` | `PARTIAL` | `FormDefinition` carries `supportsFill`, `supportsBarcode`, `sourceSha256`, `status`; `PdfBoxFormEngine` :38 detects `hasXfa` and inspection sets `BLOCKED`. **Missing:** the four-way technology classification (`STANDARD_ACROFORM`/`STATIC_XFA`/`DYNAMIC_XFA`/`NONE`) — no such enum exists — and barcode detection never sets `supportsBarcode` |
| `F41-02` | `OPEN` | No data-sheet fallback renderer for `BLOCKED` forms |
| `F41-12` | `OPEN` | Backends exist (`FormCatalogueController` exposes mapping approval and package-profile CRUD); no Angular editors — `forms-catalogue.component.ts` has no mapping, profile, or create-form UI |
| `F41-13` | `PARTIAL` | Functionality is reachable: `MappingReviewComponent` imports and embeds `ValidationReadinessComponent` and `PackageApprovalComponent`. **Missing:** the dedicated tabbed `FormsPackageWorkspaceComponent` and a case-detail entry point / status card |
| `F41-14` | `BLOCKED` | Unchanged — awaits decision `D-1` |

---

## 5. New findings

| ID | Finding | Evidence | Severity |
| --- | --- | --- | --- |
| `DR-12` | `application.properties` :68-70 still documents per-consultant MCP API keys (`format: mcp_<uuid>`). `V5__drop_consultant_mcp_api_key.sql` removed that column; the comment describes a schema that no longer exists | `backend/src/main/resources/application.properties` | Low — stale, but it is security-adjacent documentation |
| `DR-13` | The insecure DB-password fallback exists in **two** profiles, not one. `DR-09` named only the main profile | `application.properties` :15 **and** `application-dev.properties` :5 | Medium — a fix touching one file leaves the hole open |
| `DR-14` | Zero frontend test files. `DR-10` counted backend and MCP only, so the frontend gap was invisible | `find frontend/src -name "*.spec.ts"` returns 0 | High |
| `DR-15` | `app.routes.ts` comments the `forms-package` route "Replaced by the full `FormsPackageWorkspaceComponent` in Milestone 5". That component does not exist | `frontend/src/app/app.routes.ts` | Low — a false statement in code; feeds `F41-13` |

---

## 6. Re-confirmed as still open

Verified individually; no change from the prior run.

| ID | Check performed | Result |
| --- | --- | --- |
| `DR-02` | Seed references per `ServiceType` in `V2__seed_data.sql` | PNP = **3**; every other type 29–61 — STUDY_PERMIT 61, SPOUSAL_SPONSORSHIP 53, LMIA 49, EXPRESS_ENTRY 47, WORK_PERMIT 43, VISITOR_VISA 43, SUPER_VISA 35, CITIZENSHIP 34, PR_CARD_PRTD 29, PGWP 29 |
| `DR-03` | `length = 15` on the three number columns | Present — `Client` :25, `Consultant` :24, `ImmigrationCase` :26 |
| `DR-04` | `@RequestMapping("/api…")` under `context-path=/api` | `AutomationController` :16, `PartyPortalController` :14, `WorkflowController` :17 |
| `DR-05` | Expiry / revocation fields on `PartyProfile.accessToken` | None — only `accessToken` and `portalEnabled` |
| `DR-06` | `@CrossOrigin` count alongside `CorsConfig` | 9 |
| `DR-07` | `Pageable` usages | 0 |
| `DR-08` | `show-sql` / `format_sql` in the main profile | `application.properties` :21-22, both `true` |
| `DR-09` | DB-password fallback | :15 — and see `DR-13`, it is in the dev profile too |
| `SEC-06` | Rate limiting | 0 implementations; no bucket4j or resilience4j dependency |
| `SEC-08` | Malware scanning | 0 implementations |
| `SEC-10` | Security headers | `SecurityConfig` configures no HSTS and no CSP; CSRF disabled :64 |

### `DR-01` — closed as an accepted deviation

The specification asked for a `TriggerQuestion` entity. The capability is delivered without one:

- `IntakeQuestionTemplate.isTriggerQuestion` :40 — the flag
- `ConditionalRule.triggerQuestionKey` :19 — the rule's reference to it
- `IntakeQuestionTemplateRepository.findByServiceTypeAndIsTriggerQuestionTrueOrderBySortOrder` :15
- `ConditionalRuleRepository.findByServiceTypeAndTriggerQuestionKeyAndActiveTrue` :14
- `ChecklistGeneratorService` :46, :111 — resolves rules by trigger key and evaluates the answer
- Seeded by `ConditionalRuleSeeder` :153 and `IntakeQuestionSeeder` :330

**Ruling:** functionally equivalent to the specified entity and simpler. Recorded as
[ADR-003](../design/ADR-003-trigger-question-as-flag.md). `BL-03` moves to `VERIFIED`.

---

## 7. What this run did not verify

Stated so the next reader knows the edges of the evidence.

- **No build or test run.** File counts and code reading only; nothing was compiled and the stack was not brought up.
- **No runtime behaviour.** `DR-04`'s broken routes are inferred from annotations plus `context-path`, not from a failing request.
- **Seed-data quality** was measured by reference count per service type, which detects an empty seed but not a wrong one.
- **Frontend coverage** was assessed by spec-file count alone.
- **The `F41` items marked delivered are code-verified, not behaviour-verified.** They have no tests (`DR-10`), so "the code implements it" is the strongest claim available. That is precisely why `DR-10` gates the phase.
