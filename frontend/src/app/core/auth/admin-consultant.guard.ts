import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Allows only admin consultants onto organization / consultant-management routes.
 * Non-admins are redirected to their own dashboard (or the unauthorized page if unlinked).
 */
export const adminConsultantGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.ensureProfile().pipe(
    map(me => {
      if (me.consultantAdmin) {
        return true;
      }
      return me.consultantId
        ? router.createUrlTree(['/consultant', me.consultantId, 'dashboard'])
        : router.createUrlTree(['/unauthorized']);
    })
  );
};
