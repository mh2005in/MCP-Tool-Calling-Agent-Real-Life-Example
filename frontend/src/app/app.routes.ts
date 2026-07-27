import { Routes } from '@angular/router';
import { HomeRedirectComponent } from './core/auth/home-redirect.component';
import { UnauthorizedComponent } from './core/auth/unauthorized.component';
import { adminConsultantGuard } from './core/auth/admin-consultant.guard';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeRedirectComponent, canActivate: [authGuard], pathMatch: 'full' },
  { path: 'unauthorized', component: UnauthorizedComponent, canActivate: [authGuard] },
  {
    path: 'admin',
    canActivate: [authGuard, adminConsultantGuard],
    loadComponent: () => import('./features/admin/admin-portal/admin-portal.component').then(m => m.AdminPortalComponent)
  },
  {
    path: 'consultant/:consultantId/dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'consultant/:consultantId/org-dashboard',
    canActivate: [authGuard, adminConsultantGuard],
    loadComponent: () => import('./features/dashboard/org-dashboard/org-dashboard.component').then(m => m.OrgDashboardComponent)
  },
  {
    path: 'consultant/:consultantId/view-dashboard/:targetId',
    canActivate: [authGuard, adminConsultantGuard],
    loadComponent: () => import('./features/dashboard/view-dashboard/view-dashboard.component').then(m => m.ViewDashboardComponent)
  },
  {
    path: 'consultant/:consultantId/cases',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cases/case-list/case-list.component').then(m => m.CaseListComponent)
  },
  {
    path: 'consultant/:consultantId/cases/new',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cases/case-form/case-form.component').then(m => m.CaseFormComponent)
  },
  {
    path: 'consultant/:consultantId/cases/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cases/case-detail/case-detail.component').then(m => m.CaseDetailComponent)
  },
  {
    // Section 4.1 - Forms & Package workspace (Milestone 2: mapping review).
    // Replaced by the full FormsPackageWorkspaceComponent in Milestone 5.
    path: 'consultant/:consultantId/cases/:id/forms-package',
    canActivate: [authGuard],
    loadComponent: () => import('./features/forms-package/mapping-review/mapping-review.component').then(m => m.MappingReviewComponent)
  },
  {
    path: 'consultant/:consultantId/clients',
    canActivate: [authGuard],
    loadComponent: () => import('./features/clients/client-list/client-list.component').then(m => m.ClientListComponent)
  },
  {
    path: 'consultant/:consultantId/clients/new',
    canActivate: [authGuard],
    loadComponent: () => import('./features/clients/client-form/client-form.component').then(m => m.ClientFormComponent)
  },
  {
    path: 'consultant/:consultantId/clients/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/clients/client-form/client-form.component').then(m => m.ClientFormComponent)
  },
  {
    path: 'consultant/:consultantId/templates',
    canActivate: [authGuard, adminConsultantGuard],
    loadComponent: () => import('./features/templates/template-management/template-management.component').then(m => m.TemplateManagementComponent)
  },
  {
    // Section 4.1 - Milestone 6: admin form catalogue governance
    path: 'consultant/:consultantId/form-catalogue',
    canActivate: [authGuard, adminConsultantGuard],
    loadComponent: () => import('./features/forms-catalogue/forms-catalogue.component').then(m => m.FormsCatalogueComponent)
  },
  {
    path: 'client/checklist/:caseId',
    loadComponent: () => import('./features/client-view/client-checklist/client-checklist.component').then(m => m.ClientChecklistComponent)
  },
  {
    path: 'party/:token',
    loadComponent: () => import('./features/party-portal/party-task-view/party-task-view.component').then(m => m.PartyTaskViewComponent)
  }
];
