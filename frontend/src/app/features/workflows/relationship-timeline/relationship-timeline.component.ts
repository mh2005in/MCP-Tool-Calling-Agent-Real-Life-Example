import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { RelationshipTimelineEntry } from '../../../core/models/workflow.model';

@Component({
  selector: 'app-relationship-timeline',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './relationship-timeline.component.html',
  styleUrls: ['./relationship-timeline.component.css']
})
export class RelationshipTimelineComponent implements OnInit {
  @Input() caseId!: string;

  entries: RelationshipTimelineEntry[] = [];
  showForm = false;
  editingEntry: RelationshipTimelineEntry | null = null;
  form: Partial<RelationshipTimelineEntry> = this.emptyForm();

  milestoneTypes = ['FIRST_CONTACT', 'FIRST_MEETING', 'DATING', 'ENGAGEMENT', 'MARRIAGE', 'COHABITATION', 'CHILD_BORN', 'OTHER'];
  evidenceCategories = ['PHOTOS', 'CHATS', 'TRAVEL_PROOF', 'JOINT_FINANCIAL', 'CORRESPONDENCE', 'OTHER'];

  milestoneIcons: Record<string, string> = {
    FIRST_CONTACT: 'chat', FIRST_MEETING: 'handshake', DATING: 'favorite_border',
    ENGAGEMENT: 'diamond', MARRIAGE: 'favorite', COHABITATION: 'home',
    CHILD_BORN: 'child_care', OTHER: 'event'
  };

  constructor(private api: ApiService) {}

  ngOnInit() { this.loadEntries(); }

  loadEntries() {
    this.api.getRelationshipTimeline(this.caseId).subscribe(e => this.entries = e);
  }

  openForm(entry?: RelationshipTimelineEntry) {
    if (entry) {
      this.editingEntry = entry;
      this.form = { ...entry };
    } else {
      this.editingEntry = null;
      this.form = this.emptyForm();
      this.form.sortOrder = this.entries.length + 1;
    }
    this.showForm = true;
  }

  saveEntry() {
    const data = this.form as RelationshipTimelineEntry;
    if (this.editingEntry?.id) {
      this.api.updateTimelineEntry(this.caseId, this.editingEntry.id, data).subscribe(() => {
        this.showForm = false;
        this.loadEntries();
      });
    } else {
      this.api.addTimelineEntry(this.caseId, data).subscribe(() => {
        this.showForm = false;
        this.loadEntries();
      });
    }
  }

  deleteEntry(id: string) {
    this.api.deleteTimelineEntry(this.caseId, id).subscribe(() => this.loadEntries());
  }

  cancelForm() { this.showForm = false; }

  formatMilestone(type: string): string {
    return type.replace(/_/g, ' ').toLowerCase().replace(/^\w/, c => c.toUpperCase());
  }

  private emptyForm(): Partial<RelationshipTimelineEntry> {
    return { milestoneType: 'FIRST_CONTACT', milestoneDate: '', location: '', description: '', evidenceCategory: '', evidenceCount: 0, sortOrder: 1 };
  }
}
