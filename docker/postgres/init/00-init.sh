#!/bin/bash
# ============================================================
# Postgres bootstrap - runs ONCE, on a fresh data volume, via
# the official image's /docker-entrypoint-initdb.d mechanism.
#
# 1. Creates the application schema (immiauto_db) and puts it on
#    the database search_path. The V1/V2 scripts create tables
#    unqualified, while V3+ qualify with immiauto_db.* - so the
#    schema must exist and be first on the search_path.
# 2. Applies the versioned migration scripts (V1..Vn) that are
#    bind-mounted read-only at /migrations, in version order.
#
# Because this only runs on an empty data directory, re-running
# `docker compose up` against an existing volume will NOT re-run
# it. To rebuild the DB from scratch: `docker compose down -v`.
# ============================================================
set -euo pipefail

SCHEMA="immiauto_db"

echo "[db-init] Creating schema '${SCHEMA}' and setting search_path on '${POSTGRES_DB}'"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-SQL
    CREATE SCHEMA IF NOT EXISTS ${SCHEMA} AUTHORIZATION ${POSTGRES_USER};
    ALTER DATABASE ${POSTGRES_DB} SET search_path TO ${SCHEMA}, public;
SQL

if ls /migrations/V*.sql >/dev/null 2>&1; then
    echo "[db-init] Applying migration scripts from /migrations"
    for f in $(ls /migrations/V*.sql | sort -V); do
        echo "[db-init]  -> $(basename "$f")"
        PGOPTIONS="--search_path=${SCHEMA},public" \
            psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -f "$f"
    done
else
    echo "[db-init] WARNING: no migration scripts found at /migrations"
fi

echo "[db-init] Database initialization complete."
