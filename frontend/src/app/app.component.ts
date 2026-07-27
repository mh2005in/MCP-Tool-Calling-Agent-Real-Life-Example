import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  isAdminPage = true;
  consultantId = 0;

  constructor(private router: Router, public auth: AuthService) {
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe(event => {
      const url = event.urlAfterRedirects;
      // Full-width pages without the consultant sidebar.
      this.isAdminPage = url === '/'
        || url.startsWith('/unauthorized')
        || url.startsWith('/auth/')
        || url.startsWith('/client/');

      const match = url.match(/\/consultant\/([^/]+)/);
      this.consultantId = match ? Number(match[1]) : 0;
    });
  }

  /** Consultant id for sidebar links: the one in the URL, or the logged-in user's own (e.g. on /admin). */
  get navConsultantId(): number {
    return this.consultantId || (this.auth.currentUser()?.consultantId ?? 0);
  }

  ngOnInit(): void {
    // Process any pending Entra redirect response, then load the app profile.
    this.auth.initialize().subscribe(() => {
      if (this.auth.isAuthenticated()) {
        this.auth.loadProfile().subscribe({ error: () => {} });
      }
    });
  }

  get isAdminConsultant(): boolean {
    return this.auth.currentUser()?.consultantAdmin ?? false;
  }

  logout(): void {
    this.auth.logout();
  }
}
