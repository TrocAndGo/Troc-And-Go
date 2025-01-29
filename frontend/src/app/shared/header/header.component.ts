import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { LoginComponent } from '../../features/login/login.component';
import { SignupComponent } from '../../features/signup/signup.component';
import { AuthService } from '../../services/auth.service';
import { ImageManagementService } from '../../services/image-management.service';
import { ButtonComponent } from '../button/button.component';
import { UserAdressService } from '../../services/user-adress.service';
import { ProfileService } from '../../services/profile.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    ButtonComponent,
    SignupComponent,
    LoginComponent,
    CommonModule,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {

  selectedFile: File | null = null;
  imagePreview: string | null = null;
  avatarUrl: string | null = null;
  isLoggedIn = false;
  showDropdown = false;
  userAdress: string | null = null;

  constructor(public authService: AuthService,
    private router: Router,
    private imageService: ImageManagementService,
    private userAdressService: UserAdressService,
    private profileService: ProfileService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    // S'abonner à l'état de connexion
    this.authService.loggedIn$.subscribe((status) => {
      this.isLoggedIn = status;

      if (status) {
        // Charger l'adresse utilisateur après connexion
        this.userAdressService.loadUserAdress();
      }
    });

    // S'abonner aux changements d'adresse utilisateur
    this.userAdressService.userAdress$.subscribe((adress) => {
      console.log('Adresse utilisateur mise à jour :', adress);
      this.userAdress = adress;
    });

    this.imageService.avatarUrl$.subscribe((url) => {
      this.avatarUrl = url;
    });
    this.imageService.getProfilePicture(); // Charger l'avatar au démarrage
    this.listenToAdressChanges();
  }

  @Output() clicked = new EventEmitter<void>();
  isPopupVisible = false;
  isLoginPopupVisible = false;

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }

  onClick() {
    this.clicked.emit();
  }

  handleClick() {
    alert('Button clicked!');
  }

  openPopup() {
    this.isPopupVisible = true;
  }

  closePopup() {
    this.isPopupVisible = false;
  }

  openLoginPopup() {
    this.isLoginPopupVisible = true;
  }

  closeLoginPopup() {
    this.isLoginPopupVisible = false;
  }

  onSignupSuccess() {
    this.closePopup(); // Ferme la popup d'inscription
    this.openLoginPopup(); // Ouvre la popup de connexion
  }

  openCreateAd() {
    console.log("adresse: ", this.userAdress)
    if (!this.userAdress || this.userAdress === null) {
      console.log("dans la condition")
      this.router.navigate(['/profil'])
      this.toastr.warning("Vous devez renseigner votre adresse avant de pouvoir poster une annonce");
    }
    else {
      this.router.navigate(['/annonce'])
    }
  }

  logout() {
    this.authService.logout();  // Appel de la fonction de déconnexion
    this.router.navigate(['/']);  // Redirection vers la page de connexion
  }

  listenToAdressChanges() {
    this.profileService.userAdress$.subscribe((adress) => {
      console.log('Adresse utilisateur mise à jour :', adress);
      this.userAdress = adress;
    });

  }
}
