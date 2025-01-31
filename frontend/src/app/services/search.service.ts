import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import PageableResponse from '../utils/PageableResponse';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  constructor(private http: HttpClient) {}

  // Récupérer les services en fonction des filtres
  search(query: SearchQuery) {
    return this.http.get<PageableResponse<SearchResult>>(`/services`, {
      params: {...query}
    });
  }

  // Requête pour récupérer l'image décryptée en tant que blob
  getImageBlob(imagePath: string): Observable<Blob> {
    // Utilisation de l'URL de l'endpoint qui inclut le chemin de l'image
    const imageUrl = `/services/image?path=${imagePath}`;

    return this.http.get(imageUrl, {
      responseType: 'blob'
    });
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
