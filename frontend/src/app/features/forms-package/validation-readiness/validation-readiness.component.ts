import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { PackageReadinessReport, ValidationIssue } from '../../../core/models/form-automation.model';

interface SeverityGroup {
  severity: string;
  label: string;
  count: number;
}

/**
 * Section 4.1 - Milestone 4: validation & readiness.
 * Loads the deterministic readiness report for a case + package profile and
 * groups issues by severity with form/severity filters. Approval is blocked
 * while any ERROR remains.
 */
@Component({
  selector: 'app-validation-readiness',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './validation-readiness.component.html',
  styleUrls: ['./validation-readiness.component.css']
})
export class ValidationReadinessComponent implements OnChanges {
  @Input() caseId!: number;
  @Input() packageProfileId: number | null = null;

  report: PackageReadinessReport | null = null;
  loading = false;
  error = '';

  severityFilter = 'ALL';
  formFilter = 'ALL';

  readonly severities: SeverityGroup[] = [
    { severity: 'ERROR', label: 'Errors', count: 0 },
    { severity: 'WARNING', label: 'Warnings', count: 0 },
    { severity: 'DECISION', label: 'Consultant decisions', count: 0 },
    { severity: 'CLIENT_CONFIRMATION', label: 'Client confirmations', count: 0 },
    { severity: 'UNRESOLVED_EVIDENCE', label: 'Unresolved evidence', count: 0 }
  ];

  constructor(private api: ApiService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['packageProfileId']) {
      this.load();
    }
  }

  load(): void {
    this.report = null;
    this.error = '';
    if (!this.packageProfileId) return;
    this.loading = true;
    this.api.getReadinessReport(this.caseId, this.packageProfileId).subscribe({
      next: (r) => {
        this.report = r;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to evaluate readiness.';
        this.loading = false;
      }
    });
  }

  countFor(severity: string): number {
    if (!this.report) return 0;
    switch (severity) {
      case 'ERROR': return this.report.errorCount;
      case 'WARNING': return this.report.warningCount;
      case 'DECISION': return this.report.decisionCount;
      case 'CLIENT_CONFIRMATION': return this.report.clientConfirmationCount;
      case 'UNRESOLVED_EVIDENCE': return this.report.unresolvedEvidenceCount;
      default: return 0;
    }
  }

  formCodes(): string[] {
    if (!this.report) return [];
    return Array.from(new Set(
      this.report.issues.map(i => i.formCode).filter((c): c is string => !!c)
    )).sort();
  }

  filteredIssues(): ValidationIssue[] {
    if (!this.report) return [];
    return this.report.issues.filter(i =>
      (this.severityFilter === 'ALL' || i.severity === this.severityFilter) &&
      (this.formFilter === 'ALL' || i.formCode === this.formFilter)
    );
  }

  setSeverityFilter(severity: string): void {
    this.severityFilter = this.severityFilter === severity ? 'ALL' : severity;
  }

  severityLabel(severity: string): string {
    return this.severities.find(s => s.severity === severity)?.label || severity;
  }

  severityClass(severity: string): string {
    switch (severity) {
      case 'ERROR': return 'sev sev-error';
      case 'WARNING': return 'sev sev-warn';
      case 'DECISION': return 'sev sev-decision';
      case 'CLIENT_CONFIRMATION': return 'sev sev-client';
      case 'UNRESOLVED_EVIDENCE': return 'sev sev-evidence';
      default: return 'sev';
    }
  }
}
