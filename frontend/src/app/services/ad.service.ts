import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import PageableResponse from '../utils/PageableResponse';
import { SearchResult } from './search.service';

@Injectable({
  providedIn: 'root'
})
export class AdService {

  constructor(private http: HttpClient) { }

  uploadAd(adRequest: AdUploadRequest): Observable<any> {
    console.log('Données envoyées à l\'API:', adRequest);
    return this.http.post(`/services/create`, adRequest);
  }

  getServices(): Observable<any> {
    return this.http.get(`/services/all`);
  }

  getMyServices(page: number = 0): Observable<PageableResponse<SearchResult>> {
    return this.http.get<PageableResponse<SearchResult>>(`/user/me/services`, {
      params: {
        page: page,
        size: 6
      }
    });
  }

  getCategories(): Observable<any> {
    return this.http.get(`/services/categories`);
  }

  getAdressFilters(): Observable<any> {
    return this.http.get(`/services/adresses`);
  }

  deleteService(serviceId: string) {
    return this.http.delete(`/services/${serviceId}`);
  }
}

export interface AdUploadRequest {
  title: string;
  description: string;
  type: string;
  categoryId: string;
  useCreatorAddress: boolean;
}

export type AdCategory = {
  id: number;
  title: string
};

export type AdressFilters = {
  regions: string[];
  departments: string[];
  cities: string[];
}
