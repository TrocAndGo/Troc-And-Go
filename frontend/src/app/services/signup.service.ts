import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SignupService {

    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient) {}

    signup(signupRequest: SignupRequest): Observable<any> {
      return this.http.post(`${this.apiUrl}/auth/signup`, signupRequest, {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' })
      });
  }
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  roles: Array<string>;
}
