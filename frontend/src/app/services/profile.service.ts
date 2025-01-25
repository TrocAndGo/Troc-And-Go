import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, switchMap, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private userAdressSubject = new BehaviorSubject<string | null>(null);
    userAdress$ = this.userAdressSubject.asObservable();

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {};

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getUserProfile(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.apiUrl}/user/profile`, { headers });
  }
/*
  updateUserProfile(profileData: ProfileRequest): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.put<any>(`${this.apiUrl}/profile/update`, profileData, { headers });
  }*/

    updateUserProfileWithGeocode(data: {
      phoneNumber: string;
      address: string;
      city: string;
      region: string;
      department: string;
      zipCode: string;
      latitude: number;
      longitude: number;
    }): Observable<any> {
      const headers = this.getAuthHeaders();

      return this.http.put<any>(`${this.apiUrl}/user/profile/update`, data, { headers }).pipe(
        // Charger la nouvelle adresse utilisateur après la mise à jour
        switchMap(() =>
          this.getUserProfile().pipe(
            tap((profileData) => {
              console.log('Données utilisateur récupérées :', profileData);

              let adress: string | null = null;
              if (profileData.address && profileData.zipCode && profileData.city) {
                adress = `${profileData.address} ${profileData.zipCode} ${profileData.city}`;
              }

              this.userAdressSubject.next(adress);
              console.log('Nouvelle adresse émise :', adress);
            })
          )
        )
      );
    }
  }


export interface ProfileRequest {
  //username: string;
  //email: string;
  //roles: Array<string>;
  address: string;
  city: string;
  zipCode: string;
  phoneNumber: string;
}
