import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { IntakeQuestionTemplate, IntakeResponse, IntakeSubmissionRequest, IntakeSummary } from '../../../core/models/intake.model';
import { ChecklistItem } from '../../../core/models/checklist.model';
import { ServiceType } from '../../../core/models/case.model';

@Component({
  selector: 'app-intake-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './intake-form.component.html',
  styleUrls: ['./intake-form.component.css']
})
export class IntakeFormComponent implements OnInit, OnChanges {
  @Input() caseId = '';
  @Input() serviceType: ServiceType = '' as ServiceType;
  @Output() checklistGenerated = new EventEmitter<void>();

  sections: string[] = [];
  questionsBySection: Record<string, IntakeQuestionTemplate[]> = {};
  answers: Record<string, string> = {};
  activeSection = '';

  existingResponses: IntakeResponse[] = [];
  summary: IntakeSummary | null = null;

  mode: 'form' | 'summary' = 'form';
  submitting = false;
  generating = false;
  hasSubmitted = false;
  successMsg = '';
  errorMsg = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadQuestions();
    this.loadExistingResponses();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['serviceType'] && !changes['serviceType'].firstChange) {
      this.loadQuestions();
    }
  }

  private loadQuestions() {
    if (!this.serviceType) return;
    this.api.getIntakeQuestions(this.serviceType as any).subscribe(questions => {
      this.sections = [];
      this.questionsBySection = {};
      for (const q of questions) {
        if (!this.questionsBySection[q.sectionName]) {
          this.questionsBySection[q.sectionName] = [];
          this.sections.push(q.sectionName);
        }
        this.questionsBySection[q.sectionName].push(q);
      }
      if (this.sections.length > 0 && !this.activeSection) {
        this.activeSection = this.sections[0];
      }
    });
  }

  private loadExistingResponses() {
    if (!this.caseId) return;
    this.api.getIntakeResponses(this.caseId).subscribe(responses => {
      this.existingResponses = responses;
      if (responses.length > 0) {
        this.hasSubmitted = true;
        for (const r of responses) {
          this.answers[r.questionKey] = r.answer;
        }
        this.loadSummary();
      }
    });
  }

  private loadSummary() {
    this.api.getIntakeSummary(this.caseId).subscribe(s => {
      this.summary = s;
    });
  }

  getOptions(q: IntakeQuestionTemplate): string[] {
    if (!q.options) return [];
    return q.options.split('|').map(o => o.trim());
  }

  sectionProgress(section: string): { answered: number; total: number } {
    const questions = this.questionsBySection[section] || [];
    const total = questions.length;
    const answered = questions.filter(q => this.answers[q.questionKey]?.trim()).length;
    return { answered, total };
  }

  sectionComplete(section: string): boolean {
    const p = this.sectionProgress(section);
    return p.answered === p.total && p.total > 0;
  }

  requiredMissing(): IntakeQuestionTemplate[] {
    const missing: IntakeQuestionTemplate[] = [];
    for (const section of this.sections) {
      for (const q of this.questionsBySection[section]) {
        if (q.required && !this.answers[q.questionKey]?.trim()) {
          missing.push(q);
        }
      }
    }
    return missing;
  }

  goToSection(section: string) {
    this.activeSection = section;
  }

  nextSection() {
    const idx = this.sections.indexOf(this.activeSection);
    if (idx < this.sections.length - 1) {
      this.activeSection = this.sections[idx + 1];
    }
  }

  prevSection() {
    const idx = this.sections.indexOf(this.activeSection);
    if (idx > 0) {
      this.activeSection = this.sections[idx - 1];
    }
  }

  isFirstSection(): boolean {
    return this.sections.indexOf(this.activeSection) === 0;
  }

  isLastSection(): boolean {
    return this.sections.indexOf(this.activeSection) === this.sections.length - 1;
  }

  submit() {
    const missing = this.requiredMissing();
    if (missing.length > 0) {
      this.errorMsg = `${missing.length} required question(s) not answered. Please review all sections.`;
      setTimeout(() => this.errorMsg = '', 5000);
      return;
    }

    this.submitting = true;
    const responses: IntakeResponse[] = [];
    let sortOrder = 0;

    for (const section of this.sections) {
      for (const q of this.questionsBySection[section]) {
        const answer = this.answers[q.questionKey] || '';
        responses.push({
          sectionName: q.sectionName,
          questionKey: q.questionKey,
          questionLabel: q.questionLabel,
          answer,
          sortOrder: ++sortOrder,
          flaggedForReview: false
        });
      }
    }

    const request: IntakeSubmissionRequest = { responses };
    this.api.submitIntake(this.caseId, request).subscribe({
      next: () => {
        this.submitting = false;
        this.hasSubmitted = true;
        this.successMsg = 'Intake submitted successfully.';
        setTimeout(() => this.successMsg = '', 4000);
        this.loadSummary();
        this.mode = 'summary';
      },
      error: (err) => {
        this.submitting = false;
        this.errorMsg = err.error?.message || 'Failed to submit intake. Please try again.';
        setTimeout(() => this.errorMsg = '', 6000);
      }
    });
  }

  generateChecklist() {
    this.generating = true;
    this.api.generateChecklistFromIntake(this.caseId).subscribe({
      next: (items) => {
        this.generating = false;
        this.successMsg = `Generated ${items.length} checklist items from intake answers.`;
        setTimeout(() => this.successMsg = '', 4000);
        this.checklistGenerated.emit();
      },
      error: () => {
        this.generating = false;
        this.errorMsg = 'Failed to generate checklist. Please try again.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  toggleFlag(response: IntakeResponse) {
    if (!response.id) return;
    this.api.flagIntakeResponse(response.id, !response.flaggedForReview).subscribe(updated => {
      response.flaggedForReview = updated.flaggedForReview;
      this.loadSummary();
    });
  }

  switchMode(m: 'form' | 'summary') {
    this.mode = m;
    if (m === 'summary') this.loadSummary();
  }
}
