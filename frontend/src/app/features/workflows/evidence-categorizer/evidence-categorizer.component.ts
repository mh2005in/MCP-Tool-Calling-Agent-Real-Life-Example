import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { CaseDocument } from '../../../core/models/document.model';
import { DocumentStatus } from '../../../core/models/checklist.model';

interface EvidenceCategory {
  name: string;
  icon: string;
  count: number;
}

@Component({
  selector: 'app-evidence-categorizer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './evidence-categorizer.component.html',
  styleUrls: ['./evidence-categorizer.component.css']
})
export class EvidenceCategorizerComponent implements OnInit {
  @Input() caseId!: string;

  documents: CaseDocument[] = [];
  loading = true;
  error = '';
  filterCategory = '';

  categoryNames = [
    'Photos & Images',
    'Chat & Communication',
    'Travel Records',
    'Joint Financial',
    'Cohabitation Proof',
    'Third-party Statements',
    'Official Documents',
    'Other'
  ];

  categoryIcons: Record<string, string> = {
    'Photos & Images': 'photo_library',
    'Chat & Communication': 'chat',
    'Travel Records': 'flight',
    'Joint Financial': 'account_balance',
    'Cohabitation Proof': 'home',
    'Third-party Statements': 'description',
    'Official Documents': 'verified',
    'Other': 'folder'
  };

  categories: EvidenceCategory[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadDocuments();
  }

  loadDocuments() {
    this.loading = true;
    this.api.getDocuments(this.caseId).subscribe({
      next: (docs) => {
        this.documents = docs;
        this.buildCategories();
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load documents.';
        this.loading = false;
      }
    });
  }

  private buildCategories() {
    this.categories = this.categoryNames.map(name => ({
      name,
      icon: this.categoryIcons[name],
      count: this.documents.filter(d => this.resolveCategory(d) === name).length
    }));
  }

  resolveCategory(doc: CaseDocument): string {
    const cat = doc.documentCategory || '';
    if (this.categoryNames.includes(cat)) return cat;
    return 'Other';
  }

  get filteredDocs(): CaseDocument[] {
    if (!this.filterCategory) return this.documents;
    return this.documents.filter(d => this.resolveCategory(d) === this.filterCategory);
  }

  get totalCount(): number {
    return this.documents.length;
  }

  selectFilter(name: string) {
    this.filterCategory = this.filterCategory === name ? '' : name;
  }

  onCategoryChange(doc: CaseDocument, newCategory: string) {
    doc.documentCategory = newCategory;
    const status: DocumentStatus = doc.status || 'UPLOADED';
    this.api.reviewDocument(this.caseId, doc.id!, status, 'Category: ' + newCategory).subscribe({
      next: () => this.buildCategories(),
      error: () => { /* silent fallback, local state already updated */ }
    });
  }

  formatFileSize(bytes?: number): string {
    if (!bytes) return '';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  }

  fileIcon(mimeType?: string): string {
    if (!mimeType) return 'insert_drive_file';
    if (mimeType.startsWith('image/')) return 'image';
    if (mimeType.includes('pdf')) return 'picture_as_pdf';
    if (mimeType.includes('word') || mimeType.includes('document')) return 'article';
    if (mimeType.includes('sheet') || mimeType.includes('excel')) return 'table_chart';
    return 'insert_drive_file';
  }
}
