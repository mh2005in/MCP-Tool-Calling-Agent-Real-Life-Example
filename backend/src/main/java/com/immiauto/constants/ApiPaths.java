package com.immiauto.constants;

public final class ApiPaths {

    private ApiPaths() {}

    public static final String API_VERSION = "/v1";

    public static final String ME_CONTROLLER = API_VERSION + "/me";

    public static final String CLIENTS_CONTROLLER = API_VERSION + "/consultants/{consultantId}/clients";
    public static final String CLIENTS_CREATE = "/create";
    public static final String CLIENTS_GET = "/get";
    public static final String CLIENTS_SEARCH = "/search";
    public static final String CLIENTS_UPDATE = "/update/{id}";
    public static final String CLIENTS_GET_BY_ID = "/getById/{id}";
    
    public static final String CASES_CONTROLLER = API_VERSION + "/consultants/{consultantId}/cases";
    public static final String CASES_CREATE = "/create";
    public static final String CASES_GET = "/get";
    public static final String CASES_GET_BY_ID = "/get/{id}";
    public static final String CASES_UPDATE = "/update/{id}";
    public static final String CASES_STATUS_BY_ID = "/status/{id}";
    public static final String CASES_LEAD_STATUS_BY_ID = "/lead-status/{id}";
    public static final String CASES_DEADLINE = "/deadlines";
    
    public static final String CHECKLIST_CONTROLLER = API_VERSION + "/checklist";
    public static final String CHECKLIST_CREATE = "/create/{caseId}";
    public static final String CHECKLIST_GET = "/get/{caseId}";
    public static final String CHECKLIST_MISSING = "/missing/{caseId}";
    public static final String CHECKLIST_UPDATE_STATUS = "/update/items/status/{caseId}/{itemId}";
    public static final String CHECKLIST_DELETE = "/delete/items/{itemId}";
    public static final String CHECKLIST_CLIENT_VIEW = "/client/{caseId}";
    
    public static final String DOCUMENTS_CONTROLLER = API_VERSION + "/document";
    public static final String DOCUMENTS_UPLOAD = "/upload/{caseId}";
    public static final String DOCUMENTS_GET = "/get/{caseId}";
    public static final String DOCUMENTS_REVIEW = "/review/{caseId}/{docId}";
    
    
    public static final String REMINDERS_CONTROLLER = API_VERSION + "/reminders";
    public static final String REMINDERS_CREATE = "/create/{caseId}";
    public static final String REMINDERS_GET = "/get/{caseId}";
    public static final String REMINDERS_APPROVE = "/approve/{reminderId}";
    public static final String REMINDERS_PENDING = "/pending";
    public static final String REMINDERS_PENDING_BY_CONSULTANT = "/pending/{consultantId}";
    
    
    public static final String DASHBOARD_CONTROLLER = API_VERSION + "/dashboard";
    public static final String DASHBOARD_GET_BY_ID = "/get/{consultantId}";
    public static final String DASHBOARD_ORG = "/organization";

    public static final String CONSULTANTS_CONTROLLER = API_VERSION + "/consultants";
    public static final String CONSULTANTS_CREATE = "/create";
    public static final String CONSULTANTS_GET = "/get";
    public static final String CONSULTANTS_GET_BY_ID = "/getById/{id}";
    public static final String CONSULTANTS_UPDATE = "/update/{id}";
    public static final String CONSULTANTS_PROFILE = "/profile/{id}";
    public static final String CONSULTANTS_SET_ACTIVE = "/status/{id}";

    public static final String TEMPLATES_CONTROLLER = API_VERSION + "/templates";
    public static final String TEMPLATES_CREATE = "/create";
    public static final String TEMPLATES_UPDATE = "/update/{templateId}";
    public static final String TEMPLATES_REVIEW = "/review/{templateId}";
    public static final String TEMPLATES_APPROVE = "/approve/{templateId}";
    public static final String TEMPLATES_REVOKE = "/revoke/{templateId}";
    public static final String TEMPLATES_GET_BY_TYPE = "/by-type";
    public static final String TEMPLATES_GET_APPROVED = "/approved";
    public static final String TEMPLATES_GET_BY_ID = "/get/{templateId}";
    public static final String TEMPLATES_GET_ALL = "/get";
    public static final String TEMPLATES_DELETE = "/delete/{templateId}";
    public static final String TEMPLATES_AUDIT = "/audit/{templateId}";

    public static final String CLIENTS_GET_ALL_ADMIN = "/admin/all";
    public static final String CLIENTS_GET_BY_TARGET_CONSULTANT = "/admin/by-consultant/{targetConsultantId}";
    public static final String CLIENTS_SEARCH_ADMIN = "/admin/search";

    public static final String CASES_GET_ALL_ADMIN = "/admin/all";
    public static final String CASES_GET_BY_TARGET_CONSULTANT = "/admin/by-consultant/{targetConsultantId}";

    public static final String INTAKE_CONTROLLER = API_VERSION + "/intake";
    public static final String INTAKE_QUESTIONS = "/questions";
    public static final String INTAKE_QUESTIONS_BY_SECTION = "/questions/section";
    public static final String INTAKE_SECTIONS = "/sections";
    public static final String INTAKE_SUBMIT = "/submit/{caseId}";
    public static final String INTAKE_RESPONSES = "/responses/{caseId}";
    public static final String INTAKE_SUMMARY = "/summary/{caseId}";
    public static final String INTAKE_FLAG = "/flag/{responseId}";
    public static final String INTAKE_UPDATE_RESPONSE = "/response/{responseId}";
    public static final String INTAKE_GENERATE_CHECKLIST = "/generate-checklist/{caseId}";

    public static final String MCP_CONTROLLER = API_VERSION + "/mcp";
    public static final String MCP_INTAKE_SUMMARY = "/cases/{caseId}/intake-summary";
    public static final String MCP_DOCUMENTS = "/cases/{caseId}/documents";
    public static final String MCP_CHECKLIST_STATUS = "/cases/{caseId}/checklist-status";
    public static final String MCP_CONSISTENCY_REPORT = "/cases/{caseId}/consistency-report";
    public static final String MCP_TIMELINE = "/cases/{caseId}/timeline";
    public static final String MCP_CLASSIFY_DOCUMENT = "/cases/{caseId}/classify-document";
    public static final String MCP_REMINDERS_PENDING = "/cases/{caseId}/reminders/pending";
    public static final String MCP_VALIDATE_OUTPUT = "/validate-output";
    public static final String MCP_CASE_OVERVIEW = "/cases/{caseId}/overview";

    public static final String MCP_CLIENTS_LIST = "/clients";
    public static final String MCP_CLIENT_GET = "/clients/{clientId}";
    public static final String MCP_CLIENT_CREATE = "/clients/create";

    public static final String CASES_SEARCH_BY_NUMBER = "/search-by-number";
    public static final String CLIENTS_SEARCH_BY_NUMBER = "/search-by-number";
    public static final String CONSULTANTS_SEARCH_BY_NUMBER = "/search-by-number";

    public static final String MCP_SEARCH_BY_CASE_NUMBER = "/search/case-number";
    public static final String MCP_SEARCH_BY_CLIENT_NUMBER = "/search/client-number";
    public static final String MCP_SEARCH_BY_CONSULTANT_NUMBER = "/search/consultant-number";

    public static final String MCP_AUDIT = "/audit";
}
