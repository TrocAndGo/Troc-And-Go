import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MenusRequestsService {

  private baseUrl = 'http://localhost:8080/api/v1/user/'; // URL de base de votre API

  constructor(private http: HttpClient) {}

  // Récupérer la liste des catégories
  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/categories`);
  }

}
