import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { ImageManagementService } from './image-management.service';
import { environment } from '../../environments/environment';
import { UserAdressService } from './user-adress.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient,
    private authService: AuthService,
    private imageService: ImageManagementService,
    private userAdressService: UserAdressService) {}

  login(loginRequest: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, loginRequest, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    }).pipe(
      tap((response: LoginResponse) => {
        localStorage.setItem('authToken', response.token);
        this.authService.setLoggedIn(true);
        this.imageService.getProfilePicture();
        this.userAdressService.loadUserAdress();

        this.userAdressService.userAdress$.subscribe({
          next: (adress) => {
            console.log('Adresse utilisateur récupérée après login :', adress);
          },
          error: (err) => {
            console.error('Erreur lors de la récupération de l\'adresse utilisateur', err);
          }
        });
      }),
      catchError(this.handleError)  // Ajout de la gestion des erreurs
    );
  }

  private handleError(error: any) {
    console.error('An error occurred:', error);
    return throwError('Something went wrong; please try again later.');
  }
}

export interface LoginRequest {
  username: string;
  password: string;
}

// Typage de la réponse pour mieux gérer les données
export interface LoginResponse {
  message: string;
  token: string;
}
