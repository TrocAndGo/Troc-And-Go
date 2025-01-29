import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import PageableResponse from '../utils/PageableResponse';
import { SearchResult } from './search.service';

@Injectable({
  providedIn: 'root'
})
export class AdService {

  private apiUrl = environment.apiUrl;;

  constructor(private http: HttpClient) { }

  uploadAd(adRequest: AdUploadRequest): Observable<any> {
    const token = localStorage.getItem('authToken');
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.post(`${this.apiUrl}/services`, adRequest, {
        headers: headers,  // Passe les headers dans la requête
      });
    }
    return throwError(() => new Error('Token is missing'));  // Retourne une erreur si le token est absent
  }

  getServices(): Observable<any> {
    const token = localStorage.getItem('authToken');
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

      return this.http.get(`${this.apiUrl}/services`, {
        headers: headers,
      });
    }
    return throwError(() => new Error('Token is missing'));  // Retourne une erreur si le token est absent
  }

  getMyServices(page: number = 0): Observable<PageableResponse<SearchResult>> {
    const token = localStorage.getItem('authToken');
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

      return this.http.get<PageableResponse<SearchResult>>(`${this.apiUrl}/user/me/services`, {
        headers: headers,
        params: {
          page: page,
          size: 6
        }
      });
    }
    return throwError(() => new Error('Token is missing'));  // Retourne une erreur si le token est absent
  }

  getCategories(): Observable<any> {
    return this.http.get(`${this.apiUrl}/services/categories`);
  }

  getAdressFilters(): Observable<any> {
    return this.http.get(`${this.apiUrl}/services/adresses`);
  }

  deleteService(serviceId: string) {
    const token = localStorage.getItem('authToken');
    if (!token) {
      return throwError(() => new Error('Token is missing'));
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const url = `${this.apiUrl}/services/${serviceId}`;

    return this.http.delete(url, { headers }).pipe(
      catchError((error) => {
        console.error('Error deleting service:', error);
        return throwError(() => new Error('Failed to delete service. Please try again.'));
      })
    );
  }
}

export interface AdUploadRequest {
  title: string;
  description: string;
  type: string;
  categoryId: string;
  useCreatorAddress: boolean;
}

export type AdCategory = {
  id: number;
  title: string
};

export type AdressFilters = {
  regions: string[];
  departments: string[];
  cities: string[];
}
