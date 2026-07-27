import { ServiceType } from './case.model';

export interface IntakeQuestionTemplate {
  id?: number;
  serviceType: ServiceType;
  sectionName: string;
  questionKey: string;
  questionLabel: string;
  helpText?: string;
  inputType: 'TEXT' | 'SELECT' | 'RADIO' | 'CHECKBOX' | 'DATE' | 'TEXTAREA';
  options?: string;
  required: boolean;
  isTriggerQuestion: boolean;
  sortOrder: number;
}

export interface IntakeResponse {
  id?: number;
  caseId?: number;
  sectionName: string;
  questionKey: string;
  questionLabel: string;
  answer: string;
  sortOrder: number;
  flaggedForReview: boolean;
}

export interface IntakeSubmissionRequest {
  responses: IntakeResponse[];
}

export interface IntakeSummary {
  caseId: number;
  serviceType: ServiceType;
  responsesBySection: { [section: string]: IntakeResponse[] };
  aiSummary: string;
  flaggedResponses: IntakeResponse[];
}

export interface ConditionalRule {
  id?: number;
  serviceType: ServiceType;
  triggerQuestionKey: string;
  triggerValue: string;
  operator: 'EQUALS' | 'NOT_EQUALS' | 'CONTAINS' | 'NOT_CONTAINS' | 'YES' | 'NO';
  actionType: 'INCLUDE' | 'EXCLUDE';
  targetChecklistTemplateId?: number;
  targetQuestionKey?: string;
  description: string;
  active: boolean;
}
