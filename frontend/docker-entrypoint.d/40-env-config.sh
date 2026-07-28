#!/bin/sh
# Rendered at nginx startup (nginx:alpine runs /docker-entrypoint.d/*.sh before
# launching). Substitutes environment variables into the SPA's runtime config so
# the browser gets Keycloak/API endpoints from env, with no rebuild.
set -eu

: "${KEYCLOAK_PUBLIC_URL:=http://localhost:8085}"
: "${KC_REALM:=immiauto}"
: "${FRONTEND_CLIENT_ID:=immiauto-frontend}"
: "${API_BASE_URL:=/api/v1}"
export KEYCLOAK_PUBLIC_URL KC_REALM FRONTEND_CLIENT_ID API_BASE_URL

envsubst '${KEYCLOAK_PUBLIC_URL} ${KC_REALM} ${FRONTEND_CLIENT_ID} ${API_BASE_URL}' \
  < /etc/nginx/env.template.js \
  > /usr/share/nginx/html/assets/env.js

echo "[env-config] wrote /assets/env.js (keycloak=${KEYCLOAK_PUBLIC_URL} realm=${KC_REALM} api=${API_BASE_URL})"
