export type DocumentStatus = 'NOT_UPLOADED' | 'UPLOADED' | 'NEEDS_REVIEW' | 'ACCEPTED' |
  'REJECTED' | 'EXPIRED' | 'INCORRECT_DOCUMENT' | 'TRANSLATION_REQUIRED' |
  'NOTARIZATION_REQUIRED' | 'CLIENT_ACTION_NEEDED' | 'CONSULTANT_ACTION_NEEDED';

export type ServiceType = 'STUDY_PERMIT' | 'VISITOR_VISA' | 'SPOUSAL_SPONSORSHIP' |
  'EXPRESS_ENTRY' | 'WORK_PERMIT' | 'LMIA' | 'CITIZENSHIP' | 'PGWP' |
  'SUPER_VISA' | 'PR_CARD_PRTD' | 'PNP' | 'OTHER';

export interface ChecklistItem {
  id?: number;
  caseId?: number;
  category: string;
  documentName: string;
  description?: string;
  status: DocumentStatus;
  required: boolean;
  conditional: boolean;
  conditionDescription?: string;
  sortOrder: number;
  consultantReviewNote?: string;
  linkedDocumentId?: number;
}

export interface ChecklistTemplate {
  id?: number;
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
  reviewedByConsultantId?: number;
  reviewedByConsultantName?: string;
  ruleVersion: number;
  approvedForUse: boolean;
  approvedByConsultantId?: number;
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
  id: number;
  entityType: string;
  entityId: number;
  action: string;
  details: string;
  performedBy: string;
  performedAt: string;
}
