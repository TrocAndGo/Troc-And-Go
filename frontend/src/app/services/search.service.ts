import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { map, catchError } from 'rxjs/operators';
import PageableResponse from '../utils/PageableResponse';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http: HttpClient) {}

  // Récupérer les services en fonction des filtres
  search(query: SearchQuery) {
    return this.http.get<PageableResponse<SearchResult>>(`/services/all`, {
      params: {...query}
    });
  }

  // Requête pour récupérer l'image décryptée en tant que blob
  getImageBlob(imagePath: string): Observable<Blob> {
    const imageUrl = `/services/image?path=${imagePath}`;

    return this.http.get(imageUrl, {
      responseType: 'blob',
      observe: 'response' // Permet de vérifier le statut de la réponse
    }).pipe(
      map(response => {
        if (response.status === 200) {
          return response.body as Blob;
        } else {
          throw new Error(`Erreur HTTP : ${response.status}`);
        }
      }),
      catchError((err) => {
        console.error('Erreur lors de la récupération de l’image :', err);
        return throwError(() => err);
      })
    );
  }

}

export type SearchQuery = {
  region?: string;
  department?: string;
  city?: string;
  category?: string;
  sort?: string;
  page?: number;
  size?: number;
}

export type SearchResult = {
  id: string;
  title: string;
  description: string;
  category: string;
  status: string;
  type: string;
  city: string;
  creationDate: Date;
  createdBy: string;
  creatorProfilePicture: string | null;
  mail: string | null;
  phoneNumber: string | null;
  owner: boolean;
  favorite: boolean;
}

export type SearchPagination = {
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
