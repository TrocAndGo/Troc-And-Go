import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private apiUrl = 'http://localhost:8080/api/v1/user';

  constructor(private http: HttpClient) {}

  addFavorite(serviceId: string): Observable<any> {
    const token = localStorage.getItem('authToken');
    if (!token) {
      return throwError(() => new Error('Token is missing'));
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const url = `${this.apiUrl}/favorites/${serviceId}`;

    return this.http.post(url, null, { headers }).pipe(
      catchError((error) => {
        console.error('Error adding favorite:', error);
        return throwError(() => new Error('Failed to add favorite. Please try again.'));
      })
    );
  }

  removeFavorite(serviceId: string): Observable<any> {
    const token = localStorage.getItem('authToken');
    if (!token) {
      return throwError(() => new Error('Token is missing'));
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const url = `${this.apiUrl}/favorites/${serviceId}`;

    return this.http.delete(url, { headers }).pipe(
      catchError((error) => {
        console.error('Error removing favorite:', error);
        return throwError(() => new Error('Failed to remove favorite. Please try again.'));
      })
    );
  }
}

