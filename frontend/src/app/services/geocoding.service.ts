import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GeocodingService {
  private geoApiUrl = 'https://data.geopf.fr/geocodage/search';

  constructor(private http: HttpClient) {}

  searchAddress(query: string): Observable<any> {
    const url = `${this.geoApiUrl}?q=${encodeURIComponent(query)}`;
    return this.http.get<any>(url);
  }
}
