import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { Client } from '../../../core/models/client.model';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.css']
})
export class ClientListComponent implements OnInit {
  clients: Client[] = [];
  filteredClients: Client[] = [];
  loading = true;
  searchQuery = '';
  consultantId = '';

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.consultantId = this.route.snapshot.paramMap.get('consultantId') || '';
    this.api.getClients(this.consultantId).subscribe({
      next: (data) => { this.clients = data; this.filteredClients = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  applyFilter() {
    const q = this.searchQuery.toLowerCase();
    this.filteredClients = this.clients.filter(c =>
      c.fullName.toLowerCase().includes(q) ||
      c.email.toLowerCase().includes(q) ||
      (c.phone || '').includes(q) ||
      (c.clientNumber || '').toLowerCase().includes(q)
    );
  }

  statusBadge(status?: string): string {
    if (!status) return 'badge-other';
    const s = status.toLowerCase();
    if (s.includes('inside') || s.includes('student') || s.includes('worker')) return 'badge-inside';
    if (s.includes('outside') || s.includes('visitor')) return 'badge-outside';
    return 'badge-other';
  }
}
