# Section 4.1 Implementation Plan - IRCC Form and Submission-Package Automation

Assessment date: June 26, 2026

Source requirement: Section 4.1 of `Phase 5 Immigration-Consultation-Missing-Features-and-Recommendations.pdf`

Target stack:

- Frontend: Angular 18 standalone components, Angular Router, reactive forms, existing `ApiService` and `API_ENDPOINTS`.
- Backend: Spring Boot 3.3, Java 21, Spring Security, JPA/Hibernate, MapStruct, Flyway-style SQL migrations.
- Database: PostgreSQL first, with MySQL, SQL Server, and Oracle migration parity if this project continues to support all database folders.
- Storage: existing local upload folder pattern through `DocumentService`, extended for generated form/package artifacts.

## 1. Goal

Implement a governed workflow that lets a consultant select an immigration case, review mapped applicant data, generate supported IRCC/provincial PDF forms, validate form and package readiness, assemble a submission package index, and approve a final generated package with a complete audit trail.

The first release should not attempt automatic portal submission. It should produce consultant-approved, traceable, version-linked files that can be manually uploaded to the official portal.

## 2. Current application fit

The application already has useful foundation pieces:

- `Client`, `ImmigrationCase`, `IntakeResponse`, `Document`, `ChecklistItem`, `TravelHistoryEntry`, `WorkHistoryEntry`, `RelationshipTimeline`, `RecruitmentEvidence`, and `AuditLog` entities.
- Case-level consultant access enforcement through `@consultantAccess.canAccessCase(#caseId)`.
- Intake question and response storage keyed by `questionKey`.
- Document upload/review and standardized naming fields.
- Final case sign-off fields on `ImmigrationCase`.
- Angular routes for case detail, intake, workflows, documents, templates, and checklist views.

The missing layer is a formal form-automation domain:

- versioned form catalogue;
- field mappings from canonical applicant/case data to official PDF fields;
- PDF field introspection and population;
- deterministic validation results;
- generated artifact storage;
- approval and audit records tied to exact form versions, mapping versions, input snapshots, and output hashes.

## 3. Recommended MVP scope

Start with 3 to 5 high-volume Canadian workflows. Suggested first candidates:

1. Visitor visa / temporary resident visa support forms.
2. Study permit support forms.
3. Work permit support forms.
4. Express Entry profile/document package support.
5. Spousal sponsorship package support.

For MVP, choose exact official forms only after a consultant confirms the firm's highest-volume programs. Each supported form must be treated as governed content with source URL, edition date, checksum, active/retired status, and mappings.

MVP user outcome:

> A consultant opens a case, goes to "Forms & Package", selects a supported package profile, reviews mapped values, resolves validation issues, generates draft PDFs and package index, then explicitly approves a final package. The system stores generated artifacts, hashes, versions, and approver details.

## 4. Domain model

Add the following backend entities and tables.

### 4.1 `FormDefinition`

Represents a governed official form file/version.

Fields:

- `id`
- `formCode` - example: `IMM_XXXX`.
- `displayName`
- `jurisdiction` - `FEDERAL`, `ONTARIO`, etc.
- `programCategory` - visitor, study, work, family, PR, citizenship, PNP, etc.
- `sourceUrl`
- `sourceFileName`
- `sourceSha256`
- `effectiveDate`
- `retirementDate`
- `editionLabel`
- `supportsFill`
- `supportsBarcode`
- `status` - `DRAFT`, `ACTIVE`, `RETIRED`, `BLOCKED`
- `notes`
- inherited audit columns from `BaseEntity`

Indexes:

- unique `(formCode, editionLabel, sourceSha256)`
- `(programCategory, status)`
- `(effectiveDate, retirementDate)`

### 4.2 `FormFieldDefinition`

Represents fields discovered inside a PDF form.

Fields:

- `id`
- `formDefinition`
- `pdfFieldName`
- `label`
- `fieldType` - text, checkbox, radio, date, dropdown, signature, barcode, unsupported
- `required`
- `maxLength`
- `allowedValues`
- `pageNumber`
- `readOnly`
- `calculated`
- `notes`

Indexes:

- unique `(form_definition_id, pdfFieldName)`

### 4.3 `CanonicalDataField`

Represents reusable normalized application data, independent of any single form.

Fields:

- `id`
- `fieldKey` - example: `primaryApplicant.passport.number`.
- `displayName`
- `category` - person, address, education, employment, travel, family, immigration history, case, document.
- `dataType` - string, date, boolean, number, country, enum, list.
- `sourcePriority` - JSON/text list describing where the value comes from.
- `sensitive`
- `description`
- `active`

Examples:

- `primaryApplicant.fullName`
- `primaryApplicant.dateOfBirth`
- `primaryApplicant.passport.number`
- `primaryApplicant.passport.expiryDate`
- `primaryApplicant.currentAddress.country`
- `case.serviceType`
- `travelHistory.entries`
- `workHistory.entries`

### 4.4 `FormMappingVersion`

Represents a versioned mapping set for one form definition.

Fields:

- `id`
- `formDefinition`
- `mappingVersion`
- `status` - `DRAFT`, `IN_REVIEW`, `APPROVED`, `RETIRED`
- `approvedByConsultantId`
- `approvedByConsultantName`
- `approvedAt`
- `changeSummary`
- `regressionFixturePath`
- `notes`

Unique:

- `(form_definition_id, mappingVersion)`

### 4.5 `FormFieldMapping`

Maps canonical data to a PDF field.

Fields:

- `id`
- `mappingVersion`
- `formFieldDefinition`
- `canonicalFieldKey`
- `transformType` - direct, date format, concat, split name, checkbox boolean, enum map, list row, custom.
- `transformConfig` - JSON/text.
- `defaultValue`
- `requiredForPackage`
- `consultantReviewRequired`
- `notes`

### 4.6 `PackageProfile`

Represents a supported application package for a case type/program.

Fields:

- `id`
- `profileCode`
- `displayName`
- `serviceType`
- `caseSubtype`
- `jurisdiction`
- `status` - `DRAFT`, `ACTIVE`, `RETIRED`
- `description`
- `effectiveDate`
- `retirementDate`

### 4.7 `PackageProfileForm`

Connects a package profile to required forms.

Fields:

- `id`
- `packageProfile`
- `formDefinition`
- `required`
- `sortOrder`
- `conditionalExpression` - optional JSON/text rule
- `notes`

### 4.8 `PackageDocumentRequirement`

Connects package profiles to existing document/checklist concepts.

Fields:

- `id`
- `packageProfile`
- `documentCategory`
- `documentType`
- `required`
- `sortOrder`
- `namingPattern`
- `maxSizeBytes`
- `translationRule`
- `certifiedCopyRule`
- `notes`

### 4.9 `CaseFormDraft`

Represents generated draft data for a specific case/form/mapping version.

Fields:

- `id`
- `immigrationCase`
- `formDefinition`
- `mappingVersion`
- `status` - `DRAFT`, `VALIDATION_FAILED`, `READY_FOR_APPROVAL`, `APPROVED`, `SUPERSEDED`
- `inputSnapshotJson`
- `mappedValuesJson`
- `validationSummaryJson`
- `draftFilePath`
- `draftSha256`
- `generatedAt`
- `generatedBy`
- `approvedAt`
- `approvedBy`
- `approvalNotes`

### 4.10 `CasePackage`

Represents a generated package for one case.

Fields:

- `id`
- `immigrationCase`
- `packageProfile`
- `status` - `DRAFT`, `VALIDATION_FAILED`, `READY_FOR_APPROVAL`, `APPROVED`, `SUPERSEDED`
- `packageIndexJson`
- `readinessReportJson`
- `packageManifestPath`
- `packageZipPath`
- `packageSha256`
- `generatedAt`
- `generatedBy`
- `approvedAt`
- `approvedBy`
- `approvalNotes`

### 4.11 `PackageValidationIssue`

Stores deterministic validation results.

Fields:

- `id`
- `casePackage`
- `caseFormDraft` - nullable, for package-level issues
- `severity` - `ERROR`, `WARNING`, `DECISION`, `CLIENT_CONFIRMATION`, `UNRESOLVED_EVIDENCE`
- `code`
- `message`
- `fieldKey`
- `pdfFieldName`
- `sourceType` - client, intake, document, checklist, travel history, work history, package.
- `sourceId`
- `resolved`
- `resolvedBy`
- `resolvedAt`
- `resolutionNotes`

## 5. Backend implementation plan

### Phase A - Foundation and schema

1. Add enums:
   - `FormStatus`
   - `MappingStatus`
   - `PackageStatus`
   - `ValidationSeverity`
   - `PdfFieldType`
   - `TransformType`
   - `Jurisdiction`

2. Add JPA entities for:
   - `FormDefinition`
   - `FormFieldDefinition`
   - `CanonicalDataField`
   - `FormMappingVersion`
   - `FormFieldMapping`
   - `PackageProfile`
   - `PackageProfileForm`
   - `PackageDocumentRequirement`
   - `CaseFormDraft`
   - `CasePackage`
   - `PackageValidationIssue`

3. Add repositories:
   - `FormDefinitionRepository`
   - `FormFieldDefinitionRepository`
   - `CanonicalDataFieldRepository`
   - `FormMappingVersionRepository`
   - `FormFieldMappingRepository`
   - `PackageProfileRepository`
   - `PackageProfileFormRepository`
   - `PackageDocumentRequirementRepository`
   - `CaseFormDraftRepository`
   - `CasePackageRepository`
   - `PackageValidationIssueRepository`

4. Add SQL migration `V6__form_package_automation.sql` for PostgreSQL.

5. If multi-database support remains required, mirror the same migration under:
   - `backend/src/main/resources/db/migration/mysql`
   - `backend/src/main/resources/db/migration/mssql`
   - `backend/src/main/resources/db/migration/oracle`

6. Add basic seed data:
   - canonical fields;
   - 1 package profile;
   - 1 to 2 sample form definitions;
   - field definitions and mappings for a test/support form.

Deliverable: Application starts with the new schema, content is visible through backend queries, and no current flows are broken.

### Phase B - Canonical applicant data service

Create `CanonicalApplicantDataService`.

Responsibilities:

- Build a normalized case data snapshot from:
  - `Client`
  - `ImmigrationCase`
  - `IntakeResponse`
  - `TravelHistoryEntry`
  - `WorkHistoryEntry`
  - `RelationshipTimeline`
  - `RecruitmentEvidence`
  - `Document`
  - `ChecklistItem`
- Return values by canonical key.
- Preserve provenance for every value.
- Identify missing, conflicting, stale, or consultant-review-required data.

Core DTOs:

- `CanonicalDataSnapshotDto`
- `CanonicalValueDto`
- `CanonicalValueSourceDto`
- `CanonicalDataConflictDto`

Recommended behavior:

- Prefer structured entities over free-text intake responses.
- Use intake responses as fallback where structured fields do not yet exist.
- Record all source candidates, not only the selected value.
- Normalize dates to ISO `yyyy-MM-dd`.
- Normalize country names through a fixed enum/list later; for MVP, validate presence and consistent spelling.

### Phase C - PDF engine integration

Add a PDF form engine behind an interface so the implementation can be changed later.

Interface:

```java
public interface PdfFormEngine {
    PdfInspectionResult inspect(Path sourcePdf);
    PdfFillResult fill(Path sourcePdf, Map<String, String> values, Path outputPdf);
    String sha256(Path file);
}
```

Recommended library path:

- Add Apache PDFBox dependency for PDF field inspection and AcroForm filling.
- Keep the engine isolated in `com.immiauto.service.forms.pdf`.
- Do not scatter PDFBox code across controllers or general services.

Maven dependency candidate:

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.3</version>
</dependency>
```

Important constraints:

- Some official forms may use dynamic XFA/barcode behavior that PDFBox cannot fully preserve.
- For unsupported forms, mark `supportsFill=false` or `status=BLOCKED`.
- Add detection for changed field names and source hash mismatch.
- Store the source PDF exactly as inspected; mappings must point to that exact checksum.

### Phase D - Mapping and transformation service

Create `FormMappingService`.

Responsibilities:

- Load approved mapping version for a form.
- Apply `FormFieldMapping` rules to a canonical snapshot.
- Produce mapped PDF values plus mapping diagnostics.
- Support deterministic transforms:
  - direct value;
  - date formatting;
  - boolean to checkbox value;
  - enum mapping;
  - name split;
  - concatenation;
  - list row mapping for travel/work history tables;
  - default value.

Do not allow arbitrary code transforms in the database for MVP. If custom transforms are needed, implement named Java transforms and reference them by key.

### Phase E - Validation engine

Create `PackageValidationService`.

Validation layers:

1. Canonical data validation:
   - required field missing;
   - invalid date order;
   - passport expired before expected submission;
   - inconsistent name/date/passport details across client, intake, and documents;
   - address gaps;
   - travel or employment gaps.

2. Form validation:
   - required PDF field missing;
   - value exceeds max length;
   - unsupported field type;
   - mapping references unknown canonical field;
   - source form hash does not match mapping version.

3. Package validation:
   - missing required checklist/document items;
   - document rejected or pending review;
   - translation or notarization requirements unresolved;
   - package size or naming rule issue;
   - consultant decisions not recorded.

Issue categories should match section 4.1:

- errors;
- warnings;
- consultant decisions;
- client confirmations;
- unresolved evidence.

Approval must be blocked while any `ERROR` remains unresolved.

### Phase F - Case form generation service

Create `CaseFormGenerationService`.

Core methods:

- `previewMappings(caseId, packageProfileId)`
- `generateDraftForms(caseId, packageProfileId)`
- `regenerateDraftForm(caseId, formDefinitionId)`
- `getFormDraft(caseId, draftId)`
- `approveFormDraft(caseId, draftId, notes)`

Behavior:

- Capture `inputSnapshotJson` before generating.
- Capture `mappedValuesJson`.
- Generate draft PDF into a separate generated artifacts folder, e.g. `uploads/{caseNumber}/generated/forms`.
- Calculate output SHA-256.
- Create audit records:
  - `FORM_DRAFT_GENERATED`
  - `FORM_DRAFT_VALIDATED`
  - `FORM_DRAFT_APPROVED`
  - `FORM_DRAFT_SUPERSEDED`

### Phase G - Package assembly service

Create `CasePackageService`.

Core methods:

- `listAvailableProfiles(caseId)`
- `createOrRefreshPackage(caseId, packageProfileId)`
- `getPackage(caseId, packageId)`
- `getReadinessReport(caseId, packageId)`
- `getPackageIndex(caseId, packageId)`
- `approvePackage(caseId, packageId, notes)`
- `downloadPackageZip(caseId, packageId)`

Package contents:

- generated form PDFs;
- package index PDF or JSON/HTML initially;
- document manifest;
- references to uploaded supporting documents;
- readiness report.

Use existing `Document` records for uploaded evidence. For generated outputs, either:

- extend `Document` with `generatedArtifact=true`, `artifactType`, `sha256`, `generatedFromPackageId`, or
- keep generated files in `CaseFormDraft`/`CasePackage` only.

Recommendation: keep generated form/package records separate from user-uploaded evidence for MVP, then optionally expose approved generated files as read-only documents in the document tab.

### Phase H - REST API

Add `FormAutomationController` under the same security pattern as `WorkflowController`.

Suggested paths:

- `GET /api/cases/{caseId}/form-automation/profiles`
- `POST /api/cases/{caseId}/form-automation/packages`
- `GET /api/cases/{caseId}/form-automation/packages`
- `GET /api/cases/{caseId}/form-automation/packages/{packageId}`
- `POST /api/cases/{caseId}/form-automation/packages/{packageId}/refresh`
- `GET /api/cases/{caseId}/form-automation/packages/{packageId}/readiness`
- `GET /api/cases/{caseId}/form-automation/packages/{packageId}/index`
- `POST /api/cases/{caseId}/form-automation/packages/{packageId}/approve`
- `GET /api/cases/{caseId}/form-automation/packages/{packageId}/download`
- `GET /api/cases/{caseId}/form-automation/packages/{packageId}/drafts`
- `GET /api/cases/{caseId}/form-automation/drafts/{draftId}`
- `POST /api/cases/{caseId}/form-automation/drafts/{draftId}/approve`

Admin/content APIs:

- `GET /api/forms`
- `POST /api/forms`
- `GET /api/forms/{formId}`
- `POST /api/forms/{formId}/inspect`
- `GET /api/forms/{formId}/fields`
- `GET /api/forms/{formId}/mappings`
- `POST /api/forms/{formId}/mappings`
- `POST /api/forms/{formId}/mappings/{mappingVersionId}/approve`
- `GET /api/package-profiles`
- `POST /api/package-profiles`
- `PUT /api/package-profiles/{profileId}`

Security:

- case endpoints: `@PreAuthorize("@consultantAccess.canAccessCase(#caseId)")`
- content governance endpoints: admin or approved consultant only, likely `adminConsultantGuard` equivalent on frontend and `@PreAuthorize("hasAnyRole('ADMIN','CONSULTANT')")` plus stricter approval checks on backend.

### Phase I - Audit and approval

Use `CommonService.logAudit(...)` if sufficient; otherwise extend it to support structured JSON details.

Audit records must include:

- case ID;
- package ID;
- form draft ID;
- form code;
- form source hash;
- mapping version;
- input snapshot hash;
- output file hash;
- validation status;
- approving consultant;
- timestamp;
- notes.

Approval rules:

- Draft generation does not equal approval.
- Final package download should show a clear approved/unapproved status.
- Consultant approval requires:
  - no unresolved errors;
  - explicit acknowledgement that official instructions remain consultant responsibility;
  - package profile version and form versions locked.

## 6. Angular implementation plan

### Phase A - Models and API endpoints

Add frontend models:

- `form-automation.model.ts`
  - `PackageProfile`
  - `CasePackage`
  - `CaseFormDraft`
  - `PackageValidationIssue`
  - `PackageReadinessReport`
  - `PackageIndex`
  - `MappedFieldPreview`
  - `CanonicalValue`

Add endpoints in `frontend/src/app/core/constants/api-endpoints.ts`:

- `FORM_PROFILES(caseId)`
- `FORM_PACKAGES(caseId)`
- `FORM_PACKAGE(caseId, packageId)`
- `FORM_PACKAGE_REFRESH(caseId, packageId)`
- `FORM_PACKAGE_READINESS(caseId, packageId)`
- `FORM_PACKAGE_INDEX(caseId, packageId)`
- `FORM_PACKAGE_APPROVE(caseId, packageId)`
- `FORM_PACKAGE_DOWNLOAD(caseId, packageId)`
- `FORM_DRAFTS(caseId, packageId)`
- `FORM_DRAFT(caseId, draftId)`
- `FORM_DRAFT_APPROVE(caseId, draftId)`

Add admin/content endpoints later for form catalogue and mappings.

### Phase B - Route structure

Add a route under consultant case context:

```ts
{
  path: 'consultant/:consultantId/cases/:id/forms-package',
  canActivate: [MsalGuard],
  loadComponent: () => import('./features/forms-package/forms-package-workspace/forms-package-workspace.component')
    .then(m => m.FormsPackageWorkspaceComponent)
}
```

Add optional admin route:

```ts
{
  path: 'consultant/:consultantId/form-catalogue',
  canActivate: [MsalGuard, adminConsultantGuard],
  loadComponent: () => import('./features/forms-catalogue/forms-catalogue.component')
    .then(m => m.FormsCatalogueComponent)
}
```

### Phase C - Main workspace components

Create feature folder:

`frontend/src/app/features/forms-package`

Components:

1. `forms-package-workspace`
   - parent page;
   - loads case ID and package profiles;
   - shows current package status.

2. `package-profile-selector`
   - choose supported program/package;
   - shows included forms and required evidence.

3. `mapping-review`
   - shows mapped fields grouped by form and section;
   - displays value, source, confidence/status, and review-needed markers;
   - allows consultant to open the source intake/document/work history record.

4. `validation-readiness`
   - grouped issue list:
     - errors;
     - warnings;
     - consultant decisions;
     - client confirmations;
     - unresolved evidence.
   - filters by form/document/severity.

5. `form-draft-list`
   - generated forms;
   - status badges;
   - source form version;
   - mapping version;
   - download/preview draft.

6. `package-index`
   - ordered list of forms and documents;
   - naming convention;
   - size/translation/certified-copy notes;
   - missing document indicators.

7. `approval-panel`
   - final acknowledgement checkbox;
   - notes box;
   - approve button disabled until blocking issues resolved.

### Phase D - UI workflow

Recommended screen flow:

1. Select package profile.
2. Generate or refresh draft package.
3. Review mapped values.
4. Resolve validation issues.
5. Review package index.
6. Generate draft forms.
7. Approve final package.
8. Download approved package.

Status labels:

- Not started
- Draft generated
- Validation failed
- Needs consultant review
- Ready for approval
- Approved
- Superseded

Use strong visual warnings:

- "Draft - not approved for filing"
- "Official portal submission is not automated"
- "Consultant remains responsible for final review"

### Phase E - Case detail integration

Update the case detail page to include a "Forms & Package" entry point.

Suggested additions:

- action button near existing workflow actions;
- package status card:
  - selected package profile;
  - errors/warnings count;
  - last generated date;
  - approved/not approved;
  - approved by.

### Phase F - Admin form catalogue UI

For MVP, catalogue management can begin as seed data plus backend endpoints. Once stable, add Angular admin screens:

1. Form catalogue list:
   - form code;
   - edition;
   - status;
   - source hash;
   - effective/retired dates.

2. Form detail:
   - source metadata;
   - discovered fields;
   - supported/unsupported flags.

3. Mapping version editor:
   - field-by-field mapping;
   - transform selection;
   - test with a sample case;
   - submit for approval.

4. Package profile editor:
   - included forms;
   - required documents;
   - ordering and naming rules.

## 7. File generation and storage design

Add properties:

```properties
app.generated.dir=./uploads/generated
app.forms.source-dir=./forms/source
app.forms.max-generated-package-bytes=104857600
```

Recommended structure:

```text
uploads/
  {caseNumber}/
    uploaded/
    generated/
      forms/
        {packageId}/
          {formCode}_{edition}_{draftId}.pdf
      packages/
        {packageId}/
          package-index.json
          readiness-report.json
          package-manifest.json
          submission-package.zip
forms/
  source/
    {formCode}/
      {edition}/
        source.pdf
        source.sha256
```

Security requirements:

- generated files must not be served by static public paths;
- downloads must go through authenticated controllers;
- validate case access before returning a file;
- avoid leaking local file paths in API responses;
- store hashes for generated outputs.

## 8. Validation rule examples

Initial deterministic rules:

- `PRIMARY_NAME_REQUIRED`
- `DOB_REQUIRED`
- `PASSPORT_NUMBER_REQUIRED`
- `PASSPORT_EXPIRY_REQUIRED`
- `PASSPORT_EXPIRED`
- `DATE_ORDER_INVALID`
- `ADDRESS_GAP_DETECTED`
- `WORK_HISTORY_GAP_DETECTED`
- `TRAVEL_EXIT_BEFORE_ENTRY`
- `DOCUMENT_REQUIRED_MISSING`
- `DOCUMENT_PENDING_REVIEW`
- `DOCUMENT_REJECTED`
- `TRANSLATION_REQUIRED_UNRESOLVED`
- `CERTIFIED_COPY_REQUIRED_UNRESOLVED`
- `FORM_SOURCE_HASH_MISMATCH`
- `PDF_FIELD_UNMAPPED`
- `PDF_REQUIRED_FIELD_EMPTY`
- `PDF_VALUE_TOO_LONG`
- `CONSULTANT_DECISION_REQUIRED`

## 9. Testing strategy

### Backend unit tests

Add tests for:

- canonical snapshot assembly;
- source priority selection;
- conflict detection;
- mapping transforms;
- PDF field inspection;
- PDF population with a sample AcroForm fixture;
- validation rules;
- approval blocking;
- SHA-256 generation;
- access enforcement.

### Backend integration tests

Add tests for:

- create package;
- generate draft;
- readiness report;
- approve package;
- download package;
- audit entries written;
- changed source hash blocks generation.

### Frontend tests

Add tests for:

- package profile selection;
- validation grouping;
- approval button disabled with errors;
- approval payload;
- status badge rendering.

### Regression fixtures

Create a test fixture folder:

```text
backend/src/test/resources/form-fixtures/
  simple-acroform.pdf
  simple-acroform-fields.json
  sample-case-canonical-snapshot.json
  expected-mapped-values.json
```

Do not put real client documents or confidential official package examples in test fixtures.

## 10. Delivery milestones

### Milestone 1 - Schema and content foundation

Estimated effort: 1 to 2 weeks

Deliverables:

- new entities, repositories, migrations;
- canonical data field seed;
- form/package seed for one pilot flow;
- backend starts cleanly;
- smoke tests pass.

### Milestone 2 - Canonical data and mapping preview

Estimated effort: 2 weeks

Deliverables:

- canonical snapshot API;
- mapping service;
- mapping preview endpoint;
- Angular mapping review screen;
- provenance shown for mapped values.

### Milestone 3 - PDF generation prototype

Estimated effort: 2 to 3 weeks

Deliverables:

- PDFBox integration;
- inspect source PDF fields;
- generate draft PDF for pilot form;
- hash and store generated file;
- download draft through secured endpoint.

### Milestone 4 - Validation and readiness report

Estimated effort: 2 weeks

Deliverables:

- validation issue model;
- validation service;
- readiness report API;
- Angular issue grouping and filters;
- approval blocked by errors.

### Milestone 5 - Package assembly and approval

Estimated effort: 2 to 3 weeks

Deliverables:

- package index;
- package manifest;
- package zip generation;
- consultant sign-off;
- audit records;
- approved package download.

### Milestone 6 - Admin governance screens

Estimated effort: 2 to 4 weeks

Deliverables:

- form catalogue admin UI;
- mapping version admin UI;
- package profile admin UI;
- approval workflow for mappings/content.

Total MVP estimate: 11 to 16 weeks, depending on how many official forms are included and whether the selected PDFs are straightforward AcroForms or require special handling.

## 11. Suggested implementation order in this repository

1. Create backend packages:
   - `com.immiauto.entity.forms`
   - `com.immiauto.dto.forms`
   - `com.immiauto.repository.forms`
   - `com.immiauto.service.forms`
   - `com.immiauto.controller.FormAutomationController`

2. Add SQL migration `V6__form_package_automation.sql`.

3. Add PDFBox to `backend/pom.xml`.

4. Add backend services in this order:
   - `CanonicalApplicantDataService`
   - `PdfFormEngine` and `PdfBoxFormEngine`
   - `FormMappingService`
   - `PackageValidationService`
   - `CaseFormGenerationService`
   - `CasePackageService`

5. Add API endpoints and OpenAPI annotations if desired.

6. Add Angular model and endpoint constants.

7. Add Angular `forms-package` feature route and workspace page.

8. Integrate entry point into `case-detail`.

9. Add admin form catalogue once the pilot flow works.

## 12. Acceptance criteria

The section 4.1 MVP is complete when:

- At least one package profile can be selected for a case.
- Supported forms are tied to exact source URL, edition, and SHA-256 hash.
- Mappings are versioned and approved before use.
- Canonical data is shown with provenance before generation.
- Draft PDFs can be generated from mapped values.
- Readiness report separates errors, warnings, consultant decisions, client confirmations, and unresolved evidence.
- Package index lists required forms/documents in the correct order with naming, translation, certified copy, and size notes.
- Consultant cannot approve while blocking errors remain.
- Approved package stores approver, timestamp, form version, mapping version, input snapshot, output hash, and audit log.
- Downloads are protected by case access checks.
- No automatic official portal submission is attempted.

## 13. Key risks and mitigations

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Official PDFs use unsupported XFA/barcode behavior | High | Inspect each form first, mark unsupported forms as blocked, consider licensed PDF tooling for hard cases. |
| Forms change without notice | High | Require source URL, hash, effective date, retirement date, and regression tests per mapping version. |
| Mapping incorrect data to official fields | High | Use mapping preview, provenance, consultant review flags, and approval audit. |
| Consultant over-trusts automation | High | Prominent UI disclaimers, approval checklist, and readiness report. |
| Package generation leaks files | High | Secured download endpoints only, no public static generated folders. |
| Canonical model grows messy | Medium | Govern canonical field keys and source priority centrally. |
| Scope expands to portal submission too early | High | Keep portal submission out of MVP. Revisit only after package automation is reliable. |

