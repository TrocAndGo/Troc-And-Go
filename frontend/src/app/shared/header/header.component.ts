import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonComponent } from '../button/button.component';
import { SignupComponent } from '../../features/signup/signup.component';
import { LoginComponent } from '../../features/login/login.component';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ImageManagementService } from '../../services/image-management.service';

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

  constructor(public authService: AuthService, private router: Router, private imageService: ImageManagementService) {}

  ngOnInit() {
    // S'abonner à l'état de connexion
    this.authService.loggedIn$.subscribe((status) => {
      this.isLoggedIn = status;
    });
    this.imageService.avatarUrl$.subscribe((url) => {
      this.avatarUrl = url;
    });
    this.imageService.getProfilePicture(); // Charger l'avatar au démarrage
  }

  @Output() clicked = new EventEmitter<void>();
  isPopupVisible = false;
  isLoginPopupVisible = false;

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
    this.router.navigate(['/annonce'])
  }

  logout() {
    this.authService.logout();  // Appel de la fonction de déconnexion
    this.router.navigate(['/']);  // Redirection vers la page de connexion
  }
}
