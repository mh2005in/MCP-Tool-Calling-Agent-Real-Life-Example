import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../constants/api-endpoints';

import { Client } from '../models/client.model';
import { Consultant } from '../models/consultant.model';
import { ImmigrationCase, CreateCaseRequest, CaseStatus, LeadStatus, ServiceType as CaseServiceType } from '../models/case.model';
import { ChecklistItem, ChecklistTemplate, ClientChecklistResponse, AuditLogEntry, DocumentStatus, ServiceType } from '../models/checklist.model';
import { IntakeQuestionTemplate, IntakeResponse, IntakeSubmissionRequest, IntakeSummary } from '../models/intake.model';
import { CaseDocument } from '../models/document.model';
import { Reminder } from '../models/reminder.model';
import { Dashboard, OrgDashboard } from '../models/dashboard.model';
import { ExpiryAlert, ConsistencyIssue, DocumentClassification, PoliceCertificateRequirement, LmiaCompliance } from '../models/automation.model';
import { PartyProfile } from '../models/party.model';
import { TravelHistoryEntry, PhysicalPresenceSummary, WorkHistoryEntry, RelationshipTimelineEntry, RecruitmentEvidence, CandidateComparison } from '../models/workflow.model';
import { PackageProfileSummary, CanonicalDataSnapshot, MappingPreview, CaseFormDraft, PackageReadinessReport, CasePackage, PackageIndex,
  FormDefinition, FormFieldDefinition, FormInspectionResult, PackageProfileAdmin, FormMappingVersion } from '../models/form-automation.model';

@Injectable({ providedIn: 'root' })
export class ApiService {

  constructor(private http: HttpClient) {}

  // --- Clients ---
  getClients(consultantId: string): Observable<Client[]> {
    return this.http.get<Client[]>(API_ENDPOINTS.CLIENTS_GET(consultantId));
  }

  getClient(consultantId: string, id: string): Observable<Client> {
    return this.http.get<Client>(API_ENDPOINTS.CLIENTS_GET_BY_ID(consultantId, id));
  }

  createClient(consultantId: string, client: Client): Observable<Client> {
    return this.http.post<Client>(API_ENDPOINTS.CLIENTS_CREATE(consultantId), client);
  }

  updateClient(consultantId: string, id: string, client: Client): Observable<Client> {
    return this.http.put<Client>(API_ENDPOINTS.CLIENTS_UPDATE(consultantId, id), client);
  }

  searchClients(consultantId: string, query: string): Observable<Client[]> {
    return this.http.get<Client[]>(API_ENDPOINTS.CLIENTS_SEARCH(consultantId), {
      params: new HttpParams().set('q', query)
    });
  }

  getClientByNumber(consultantId: string, clientNumber: string): Observable<Client> {
    return this.http.get<Client>(API_ENDPOINTS.CLIENTS_SEARCH_BY_NUMBER(consultantId), {
      params: new HttpParams().set('clientNumber', clientNumber)
    });
  }

  // --- Cases ---
  getCases(consultantId: string): Observable<ImmigrationCase[]> {
    return this.http.get<ImmigrationCase[]>(API_ENDPOINTS.CASES_GET(consultantId));
  }

  getCase(consultantId: string, id: string): Observable<ImmigrationCase> {
    return this.http.get<ImmigrationCase>(API_ENDPOINTS.CASES_GET_BY_ID(consultantId, id));
  }

  createCase(consultantId: string, request: CreateCaseRequest): Observable<ImmigrationCase> {
    return this.http.post<ImmigrationCase>(API_ENDPOINTS.CASES_CREATE(consultantId), request);
  }

  updateCase(consultantId: string, id: string, data: Partial<ImmigrationCase>): Observable<ImmigrationCase> {
    return this.http.put<ImmigrationCase>(API_ENDPOINTS.CASES_UPDATE(consultantId, id), data);
  }

  updateCaseStatus(consultantId: string, id: string, status: CaseStatus): Observable<ImmigrationCase> {
    return this.http.patch<ImmigrationCase>(API_ENDPOINTS.CASES_STATUS(consultantId, id), null, {
      params: new HttpParams().set('status', status)
    });
  }

  updateLeadStatus(consultantId: string, id: string, status: LeadStatus): Observable<ImmigrationCase> {
    return this.http.patch<ImmigrationCase>(API_ENDPOINTS.CASES_LEAD_STATUS(consultantId, id), null, {
      params: new HttpParams().set('status', status)
    });
  }

  getCaseByNumber(consultantId: string, caseNumber: string): Observable<ImmigrationCase> {
    return this.http.get<ImmigrationCase>(API_ENDPOINTS.CASES_SEARCH_BY_NUMBER(consultantId), {
      params: new HttpParams().set('caseNumber', caseNumber)
    });
  }

  getUpcomingDeadlines(consultantId: string, days: number = 14): Observable<ImmigrationCase[]> {
    return this.http.get<ImmigrationCase[]>(API_ENDPOINTS.CASES_DEADLINES(consultantId), {
      params: new HttpParams().set('daysAhead', days.toString())
    });
  }

  // --- Checklist ---
  getChecklist(caseId: string): Observable<ChecklistItem[]> {
    return this.http.get<ChecklistItem[]>(API_ENDPOINTS.CHECKLIST_GET(caseId));
  }

  getMissingItems(caseId: string): Observable<ChecklistItem[]> {
    return this.http.get<ChecklistItem[]>(API_ENDPOINTS.CHECKLIST_MISSING(caseId));
  }

  addChecklistItem(caseId: string, item: ChecklistItem): Observable<ChecklistItem> {
    return this.http.post<ChecklistItem>(API_ENDPOINTS.CHECKLIST_CREATE(caseId), item);
  }

  updateChecklistItemStatus(caseId: string, itemId: string, status: DocumentStatus, reviewNote?: string): Observable<ChecklistItem> {
    let params = new HttpParams().set('status', status);
    if (reviewNote) params = params.set('reviewNote', reviewNote);
    return this.http.patch<ChecklistItem>(API_ENDPOINTS.CHECKLIST_UPDATE_STATUS(caseId, itemId), null, { params });
  }

  deleteChecklistItem(caseId: string, itemId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.CHECKLIST_DELETE(itemId));
  }

  // --- Documents ---
  getDocuments(caseId: string): Observable<CaseDocument[]> {
    return this.http.get<CaseDocument[]>(API_ENDPOINTS.DOCUMENTS_GET(caseId));
  }

  uploadDocument(caseId: string, file: File, category?: string, type?: string): Observable<CaseDocument> {
    const formData = new FormData();
    formData.append('file', file);
    if (category) formData.append('category', category);
    if (type) formData.append('type', type);
    return this.http.post<CaseDocument>(API_ENDPOINTS.DOCUMENTS_UPLOAD(caseId), formData);
  }

  reviewDocument(caseId: string, docId: string, status: DocumentStatus, reviewNote?: string, rejectionReason?: string): Observable<CaseDocument> {
    let params = new HttpParams().set('status', status);
    if (reviewNote) params = params.set('reviewNote', reviewNote);
    if (rejectionReason) params = params.set('rejectionReason', rejectionReason);
    return this.http.patch<CaseDocument>(API_ENDPOINTS.DOCUMENTS_REVIEW(caseId, docId), null, { params });
  }

  // --- Reminders ---
  getReminders(caseId: string): Observable<Reminder[]> {
    return this.http.get<Reminder[]>(API_ENDPOINTS.REMINDERS_GET(caseId));
  }

  createReminder(caseId: string, reminder: Reminder): Observable<Reminder> {
    return this.http.post<Reminder>(API_ENDPOINTS.REMINDERS_CREATE(caseId), reminder);
  }

  approveReminder(reminderId: string, approvedBy: string): Observable<Reminder> {
    return this.http.patch<Reminder>(API_ENDPOINTS.REMINDERS_APPROVE(reminderId), null, {
      params: new HttpParams().set('approvedBy', approvedBy)
    });
  }

  getPendingReminders(consultantId: string): Observable<Reminder[]> {
    return this.http.get<Reminder[]>(API_ENDPOINTS.REMINDERS_PENDING(consultantId));
  }

  // --- Dashboard ---
  getDashboard(consultantId: string): Observable<Dashboard> {
    return this.http.get<Dashboard>(API_ENDPOINTS.DASHBOARD_GET(consultantId));
  }

  getOrgDashboard(): Observable<OrgDashboard> {
    return this.http.get<OrgDashboard>(API_ENDPOINTS.DASHBOARD_ORG);
  }

  // --- Consultants ---
  getConsultants(): Observable<Consultant[]> {
    return this.http.get<Consultant[]>(API_ENDPOINTS.CONSULTANTS_GET);
  }

  getConsultant(id: string): Observable<Consultant> {
    return this.http.get<Consultant>(API_ENDPOINTS.CONSULTANTS_GET_BY_ID(id));
  }

  createConsultant(consultant: Consultant): Observable<Consultant> {
    return this.http.post<Consultant>(API_ENDPOINTS.CONSULTANTS_CREATE, consultant);
  }

  updateConsultant(id: string, consultant: Consultant): Observable<Consultant> {
    return this.http.put<Consultant>(API_ENDPOINTS.CONSULTANTS_UPDATE(id), consultant);
  }

  getOwnProfile(id: string): Observable<Consultant> {
    return this.http.get<Consultant>(API_ENDPOINTS.CONSULTANTS_PROFILE(id));
  }

  getConsultantByNumber(consultantNumber: string): Observable<Consultant> {
    return this.http.get<Consultant>(API_ENDPOINTS.CONSULTANTS_SEARCH_BY_NUMBER, {
      params: new HttpParams().set('consultantNumber', consultantNumber)
    });
  }

  setConsultantActive(id: string, active: boolean): Observable<Consultant> {
    return this.http.patch<Consultant>(API_ENDPOINTS.CONSULTANTS_SET_ACTIVE(id), null, {
      params: new HttpParams().set('active', active)
    });
  }

  // --- Checklist Templates ---
  createTemplate(template: ChecklistTemplate, consultantId: string): Observable<ChecklistTemplate> {
    return this.http.post<ChecklistTemplate>(API_ENDPOINTS.TEMPLATES_CREATE, template, {
      params: new HttpParams().set('consultantId', consultantId)
    });
  }

  updateTemplate(templateId: string, template: ChecklistTemplate, consultantId: string): Observable<ChecklistTemplate> {
    return this.http.put<ChecklistTemplate>(API_ENDPOINTS.TEMPLATES_UPDATE(templateId), template, {
      params: new HttpParams().set('consultantId', consultantId)
    });
  }

  reviewTemplate(templateId: string, consultantId: string): Observable<ChecklistTemplate> {
    return this.http.patch<ChecklistTemplate>(API_ENDPOINTS.TEMPLATES_REVIEW(templateId), null, {
      params: new HttpParams().set('consultantId', consultantId)
    });
  }

  approveTemplate(templateId: string, consultantId: string): Observable<ChecklistTemplate> {
    return this.http.patch<ChecklistTemplate>(API_ENDPOINTS.TEMPLATES_APPROVE(templateId), null, {
      params: new HttpParams().set('consultantId', consultantId)
    });
  }

  revokeTemplateApproval(templateId: string, consultantId: string): Observable<ChecklistTemplate> {
    return this.http.patch<ChecklistTemplate>(API_ENDPOINTS.TEMPLATES_REVOKE(templateId), null, {
      params: new HttpParams().set('consultantId', consultantId)
    });
  }

  getTemplatesByServiceType(serviceType: ServiceType): Observable<ChecklistTemplate[]> {
    return this.http.get<ChecklistTemplate[]>(API_ENDPOINTS.TEMPLATES_GET_BY_TYPE, {
      params: new HttpParams().set('serviceType', serviceType)
    });
  }

  getApprovedTemplates(serviceType: ServiceType): Observable<ChecklistTemplate[]> {
    return this.http.get<ChecklistTemplate[]>(API_ENDPOINTS.TEMPLATES_GET_APPROVED, {
      params: new HttpParams().set('serviceType', serviceType)
    });
  }

  getTemplate(templateId: string): Observable<ChecklistTemplate> {
    return this.http.get<ChecklistTemplate>(API_ENDPOINTS.TEMPLATES_GET_BY_ID(templateId));
  }

  getAllTemplates(): Observable<ChecklistTemplate[]> {
    return this.http.get<ChecklistTemplate[]>(API_ENDPOINTS.TEMPLATES_GET_ALL);
  }

  deleteTemplate(templateId: string, consultantId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.TEMPLATES_DELETE(templateId), {
      params: new HttpParams().set('consultantId', consultantId)
    });
  }

  getTemplateAuditHistory(templateId: string): Observable<AuditLogEntry[]> {
    return this.http.get<AuditLogEntry[]>(API_ENDPOINTS.TEMPLATES_AUDIT(templateId));
  }

  // --- Admin (cross-consultant access) ---
  adminGetAllClients(consultantId: string): Observable<Client[]> {
    return this.http.get<Client[]>(API_ENDPOINTS.ADMIN_CLIENTS_ALL(consultantId));
  }

  adminGetClientsByConsultant(consultantId: string, targetConsultantId: string): Observable<Client[]> {
    return this.http.get<Client[]>(API_ENDPOINTS.ADMIN_CLIENTS_BY_CONSULTANT(consultantId, targetConsultantId));
  }

  adminGetClient(consultantId: string, clientId: string): Observable<Client> {
    return this.http.get<Client>(API_ENDPOINTS.ADMIN_CLIENT_BY_ID(consultantId, clientId));
  }

  adminSearchClients(consultantId: string, query: string): Observable<Client[]> {
    return this.http.get<Client[]>(API_ENDPOINTS.ADMIN_CLIENTS_SEARCH(consultantId), {
      params: new HttpParams().set('q', query)
    });
  }

  adminGetAllCases(consultantId: string): Observable<ImmigrationCase[]> {
    return this.http.get<ImmigrationCase[]>(API_ENDPOINTS.ADMIN_CASES_ALL(consultantId));
  }

  adminGetCasesByConsultant(consultantId: string, targetConsultantId: string): Observable<ImmigrationCase[]> {
    return this.http.get<ImmigrationCase[]>(API_ENDPOINTS.ADMIN_CASES_BY_CONSULTANT(consultantId, targetConsultantId));
  }

  adminGetCase(consultantId: string, caseId: string): Observable<ImmigrationCase> {
    return this.http.get<ImmigrationCase>(API_ENDPOINTS.ADMIN_CASE_BY_ID(consultantId, caseId));
  }

  // --- Client Checklist (with disclaimer) ---
  getClientChecklist(caseId: string): Observable<ClientChecklistResponse> {
    return this.http.get<ClientChecklistResponse>(API_ENDPOINTS.CHECKLIST_CLIENT_VIEW(caseId));
  }

  // --- Intake ---
  getIntakeQuestions(serviceType: ServiceType): Observable<IntakeQuestionTemplate[]> {
    return this.http.get<IntakeQuestionTemplate[]>(API_ENDPOINTS.INTAKE_QUESTIONS, {
      params: new HttpParams().set('serviceType', serviceType)
    });
  }

  getIntakeQuestionsBySection(serviceType: ServiceType, sectionName: string): Observable<IntakeQuestionTemplate[]> {
    return this.http.get<IntakeQuestionTemplate[]>(API_ENDPOINTS.INTAKE_QUESTIONS_BY_SECTION, {
      params: new HttpParams().set('serviceType', serviceType).set('sectionName', sectionName)
    });
  }

  getIntakeSections(serviceType: ServiceType): Observable<string[]> {
    return this.http.get<string[]>(API_ENDPOINTS.INTAKE_SECTIONS, {
      params: new HttpParams().set('serviceType', serviceType)
    });
  }

  submitIntake(caseId: string, request: IntakeSubmissionRequest): Observable<IntakeResponse[]> {
    return this.http.post<IntakeResponse[]>(API_ENDPOINTS.INTAKE_SUBMIT(caseId), request);
  }

  getIntakeResponses(caseId: string): Observable<IntakeResponse[]> {
    return this.http.get<IntakeResponse[]>(API_ENDPOINTS.INTAKE_RESPONSES(caseId));
  }

  getIntakeSummary(caseId: string): Observable<IntakeSummary> {
    return this.http.get<IntakeSummary>(API_ENDPOINTS.INTAKE_SUMMARY(caseId));
  }

  flagIntakeResponse(responseId: string, flagged: boolean): Observable<IntakeResponse> {
    return this.http.patch<IntakeResponse>(API_ENDPOINTS.INTAKE_FLAG(responseId), null, {
      params: new HttpParams().set('flagged', flagged.toString())
    });
  }

  updateIntakeResponse(responseId: string, answer: string): Observable<IntakeResponse> {
    return this.http.patch<IntakeResponse>(API_ENDPOINTS.INTAKE_UPDATE_RESPONSE(responseId), null, {
      params: new HttpParams().set('answer', answer)
    });
  }

  generateChecklistFromIntake(caseId: string): Observable<ChecklistItem[]> {
    return this.http.post<ChecklistItem[]>(API_ENDPOINTS.INTAKE_GENERATE_CHECKLIST(caseId), null);
  }

  // --- Expiry Alerts ---
  getExpiryAlertsByConsultant(consultantId: string): Observable<ExpiryAlert[]> {
    return this.http.get<ExpiryAlert[]>(API_ENDPOINTS.EXPIRY_ALERTS_BY_CONSULTANT(consultantId));
  }

  getExpiryAlertsByCase(caseId: string): Observable<ExpiryAlert[]> {
    return this.http.get<ExpiryAlert[]>(API_ENDPOINTS.EXPIRY_ALERTS_BY_CASE(caseId));
  }

  acknowledgeExpiryAlert(alertId: string, acknowledgedBy: string): Observable<ExpiryAlert> {
    return this.http.put<ExpiryAlert>(API_ENDPOINTS.EXPIRY_ALERT_ACKNOWLEDGE(alertId), null, {
      params: new HttpParams().set('acknowledgedBy', acknowledgedBy)
    });
  }

  triggerExpiryScan(): Observable<string> {
    return this.http.post<string>(API_ENDPOINTS.EXPIRY_ALERTS_SCAN, null);
  }

  // --- Automation ---
  classifyDocument(fileName: string): Observable<DocumentClassification> {
    return this.http.get<DocumentClassification>(API_ENDPOINTS.DOCUMENT_CLASSIFY, {
      params: new HttpParams().set('fileName', fileName)
    });
  }

  checkConsistency(caseId: string): Observable<ConsistencyIssue[]> {
    return this.http.get<ConsistencyIssue[]>(API_ENDPOINTS.CONSISTENCY_CHECK(caseId));
  }

  getRequiredPoliceCertificates(caseId: string): Observable<PoliceCertificateRequirement[]> {
    return this.http.get<PoliceCertificateRequirement[]>(API_ENDPOINTS.POLICE_CERTIFICATES(caseId));
  }

  getLmiaCompliance(caseId: string): Observable<LmiaCompliance> {
    return this.http.get<LmiaCompliance>(API_ENDPOINTS.LMIA_COMPLIANCE(caseId));
  }

  // --- Party Portals ---
  createParty(caseId: string, party: PartyProfile): Observable<PartyProfile> {
    return this.http.post<PartyProfile>(API_ENDPOINTS.PARTIES_CREATE(caseId), party);
  }

  getParties(caseId: string): Observable<PartyProfile[]> {
    return this.http.get<PartyProfile[]>(API_ENDPOINTS.PARTIES_GET(caseId));
  }

  updateParty(partyId: string, party: PartyProfile): Observable<PartyProfile> {
    return this.http.put<PartyProfile>(API_ENDPOINTS.PARTY_UPDATE(partyId), party);
  }

  deleteParty(partyId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.PARTY_DELETE(partyId));
  }

  getPortalByToken(token: string): Observable<PartyProfile> {
    return this.http.get<PartyProfile>(API_ENDPOINTS.PORTAL_BY_TOKEN(token));
  }

  // --- Travel History ---
  getTravelHistory(caseId: string): Observable<TravelHistoryEntry[]> {
    return this.http.get<TravelHistoryEntry[]>(API_ENDPOINTS.TRAVEL_HISTORY(caseId));
  }

  addTravelEntry(caseId: string, entry: TravelHistoryEntry): Observable<TravelHistoryEntry> {
    return this.http.post<TravelHistoryEntry>(API_ENDPOINTS.TRAVEL_HISTORY(caseId), entry);
  }

  updateTravelEntry(caseId: string, entryId: string, entry: TravelHistoryEntry): Observable<TravelHistoryEntry> {
    return this.http.put<TravelHistoryEntry>(API_ENDPOINTS.TRAVEL_HISTORY_ENTRY(caseId, entryId), entry);
  }

  deleteTravelEntry(caseId: string, entryId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.TRAVEL_HISTORY_ENTRY(caseId, entryId));
  }

  getPhysicalPresence(caseId: string): Observable<PhysicalPresenceSummary> {
    return this.http.get<PhysicalPresenceSummary>(API_ENDPOINTS.PHYSICAL_PRESENCE(caseId));
  }

  // --- Work History ---
  getWorkHistory(caseId: string): Observable<WorkHistoryEntry[]> {
    return this.http.get<WorkHistoryEntry[]>(API_ENDPOINTS.WORK_HISTORY(caseId));
  }

  addWorkEntry(caseId: string, entry: WorkHistoryEntry): Observable<WorkHistoryEntry> {
    return this.http.post<WorkHistoryEntry>(API_ENDPOINTS.WORK_HISTORY(caseId), entry);
  }

  updateWorkEntry(caseId: string, entryId: string, entry: WorkHistoryEntry): Observable<WorkHistoryEntry> {
    return this.http.put<WorkHistoryEntry>(API_ENDPOINTS.WORK_HISTORY_ENTRY(caseId, entryId), entry);
  }

  deleteWorkEntry(caseId: string, entryId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.WORK_HISTORY_ENTRY(caseId, entryId));
  }

  // --- Relationship Timeline ---
  getRelationshipTimeline(caseId: string): Observable<RelationshipTimelineEntry[]> {
    return this.http.get<RelationshipTimelineEntry[]>(API_ENDPOINTS.RELATIONSHIP_TIMELINE(caseId));
  }

  addTimelineEntry(caseId: string, entry: RelationshipTimelineEntry): Observable<RelationshipTimelineEntry> {
    return this.http.post<RelationshipTimelineEntry>(API_ENDPOINTS.RELATIONSHIP_TIMELINE(caseId), entry);
  }

  updateTimelineEntry(caseId: string, entryId: string, entry: RelationshipTimelineEntry): Observable<RelationshipTimelineEntry> {
    return this.http.put<RelationshipTimelineEntry>(API_ENDPOINTS.RELATIONSHIP_TIMELINE_ENTRY(caseId, entryId), entry);
  }

  deleteTimelineEntry(caseId: string, entryId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.RELATIONSHIP_TIMELINE_ENTRY(caseId, entryId));
  }

  // --- Recruitment ---
  getRecruitmentEvidence(caseId: string): Observable<RecruitmentEvidence[]> {
    return this.http.get<RecruitmentEvidence[]>(API_ENDPOINTS.RECRUITMENT_EVIDENCE(caseId));
  }

  addRecruitmentEvidence(caseId: string, evidence: RecruitmentEvidence): Observable<RecruitmentEvidence> {
    return this.http.post<RecruitmentEvidence>(API_ENDPOINTS.RECRUITMENT_EVIDENCE(caseId), evidence);
  }

  deleteRecruitmentEvidence(caseId: string, entryId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.RECRUITMENT_EVIDENCE_ENTRY(caseId, entryId));
  }

  getCandidates(caseId: string): Observable<CandidateComparison[]> {
    return this.http.get<CandidateComparison[]>(API_ENDPOINTS.CANDIDATES(caseId));
  }

  addCandidate(caseId: string, candidate: CandidateComparison): Observable<CandidateComparison> {
    return this.http.post<CandidateComparison>(API_ENDPOINTS.CANDIDATES(caseId), candidate);
  }

  deleteCandidate(caseId: string, candidateId: string): Observable<void> {
    return this.http.delete<void>(API_ENDPOINTS.CANDIDATE_ENTRY(caseId, candidateId));
  }

  // --- Forms & Package Automation (Section 4.1) ---
  getFormProfiles(caseId: string): Observable<PackageProfileSummary[]> {
    return this.http.get<PackageProfileSummary[]>(API_ENDPOINTS.FORM_PROFILES(caseId));
  }

  getCanonicalSnapshot(caseId: string): Observable<CanonicalDataSnapshot> {
    return this.http.get<CanonicalDataSnapshot>(API_ENDPOINTS.FORM_CANONICAL_SNAPSHOT(caseId));
  }

  getMappingPreview(caseId: string, packageProfileId: string): Observable<MappingPreview> {
    return this.http.get<MappingPreview>(API_ENDPOINTS.FORM_MAPPING_PREVIEW(caseId), {
      params: new HttpParams().set('packageProfileId', packageProfileId)
    });
  }

  getReadinessReport(caseId: string, packageProfileId: string): Observable<PackageReadinessReport> {
    return this.http.get<PackageReadinessReport>(API_ENDPOINTS.FORM_READINESS(caseId), {
      params: new HttpParams().set('packageProfileId', packageProfileId)
    });
  }

  generateDraftForms(caseId: string, profileId: string): Observable<CaseFormDraft[]> {
    return this.http.post<CaseFormDraft[]>(API_ENDPOINTS.FORM_GENERATE_DRAFTS(caseId, profileId), null);
  }

  getFormDrafts(caseId: string): Observable<CaseFormDraft[]> {
    return this.http.get<CaseFormDraft[]>(API_ENDPOINTS.FORM_DRAFTS(caseId));
  }

  regenerateDraft(caseId: string, draftId: string): Observable<CaseFormDraft> {
    return this.http.post<CaseFormDraft>(API_ENDPOINTS.FORM_DRAFT_REGENERATE(caseId, draftId), null);
  }

  uploadFilledForm(caseId: string, formDefinitionId: string, file: File): Observable<CaseFormDraft> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('formDefinitionId', String(formDefinitionId));
    return this.http.post<CaseFormDraft>(API_ENDPOINTS.FORM_DRAFT_UPLOAD(caseId), formData);
  }

  downloadDraft(caseId: string, draftId: string): Observable<Blob> {
    return this.http.get(API_ENDPOINTS.FORM_DRAFT_DOWNLOAD(caseId, draftId), { responseType: 'blob' });
  }

  // --- Packages (Milestone 5) ---
  createOrRefreshPackage(caseId: string, packageProfileId: string): Observable<CasePackage> {
    return this.http.post<CasePackage>(API_ENDPOINTS.FORM_PACKAGES(caseId), null, {
      params: new HttpParams().set('packageProfileId', packageProfileId)
    });
  }

  getPackages(caseId: string): Observable<CasePackage[]> {
    return this.http.get<CasePackage[]>(API_ENDPOINTS.FORM_PACKAGES(caseId));
  }

  refreshPackage(caseId: string, packageId: string): Observable<CasePackage> {
    return this.http.post<CasePackage>(API_ENDPOINTS.FORM_PACKAGE_REFRESH(caseId, packageId), null);
  }

  getPackageReadiness(caseId: string, packageId: string): Observable<PackageReadinessReport> {
    return this.http.get<PackageReadinessReport>(API_ENDPOINTS.FORM_PACKAGE_READINESS(caseId, packageId));
  }

  getPackageIndex(caseId: string, packageId: string): Observable<PackageIndex> {
    return this.http.get<PackageIndex>(API_ENDPOINTS.FORM_PACKAGE_INDEX(caseId, packageId));
  }

  resolvePackageIssue(caseId: string, packageId: string, issueId: string, notes?: string): Observable<PackageReadinessReport> {
    let params = new HttpParams();
    if (notes) params = params.set('notes', notes);
    return this.http.post<PackageReadinessReport>(API_ENDPOINTS.FORM_PACKAGE_RESOLVE_ISSUE(caseId, packageId, issueId), null, { params });
  }

  approvePackage(caseId: string, packageId: string, acknowledged: boolean, notes?: string): Observable<CasePackage> {
    let params = new HttpParams().set('acknowledged', acknowledged);
    if (notes) params = params.set('notes', notes);
    return this.http.post<CasePackage>(API_ENDPOINTS.FORM_PACKAGE_APPROVE(caseId, packageId), null, { params });
  }

  downloadPackage(caseId: string, packageId: string): Observable<Blob> {
    return this.http.get(API_ENDPOINTS.FORM_PACKAGE_DOWNLOAD(caseId, packageId), { responseType: 'blob' });
  }

  // --- Admin form catalogue (Milestone 6) ---
  getCatalogueForms(): Observable<FormDefinition[]> {
    return this.http.get<FormDefinition[]>(API_ENDPOINTS.CATALOGUE_FORMS);
  }

  createCatalogueForm(form: Partial<FormDefinition>): Observable<FormDefinition> {
    return this.http.post<FormDefinition>(API_ENDPOINTS.CATALOGUE_FORMS, form);
  }

  getCatalogueFormFields(formId: string): Observable<FormFieldDefinition[]> {
    return this.http.get<FormFieldDefinition[]>(API_ENDPOINTS.CATALOGUE_FORM_FIELDS(formId));
  }

  inspectCatalogueForm(formId: string): Observable<FormInspectionResult> {
    return this.http.post<FormInspectionResult>(API_ENDPOINTS.CATALOGUE_FORM_INSPECT(formId), null);
  }

  getCatalogueFormMappings(formId: string): Observable<FormMappingVersion[]> {
    return this.http.get<FormMappingVersion[]>(API_ENDPOINTS.CATALOGUE_FORM_MAPPINGS(formId));
  }

  approveCatalogueMapping(formId: string, mvId: string): Observable<FormMappingVersion> {
    return this.http.post<FormMappingVersion>(API_ENDPOINTS.CATALOGUE_FORM_MAPPING_APPROVE(formId, mvId), null);
  }

  getCatalogueProfiles(): Observable<PackageProfileAdmin[]> {
    return this.http.get<PackageProfileAdmin[]>(API_ENDPOINTS.CATALOGUE_PROFILES);
  }

  createCatalogueProfile(profile: Partial<PackageProfileAdmin>): Observable<PackageProfileAdmin> {
    return this.http.post<PackageProfileAdmin>(API_ENDPOINTS.CATALOGUE_PROFILES, profile);
  }

  updateCatalogueProfile(profileId: string, profile: Partial<PackageProfileAdmin>): Observable<PackageProfileAdmin> {
    return this.http.put<PackageProfileAdmin>(API_ENDPOINTS.CATALOGUE_PROFILE(profileId), profile);
  }
}
