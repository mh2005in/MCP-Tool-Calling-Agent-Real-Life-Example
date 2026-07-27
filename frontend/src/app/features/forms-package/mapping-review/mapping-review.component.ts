import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ValidationReadinessComponent } from '../validation-readiness/validation-readiness.component';
import { PackageApprovalComponent } from '../package-approval/package-approval.component';
import {
  PackageProfileSummary,
  MappingPreview,
  CanonicalValue,
  FormMappingPreview,
  CaseFormDraft
} from '../../../core/models/form-automation.model';

/**
 * Section 4.1 - Milestone 2: Mapping review.
 * Lets a consultant choose a supported package profile for a case, then review
 * the canonical data snapshot (with provenance/conflicts) and per-form mapped
 * field previews before any generation. Draft-only - nothing is filed.
 */
@Component({
  selector: 'app-mapping-review',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ValidationReadinessComponent, PackageApprovalComponent],
  templateUrl: './mapping-review.component.html',
  styleUrls: ['./mapping-review.component.css']
})
export class MappingReviewComponent implements OnInit {
  consultantId!: number;
  caseId!: number;

  profiles: PackageProfileSummary[] = [];
  selectedProfileId: number | null = null;
  preview: MappingPreview | null = null;

  drafts: CaseFormDraft[] = [];
  generating = false;
  uploadingFormId: number | null = null;

  loadingProfiles = false;
  loadingPreview = false;
  error = '';
  draftError = '';

  constructor(private route: ActivatedRoute, private api: ApiService) {}

  ngOnInit(): void {
    this.consultantId = Number(this.route.snapshot.paramMap.get('consultantId'));
    this.caseId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProfiles();
    this.loadDrafts();
  }

  loadProfiles(): void {
    this.loadingProfiles = true;
    this.error = '';
    this.api.getFormProfiles(this.caseId).subscribe({
      next: (p) => {
        this.profiles = p;
        this.loadingProfiles = false;
        if (p.length === 1) {
          this.selectProfile(p[0].id);
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load package profiles.';
        this.loadingProfiles = false;
      }
    });
  }

  selectProfile(profileId: number): void {
    this.selectedProfileId = profileId;
    this.loadingPreview = true;
    this.preview = null;
    this.error = '';
    this.api.getMappingPreview(this.caseId, profileId).subscribe({
      next: (mp) => {
        this.preview = mp;
        this.loadingPreview = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load mapping preview.';
        this.loadingPreview = false;
      }
    });
  }

  // --- draft generation ---

  loadDrafts(): void {
    this.api.getFormDrafts(this.caseId).subscribe({
      next: (d) => this.drafts = d,
      error: () => { /* drafts are optional context; ignore load errors here */ }
    });
  }

  generateDrafts(): void {
    if (!this.selectedProfileId) return;
    this.generating = true;
    this.draftError = '';
    this.api.generateDraftForms(this.caseId, this.selectedProfileId).subscribe({
      next: () => {
        this.generating = false;
        this.loadDrafts();
      },
      error: (err) => {
        this.draftError = err.error?.message || 'Failed to generate draft forms.';
        this.generating = false;
      }
    });
  }

  regenerate(draft: CaseFormDraft): void {
    this.draftError = '';
    this.api.regenerateDraft(this.caseId, draft.id).subscribe({
      next: () => this.loadDrafts(),
      error: (err) => this.draftError = err.error?.message || 'Failed to regenerate draft.'
    });
  }

  download(draft: CaseFormDraft): void {
    this.api.downloadDraft(this.caseId, draft.id).subscribe({
      next: (blob) => this.saveBlob(blob, draft.fileName || `${draft.formCode}.pdf`),
      error: (err) => this.draftError = err.error?.message || 'Failed to download draft.'
    });
  }

  uploadFilledForm(form: FormMappingPreview, event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.uploadingFormId = form.formDefinitionId;
    this.draftError = '';
    this.api.uploadFilledForm(this.caseId, form.formDefinitionId, file).subscribe({
      next: () => {
        this.uploadingFormId = null;
        input.value = '';
        this.loadDrafts();
      },
      error: (err) => {
        this.draftError = err.error?.message || 'Failed to upload filled form.';
        this.uploadingFormId = null;
        input.value = '';
      }
    });
  }

  /** A form can be auto-filled only if it is a fillable AcroForm with an approved mapping. */
  isAutoFillable(form: FormMappingPreview): boolean {
    return form.supportsFill && !!form.mappingStatus;
  }

  originLabel(origin: string): string {
    switch (origin) {
      case 'GENERATED': return 'Auto-generated';
      case 'UPLOADED': return 'Uploaded';
      case 'DATA_SHEET': return 'Data sheet';
      default: return origin;
    }
  }

  activeDrafts(): CaseFormDraft[] {
    return this.drafts.filter(d => d.status !== 'SUPERSEDED');
  }

  draftStatusClass(status: string): string {
    switch (status) {
      case 'APPROVED': return 'badge badge-ok';
      case 'READY_FOR_APPROVAL': return 'badge badge-ok';
      case 'DRAFT': return 'badge';
      case 'VALIDATION_FAILED': return 'badge badge-error';
      case 'SUPERSEDED': return 'badge badge-warn';
      default: return 'badge';
    }
  }

  private saveBlob(blob: Blob, fileName: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  // --- presentation helpers ---

  statusClass(status: string): string {
    switch (status) {
      case 'MAPPED': return 'badge badge-ok';
      case 'REVIEW_REQUIRED': return 'badge badge-warn';
      case 'UNSUPPORTED_TRANSFORM': return 'badge badge-warn';
      case 'MISSING_VALUE': return 'badge badge-error';
      case 'NO_SOURCE': return 'badge badge-error';
      default: return 'badge';
    }
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'MAPPED': return 'Mapped';
      case 'REVIEW_REQUIRED': return 'Review needed';
      case 'UNSUPPORTED_TRANSFORM': return 'Unsupported transform';
      case 'MISSING_VALUE': return 'Missing value';
      case 'NO_SOURCE': return 'No source';
      default: return status;
    }
  }

  selectedSourceLabel(value: CanonicalValue): string {
    const s = value.sources?.find(x => x.selected);
    return s ? s.sourceLabel : '—';
  }

  formHasIssues(form: FormMappingPreview): boolean {
    return !!form.diagnostic
      || form.unmappedRequiredFieldNames.length > 0
      || form.fields.some(f => f.status !== 'MAPPED');
  }

  reviewCount(): number {
    if (!this.preview) return 0;
    return this.preview.snapshot.values.filter(v => v.reviewRequired).length;
  }

  missingCount(): number {
    return this.preview ? this.preview.snapshot.missingFieldKeys.length : 0;
  }
}
