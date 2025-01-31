import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import PageableResponse from '../utils/PageableResponse';
import { SearchResult } from './search.service';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  constructor(private http: HttpClient) {}

  getFavorites(page: number = 0): Observable<PageableResponse<SearchResult>> {
    return this.http.get<PageableResponse<SearchResult>>(`/user/me/favorites`, {
      params: {
        size: 6,
        page: page
      }
    });
  }

  addFavorite(serviceId: string): Observable<any> {
    const url = `/user/favorites/${serviceId}`;

    return this.http.post(url, null);
  }

  removeFavorite(serviceId: string): Observable<any> {
    const url = `/user/favorites/${serviceId}`;

    return this.http.delete(url);
  }
}

