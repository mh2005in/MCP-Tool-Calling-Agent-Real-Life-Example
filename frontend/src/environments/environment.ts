export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  keycloak: {
    // Public base URL of Keycloak (browser-reachable). Realm + public SPA client.
    url: 'http://localhost:8085',
    realm: 'immiauto',
    clientId: 'immiauto-frontend'
  }
};
