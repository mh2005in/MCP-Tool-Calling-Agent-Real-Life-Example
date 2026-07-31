import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Dashboard } from '../../../core/models/dashboard.model';
import { ImmigrationCase } from '../../../core/models/case.model';
import { Consultant } from '../../../core/models/consultant.model';
import { Reminder } from '../../../core/models/reminder.model';
import { ExpiryAlert } from '../../../core/models/automation.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  consultantId = '';
  consultant: Consultant | null = null;
  dashboard: Dashboard | null = null;
  cases: ImmigrationCase[] = [];
  loading = true;

  pendingReminders: Reminder[] = [];
  expiryAlerts: ExpiryAlert[] = [];
  scanningExpiry = false;
  expiryScanMsg = '';

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.consultantId = this.route.snapshot.paramMap.get('consultantId') || '';

    if (this.consultantId) {
      this.api.getOwnProfile(this.consultantId).subscribe({
        next: (c) => this.consultant = c,
        error: () => {}
      });

      this.api.getDashboard(this.consultantId).subscribe({
        next: (d) => {
          this.dashboard = d;
          this.cases = d.recentCases || [];
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });

      this.api.getPendingReminders(this.consultantId).subscribe({
        next: (r) => this.pendingReminders = r,
        error: () => {}
      });

      this.api.getExpiryAlertsByConsultant(this.consultantId).subscribe({
        next: (a) => this.expiryAlerts = a,
        error: () => {}
      });
    } else {
      this.loading = false;
    }
  }

  get missingDocsCases(): ImmigrationCase[] {
    const urgent = this.dashboard?.urgentCases || [];
    const recent = this.dashboard?.recentCases || [];
    const all = [...urgent, ...recent];
    const unique = new Map<string, ImmigrationCase>();
    for (const c of all) {
      if ((c.missingItems || 0) > 0 && !unique.has(c.id!)) {
        unique.set(c.id!, c);
      }
    }
    return Array.from(unique.values())
      .sort((a, b) => (b.missingItems || 0) - (a.missingItems || 0))
      .slice(0, 5);
  }

  get activeExpiryAlerts(): ExpiryAlert[] {
    return this.expiryAlerts.filter(a => !a.acknowledged).slice(0, 5);
  }

  triggerExpiryScan() {
    this.scanningExpiry = true;
    this.expiryScanMsg = '';
    this.api.triggerExpiryScan().subscribe({
      next: () => {
        this.scanningExpiry = false;
        this.expiryScanMsg = 'Expiry scan completed successfully.';
        // Reload alerts
        if (this.consultantId) {
          this.api.getExpiryAlertsByConsultant(this.consultantId).subscribe({
            next: (a) => this.expiryAlerts = a,
            error: () => {}
          });
        }
        setTimeout(() => this.expiryScanMsg = '', 4000);
      },
      error: () => {
        this.scanningExpiry = false;
        this.expiryScanMsg = 'Scan failed. Please try again.';
        setTimeout(() => this.expiryScanMsg = '', 4000);
      }
    });
  }

  formatServiceType(t: string): string {
    return t.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
  }

  formatStatus(s: string): string {
    return s.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  }

  getStatusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      'INTAKE_PENDING': 'badge-neutral', 'INTAKE_COMPLETED': 'badge-info',
      'CHECKLIST_PENDING': 'badge-warning', 'DOCUMENTS_COLLECTING': 'badge-warning',
      'DOCUMENTS_UNDER_REVIEW': 'badge-info', 'FILE_READY': 'badge-success',
      'APPLICATION_SUBMITTED': 'badge-primary', 'CLOSED': 'badge-neutral'
    };
    return map[status] || 'badge-neutral';
  }

  getSeverityClass(severity: string): string {
    const map: Record<string, string> = {
      'INFO': 'badge-info', 'WARNING': 'badge-warning',
      'URGENT': 'badge-danger', 'CRITICAL': 'badge-danger'
    };
    return map[severity] || 'badge-neutral';
  }

  getDocProgress(c: ImmigrationCase): number {
    if (!c.totalChecklistItems) return 0;
    return Math.round(((c.completedItems || 0) / c.totalChecklistItems) * 100);
  }
}
