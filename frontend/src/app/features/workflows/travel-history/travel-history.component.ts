import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { TravelHistoryEntry, PhysicalPresenceSummary } from '../../../core/models/workflow.model';

@Component({
  selector: 'app-travel-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './travel-history.component.html',
  styleUrls: ['./travel-history.component.css']
})
export class TravelHistoryComponent implements OnInit {
  @Input() caseId!: string;
  @Input() showPresenceCalc = false;

  entries: TravelHistoryEntry[] = [];
  presence: PhysicalPresenceSummary | null = null;
  showForm = false;
  editingEntry: TravelHistoryEntry | null = null;
  formError = '';
  today = new Date().toISOString().split('T')[0];

  form: Partial<TravelHistoryEntry> = this.emptyForm();

  purposeOptions = ['TOURISM', 'WORK', 'STUDY', 'FAMILY_VISIT', 'TRANSIT', 'RESIDENCE', 'OTHER'];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadEntries();
    if (this.showPresenceCalc) this.loadPresence();
  }

  loadEntries() {
    this.api.getTravelHistory(this.caseId).subscribe(e => this.entries = e);
  }

  loadPresence() {
    this.api.getPhysicalPresence(this.caseId).subscribe(p => this.presence = p);
  }

  openForm(entry?: TravelHistoryEntry) {
    this.formError = '';
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

  isFormValid(): boolean {
    if (!this.form.country?.trim()) return false;
    if (!this.form.entryDate) return false;
    if (this.form.exitDate && this.form.entryDate && this.form.exitDate < this.form.entryDate) return false;
    return true;
  }

  saveEntry() {
    this.formError = '';
    if (!this.form.country?.trim()) {
      this.formError = 'Country is required';
      return;
    }
    if (!this.form.entryDate) {
      this.formError = 'Entry date is required';
      return;
    }
    if (this.form.exitDate && this.form.entryDate && this.form.exitDate < this.form.entryDate) {
      this.formError = 'Exit date cannot be before entry date';
      return;
    }

    const data = this.form as TravelHistoryEntry;
    if (this.editingEntry?.id) {
      this.api.updateTravelEntry(this.caseId, this.editingEntry.id, data).subscribe({
        next: () => {
          this.showForm = false;
          this.loadEntries();
          if (this.showPresenceCalc) this.loadPresence();
        },
        error: (err) => {
          this.formError = err.error?.message || err.error?.errors?.entryDate || 'Failed to update entry';
        }
      });
    } else {
      this.api.addTravelEntry(this.caseId, data).subscribe({
        next: () => {
          this.showForm = false;
          this.loadEntries();
          if (this.showPresenceCalc) this.loadPresence();
        },
        error: (err) => {
          this.formError = err.error?.message || err.error?.errors?.entryDate || 'Failed to add entry';
        }
      });
    }
  }

  deleteEntry(id: string) {
    this.api.deleteTravelEntry(this.caseId, id).subscribe(() => {
      this.loadEntries();
      if (this.showPresenceCalc) this.loadPresence();
    });
  }

  cancelForm() {
    this.showForm = false;
    this.formError = '';
  }

  private emptyForm(): Partial<TravelHistoryEntry> {
    return { country: '', entryDate: '', exitDate: '', purpose: 'TOURISM', daysAbsent: 0, notes: '', sortOrder: 1 };
  }
}
