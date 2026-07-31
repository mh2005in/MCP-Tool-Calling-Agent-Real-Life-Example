-- ============================================================
-- Immigration Automation - Database Schema (MySQL 8.0+)
-- V1: Initial schema creation
--
-- STALE: this MySQL mirror is NOT used by the running stack (Postgres only)
-- and has NOT been migrated to GUID primary keys. The PostgreSQL schema under
-- ../postgresql is the source of truth (uuid PKs via gen_random_uuid()).
-- Regenerate this mirror before targeting MySQL.
-- ============================================================

-- ======================== TABLES ========================

-- Consultants
CREATE TABLE consultants (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    consultant_number VARCHAR(15)  NOT NULL,
    full_name         VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    phone             VARCHAR(255),
    license_number    VARCHAR(255),
    company_name      VARCHAR(255),
    admin             TINYINT(1)   NOT NULL DEFAULT 0,
    active            TINYINT(1)   NOT NULL DEFAULT 1,
    mcp_api_key       VARCHAR(255) NOT NULL,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_consultants_consultant_number (consultant_number),
    UNIQUE KEY uk_consultants_email (email),
    UNIQUE KEY uk_consultants_mcp_api_key (mcp_api_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Clients
CREATE TABLE clients (
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    client_number            VARCHAR(15)  NOT NULL,
    full_name                VARCHAR(255) NOT NULL,
    email                    VARCHAR(255) NOT NULL,
    phone                    VARCHAR(255),
    whatsapp                 VARCHAR(255),
    date_of_birth            DATE,
    country_of_citizenship   VARCHAR(255),
    current_location         VARCHAR(255),
    current_status           VARCHAR(255),
    passport_number          VARCHAR(255),
    marital_status           VARCHAR(255),
    preferred_language       VARCHAR(255),
    notes                    TEXT,
    consultant_id            BIGINT       NOT NULL,
    created_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by               VARCHAR(255),
    updated_by               VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_clients_client_number (client_number),
    UNIQUE KEY uk_clients_email (email),
    UNIQUE KEY uk_client_name_dob_email (full_name, date_of_birth, email),
    CONSTRAINT fk_clients_consultant FOREIGN KEY (consultant_id) REFERENCES consultants (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Immigration Cases
CREATE TABLE immigration_cases (
    id                        BIGINT       NOT NULL AUTO_INCREMENT,
    case_number               VARCHAR(15)  NOT NULL,
    service_type              VARCHAR(50)  NOT NULL,
    subtype                   VARCHAR(50),
    applicant_role            VARCHAR(50),
    lead_status               VARCHAR(50)  NOT NULL,
    case_status               VARCHAR(50)  NOT NULL,
    client_id                 BIGINT       NOT NULL,
    consultant_id             BIGINT       NOT NULL,
    intake_summary            TEXT,
    consultant_notes          TEXT,
    deadline                  DATE,
    urgency_reason            VARCHAR(255),
    submission_date           DATE,
    application_number        VARCHAR(255),
    portal_used               VARCHAR(255),
    biometrics_status         VARCHAR(255),
    medical_status            VARCHAR(255),
    retainer_signed_date      DATE,
    retainer_document_path    VARCHAR(255),
    engagement_letter_status  VARCHAR(255),
    final_reviewed_by         VARCHAR(255),
    final_reviewed_at         DATETIME,
    final_review_notes        TEXT,
    consultant_signed_off     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by                VARCHAR(255),
    updated_by                VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_immigration_cases_case_number (case_number),
    CONSTRAINT fk_cases_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_cases_consultant FOREIGN KEY (consultant_id) REFERENCES consultants (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Documents
CREATE TABLE documents (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    case_id               BIGINT       NOT NULL,
    original_file_name    VARCHAR(255) NOT NULL,
    stored_file_name      VARCHAR(255) NOT NULL,
    file_path             VARCHAR(255) NOT NULL,
    mime_type             VARCHAR(255),
    file_size_bytes       BIGINT,
    document_category     VARCHAR(255),
    document_type         VARCHAR(255),
    status                VARCHAR(50)  NOT NULL,
    expiry_date           DATE,
    review_note           TEXT,
    rejection_reason      TEXT,
    translation_required  TINYINT(1)   NOT NULL DEFAULT 0,
    notarization_required TINYINT(1)   NOT NULL DEFAULT 0,
    standardized_name     VARCHAR(255),
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_documents_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Checklist Items
CREATE TABLE checklist_items (
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    case_id                  BIGINT       NOT NULL,
    category                 VARCHAR(255) NOT NULL,
    document_name            VARCHAR(255) NOT NULL,
    description              TEXT,
    status                   VARCHAR(50)  NOT NULL,
    required                 TINYINT(1)   NOT NULL DEFAULT 1,
    conditional              TINYINT(1)   NOT NULL DEFAULT 0,
    condition_description    VARCHAR(255),
    sort_order               INT          NOT NULL DEFAULT 0,
    consultant_review_note   TEXT,
    assigned_party           VARCHAR(255),
    document_id              BIGINT,
    created_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by               VARCHAR(255),
    updated_by               VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_checklist_items_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id),
    CONSTRAINT fk_checklist_items_document FOREIGN KEY (document_id) REFERENCES documents (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Checklist Templates
CREATE TABLE checklist_templates (
    id                          BIGINT       NOT NULL AUTO_INCREMENT,
    service_type                VARCHAR(50)  NOT NULL,
    category                    VARCHAR(255) NOT NULL,
    document_name               VARCHAR(255) NOT NULL,
    description                 TEXT,
    required                    TINYINT(1)   NOT NULL DEFAULT 1,
    conditional                 TINYINT(1)   NOT NULL DEFAULT 0,
    condition_description       VARCHAR(255),
    sort_order                  INT          NOT NULL DEFAULT 0,
    source_url                  TEXT,
    last_reviewed_date          DATE,
    reviewed_by_consultant_id   BIGINT,
    reviewed_by_consultant_name VARCHAR(255),
    rule_version                INT          NOT NULL DEFAULT 1,
    approved_for_use            TINYINT(1)   NOT NULL DEFAULT 0,
    approved_by_consultant_id   BIGINT,
    approved_by_consultant_name VARCHAR(255),
    approved_date               DATE,
    created_at                  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Intake Question Templates
CREATE TABLE intake_question_templates (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    service_type        VARCHAR(50)  NOT NULL,
    section_name        VARCHAR(255) NOT NULL,
    question_key        VARCHAR(255) NOT NULL,
    question_label      VARCHAR(255) NOT NULL,
    help_text           TEXT,
    input_type          VARCHAR(50)  NOT NULL,
    options             TEXT,
    required            TINYINT(1)   NOT NULL DEFAULT 0,
    is_trigger_question TINYINT(1)   NOT NULL DEFAULT 0,
    sort_order          INT          NOT NULL DEFAULT 0,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Intake Responses
CREATE TABLE intake_responses (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    case_id            BIGINT       NOT NULL,
    section_name       VARCHAR(255) NOT NULL,
    question_key       VARCHAR(255) NOT NULL,
    question_label     VARCHAR(255) NOT NULL,
    answer             TEXT,
    sort_order         INT          NOT NULL DEFAULT 0,
    flagged_for_review TINYINT(1)   NOT NULL DEFAULT 0,
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_intake_responses_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reminders
CREATE TABLE reminders (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    case_id       BIGINT       NOT NULL,
    subject       VARCHAR(255) NOT NULL,
    message_body  TEXT         NOT NULL,
    channel       VARCHAR(50)  NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    scheduled_at  DATETIME,
    sent_at       DATETIME,
    approved_at   DATETIME,
    approved_by   VARCHAR(255),
    attempt_count INT          NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_reminders_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Audit Logs
CREATE TABLE audit_logs (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    entity_type  VARCHAR(255) NOT NULL,
    entity_id    BIGINT       NOT NULL,
    action       VARCHAR(255) NOT NULL,
    details      TEXT,
    performed_by VARCHAR(255),
    performed_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Conditional Rules
CREATE TABLE conditional_rules (
    id                           BIGINT       NOT NULL AUTO_INCREMENT,
    service_type                 VARCHAR(50)  NOT NULL,
    trigger_question_key         VARCHAR(255) NOT NULL,
    trigger_value                VARCHAR(255) NOT NULL,
    operator                     VARCHAR(50)  NOT NULL,
    action_type                  VARCHAR(50)  NOT NULL,
    target_checklist_template_id BIGINT,
    target_question_key          VARCHAR(255),
    description                  TEXT,
    active                       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at                   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                   DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by                   VARCHAR(255),
    updated_by                   VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Party Profiles
CREATE TABLE party_profiles (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    case_id        BIGINT       NOT NULL,
    party_type     VARCHAR(50)  NOT NULL,
    full_name      VARCHAR(255) NOT NULL,
    email          VARCHAR(255),
    phone          VARCHAR(255),
    relationship   VARCHAR(255),
    organization   VARCHAR(255),
    access_token   VARCHAR(255),
    portal_enabled TINYINT(1)   NOT NULL DEFAULT 0,
    notes          TEXT,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_party_profiles_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Travel History Entries
CREATE TABLE travel_history_entries (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    case_id     BIGINT       NOT NULL,
    country     VARCHAR(255) NOT NULL,
    entry_date  DATE         NOT NULL,
    exit_date   DATE,
    purpose     VARCHAR(255),
    days_absent INT          NOT NULL DEFAULT 0,
    notes       TEXT,
    sort_order  INT          NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_travel_history_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work History Entries
CREATE TABLE work_history_entries (
    id                         BIGINT       NOT NULL AUTO_INCREMENT,
    case_id                    BIGINT       NOT NULL,
    employer_name              VARCHAR(255) NOT NULL,
    job_title                  VARCHAR(255),
    noc_teer_code              VARCHAR(255),
    start_date                 DATE         NOT NULL,
    end_date                   DATE,
    current_job                TINYINT(1)   NOT NULL DEFAULT 0,
    hours_per_week             DOUBLE       NOT NULL DEFAULT 0,
    employment_type            VARCHAR(50),
    duties                     TEXT,
    country                    VARCHAR(255),
    city                       VARCHAR(255),
    reference_letter_received  TINYINT(1)   NOT NULL DEFAULT 0,
    reference_dates_match      TINYINT(1)   NOT NULL DEFAULT 0,
    reference_duties_described TINYINT(1)   NOT NULL DEFAULT 0,
    sort_order                 INT          NOT NULL DEFAULT 0,
    created_at                 DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by                 VARCHAR(255),
    updated_by                 VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_work_history_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Relationship Timeline Entries
CREATE TABLE relationship_timeline_entries (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    case_id           BIGINT       NOT NULL,
    milestone_type    VARCHAR(255) NOT NULL,
    milestone_date    DATE         NOT NULL,
    location          VARCHAR(255),
    description       TEXT,
    evidence_category VARCHAR(255),
    evidence_count    INT          NOT NULL DEFAULT 0,
    sort_order        INT          NOT NULL DEFAULT 0,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_relationship_timeline_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Recruitment Evidence
CREATE TABLE recruitment_evidence (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    case_id              BIGINT       NOT NULL,
    evidence_type        VARCHAR(255) NOT NULL,
    platform             VARCHAR(255) NOT NULL,
    posting_date         DATE         NOT NULL,
    expiry_date          DATE,
    days_posted          INT          NOT NULL DEFAULT 0,
    applicants_received  INT          NOT NULL DEFAULT 0,
    interviews_conducted INT          NOT NULL DEFAULT 0,
    non_hire_reasons     TEXT,
    screenshot_attached  TINYINT(1)   NOT NULL DEFAULT 0,
    notes                TEXT,
    sort_order           INT          NOT NULL DEFAULT 0,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_recruitment_evidence_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Candidate Comparisons
CREATE TABLE candidate_comparisons (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    case_id          BIGINT       NOT NULL,
    candidate_name   VARCHAR(255) NOT NULL,
    candidate_type   VARCHAR(50)  NOT NULL,
    qualifications   VARCHAR(255),
    years_experience INT          NOT NULL DEFAULT 0,
    education_level  VARCHAR(255),
    language_skills  VARCHAR(255),
    interviewed      TINYINT(1)   NOT NULL DEFAULT 0,
    interview_notes  TEXT,
    outcome          VARCHAR(50),
    non_hire_reason  TEXT,
    sort_order       INT          NOT NULL DEFAULT 0,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_candidate_comparisons_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Expiry Alerts
CREATE TABLE expiry_alerts (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    case_id               BIGINT       NOT NULL,
    alert_type            VARCHAR(255) NOT NULL,
    document_description  VARCHAR(255) NOT NULL,
    expiry_date           DATE         NOT NULL,
    days_until_expiry     INT          NOT NULL DEFAULT 0,
    severity              VARCHAR(50)  NOT NULL,
    acknowledged          TINYINT(1)   NOT NULL DEFAULT 0,
    acknowledged_by       VARCHAR(255),
    linked_document_id    BIGINT,
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_expiry_alerts_case FOREIGN KEY (case_id) REFERENCES immigration_cases (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================== INDEXES ========================

CREATE INDEX idx_clients_consultant ON clients (consultant_id);
CREATE INDEX idx_clients_email ON clients (email);
CREATE INDEX idx_cases_client ON immigration_cases (client_id);
CREATE INDEX idx_cases_consultant ON immigration_cases (consultant_id);
CREATE INDEX idx_cases_status ON immigration_cases (case_status);
CREATE INDEX idx_cases_lead_status ON immigration_cases (lead_status);
CREATE INDEX idx_cases_service_type ON immigration_cases (service_type);
CREATE INDEX idx_documents_case ON documents (case_id);
CREATE INDEX idx_documents_status ON documents (status);
CREATE INDEX idx_checklist_items_case ON checklist_items (case_id);
CREATE INDEX idx_checklist_templates_service_type ON checklist_templates (service_type);
CREATE INDEX idx_intake_templates_service_type ON intake_question_templates (service_type);
CREATE INDEX idx_intake_responses_case ON intake_responses (case_id);
CREATE INDEX idx_reminders_case ON reminders (case_id);
CREATE INDEX idx_reminders_status ON reminders (status);
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
