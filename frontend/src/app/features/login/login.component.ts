import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest, LoginService } from '../../services/login.service';
import { errorMessageFromStatusCode } from '../../utils/ErrorMessage';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  @Input() isVisible = false;
  @Output() closed = new EventEmitter<void>();
  isLoginPopupVisible = false;

  errorMessage: string | null = null;

  constructor(
    private LoginService: LoginService,
    private authService: AuthService
  ) {}

  close() {
    this.isVisible = false;
    this.closed.emit();
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

    const LoginRequest: LoginRequest = {
      username: form.value.username,
      password: form.value.password,
    };

    this.LoginService.login(LoginRequest).subscribe({
      next: (response) => {
        console.log('User logged in successfully:', response);

        const token = response.token; // Récupère le token de la réponse
        localStorage.setItem('authToken', token); // Sauvegarde le token dans le localStorage

        this.authService.setLoggedIn(true);

        this.errorMessage = null; // Réinitialiser les erreurs
        form.reset(); // Réinitialiser le formulaire
        this.close();
      },
      error: (err) => {
        this.errorMessage = errorMessageFromStatusCode(err.status);
      },
    });
  }

  isLoggedIn(): boolean {
    // Vérifier si l'utilisateur est connecté
    return this.authService.isLoggedIn();
  }

  openLoginPopup() {
    this.isLoginPopupVisible = true;
  }

  closeLoginPopup() {
    this.isLoginPopupVisible = false;
  }
}
