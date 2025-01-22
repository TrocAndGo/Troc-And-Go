import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private apiUrl = 'http://localhost:8080/api/v1/services';

  constructor(private http: HttpClient) {}

  search(query: SearchQuery) {
    return this.http.get<SearchResponse>(this.apiUrl, {
      params: {...query}
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
