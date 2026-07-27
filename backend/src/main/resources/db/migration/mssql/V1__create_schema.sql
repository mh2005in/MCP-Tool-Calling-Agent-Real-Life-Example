-- ============================================================
-- Immigration Automation - Database Schema (SQL Server 2016+)
-- V1: Initial schema creation
-- ============================================================

-- ======================== TABLES ========================

-- Consultants
CREATE TABLE consultants (
    id                BIGINT        NOT NULL IDENTITY(1,1),
    consultant_number NVARCHAR(15)  NOT NULL,
    full_name         NVARCHAR(255) NOT NULL,
    email             NVARCHAR(255) NOT NULL,
    phone             NVARCHAR(255),
    license_number    NVARCHAR(255),
    company_name      NVARCHAR(255),
    [admin]           BIT           NOT NULL DEFAULT 0,
    active            BIT           NOT NULL DEFAULT 1,
    mcp_api_key       NVARCHAR(255) NOT NULL,
    created_at        DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at        DATETIME2,
    created_by        NVARCHAR(255),
    updated_by        NVARCHAR(255),
    CONSTRAINT pk_consultants PRIMARY KEY (id),
    CONSTRAINT uk_consultants_consultant_number UNIQUE (consultant_number),
    CONSTRAINT uk_consultants_email UNIQUE (email),
    CONSTRAINT uk_consultants_mcp_api_key UNIQUE (mcp_api_key)
);

-- Clients
CREATE TABLE clients (
    id                       BIGINT        NOT NULL IDENTITY(1,1),
    client_number            NVARCHAR(15)  NOT NULL,
    full_name                NVARCHAR(255) NOT NULL,
    email                    NVARCHAR(255) NOT NULL,
    phone                    NVARCHAR(255),
    whatsapp                 NVARCHAR(255),
    date_of_birth            DATE,
    country_of_citizenship   NVARCHAR(255),
    current_location         NVARCHAR(255),
    current_status           NVARCHAR(255),
    passport_number          NVARCHAR(255),
    marital_status           NVARCHAR(255),
    preferred_language       NVARCHAR(255),
    notes                    NVARCHAR(MAX),
    consultant_id            BIGINT        NOT NULL,
    created_at               DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at               DATETIME2,
    created_by               NVARCHAR(255),
    updated_by               NVARCHAR(255),
    CONSTRAINT pk_clients PRIMARY KEY (id),
    CONSTRAINT uk_clients_client_number UNIQUE (client_number),
    CONSTRAINT uk_clients_email UNIQUE (email),
    CONSTRAINT uk_client_name_dob_email UNIQUE (full_name, date_of_birth, email),
    CONSTRAINT fk_clients_consultant FOREIGN KEY (consultant_id) REFERENCES consultants (id)
);

-- Immigration Cases
CREATE TABLE immigration_cases (
    id                        BIGINT        NOT NULL IDENTITY(1,1),
    case_number               NVARCHAR(15)  NOT NULL,
    service_type              NVARCHAR(50)  NOT NULL,
    subtype                   NVARCHAR(50),
    applicant_role            NVARCHAR(50),
    lead_status               NVARCHAR(50)  NOT NULL,
    case_status               NVARCHAR(50)  NOT NULL,
    client_id                 BIGINT        NOT NULL,
    consultant_id             BIGINT        NOT NULL,
    intake_summary            NVARCHAR(MAX),
    consultant_notes          NVARCHAR(MAX),
    deadline                  DATE,
    urgency_reason            NVARCHAR(255),
    submission_date           DATE,
    application_number        NVARCHAR(255),
    portal_used               NVARCHAR(255),
    biometrics_status         NVARCHAR(255),
    medical_status            NVARCHAR(255),
    retainer_signed_date      DATE,
    retainer_document_path    NVARCHAR(255),
    engagement_letter_status  NVARCHAR(255),
    final_reviewed_by         NVARCHAR(255),
    final_reviewed_at         DATETIME2,
    final_review_notes        NVARCHAR(MAX),
    consultant_signed_off     BIT           NOT NULL DEFAULT 0,
    created_at                DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at                DATETIME2,
    created_by                NVARCHAR(255),
    updated_by                NVARCHAR(255),
    CONSTRAINT pk_immigration_cases PRIMARY KEY (id),
    CONSTRAINT uk_immigration_cases_case_number UNIQUE (case_number),
    CONSTRAINT fk_cases_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_cases_consultant FOREIGN KEY (consultant_id) REFERENCES consultants (id)
);

-- Documents
CREATE TABLE documents (
    id                    BIGINT        NOT NULL IDENTITY(1,1),
    case_id               BIGINT        NOT NULL,
    original_file_name    NVARCHAR(255) NOT NULL,
    stored_file_name      NVARCHAR(255) NOT NULL,
    file_path             NVARCHAR(255) NOT NULL,
    mime_type             NVARCHAR(255),
    file_size_bytes       BIGINT,
    document_category     NVARCHAR(255),
    document_type         NVARCHAR(255),
    [status]              NVARCHAR(50)  NOT NULL,
    expiry_date           DATE,
    review_note           NVARCHAR(MAX),
    rejection_reason      NVARCHAR(MAX),
    translation_required  BIT           NOT NULL DEFAULT 0,
    notarization_required BIT           NOT NULL DEFAULT 0,
    standardized_name     NVARCHAR(255),
    created_at            DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2,
    created_by            NVARCHAR(255),
    updated_by            NVARCHAR(255),
    CONSTRAINT pk_documents PRIMARY KEY (id),
    CONSTRAINT fk_documents_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Checklist Items
CREATE TABLE checklist_items (
    id                       BIGINT        NOT NULL IDENTITY(1,1),
    case_id                  BIGINT        NOT NULL,
    category                 NVARCHAR(255) NOT NULL,
    document_name            NVARCHAR(255) NOT NULL,
    [description]            NVARCHAR(MAX),
    [status]                 NVARCHAR(50)  NOT NULL,
    required                 BIT           NOT NULL DEFAULT 1,
    conditional              BIT           NOT NULL DEFAULT 0,
    condition_description    NVARCHAR(255),
    sort_order               INT           NOT NULL DEFAULT 0,
    consultant_review_note   NVARCHAR(MAX),
    assigned_party           NVARCHAR(255),
    document_id              BIGINT,
    created_at               DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at               DATETIME2,
    created_by               NVARCHAR(255),
    updated_by               NVARCHAR(255),
    CONSTRAINT pk_checklist_items PRIMARY KEY (id),
    CONSTRAINT fk_checklist_items_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id),
    CONSTRAINT fk_checklist_items_document FOREIGN KEY (document_id) REFERENCES documents (id)
);

-- Checklist Templates
CREATE TABLE checklist_templates (
    id                          BIGINT        NOT NULL IDENTITY(1,1),
    service_type                NVARCHAR(50)  NOT NULL,
    category                    NVARCHAR(255) NOT NULL,
    document_name               NVARCHAR(255) NOT NULL,
    [description]               NVARCHAR(MAX),
    required                    BIT           NOT NULL DEFAULT 1,
    conditional                 BIT           NOT NULL DEFAULT 0,
    condition_description       NVARCHAR(255),
    sort_order                  INT           NOT NULL DEFAULT 0,
    source_url                  NVARCHAR(MAX),
    last_reviewed_date          DATE,
    reviewed_by_consultant_id   BIGINT,
    reviewed_by_consultant_name NVARCHAR(255),
    rule_version                INT           NOT NULL DEFAULT 1,
    approved_for_use            BIT           NOT NULL DEFAULT 0,
    approved_by_consultant_id   BIGINT,
    approved_by_consultant_name NVARCHAR(255),
    approved_date               DATE,
    created_at                  DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at                  DATETIME2,
    created_by                  NVARCHAR(255),
    updated_by                  NVARCHAR(255),
    CONSTRAINT pk_checklist_templates PRIMARY KEY (id)
);

-- Intake Question Templates
CREATE TABLE intake_question_templates (
    id                  BIGINT        NOT NULL IDENTITY(1,1),
    service_type        NVARCHAR(50)  NOT NULL,
    section_name        NVARCHAR(255) NOT NULL,
    question_key        NVARCHAR(255) NOT NULL,
    question_label      NVARCHAR(255) NOT NULL,
    help_text           NVARCHAR(MAX),
    input_type          NVARCHAR(50)  NOT NULL,
    [options]           NVARCHAR(MAX),
    required            BIT           NOT NULL DEFAULT 0,
    is_trigger_question BIT           NOT NULL DEFAULT 0,
    sort_order          INT           NOT NULL DEFAULT 0,
    created_at          DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at          DATETIME2,
    created_by          NVARCHAR(255),
    updated_by          NVARCHAR(255),
    CONSTRAINT pk_intake_question_templates PRIMARY KEY (id)
);

-- Intake Responses
CREATE TABLE intake_responses (
    id                 BIGINT        NOT NULL IDENTITY(1,1),
    case_id            BIGINT        NOT NULL,
    section_name       NVARCHAR(255) NOT NULL,
    question_key       NVARCHAR(255) NOT NULL,
    question_label     NVARCHAR(255) NOT NULL,
    answer             NVARCHAR(MAX),
    sort_order         INT           NOT NULL DEFAULT 0,
    flagged_for_review BIT           NOT NULL DEFAULT 0,
    created_at         DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at         DATETIME2,
    created_by         NVARCHAR(255),
    updated_by         NVARCHAR(255),
    CONSTRAINT pk_intake_responses PRIMARY KEY (id),
    CONSTRAINT fk_intake_responses_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Reminders
CREATE TABLE reminders (
    id            BIGINT        NOT NULL IDENTITY(1,1),
    case_id       BIGINT        NOT NULL,
    subject       NVARCHAR(255) NOT NULL,
    message_body  NVARCHAR(MAX) NOT NULL,
    channel       NVARCHAR(50)  NOT NULL,
    [status]      NVARCHAR(50)  NOT NULL,
    scheduled_at  DATETIME2,
    sent_at       DATETIME2,
    approved_at   DATETIME2,
    approved_by   NVARCHAR(255),
    attempt_count INT           NOT NULL DEFAULT 0,
    created_at    DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at    DATETIME2,
    created_by    NVARCHAR(255),
    updated_by    NVARCHAR(255),
    CONSTRAINT pk_reminders PRIMARY KEY (id),
    CONSTRAINT fk_reminders_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Audit Logs
CREATE TABLE audit_logs (
    id           BIGINT        NOT NULL IDENTITY(1,1),
    entity_type  NVARCHAR(255) NOT NULL,
    entity_id    BIGINT        NOT NULL,
    [action]     NVARCHAR(255) NOT NULL,
    details      NVARCHAR(MAX),
    performed_by NVARCHAR(255),
    performed_at DATETIME2     NOT NULL DEFAULT GETDATE(),
    CONSTRAINT pk_audit_logs PRIMARY KEY (id)
);

-- Conditional Rules
CREATE TABLE conditional_rules (
    id                           BIGINT        NOT NULL IDENTITY(1,1),
    service_type                 NVARCHAR(50)  NOT NULL,
    trigger_question_key         NVARCHAR(255) NOT NULL,
    trigger_value                NVARCHAR(255) NOT NULL,
    operator                     NVARCHAR(50)  NOT NULL,
    action_type                  NVARCHAR(50)  NOT NULL,
    target_checklist_template_id BIGINT,
    target_question_key          NVARCHAR(255),
    [description]                NVARCHAR(MAX),
    active                       BIT           NOT NULL DEFAULT 1,
    created_at                   DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at                   DATETIME2,
    created_by                   NVARCHAR(255),
    updated_by                   NVARCHAR(255),
    CONSTRAINT pk_conditional_rules PRIMARY KEY (id)
);

-- Party Profiles
CREATE TABLE party_profiles (
    id             BIGINT        NOT NULL IDENTITY(1,1),
    case_id        BIGINT        NOT NULL,
    party_type     NVARCHAR(50)  NOT NULL,
    full_name      NVARCHAR(255) NOT NULL,
    email          NVARCHAR(255),
    phone          NVARCHAR(255),
    relationship   NVARCHAR(255),
    organization   NVARCHAR(255),
    access_token   NVARCHAR(255),
    portal_enabled BIT           NOT NULL DEFAULT 0,
    notes          NVARCHAR(MAX),
    created_at     DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2,
    created_by     NVARCHAR(255),
    updated_by     NVARCHAR(255),
    CONSTRAINT pk_party_profiles PRIMARY KEY (id),
    CONSTRAINT fk_party_profiles_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Travel History Entries
CREATE TABLE travel_history_entries (
    id          BIGINT        NOT NULL IDENTITY(1,1),
    case_id     BIGINT        NOT NULL,
    country     NVARCHAR(255) NOT NULL,
    entry_date  DATE          NOT NULL,
    exit_date   DATE,
    purpose     NVARCHAR(255),
    days_absent INT           NOT NULL DEFAULT 0,
    notes       NVARCHAR(MAX),
    sort_order  INT           NOT NULL DEFAULT 0,
    created_at  DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2,
    created_by  NVARCHAR(255),
    updated_by  NVARCHAR(255),
    CONSTRAINT pk_travel_history_entries PRIMARY KEY (id),
    CONSTRAINT fk_travel_history_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Work History Entries
CREATE TABLE work_history_entries (
    id                         BIGINT        NOT NULL IDENTITY(1,1),
    case_id                    BIGINT        NOT NULL,
    employer_name              NVARCHAR(255) NOT NULL,
    job_title                  NVARCHAR(255),
    noc_teer_code              NVARCHAR(255),
    start_date                 DATE          NOT NULL,
    end_date                   DATE,
    current_job                BIT           NOT NULL DEFAULT 0,
    hours_per_week             FLOAT         NOT NULL DEFAULT 0,
    employment_type            NVARCHAR(50),
    duties                     NVARCHAR(MAX),
    country                    NVARCHAR(255),
    city                       NVARCHAR(255),
    reference_letter_received  BIT           NOT NULL DEFAULT 0,
    reference_dates_match      BIT           NOT NULL DEFAULT 0,
    reference_duties_described BIT           NOT NULL DEFAULT 0,
    sort_order                 INT           NOT NULL DEFAULT 0,
    created_at                 DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at                 DATETIME2,
    created_by                 NVARCHAR(255),
    updated_by                 NVARCHAR(255),
    CONSTRAINT pk_work_history_entries PRIMARY KEY (id),
    CONSTRAINT fk_work_history_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Relationship Timeline Entries
CREATE TABLE relationship_timeline_entries (
    id                BIGINT        NOT NULL IDENTITY(1,1),
    case_id           BIGINT        NOT NULL,
    milestone_type    NVARCHAR(255) NOT NULL,
    milestone_date    DATE          NOT NULL,
    location          NVARCHAR(255),
    [description]     NVARCHAR(MAX),
    evidence_category NVARCHAR(255),
    evidence_count    INT           NOT NULL DEFAULT 0,
    sort_order        INT           NOT NULL DEFAULT 0,
    created_at        DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at        DATETIME2,
    created_by        NVARCHAR(255),
    updated_by        NVARCHAR(255),
    CONSTRAINT pk_relationship_timeline_entries PRIMARY KEY (id),
    CONSTRAINT fk_relationship_timeline_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Recruitment Evidence
CREATE TABLE recruitment_evidence (
    id                   BIGINT        NOT NULL IDENTITY(1,1),
    case_id              BIGINT        NOT NULL,
    evidence_type        NVARCHAR(255) NOT NULL,
    platform             NVARCHAR(255) NOT NULL,
    posting_date         DATE          NOT NULL,
    expiry_date          DATE,
    days_posted          INT           NOT NULL DEFAULT 0,
    applicants_received  INT           NOT NULL DEFAULT 0,
    interviews_conducted INT           NOT NULL DEFAULT 0,
    non_hire_reasons     NVARCHAR(MAX),
    screenshot_attached  BIT           NOT NULL DEFAULT 0,
    notes                NVARCHAR(MAX),
    sort_order           INT           NOT NULL DEFAULT 0,
    created_at           DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at           DATETIME2,
    created_by           NVARCHAR(255),
    updated_by           NVARCHAR(255),
    CONSTRAINT pk_recruitment_evidence PRIMARY KEY (id),
    CONSTRAINT fk_recruitment_evidence_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Candidate Comparisons
CREATE TABLE candidate_comparisons (
    id               BIGINT        NOT NULL IDENTITY(1,1),
    case_id          BIGINT        NOT NULL,
    candidate_name   NVARCHAR(255) NOT NULL,
    candidate_type   NVARCHAR(50)  NOT NULL,
    qualifications   NVARCHAR(255),
    years_experience INT           NOT NULL DEFAULT 0,
    education_level  NVARCHAR(255),
    language_skills  NVARCHAR(255),
    interviewed      BIT           NOT NULL DEFAULT 0,
    interview_notes  NVARCHAR(MAX),
    outcome          NVARCHAR(50),
    non_hire_reason  NVARCHAR(MAX),
    sort_order       INT           NOT NULL DEFAULT 0,
    created_at       DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at       DATETIME2,
    created_by       NVARCHAR(255),
    updated_by       NVARCHAR(255),
    CONSTRAINT pk_candidate_comparisons PRIMARY KEY (id),
    CONSTRAINT fk_candidate_comparisons_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- Expiry Alerts
CREATE TABLE expiry_alerts (
    id                    BIGINT        NOT NULL IDENTITY(1,1),
    case_id               BIGINT        NOT NULL,
    alert_type            NVARCHAR(255) NOT NULL,
    document_description  NVARCHAR(255) NOT NULL,
    expiry_date           DATE          NOT NULL,
    days_until_expiry     INT           NOT NULL DEFAULT 0,
    severity              NVARCHAR(50)  NOT NULL,
    acknowledged          BIT           NOT NULL DEFAULT 0,
    acknowledged_by       NVARCHAR(255),
    linked_document_id    BIGINT,
    created_at            DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2,
    created_by            NVARCHAR(255),
    updated_by            NVARCHAR(255),
    CONSTRAINT pk_expiry_alerts PRIMARY KEY (id),
    CONSTRAINT fk_expiry_alerts_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
);

-- ======================== INDEXES ========================

CREATE INDEX idx_clients_consultant ON clients (consultant_id);
CREATE INDEX idx_clients_email ON clients (email);
CREATE INDEX idx_cases_client ON immigration_cases (client_id);
CREATE INDEX idx_cases_consultant ON immigration_cases (consultant_id);
CREATE INDEX idx_cases_status ON immigration_cases (case_status);
CREATE INDEX idx_cases_lead_status ON immigration_cases (lead_status);
CREATE INDEX idx_cases_service_type ON immigration_cases (service_type);
CREATE INDEX idx_documents_case ON documents (case_id);
CREATE INDEX idx_documents_status ON documents ([status]);
CREATE INDEX idx_checklist_items_case ON checklist_items (case_id);
CREATE INDEX idx_checklist_templates_service_type ON checklist_templates (service_type);
CREATE INDEX idx_intake_templates_service_type ON intake_question_templates (service_type);
CREATE INDEX idx_intake_responses_case ON intake_responses (case_id);
CREATE INDEX idx_reminders_case ON reminders (case_id);
CREATE INDEX idx_reminders_status ON reminders ([status]);
CREATE INDEX idx_audit_logs_entity ON audit_logs (entity_type, entity_id);
CREATE INDEX idx_conditional_rules_service_type ON conditional_rules (service_type);
CREATE INDEX idx_party_profiles_case ON party_profiles (case_id);
CREATE INDEX idx_travel_history_case ON travel_history_entries (case_id);
CREATE INDEX idx_work_history_case ON work_history_entries (case_id);
CREATE INDEX idx_relationship_timeline_case ON relationship_timeline_entries (case_id);
CREATE INDEX idx_recruitment_evidence_case ON recruitment_evidence (case_id);
CREATE INDEX idx_candidate_comparisons_case ON candidate_comparisons (case_id);
CREATE INDEX idx_expiry_alerts_case ON expiry_alerts (case_id);
CREATE INDEX idx_expiry_alerts_expiry ON expiry_alerts (expiry_date);
CREATE INDEX idx_expiry_alerts_severity ON expiry_alerts (severity);
