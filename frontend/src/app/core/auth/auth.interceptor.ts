import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { from, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * Attaches a Keycloak access token to outgoing backend API calls.
 *
 * Public client-facing views (shared checklist links) are called without a token. For everything
 * else, the token is refreshed if it expires within 30s, then sent as a Bearer token.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isApiCall = req.url.startsWith(environment.apiUrl);
  const isPublicClientView = req.url.includes('/checklist/client/');
  if (!isApiCall || isPublicClientView) {
    return next(req);
  }

  const keycloak = inject(KeycloakService);
  if (!keycloak.isLoggedIn()) {
    return next(req);
  }

  return from(keycloak.updateToken(30).then(() => keycloak.getToken())).pipe(
    switchMap(token => {
      const authReq = token
        ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
        : req;
      return next(authReq);
    })
  );
};
