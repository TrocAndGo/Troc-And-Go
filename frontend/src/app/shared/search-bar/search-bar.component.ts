import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdCategory, AdressFilters, AdService } from '../../services/ad.service';
import { Page } from '../../utils/PageableResponse';
import { DropdownButtonComponent } from '../dropdown-button/dropdown-button.component';
import { PaginationComponent } from '../pagination/pagination.component';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [DropdownButtonComponent, ReactiveFormsModule, CommonModule, PaginationComponent],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit {
  @Input() serviceCount: number = 0;
  @Input() showFilters: boolean = true;
  @Input() paginationData: Page | null = null;
  @Input() region: string | null = null;
  @Input() department: string | null = null;
  @Input() city: string | null = null;
  @Input() category: string | null = null;
  form!: FormGroup;
  regions: string[] = [];
  departments: string[] = [];
  cities: string[] = [];
  categories: AdCategory[] = [];


  @Output() searched = new EventEmitter<FormGroup>();
  @Output() onPageChange = new EventEmitter<number>();

  constructor(private formBuilder: FormBuilder, private router: Router, private activatedRoute: ActivatedRoute, private adService: AdService) {}

  ngOnInit(): void {
    this.loadCategories();
    this.category = this.category?.trim() || null;

    var controls = {
      category: [this.category]
    };

    if (this.showFilters) {
      this.loadAdressFilters();
      this.region = this.region?.trim() || null;
      this.department = this.department?.trim() || null;
      this.city = this.city?.trim() || null;

      controls = Object.assign(controls, {
        region: [this.region],
        departement: [this.department],
        ville: [this.city]
      });
    }

    this.form = this.formBuilder.group(controls);
  }

  loadCategories() {
    this.adService.getCategories().subscribe((categories: AdCategory[]) => {
      this.categories = categories;
    })
  }

  loadAdressFilters() {
    this.adService.getAdressFilters().subscribe((filters: AdressFilters) => {
      this.regions = filters.regions;
      this.departments = filters.departments;
      this.cities = filters.cities;
    });
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
