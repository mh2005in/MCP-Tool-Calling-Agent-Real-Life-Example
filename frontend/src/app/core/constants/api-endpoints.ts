import { environment } from '../../../environments/environment';

const BASE = environment.apiUrl;

export const API_ENDPOINTS = {
  CLIENTS_CREATE: (consultantId: string) => `${BASE}/consultants/${consultantId}/clients/create`,
  CLIENTS_GET: (consultantId: string) => `${BASE}/consultants/${consultantId}/clients/get`,
  CLIENTS_GET_BY_ID: (consultantId: string, id: string) => `${BASE}/consultants/${consultantId}/clients/getById/${id}`,
  CLIENTS_SEARCH: (consultantId: string) => `${BASE}/consultants/${consultantId}/clients/search`,
  CLIENTS_UPDATE: (consultantId: string, id: string) => `${BASE}/consultants/${consultantId}/clients/update/${id}`,
  CLIENTS_SEARCH_BY_NUMBER: (consultantId: string) => `${BASE}/consultants/${consultantId}/clients/search-by-number`,

  CASES_CREATE: (consultantId: string) => `${BASE}/consultants/${consultantId}/cases/create`,
  CASES_GET: (consultantId: string) => `${BASE}/consultants/${consultantId}/cases/get`,
  CASES_GET_BY_ID: (consultantId: string, id: string) => `${BASE}/consultants/${consultantId}/cases/get/${id}`,
  CASES_UPDATE: (consultantId: string, id: string) => `${BASE}/consultants/${consultantId}/cases/update/${id}`,
  CASES_STATUS: (consultantId: string, id: string) => `${BASE}/consultants/${consultantId}/cases/status/${id}`,
  CASES_LEAD_STATUS: (consultantId: string, id: string) => `${BASE}/consultants/${consultantId}/cases/lead-status/${id}`,
  CASES_DEADLINES: (consultantId: string) => `${BASE}/consultants/${consultantId}/cases/deadlines`,
  CASES_SEARCH_BY_NUMBER: (consultantId: string) => `${BASE}/consultants/${consultantId}/cases/search-by-number`,

  CHECKLIST_CREATE: (caseId: string) => `${BASE}/checklist/create/${caseId}`,
  CHECKLIST_GET: (caseId: string) => `${BASE}/checklist/get/${caseId}`,
  CHECKLIST_MISSING: (caseId: string) => `${BASE}/checklist/missing/${caseId}`,
  CHECKLIST_UPDATE_STATUS: (caseId: string, itemId: string) => `${BASE}/checklist/update/items/status/${caseId}/${itemId}`,
  CHECKLIST_DELETE: (itemId: string) => `${BASE}/checklist/delete/items/${itemId}`,

  DOCUMENTS_UPLOAD: (caseId: string) => `${BASE}/document/upload/${caseId}`,
  DOCUMENTS_GET: (caseId: string) => `${BASE}/document/get/${caseId}`,
  DOCUMENTS_REVIEW: (caseId: string, docId: string) => `${BASE}/document/review/${caseId}/${docId}`,

  REMINDERS_CREATE: (caseId: string) => `${BASE}/reminders/create/${caseId}`,
  REMINDERS_GET: (caseId: string) => `${BASE}/reminders/get/${caseId}`,
  REMINDERS_APPROVE: (reminderId: string) => `${BASE}/reminders/approve/${reminderId}`,
  REMINDERS_PENDING: (consultantId: string) => `${BASE}/reminders/pending/${consultantId}`,

  DASHBOARD_GET: (consultantId: string) => `${BASE}/dashboard/get/${consultantId}`,
  DASHBOARD_ORG: `${BASE}/dashboard/organization`,

  CONSULTANTS_CREATE: `${BASE}/consultants/create`,
  CONSULTANTS_GET: `${BASE}/consultants/get`,
  CONSULTANTS_GET_BY_ID: (id: string) => `${BASE}/consultants/getById/${id}`,
  CONSULTANTS_UPDATE: (id: string) => `${BASE}/consultants/update/${id}`,
  CONSULTANTS_PROFILE: (id: string) => `${BASE}/consultants/profile/${id}`,
  CONSULTANTS_SET_ACTIVE: (id: string) => `${BASE}/consultants/status/${id}`,
  CONSULTANTS_SEARCH_BY_NUMBER: `${BASE}/consultants/search-by-number`,

  TEMPLATES_CREATE: `${BASE}/templates/create`,
  TEMPLATES_UPDATE: (templateId: string) => `${BASE}/templates/update/${templateId}`,
  TEMPLATES_REVIEW: (templateId: string) => `${BASE}/templates/review/${templateId}`,
  TEMPLATES_APPROVE: (templateId: string) => `${BASE}/templates/approve/${templateId}`,
  TEMPLATES_REVOKE: (templateId: string) => `${BASE}/templates/revoke/${templateId}`,
  TEMPLATES_GET_BY_TYPE: `${BASE}/templates/by-type`,
  TEMPLATES_GET_APPROVED: `${BASE}/templates/approved`,
  TEMPLATES_GET_BY_ID: (templateId: string) => `${BASE}/templates/get/${templateId}`,
  TEMPLATES_GET_ALL: `${BASE}/templates/get`,
  TEMPLATES_DELETE: (templateId: string) => `${BASE}/templates/delete/${templateId}`,
  TEMPLATES_AUDIT: (templateId: string) => `${BASE}/templates/audit/${templateId}`,

  // Admin (cross-consultant access — routed through existing controllers)
  ADMIN_CLIENTS_ALL: (consultantId: string) => `${BASE}/consultants/${consultantId}/clients/admin/all`,
  ADMIN_CLIENTS_BY_CONSULTANT: (consultantId: string, targetId: string) => `${BASE}/consultants/${consultantId}/clients/admin/by-consultant/${targetId}`,
  ADMIN_CLIENT_BY_ID: (consultantId: string, clientId: string) => `${BASE}/consultants/${consultantId}/clients/getById/${clientId}`,
  ADMIN_CLIENTS_SEARCH: (consultantId: string) => `${BASE}/consultants/${consultantId}/clients/admin/search`,
  ADMIN_CASES_ALL: (consultantId: string) => `${BASE}/consultants/${consultantId}/cases/admin/all`,
  ADMIN_CASES_BY_CONSULTANT: (consultantId: string, targetId: string) => `${BASE}/consultants/${consultantId}/cases/admin/by-consultant/${targetId}`,
  ADMIN_CASE_BY_ID: (consultantId: string, caseId: string) => `${BASE}/consultants/${consultantId}/cases/get/${caseId}`,

  CHECKLIST_CLIENT_VIEW: (caseId: string) => `${BASE}/checklist/client/${caseId}`,

  INTAKE_QUESTIONS: `${BASE}/intake/questions`,
  INTAKE_QUESTIONS_BY_SECTION: `${BASE}/intake/questions/section`,
  INTAKE_SECTIONS: `${BASE}/intake/sections`,
  INTAKE_SUBMIT: (caseId: string) => `${BASE}/intake/submit/${caseId}`,
  INTAKE_RESPONSES: (caseId: string) => `${BASE}/intake/responses/${caseId}`,
  INTAKE_SUMMARY: (caseId: string) => `${BASE}/intake/summary/${caseId}`,
  INTAKE_FLAG: (responseId: string) => `${BASE}/intake/flag/${responseId}`,
  INTAKE_UPDATE_RESPONSE: (responseId: string) => `${BASE}/intake/response/${responseId}`,
  INTAKE_GENERATE_CHECKLIST: (caseId: string) => `${BASE}/intake/generate-checklist/${caseId}`,

  // Expiry Alerts
  EXPIRY_ALERTS_BY_CONSULTANT: (consultantId: string) => `${BASE}/consultants/${consultantId}/expiry-alerts`,
  EXPIRY_ALERTS_BY_CASE: (caseId: string) => `${BASE}/cases/${caseId}/expiry-alerts`,
  EXPIRY_ALERT_ACKNOWLEDGE: (alertId: string) => `${BASE}/expiry-alerts/${alertId}/acknowledge`,
  EXPIRY_ALERTS_SCAN: `${BASE}/expiry-alerts/scan`,

  // Automation
  DOCUMENT_CLASSIFY: `${BASE}/documents/classify`,
  CONSISTENCY_CHECK: (caseId: string) => `${BASE}/cases/${caseId}/consistency-check`,
  POLICE_CERTIFICATES: (caseId: string) => `${BASE}/cases/${caseId}/police-certificates`,
  LMIA_COMPLIANCE: (caseId: string) => `${BASE}/cases/${caseId}/lmia-compliance`,

  // Party Portals
  PARTIES_CREATE: (caseId: string) => `${BASE}/cases/${caseId}/parties`,
  PARTIES_GET: (caseId: string) => `${BASE}/cases/${caseId}/parties`,
  PARTY_UPDATE: (partyId: string) => `${BASE}/parties/${partyId}`,
  PARTY_DELETE: (partyId: string) => `${BASE}/parties/${partyId}`,
  PORTAL_BY_TOKEN: (token: string) => `${BASE}/portal/${token}`,

  // Travel History
  TRAVEL_HISTORY: (caseId: string) => `${BASE}/cases/${caseId}/travel-history`,
  TRAVEL_HISTORY_ENTRY: (caseId: string, entryId: string) => `${BASE}/cases/${caseId}/travel-history/${entryId}`,
  PHYSICAL_PRESENCE: (caseId: string) => `${BASE}/cases/${caseId}/physical-presence`,

  // Work History
  WORK_HISTORY: (caseId: string) => `${BASE}/cases/${caseId}/work-history`,
  WORK_HISTORY_ENTRY: (caseId: string, entryId: string) => `${BASE}/cases/${caseId}/work-history/${entryId}`,

  // Relationship Timeline
  RELATIONSHIP_TIMELINE: (caseId: string) => `${BASE}/cases/${caseId}/relationship-timeline`,
  RELATIONSHIP_TIMELINE_ENTRY: (caseId: string, entryId: string) => `${BASE}/cases/${caseId}/relationship-timeline/${entryId}`,

  // Recruitment
  RECRUITMENT_EVIDENCE: (caseId: string) => `${BASE}/cases/${caseId}/recruitment-evidence`,
  RECRUITMENT_EVIDENCE_ENTRY: (caseId: string, entryId: string) => `${BASE}/cases/${caseId}/recruitment-evidence/${entryId}`,
  CANDIDATES: (caseId: string) => `${BASE}/cases/${caseId}/candidates`,
  CANDIDATE_ENTRY: (caseId: string, candidateId: string) => `${BASE}/cases/${caseId}/candidates/${candidateId}`,

  // Forms & Package Automation (Section 4.1)
  FORM_PROFILES: (caseId: string) => `${BASE}/cases/${caseId}/form-automation/profiles`,
  FORM_CANONICAL_SNAPSHOT: (caseId: string) => `${BASE}/cases/${caseId}/form-automation/canonical-snapshot`,
  FORM_MAPPING_PREVIEW: (caseId: string) => `${BASE}/cases/${caseId}/form-automation/mapping-preview`,
  FORM_READINESS: (caseId: string) => `${BASE}/cases/${caseId}/form-automation/readiness`,
  FORM_GENERATE_DRAFTS: (caseId: string, profileId: string) => `${BASE}/cases/${caseId}/form-automation/profiles/${profileId}/generate-drafts`,
  FORM_DRAFTS: (caseId: string) => `${BASE}/cases/${caseId}/form-automation/drafts`,
  FORM_DRAFT: (caseId: string, draftId: string) => `${BASE}/cases/${caseId}/form-automation/drafts/${draftId}`,
  FORM_DRAFT_REGENERATE: (caseId: string, draftId: string) => `${BASE}/cases/${caseId}/form-automation/drafts/${draftId}/regenerate`,
  FORM_DRAFT_DOWNLOAD: (caseId: string, draftId: string) => `${BASE}/cases/${caseId}/form-automation/drafts/${draftId}/download`,
  FORM_DRAFT_UPLOAD: (caseId: string) => `${BASE}/cases/${caseId}/form-automation/drafts/upload`,
  FORM_PACKAGES: (caseId: string) => `${BASE}/cases/${caseId}/form-automation/packages`,
  FORM_PACKAGE: (caseId: string, packageId: string) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}`,
  FORM_PACKAGE_REFRESH: (caseId: string, packageId: string) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/refresh`,
  FORM_PACKAGE_READINESS: (caseId: string, packageId: string) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/readiness`,
  FORM_PACKAGE_INDEX: (caseId: string, packageId: string) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/index`,
  FORM_PACKAGE_APPROVE: (caseId: string, packageId: string) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/approve`,
  FORM_PACKAGE_DOWNLOAD: (caseId: string, packageId: string) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/download`,
  FORM_PACKAGE_RESOLVE_ISSUE: (caseId: string, packageId: string, issueId: string) => `${BASE}/cases/${caseId}/form-automation/packages/${packageId}/issues/${issueId}/resolve`,

  // Admin form catalogue (Section 4.1 - Milestone 6)
  CATALOGUE_FORMS: `${BASE}/forms`,
  CATALOGUE_FORM: (formId: string) => `${BASE}/forms/${formId}`,
  CATALOGUE_FORM_FIELDS: (formId: string) => `${BASE}/forms/${formId}/fields`,
  CATALOGUE_FORM_INSPECT: (formId: string) => `${BASE}/forms/${formId}/inspect`,
  CATALOGUE_FORM_MAPPINGS: (formId: string) => `${BASE}/forms/${formId}/mappings`,
  CATALOGUE_FORM_MAPPING_APPROVE: (formId: string, mvId: string) => `${BASE}/forms/${formId}/mappings/${mvId}/approve`,
  CATALOGUE_PROFILES: `${BASE}/package-profiles`,
  CATALOGUE_PROFILE: (profileId: string) => `${BASE}/package-profiles/${profileId}`,
} as const;
