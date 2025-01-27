import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdCategory, AdService, AdUploadRequest } from '../../services/ad.service';
import { UserAdressService } from '../../services/user-adress.service';
import { DropdownButtonComponent } from '../../shared/dropdown-button/dropdown-button.component';

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

  constructor(private adService: AdService,  private userAdressService: UserAdressService ) { }

  ngOnInit(): void {
    this.loadCategories();
    this.subscribeToUserAdress();
  }

  loadCategories(): void {
    this.adService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => console.error('Erreur lors de la récupération des catégories', err),
    });
  }

  subscribeToUserAdress(): void {
    this.userAdressService.userAdress$.subscribe({
      next: (adress) => {
        this.userAdress = adress;
      },
      error: (err) => {
        console.error('Erreur lors de l\'abonnement à l\'adresse utilisateur', err);
        this.userAdress = null;
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
    console.log('Ad request:', adRequest);

    this.adService.uploadAd(adRequest).subscribe({
      next: (response) => {
        console.log('Ad registered successfully:', response);
        alert('Annonce enregistrée avec succès');
        this.errorMessage = null; // Réinitialiser les erreurs
        form.reset(); // Réinitialiser le formulaire
      },
      error: (err) => {
        console.error('Error registering user:');
        console.error('Status:', err.status);
        console.error('Status Text:', err.statusText);
        console.error('Error message:', err.message);
        console.error('Detailed error:', err.error);

        // Affichage d'un message d'erreur plus précis
        if (err.status === 0) {
          // Cas où il y a une erreur de connexion ou si le backend n'est pas accessible
          this.errorMessage = 'Problème de connexion au serveur';
        } else if (err.status === 400) {
          // Cas d'une mauvaise requête (par exemple, données invalides)
          this.errorMessage = 'Données non valides';
        } else if (err.status === 404) {
          // Cas d'une ressource introuvable
          this.errorMessage = 'Ressource introuvable';
        } else if (err.status === 500) {
          // Cas d'une erreur serveur interne
          this.errorMessage = 'Problème de serveur, merci de rééssayer plus tard';
        } else {
          // Cas général d'erreur
          this.errorMessage = err.error || "Erreur d'enregistrement";
        }
      },
    });
  }
}
