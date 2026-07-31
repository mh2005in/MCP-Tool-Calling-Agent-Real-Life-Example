-- ============================================================
-- Immigration Automation - Database Schema (PostgreSQL)
-- V6: Section 4.1 - IRCC Form & Submission-Package Automation
--     Form catalogue, canonical data fields, versioned mappings,
--     package profiles, generated case drafts/packages, validation.
-- ============================================================

-- ======================== TABLES ========================

-- Form Definitions (governed official form file/version)
CREATE TABLE immiauto_db.form_definitions (
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    form_code         VARCHAR(100) NOT NULL,
    display_name      VARCHAR(255) NOT NULL,
    jurisdiction      VARCHAR(50)  NOT NULL,
    program_category  VARCHAR(100),
    source_url        TEXT,
    source_file_name  VARCHAR(255),
    source_sha256     VARCHAR(100),
    effective_date    DATE,
    retirement_date   DATE,
    edition_label     VARCHAR(100),
    supports_fill     BOOLEAN      NOT NULL DEFAULT FALSE,
    supports_barcode  BOOLEAN      NOT NULL DEFAULT FALSE,
    status            VARCHAR(50)  NOT NULL,
    notes             TEXT,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_form_definitions PRIMARY KEY (id),
    CONSTRAINT uk_form_def_code_edition_sha UNIQUE (form_code, edition_label, source_sha256)
);
CREATE INDEX idx_form_def_category_status ON immiauto_db.form_definitions (program_category, status);
CREATE INDEX idx_form_def_effective_retire ON immiauto_db.form_definitions (effective_date, retirement_date);

-- Form Field Definitions (fields discovered inside a PDF form)
CREATE TABLE immiauto_db.form_field_definitions (
    id                   UUID         NOT NULL DEFAULT gen_random_uuid(),
    form_definition_id   UUID         NOT NULL,
    pdf_field_name       VARCHAR(255) NOT NULL,
    label                VARCHAR(255),
    field_type           VARCHAR(50)  NOT NULL,
    required             BOOLEAN      NOT NULL DEFAULT FALSE,
    max_length           INTEGER,
    allowed_values       TEXT,
    page_number          INTEGER,
    read_only            BOOLEAN      NOT NULL DEFAULT FALSE,
    calculated           BOOLEAN      NOT NULL DEFAULT FALSE,
    notes                TEXT,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    CONSTRAINT pk_form_field_definitions PRIMARY KEY (id),
    CONSTRAINT uk_form_field_def_form_name UNIQUE (form_definition_id, pdf_field_name),
    CONSTRAINT fk_form_field_def_form FOREIGN KEY (form_definition_id) REFERENCES immiauto_db.form_definitions (id)
);

-- Canonical Data Fields (reusable normalized application data)
CREATE TABLE immiauto_db.canonical_data_fields (
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    field_key         VARCHAR(255) NOT NULL,
    display_name      VARCHAR(255) NOT NULL,
    category          VARCHAR(100),
    data_type         VARCHAR(50),
    source_priority   TEXT,
    sensitive         BOOLEAN      NOT NULL DEFAULT FALSE,
    description       TEXT,
    active            BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_canonical_data_fields PRIMARY KEY (id),
    CONSTRAINT uk_canonical_field_key UNIQUE (field_key)
);

-- Form Mapping Versions (versioned mapping set for one form definition)
CREATE TABLE immiauto_db.form_mapping_versions (
    id                          UUID         NOT NULL DEFAULT gen_random_uuid(),
    form_definition_id          UUID         NOT NULL,
    mapping_version             INTEGER      NOT NULL,
    status                      VARCHAR(50)  NOT NULL,
    approved_by_consultant_id   UUID,
    approved_by_consultant_name VARCHAR(255),
    approved_at                 TIMESTAMP,
    change_summary              TEXT,
    regression_fixture_path     TEXT,
    notes                       TEXT,
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    CONSTRAINT pk_form_mapping_versions PRIMARY KEY (id),
    CONSTRAINT uk_form_mapping_form_version UNIQUE (form_definition_id, mapping_version),
    CONSTRAINT fk_form_mapping_form FOREIGN KEY (form_definition_id) REFERENCES immiauto_db.form_definitions (id)
);

-- Form Field Mappings (maps canonical data to a PDF field)
CREATE TABLE immiauto_db.form_field_mappings (
    id                          UUID         NOT NULL DEFAULT gen_random_uuid(),
    mapping_version_id          UUID         NOT NULL,
    form_field_definition_id    UUID         NOT NULL,
    canonical_field_key         VARCHAR(255) NOT NULL,
    transform_type              VARCHAR(50)  NOT NULL,
    transform_config            TEXT,
    default_value               TEXT,
    required_for_package        BOOLEAN      NOT NULL DEFAULT FALSE,
    consultant_review_required  BOOLEAN      NOT NULL DEFAULT FALSE,
    notes                       TEXT,
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    CONSTRAINT pk_form_field_mappings PRIMARY KEY (id),
    CONSTRAINT fk_form_field_mapping_version FOREIGN KEY (mapping_version_id) REFERENCES immiauto_db.form_mapping_versions (id),
    CONSTRAINT fk_form_field_mapping_field FOREIGN KEY (form_field_definition_id) REFERENCES immiauto_db.form_field_definitions (id)
);
CREATE INDEX idx_form_field_mapping_version ON immiauto_db.form_field_mappings (mapping_version_id);

-- Package Profiles (a supported application package for a case type/program)
CREATE TABLE immiauto_db.package_profiles (
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    profile_code      VARCHAR(100) NOT NULL,
    display_name      VARCHAR(255) NOT NULL,
    service_type      VARCHAR(50),
    case_subtype      VARCHAR(50),
    jurisdiction      VARCHAR(50),
    status            VARCHAR(50)  NOT NULL,
    description       TEXT,
    effective_date    DATE,
    retirement_date   DATE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_package_profiles PRIMARY KEY (id),
    CONSTRAINT uk_package_profile_code UNIQUE (profile_code)
);

-- Package Profile Forms (connects a package profile to required forms)
CREATE TABLE immiauto_db.package_profile_forms (
    id                     UUID         NOT NULL DEFAULT gen_random_uuid(),
    package_profile_id     UUID         NOT NULL,
    form_definition_id     UUID         NOT NULL,
    required               BOOLEAN      NOT NULL DEFAULT FALSE,
    sort_order             INTEGER      NOT NULL DEFAULT 0,
    conditional_expression TEXT,
    notes                  TEXT,
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP,
    created_by             VARCHAR(255),
    updated_by             VARCHAR(255),
    CONSTRAINT pk_package_profile_forms PRIMARY KEY (id),
    CONSTRAINT fk_pp_form_profile FOREIGN KEY (package_profile_id) REFERENCES immiauto_db.package_profiles (id),
    CONSTRAINT fk_pp_form_form FOREIGN KEY (form_definition_id) REFERENCES immiauto_db.form_definitions (id)
);
CREATE INDEX idx_pp_form_profile ON immiauto_db.package_profile_forms (package_profile_id);

-- Package Document Requirements (connects profiles to document/checklist concepts)
CREATE TABLE immiauto_db.package_document_requirements (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    package_profile_id  UUID         NOT NULL,
    document_category   VARCHAR(255),
    document_type       VARCHAR(255),
    required            BOOLEAN      NOT NULL DEFAULT FALSE,
    sort_order          INTEGER      NOT NULL DEFAULT 0,
    naming_pattern      VARCHAR(255),
    max_size_bytes      BIGINT,
    translation_rule    TEXT,
    certified_copy_rule TEXT,
    notes               TEXT,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_package_document_requirements PRIMARY KEY (id),
    CONSTRAINT fk_pdr_profile FOREIGN KEY (package_profile_id) REFERENCES immiauto_db.package_profiles (id)
);
CREATE INDEX idx_pdr_profile ON immiauto_db.package_document_requirements (package_profile_id);

-- Case Form Drafts (generated draft data for a case/form/mapping version)
CREATE TABLE immiauto_db.case_form_drafts (
    id                      UUID         NOT NULL DEFAULT gen_random_uuid(),
    case_id                 UUID         NOT NULL,
    form_definition_id      UUID         NOT NULL,
    mapping_version_id      UUID         NOT NULL,
    status                  VARCHAR(50)  NOT NULL,
    input_snapshot_json     TEXT,
    mapped_values_json      TEXT,
    validation_summary_json TEXT,
    draft_file_path         TEXT,
    draft_sha256            VARCHAR(100),
    generated_at            TIMESTAMP,
    generated_by            VARCHAR(255),
    approved_at             TIMESTAMP,
    approved_by             VARCHAR(255),
    approval_notes          TEXT,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_case_form_drafts PRIMARY KEY (id),
    CONSTRAINT fk_cfd_case FOREIGN KEY (case_id) REFERENCES immiauto_db.immigration_cases (id),
    CONSTRAINT fk_cfd_form FOREIGN KEY (form_definition_id) REFERENCES immiauto_db.form_definitions (id),
    CONSTRAINT fk_cfd_mapping FOREIGN KEY (mapping_version_id) REFERENCES immiauto_db.form_mapping_versions (id)
);
CREATE INDEX idx_cfd_case ON immiauto_db.case_form_drafts (case_id);

-- Case Packages (a generated package for one case)
CREATE TABLE immiauto_db.case_packages (
    id                    UUID         NOT NULL DEFAULT gen_random_uuid(),
    case_id               UUID         NOT NULL,
    package_profile_id    UUID         NOT NULL,
    status                VARCHAR(50)  NOT NULL,
    package_index_json    TEXT,
    readiness_report_json TEXT,
    package_manifest_path TEXT,
    package_zip_path      TEXT,
    package_sha256        VARCHAR(100),
    generated_at          TIMESTAMP,
    generated_by          VARCHAR(255),
    approved_at           TIMESTAMP,
    approved_by           VARCHAR(255),
    approval_notes        TEXT,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    CONSTRAINT pk_case_packages PRIMARY KEY (id),
    CONSTRAINT fk_cp_case FOREIGN KEY (case_id) REFERENCES immiauto_db.immigration_cases (id),
    CONSTRAINT fk_cp_profile FOREIGN KEY (package_profile_id) REFERENCES immiauto_db.package_profiles (id)
);
CREATE INDEX idx_cp_case ON immiauto_db.case_packages (case_id);

-- Package Validation Issues (deterministic validation results)
CREATE TABLE immiauto_db.package_validation_issues (
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    case_package_id   UUID         NOT NULL,
    case_form_draft_id UUID,
    severity          VARCHAR(50)  NOT NULL,
    code              VARCHAR(100) NOT NULL,
    message           TEXT,
    field_key         VARCHAR(255),
    pdf_field_name    VARCHAR(255),
    source_type       VARCHAR(100),
    source_id         UUID,
    resolved          BOOLEAN      NOT NULL DEFAULT FALSE,
    resolved_by       VARCHAR(255),
    resolved_at       TIMESTAMP,
    resolution_notes  TEXT,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_package_validation_issues PRIMARY KEY (id),
    CONSTRAINT fk_pvi_package FOREIGN KEY (case_package_id) REFERENCES immiauto_db.case_packages (id),
    CONSTRAINT fk_pvi_draft FOREIGN KEY (case_form_draft_id) REFERENCES immiauto_db.case_form_drafts (id)
);
CREATE INDEX idx_pvi_package ON immiauto_db.package_validation_issues (case_package_id);
CREATE INDEX idx_pvi_package_severity ON immiauto_db.package_validation_issues (case_package_id, severity);
