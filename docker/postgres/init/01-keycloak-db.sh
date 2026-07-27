#!/bin/bash
# ============================================================
# Creates a dedicated 'keycloak' database in the same Postgres
# instance, owned by the application DB user. Keycloak stores
# its realms/users/sessions here (separate from the app's
# 'immiauto' database). Runs once, on a fresh data volume.
# ============================================================
set -euo pipefail

if psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \
      -tc "SELECT 1 FROM pg_database WHERE datname = 'keycloak'" | grep -q 1; then
    echo "[db-init] Database 'keycloak' already exists - skipping"
else
    echo "[db-init] Creating 'keycloak' database owned by ${POSTGRES_USER}"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \
        -c "CREATE DATABASE keycloak OWNER ${POSTGRES_USER}"
fi
