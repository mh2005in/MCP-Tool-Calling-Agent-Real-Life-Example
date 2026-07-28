// Values come from runtime config (window.__env, set by assets/env.js). Fallbacks are the
// local `ng serve` defaults (SPA on :4200 calling the backend directly on :8080).
declare global {
  interface Window {
    __env?: {
      keycloakUrl?: string;
      keycloakRealm?: string;
      keycloakClientId?: string;
      apiBaseUrl?: string;
    };
  }
}

const env = (typeof window !== 'undefined' && window.__env) || {};

export const environment = {
  production: false,
  apiUrl: env.apiBaseUrl || 'http://localhost:8080/api/v1',
  keycloak: {
    url: env.keycloakUrl || 'http://localhost:8085',
    realm: env.keycloakRealm || 'immiauto',
    clientId: env.keycloakClientId || 'immiauto-frontend'
  }
};
