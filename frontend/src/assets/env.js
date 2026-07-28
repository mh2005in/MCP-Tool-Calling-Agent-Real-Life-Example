// Runtime configuration for local `ng serve` development.
//
// In Docker this file is OVERWRITTEN at container start by nginx (envsubst on
// env.template.js) so the values come from environment variables — no rebuild
// needed to point the SPA at a different Keycloak/API host. See
// frontend/env.template.js and frontend/docker-entrypoint.d/40-env-config.sh.
window.__env = {
  keycloakUrl: 'http://localhost:8085',
  keycloakRealm: 'immiauto',
  keycloakClientId: 'immiauto-frontend',
  apiBaseUrl: 'http://localhost:8080/api/v1'
};
