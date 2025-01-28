import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdCategory, AdService, AdUploadRequest } from '../../services/ad.service';
import { UserAdressService } from '../../services/user-adress.service';
import { DropdownButtonComponent } from '../../shared/dropdown-button/dropdown-button.component';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { errorMessageFromStatusCode } from '../../utils/ErrorMessage';

@Component({
  selector: 'app-create-ad',
  standalone: true,
  imports: [DropdownButtonComponent, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-ad.component.html',
  styleUrl: './create-ad.component.css'
})
export class CreateAdComponent implements OnInit {

  categories: AdCategory[] = [];
  userAdress: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private adService: AdService,
    private userAdressService: UserAdressService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.subscribeToUserAdress();
  }

  loadCategories(): void {
    this.adService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des catégories', err);
        this.toastr.error('Erreur lors de la récupération des catégories');
      }
    });
  }

  subscribeToUserAdress(): void {
    this.userAdressService.userAdress$.subscribe({
      next: (adress) => {
        this.userAdress = adress;
      },
      error: (err) => {
        this.userAdress = null;
        console.error('Erreur lors de la récupération des catégories', err);
        this.toastr.error('Erreur lors de l\'abonnement à l\'adresse utilisateur');
      },
    });

    this.userAdressService.loadUserAdress();
  }

  get categoryNames(): string[] {
    return this.categories.map((category) => category.title);
  }

  get categoryValues(): string[] {
    return this.categories.map((category) => category.id.toString());
  }

  onSubmit(form: any): void {
    if (!form.valid) {
      // Mark all fields as touched to show validation errors
      Object.keys(form.controls).forEach((field) => {
        const control = form.controls[field];
        control.markAsTouched({ onlySelf: true });
      });
      return;
    }

    const adRequest: AdUploadRequest = {
      title: form.value.title,
      description: form.value.description,
      type: "OFFER",
      categoryId: form.value.category,
      useCreatorAddress: true
    };

    this.adService.uploadAd(adRequest).subscribe({
      next: (_) => {
        this.errorMessage = null; // Réinitialiser les erreurs
        form.reset(); // Réinitialiser le formulaire
        this.toastr.success('Annonce enregistrée avec succès');
        this.router.navigate(['/my-ads']);
      },
      error: (err) => {
        this.errorMessage = errorMessageFromStatusCode(err.status) ?? 'Erreur inconnue.';
        console.error('Erreur', this.errorMessage);
        this.toastr.error(this.errorMessage, 'Erreur');
      },
    });
  }
}
