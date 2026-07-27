import { ImmigrationCase } from './case.model';
import { Consultant } from './consultant.model';

export interface Dashboard {
  totalActiveCases: number;
  casesAwaitingDocuments: number;
  casesFileReady: number;
  casesWithUpcomingDeadlines: number;
  pendingReminders: number;
  casesByServiceType: Record<string, number>;
  casesByStatus: Record<string, number>;
  recentCases: ImmigrationCase[];
  urgentCases: ImmigrationCase[];
}

export interface ConsultantSummary {
  consultant: Consultant;
  activeCases: number;
  upcomingDeadlines: number;
  pendingReminders: number;
  casesAwaitingDocuments: number;
}

export interface OrgDashboard {
  totalConsultants: number;
  totalActiveCases: number;
  casesAwaitingDocuments: number;
  casesFileReady: number;
  casesWithUpcomingDeadlines: number;
  pendingReminders: number;
  casesByServiceType: Record<string, number>;
  casesByStatus: Record<string, number>;
  consultantSummaries: ConsultantSummary[];
}
