import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonComponent } from '../button/button.component';
import { SignupComponent } from '../../features/signup/signup.component';
import { LoginComponent } from '../../features/login/login.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    ButtonComponent,
    SignupComponent,
    LoginComponent
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
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
}
