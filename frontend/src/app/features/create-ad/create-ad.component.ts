import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DropdownButtonComponent } from '../../shared/dropdown-button/dropdown-button.component';
import { MenusRequestsService } from '../../services/menus-requests.service';
import { AdService, AdRequest } from '../../services/ad.service';

@Component({
  selector: 'app-create-ad',
  standalone: true,
  imports: [DropdownButtonComponent, CommonModule, FormsModule],
  templateUrl: './create-ad.component.html',
  styleUrl: './create-ad.component.css'
})
export class CreateAdComponent implements OnInit{

  types: string[] = [];
  categories: string[] = [];
  errorMessage: string | null = null;

  constructor(private menusrequestsservice: MenusRequestsService, private adservice: AdService) {}

  ngOnInit(): void {
      this.loadTypes();
      this.loadCategories();
  }

      loadTypes(): void {
        this.menusrequestsservice.getTypes().subscribe({
          next: (data) => (this.types = data),
          error: (err) => console.error('Erreur lors de la récupération des types', err),
        });
      }

      loadCategories(): void {
        this.menusrequestsservice.getCategories().subscribe({
          next: (data) => (this.categories = data),
          error: (err) => console.error('Erreur lors de la récupération des catégories', err),
        });
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

            const adRequest: AdRequest = {
              type: form.value.type,
              categorie: form.value.categorie,
              titre: form.value.titre,
              description: form.value.description,
              address: form.value.address,

            };

            this.adservice.uploadAd(adRequest).subscribe({
              next: (response) => {
                console.log('Ad registered successfully:', response);
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
