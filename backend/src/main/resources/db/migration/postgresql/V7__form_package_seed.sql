-- ============================================================
-- Immigration Automation - Seed Data
-- V7: Section 4.1 pilot content - canonical fields, one package
--     profile, two sample form definitions, and field definitions
--     + an approved mapping version for a test/support form (IMM 5476).
-- NOTE: sample forms have no real source PDF yet, so they are seeded as
--       DRAFT with supports_fill = FALSE until a governed PDF + SHA-256
--       is registered through the catalogue inspect flow.
-- ============================================================

-- ======================== CANONICAL DATA FIELDS ========================
INSERT INTO immiauto_db.canonical_data_fields (id, field_key, display_name, category, data_type, source_priority, sensitive, description, active, created_at) VALUES
(nextval('immiauto_db.canonical_data_fields_seq'), 'primaryApplicant.fullName',              'Primary Applicant Full Name',        'person',     'string',  'client.fullName > intake.fullName',                FALSE, 'Full legal name of the primary applicant.',          TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'primaryApplicant.dateOfBirth',           'Primary Applicant Date of Birth',    'person',     'date',    'client.dateOfBirth > intake.dateOfBirth',          FALSE, 'Date of birth, normalized to ISO yyyy-MM-dd.',       TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'primaryApplicant.passport.number',       'Passport Number',                    'person',     'string',  'client.passportNumber > intake.passportNumber',    TRUE,  'Passport number of the primary applicant.',          TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'primaryApplicant.passport.expiryDate',   'Passport Expiry Date',               'person',     'date',    'intake.passportExpiry',                            FALSE, 'Passport expiry date, normalized to ISO.',           TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'primaryApplicant.currentAddress.country','Current Country of Residence',       'address',    'country', 'client.currentLocation > intake.currentCountry',   FALSE, 'Country where the applicant currently resides.',     TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'primaryApplicant.email',                 'Primary Applicant Email',            'person',     'string',  'client.email > intake.email',                      FALSE, 'Contact email of the primary applicant.',            TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'primaryApplicant.phone',                 'Primary Applicant Phone',            'person',     'string',  'client.phone > intake.phone',                      FALSE, 'Contact phone of the primary applicant.',            TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'case.serviceType',                       'Case Service Type',                  'case',       'enum',    'case.serviceType',                                 FALSE, 'Immigration service/program type for the case.',     TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'travelHistory.entries',                  'Travel History Entries',             'travel',     'list',    'travelHistoryEntry[*]',                            FALSE, 'List of travel history rows for the case.',          TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'workHistory.entries',                    'Work History Entries',               'employment', 'list',    'workHistoryEntry[*]',                              FALSE, 'List of employment history rows for the case.',      TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'representative.fullName',                'Representative Full Name',           'person',     'string',  'consultant.fullName',                              FALSE, 'Full name of the authorized representative.',        TRUE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.canonical_data_fields_seq'), 'representative.licenseNumber',           'Representative License Number',      'person',     'string',  'consultant.licenseNumber',                         FALSE, 'RCIC / license number of the representative.',       TRUE, CURRENT_TIMESTAMP);

-- ======================== FORM DEFINITIONS ========================
INSERT INTO immiauto_db.form_definitions (id, form_code, display_name, jurisdiction, program_category, source_url, edition_label, supports_fill, supports_barcode, status, notes, created_at) VALUES
(nextval('immiauto_db.form_definitions_seq'), 'IMM_5257', 'Application for Temporary Resident Visa', 'FEDERAL', 'visitor', 'https://www.canada.ca/en/immigration-refugees-citizenship/services/application/application-forms-guides/imm5257.html', '2024', FALSE, TRUE,  'DRAFT', 'Pilot placeholder. Register governed source PDF + SHA-256 before enabling fill.', CURRENT_TIMESTAMP),
(nextval('immiauto_db.form_definitions_seq'), 'IMM_5476', 'Use of a Representative',                 'FEDERAL', 'all',     'https://www.canada.ca/en/immigration-refugees-citizenship/services/application/application-forms-guides/imm5476.html', '2024', FALSE, FALSE, 'DRAFT', 'Test/support form used to seed sample field definitions and mappings.',           CURRENT_TIMESTAMP);

-- ======================== FORM FIELD DEFINITIONS (IMM 5476 test/support form) ========================
INSERT INTO immiauto_db.form_field_definitions (id, form_definition_id, pdf_field_name, label, field_type, required, page_number, read_only, calculated, created_at) VALUES
(nextval('immiauto_db.form_field_definitions_seq'), (SELECT id FROM immiauto_db.form_definitions WHERE form_code = 'IMM_5476'), 'applicant_name',         'Applicant Full Name',        'TEXT', TRUE,  1, FALSE, FALSE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.form_field_definitions_seq'), (SELECT id FROM immiauto_db.form_definitions WHERE form_code = 'IMM_5476'), 'representative_name',    'Representative Full Name',    'TEXT', TRUE,  1, FALSE, FALSE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.form_field_definitions_seq'), (SELECT id FROM immiauto_db.form_definitions WHERE form_code = 'IMM_5476'), 'representative_license', 'Representative License No.',  'TEXT', FALSE, 1, FALSE, FALSE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.form_field_definitions_seq'), (SELECT id FROM immiauto_db.form_definitions WHERE form_code = 'IMM_5476'), 'signature_date',         'Signature Date',              'DATE', TRUE,  1, FALSE, FALSE, CURRENT_TIMESTAMP);

-- ======================== MAPPING VERSION (IMM 5476 v1, APPROVED) ========================
INSERT INTO immiauto_db.form_mapping_versions (id, form_definition_id, mapping_version, status, change_summary, created_at) VALUES
(nextval('immiauto_db.form_mapping_versions_seq'), (SELECT id FROM immiauto_db.form_definitions WHERE form_code = 'IMM_5476'), 1, 'APPROVED', 'Initial pilot mapping for the Use of a Representative support form.', CURRENT_TIMESTAMP);

-- ======================== FIELD MAPPINGS (IMM 5476 v1) ========================
INSERT INTO immiauto_db.form_field_mappings (id, mapping_version_id, form_field_definition_id, canonical_field_key, transform_type, required_for_package, consultant_review_required, created_at) VALUES
(nextval('immiauto_db.form_field_mappings_seq'),
    (SELECT mv.id FROM immiauto_db.form_mapping_versions mv JOIN immiauto_db.form_definitions fd ON mv.form_definition_id = fd.id WHERE fd.form_code = 'IMM_5476' AND mv.mapping_version = 1),
    (SELECT ffd.id FROM immiauto_db.form_field_definitions ffd JOIN immiauto_db.form_definitions fd ON ffd.form_definition_id = fd.id WHERE fd.form_code = 'IMM_5476' AND ffd.pdf_field_name = 'applicant_name'),
    'primaryApplicant.fullName', 'DIRECT', TRUE, FALSE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.form_field_mappings_seq'),
    (SELECT mv.id FROM immiauto_db.form_mapping_versions mv JOIN immiauto_db.form_definitions fd ON mv.form_definition_id = fd.id WHERE fd.form_code = 'IMM_5476' AND mv.mapping_version = 1),
    (SELECT ffd.id FROM immiauto_db.form_field_definitions ffd JOIN immiauto_db.form_definitions fd ON ffd.form_definition_id = fd.id WHERE fd.form_code = 'IMM_5476' AND ffd.pdf_field_name = 'representative_name'),
    'representative.fullName', 'DIRECT', TRUE, FALSE, CURRENT_TIMESTAMP),
(nextval('immiauto_db.form_field_mappings_seq'),
    (SELECT mv.id FROM immiauto_db.form_mapping_versions mv JOIN immiauto_db.form_definitions fd ON mv.form_definition_id = fd.id WHERE fd.form_code = 'IMM_5476' AND mv.mapping_version = 1),
    (SELECT ffd.id FROM immiauto_db.form_field_definitions ffd JOIN immiauto_db.form_definitions fd ON ffd.form_definition_id = fd.id WHERE fd.form_code = 'IMM_5476' AND ffd.pdf_field_name = 'representative_license'),
    'representative.licenseNumber', 'DIRECT', FALSE, FALSE, CURRENT_TIMESTAMP);
-- signature_date is intentionally left unmapped: it is applied by the consultant at signing time.

-- ======================== PACKAGE PROFILE (Visitor Visa pilot) ========================
INSERT INTO immiauto_db.package_profiles (id, profile_code, display_name, service_type, jurisdiction, status, description, effective_date, created_at) VALUES
(nextval('immiauto_db.package_profiles_seq'), 'VISITOR_VISA_BASIC', 'Visitor Visa Support Package', 'VISITOR_VISA', 'FEDERAL', 'ACTIVE', 'Pilot package profile for temporary resident visa support forms and core evidence.', CURRENT_DATE, CURRENT_TIMESTAMP);

-- ======================== PACKAGE PROFILE FORMS ========================
INSERT INTO immiauto_db.package_profile_forms (id, package_profile_id, form_definition_id, required, sort_order, created_at) VALUES
(nextval('immiauto_db.package_profile_forms_seq'),
    (SELECT id FROM immiauto_db.package_profiles WHERE profile_code = 'VISITOR_VISA_BASIC'),
    (SELECT id FROM immiauto_db.form_definitions WHERE form_code = 'IMM_5257'), TRUE, 1, CURRENT_TIMESTAMP),
(nextval('immiauto_db.package_profile_forms_seq'),
    (SELECT id FROM immiauto_db.package_profiles WHERE profile_code = 'VISITOR_VISA_BASIC'),
    (SELECT id FROM immiauto_db.form_definitions WHERE form_code = 'IMM_5476'), TRUE, 2, CURRENT_TIMESTAMP);

-- ======================== PACKAGE DOCUMENT REQUIREMENTS ========================
INSERT INTO immiauto_db.package_document_requirements (id, package_profile_id, document_category, document_type, required, sort_order, naming_pattern, certified_copy_rule, created_at) VALUES
(nextval('immiauto_db.package_document_requirements_seq'),
    (SELECT id FROM immiauto_db.package_profiles WHERE profile_code = 'VISITOR_VISA_BASIC'),
    'Identity', 'Passport biographical page', TRUE, 1, '{caseNumber}_passport_bio.pdf', 'Clear colour scan; certified copy not required for upload.', CURRENT_TIMESTAMP),
(nextval('immiauto_db.package_document_requirements_seq'),
    (SELECT id FROM immiauto_db.package_profiles WHERE profile_code = 'VISITOR_VISA_BASIC'),
    'Financial', 'Proof of funds - bank statements', TRUE, 2, '{caseNumber}_proof_of_funds.pdf', NULL, CURRENT_TIMESTAMP);
