import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ImageManagementService {
  private defaultAvatar = 'default_avatar.jpg';
  private avatarUrlSubject = new BehaviorSubject<string>(this.defaultAvatar);
  avatarUrl$ = this.avatarUrlSubject.asObservable();

  constructor(private http: HttpClient) { }

  getProfilePicture(): void {
    this.http
      .get('/user/profile/download-picture', { responseType: 'blob' })
      .pipe(
        tap((blob) => {
          if (blob.size === 0) {
            // Cas où l'image n'existe pas (blob vide)
            console.warn('Image de profil non trouvée, chargement de l’avatar par défaut.');
            this.avatarUrlSubject.next(this.defaultAvatar);
          } else {
            // Crée la nouvelle URL blob
            const newUrl = URL.createObjectURL(blob);

            // Libérer l'ancienne URL seulement après la création de la nouvelle
            const oldUrl = this.avatarUrlSubject.value;
            if (oldUrl && oldUrl !== this.defaultAvatar) {
              URL.revokeObjectURL(oldUrl);
            }

            // Mise à jour de l'avatar
            this.avatarUrlSubject.next(newUrl);
            console.log('Image de profil chargée avec succès :', newUrl);
          }
        }),
        catchError((error: HttpErrorResponse) => {
          if (error.status === 404) {
            console.warn('Aucune image de profil trouvée (404), utilisation de l\'avatar par défaut.');
            this.avatarUrlSubject.next(this.defaultAvatar);
          } else {
            console.error('Erreur lors du téléchargement de l\'image :', error);
            this.avatarUrlSubject.next(this.defaultAvatar);
          }
          return of(null); // Empêche l'erreur de propager
        }),
        finalize(() => {
          console.log('Téléchargement de l\'image de profil terminé.');
        })
      )
      .subscribe();
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
    this.avatarUrlSubject.next(this.defaultAvatar); // Remet l'avatar par défaut
  }

  // Getter public pour accéder à la valeur actuelle de l'avatar dans my-ads
  get currentAvatarUrl(): string {
    return this.avatarUrlSubject.value;
  }
}
