import { Component, inject } from '@angular/core';
import { AuthService } from './auth.service';

/**
 * Shown when an authenticated user has no active consultant link (e.g. PENDING/DISABLED).
 */
@Component({
  selector: 'app-unauthorized',
  standalone: true,
  template: `
    <div style="padding:3rem;font-family:sans-serif;max-width:560px;margin:0 auto;text-align:center">
      <h2>Access not yet available</h2>
      <p>Your account is signed in but isn't linked to an active consultant workspace yet.
         Please contact your administrator.</p>
      <button (click)="logout()"
              style="margin-top:1rem;padding:.5rem 1rem;cursor:pointer">Sign out</button>
    </div>
  `
})
export class UnauthorizedComponent {
  private readonly auth = inject(AuthService);
  logout(): void { this.auth.logout(); }
}
