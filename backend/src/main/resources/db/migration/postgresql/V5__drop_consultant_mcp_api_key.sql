-- ============================================================
-- Immigration Automation - Database Schema (PostgreSQL)
-- V5: drop the per-consultant MCP API key (replaced by Entra OBO in Phase 3)
-- ============================================================

ALTER TABLE immiauto_db.consultants DROP CONSTRAINT IF EXISTS uk_consultants_mcp_api_key;
ALTER TABLE immiauto_db.consultants DROP COLUMN IF EXISTS mcp_api_key;
