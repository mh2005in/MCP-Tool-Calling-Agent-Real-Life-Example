import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { ImmigrationCase, ServiceType, CaseStatus } from '../../../core/models/case.model';

@Component({
  selector: 'app-case-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './case-list.component.html',
  styleUrls: ['./case-list.component.css']
})
export class CaseListComponent implements OnInit {
  cases: ImmigrationCase[] = [];
  filteredCases: ImmigrationCase[] = [];
  loading = true;

  consultantId = 0;
  searchQuery = '';
  filterServiceType = '';
  filterCaseStatus = '';

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.consultantId = Number(this.route.snapshot.paramMap.get('consultantId'));
    this.api.getCases(this.consultantId).subscribe({
      next: (data) => { this.cases = data; this.applyFilters(); this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  applyFilters() {
    let result = [...this.cases];
    if (this.searchQuery) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(c =>
        (c.caseNumber || '').toLowerCase().includes(q) ||
        (c.clientName || '').toLowerCase().includes(q)
      );
    }
    if (this.filterServiceType) {
      result = result.filter(c => c.serviceType === this.filterServiceType);
    }
    if (this.filterCaseStatus) {
      result = result.filter(c => c.caseStatus === this.filterCaseStatus);
    }
    this.filteredCases = result;
  }

  progressPct(c: ImmigrationCase): number {
    if (!c.totalChecklistItems) return 0;
    return Math.round(((c.completedItems || 0) / c.totalChecklistItems) * 100);
  }

  isOverdue(deadline: string): boolean {
    return new Date(deadline) < new Date();
  }

  formatEnum(val: string): string {
    return val.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()).toLowerCase().replace(/^\w/, l => l.toUpperCase());
  }

  formatServiceType(type: ServiceType): string {
    const map: Record<string, string> = {
      STUDY_PERMIT: 'Study Permit', VISITOR_VISA: 'Visitor Visa',
      SPOUSAL_SPONSORSHIP: 'Spousal', EXPRESS_ENTRY: 'Express Entry',
      WORK_PERMIT: 'Work Permit', LMIA: 'LMIA', CITIZENSHIP: 'Citizenship', OTHER: 'Other'
    };
    return map[type] || type;
  }

  serviceClass(type: ServiceType): string {
    const map: Record<string, string> = {
      STUDY_PERMIT: 'badge-study', VISITOR_VISA: 'badge-visitor',
      SPOUSAL_SPONSORSHIP: 'badge-spousal', EXPRESS_ENTRY: 'badge-express',
      WORK_PERMIT: 'badge-work', LMIA: 'badge-lmia', CITIZENSHIP: 'badge-citizenship'
    };
    return map[type] || '';
  }

  statusClass(status: CaseStatus): string {
    const active = ['DOCUMENTS_COLLECTING', 'DOCUMENTS_UNDER_REVIEW', 'POST_SUBMISSION'];
    const pending = ['INTAKE_PENDING', 'CHECKLIST_PENDING'];
    const done = ['FILE_READY', 'APPLICATION_SUBMITTED', 'DECISION_RECEIVED'];
    if (active.includes(status)) return 'badge-status-active';
    if (pending.includes(status)) return 'badge-status-pending';
    if (done.includes(status)) return 'badge-status-done';
    if (status === 'CLOSED') return 'badge-status-closed';
    return '';
  }

  leadClass(status: string): string {
    if (status === 'NEW') return 'badge-lead-new';
    if (status === 'CLIENT_RETAINED') return 'badge-lead-retained';
    if (status === 'CLOSED') return 'badge-lead-closed';
    return 'badge-lead-progress';
  }
}
