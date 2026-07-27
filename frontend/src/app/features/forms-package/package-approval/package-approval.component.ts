import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { CasePackage, PackageIndex, PackageReadinessReport, ValidationIssue } from '../../../core/models/form-automation.model';

/**
 * Section 4.1 - Milestone 5: package assembly & approval.
 * Builds/refreshes a submission package, shows its index + persisted readiness
 * (with resolve actions for non-error issues), and gates consultant approval
 * on zero unresolved errors + explicit acknowledgement. Downloads the zip.
 */
@Component({
  selector: 'app-package-approval',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './package-approval.component.html',
  styleUrls: ['./package-approval.component.css']
})
export class PackageApprovalComponent implements OnChanges {
  @Input() caseId!: number;
  @Input() packageProfileId: number | null = null;

  pkg: CasePackage | null = null;
  index: PackageIndex | null = null;
  readiness: PackageReadinessReport | null = null;

  acknowledged = false;
  approvalNotes = '';
  busy = false;
  error = '';

  constructor(private api: ApiService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['packageProfileId']) {
      this.pkg = null;
      this.index = null;
      this.readiness = null;
      this.acknowledged = false;
    }
  }

  buildPackage(): void {
    if (!this.packageProfileId) return;
    this.busy = true;
    this.error = '';
    this.api.createOrRefreshPackage(this.caseId, this.packageProfileId).subscribe({
      next: (p) => { this.pkg = p; this.busy = false; this.loadDetails(); },
      error: (err) => { this.error = err.error?.message || 'Failed to build package.'; this.busy = false; }
    });
  }

  refresh(): void {
    if (!this.pkg) return;
    this.busy = true;
    this.error = '';
    this.api.refreshPackage(this.caseId, this.pkg.id).subscribe({
      next: (p) => { this.pkg = p; this.busy = false; this.loadDetails(); },
      error: (err) => { this.error = err.error?.message || 'Failed to refresh package.'; this.busy = false; }
    });
  }

  loadDetails(): void {
    if (!this.pkg) return;
    this.api.getPackageIndex(this.caseId, this.pkg.id).subscribe({ next: (i) => this.index = i });
    this.api.getPackageReadiness(this.caseId, this.pkg.id).subscribe({ next: (r) => this.readiness = r });
  }

  resolve(issue: ValidationIssue): void {
    if (!this.pkg || !issue.id) return;
    this.api.resolvePackageIssue(this.caseId, this.pkg.id, issue.id).subscribe({
      next: (r) => { this.readiness = r; this.reloadPackage(); },
      error: (err) => this.error = err.error?.message || 'Failed to resolve issue.'
    });
  }

  approve(): void {
    if (!this.pkg) return;
    this.busy = true;
    this.error = '';
    this.api.approvePackage(this.caseId, this.pkg.id, this.acknowledged, this.approvalNotes).subscribe({
      next: (p) => { this.pkg = p; this.busy = false; },
      error: (err) => { this.error = err.error?.message || 'Approval failed.'; this.busy = false; }
    });
  }

  download(): void {
    if (!this.pkg) return;
    this.api.downloadPackage(this.caseId, this.pkg.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `submission-package-${this.pkg!.profileCode}.zip`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => this.error = err.error?.message || 'Failed to download package.'
    });
  }

  private reloadPackage(): void {
    if (!this.pkg) return;
    this.api.getPackages(this.caseId).subscribe({
      next: (list) => {
        const found = list.find(p => p.id === this.pkg!.id);
        if (found) this.pkg = found;
      }
    });
  }

  resolvableIssues(): ValidationIssue[] {
    return this.readiness ? this.readiness.issues.filter(i => i.severity !== 'ERROR' && !i.resolved) : [];
  }

  errorIssues(): ValidationIssue[] {
    return this.readiness ? this.readiness.issues.filter(i => i.severity === 'ERROR') : [];
  }

  canApprove(): boolean {
    return !!this.pkg && !this.pkg.approvalBlocked && this.acknowledged
      && this.pkg.status !== 'APPROVED' && !this.busy;
  }

  statusClass(status: string): string {
    switch (status) {
      case 'APPROVED': return 'badge badge-ok';
      case 'READY_FOR_APPROVAL': return 'badge badge-ok';
      case 'VALIDATION_FAILED': return 'badge badge-error';
      default: return 'badge';
    }
  }
}
