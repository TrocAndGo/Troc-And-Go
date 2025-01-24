import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Récupérer les services en fonction des filtres
  search(query: SearchQuery) {
    return this.http.get<SearchResponse>(this.apiUrl, {
      params: {...query}
    });
  }

  // Requête pour récupérer l'image décryptée en tant que blob
  getImageBlob(imagePath: string): Observable<Blob> {
    // Utilisation de l'URL de l'endpoint qui inclut le chemin de l'image
    const imageUrl = `${this.apiUrl}/services/image?path=${imagePath}`;

    return this.http.get(imageUrl, {
      responseType: 'blob'
    });
  }
}

export type SearchQuery = {
  region: string;
  department: string;
  city: string;
  category: string;
  sort: string;
  size: number;
}

export type SearchResponse = {
  content: SearchResult[];
  page: SearchPagination;
}

export type SearchResult = {
  id: number;
  title: string;
  description: string;
  category: string;
  status: string;
  type: string;
  city: string;
  creationDate: Date;
  createdBy: string;
  creatorProfilePicture: string | null;
}

export type SearchPagination = {
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
