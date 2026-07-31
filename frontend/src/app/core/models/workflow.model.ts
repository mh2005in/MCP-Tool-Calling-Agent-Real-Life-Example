export interface TravelHistoryEntry {
  id?: string;
  caseId?: string;
  country: string;
  entryDate: string;
  exitDate?: string;
  purpose?: string;
  daysAbsent: number;
  notes?: string;
  sortOrder: number;
}

export interface PhysicalPresenceSummary {
  totalDaysAbsent: number;
  daysInCanadaAsPR: number;
  prePrCreditDays: number;
  effectiveDaysInCanada: number;
  requiredDays: number;
  meetsPhysicalPresence: boolean;
  meetsPrMinimum: boolean;
  daysShort: number;
  totalTrips: number;
  applicationDate?: string;
  prStartDate?: string;
  disclaimer: string;
}

export interface WorkHistoryEntry {
  id?: string;
  caseId?: string;
  employerName: string;
  jobTitle?: string;
  nocTeerCode?: string;
  startDate: string;
  endDate?: string;
  currentJob: boolean;
  hoursPerWeek: number;
  employmentType?: string;
  duties?: string;
  country?: string;
  city?: string;
  referenceLetterReceived: boolean;
  referenceDatesMatch: boolean;
  referenceDutiesDescribed: boolean;
  sortOrder: number;
}

export interface RelationshipTimelineEntry {
  id?: string;
  caseId?: string;
  milestoneType: string;
  milestoneDate: string;
  location?: string;
  description?: string;
  evidenceCategory?: string;
  evidenceCount: number;
  sortOrder: number;
}

export interface RecruitmentEvidence {
  id?: string;
  caseId?: string;
  evidenceType: string;
  platform: string;
  postingDate: string;
  expiryDate?: string;
  daysPosted: number;
  applicantsReceived: number;
  interviewsConducted: number;
  nonHireReasons?: string;
  screenshotAttached: boolean;
  notes?: string;
  sortOrder: number;
}

export interface CandidateComparison {
  id?: string;
  caseId?: string;
  candidateName: string;
  candidateType: 'CANADIAN' | 'FOREIGN';
  qualifications?: string;
  yearsExperience: number;
  educationLevel?: string;
  languageSkills?: string;
  interviewed: boolean;
  interviewNotes?: string;
  outcome?: string;
  nonHireReason?: string;
  sortOrder: number;
}
