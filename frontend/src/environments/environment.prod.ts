// Values come from runtime config (window.__env, set by assets/env.js) so hosts/ports
// are configured at deploy time via environment variables, not baked in at build time.
// Fallbacks keep a direct `ng build` usable without the runtime file.
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
  production: true,
  apiUrl: env.apiBaseUrl || '/api/v1',
  keycloak: {
    url: env.keycloakUrl || 'http://localhost:8085',
    realm: env.keycloakRealm || 'immiauto',
    clientId: env.keycloakClientId || 'immiauto-frontend'
  }
};
