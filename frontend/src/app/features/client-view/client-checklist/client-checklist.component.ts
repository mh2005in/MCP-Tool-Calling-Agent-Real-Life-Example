import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ClientChecklistResponse, ChecklistItem, DocumentStatus } from '../../../core/models/checklist.model';
import { CaseDocument } from '../../../core/models/document.model';

@Component({
  selector: 'app-client-checklist',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './client-checklist.component.html',
  styleUrls: ['./client-checklist.component.css']
})
export class ClientChecklistComponent implements OnInit {
  checklist: ClientChecklistResponse | null = null;
  groups: { category: string; items: ChecklistItem[] }[] = [];
  loading = true;
  error = '';

  uploadingItemId: string | null = null;
  dragOver = false;
  generalUploading = false;

  private caseId = '';

  private readonly uploadableStatuses: DocumentStatus[] = [
    'NOT_UPLOADED', 'REJECTED', 'EXPIRED', 'CLIENT_ACTION_NEEDED', 'INCORRECT_DOCUMENT'
  ];

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.caseId = this.route.snapshot.paramMap.get('caseId') || '';
    this.loadChecklist();
  }

  private loadChecklist() {
    this.loading = true;
    this.api.getClientChecklist(this.caseId).subscribe({
      next: (data) => {
        this.checklist = data;
        this.groupItems(data.items);
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load checklist. Please contact your consultant.';
        this.loading = false;
      }
    });
  }

  private groupItems(items: ChecklistItem[]) {
    const map = new Map<string, ChecklistItem[]>();
    for (const item of items) {
      const cat = item.category || 'Other';
      if (!map.has(cat)) map.set(cat, []);
      map.get(cat)!.push(item);
    }
    this.groups = Array.from(map.entries()).map(([category, items]) => ({ category, items }));
  }

  progressPct(): number {
    if (!this.checklist?.totalItems) return 0;
    return Math.round((this.checklist.completedItems / this.checklist.totalItems) * 100);
  }

  canUpload(item: ChecklistItem): boolean {
    return this.uploadableStatuses.includes(item.status);
  }

  onFileSelected(event: Event, item: ChecklistItem) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    this.uploadFileForItem(input.files[0], item);
    input.value = '';
  }

  uploadFileForItem(file: File, item: ChecklistItem) {
    if (!this.caseId || !item.id) return;
    this.uploadingItemId = item.id;
    this.api.uploadDocument(this.caseId, file, item.category, item.documentName).subscribe({
      next: () => {
        this.uploadingItemId = null;
        this.loadChecklist();
      },
      error: () => {
        this.uploadingItemId = null;
        this.error = 'Upload failed. Please try again or contact your consultant.';
      }
    });
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = false;
    const files = event.dataTransfer?.files;
    if (!files?.length || !this.caseId) return;
    this.uploadGeneral(files[0]);
  }

  onGeneralFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    this.uploadGeneral(input.files[0]);
    input.value = '';
  }

  private uploadGeneral(file: File) {
    this.generalUploading = true;
    this.api.uploadDocument(this.caseId, file).subscribe({
      next: () => {
        this.generalUploading = false;
        this.loadChecklist();
      },
      error: () => {
        this.generalUploading = false;
        this.error = 'Upload failed. Please try again or contact your consultant.';
      }
    });
  }

  statusIcon(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'radio_button_unchecked', UPLOADED: 'pending', NEEDS_REVIEW: 'help_outline',
      ACCEPTED: 'check_circle', REJECTED: 'cancel', EXPIRED: 'schedule',
      TRANSLATION_REQUIRED: 'translate', CLIENT_ACTION_NEEDED: 'person',
      CONSULTANT_ACTION_NEEDED: 'assignment_ind', INCORRECT_DOCUMENT: 'error_outline',
      NOTARIZATION_REQUIRED: 'verified'
    };
    return map[status] || 'circle';
  }

  statusClass(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'status-pending', UPLOADED: 'status-info', NEEDS_REVIEW: 'status-warning',
      ACCEPTED: 'status-success', REJECTED: 'status-danger', EXPIRED: 'status-danger',
      TRANSLATION_REQUIRED: 'status-warning', CLIENT_ACTION_NEEDED: 'status-warning',
      CONSULTANT_ACTION_NEEDED: 'status-info'
    };
    return map[status] || 'status-pending';
  }

  statusLabel(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'Not yet uploaded', UPLOADED: 'Uploaded — under review', NEEDS_REVIEW: 'Needs review',
      ACCEPTED: 'Accepted', REJECTED: 'Rejected — please re-upload', EXPIRED: 'Expired — please update',
      TRANSLATION_REQUIRED: 'Translation required', CLIENT_ACTION_NEEDED: 'Action needed from you',
      CONSULTANT_ACTION_NEEDED: 'Being reviewed by consultant', INCORRECT_DOCUMENT: 'Incorrect document',
      NOTARIZATION_REQUIRED: 'Notarization required'
    };
    return map[status] || status;
  }
}
