import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImageManagementService } from '../../services/image-management.service';
import { ButtonComponent } from '../../shared/button/button.component';
import { ProfileService, ProfileRequest } from '../../services/profile.service';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonComponent ],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class ProfilComponent implements OnInit {
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  avatarUrl: string | null = null;
  username: string = '';
  address: string = '';
  phone_number: string = '';
  phonePattern = '^\\+?[0-9]{1,3}?[-.\\s]?\\(?(?:[0-9]{1,4})\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}$';
  password: string = '';
  email: string = '';
  passwordsMatch: boolean = true;
  errorMessage: string | null = null;

  constructor(private imageService: ImageManagementService, private profileService: ProfileService) {}

  ngOnInit(): void {
    // Récupérer le profil utilisateur depuis le backend
    this.profileService.getUserProfile().subscribe({
      next: (data) => {
        this.username = data.name || '';
        this.address = data.address || '';
        this.phone_number = data.phone_number || '';
        this.password = data.password || '';
        this.email = data.email || '';
      },
      error: (err) => {
        console.error('Erreur lors de la récupération du profil utilisateur:', err);
      },
    });
  }

  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      this.selectedFile = file;

      // Prévisualisation de l'image
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    } else {
      this.selectedFile = null;
      this.imagePreview = null; // Réinitialiser si aucun fichier sélectionné
    }
  }

  onSubmitImg(): void {
    if (this.selectedFile) {
      const token = localStorage.getItem('authToken');
      if (token) {
        this.imageService.uploadImage(this.selectedFile, token).subscribe({
          next: (response) => {
            console.log('Image uploaded successfully:', response);
          },
          error: (error) => {
            console.error('Failed to upload image:', error);
          },
        });
      } else {
        console.error('Token not found.');
      }
    } else {
      console.error('No file selected.');
    }
  }

  onPasswordChange(password: string, confirmPassword: string): void {
    this.passwordsMatch = password === confirmPassword;
  }

  onSubmit(form: any): void {
      if (!form.valid || !this.passwordsMatch) {
        // Mark all fields as touched to show validation errors
        Object.keys(form.controls).forEach((field) => {
          const control = form.controls[field];
          control.markAsTouched({ onlySelf: true });
        });
        return;
      }

      const profileRequest: ProfileRequest = {
        username: form.value.username,
        email: form.value.email,
        password: form.value.password,
        roles: ["ROLE_USER"],
        address: form.value.address,
        phone_number: form.value.phone_number,
      };

      this.profileService.updateUserProfile(profileRequest).subscribe({
        next: (response) => {
          console.log('User registered successfully:', response);
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
