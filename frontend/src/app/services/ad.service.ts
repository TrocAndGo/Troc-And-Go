import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdService {

  private apiUrl = environment.apiUrl;;

      constructor(private http: HttpClient) {}

      uploadAd(adRequest: AdRequest): Observable<any> {
        const token = localStorage.getItem('authToken');
        if (token) {
          const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

          return this.http.post(`${this.apiUrl}/services`, adRequest, {
            headers: headers,  // Passe les headers dans la requête
          });
        }
        return throwError('Token is missing');  // Retourne une erreur si le token est absent
      }


      getServices(): Observable<any> {
        const token = localStorage.getItem('authToken');
        if (token) {
          const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

          return this.http.get(`${this.apiUrl}`, {
            headers: headers,
          });
        }
        return throwError('Token is missing');  // Retourne une erreur si le token est absent
      }
    }

  export interface AdRequest {
    type: string;
    categorie: string;
    titre: string;
    description: string;
    address: string;
  }

