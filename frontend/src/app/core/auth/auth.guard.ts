import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

/**
 * Requires an authenticated Keycloak session. If the user is not logged in, redirects to Keycloak
 * and returns to the originally requested URL after login. Replaces the former MSAL guard.
 */
export const authGuard: CanActivateFn = async (_route, state) => {
  const keycloak = inject(KeycloakService);
  if (keycloak.isLoggedIn()) {
    return true;
  }
  await keycloak.login({ redirectUri: window.location.origin + state.url });
  return false;
};
