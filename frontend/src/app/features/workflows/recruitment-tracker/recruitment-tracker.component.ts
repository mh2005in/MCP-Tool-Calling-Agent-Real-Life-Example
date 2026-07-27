import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { RecruitmentEvidence, CandidateComparison } from '../../../core/models/workflow.model';
import { LmiaCompliance } from '../../../core/models/automation.model';

@Component({
  selector: 'app-recruitment-tracker',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recruitment-tracker.component.html',
  styleUrls: ['./recruitment-tracker.component.css']
})
export class RecruitmentTrackerComponent implements OnInit {
  @Input() caseId!: number;

  evidence: RecruitmentEvidence[] = [];
  candidates: CandidateComparison[] = [];
  compliance: LmiaCompliance | null = null;
  activeTab: 'evidence' | 'candidates' | 'compliance' = 'evidence';

  showEvidenceForm = false;
  showCandidateForm = false;
  evidenceForm: Partial<RecruitmentEvidence> = this.emptyEvidence();
  candidateForm: Partial<CandidateComparison> = this.emptyCandidate();
  evidenceFormError = '';
  today = new Date().toISOString().split('T')[0];

  evidenceTypes = ['JOB_AD', 'JOB_BANK_POSTING', 'INTERVIEW_RECORD', 'APPLICANT_SUMMARY', 'SCREENSHOT'];
  candidateTypes: ('CANADIAN' | 'FOREIGN')[] = ['CANADIAN', 'FOREIGN'];
  outcomeOptions = ['HIRED', 'NOT_HIRED', 'WITHDREW', 'NO_RESPONSE'];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadEvidence();
    this.loadCandidates();
    this.loadCompliance();
  }

  loadEvidence() { this.api.getRecruitmentEvidence(this.caseId).subscribe(e => this.evidence = e); }
  loadCandidates() { this.api.getCandidates(this.caseId).subscribe(c => this.candidates = c); }
  loadCompliance() { this.api.getLmiaCompliance(this.caseId).subscribe(c => this.compliance = c); }

  addEvidence() {
    this.evidenceFormError = '';
    if (!this.evidenceForm.platform?.trim()) {
      this.evidenceFormError = 'Platform is required';
      return;
    }
    if (!this.evidenceForm.postingDate) {
      this.evidenceFormError = 'Posting date is required';
      return;
    }
    if (this.evidenceForm.expiryDate && this.evidenceForm.postingDate
        && this.evidenceForm.expiryDate < this.evidenceForm.postingDate) {
      this.evidenceFormError = 'Expiry date cannot be before posting date';
      return;
    }
    const interviews = this.evidenceForm.interviewsConducted || 0;
    const applicants = this.evidenceForm.applicantsReceived || 0;
    if (interviews > applicants && applicants > 0) {
      this.evidenceFormError = 'Interviews conducted cannot exceed applicants received';
      return;
    }

    const data = this.evidenceForm as RecruitmentEvidence;
    data.sortOrder = this.evidence.length + 1;
    this.api.addRecruitmentEvidence(this.caseId, data).subscribe({
      next: () => {
        this.showEvidenceForm = false;
        this.evidenceForm = this.emptyEvidence();
        this.evidenceFormError = '';
        this.loadEvidence();
        this.loadCompliance();
      },
      error: (err) => {
        this.evidenceFormError = err.error?.message || 'Failed to add evidence';
      }
    });
  }

  deleteEvidence(id: number) {
    this.api.deleteRecruitmentEvidence(this.caseId, id).subscribe(() => {
      this.loadEvidence();
      this.loadCompliance();
    });
  }

  addCandidate() {
    const data = this.candidateForm as CandidateComparison;
    data.sortOrder = this.candidates.length + 1;
    this.api.addCandidate(this.caseId, data).subscribe(() => {
      this.showCandidateForm = false;
      this.candidateForm = this.emptyCandidate();
      this.loadCandidates();
    });
  }

  deleteCandidate(id: number) {
    this.api.deleteCandidate(this.caseId, id).subscribe(() => this.loadCandidates());
  }

  private emptyEvidence(): Partial<RecruitmentEvidence> {
    return { evidenceType: 'JOB_AD', platform: '', postingDate: '', daysPosted: 0, applicantsReceived: 0, interviewsConducted: 0, screenshotAttached: false, notes: '', sortOrder: 1 };
  }

  private emptyCandidate(): Partial<CandidateComparison> {
    return { candidateName: '', candidateType: 'CANADIAN', qualifications: '', yearsExperience: 0, educationLevel: '', languageSkills: '', interviewed: false, interviewNotes: '', outcome: '', nonHireReason: '', sortOrder: 1 };
  }
}
