import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { PartyProfile } from '../../../core/models/party.model';
import { ChecklistItem, DocumentStatus } from '../../../core/models/checklist.model';

interface TaskGroup {
  category: string;
  items: ChecklistItem[];
  completedCount: number;
}

const partyCategories: Record<string, string[]> = {
  HOST: ['Host Documents', 'Invitation', 'Host Status', 'Host Financial'],
  SPONSOR: ['Sponsor Identity', 'Sponsor Financial', 'Sponsor Status', 'Sponsor Documents'],
  EMPLOYER: ['LMIA', 'Business Documents', 'Job Offer', 'Employer Documents', 'Recruitment']
};

@Component({
  selector: 'app-party-task-view',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './party-task-view.component.html',
  styleUrls: ['./party-task-view.component.css']
})
export class PartyTaskViewComponent implements OnInit {
  party: PartyProfile | null = null;
  allItems: ChecklistItem[] = [];
  taskGroups: TaskGroup[] = [];
  loading = true;
  error = '';

  private token = '';

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.token = this.route.snapshot.paramMap.get('token') || '';
    this.loadPortal();
  }

  private loadPortal() {
    this.loading = true;
    this.api.getPortalByToken(this.token).subscribe({
      next: (profile) => {
        this.party = profile;
        this.loadChecklist(profile.caseId);
      },
      error: () => {
        this.error = 'Invalid or expired portal link. Please contact your immigration consultant.';
        this.loading = false;
      }
    });
  }

  private loadChecklist(caseId: number) {
    this.api.getChecklist(caseId).subscribe({
      next: (items) => {
        this.allItems = items;
        this.filterAndGroup();
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load tasks. Please try again later.';
        this.loading = false;
      }
    });
  }

  private filterAndGroup() {
    if (!this.party) return;

    const relevantCategories = partyCategories[this.party.partyType] || [];
    const filtered = this.allItems.filter(item => {
      const cat = (item.category || '').toLowerCase();
      return relevantCategories.some(rc => cat.toLowerCase().includes(rc.toLowerCase()));
    });

    const map = new Map<string, ChecklistItem[]>();
    for (const item of filtered) {
      const cat = item.category || 'Other';
      if (!map.has(cat)) map.set(cat, []);
      map.get(cat)!.push(item);
    }

    this.taskGroups = Array.from(map.entries()).map(([category, items]) => ({
      category,
      items: items.sort((a, b) => a.sortOrder - b.sortOrder),
      completedCount: items.filter(i => i.status === 'ACCEPTED' || i.status === 'UPLOADED').length
    }));
  }

  get totalItems(): number {
    return this.taskGroups.reduce((sum, g) => sum + g.items.length, 0);
  }

  get completedItems(): number {
    return this.taskGroups.reduce((sum, g) => sum + g.completedCount, 0);
  }

  get progressPct(): number {
    if (this.totalItems === 0) return 0;
    return Math.round((this.completedItems / this.totalItems) * 100);
  }

  partyTypeLabel(type: string): string {
    const map: Record<string, string> = {
      HOST: 'Host',
      SPONSOR: 'Sponsor',
      EMPLOYER: 'Employer'
    };
    return map[type] || type;
  }

  partyTypeIcon(type: string): string {
    const map: Record<string, string> = {
      HOST: 'person_pin',
      SPONSOR: 'favorite',
      EMPLOYER: 'business'
    };
    return map[type] || 'person';
  }

  statusIcon(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'radio_button_unchecked',
      UPLOADED: 'pending',
      NEEDS_REVIEW: 'help_outline',
      ACCEPTED: 'check_circle',
      REJECTED: 'cancel',
      EXPIRED: 'schedule',
      TRANSLATION_REQUIRED: 'translate',
      CLIENT_ACTION_NEEDED: 'person',
      CONSULTANT_ACTION_NEEDED: 'assignment_ind',
      INCORRECT_DOCUMENT: 'error_outline',
      NOTARIZATION_REQUIRED: 'verified'
    };
    return map[status] || 'circle';
  }

  statusClass(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'status-pending',
      UPLOADED: 'status-info',
      NEEDS_REVIEW: 'status-warning',
      ACCEPTED: 'status-success',
      REJECTED: 'status-danger',
      EXPIRED: 'status-danger',
      TRANSLATION_REQUIRED: 'status-warning',
      CLIENT_ACTION_NEEDED: 'status-warning',
      CONSULTANT_ACTION_NEEDED: 'status-info'
    };
    return map[status] || 'status-pending';
  }

  statusLabel(status: DocumentStatus): string {
    const map: Record<string, string> = {
      NOT_UPLOADED: 'Not yet uploaded',
      UPLOADED: 'Uploaded',
      NEEDS_REVIEW: 'Needs review',
      ACCEPTED: 'Accepted',
      REJECTED: 'Rejected',
      EXPIRED: 'Expired',
      TRANSLATION_REQUIRED: 'Translation required',
      CLIENT_ACTION_NEEDED: 'Action needed',
      CONSULTANT_ACTION_NEEDED: 'Under review',
      INCORRECT_DOCUMENT: 'Incorrect document',
      NOTARIZATION_REQUIRED: 'Notarization required'
    };
    return map[status] || status;
  }

  onFileSelected(event: Event, item: ChecklistItem) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length || !this.party) return;
    const file = input.files[0];
    this.api.uploadDocument(this.party.caseId, file, item.category, item.documentName).subscribe({
      next: () => this.loadChecklist(this.party!.caseId),
      error: () => { /* upload error handling */ }
    });
  }
}
