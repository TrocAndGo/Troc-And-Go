import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImageManagementService } from '../../services/image-management.service';
import { ButtonComponent } from '../../shared/button/button.component';
import { ProfileService, ProfileRequest } from '../../services/profile.service';
import { GeocodingService } from '../../services/geocoding.service';

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
  fullAddress: string = '';
  address: string = '';
  city: string = '';
  zipCode: string = '';
  region: string = '';
  department: string = '';
  latitude: number | null = null;
  longitude: number | null = null;
  phoneNumber: string = '';
  phonePattern = '^\\+?[0-9]{1,3}?[-.\\s]?\\(?(?:[0-9]{1,4})\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}$';
  password: string = '';
  email: string = '';
  passwordsMatch: boolean = true;
  errorMessage: string | null = null;
  isSubmitting = false;
  addressPlaceholder: string = 'Entrez une adresse';
  cityPlaceholder: string = 'Entrez une ville';

  constructor(
    private imageService: ImageManagementService,
    private profileService: ProfileService,
    private geocodingService: GeocodingService
  ) {}

  ngOnInit(): void {
    // Charger l'image de profil au démarrage
    this.imageService.getProfilePicture();

    // Récupérer l'URL de l'avatar depuis le service
    this.imageService.avatarUrl$.subscribe((url) => {
      this.avatarUrl = url;
    });

    // Récupérer le profil utilisateur depuis le backend
    this.profileService.getUserProfile().subscribe({
      next: (data) => {
        console.log('Données récupérées depuis le backend :', data);

        this.username = data.username || '';
        this.address = data.address || '';
        this.city = data.city || '';
        this.zipCode = data.zipCode || '';
        this.phoneNumber = data.phoneNumber || '';
        //this.email = data.email || '';
        // Mettre à jour les placeholders avec les valeurs récupérées

      },
      error: (err) => {
        console.error('Erreur lors de la récupération du profil utilisateur:', err);
      },
    });
  }

selectedFileName: string | null = null;
isPhotoValidated: boolean = false;

onFileSelected(event: Event): void {
  const fileInput = event.target as HTMLInputElement;
  if (fileInput.files && fileInput.files.length > 0) {
    const file = fileInput.files[0];
    this.selectedFile = file; // Stocker le fichier sélectionné
    this.selectedFileName = file.name; // Stocker le nom du fichier
    this.isPhotoValidated = false; // Réinitialise l'état de validation

    // Prévisualisation de l'image
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  } else {
    // Réinitialiser si aucun fichier sélectionné
    this.selectedFile = null;
    this.selectedFileName = null;
    this.imagePreview = null; // Réinitialise si aucun fichier sélectionné
    this.isPhotoValidated = false; // Réinitialise l'état de validation
  }
}

onSubmitImg(): void {
  if (this.selectedFile) {
    const token = localStorage.getItem('authToken');
    if (token) {
      this.imageService.uploadImage(this.selectedFile, token).subscribe({
        next: (response) => {
          console.log('Image uploaded successfully:', response);
          alert('Votre photo de profil a été mise à jour avec succès !');
          this.isPhotoValidated = true; // La photo a été validée
          // Vous pouvez réinitialiser selectedFile ici si nécessaire :
          // this.selectedFile = null;
        },
        error: (error) => {
          console.error('Failed to upload image:', error);
          alert('Une erreur est survenue lors de la mise à jour de votre photo de profil.');
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

  validateAddress(): void {
    if (!this.fullAddress) {
      alert('Veuillez saisir une adresse complète.');
      return;
    }

    this.geocodingService.searchAddress(this.fullAddress).subscribe({
      next: (response) => {
        if (response.features && response.features.length > 0) {
          const feature = response.features[0];

          // Extraire les champs pour affichage
          this.address = feature.properties.name || '';
          this.city = feature.properties.city || '';
          this.zipCode = feature.properties.postcode || '';

          // Stocker les données supplémentaires pour le backend
          this.region = feature.properties.context.split(', ')[2] || '';
          this.department = feature.properties.context.split(', ')[1] || '';
          [this.longitude, this.latitude] = feature.geometry.coordinates;

          alert('Adresse validée avec succès.');
        } else {
          alert("L'adresse n'a pas pu être trouvée.");
        }
      },
      error: (err) => {
        console.error('Erreur lors de la validation de l’adresse :', err);
        alert('Une erreur est survenue lors de la validation de l’adresse.');
      },
    });
  }

  onSubmit(form: any): void {
    if (form.invalid) return;

    // Vérification et conversion des coordonnées
    if (this.latitude === null || this.longitude === null) {
      alert("Les coordonnées sont manquantes. Veuillez valider l'adresse à nouveau.");
      return;
    }

    const profileData = {
      phoneNumber: this.phoneNumber,
      address: this.address,
      city: this.city,
      zipCode: this.zipCode,
      region: this.region,
      department: this.department,
      latitude: this.latitude,
      longitude: this.longitude,
    };

    this.profileService.updateUserProfileWithGeocode(profileData).subscribe({
      next: () => {
        alert('Profil mis à jour avec succès !');
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour du profil :', err);
        alert('Une erreur est survenue lors de la mise à jour.');
      },
    });
  }

}
