import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

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
      region: string,
      department: string,
      zipCode: string;
      latitude: number;
      longitude: number
    }): Observable<any> {
      const headers = this.getAuthHeaders();
      return this.http.put<any>(
        `${this.apiUrl}/user/profile/update`,
        data,
        { headers }
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
