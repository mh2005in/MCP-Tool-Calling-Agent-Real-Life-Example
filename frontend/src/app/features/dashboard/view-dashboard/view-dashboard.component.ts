import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Dashboard } from '../../../core/models/dashboard.model';
import { ImmigrationCase } from '../../../core/models/case.model';
import { Consultant } from '../../../core/models/consultant.model';

@Component({
  selector: 'app-view-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './view-dashboard.component.html',
  styleUrls: ['./view-dashboard.component.css']
})
export class ViewDashboardComponent implements OnInit {
  adminConsultantId = '';
  viewedConsultantId = '';
  viewedConsultant: Consultant | null = null;
  dashboard: Dashboard | null = null;
  cases: ImmigrationCase[] = [];
  loading = true;

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.adminConsultantId = this.route.snapshot.paramMap.get('consultantId') || '';
    this.viewedConsultantId = this.route.snapshot.paramMap.get('targetId') || '';

    this.api.getConsultant(this.viewedConsultantId).subscribe({
      next: (c) => this.viewedConsultant = c,
      error: () => {}
    });

    this.api.getDashboard(this.viewedConsultantId).subscribe({
      next: (d) => {
        this.dashboard = d;
        this.cases = d.recentCases || [];
        this.loading = false;
      },
      error: () => { this.loading = false; }
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

  getDocProgress(c: ImmigrationCase): number {
    if (!c.totalChecklistItems) return 0;
    return Math.round(((c.completedItems || 0) / c.totalChecklistItems) * 100);
  }
}
