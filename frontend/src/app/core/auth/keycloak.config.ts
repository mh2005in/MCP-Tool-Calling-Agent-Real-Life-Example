import { KeycloakService } from 'keycloak-angular';
import { environment } from '../../../environments/environment';

/**
 * APP_INITIALIZER factory: initializes Keycloak before the app bootstraps.
 *
 * <p>Uses {@code check-sso} so public routes (shared client/party links) render without forcing a
 * login; protected routes trigger a login redirect via {@link authGuard}. A hidden iframe
 * ({@code /assets/silent-check-sso.html}) detects an existing SSO session without a full redirect.
 */
export function initializeKeycloak(keycloak: KeycloakService): () => Promise<boolean> {
  return () =>
    keycloak.init({
      config: {
        url: environment.keycloak.url,
        realm: environment.keycloak.realm,
        clientId: environment.keycloak.clientId
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        checkLoginIframe: false,
        pkceMethod: 'S256'
      }
    });
}

/** Base OIDC scopes always requested at login. */
export const LOGIN_SCOPES = 'openid profile email';
