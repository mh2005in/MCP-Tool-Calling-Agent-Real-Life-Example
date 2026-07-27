import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { WorkHistoryEntry } from '../../../core/models/workflow.model';

@Component({
  selector: 'app-work-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './work-history.component.html',
  styleUrls: ['./work-history.component.css']
})
export class WorkHistoryComponent implements OnInit {
  @Input() caseId!: number;

  entries: WorkHistoryEntry[] = [];
  showForm = false;
  editingEntry: WorkHistoryEntry | null = null;
  form: Partial<WorkHistoryEntry> = this.emptyForm();

  employmentTypes = ['FULL_TIME', 'PART_TIME', 'SELF_EMPLOYED', 'CONTRACT'];

  constructor(private api: ApiService) {}

  ngOnInit() { this.loadEntries(); }

  loadEntries() {
    this.api.getWorkHistory(this.caseId).subscribe(e => this.entries = e);
  }

  openForm(entry?: WorkHistoryEntry) {
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
    const data = this.form as WorkHistoryEntry;
    if (this.editingEntry?.id) {
      this.api.updateWorkEntry(this.caseId, this.editingEntry.id, data).subscribe(() => {
        this.showForm = false;
        this.loadEntries();
      });
    } else {
      this.api.addWorkEntry(this.caseId, data).subscribe(() => {
        this.showForm = false;
        this.loadEntries();
      });
    }
  }

  deleteEntry(id: number) {
    this.api.deleteWorkEntry(this.caseId, id).subscribe(() => this.loadEntries());
  }

  cancelForm() { this.showForm = false; }

  getReferenceStatus(e: WorkHistoryEntry): string {
    if (!e.referenceLetterReceived) return 'missing';
    if (e.referenceDatesMatch && e.referenceDutiesDescribed) return 'complete';
    return 'incomplete';
  }

  private emptyForm(): Partial<WorkHistoryEntry> {
    return {
      employerName: '', jobTitle: '', nocTeerCode: '', startDate: '', endDate: '',
      currentJob: false, hoursPerWeek: 40, employmentType: 'FULL_TIME', duties: '',
      country: 'Canada', city: '', referenceLetterReceived: false,
      referenceDatesMatch: false, referenceDutiesDescribed: false, sortOrder: 1
    };
  }
}
