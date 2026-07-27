// Section 4.1 - IRCC Form & Package Automation models

export interface CanonicalValueSource {
  sourceType: string;
  sourceId?: number;
  sourceLabel: string;
  rawValue: string;
  selected: boolean;
  priority: number;
}

export interface CanonicalValue {
  fieldKey: string;
  displayName: string;
  category?: string;
  dataType?: string;
  value?: string;
  present: boolean;
  sensitive: boolean;
  reviewRequired: boolean;
  conflict: boolean;
  note?: string;
  sources: CanonicalValueSource[];
}

export interface CanonicalDataConflict {
  fieldKey: string;
  displayName: string;
  message: string;
  candidateValues: string[];
  sources: CanonicalValueSource[];
}

export interface CanonicalDataSnapshot {
  caseId: number;
  caseNumber: string;
  generatedAt: string;
  values: CanonicalValue[];
  conflicts: CanonicalDataConflict[];
  missingFieldKeys: string[];
}

export interface MappedFieldPreview {
  pdfFieldName: string;
  label?: string;
  fieldType?: string;
  canonicalFieldKey: string;
  transformType?: string;
  resolvedValue?: string;
  present: boolean;
  requiredForPackage: boolean;
  consultantReviewRequired: boolean;
  status: string; // MAPPED, MISSING_VALUE, REVIEW_REQUIRED, NO_SOURCE, UNSUPPORTED_TRANSFORM
  sourceSummary?: string;
  diagnostic?: string;
}

export interface FormMappingPreview {
  formDefinitionId: number;
  formCode: string;
  displayName: string;
  editionLabel?: string;
  supportsFill: boolean;
  mappingVersionId?: number;
  mappingVersion?: number;
  mappingStatus?: string;
  diagnostic?: string;
  fields: MappedFieldPreview[];
  unmappedRequiredFieldNames: string[];
}

export interface MappingPreview {
  caseId: number;
  packageProfileId: number;
  profileCode: string;
  profileDisplayName: string;
  generatedAt: string;
  snapshot: CanonicalDataSnapshot;
  forms: FormMappingPreview[];
}

// --- Admin catalogue (Milestone 6) ---
export interface FormDefinition {
  id: number;
  formCode: string;
  displayName: string;
  jurisdiction: string;
  programCategory?: string;
  sourceUrl?: string;
  sourceFileName?: string;
  sourceSha256?: string;
  effectiveDate?: string;
  retirementDate?: string;
  editionLabel?: string;
  supportsFill: boolean;
  supportsBarcode: boolean;
  status: string;
  notes?: string;
  fieldCount: number;
}

export interface FormFieldDefinition {
  id: number;
  pdfFieldName: string;
  label?: string;
  fieldType: string;
  required: boolean;
  maxLength?: number;
  allowedValues?: string;
  pageNumber?: number;
  readOnly: boolean;
  calculated: boolean;
  notes?: string;
}

export interface FormInspectionResult {
  formId: number;
  formCode: string;
  hasAcroForm: boolean;
  hasXfa: boolean;
  pageCount: number;
  fieldCount: number;
  sha256: string;
  classification: string; // STANDARD_ACROFORM, DYNAMIC_XFA, NO_FORM_FIELDS
  supportsFill: boolean;
  status: string;
  message: string;
}

export interface PackageProfileAdmin {
  id: number;
  profileCode: string;
  displayName: string;
  serviceType?: string;
  caseSubtype?: string;
  jurisdiction?: string;
  status: string;
  description?: string;
  effectiveDate?: string;
  retirementDate?: string;
  formCount: number;
  documentRequirementCount: number;
}

export interface FormMappingVersion {
  id: number;
  formDefinitionId: number;
  mappingVersion: number;
  status: string;
  approvedByConsultantName?: string;
  approvedAt?: string;
  changeSummary?: string;
  notes?: string;
}

export interface PackageProfileSummary {
  id: number;
  profileCode: string;
  displayName: string;
  serviceType?: string;
  caseSubtype?: string;
  jurisdiction?: string;
  status: string;
  description?: string;
  formCount: number;
  documentRequirementCount: number;
}

export interface ValidationIssue {
  id?: number;
  severity: string; // ERROR, WARNING, DECISION, CLIENT_CONFIRMATION, UNRESOLVED_EVIDENCE
  code: string;
  message: string;
  formCode?: string;
  fieldKey?: string;
  pdfFieldName?: string;
  sourceType?: string;
  sourceId?: number;
  resolved?: boolean;
  resolvedBy?: string;
  resolvedAt?: string;
  resolutionNotes?: string;
}

export interface CasePackage {
  id: number;
  caseId: number;
  packageProfileId: number;
  profileCode: string;
  profileDisplayName: string;
  status: string; // DRAFT, VALIDATION_FAILED, READY_FOR_APPROVAL, APPROVED, SUPERSEDED
  errorCount: number;
  warningCount: number;
  decisionCount: number;
  clientConfirmationCount: number;
  unresolvedEvidenceCount: number;
  approvalBlocked: boolean;
  hasZip: boolean;
  packageSha256?: string;
  generatedAt?: string;
  generatedBy?: string;
  approvedAt?: string;
  approvedBy?: string;
  approvalNotes?: string;
}

export interface PackageIndexForm {
  sortOrder: number;
  formCode: string;
  displayName: string;
  required: boolean;
  provided: boolean;
  origin?: string;
  status?: string;
  fileName?: string;
  draftId?: number;
}

export interface PackageIndexDocument {
  sortOrder: number;
  documentCategory?: string;
  documentType?: string;
  required: boolean;
  present: boolean;
  matchedCount: number;
  namingPattern?: string;
  translationRule?: string;
  certifiedCopyRule?: string;
}

export interface PackageIndex {
  caseId: number;
  packageId: number;
  profileCode: string;
  profileDisplayName: string;
  generatedAt: string;
  forms: PackageIndexForm[];
  documents: PackageIndexDocument[];
}

export interface PackageReadinessReport {
  caseId: number;
  packageProfileId: number;
  profileCode: string;
  profileDisplayName: string;
  generatedAt: string;
  errorCount: number;
  warningCount: number;
  decisionCount: number;
  clientConfirmationCount: number;
  unresolvedEvidenceCount: number;
  approvalBlocked: boolean;
  issues: ValidationIssue[];
}

export interface CaseFormDraft {
  id: number;
  caseId: number;
  formDefinitionId: number;
  formCode: string;
  formDisplayName: string;
  mappingVersionId?: number;
  mappingVersion?: number;
  origin: string; // GENERATED, UPLOADED, DATA_SHEET
  status: string; // DRAFT, VALIDATION_FAILED, READY_FOR_APPROVAL, APPROVED, SUPERSEDED
  draftSha256?: string;
  fileName?: string;
  originalFileName?: string;
  generatedAt?: string;
  generatedBy?: string;
  approvedAt?: string;
  approvedBy?: string;
  approvalNotes?: string;
}
