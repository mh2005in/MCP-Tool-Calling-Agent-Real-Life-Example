export type ServiceType = 'STUDY_PERMIT' | 'VISITOR_VISA' | 'SPOUSAL_SPONSORSHIP' |
  'EXPRESS_ENTRY' | 'WORK_PERMIT' | 'LMIA' | 'CITIZENSHIP' | 'PGWP' |
  'SUPER_VISA' | 'PR_CARD_PRTD' | 'PNP' | 'OTHER';

export type CaseSubtype =
  // Study Permit
  'NEW_INTERNATIONAL_STUDENT' | 'STUDENT_INSIDE_CANADA' | 'MINOR_STUDENT' | 'QUEBEC_STUDENT' | 'STUDENT_WITH_FAMILY' |
  // Visitor Visa
  'FAMILY_VISITOR' | 'TOURISM' | 'BUSINESS_VISITOR' | 'EVENT_VISITOR' | 'PREVIOUS_REFUSAL_VISITOR' |
  // Spousal Sponsorship
  'SPOUSAL_INLAND' | 'SPOUSAL_OUTLAND' | 'COMMON_LAW' | 'CONJUGAL' | 'DEPENDENT_CHILD' |
  // Express Entry
  'FEDERAL_SKILLED_WORKER' | 'CANADIAN_EXPERIENCE_CLASS' | 'FEDERAL_SKILLED_TRADES' | 'PNP_EXPRESS_ENTRY' |
  // Work Permit
  'EMPLOYER_SPECIFIC_LMIA' | 'EMPLOYER_SPECIFIC_EXEMPT' | 'OPEN_WORK_PERMIT' | 'BRIDGING_OPEN_WORK_PERMIT' | 'INTRA_COMPANY_TRANSFER' | 'INTERNATIONAL_MOBILITY' |
  // LMIA
  'LMIA_HIGH_WAGE' | 'LMIA_LOW_WAGE' | 'LMIA_PR_SUPPORT' | 'LMIA_GLOBAL_TALENT' | 'LMIA_AGRICULTURAL' | 'LMIA_CAREGIVER' | 'LMIA_SEASONAL' |
  // Citizenship
  'ADULT_CITIZENSHIP_GRANT' | 'MINOR_CITIZENSHIP_GRANT' | 'CITIZENSHIP_CERTIFICATE' | 'REPLACEMENT_CERTIFICATE' |
  // PR Card / PRTD
  'PR_CARD_RENEWAL' | 'PR_CARD_REPLACEMENT' | 'PR_CARD_FIRST' | 'PR_TRAVEL_DOCUMENT' |
  // Super Visa
  'SUPER_VISA_PARENT' | 'SUPER_VISA_GRANDPARENT' |
  'OTHER_SUBTYPE';

export type ApplicantRole = 'PRINCIPAL_APPLICANT' | 'SPOUSE' | 'DEPENDENT_CHILD' |
  'SPONSOR' | 'EMPLOYER' | 'HOST';

export type LeadStatus = 'NEW' | 'PRE_SCREENED' | 'CONSULTATION_BOOKED' |
  'RETAINER_SENT' | 'CLIENT_RETAINED' | 'CLOSED';

export type CaseStatus = 'INTAKE_PENDING' | 'INTAKE_COMPLETED' | 'CHECKLIST_PENDING' |
  'DOCUMENTS_COLLECTING' | 'DOCUMENTS_UNDER_REVIEW' | 'FILE_READY' |
  'APPLICATION_PREP' | 'FINAL_REVIEW' |
  'APPLICATION_SUBMITTED' | 'POST_SUBMISSION' | 'DECISION_RECEIVED' | 'CLOSED';

export interface ImmigrationCase {
  id?: string;
  caseNumber?: string;
  serviceType: ServiceType;
  subtype?: CaseSubtype;
  applicantRole?: ApplicantRole;
  leadStatus: LeadStatus;
  caseStatus: CaseStatus;
  clientId: string;
  clientName?: string;
  consultantId: string;
  consultantName?: string;
  intakeSummary?: string;
  consultantNotes?: string;
  deadline?: string;
  urgencyReason?: string;
  submissionDate?: string;
  applicationNumber?: string;
  portalUsed?: string;
  biometricsStatus?: string;
  medicalStatus?: string;
  retainerSignedDate?: string;
  retainerDocumentPath?: string;
  engagementLetterStatus?: string;
  finalReviewedBy?: string;
  finalReviewedAt?: string;
  finalReviewNotes?: string;
  consultantSignedOff?: boolean;
  totalChecklistItems?: number;
  completedItems?: number;
  missingItems?: number;
  createdAt?: string;
}

export interface CreateCaseRequest {
  serviceType: ServiceType;
  subtype?: CaseSubtype;
  applicantRole?: ApplicantRole;
  clientId: string;
  consultantId: string;
  deadline?: string;
  urgencyReason?: string;
  consultantNotes?: string;
  applyDefaultChecklist: boolean;
}
