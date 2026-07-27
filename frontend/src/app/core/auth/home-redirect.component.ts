import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Landing route after login: resolves the user's profile and sends an active consultant
 * straight to their dashboard, or to the unauthorized page if they have no consultant link.
 */
@Component({
  selector: 'app-home-redirect',
  standalone: true,
  template: `<p style="padding:2rem;font-family:sans-serif">Loading your workspace…</p>`
})
export class HomeRedirectComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.auth.ensureProfile().subscribe({
      next: me => {
        if (me.status === 'ACTIVE' && me.consultantId) {
          this.router.navigate(['/consultant', me.consultantId, 'dashboard']);
        } else {
          this.router.navigate(['/unauthorized']);
        }
      },
      error: () => this.router.navigate(['/unauthorized'])
    });
  }
}
