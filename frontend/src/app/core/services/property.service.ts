import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/auth.model';
import { Property, PropertyReview, CreateReviewRequest, PropertySearchRequest } from '../models/property.model';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/properties';

  searchProperties(req: PropertySearchRequest): Observable<ApiResponse<PageResponse<Property>>> {
    let params = new HttpParams();
    if (req.keyword) params = params.set('keyword', req.keyword);
    if (req.city) params = params.set('city', req.city);
    if (req.minPrice) params = params.set('minPrice', req.minPrice.toString());
    if (req.maxPrice) params = params.set('maxPrice', req.maxPrice.toString());
    if (req.propertyType) params = params.set('propertyType', req.propertyType);
    if (req.bhk) params = params.set('bhk', req.bhk.toString());
    if (req.status) params = params.set('status', req.status);
    if (req.listingType) params = params.set('listingType', req.listingType);
    if (req.amenity) params = params.set('amenity', req.amenity);
    if (req.sortBy) params = params.set('sortBy', req.sortBy);
    if (req.page !== undefined) params = params.set('page', req.page.toString());
    if (req.size !== undefined) params = params.set('size', req.size.toString());

    return this.http.get<ApiResponse<PageResponse<Property>>>(this.apiUrl, { params });
  }

  getFeaturedProperties(): Observable<ApiResponse<Property[]>> {
    return this.http.get<ApiResponse<Property[]>>(`${this.apiUrl}/featured`);
  }

  getPropertyById(id: number): Observable<ApiResponse<Property>> {
    return this.http.get<ApiResponse<Property>>(`${this.apiUrl}/${id}`);
  }

  addReview(propertyId: number, req: CreateReviewRequest): Observable<ApiResponse<PropertyReview>> {
    return this.http.post<ApiResponse<PropertyReview>>(`${this.apiUrl}/${propertyId}/reviews`, req);
  }

  getPropertyReviews(propertyId: number): Observable<ApiResponse<PropertyReview[]>> {
    return this.http.get<ApiResponse<PropertyReview[]>>(`${this.apiUrl}/${propertyId}/reviews`);
  }

  getBrochureUrl(propertyId: number): string {
    return `${this.apiUrl}/${propertyId}/brochure`;
  }
}
