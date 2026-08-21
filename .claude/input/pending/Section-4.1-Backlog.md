# Section 4.1 — Form & Package Automation — Backlog

_Living backlog. This markdown is the source of truth; regenerate `Section-4.1-Backlog.pdf` from it (see `scripts/generate_backlog_pdf.py`). Append new items to the relevant section and re-run the generator._

Last updated: 2026-07-01

## Status snapshot
- M1 Schema & content foundation — COMPLETE
- M2 Canonical data + mapping preview — COMPLETE
- M3 PDF generation prototype (AcroForm) — COMPLETE
- M4 Validation & readiness report — COMPLETE
- Manual filled-form upload (approach change) — COMPLETE
- M5 Package assembly & approval — CODE-COMPLETE
- M6 Admin governance + form inspection — CORE CODE-COMPLETE (a few admin editor UIs deferred)

## 0. Remaining UI / cleanup
- FormsPackageWorkspaceComponent (tabbed workspace) — functionality currently lives as sections in mapping-review; the dedicated tabbed workspace is not built.
- Admin editor UIs deferred: mapping-version field-by-field editor (backend approve exists), package-profile form/document composition editor, create-form UI (backend exists).
- Pre-existing bug: WorkflowController maps `/api/cases` but with context-path=/api resolves to `/api/api/cases`; should be `/v1/cases`. Flagged as a separate task.
- Harden admin authorization is already applied via `@adminGuard.isAdminConsultant()` on the catalogue controller.

## 1. PDF generation — XFA / real-form onboarding
- Real IRCC forms (IMM 0008/5257/5645/5669/5406/5532/1294/1295) are dynamic XFA + certified; PDFBox cannot re-save them into an Adobe-valid file (PoC-confirmed 2026-06-29).
- Interim solution shipped: MANUAL UPLOAD — consultant fills the official form in Adobe and uploads it (origin=UPLOADED draft).
- Deferred: automated XFA autofill (datasets-injection engine + saveIncremental + `xfaDataPath` on FormFieldDefinition/Mapping + M6 inspect dumping the xfa:data skeleton). Needs a licensed save library (iText 7 / Aspose.PDF / Qoppa) or Adobe AEM for a valid official PDF.
- Deferred: DATA_SHEET artifact (a fresh, un-certified mapped-values sheet the consultant transcribes) — prototyped in `pdf-xfa-poc/`.
- Full analysis: `Section-4.1-XFA-Real-PDF-Onboarding-Issue.md`.

## 2. Milestone 5 — package assembly & approval (todos)
- T1: Persist validation issues — convert transient `ValidationIssueDto` to `PackageValidationIssue` rows tied to the `CasePackage` (+ `caseFormDraft` where applicable).
- T2: New approval gate — each REQUIRED `PackageProfileForm` must have a current (non-superseded) draft (GENERATED or UPLOADED), else ERROR `REQUIRED_FORM_NOT_PROVIDED`.
- T3: Suppress form-field validation noise for forms handled by manual upload (skip no-mapping / unmapped / required-empty when an UPLOADED draft exists).
- T4: `CasePackageService.createOrRefreshPackage` — assemble index/manifest including both generated and uploaded drafts, recording origin per form.
- T5: Package zip — bundle generated + uploaded PDFs + index/manifest/readiness; enforce `app.forms.max-generated-package-bytes`.
- T6: `approvePackage` — gated on zero unresolved ERRORs + acknowledgement (incl. manual-form responsibility); status transitions + audit.
- T7: Issue-resolution endpoints/UI for DECISION / CLIENT_CONFIRMATION / UNRESOLVED_EVIDENCE.
- T8: Secured package (zip) download endpoint.
- T9: `FormsPackageWorkspaceComponent` replaces the `forms-package` route; host mapping-review / drafts / package-index / approval as tabs; case-detail entry point + status card.

## 3. Milestone 6 — admin governance + inspection (todos)
- T10: Inspect flow sets `supportsFill` / `status` (BLOCKED) / `supportsBarcode` + XFA classification + `sourceSha256` — drives the manual-upload vs auto-fill UI.
- T11: Form-catalogue UI surfaces fillable-vs-manual + XFA/blocked status.
- Form catalogue list/detail, mapping-version editor, package-profile editor (admin screens).

## 4. Validation & data-model gaps
- `ADDRESS_GAP_DETECTED` (plan §8) not implemented — no address-history entity exists.
- No "certified copy" flag on `Document` — `CERTIFIED_COPY_REQUIRED_UNRESOLVED` is informational only.
- `FORM_SOURCE_HASH_MISMATCH` only enforced at generation, not in the readiness layer.
- Audit detail is a compact JSON string via `CommonService.logAudit`; plan suggested richer structured audit.

## 5. Platform / infra
- Multi-DB parity: MySQL/MSSQL/Oracle frozen at V2 — V3–V9 are PostgreSQL-only. Backfill only if multi-DB is revived.
- Express Entry has no fillable PDF (online-only) — an EE package profile would be document-checklist only.
- Optional (plan §G): expose approved generated files as read-only entries in the Documents tab.

## 6. Testing (plan §9 — not yet written)
- Backend unit: snapshot assembly, transforms, PDF inspect + fill, validation rules, approval blocking, SHA-256, access enforcement.
- Backend integration: create package, generate draft, readiness, approve, download, audit entries, changed-source-hash blocks generation.
- Frontend: profile selection, validation grouping, approval button disabled with errors, approval payload, status badges.
- Regression fixtures under `backend/src/test/resources/form-fixtures/`.

## 7. Verification owed
- Compile M3 / M4 / manual-upload / M5 / M6 backend in Eclipse (Eclipse-only build).
- Apply migrations V6–V9 (and any new) manually against `immiauto_db` (Flyway disabled).
- Run dev profile (`app.forms.seed-sample-pdf=true`) and exercise generate → download → readiness → manual upload → package → approve.
