import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Consultant } from '../../../core/models/consultant.model';

@Component({
  selector: 'app-admin-portal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-portal.component.html',
  styleUrls: ['./admin-portal.component.css']
})
export class AdminPortalComponent implements OnInit {
  consultants: Consultant[] = [];
  filteredConsultants: Consultant[] = [];
  selectedConsultant: Consultant | null = null;
  searchQuery = '';
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  newConsultant: Partial<Consultant> = {
    fullName: '',
    email: '',
    phone: '',
    licenseNumber: '',
    companyName: '',
    admin: false
  };

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit() {
    this.loadConsultants();
  }

  loadConsultants() {
    this.loading = true;
    this.api.getConsultants().subscribe({
      next: (data) => {
        this.consultants = data;
        this.filteredConsultants = data;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilter() {
    const q = this.searchQuery.toLowerCase();
    this.filteredConsultants = this.consultants.filter(c =>
      c.fullName.toLowerCase().includes(q) ||
      c.email.toLowerCase().includes(q) ||
      (c.licenseNumber || '').toLowerCase().includes(q) ||
      (c.companyName || '').toLowerCase().includes(q)
    );
  }

  selectConsultant(consultant: Consultant) {
    this.selectedConsultant = consultant;
  }

  toggleActive(consultant: Consultant) {
    const target = !consultant.active;
    const action = target ? 'Enable' : 'Disable';
    if (!confirm(`${action} login for ${consultant.fullName}?`)) {
      return;
    }
    this.api.setConsultantActive(consultant.id, target).subscribe({
      next: (updated) => { consultant.active = updated.active; },
      error: (err) => { this.errorMessage = err.error?.message || `Failed to ${action.toLowerCase()} consultant.`; }
    });
  }

  goToDashboard() {
    if (this.selectedConsultant) {
      this.router.navigate(['/consultant', this.selectedConsultant.id, 'dashboard']);
    }
  }

  addConsultant() {
    if (!this.newConsultant.fullName || !this.newConsultant.email) {
      this.errorMessage = 'Full name and email are required.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.api.createConsultant(this.newConsultant as Consultant).subscribe({
      next: (created) => {
        this.consultants.unshift(created);
        this.applyFilter();
        this.successMessage = `${created.fullName} has been added successfully.`;
        this.newConsultant = { fullName: '', email: '', phone: '', licenseNumber: '', companyName: '', admin: false };
        this.saving = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to add consultant. Please try again.';
        this.saving = false;
      }
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(w => w[0]).join('').toUpperCase().substring(0, 2);
  }
}
