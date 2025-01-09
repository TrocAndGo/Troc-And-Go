import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgForm, FormsModule } from '@angular/forms';
import { SignupService, SignupRequest } from '../../services/signup.service';
import { LoginComponent } from '../login/login.component';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, LoginComponent],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {
  @Input() isVisible = false;
  @Output() closed = new EventEmitter<void>();
  @Output() signupSuccess = new EventEmitter<void>();
  isLoginPopupVisible = false;

  errorMessage: string | null = null;
  passwordsMatch: boolean = true;

  constructor(private SignupService: SignupService) {}

  close() {
    this.isVisible = false;
    this.closed.emit();
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

    const signupRequest: SignupRequest = {
      username: form.value.username,
      email: form.value.email,
      password: form.value.password,
      roles: ["ROLE_USER"]
    };

    this.SignupService.signup(signupRequest).subscribe({
      next: (response) => {
        console.log('User registered successfully:', response);
        this.errorMessage = null; // Réinitialiser les erreurs
        form.reset(); // Réinitialiser le formulaire
        this.signupSuccess.emit();

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

  onPasswordChange(password: string, confirmPassword: string): void {
    this.passwordsMatch = password === confirmPassword;
  }

  openLoginPopup() {
    this.isLoginPopupVisible = true;
  }

  closeLoginPopup() {
    this.isLoginPopupVisible = false;
  }
}
