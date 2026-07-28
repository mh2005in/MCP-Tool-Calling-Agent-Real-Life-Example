import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { EMPTY, from, switchMap, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * Attaches a Keycloak access token to outgoing backend API calls.
 *
 * Public client-facing views (shared checklist links) are called without a token. For everything
 * else, the token is refreshed if it expires within 30s, then sent as a Bearer token.
 *
 * If the refresh fails (e.g. the SSO session / refresh token has expired), we re-authenticate the
 * user instead of silently dropping the request or surfacing a confusing 500/403 — otherwise an
 * expired session leaves the app stuck with an unusable token.
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
    // Only the token-refresh step is guarded here. Placing catchError BEFORE switchMap means
    // HTTP errors from the actual request (e.g. 403/409) are NOT swallowed — they propagate to
    // the component so the user sees the real error, while a failed refresh sends them to login.
    catchError(() => {
      keycloak.login({ redirectUri: window.location.href });
      return EMPTY;
    }),
    switchMap(token => {
      const authReq = token
        ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
        : req;
      return next(authReq);
    })
  );
};
