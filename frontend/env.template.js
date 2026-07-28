// Template rendered by nginx (envsubst) at container start into
// /usr/share/nginx/html/assets/env.js. Values come from environment variables,
// so the SPA's Keycloak/API endpoints are configured at deploy time, not baked
// in at build time.
window.__env = {
  keycloakUrl: "${KEYCLOAK_PUBLIC_URL}",
  keycloakRealm: "${KC_REALM}",
  keycloakClientId: "${FRONTEND_CLIENT_ID}",
  apiBaseUrl: "${API_BASE_URL}"
};
