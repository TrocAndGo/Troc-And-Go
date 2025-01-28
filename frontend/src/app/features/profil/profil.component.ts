import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ImageManagementService } from '../../services/image-management.service';
import { ButtonComponent } from '../../shared/button/button.component';
import { ProfileService } from '../../services/profile.service';
import { GeocodingService } from '../../services/geocoding.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonComponent],
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.css']
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
  isAddressValid: boolean = false;
  isFormModified: boolean = false;
  isAddressModified: boolean = false; // Nouvelle variable pour savoir si l'adresse a été modifiée
  initialFullAddress: string = '';
  initialPhoneNumber: string = '';
  // États pour gérer les modifications
  isAddressValidated: boolean = false; // Indique si l'adresse est validée par l'API
  // Pour la gestion de l'image de profil
  selectedFileName: string | null = null;
  isPhotoValidated: boolean = false;

  constructor(
    private imageService: ImageManagementService,
    private profileService: ProfileService,
    private geocodingService: GeocodingService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Initialisation des valeurs initiales
    this.initialFullAddress = this.fullAddress;
    this.initialPhoneNumber = this.phoneNumber;
    // Charger l'image de profil
    this.imageService.getProfilePicture();
    // Récupérer l'URL de l'avatar depuis le service
    this.imageService.avatarUrl$.subscribe((url) => {
      this.avatarUrl = url;
    });
    // Récupérer le profil utilisateur depuis le backend
    this.profileService.getUserProfile().subscribe({
      next: (data) => {
        this.username = data.username || '';
        this.fullAddress = data.fullAddress || '';
        this.address = data.address || '';
        this.city = data.city || '';
        this.zipCode = data.zipCode || '';
        this.phoneNumber = data.phoneNumber || '';
        this.initialFullAddress = this.fullAddress;
        this.initialPhoneNumber = this.phoneNumber;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération du profil utilisateur:', err);
        this.toastr.error('Erreur lors de la récupération du profil utilisateur.')
      },
    });
  }

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
        // Valider automatiquement la photo après prévisualisation
        this.onSubmitImg();
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
            this.toastr.success('Votre photo de profil a été mise à jour avec succès !');
            this.isPhotoValidated = true; // La photo a été validée
          },
          error: (error) => {
            console.error('Failed to upload image:', error);
            this.toastr.error('Une erreur est survenue lors de la mise à jour de votre photo de profil.');
          },
        });
      } else {
        console.error('Token not found.');
        this.toastr.error('Vous n\'êtes pas authentifié.');
      }
    } else {
      console.error('No file selected.');
      this.toastr.error('Aucun fichier sélectionné.');
    }
  }

  // Validation de l'adresse via Géoportail
  validateAddress(): void {
    if (!this.fullAddress) {
      this.toastr.error('Veuillez saisir une adresse complète.');
      return;
    }
    // Appel à l'API de géocodage pour valider l'adresse
    this.geocodingService.searchAddress(this.fullAddress).subscribe({
      next: (response) => {
        if (response.features && response.features.length > 0) {
          const feature = response.features[0];
          // Extraire les champs pour affichage
          this.address = feature.properties.name || '';
          this.city = feature.properties.city || '';
          this.zipCode = feature.properties.postcode || '';
          this.isAddressModified = true; // Marque l'adresse comme modifiée
          this.isAddressValidated = true; // Adresse validée
          this.updateFormState(); // Vérifie si le formulaire doit être activé
          this.toastr.success('Adresse validée avec succès.');
          this.onProfileChange(); // Mise à jour de l'état du formulaire
        } else {
          this.toastr.error("L'adresse n'a pas pu être trouvée.");
          this.isAddressValidated = false; // Échec de validation
          this.updateFormState();
        }
      },
      error: (err) => {
        console.error('Erreur lors de la validation de l’adresse :', err);
        this.toastr.error('Une erreur est survenue lors de la validation de l’adresse.');
      },
    });
  }

  // Fonction qui suit les modifications du formulaire
  onProfileChange(): void {
    // Appelé lorsque le téléphone est modifié
    const phoneModified = this.phoneNumber !== this.initialPhoneNumber;
    const addressModified = this.isAddressValidated; // Adresse validée après appel à l'API
    // Mise à jour de l'état du formulaire
    this.isFormModified = phoneModified || addressModified;
  }

  updateFormState(): void {
    // Met à jour l'état du formulaire après validation de l'adresse ou modification du téléphone
    const phoneModified = this.phoneNumber !== this.initialPhoneNumber;
    const addressModified = this.isAddressValidated; // Adresse validée par Géoportail
    this.isFormModified = phoneModified || addressModified;
  }

  // Envoi des données modifiées au backend
  onSubmit(form: any): void {
    // Initialisation de l'objet de données à envoyer
    const profileData: any = {};
    // Vérification et ajout du numéro de téléphone si modifié
    if (this.phoneNumber !== this.initialPhoneNumber) {
      console.log('je suis ici.');
      profileData.phoneNumber = this.phoneNumber;
    }
    // Vérification et ajout de l'adresse si modifiée et validée
    if (this.isAddressModified && this.fullAddress !== this.initialFullAddress) {
      profileData.address = this.address;
      profileData.city = this.city;
      profileData.zipCode = this.zipCode;
      profileData.latitude = this.latitude;
      profileData.longitude = this.longitude;
    }
    // Vérifier s'il y a des modifications
    if (Object.keys(profileData).length === 0) {
      console.log('Aucune modification à envoyer.');
      return;
    }
    this.isSubmitting = true; // Désactiver temporairement le bouton pendant l'envoi
    // Envoi les données au backend
    this.profileService.updateUserProfileWithGeocode(profileData).subscribe({
      next: () => {
        this.toastr.success('Profil mis à jour avec succès !');
        // Réinitialiser l'état du formulaire après un envoi réussi
        this.initialPhoneNumber = this.phoneNumber;
        this.initialFullAddress = this.fullAddress;
        this.isAddressValidated = false; // Réinitialiser la validation d'adresse
        this.isFormModified = false; // Griser le bouton
        this.isSubmitting = false; // Réactiver le formulaire si besoin
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour du profil :', err);
        this.toastr.error('Une erreur est survenue lors de la mise à jour.');
        this.isSubmitting = false;
      },
    });
  }
}
