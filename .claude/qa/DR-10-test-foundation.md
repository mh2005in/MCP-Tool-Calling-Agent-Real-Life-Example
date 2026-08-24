# Test plan — `DR-10` + `DR-14`, the test foundation

**Requirements:** `DR-10` (backend/MCP coverage), `DR-14` (frontend coverage)
**Phase:** 0.5, slice 7 — [`../plan/phase-0.5-drift.md`](../plan/phase-0.5-drift.md)
**Agent:** [`test-author`](../agents/test-author.md)
**Exit target:** backend above 60 files; frontend above 0

---

## Starting position

| Suite | Files | Covering |
| --- | ---: | --- |
| Backend | **2** | `GlobalExceptionHandlerTest`, `UserProvisioningServiceTest` |
| MCP | **4** | `McpSecurityConfigTest`, `ToolMappingLoaderTest`, `ApiToolExecutorTest`, `TemplateResolverTest` |
| Frontend | **0** | — |
| **Against** | | 37 services · 17 controllers · 31 repositories · 27 components · 121 API calls |

**Nothing in the forms and package domain is tested.** That is 2,218 lines across eight services,
including the approval gate that enforces `BR-2`.

---

## Priority order

Not alphabetical, not by layer. By what breaks worst if it silently regresses.

### 1. Characterisation tests for the nine `F41` items promoted to `VERIFIED`

The 2026-08-24 verification moved `F41-03`…`F41-11` from `OPEN` to `VERIFIED` **on code-reading
alone**. They are the largest body of working, untested, business-critical code in the repository,
and the dashboard now asserts they work.

- [ ] `persistIssues` replaces the prior issue set on refresh and does not orphan rows — `CasePackageService` :249
- [ ] `REQUIRED_FORM_NOT_PROVIDED` is emitted as `ERROR` when a required `PackageProfileForm` has no current draft — `PackageValidationService` :114
- [ ] A form satisfied by `DraftOrigin.UPLOADED` produces **no** field-level issues — :207
- [ ] `buildIndex` lists generated **and** uploaded drafts, recording origin per form
- [ ] `buildZip` includes PDFs + index + manifest + readiness, and **refuses** to exceed `app.forms.max-generated-package-bytes`
- [ ] `approvePackage` throws when `acknowledged = false`
- [ ] `approvePackage` throws when any unresolved `ERROR` remains
- [ ] `approvePackage` succeeds with zero unresolved errors + acknowledgement, sets `APPROVED`, stamps approver and time, writes a `PACKAGE_APPROVED` audit row
- [ ] `approvePackage` on an already-approved package throws
- [ ] `resolveIssue` marks resolved with actor and timestamp; a resolved `ERROR` no longer blocks approval
- [ ] `getPackageForDownload` rebuilds the zip when the file is missing
- [ ] Manifest SHA-256 matches the bytes actually written

### 2. Silent-failure logic guarding the product boundary

These enforce `BR-1` and `BR-5`. They fail quietly by nature — a masking gap produces output that
looks fine.

- [ ] `DataMaskingService` masks every field in `SensitiveFieldRegistry`; an **unregistered** new field is caught by a test that enumerates entity fields against the registry
- [ ] `AiBoundaryService` rejects/rewrites eligibility-conclusion phrasing
- [ ] `DocumentMetadataSanitizer` strips identifying metadata
- [ ] `TravelHistoryService` — `MIN_PR_DAYS=730`, `PRE_PR_CAP_DAYS=365`, including the pre-PR cap boundary
- [ ] `PoliceCertificateService` — 183-day *continuous* stay, 10-year window, age-18 floor
- [ ] `LmiaCalculatorService` returns `preliminaryReviewStatus` and **never** a "requirements met" string
- [ ] `McpApiController` responses cross the masking boundary — a tool response never contains an unmasked sensitive field

### 3. Authorization — highest value on this codebase

Per the [`qa/` README](README.md) template. 26 `@PreAuthorize` annotations and no test proves any of them.

- [ ] Cross-case access returns 404, not 403 (no existence disclosure)
- [ ] Cross-consultant access denied on every nested resource — IDOR sweep over `/v1/cases/{caseId}/**`
- [ ] Admin override works where intended, and **only** there
- [ ] `DisabledUserFilter` cuts off a disabled consultant on the next request
- [ ] `/v1/checklist/client/**` is the only permitted anonymous path besides health and docs
- [ ] Party-portal token access is scoped to its own case (and, after slice 4, expires and revokes)

### 4. Failing-without-the-fix tests, one per drift item

Written as each fix lands (CLAUDE.md §9), not retrofitted.

- [ ] `DR-03` — the three number columns carry no length constraint
- [ ] `DR-04` — each moved controller resolves under `/v1/**` and is authenticated
- [ ] `DR-05` — an expired token is refused; a revoked token is refused; the token is absent from responses
- [ ] `DR-06` — exactly one CORS configuration is active
- [ ] `DR-07` — a list endpoint returns a bounded page and honours `Pageable`
- [ ] `DR-08` — `show-sql` is false under the default profile
- [ ] `DR-09` + `DR-13` — startup fails when `DB_PASSWORD` is unset, in **both** profiles

### 5. Seed integrity

Cheap, and it closes the referential gap [ADR-003](../design/ADR-003-trigger-question-as-flag.md) accepted.

- [ ] Every `ConditionalRule.triggerQuestionKey` resolves to a seeded `IntakeQuestionTemplate.questionKey`, per service type
- [ ] Every `ServiceType` has checklist templates seeded above a floor — this is the test that would have caught `DR-02`
- [ ] Every `ChecklistTemplate` carries `sourceUrl` and `lastReviewedDate` (`BR-3`)

### 6. Frontend (`DR-14`)

- [ ] `api.service` — URL construction from `window.__env`, never a hardcoded host
- [ ] `authGuard` and `adminConsultantGuard` — redirect unauthenticated, block non-admin
- [ ] `package-approval` — approve button disabled while unresolved errors exist
- [ ] `package-approval` — approve payload carries the acknowledgement flag
- [ ] `validation-readiness` — issues grouped by severity, counts match the report
- [ ] `forms-catalogue` — badge reflects `status`/`supportsFill` correctly, including `BLOCKED`
- [ ] `mapping-review` — a form with `supportsFill = false` offers upload, not generate

---

## Integration suite (slower, separate)

Per CLAUDE.md §9, anything needing a live database or network is a separate suite.

- [ ] End-to-end: create package → generate draft → readiness → resolve issues → approve → download → audit rows exist for each transition
- [ ] Changed `sourceSha256` blocks generation (`FORM_SOURCE_HASH_MISMATCH`)
- [ ] Manual upload path: upload → package refresh → no field noise → approval permitted

## Regression fixtures

`backend/src/test/resources/form-fixtures/` — one per onboarded form, as `F41-01` classifies them.

- [ ] Round-trip per fillable form: fill → reopen → values present → file opens
- [ ] A `BLOCKED` form is **never** generated, only offered as data-sheet or manual upload

---

## Ground rules

- **Fast and offline by default.** Mock Keycloak, the database, and third-party APIs. Anything else goes in the integration suite.
- **A bug fix ships with a test that fails without the fix.** Not "a test that covers the area".
- **Never delete or weaken a failing test to make the suite pass.** Fix the cause, or ask.
- **No real PII in fixtures** (CLAUDE.md §8) — `Jane Doe`, `AA000000`, `applicant@example.com`.
- **Characterisation tests record current behaviour, including behaviour that may be wrong.** Where a test pins something that looks like a defect, note it in the test and raise it — do not silently encode a bug as expected.

## Coverage snapshots

Record progress as `coverage-YYYY-MM-DD.md` in this folder. The trend is the guardrail metric for
Phase 0.5 ([Plan.md](../Plan.md) §12: a phase exits when its guardrail is instrumented, not when its
tasks are closed).
