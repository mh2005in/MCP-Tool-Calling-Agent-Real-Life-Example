import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import {
  FormDefinition, FormFieldDefinition, FormInspectionResult, PackageProfileAdmin
} from '../../core/models/form-automation.model';

/**
 * Section 4.1 - Milestone 6: admin form-catalogue governance.
 * Lists governed forms, runs source-PDF inspection (which sets supports-fill /
 * status and thus auto-fill vs manual-upload), views discovered fields, and
 * lists/creates package profiles. Admin-only route.
 */
@Component({
  selector: 'app-forms-catalogue',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forms-catalogue.component.html',
  styleUrls: ['./forms-catalogue.component.css']
})
export class FormsCatalogueComponent implements OnInit {
  forms: FormDefinition[] = [];
  profiles: PackageProfileAdmin[] = [];
  fieldsByForm: { [formId: number]: FormFieldDefinition[] } = {};
  expandedFormId: number | null = null;
  inspection: { [formId: number]: FormInspectionResult } = {};
  busyFormId: number | null = null;
  error = '';

  showNewProfile = false;
  newProfile: Partial<PackageProfileAdmin> = { status: 'DRAFT' };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadForms();
    this.loadProfiles();
  }

  loadForms(): void {
    this.api.getCatalogueForms().subscribe({
      next: (f) => this.forms = f,
      error: (err) => this.error = err.error?.message || 'Failed to load forms.'
    });
  }

  loadProfiles(): void {
    this.api.getCatalogueProfiles().subscribe({ next: (p) => this.profiles = p });
  }

  inspect(form: FormDefinition): void {
    this.busyFormId = form.id;
    this.error = '';
    this.api.inspectCatalogueForm(form.id).subscribe({
      next: (r) => {
        this.inspection[form.id] = r;
        this.busyFormId = null;
        this.loadForms();
        if (this.expandedFormId === form.id) this.loadFields(form.id);
      },
      error: (err) => {
        this.error = err.error?.message || 'Inspection failed.';
        this.busyFormId = null;
      }
    });
  }

  toggleFields(form: FormDefinition): void {
    if (this.expandedFormId === form.id) {
      this.expandedFormId = null;
      return;
    }
    this.expandedFormId = form.id;
    this.loadFields(form.id);
  }

  loadFields(formId: number): void {
    this.api.getCatalogueFormFields(formId).subscribe({ next: (f) => this.fieldsByForm[formId] = f });
  }

  createProfile(): void {
    if (!this.newProfile.profileCode || !this.newProfile.displayName) return;
    this.api.createCatalogueProfile(this.newProfile).subscribe({
      next: () => {
        this.showNewProfile = false;
        this.newProfile = { status: 'DRAFT' };
        this.loadProfiles();
      },
      error: (err) => this.error = err.error?.message || 'Failed to create profile.'
    });
  }

  fillClass(form: FormDefinition): string {
    if (form.status === 'BLOCKED') return 'badge badge-error';
    if (form.supportsFill) return 'badge badge-ok';
    return 'badge badge-warn';
  }

  fillLabel(form: FormDefinition): string {
    if (form.status === 'BLOCKED') return 'Blocked (manual upload)';
    if (form.supportsFill) return 'Auto-fill';
    return 'Not inspected';
  }
}
