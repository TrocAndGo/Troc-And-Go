import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient, private authService: AuthService) {}

  login(loginRequest: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, loginRequest, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    }).pipe(
      tap(() => {
        // Met à jour l'état de connexion après un login réussi
        this.authService.setLoggedIn(true);
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
