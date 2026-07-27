-- ============================================================
-- Immigration Automation - Database Schema (PostgreSQL)
-- V8: Enable fill for the pilot support form (IMM 5476).
--     A synthetic fillable AcroForm is generated at dev startup by
--     SampleAcroFormSeeder into {app.forms.source-dir}/IMM_5476/2024/source.pdf.
--     source_sha256 is intentionally left NULL until the form is formally
--     inspected (Milestone 6); generation skips hash enforcement while NULL.
-- ============================================================

UPDATE immiauto_db.form_definitions
SET supports_fill = TRUE,
    source_file_name = 'source.pdf'
WHERE form_code = 'IMM_5476';
