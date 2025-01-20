import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ImageManagementService } from './image-management.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  public loggedIn$ = this.loggedInSubject.asObservable();

  constructor(private imageService: ImageManagementService) {}

  // Met à jour l'état de connexion
  setLoggedIn(isLoggedIn: boolean): void {
    this.loggedInSubject.next(isLoggedIn);
  }

  // Vérifie si l'utilisateur est connecté
  isLoggedIn(): boolean {
    return this.loggedInSubject.getValue();
  }

  logout(): void {
    // Réinitialisation de l'état de connexion
    this.setLoggedIn(false); // Met à jour l'état de connexion
    localStorage.removeItem('authToken'); // Supprime le token ou autre méthode
    this.imageService.resetProfilePicture();
  }

}
