import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConversationsService {
  private apiUrl = '/chat/conversations';

  constructor(private http: HttpClient) {}

  getConversations(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
}
