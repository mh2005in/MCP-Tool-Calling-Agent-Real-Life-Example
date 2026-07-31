import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { OrgDashboard, ConsultantSummary } from '../../../core/models/dashboard.model';

@Component({
  selector: 'app-org-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './org-dashboard.component.html',
  styleUrls: ['./org-dashboard.component.css']
})
export class OrgDashboardComponent implements OnInit {
  consultantId = '';
  data: OrgDashboard | null = null;
  summaries: ConsultantSummary[] = [];
  loading = true;

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.consultantId = this.route.snapshot.paramMap.get('consultantId') || '';
    this.api.getOrgDashboard().subscribe({
      next: (d) => {
        this.data = d;
        this.summaries = d.consultantSummaries || [];
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(w => w[0]).join('').toUpperCase().substring(0, 2);
  }
}
