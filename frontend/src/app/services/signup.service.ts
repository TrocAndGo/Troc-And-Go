import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SignupService {
    constructor(private http: HttpClient) {}

    signup(signupRequest: SignupRequest): Observable<any> {
      return this.http.post(`/auth/signup`, signupRequest, {
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
