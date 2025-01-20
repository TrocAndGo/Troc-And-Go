import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DropdownButtonComponent } from '../../shared/dropdown-button/dropdown-button.component';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [DropdownButtonComponent, ReactiveFormsModule, CommonModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit {
  @Input() serviceCount: number = 0;
  form!: FormGroup;

  constructor(private formBuilder: FormBuilder, private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    if(this.isOnSearchPage() === false) {
      this.form = this.formBuilder.group({
        category: [null, Validators.required],
      });
    } else {
      this.form = this.formBuilder.group({
        region: [null],
        departement: [null],
        ville: [null],
        category: [null],
      });
    }
  }

  onSubmit() {
    if (this.form.invalid) return;

    if (this.isOnSearchPage() === false) {
      this.router.navigate(['/recherche'], {
        queryParams: {
          area: this.form.value.area,
          category: this.form.value.category
        }
      });
    } else {
      this.router.navigate([], {
        relativeTo: this.activatedRoute,
        queryParams: {
          region: this.form.value.region,
          departement: this.form.value.departement,
          ville: this.form.value.ville,
          category: this.form.value.category
        },
        queryParamsHandling: 'merge',
      });
    }
  }

  getPluralSuffix(): string {
    return this.serviceCount > 1 ? 's' : '';
  }

  isOnSearchPage(): boolean {
    return this.activatedRoute.routeConfig?.path === 'recherche';
  }
}
