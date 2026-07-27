import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { Observable, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Me } from '../models/me.model';

/**
 * Thin wrapper around Keycloak for login/logout plus the application profile (GET /v1/me).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly keycloak = inject(KeycloakService);
  private readonly http = inject(HttpClient);

  /** Current application profile, populated after login via loadProfile(). */
  readonly currentUser = signal<Me | null>(null);

  /**
   * Keycloak is initialized in APP_INITIALIZER (see keycloak.config.ts), so the session is already
   * resolved by the time the app renders. Kept for call-site compatibility.
   */
  initialize(): Observable<unknown> {
    return of(null);
  }

  isAuthenticated(): boolean {
    return this.keycloak.isLoggedIn();
  }

  login(): void {
    this.keycloak.login({ redirectUri: window.location.origin });
  }

  logout(): void {
    this.currentUser.set(null);
    this.keycloak.logout(window.location.origin);
  }

  loadProfile(): Observable<Me> {
    return this.http.get<Me>(`${environment.apiUrl}/me`).pipe(
      tap(me => this.currentUser.set(me))
    );
  }

  /** Returns the cached profile if present, otherwise loads it from /v1/me. */
  ensureProfile(): Observable<Me> {
    const cached = this.currentUser();
    return cached ? of(cached) : this.loadProfile();
  }
}
