import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgForm, FormsModule } from '@angular/forms';

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

  passwordsMatch = true;

  close() {
    this.isVisible = false;
    this.closed.emit();
  }

  onSubmit(form: NgForm) {
    if (!this.passwordsMatch) {
      console.error('Passwords do not match!');
      return;
    }
    console.log('Form Submitted', form.value);
    // Effectuer la logique de connexion ici
  }

  onPasswordChange(password: string, confirmPassword: string) {
    this.passwordsMatch = password === confirmPassword;
  }
}
