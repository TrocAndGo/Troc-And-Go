import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdCategory, AdService } from '../../services/ad.service';
import { DropdownButtonComponent } from '../dropdown-button/dropdown-button.component';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [DropdownButtonComponent, ReactiveFormsModule, CommonModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit {
  @Input() serviceCount: number = 0;
  @Input() showAdressFilters: boolean = true;
  @Input() region: string | null = null;
  @Input() department: string | null = null;
  @Input() city: string | null = null;
  @Input() category: string | null = null;
  form!: FormGroup;

  @Output() searched = new EventEmitter<FormGroup>();
  categories: AdCategory[] = [];

  constructor(private formBuilder: FormBuilder, private router: Router, private activatedRoute: ActivatedRoute, private adService: AdService) {}

  ngOnInit(): void {
    this.loadCategories();
    // Ensure empty strings are converted to null
    this.region = this.region?.trim() || null;
    this.department = this.department?.trim() || null;
    this.city = this.city?.trim() || null;
    this.category = this.category?.trim() || null;

    var controls = {
      category: [this.category]
    };

    if (this.showAdressFilters) {
      controls = Object.assign(controls, {
        region: [this.region],
        departement: [this.department],
        ville: [this.city]
      });
    }

    this.form = this.formBuilder.group(controls);
  }

  loadCategories() {
    this.adService.getCategories().subscribe((categories) => {
      this.categories = categories;
    })
  }

  get categoryNames(): string[] {
    return this.categories.map((category) => category.title);
  }

  get categoryValues(): string[] {
    return this.categories.map((category) => category.id.toString());
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.searched.emit(this.form);
  }

  getPluralSuffix(): string {
    return this.serviceCount > 1 ? 's' : '';
  }
}
