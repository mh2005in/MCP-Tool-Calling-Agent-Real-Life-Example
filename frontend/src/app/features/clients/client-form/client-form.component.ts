import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.css']
})
export class ClientFormComponent implements OnInit {
  client: Client = { fullName: '', email: '' };
  isEdit = false;
  submitting = false;
  errorMsg = '';
  successMsg = '';
  consultantId = '';
  duplicateClientId: string | null = null;
  duplicateEmail = false;
  isAdmin = false;

  private clientId: string | null = null;

  constructor(private api: ApiService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.consultantId = this.route.snapshot.paramMap.get('consultantId') || '';
    const idParam = this.route.snapshot.paramMap.get('id');
    this.clientId = idParam || null;

    if (this.consultantId) {
      this.api.getConsultant(this.consultantId).subscribe({
        next: (c) => this.isAdmin = c.admin,
        error: () => this.isAdmin = false
      });
    }

    if (this.clientId) {
      this.isEdit = true;
      this.api.getClient(this.consultantId, this.clientId).subscribe(data => this.client = data);
    }
  }

  viewExistingClient() {
    if (this.duplicateClientId) {
      this.router.navigate(['/consultant', this.consultantId, 'clients', this.duplicateClientId]);
    }
  }

  dismissDuplicate() {
    this.duplicateClientId = null;
    this.errorMsg = '';
  }

  canSubmit(): boolean {
    return !!this.client.fullName && !!this.client.email;
  }

  submit() {
    if (!this.canSubmit()) return;
    this.submitting = true;
    this.errorMsg = '';
    this.duplicateEmail = false;

    const obs = this.isEdit
      ? this.api.updateClient(this.consultantId, this.clientId!, this.client)
      : this.api.createClient(this.consultantId, this.client);

    obs.subscribe({
      next: (saved) => {
        this.submitting = false;
        if (this.isEdit) {
          this.successMsg = 'Client updated successfully.';
          setTimeout(() => this.successMsg = '', 3000);
        } else {
          this.router.navigate(['/consultant', this.consultantId, 'clients']);
        }
      },
      error: (err) => {
        this.submitting = false;
        this.duplicateClientId = null;
        if (err.status === 409 && err.error?.existingClientId) {
          this.duplicateClientId = err.error.existingClientId;
          this.errorMsg = err.error.message || 'A client with this name, date of birth, and email already exists.';
        } else if (err.status === 409) {
          this.duplicateEmail = true;
          this.errorMsg = err.error?.message || 'A client with this email already exists.';
        } else {
          this.errorMsg = 'Failed to save client. Please try again.';
          setTimeout(() => this.errorMsg = '', 4000);
        }
      }
    });
  }
}
