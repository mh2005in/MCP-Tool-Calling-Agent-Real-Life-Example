import { environment } from '../../../environments/environment';

const BASE = environment.apiUrl;

export const API_ENDPOINTS = {
  CLIENTS_CREATE: (consultantId: number) => `${BASE}/consultants/${consultantId}/clients/create`,
  CLIENTS_GET: (consultantId: number) => `${BASE}/consultants/${consultantId}/clients/get`,
  CLIENTS_GET_BY_ID: (consultantId: number, id: number) => `${BASE}/consultants/${consultantId}/clients/getById/${id}`,
  CLIENTS_SEARCH: (consultantId: number) => `${BASE}/consultants/${consultantId}/clients/search`,
  CLIENTS_UPDATE: (consultantId: number, id: number) => `${BASE}/consultants/${consultantId}/clients/update/${id}`,
  CLIENTS_SEARCH_BY_NUMBER: (consultantId: number) => `${BASE}/consultants/${consultantId}/clients/search-by-number`,

  CASES_CREATE: (consultantId: number) => `${BASE}/consultants/${consultantId}/cases/create`,
  CASES_GET: (consultantId: number) => `${BASE}/consultants/${consultantId}/cases/get`,
  CASES_GET_BY_ID: (consultantId: number, id: number) => `${BASE}/consultants/${consultantId}/cases/get/${id}`,
  CASES_UPDATE: (consultantId: number, id: number) => `${BASE}/consultants/${consultantId}/cases/update/${id}`,
  CASES_STATUS: (consultantId: number, id: number) => `${BASE}/consultants/${consultantId}/cases/status/${id}`,
  CASES_LEAD_STATUS: (consultantId: number, id: number) => `${BASE}/consultants/${consultantId}/cases/lead-status/${id}`,
  CASES_DEADLINES: (consultantId: number) => `${BASE}/consultants/${consultantId}/cases/deadlines`,
  CASES_SEARCH_BY_NUMBER: (consultantId: number) => `${BASE}/consultants/${consultantId}/cases/search-by-number`,

  CHECKLIST_CREATE: (caseId: number) => `${BASE}/checklist/create/${caseId}`,
  CHECKLIST_GET: (caseId: number) => `${BASE}/checklist/get/${caseId}`,
  CHECKLIST_MISSING: (caseId: number) => `${BASE}/checklist/missing/${caseId}`,
  CHECKLIST_UPDATE_STATUS: (caseId: number, itemId: number) => `${BASE}/checklist/update/items/status/${caseId}/${itemId}`,
  CHECKLIST_DELETE: (itemId: number) => `${BASE}/checklist/delete/items/${itemId}`,

  DOCUMENTS_UPLOAD: (caseId: number) => `${BASE}/document/upload/${caseId}`,
  DOCUMENTS_GET: (caseId: number) => `${BASE}/document/get/${caseId}`,
  DOCUMENTS_REVIEW: (caseId: number, docId: number) => `${BASE}/document/review/${caseId}/${docId}`,

  REMINDERS_CREATE: (caseId: number) => `${BASE}/reminders/create/${caseId}`,
  REMINDERS_GET: (caseId: number) => `${BASE}/reminders/get/${caseId}`,
  REMINDERS_APPROVE: (reminderId: number) => `${BASE}/reminders/approve/${reminderId}`,
  REMINDERS_PENDING: (consultantId: number) => `${BASE}/reminders/pending/${consultantId}`,

  DASHBOARD_GET: (consultantId: number) => `${BASE}/dashboard/get/${consultantId}`,
  DASHBOARD_ORG: `${BASE}/dashboard/organization`,

  CONSULTANTS_CREATE: `${BASE}/consultants/create`,
  CONSULTANTS_GET: `${BASE}/consultants/get`,
  CONSULTANTS_GET_BY_ID: (id: number) => `${BASE}/consultants/getById/${id}`,
  CONSULTANTS_UPDATE: (id: number) => `${BASE}/consultants/update/${id}`,
  CONSULTANTS_PROFILE: (id: number) => `${BASE}/consultants/profile/${id}`,
  CONSULTANTS_SET_ACTIVE: (id: number) => `${BASE}/consultants/status/${id}`,
  CONSULTANTS_SEARCH_BY_NUMBER: `${BASE}/consultants/search-by-number`,

  TEMPLATES_CREATE: `${BASE}/templates/create`,
  TEMPLATES_UPDATE: (templateId: number) => `${BASE}/templates/update/${templateId}`,
  TEMPLATES_REVIEW: (templateId: number) => `${BASE}/templates/review/${templateId}`,
  TEMPLATES_APPROVE: (templateId: number) => `${BASE}/templates/approve/${templateId}`,
  TEMPLATES_REVOKE: (templateId: number) => `${BASE}/templates/revoke/${templateId}`,
  TEMPLATES_GET_BY_TYPE: `${BASE}/templates/by-type`,
  TEMPLATES_GET_APPROVED: `${BASE}/templates/approved`,
  TEMPLATES_GET_BY_ID: (templateId: number) => `${BASE}/templates/get/${templateId}`,
  TEMPLATES_GET_ALL: `${BASE}/templates/get`,
  TEMPLATES_DELETE: (templateId: number) => `${BASE}/templates/delete/${templateId}`,
  TEMPLATES_AUDIT: (templateId: number) => `${BASE}/templates/audit/${templateId}`,

  // Admin (cross-consultant access — routed through existing controllers)
  ADMIN_CLIENTS_ALL: (consultantId: number) => `${BASE}/consultants/${consultantId}/clients/admin/all`,
  ADMIN_CLIENTS_BY_CONSULTANT: (consultantId: number, targetId: number) => `${BASE}/consultants/${consultantId}/clients/admin/by-consultant/${targetId}`,
  ADMIN_CLIENT_BY_ID: (consultantId: number, clientId: number) => `${BASE}/consultants/${consultantId}/clients/getById/${clientId}`,
  ADMIN_CLIENTS_SEARCH: (consultantId: number) => `${BASE}/consultants/${consultantId}/clients/admin/search`,
  ADMIN_CASES_ALL: (consultantId: number) => `${BASE}/consultants/${consultantId}/cases/admin/all`,
  ADMIN_CASES_BY_CONSULTANT: (consultantId: number, targetId: number) => `${BASE}/consultants/${consultantId}/cases/admin/by-consultant/${targetId}`,
  ADMIN_CASE_BY_ID: (consultantId: number, caseId: number) => `${BASE}/consultants/${consultantId}/cases/get/${caseId}`,

  CHECKLIST_CLIENT_VIEW: (caseId: number) => `${BASE}/checklist/client/${caseId}`,

  INTAKE_QUESTIONS: `${BASE}/intake/questions`,
  INTAKE_QUESTIONS_BY_SECTION: `${BASE}/intake/questions/section`,
  INTAKE_SECTIONS: `${BASE}/intake/sections`,
  INTAKE_SUBMIT: (caseId: number) => `${BASE}/intake/submit/${caseId}`,
  INTAKE_RESPONSES: (caseId: number) => `${BASE}/intake/responses/${caseId}`,
  INTAKE_SUMMARY: (caseId: number) => `${BASE}/intake/summary/${caseId}`,
  INTAKE_FLAG: (responseId: number) => `${BASE}/intake/flag/${responseId}`,
  INTAKE_UPDATE_RESPONSE: (responseId: number) => `${BASE}/intake/response/${responseId}`,
  INTAKE_GENERATE_CHECKLIST: (caseId: number) => `${BASE}/intake/generate-checklist/${caseId}`,

  // Expiry Alerts
  EXPIRY_ALERTS_BY_CONSULTANT: (consultantId: number) => `${BASE}/consultants/${consultantId}/expiry-alerts`,
  EXPIRY_ALERTS_BY_CASE: (caseId: number) => `${BASE}/cases/${caseId}/expiry-alerts`,
  EXPIRY_ALERT_ACKNOWLEDGE: (alertId: number) => `${BASE}/expiry-alerts/${alertId}/acknowledge`,
  EXPIRY_ALERTS_SCAN: `${BASE}/expiry-alerts/scan`,

  // Automation
  DOCUMENT_CLASSIFY: `${BASE}/documents/classify`,
  CONSISTENCY_CHECK: (caseId: number) => `${BASE}/cases/${caseId}/consistency-check`,
  POLICE_CERTIFICATES: (caseId: number) => `${BASE}/cases/${caseId}/police-certificates`,
  LMIA_COMPLIANCE: (caseId: number) => `${BASE}/cases/${caseId}/lmia-compliance`,

  // Party Portals
  PARTIES_CREATE: (caseId: number) => `${BASE}/cases/${caseId}/parties`,
  PARTIES_GET: (caseId: number) => `${BASE}/cases/${caseId}/parties`,
  PARTY_UPDATE: (partyId: number) => `${BASE}/parties/${partyId}`,
  PARTY_DELETE: (partyId: number) => `${BASE}/parties/${partyId}`,
  PORTAL_BY_TOKEN: (token: string) => `${BASE}/portal/${token}`,

  // Travel History
  TRAVEL_HISTORY: (caseId: number) => `${BASE}/cases/${caseId}/travel-history`,
  TRAVEL_HISTORY_ENTRY: (caseId: number, entryId: number) => `${BASE}/cases/${caseId}/travel-history/${entryId}`,
  PHYSICAL_PRESENCE: (caseId: number) => `${BASE}/cases/${caseId}/physical-presence`,

  // Work History
  WORK_HISTORY: (caseId: number) => `${BASE}/cases/${caseId}/work-history`,
  WORK_HISTORY_ENTRY: (caseId: number, entryId: number) => `${BASE}/cases/${caseId}/work-history/${entryId}`,

  // Relationship Timeline
  RELATIONSHIP_TIMELINE: (caseId: number) => `${BASE}/cases/${caseId}/relationship-timeline`,
  RELATIONSHIP_TIMELINE_ENTRY: (caseId: number, entryId: number) => `${BASE}/cases/${caseId}/relationship-timeline/${entryId}`,

  // Recruitment
  RECRUITMENT_EVIDENCE: (caseId: number) => `${BASE}/cases/${caseId}/recruitment-evidence`,
  RECRUITMENT_EVIDENCE_ENTRY: (caseId: number, entryId: number) => `${BASE}/cases/${caseId}/recruitment-evidence/${entryId}`,
  CANDIDATES: (caseId: number) => `${BASE}/cases/${caseId}/candidates`,
  CANDIDATE_ENTRY: (caseId: number, candidateId: number) => `${BASE}/cases/${caseId}/candidates/${candidateId}`,

  // Forms & Package Automation (Section 4.1)
  FORM_PROFILES: (caseId: number) => `${BASE}/cases/${caseId}/form-automation/profiles`,
  FORM_CANONICAL_SNAPSHOT: (caseId: number) => `${BASE}/cases/${caseId}/form-automation/canonical-snapshot`,
  FORM_MAPPING_PREVIEW: (caseId: number) => `${BASE}/cases/${caseId}/form-automation/mapping-preview`,
  FORM_READINESS: (caseId: number) => `${BASE}/cases/${caseId}/form-automation/readiness`,
  FORM_GENERATE_DRAFTS: (caseId: number, profileId: number) => `${BASE}/cases/${caseId}/form-automation/profiles/${profileId}/generate-drafts`,
  FORM_DRAFTS: (caseId: number) => `${BASE}/cases/${caseId}/form-automation/drafts`,
  FORM_DRAFT: (caseId: number, draftId: number) => `${BASE}/cases/${caseId}/form-automation/drafts/${draftId}`,
  FORM_DRAFT_REGENERATE: (caseId: number, draftId: number) => `${BASE}/cases/${caseId}/form-automation/drafts/${draftId}/regenerate`,
  FORM_DRAFT_DOWNLOAD: (caseId: number, draftId: number) => `${BASE}/cases/${caseId}/form-automation/drafts/${draftId}/download`,
  FORM_DRAFT_UPLOAD: (caseId: number) => `${BASE}/cases/${caseId}/form-automation/drafts/upload`,
  FORM_PACKAGES: (caseId: number) => `${BASE}/cases/${caseId}/form-automation/packages`,
  FORM_PACKAGE: (caseId: number, packageId: number) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}`,
  FORM_PACKAGE_REFRESH: (caseId: number, packageId: number) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/refresh`,
  FORM_PACKAGE_READINESS: (caseId: number, packageId: number) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/readiness`,
  FORM_PACKAGE_INDEX: (caseId: number, packageId: number) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/index`,
  FORM_PACKAGE_APPROVE: (caseId: number, packageId: number) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/approve`,
  FORM_PACKAGE_DOWNLOAD: (caseId: number, packageId: number) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/download`,
  FORM_PACKAGE_RESOLVE_ISSUE: (caseId: number, packageId: number, issueId: number) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/issues/${issueId}/resolve`,

  // Admin form catalogue (Section 4.1 - Milestone 6)
  CATALOGUE_FORMS: `${BASE}/forms`,
  CATALOGUE_FORM: (formId: number) => `${BASE}/forms/${formId}`,
  CATALOGUE_FORM_FIELDS: (formId: number) => `${BASE}/forms/${formId}/fields`,
  CATALOGUE_FORM_INSPECT: (formId: number) => `${BASE}/forms/${formId}/inspect`,
  CATALOGUE_FORM_MAPPINGS: (formId: number) => `${BASE}/forms/${formId}/mappings`,
  CATALOGUE_FORM_MAPPING_APPROVE: (formId: number, mvId: number) => `${BASE}/forms/${formId}/mappings/${mvId}/approve`,
  CATALOGUE_PROFILES: `${BASE}/package-profiles`,
  CATALOGUE_PROFILE: (profileId: number) => `${BASE}/package-profiles/${profileId}`,
} as const;
