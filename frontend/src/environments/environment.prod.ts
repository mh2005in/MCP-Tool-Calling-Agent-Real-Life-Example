export const environment = {
  production: true,
  apiUrl: '/api/v1',
  keycloak: {
    // Public base URL of Keycloak as reached from the browser. Change for a hosted deployment.
    url: 'http://localhost:8085',
    realm: 'immiauto',
    clientId: 'immiauto-frontend'
  }
};
