import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { ImmigrationCase, CaseStatus, LeadStatus } from '../../../core/models/case.model';

interface PipelineStage {
  key: string;
  label: string;
  icon: string;
  leadStatuses: LeadStatus[];
  caseStatuses: CaseStatus[];
}

@Component({
  selector: 'app-case-pipeline',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './case-pipeline.component.html',
  styleUrls: ['./case-pipeline.component.css']
})
export class CasePipelineComponent implements OnChanges {
  @Input() case!: ImmigrationCase;
  @Input() consultantId!: number;
  @Output() statusChanged = new EventEmitter<void>();

  stages: PipelineStage[] = [
    { key: 'lead_intake', label: 'Lead Intake', icon: 'person_add', leadStatuses: ['NEW'], caseStatuses: [] },
    { key: 'pre_screen', label: 'Pre-Screening', icon: 'fact_check', leadStatuses: ['PRE_SCREENED'], caseStatuses: [] },
    { key: 'consultation', label: 'Consultation', icon: 'event', leadStatuses: ['CONSULTATION_BOOKED'], caseStatuses: [] },
    { key: 'retainer', label: 'Retainer', icon: 'description', leadStatuses: ['RETAINER_SENT', 'CLIENT_RETAINED'], caseStatuses: [] },
    { key: 'intake', label: 'Questionnaire', icon: 'quiz', leadStatuses: [], caseStatuses: ['INTAKE_PENDING', 'INTAKE_COMPLETED'] },
    { key: 'checklist', label: 'Checklist', icon: 'checklist', leadStatuses: [], caseStatuses: ['CHECKLIST_PENDING'] },
    { key: 'documents', label: 'Document Collection', icon: 'upload_file', leadStatuses: [], caseStatuses: ['DOCUMENTS_COLLECTING', 'DOCUMENTS_UNDER_REVIEW'] },
    { key: 'review', label: 'File Review', icon: 'rate_review', leadStatuses: [], caseStatuses: ['FILE_READY'] },
    { key: 'prep', label: 'Application Prep', icon: 'build', leadStatuses: [], caseStatuses: ['APPLICATION_PREP'] },
    { key: 'final_review', label: 'Final Review', icon: 'verified', leadStatuses: [], caseStatuses: ['FINAL_REVIEW'] },
    { key: 'submitted', label: 'Submitted', icon: 'send', leadStatuses: [], caseStatuses: ['APPLICATION_SUBMITTED'] },
    { key: 'post_sub', label: 'Post-Submission', icon: 'hourglass_top', leadStatuses: [], caseStatuses: ['POST_SUBMISSION'] },
    { key: 'decision', label: 'Decision', icon: 'gavel', leadStatuses: [], caseStatuses: ['DECISION_RECEIVED'] }
  ];

  currentStageIndex = -1;
  expandedStage: string | null = null;

  // Retainer fields
  engagementLetterStatus = '';
  retainerSignedDate = '';

  // Application prep fields
  prepFormsCompleted = false;
  prepFeesPaid = false;
  prepPackageAssembled = false;
  prepNotes = '';

  // Final review fields
  finalReviewNotes = '';
  signoffConfirmed = false;
  signingOff = false;

  constructor(private api: ApiService) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['case'] && this.case) {
      this.computeCurrentStage();
      this.engagementLetterStatus = this.case.engagementLetterStatus || '';
      this.retainerSignedDate = this.case.retainerSignedDate || '';
      this.finalReviewNotes = this.case.finalReviewNotes || '';
    }
  }

  private computeCurrentStage() {
    const leadStatus = this.case.leadStatus;
    const caseStatus = this.case.caseStatus;

    // Lead phase takes priority when case status is at initial stage
    const isLeadPhase = ['NEW', 'PRE_SCREENED', 'CONSULTATION_BOOKED', 'RETAINER_SENT'].includes(leadStatus)
      && caseStatus === 'INTAKE_PENDING';

    for (let i = this.stages.length - 1; i >= 0; i--) {
      const stage = this.stages[i];
      if (isLeadPhase && stage.leadStatuses.includes(leadStatus as LeadStatus)) {
        this.currentStageIndex = i;
        this.expandedStage = stage.key;
        return;
      }
      if (!isLeadPhase && stage.caseStatuses.includes(caseStatus as CaseStatus)) {
        this.currentStageIndex = i;
        this.expandedStage = stage.key;
        return;
      }
    }

    // Fallback: CLIENT_RETAINED maps to retainer stage
    if (leadStatus === 'CLIENT_RETAINED') {
      this.currentStageIndex = 3;
      this.expandedStage = 'retainer';
    }
  }

  stageState(index: number): 'completed' | 'current' | 'future' {
    if (index < this.currentStageIndex) return 'completed';
    if (index === this.currentStageIndex) return 'current';
    return 'future';
  }

  toggleExpand(key: string) {
    this.expandedStage = this.expandedStage === key ? null : key;
  }

  // --- Retainer actions ---
  updateEngagementStatus(status: string) {
    this.api.updateCase(this.consultantId, this.case.id!, {
      engagementLetterStatus: status
    } as any).subscribe(() => this.statusChanged.emit());
  }

  markRetainerSigned() {
    const today = new Date().toISOString().split('T')[0];
    this.api.updateCase(this.consultantId, this.case.id!, {
      engagementLetterStatus: 'SIGNED',
      retainerSignedDate: today
    } as any).subscribe(() => {
      this.api.updateLeadStatus(this.consultantId, this.case.id!, 'CLIENT_RETAINED')
        .subscribe(() => this.statusChanged.emit());
    });
  }

  // --- Application Prep actions ---
  savePrepNotes() {
    this.api.updateCase(this.consultantId, this.case.id!, {
      consultantNotes: this.prepNotes
    } as any).subscribe(() => this.statusChanged.emit());
  }

  advanceToFinalReview() {
    this.api.updateCaseStatus(this.consultantId, this.case.id!, 'FINAL_REVIEW')
      .subscribe(() => this.statusChanged.emit());
  }

  // --- Final Review actions ---
  signOffAndSubmit() {
    if (!this.signoffConfirmed) return;
    this.signingOff = true;
    this.api.updateCase(this.consultantId, this.case.id!, {
      finalReviewedBy: this.consultantId,
      finalReviewedAt: new Date().toISOString(),
      finalReviewNotes: this.finalReviewNotes,
      consultantSignedOff: true
    } as any).subscribe(() => {
      this.api.updateCaseStatus(this.consultantId, this.case.id!, 'APPLICATION_SUBMITTED')
        .subscribe(() => {
          this.signingOff = false;
          this.statusChanged.emit();
        });
    });
  }

  formatEnum(val: string): string {
    return val.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()).toLowerCase().replace(/^\w/, l => l.toUpperCase());
  }
}
