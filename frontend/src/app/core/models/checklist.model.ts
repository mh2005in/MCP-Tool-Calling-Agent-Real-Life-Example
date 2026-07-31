export type DocumentStatus = 'NOT_UPLOADED' | 'UPLOADED' | 'NEEDS_REVIEW' | 'ACCEPTED' |
  'REJECTED' | 'EXPIRED' | 'INCORRECT_DOCUMENT' | 'TRANSLATION_REQUIRED' |
  'NOTARIZATION_REQUIRED' | 'CLIENT_ACTION_NEEDED' | 'CONSULTANT_ACTION_NEEDED';

export type ServiceType = 'STUDY_PERMIT' | 'VISITOR_VISA' | 'SPOUSAL_SPONSORSHIP' |
  'EXPRESS_ENTRY' | 'WORK_PERMIT' | 'LMIA' | 'CITIZENSHIP' | 'PGWP' |
  'SUPER_VISA' | 'PR_CARD_PRTD' | 'PNP' | 'OTHER';

export interface ChecklistItem {
  id?: string;
  caseId?: string;
  category: string;
  documentName: string;
  description?: string;
  status: DocumentStatus;
  required: boolean;
  conditional: boolean;
  conditionDescription?: string;
  sortOrder: number;
  consultantReviewNote?: string;
  linkedDocumentId?: string;
}

export interface ChecklistTemplate {
  id?: string;
  serviceType: ServiceType;
  category: string;
  documentName: string;
  description?: string;
  required: boolean;
  conditional: boolean;
  conditionDescription?: string;
  sortOrder: number;
  sourceUrl?: string;
  lastReviewedDate?: string;
  reviewedByConsultantId?: string;
  reviewedByConsultantName?: string;
  ruleVersion: number;
  approvedForUse: boolean;
  approvedByConsultantId?: string;
  approvedByConsultantName?: string;
  approvedDate?: string;
}

export interface ClientChecklistResponse {
  disclaimer: string;
  items: ChecklistItem[];
  totalItems: number;
  completedItems: number;
  missingItems: number;
}

export interface AuditLogEntry {
  id: string;
  entityType: string;
  entityId: string;
  action: string;
  details: string;
  performedBy: string;
  performedAt: string;
}
