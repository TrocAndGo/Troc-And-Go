import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { GeocodingService } from '../../services/geocoding.service';
import { ImageManagementService } from '../../services/image-management.service';
import { ProfileService } from '../../services/profile.service';
import { ButtonComponent } from '../../shared/button/button.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonComponent],
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.css']
})
export class ProfilComponent implements OnInit, OnDestroy {
  // Variables pour l'affichage du template
  imagePreview: string | null = null;
  avatarUrl: string | null = null;
  username: string = '';
  fullAddress: string = '';
  address: string = '';
  city: string = '';
  zipCode: string = '';
  phoneNumber: string = '';

  // Variables supplémentaires pour stocker les données de géocodage
  region: string = '';
  department: string = '';
  latitude: number | null = null;
  longitude: number | null = null;

  // Variables pour la gestion des fichiers
  selectedFile: File | null = null;
  selectedFileName: string | null = null;
  isPhotoValidated: boolean = false;

  // Variables pour la validation du formulaire
  phonePattern = '^(0[1-7])[0-9]{8}$';
  isSubmitting = false;
  isFormModified: boolean = false;
  isAddressModified: boolean = false;
  initialFullAddress: string = '';
  initialPhoneNumber: string = '';
  isAddressValidated: boolean = false;

  // Variables pour la modal du mot de passe
  isPasswordModalOpen: boolean = false;
  currentPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessageModal: string = '';

  private subscriptions: Subscription = new Subscription();

  constructor(
    private imageService: ImageManagementService,
    private profileService: ProfileService,
    private geocodingService: GeocodingService,
    private toastr: ToastrService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Initialisation des valeurs initiales
    this.initialFullAddress = this.fullAddress;
    this.initialPhoneNumber = this.phoneNumber;

    // Abonnement pour récupérer l'avatar de l'utilisateur
    this.subscriptions.add(
      this.imageService.avatarUrl$.subscribe((url) => {
        this.avatarUrl = url;
      })
    );

    // Récupérer le profil utilisateur depuis le backend
    this.subscriptions.add(
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
      })
    );
  }

  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      this.selectedFile = file; // Stocker le fichier sélectionné
      this.selectedFileName = file.name; // Stocker le nom du fichier
      this.isPhotoValidated = false; // Réinitialise l'état de validation

      // Vérificationde l'image avant traitement
      const allowedTypes = ['image/jpeg', 'image/png'];
      if (!allowedTypes.includes(file.type)) {
        this.toastr.error('Veuillez sélectionner un fichier image au format JPG, JPEG ou PNG.');
        return;
      }
      if (file.size > 2 * 1024 * 1024) { // 2 Mo
        this.toastr.error('Le fichier est trop volumineux (max 2 Mo).');
        return;
      }
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
      this.subscriptions.add(
        this.imageService.uploadImage(this.selectedFile).subscribe({
          next: (response) => {
            console.log('Image uploaded successfully:', response);
            this.toastr.success('Votre photo de profil a été mise à jour avec succès !');
            this.isPhotoValidated = true;
          },
          error: (error) => {
            console.error('Failed to upload image:', error);
            this.toastr.error('Une erreur est survenue lors de la mise à jour de votre photo de profil.');
          },
        })
      );
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

          // Stocker les données supplémentaires pour le backend
          this.region = feature.properties.context.split(', ')[2] || '';
          this.department = feature.properties.context.split(', ')[1] || '';
          [this.longitude, this.latitude] = feature.geometry.coordinates;

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
      profileData.phoneNumber = this.phoneNumber;
    }
    // Vérification et ajout de l'adresse si modifiée et validée
    if (this.isAddressModified && this.fullAddress !== this.initialFullAddress) {
      profileData.address = this.address;
      profileData.city = this.city;
      profileData.zipCode = this.zipCode;
      profileData.region = this.region;
      profileData.department = this.department;
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
    this.subscriptions.add(
      this.profileService.updateUserProfileWithGeocode(profileData).subscribe({
        next: () => {
          this.toastr.success('Profil mis à jour avec succès !');
          this.initialPhoneNumber = this.phoneNumber;
          this.initialFullAddress = this.fullAddress;
          this.isAddressValidated = false;
          this.isFormModified = false;
          this.isSubmitting = false;
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Erreur lors de la mise à jour du profil :', err);
          this.toastr.error('Une erreur est survenue lors de la mise à jour.');
          this.isSubmitting = false;
        },
      })
    );
  }

  openPasswordModal() {
    this.isPasswordModalOpen = true;
    this.errorMessageModal = '';
  }

  closePasswordModal() {
    this.isPasswordModalOpen = false;
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.errorMessageModal = '';
  }

  changePassword() {
    // Vérifier si tous les champs sont remplis
    if (!this.currentPassword || !this.newPassword || !this.confirmPassword) {
      this.errorMessageModal = "Tous les champs doivent être remplis !";
      return;
    }

    // Vérifier si le nouveau mot de passe respecte un format sécurisé
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(this.newPassword)) {
      this.errorMessageModal = "Le nouveau mot de passe doit contenir au moins 8 caractères, une majuscule et un chiffre.";
      return;
    }

    // Vérifier si le nouveau mot de passe et la confirmation sont identiques
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessageModal = "Les nouveaux mots de passe ne correspondent pas !";
      return;
    }

    // Envoi au backend
    this.subscriptions.add(
      this.profileService.updatePassword(this.currentPassword, this.newPassword).subscribe({
        next: () => {
          this.closePasswordModal();
          this.toastr.success("Mot de passe modifié avec succès !");
        },
        error: (err) => {
          switch (err.error.message) {
            case "Current password is incorrect.":
              this.errorMessageModal = "Mot de passe actuel incorrect.";
              break;
            case null:
              this.errorMessageModal = "Erreur lors du changement de mot de passe.";
              break;
            default:
              this.errorMessageModal = err.error.message;
          }
        }
      })
    );
  }

  ngOnDestroy(): void {
    // Si le composant est détruit, on se désabonne des abonnements pour éviter les fuites de mémoire
    this.subscriptions.unsubscribe();
  }
}
