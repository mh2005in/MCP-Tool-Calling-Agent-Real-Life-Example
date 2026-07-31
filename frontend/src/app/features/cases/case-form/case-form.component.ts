import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Client } from '../../../core/models/client.model';
import { CreateCaseRequest, ServiceType, CaseSubtype, ApplicantRole } from '../../../core/models/case.model';

@Component({
  selector: 'app-case-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './case-form.component.html',
  styleUrls: ['./case-form.component.css']
})
export class CaseFormComponent implements OnInit {
  request: CreateCaseRequest = {
    serviceType: '' as ServiceType,
    clientId: '',
    consultantId: '',
    applyDefaultChecklist: true
  };

  subtypeOptions: { value: CaseSubtype; label: string }[] = [];
  roleOptions: { value: ApplicantRole; label: string }[] = [
    { value: 'PRINCIPAL_APPLICANT', label: 'Principal Applicant' },
    { value: 'SPOUSE', label: 'Spouse / Partner' },
    { value: 'DEPENDENT_CHILD', label: 'Dependent Child' },
    { value: 'SPONSOR', label: 'Sponsor' },
    { value: 'EMPLOYER', label: 'Employer' },
    { value: 'HOST', label: 'Host' }
  ];

  private subtypeMap: Record<string, { value: CaseSubtype; label: string }[]> = {
    STUDY_PERMIT: [
      { value: 'NEW_INTERNATIONAL_STUDENT', label: 'New International Student' },
      { value: 'STUDENT_INSIDE_CANADA', label: 'Student Inside Canada (Extension/Change)' },
      { value: 'MINOR_STUDENT', label: 'Minor Student (K-12)' },
      { value: 'QUEBEC_STUDENT', label: 'Quebec Student (CAQ)' },
      { value: 'STUDENT_WITH_FAMILY', label: 'Student with Accompanying Family' }
    ],
    VISITOR_VISA: [
      { value: 'FAMILY_VISITOR', label: 'Family Visit' },
      { value: 'TOURISM', label: 'Tourism' },
      { value: 'BUSINESS_VISITOR', label: 'Business Visitor' },
      { value: 'EVENT_VISITOR', label: 'Event / Conference' },
      { value: 'PREVIOUS_REFUSAL_VISITOR', label: 'Previous Refusal' }
    ],
    SPOUSAL_SPONSORSHIP: [
      { value: 'SPOUSAL_INLAND', label: 'Spousal Inland' },
      { value: 'SPOUSAL_OUTLAND', label: 'Spousal Outland' },
      { value: 'COMMON_LAW', label: 'Common-Law Partner' },
      { value: 'CONJUGAL', label: 'Conjugal Partner' },
      { value: 'DEPENDENT_CHILD', label: 'Dependent Child' }
    ],
    EXPRESS_ENTRY: [
      { value: 'FEDERAL_SKILLED_WORKER', label: 'Federal Skilled Worker (FSW)' },
      { value: 'CANADIAN_EXPERIENCE_CLASS', label: 'Canadian Experience Class (CEC)' },
      { value: 'FEDERAL_SKILLED_TRADES', label: 'Federal Skilled Trades (FST)' },
      { value: 'PNP_EXPRESS_ENTRY', label: 'PNP via Express Entry' }
    ],
    WORK_PERMIT: [
      { value: 'EMPLOYER_SPECIFIC_LMIA', label: 'Employer-Specific (LMIA)' },
      { value: 'EMPLOYER_SPECIFIC_EXEMPT', label: 'Employer-Specific (LMIA-Exempt)' },
      { value: 'OPEN_WORK_PERMIT', label: 'Open Work Permit' },
      { value: 'BRIDGING_OPEN_WORK_PERMIT', label: 'Bridging Open Work Permit' },
      { value: 'INTRA_COMPANY_TRANSFER', label: 'Intra-Company Transfer' },
      { value: 'INTERNATIONAL_MOBILITY', label: 'International Mobility Program' }
    ],
    LMIA: [
      { value: 'LMIA_HIGH_WAGE', label: 'High-Wage Stream' },
      { value: 'LMIA_LOW_WAGE', label: 'Low-Wage Stream' },
      { value: 'LMIA_PR_SUPPORT', label: 'PR Support Stream' },
      { value: 'LMIA_GLOBAL_TALENT', label: 'Global Talent Stream' },
      { value: 'LMIA_AGRICULTURAL', label: 'Agricultural Stream' },
      { value: 'LMIA_CAREGIVER', label: 'Caregiver Stream' },
      { value: 'LMIA_SEASONAL', label: 'Seasonal Agricultural Worker' }
    ],
    CITIZENSHIP: [
      { value: 'ADULT_CITIZENSHIP_GRANT', label: 'Adult Grant of Citizenship' },
      { value: 'MINOR_CITIZENSHIP_GRANT', label: 'Minor Grant of Citizenship' },
      { value: 'CITIZENSHIP_CERTIFICATE', label: 'Citizenship Certificate (Proof)' },
      { value: 'REPLACEMENT_CERTIFICATE', label: 'Replacement Certificate' }
    ],
    PR_CARD_PRTD: [
      { value: 'PR_CARD_RENEWAL', label: 'PR Card Renewal' },
      { value: 'PR_CARD_REPLACEMENT', label: 'PR Card Replacement' },
      { value: 'PR_CARD_FIRST', label: 'First PR Card' },
      { value: 'PR_TRAVEL_DOCUMENT', label: 'PR Travel Document (PRTD)' }
    ],
    PGWP: [],
    SUPER_VISA: [
      { value: 'SUPER_VISA_PARENT', label: 'Parent Super Visa' },
      { value: 'SUPER_VISA_GRANDPARENT', label: 'Grandparent Super Visa' }
    ],
    PNP: [],
    OTHER: [{ value: 'OTHER_SUBTYPE', label: 'Other' }]
  };

  onServiceTypeChange() {
    this.request.subtype = undefined;
    this.subtypeOptions = this.subtypeMap[this.request.serviceType] || [];
  }

  clients: Client[] = [];
  clientResults: Client[] = [];
  selectedClient: Client | null = null;
  clientSearch = '';
  showDropdown = false;
  submitting = false;
  errorMsg = '';

  consultantId = '';

  constructor(private api: ApiService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.consultantId = this.route.snapshot.paramMap.get('consultantId') || '';
    this.request.consultantId = this.consultantId;
    this.api.getClients(this.consultantId).subscribe(data => this.clients = data);
  }

  searchClients() {
    this.showDropdown = true;
    const q = this.clientSearch.toLowerCase();
    this.clientResults = this.clients.filter(c =>
      c.fullName.toLowerCase().includes(q) || c.email.toLowerCase().includes(q)
    ).slice(0, 8);
  }

  selectClient(client: Client) {
    this.selectedClient = client;
    this.request.clientId = client.id!;
    this.clientSearch = '';
    this.showDropdown = false;
    this.clientResults = [];
  }

  clearClient() {
    this.selectedClient = null;
    this.request.clientId = '';
  }

  canSubmit(): boolean {
    return !!this.request.serviceType && !!this.request.clientId;
  }

  submit() {
    if (!this.canSubmit()) return;
    this.submitting = true;
    this.errorMsg = '';

    this.api.createCase(this.consultantId, this.request).subscribe({
      next: (created) => {
        this.router.navigate(['/consultant', this.consultantId, 'cases', created.id]);
      },
      error: (err) => {
        this.submitting = false;
        this.errorMsg = 'Failed to create case. Please try again.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }
}
