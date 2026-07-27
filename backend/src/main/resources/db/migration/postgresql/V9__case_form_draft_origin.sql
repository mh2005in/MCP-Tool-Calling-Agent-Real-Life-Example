-- ============================================================
-- Immigration Automation - Database Schema (PostgreSQL)
-- V9: Support manually-uploaded filled forms alongside auto-generated ones.
--     - origin distinguishes GENERATED vs UPLOADED (vs DATA_SHEET, backlog)
--     - uploaded forms have no mapping version, so relax the NOT NULL
--     - original_file_name captures the consultant's uploaded file name
-- ============================================================

ALTER TABLE immiauto_db.case_form_drafts
    ADD COLUMN origin VARCHAR(50) NOT NULL DEFAULT 'GENERATED';

ALTER TABLE immiauto_db.case_form_drafts
    ADD COLUMN original_file_name VARCHAR(255);

ALTER TABLE immiauto_db.case_form_drafts
    ALTER COLUMN mapping_version_id DROP NOT NULL;
