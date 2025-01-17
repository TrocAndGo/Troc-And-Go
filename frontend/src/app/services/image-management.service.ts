import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImageManagementService {
  private apiUrl = 'http://localhost:8080/api/v1/user/';
  private avatarUrlSubject = new BehaviorSubject<string>('icone.jpg'); // URL par défaut
  avatarUrl$ = this.avatarUrlSubject.asObservable();

  constructor(private http: HttpClient) {}

  getProfilePicture(): void {
    const token = localStorage.getItem('authToken');
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      this.http
        .get(this.apiUrl + 'profile/download-picture', { headers, responseType: 'blob' })
        .pipe(
          tap((blob) => {
            const oldUrl = this.avatarUrlSubject.value;
          if (oldUrl && oldUrl !== 'icone.jpg') {
            URL.revokeObjectURL(oldUrl);
          }
          const newUrl = URL.createObjectURL(blob);
          this.avatarUrlSubject.next(newUrl);
        })
      )
        .subscribe();
    }
  }

  uploadImage(file: File, authToken: string): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);

    const headers = new HttpHeaders({
      Authorization: `Bearer ${authToken}`,
    });

    return this.http.post(this.apiUrl + 'profile/upload-picture', formData, { headers }).pipe(
      tap(() => {
        this.getProfilePicture(); // Recharge automatiquement l'avatar après téléversement
      })
    );
  }

  resetProfilePicture(): void {
    this.avatarUrlSubject.next('icone.jpg'); // Remet l'avatar par défaut
  }
}
