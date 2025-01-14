import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgForm, FormsModule } from '@angular/forms';
import { ImageManagementService } from '../../services/image-management.service';
import { ButtonComponent } from '../../shared/button/button.component';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonComponent ],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class ProfilComponent implements OnInit{
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  avatarUrl: string | null = null;

  constructor(private imageManagementService: ImageManagementService) {}

  ngOnInit(): void {
    this.loadAvatar();
  }

  loadAvatar(): void {
    this.imageManagementService.getProfilePicture().subscribe({
      next: (blob) => {
        // Convertir le Blob en URL d'objet
        this.avatarUrl = URL.createObjectURL(blob);
      },
      error: (err) => {
        console.error('Erreur lors du chargement de l\'avatar:', err);
        if (err.status) {
          console.log(`Statut HTTP: ${err.status}`);
        }
        if (err.message) {
          console.log(`Message d'erreur: ${err.message}`);
        }
        this.avatarUrl = 'icone.jpg'; // Avatar par défaut en cas d'erreur
      },

    });
  }

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.selectedFile = file;

      // Prévisualisation de l'image
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmitImg(): void {
    if (this.selectedFile) {
      const token = localStorage.getItem('authToken');
      if (token) {
        this.imageManagementService.uploadImage(this.selectedFile, token).subscribe({
          next: (response) => {
            console.log('Image uploaded successfully:', response);
            localStorage.setItem('userAvatarUrl', response.avatarUrl);
          },
          error: (error) => {
            console.error('Failed to upload image:', error);
            console.log("token in function: ", token)
          },
        });
      } else {
        console.error('Aucun token d\'authentification trouvé dans localStorage.');
      }
    } else {
      console.error('Aucun fichier sélectionné.');
    }
  }


}
