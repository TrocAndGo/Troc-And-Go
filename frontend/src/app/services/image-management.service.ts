import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImageManagementService {
  private apiUrl = 'http://localhost:8080/api/v1/user/';

  constructor(private http: HttpClient) {}

  uploadImage(file: File, authToken: string): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);

    // Ajout des headers avec le token d'authentification
    const headers = new HttpHeaders({
      Authorization: `Bearer ${authToken}`
    });

    return this.http.post(this.apiUrl + 'upload', formData, { headers });
  }

  getProfilePicture(): Observable<Blob> {
    const token = localStorage.getItem('authToken'); // Récupérer le token d'authentification
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`); // Ajouter le token dans les en-têtes

      console.log('URL de l\'API:', this.apiUrl + 'profile-picture');

      return this.http.get(this.apiUrl + 'profile-picture', { headers, responseType: 'blob' });
    } else {
      throw new Error('Token d\'authentification non trouvé');
    }
  }
}
