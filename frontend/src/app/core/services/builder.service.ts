import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/auth.model';
import {
  Property,
  CreatePropertyRequest,
  UpdatePropertyRequest,
  PropertyInquiry,
  ReplyInquiryRequest,
  SiteVisit,
  BuilderDashboardMetrics,
  BuilderProfile
} from '../models/property.model';

@Injectable({
  providedIn: 'root'
})
export class BuilderService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/builder';

  // Metrics
  getDashboardMetrics(): Observable<ApiResponse<BuilderDashboardMetrics>> {
    return this.http.get<ApiResponse<BuilderDashboardMetrics>>(`${this.apiUrl}/dashboard/metrics`);
  }

  // Property Management
  getBuilderProperties(): Observable<ApiResponse<Property[]>> {
    return this.http.get<ApiResponse<Property[]>>(`${this.apiUrl}/properties`);
  }

  createProperty(req: CreatePropertyRequest): Observable<ApiResponse<Property>> {
    return this.http.post<ApiResponse<Property>>(`${this.apiUrl}/properties`, req);
  }

  updateProperty(id: number, req: UpdatePropertyRequest): Observable<ApiResponse<Property>> {
    return this.http.put<ApiResponse<Property>>(`${this.apiUrl}/properties/${id}`, req);
  }

  deleteProperty(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/properties/${id}`);
  }

  // Inquiries / Leads
  getBuilderInquiries(): Observable<ApiResponse<PropertyInquiry[]>> {
    return this.http.get<ApiResponse<PropertyInquiry[]>>(`${this.apiUrl}/inquiries`);
  }

  replyInquiry(id: number, req: ReplyInquiryRequest): Observable<ApiResponse<PropertyInquiry>> {
    return this.http.post<ApiResponse<PropertyInquiry>>(`${this.apiUrl}/inquiries/${id}/reply`, req);
  }

  // Site Visits
  getBuilderSiteVisits(): Observable<ApiResponse<SiteVisit[]>> {
    return this.http.get<ApiResponse<SiteVisit[]>>(`${this.apiUrl}/site-visits`);
  }

  updateSiteVisitStatus(id: number, status: string): Observable<ApiResponse<SiteVisit>> {
    return this.http.patch<ApiResponse<SiteVisit>>(`${this.apiUrl}/site-visits/${id}/status?status=${status}`, {});
  }

  // Profile
  getBuilderProfile(): Observable<ApiResponse<BuilderProfile>> {
    return this.http.get<ApiResponse<BuilderProfile>>(`${this.apiUrl}/profile`);
  }

  updateBuilderProfile(profile: BuilderProfile): Observable<ApiResponse<BuilderProfile>> {
    return this.http.put<ApiResponse<BuilderProfile>>(`${this.apiUrl}/profile`, profile);
  }
}
