import { DocumentStatus } from './checklist.model';

export interface CaseDocument {
  id?: string;
  caseId?: string;
  originalFileName: string;
  mimeType?: string;
  fileSizeBytes?: number;
  documentCategory?: string;
  documentType?: string;
  status: DocumentStatus;
  expiryDate?: string;
  reviewNote?: string;
  rejectionReason?: string;
  translationRequired: boolean;
  notarizationRequired: boolean;
  standardizedName?: string;
  createdAt?: string;
}
