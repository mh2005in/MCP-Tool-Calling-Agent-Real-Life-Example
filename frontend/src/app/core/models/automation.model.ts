export interface ExpiryAlert {
  id: number;
  caseId: number;
  caseNumber: string;
  clientName: string;
  alertType: string;
  documentDescription: string;
  expiryDate: string;
  daysUntilExpiry: number;
  severity: 'INFO' | 'WARNING' | 'URGENT' | 'CRITICAL';
  acknowledged: boolean;
  acknowledgedBy?: string;
  linkedDocumentId?: number;
  createdAt: string;
}

export interface ConsistencyIssue {
  type: string;
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  description: string;
  documentId?: number;
}

export interface DocumentClassification {
  category: string;
  type: string;
  confidence: 'LOW' | 'HIGH';
}

export interface PoliceCertificateRequirement {
  country: string;
  longestContinuousStayDays: number;
  required: boolean;
  reason: string;
}

export interface LmiaCompliance {
  intendedSubmissionDate?: string;
  recruitmentWindowStart?: string;
  jobBankPosted: boolean;
  jobBankDaysPosted: number;
  jobBankMeetsMinimum: boolean;
  jobBankPostingDate?: string;
  jobBankWithinWindow: boolean;
  additionalAdsCount: number;
  additionalAdsMeetMinimum: boolean;
  distinctPlatforms: number;
  distinctAudiencesVerified: boolean;
  allAdsWithinWindow: boolean;
  totalApplicants: number;
  totalInterviews: number;
  preliminaryReviewStatus: 'REQUIREMENTS_APPEAR_MET' | 'REQUIREMENTS_NOT_YET_MET';
  warnings: string[];
  disclaimer: string;
}
