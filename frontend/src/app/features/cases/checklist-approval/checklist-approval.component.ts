import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { ChecklistItem, DocumentStatus } from '../../../core/models/checklist.model';
import { CaseStatus } from '../../../core/models/case.model';

interface ChecklistGroup {
  category: string;
  items: ReviewableItem[];
  expanded: boolean;
}

interface ReviewableItem extends ChecklistItem {
  reviewed: boolean;
  noteDraft: string;
  editing: boolean;
}

@Component({
  selector: 'app-checklist-approval',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checklist-approval.component.html',
  styleUrls: ['./checklist-approval.component.css']
})
export class ChecklistApprovalComponent implements OnInit {
  @Input() caseId!: number;
  @Input() consultantId!: number;
  @Output() approved = new EventEmitter<void>();

  groups: ChecklistGroup[] = [];
  loading = true;
  error = '';
  approving = false;
  showAddForm = false;

  newItem: Partial<ChecklistItem> = this.emptyItem();

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadChecklist();
  }

  loadChecklist() {
    this.loading = true;
    this.api.getChecklist(this.caseId).subscribe({
      next: (items) => {
        this.buildGroups(items);
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load checklist.';
        this.loading = false;
      }
    });
  }

  private buildGroups(items: ChecklistItem[]) {
    const map = new Map<string, ReviewableItem[]>();
    for (const item of items) {
      const cat = item.category || 'Other';
      if (!map.has(cat)) map.set(cat, []);
      map.get(cat)!.push({
        ...item,
        reviewed: false,
        noteDraft: item.consultantReviewNote || '',
        editing: false
      });
    }
    this.groups = Array.from(map.entries()).map(([category, items]) => ({
      category,
      items: items.sort((a, b) => a.sortOrder - b.sortOrder),
      expanded: true
    }));
  }

  get allItems(): ReviewableItem[] {
    return this.groups.flatMap(g => g.items);
  }

  get totalItems(): number {
    return this.allItems.length;
  }

  get requiredItems(): number {
    return this.allItems.filter(i => i.required).length;
  }

  get conditionalItems(): number {
    return this.allItems.filter(i => i.conditional).length;
  }

  get reviewedItems(): number {
    return this.allItems.filter(i => i.reviewed).length;
  }

  get allReviewed(): boolean {
    return this.totalItems > 0 && this.allItems.every(i => i.reviewed);
  }

  get reviewProgress(): number {
    if (this.totalItems === 0) return 0;
    return Math.round((this.reviewedItems / this.totalItems) * 100);
  }

  toggleGroup(group: ChecklistGroup) {
    group.expanded = !group.expanded;
  }

  toggleReviewed(item: ReviewableItem) {
    item.reviewed = !item.reviewed;
  }

  reviewAll(group: ChecklistGroup) {
    const allReviewed = group.items.every(i => i.reviewed);
    group.items.forEach(i => i.reviewed = !allReviewed);
  }

  toggleEdit(item: ReviewableItem) {
    item.editing = !item.editing;
  }

  saveNote(item: ReviewableItem) {
    item.consultantReviewNote = item.noteDraft;
    item.editing = false;
  }

  removeItem(item: ReviewableItem, group: ChecklistGroup) {
    if (!item.id) return;
    this.api.deleteChecklistItem(this.caseId, item.id).subscribe({
      next: () => {
        group.items = group.items.filter(i => i.id !== item.id);
        if (group.items.length === 0) {
          this.groups = this.groups.filter(g => g !== group);
        }
      }
    });
  }

  addItem() {
    const item: ChecklistItem = {
      caseId: this.caseId,
      category: this.newItem.category || 'Other',
      documentName: this.newItem.documentName || '',
      description: this.newItem.description || '',
      status: 'NOT_UPLOADED' as DocumentStatus,
      required: this.newItem.required ?? true,
      conditional: this.newItem.conditional ?? false,
      conditionDescription: this.newItem.conditionDescription || '',
      sortOrder: this.totalItems + 1
    };

    this.api.addChecklistItem(this.caseId, item).subscribe({
      next: () => {
        this.showAddForm = false;
        this.newItem = this.emptyItem();
        this.loadChecklist();
      }
    });
  }

  approveChecklist() {
    if (!this.allReviewed || this.approving) return;
    this.approving = true;

    const updates = this.allItems.map(item =>
      this.api.updateChecklistItemStatus(
        this.caseId,
        item.id!,
        item.status,
        item.consultantReviewNote || 'Approved by consultant'
      ).toPromise()
    );

    Promise.all(updates).then(() => {
      return this.api.updateCaseStatus(
        this.consultantId,
        this.caseId,
        'DOCUMENTS_COLLECTING' as CaseStatus
      ).toPromise();
    }).then(() => {
      this.approving = false;
      this.approved.emit();
    }).catch(() => {
      this.error = 'Failed to approve checklist. Please try again.';
      this.approving = false;
    });
  }

  isGroupAllReviewed(group: ChecklistGroup): boolean {
    return group.items.every(i => i.reviewed);
  }

  groupReviewedCount(group: ChecklistGroup): number {
    return group.items.filter(i => i.reviewed).length;
  }

  get existingCategories(): string[] {
    return this.groups.map(g => g.category);
  }

  private emptyItem(): Partial<ChecklistItem> {
    return {
      category: '',
      documentName: '',
      description: '',
      required: true,
      conditional: false,
      conditionDescription: ''
    };
  }
}
