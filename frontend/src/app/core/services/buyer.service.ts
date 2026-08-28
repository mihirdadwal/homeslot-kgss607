import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/auth.model';
import {
  Property,
  PropertyInquiry,
  CreateInquiryRequest,
  SiteVisit,
  CreateSiteVisitRequest,
  BuyerDashboardMetrics,
  BuyerProfile
} from '../models/property.model';

@Injectable({
  providedIn: 'root'
})
export class BuyerService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/buyer';

  // Favorites
  addFavorite(propertyId: number): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/favorites/${propertyId}`, {});
  }

  removeFavorite(propertyId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/favorites/${propertyId}`);
  }

  getFavoriteProperties(): Observable<ApiResponse<Property[]>> {
    return this.http.get<ApiResponse<Property[]>>(`${this.apiUrl}/favorites`);
  }

  // Recently Viewed
  getRecentlyViewed(): Observable<ApiResponse<Property[]>> {
    return this.http.get<ApiResponse<Property[]>>(`${this.apiUrl}/recently-viewed`);
  }

  clearRecentlyViewed(): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/recently-viewed`);
  }

  // Inquiries
  createInquiry(req: CreateInquiryRequest): Observable<ApiResponse<PropertyInquiry>> {
    return this.http.post<ApiResponse<PropertyInquiry>>(`${this.apiUrl}/inquiries`, req);
  }

  getBuyerInquiries(): Observable<ApiResponse<PropertyInquiry[]>> {
    return this.http.get<ApiResponse<PropertyInquiry[]>>(`${this.apiUrl}/inquiries`);
  }

  // Site Visits
  scheduleSiteVisit(req: CreateSiteVisitRequest): Observable<ApiResponse<SiteVisit>> {
    return this.http.post<ApiResponse<SiteVisit>>(`${this.apiUrl}/site-visits`, req);
  }

  getBuyerSiteVisits(): Observable<ApiResponse<SiteVisit[]>> {
    return this.http.get<ApiResponse<SiteVisit[]>>(`${this.apiUrl}/site-visits`);
  }

  cancelSiteVisit(visitId: number): Observable<ApiResponse<void>> {
    return this.http.patch<ApiResponse<void>>(`${this.apiUrl}/site-visits/${visitId}/cancel`, {});
  }

  // Dashboard Metrics
  getDashboardMetrics(): Observable<ApiResponse<BuyerDashboardMetrics>> {
    return this.http.get<ApiResponse<BuyerDashboardMetrics>>(`${this.apiUrl}/dashboard/metrics`);
  }

  // Profile
  getProfile(): Observable<ApiResponse<BuyerProfile>> {
    return this.http.get<ApiResponse<BuyerProfile>>(`${this.apiUrl}/profile`);
  }

  updateProfile(profile: BuyerProfile): Observable<ApiResponse<BuyerProfile>> {
    return this.http.put<ApiResponse<BuyerProfile>>(`${this.apiUrl}/profile`, profile);
  }
}
