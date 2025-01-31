import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ImageManagementService } from './image-management.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  public loggedIn$ = this.loggedInSubject.asObservable();

  constructor(private imageService: ImageManagementService, private http: HttpClient) {
    if (this.isTokenExpired() == false) {
      this.setLoggedIn(true);
    }
  }

  // Met à jour l'état de connexion
  setLoggedIn(isLoggedIn: boolean): void {
    this.loggedInSubject.next(isLoggedIn);
  }

  // Vérifie si l'utilisateur est connecté
  isLoggedIn(): boolean {
    return this.loggedInSubject.getValue();
  }

  logout(): void {
    this.http.post(`/auth/logout`, null).subscribe(() => {
      console.log('Logged out');
    });

    // Réinitialisation de l'état de connexion
    this.setLoggedIn(false); // Met à jour l'état de connexion
    localStorage.removeItem('authToken'); // Supprime le token ou autre méthode
    this.imageService.resetProfilePicture();
  }

  private isTokenExpired(): boolean {
    const authToken = localStorage.getItem('authToken');
    if (!authToken) {
      return true;
    }

    const tokenPayload = JSON.parse(atob(authToken.split('.')[1]));
    const expiryTime = tokenPayload.exp * 1000;
    return Date.now() > expiryTime;
  }
}
