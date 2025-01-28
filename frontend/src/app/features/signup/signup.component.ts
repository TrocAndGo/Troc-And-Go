import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgForm, FormsModule } from '@angular/forms';
import { SignupService, SignupRequest } from '../../services/signup.service';
import { LoginComponent } from '../login/login.component';
import { errorMessageFromStatusCode } from '../../utils/ErrorMessage';

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
        this.errorMessage = errorMessageFromStatusCode(err.status);
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
