import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {}

  getUserProfile(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/profile`);
  }

  updateUserProfile(profileData: ProfileRequest): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/profile`, profileData);
  }
}

export interface ProfileRequest {
  username: string;
  email: string;
  password: string;
  roles: Array<string>;
  address: string;
  phone_number: string;
}
