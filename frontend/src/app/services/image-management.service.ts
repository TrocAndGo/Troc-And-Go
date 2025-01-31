import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImageManagementService {
  avatarUrlSubject = new BehaviorSubject<string>('icone.jpg'); // URL par défaut
  avatarUrl$ = this.avatarUrlSubject.asObservable();

  constructor(private http: HttpClient) { }

  getProfilePicture(): void {
    this.http
      .get('/user/profile/download-picture', { responseType: 'blob' })
      .pipe(
        tap((blob) => {
          const oldUrl = this.avatarUrlSubject.value;
          if (oldUrl && oldUrl !== 'icone.jpg') {
            URL.revokeObjectURL(oldUrl);
          }
          const newUrl = URL.createObjectURL(blob);
          this.avatarUrlSubject.next(newUrl);
        })
      ).subscribe();
  }

  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);

    return this.http.post('/user/profile/upload-picture', formData).pipe(
      tap(() => {
        this.getProfilePicture(); // Recharge automatiquement l'avatar après téléversement
      })
    );
  }

  resetProfilePicture(): void {
    this.avatarUrlSubject.next('icone.jpg'); // Remet l'avatar par défaut
  }
}
