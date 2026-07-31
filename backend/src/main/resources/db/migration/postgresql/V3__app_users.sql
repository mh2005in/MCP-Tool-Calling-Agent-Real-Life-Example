-- ============================================================
-- Immigration Automation - Database Schema (PostgreSQL)
-- V3: Microsoft Entra External ID -> application user mapping
-- ============================================================

CREATE TABLE immiauto_db.app_users (
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    external_subject  VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    display_name      VARCHAR(255),
    tenant_id         VARCHAR(255),
    role              VARCHAR(255) NOT NULL,
    status            VARCHAR(255) NOT NULL,
    consultant_id     UUID,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_app_users PRIMARY KEY (id),
    CONSTRAINT uk_app_users_external_subject UNIQUE (external_subject),
    CONSTRAINT fk_app_users_consultant FOREIGN KEY (consultant_id) REFERENCES immiauto_db.consultants (id)
);

-- ======================== DEMO APP USER ========================
-- Maps the demo Entra identity (real oid) to the demo consultant seeded in V2 (resolved by
-- email so it works regardless of the generated consultant id).
INSERT INTO immiauto_db.app_users (external_subject, email, display_name, role, status, consultant_id, created_at)
SELECT '0186e7d8-a81f-4526-b1bc-b4a5726af370',
       c.email,
       c.full_name,
       'CONSULTANT_OWNER',
       'ACTIVE',
       c.id,
       CURRENT_TIMESTAMP
FROM immiauto_db.consultants c
WHERE c.email = 'demo@immiauto.ca'
  AND NOT EXISTS (
      SELECT 1 FROM immiauto_db.app_users a WHERE a.external_subject = '0186e7d8-a81f-4526-b1bc-b4a5726af370'
  );
