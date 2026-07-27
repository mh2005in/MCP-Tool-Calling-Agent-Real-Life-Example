import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ChecklistTemplate, AuditLogEntry, ServiceType } from '../../../core/models/checklist.model';

@Component({
  selector: 'app-template-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './template-management.component.html',
  styleUrls: ['./template-management.component.css']
})
export class TemplateManagementComponent implements OnInit {
  templates: ChecklistTemplate[] = [];
  filteredTemplates: ChecklistTemplate[] = [];
  auditLog: AuditLogEntry[] = [];

  filterType: ServiceType | '' = '';
  filterApproval: '' | 'approved' | 'pending' = '';
  searchQuery = '';

  serviceTypes: { value: ServiceType; label: string }[] = [
    { value: 'STUDY_PERMIT', label: 'Study Permit' },
    { value: 'VISITOR_VISA', label: 'Visitor Visa' },
    { value: 'SPOUSAL_SPONSORSHIP', label: 'Spousal Sponsorship' },
    { value: 'EXPRESS_ENTRY', label: 'Express Entry' },
    { value: 'WORK_PERMIT', label: 'Work Permit' },
    { value: 'LMIA', label: 'LMIA' },
    { value: 'CITIZENSHIP', label: 'Citizenship' },
    { value: 'PGWP', label: 'PGWP' },
    { value: 'SUPER_VISA', label: 'Super Visa' },
    { value: 'PR_CARD_PRTD', label: 'PR Card / PRTD' },
    { value: 'PNP', label: 'PNP' },
    { value: 'OTHER', label: 'Other' }
  ];

  showCreateForm = false;
  editingTemplate: ChecklistTemplate | null = null;
  newTemplate: Partial<ChecklistTemplate> = this.emptyTemplate();

  showAuditPanel = false;
  auditTemplateId = 0;
  auditTemplateName = '';

  consultantId = 0;
  isAdmin = false;
  accessDenied = false;
  successMsg = '';
  errorMsg = '';

  constructor(private api: ApiService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.consultantId = Number(this.route.snapshot.paramMap.get('consultantId'));
    if (this.consultantId) {
      this.api.getConsultant(this.consultantId).subscribe({
        next: (c) => {
          this.isAdmin = c.admin;
          if (!c.admin) {
            this.accessDenied = true;
          } else {
            this.loadTemplates();
          }
        },
        error: () => {
          this.router.navigate(['/consultant', this.consultantId, 'dashboard']);
        }
      });
    }
  }

  private loadTemplates() {
    this.api.getAllTemplates().subscribe(data => {
      this.templates = data;
      this.applyFilters();
    });
  }

  applyFilters() {
    let result = [...this.templates];
    if (this.filterType) {
      result = result.filter(t => t.serviceType === this.filterType);
    }
    if (this.filterApproval === 'approved') {
      result = result.filter(t => t.approvedForUse);
    } else if (this.filterApproval === 'pending') {
      result = result.filter(t => !t.approvedForUse);
    }
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(t =>
        t.documentName.toLowerCase().includes(q) ||
        t.category.toLowerCase().includes(q) ||
        (t.description || '').toLowerCase().includes(q)
      );
    }
    this.filteredTemplates = result;
  }

  private emptyTemplate(): Partial<ChecklistTemplate> {
    return {
      serviceType: '' as ServiceType,
      category: '',
      documentName: '',
      description: '',
      required: true,
      conditional: false,
      conditionDescription: '',
      sortOrder: 0,
      sourceUrl: ''
    };
  }

  openCreate() {
    this.editingTemplate = null;
    this.newTemplate = this.emptyTemplate();
    this.showCreateForm = true;
  }

  openEdit(t: ChecklistTemplate) {
    this.editingTemplate = t;
    this.newTemplate = { ...t };
    this.showCreateForm = true;
  }

  cancelForm() {
    this.showCreateForm = false;
    this.editingTemplate = null;
    this.newTemplate = this.emptyTemplate();
  }

  saveTemplate() {
    if (!this.newTemplate.documentName || !this.newTemplate.serviceType) return;

    if (this.editingTemplate) {
      this.api.updateTemplate(this.editingTemplate.id!, this.newTemplate as ChecklistTemplate, this.consultantId).subscribe({
        next: () => { this.toast('Template updated.'); this.cancelForm(); this.loadTemplates(); },
        error: (err) => this.handleError(err, 'Failed to update template.')
      });
    } else {
      this.api.createTemplate(this.newTemplate as ChecklistTemplate, this.consultantId).subscribe({
        next: () => { this.toast('Template created.'); this.cancelForm(); this.loadTemplates(); },
        error: (err) => this.handleError(err, 'Failed to create template.')
      });
    }
  }

  reviewTemplate(t: ChecklistTemplate) {
    this.api.reviewTemplate(t.id!, this.consultantId).subscribe({
      next: () => { this.toast('Template marked as reviewed.'); this.loadTemplates(); },
      error: () => this.showError('Failed to review template.')
    });
  }

  approveTemplate(t: ChecklistTemplate) {
    this.api.approveTemplate(t.id!, this.consultantId).subscribe({
      next: () => { this.toast('Template approved for use.'); this.loadTemplates(); },
      error: (err) => this.handleError(err, 'Failed to approve template.')
    });
  }

  revokeTemplate(t: ChecklistTemplate) {
    this.api.revokeTemplateApproval(t.id!, this.consultantId).subscribe({
      next: () => { this.toast('Approval revoked.'); this.loadTemplates(); },
      error: (err) => this.handleError(err, 'Failed to revoke approval.')
    });
  }

  deleteTemplate(t: ChecklistTemplate) {
    if (!confirm(`Delete template "${t.documentName}"?`)) return;
    this.api.deleteTemplate(t.id!, this.consultantId).subscribe({
      next: () => { this.toast('Template deleted.'); this.loadTemplates(); },
      error: (err) => this.handleError(err, 'Failed to delete template.')
    });
  }

  showAudit(t: ChecklistTemplate) {
    this.auditTemplateId = t.id!;
    this.auditTemplateName = t.documentName;
    this.showAuditPanel = true;
    this.api.getTemplateAuditHistory(t.id!).subscribe(data => {
      this.auditLog = data;
    });
  }

  closeAudit() {
    this.showAuditPanel = false;
    this.auditLog = [];
  }

  formatServiceType(type: string): string {
    return this.serviceTypes.find(s => s.value === type)?.label || type;
  }

  formatEnum(val: string): string {
    return val.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()).toLowerCase().replace(/^\w/, l => l.toUpperCase());
  }

  approvedCount(): number { return this.templates.filter(t => t.approvedForUse).length; }
  pendingCount(): number { return this.templates.filter(t => !t.approvedForUse).length; }

  templatesByServiceType(): { type: string; label: string; count: number }[] {
    const counts = new Map<string, number>();
    for (const t of this.templates) {
      counts.set(t.serviceType, (counts.get(t.serviceType) || 0) + 1);
    }
    return Array.from(counts.entries())
      .map(([type, count]) => ({ type, label: this.formatServiceType(type), count }))
      .sort((a, b) => b.count - a.count);
  }

  private toast(msg: string) { this.successMsg = msg; setTimeout(() => this.successMsg = '', 4000); }
  private showError(msg: string) { this.errorMsg = msg; setTimeout(() => this.errorMsg = '', 4000); }
  private handleError(err: any, fallback: string) {
    if (err.status === 403) {
      this.showError(err.error?.message || 'Only admin consultants can perform this action.');
    } else {
      this.showError(fallback);
    }
  }
}
