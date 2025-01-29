import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import PageableResponse from '../utils/PageableResponse';
import { SearchResult } from './search.service';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}


  getFavorites(page: number = 0): Observable<PageableResponse<SearchResult>> {
    const token = localStorage.getItem('authToken');
    if (!token) {
      return throwError(() => new Error('Token is missing'));
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const url = `${this.apiUrl}/user/me/favorites`;

    return this.http.get<PageableResponse<SearchResult>>(url, {
      headers,
      params: {
        size: 6,
        page: page
      }
    }).pipe(
      catchError((error) => {
        console.error('Error getting favorites:', error);
        return throwError(() => new Error('Failed to get favorites. Please try again.'));
      })
    );
  }

  addFavorite(serviceId: string): Observable<any> {
    const token = localStorage.getItem('authToken');
    if (!token) {
      return throwError(() => new Error('Token is missing'));
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const url = `${this.apiUrl}/user/favorites/${serviceId}`;

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
    const url = `${this.apiUrl}/user/favorites/${serviceId}`;

    return this.http.delete(url, { headers }).pipe(
      catchError((error) => {
        console.error('Error removing favorite:', error);
        return throwError(() => new Error('Failed to remove favorite. Please try again.'));
      })
    );
  }
}

