import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AdService {

  private apiUrl = 'http://localhost:8080/api/v1/';

      constructor(private http: HttpClient) {}

      uploadAd(adRequest: AdRequest): Observable<any> {
        return this.http.post(`${this.apiUrl}/ad`, adRequest, {
          headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        });
    }
  }

  export interface AdRequest {
    type: string;
    categorie: string;
    titre: string;
    description: string;
  }

