import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ImmigrationCase, CaseStatus, LeadStatus } from '../../../core/models/case.model';
import { ChecklistItem, DocumentStatus } from '../../../core/models/checklist.model';
import { CaseDocument } from '../../../core/models/document.model';
import { Reminder } from '../../../core/models/reminder.model';
import { IntakeFormComponent } from '../intake-form/intake-form.component';
import { TravelHistoryComponent } from '../../workflows/travel-history/travel-history.component';
import { WorkHistoryComponent } from '../../workflows/work-history/work-history.component';
import { RelationshipTimelineComponent } from '../../workflows/relationship-timeline/relationship-timeline.component';
import { RecruitmentTrackerComponent } from '../../workflows/recruitment-tracker/recruitment-tracker.component';
import { EvidenceCategorizerComponent } from '../../workflows/evidence-categorizer/evidence-categorizer.component';
import { ChecklistApprovalComponent } from '../checklist-approval/checklist-approval.component';
import { ExpiryAlert, ConsistencyIssue } from '../../../core/models/automation.model';
import { PartyProfile } from '../../../core/models/party.model';
import { CasePipelineComponent } from '../case-pipeline/case-pipeline.component';

@Component({
  selector: 'app-case-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, IntakeFormComponent, TravelHistoryComponent, WorkHistoryComponent, RelationshipTimelineComponent, RecruitmentTrackerComponent, EvidenceCategorizerComponent, ChecklistApprovalComponent, CasePipelineComponent],
  templateUrl: './case-detail.component.html',
  styleUrls: ['./case-detail.component.css']
})
export class CaseDetailComponent implements OnInit {
  caseData: ImmigrationCase | null = null;
  checklistItems: ChecklistItem[] = [];
  documents: CaseDocument[] = [];
  reminders: Reminder[] = [];

  activeTab = 'overview';
  tabs: { key: string; label: string; icon: string; count?: number }[] = [];

  caseStatuses: CaseStatus[] = [
    'INTAKE_PENDING', 'INTAKE_COMPLETED', 'CHECKLIST_PENDING',
    'DOCUMENTS_COLLECTING', 'DOCUMENTS_UNDER_REVIEW', 'FILE_READY',
    'APPLICATION_PREP', 'FINAL_REVIEW',
    'APPLICATION_SUBMITTED', 'POST_SUBMISSION', 'DECISION_RECEIVED', 'CLOSED'
  ];
  newCaseStatus: CaseStatus = '' as CaseStatus;

  // Checklist add
  showAddChecklist = false;
  newItem: Partial<ChecklistItem> = { category: 'Identity', documentName: '', description: '', required: true, conditional: false, status: 'NOT_UPLOADED' as DocumentStatus, sortOrder: 0 };

  // Document upload
  uploading = false;
  uploadingCount = 0;

  // Reminder add
  showAddReminder = false;
  newReminder: Partial<Reminder> = { subject: '', messageBody: '', channel: 'EMAIL', status: 'DRAFT' as any };

  checklistGroups: { category: string; items: ChecklistItem[] }[] = [];

  // Automation & workflow data
  expiryAlerts: ExpiryAlert[] = [];
  consistencyIssues: ConsistencyIssue[] = [];
  parties: PartyProfile[] = [];
  showAddParty = false;
  newParty: Partial<PartyProfile> = { partyType: 'HOST', fullName: '', email: '', portalEnabled: false };

  consultantId = 0;
  private caseId = 0;

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.consultantId = Number(this.route.snapshot.paramMap.get('consultantId'));
    this.caseId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadCase();
    this.loadChecklist();
    this.loadDocuments();
    this.loadReminders();
    this.loadAlerts();
    this.loadParties();
  }

  loadCase() {
    this.api.getCase(this.consultantId, this.caseId).subscribe(data => {
      this.caseData = data;
      this.newCaseStatus = data.caseStatus;
      this.buildTabs();
    });
  }

  private loadChecklist() {
    this.api.getChecklist(this.caseId).subscribe(data => {
      this.checklistItems = data;
      this.groupChecklist();
      this.buildTabs();
    });
  }

  private loadDocuments() {
    this.api.getDocuments(this.caseId).subscribe(data => {
      this.documents = data;
      this.buildTabs();
    });
  }

  private loadReminders() {
    this.api.getReminders(this.caseId).subscribe(data => {
      this.reminders = data;
      this.buildTabs();
    });
  }

  private loadAlerts() {
    this.api.getExpiryAlertsByCase(this.caseId).subscribe(a => this.expiryAlerts = a);
    this.api.checkConsistency(this.caseId).subscribe(i => this.consistencyIssues = i);
  }

  private loadParties() {
    this.api.getParties(this.caseId).subscribe(p => {
      this.parties = p;
      this.buildTabs();
    });
  }

  addParty() {
    if (!this.newParty.fullName) return;
    this.api.createParty(this.caseId, this.newParty as PartyProfile).subscribe(() => {
      this.showAddParty = false;
      this.newParty = { partyType: 'HOST', fullName: '', email: '', portalEnabled: false };
      this.loadParties();
    });
  }

  deleteParty(id: number) {
    this.api.deleteParty(id).subscribe(() => this.loadParties());
  }

  acknowledgeAlert(id: number) {
    this.api.acknowledgeExpiryAlert(id, 'consultant').subscribe(() => this.loadAlerts());
  }

  private buildTabs() {
    const type = this.caseData?.serviceType;
    this.tabs = [
      { key: 'overview', label: 'Overview', icon: 'info' },
      { key: 'intake', label: 'Intake', icon: 'quiz' },
      { key: 'checklist', label: 'Checklist', icon: 'checklist', count: this.checklistItems.length },
      { key: 'documents', label: 'Documents', icon: 'description', count: this.documents.length },
      { key: 'reminders', label: 'Reminders', icon: 'notifications', count: this.reminders.length }
    ];

    if (this.shouldShowTravelHistory(type)) {
      this.tabs.push({ key: 'travel', label: 'Travel History', icon: 'flight' });
    }
    if (this.shouldShowWorkHistory(type)) {
      this.tabs.push({ key: 'work-history', label: 'Work History', icon: 'work' });
    }
    if (type === 'SPOUSAL_SPONSORSHIP') {
      this.tabs.push({ key: 'relationship', label: 'Relationship', icon: 'favorite' });
      this.tabs.push({ key: 'evidence', label: 'Evidence', icon: 'photo_library' });
    }
    if (type === 'LMIA') {
      this.tabs.push({ key: 'recruitment', label: 'Recruitment', icon: 'person_search' });
    }
    if (this.shouldShowParties(type)) {
      this.tabs.push({ key: 'parties', label: 'Parties', icon: 'group', count: this.parties.length });
    }
    if (this.caseData?.caseStatus === 'CHECKLIST_PENDING') {
      this.tabs.push({ key: 'approval', label: 'Approve Checklist', icon: 'verified_user' });
    }
    if (this.expiryAlerts.length > 0 || this.consistencyIssues.length > 0) {
      this.tabs.push({ key: 'alerts', label: 'Alerts', icon: 'warning', count: this.expiryAlerts.filter(a => !a.acknowledged).length + this.consistencyIssues.length });
    }
  }

  private shouldShowTravelHistory(type?: string): boolean {
    return ['CITIZENSHIP', 'PR_CARD_PRTD', 'EXPRESS_ENTRY', 'STUDY_PERMIT', 'VISITOR_VISA'].includes(type || '');
  }

  private shouldShowWorkHistory(type?: string): boolean {
    return ['EXPRESS_ENTRY', 'WORK_PERMIT', 'LMIA'].includes(type || '');
  }

  private shouldShowParties(type?: string): boolean {
    return ['VISITOR_VISA', 'SUPER_VISA', 'SPOUSAL_SPONSORSHIP', 'WORK_PERMIT', 'LMIA'].includes(type || '');
  }

  showPresenceCalc(): boolean {
    return this.caseData?.serviceType === 'CITIZENSHIP' || this.caseData?.serviceType === 'PR_CARD_PRTD';
  }

  private groupChecklist() {
    const groups = new Map<string, ChecklistItem[]>();
    for (const item of this.checklistItems) {
      const cat = item.category || 'Other';
      if (!groups.has(cat)) groups.set(cat, []);
      groups.get(cat)!.push(item);
    }
    this.checklistGroups = Array.from(groups.entries()).map(([category, items]) => ({ category, items }));
  }

  progressPct(): number {
    if (!this.caseData?.totalChecklistItems) return 0;
    return Math.round(((this.caseData.completedItems || 0) / this.caseData.totalChecklistItems) * 100);
  }

  updateStatus() {
    if (!this.newCaseStatus) return;
    this.api.updateCaseStatus(this.consultantId, this.caseId, this.newCaseStatus).subscribe(updated => {
      this.caseData = updated;
    });
  }

  addChecklistItem() {
    if (!this.newItem.documentName) return;
    this.api.addChecklistItem(this.caseId, this.newItem as ChecklistItem).subscribe(() => {
      this.showAddChecklist = false;
      this.newItem = { category: 'Identity', documentName: '', description: '', required: true, conditional: false, status: 'NOT_UPLOADED' as DocumentStatus, sortOrder: 0 };
      this.loadChecklist();
      this.loadCase();
    });
  }

  updateItemStatus(item: ChecklistItem) {
    this.api.updateChecklistItemStatus(this.caseId, item.id!, item.status).subscribe(() => this.loadCase());
  }

  deleteItem(item: ChecklistItem) {
    if (!confirm('Remove this checklist item?')) return;
    this.api.deleteChecklistItem(this.caseId, item.id!).subscribe(() => { this.loadChecklist(); this.loadCase(); });
  }

  onFileSelected(event: Event) {
    const files = (event.target as HTMLInputElement).files;
    if (!files || files.length === 0) return;
    this.uploading = true;
    this.uploadingCount = files.length;
    let done = 0;
    for (let i = 0; i < files.length; i++) {
      this.api.uploadDocument(this.caseId, files[i]).subscribe({
        next: () => { done++; if (done === files.length) { this.uploading = false; this.loadDocuments(); } },
        error: () => { done++; if (done === files.length) { this.uploading = false; this.loadDocuments(); } }
      });
    }
  }

  reviewDoc(doc: CaseDocument, status: DocumentStatus) {
    this.api.reviewDocument(this.caseId, doc.id!, status).subscribe(() => this.loadDocuments());
  }

  createReminder() {
    if (!this.newReminder.subject || !this.newReminder.messageBody) return;
    this.api.createReminder(this.caseId, this.newReminder as Reminder).subscribe(() => {
      this.showAddReminder = false;
      this.newReminder = { subject: '', messageBody: '', channel: 'EMAIL', status: 'DRAFT' as any };
      this.loadReminders();
    });
  }

  approveReminder(r: Reminder) {
    this.api.approveReminder(r.id!, 'consultant').subscribe(() => this.loadReminders());
  }

  formatEnum(val: string): string {
    return val.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()).toLowerCase().replace(/^\w/, l => l.toUpperCase());
  }

  formatServiceType(type: string): string {
    const map: Record<string, string> = {
      STUDY_PERMIT: 'Study Permit', VISITOR_VISA: 'Visitor Visa',
      SPOUSAL_SPONSORSHIP: 'Spousal Sponsorship', EXPRESS_ENTRY: 'Express Entry',
      WORK_PERMIT: 'Work Permit', LMIA: 'LMIA', CITIZENSHIP: 'Citizenship',
      PGWP: 'Post-Graduation Work Permit', SUPER_VISA: 'Super Visa',
      PR_CARD_PRTD: 'PR Card / PRTD', PNP: 'Provincial Nominee Program', OTHER: 'Other'
    };
    return map[type] || type;
  }

  onChecklistGenerated() {
    this.loadChecklist();
    this.loadCase();
  }

  onChecklistApproved() {
    this.loadChecklist();
    this.loadCase();
    this.activeTab = 'checklist';
  }

  formatSize(bytes?: number): string {
    if (!bytes) return '—';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(0) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  isOverdue(deadline: string): boolean { return new Date(deadline) < new Date(); }

  statusClass(status: string): string {
    const active = ['DOCUMENTS_COLLECTING', 'DOCUMENTS_UNDER_REVIEW', 'POST_SUBMISSION', 'APPLICATION_PREP', 'FINAL_REVIEW'];
    const pending = ['INTAKE_PENDING', 'CHECKLIST_PENDING', 'INTAKE_COMPLETED'];
    const done = ['FILE_READY', 'APPLICATION_SUBMITTED', 'DECISION_RECEIVED'];
    if (active.includes(status)) return 'badge-status-active';
    if (pending.includes(status)) return 'badge-status-pending';
    if (done.includes(status)) return 'badge-status-done';
    return 'badge-status-closed';
  }

  leadClass(status: string): string {
    if (status === 'NEW') return 'badge-lead-new';
    if (status === 'CLIENT_RETAINED') return 'badge-lead-retained';
    if (status === 'CLOSED') return 'badge-lead-closed';
    return 'badge-lead-progress';
  }

  itemIcon(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'radio_button_unchecked', UPLOADED: 'pending', NEEDS_REVIEW: 'help_outline',
      ACCEPTED: 'check_circle', REJECTED: 'cancel', EXPIRED: 'schedule',
      TRANSLATION_REQUIRED: 'translate', CLIENT_ACTION_NEEDED: 'person', CONSULTANT_ACTION_NEEDED: 'assignment_ind',
      INCORRECT_DOCUMENT: 'error_outline', NOTARIZATION_REQUIRED: 'verified'
    };
    return map[status] || 'circle';
  }

  itemIconClass(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'icon-muted', UPLOADED: 'icon-info', NEEDS_REVIEW: 'icon-warning',
      ACCEPTED: 'icon-success', REJECTED: 'icon-danger', EXPIRED: 'icon-danger',
      TRANSLATION_REQUIRED: 'icon-warning', CLIENT_ACTION_NEEDED: 'icon-warning', CONSULTANT_ACTION_NEEDED: 'icon-info'
    };
    return map[status] || 'icon-muted';
  }

  docStatusClass(status: string): string {
    if (status === 'ACCEPTED') return 'doc-accepted';
    if (status === 'REJECTED') return 'doc-rejected';
    if (status === 'NEEDS_REVIEW' || status === 'UPLOADED') return 'doc-review';
    if (status === 'UPLOADED') return 'doc-uploaded';
    return 'doc-default';
  }

  reminderStatusClass(status: string): string {
    if (status === 'DRAFT') return 'reminder-draft';
    if (status === 'APPROVED') return 'reminder-approved';
    if (status === 'SENT') return 'reminder-sent';
    return '';
  }

  channelIcon(channel: string): string {
    if (channel === 'EMAIL') return 'email';
    if (channel === 'WHATSAPP') return 'chat';
    return 'sms';
  }
}
