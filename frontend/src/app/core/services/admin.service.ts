import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/auth.model';
import {
  AdminDashboardMetrics,
  UserManagementDto,
  Property,
  PropertyApprovalRequest,
  BuilderProfile,
  BuilderVerificationRequest,
  AuditLogDto
} from '../models/property.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/admin';

  // Metrics
  getDashboardMetrics(): Observable<ApiResponse<AdminDashboardMetrics>> {
    return this.http.get<ApiResponse<AdminDashboardMetrics>>(`${this.apiUrl}/dashboard/metrics`);
  }

  // User Governance
  getAllUsers(): Observable<ApiResponse<UserManagementDto[]>> {
    return this.http.get<ApiResponse<UserManagementDto[]>>(`${this.apiUrl}/users`);
  }

  updateUserStatus(userId: number, req: { active?: boolean; role?: string }): Observable<ApiResponse<UserManagementDto>> {
    return this.http.put<ApiResponse<UserManagementDto>>(`${this.apiUrl}/users/${userId}/status`, req);
  }

  // Property Moderation Queue
  getPendingProperties(): Observable<ApiResponse<Property[]>> {
    return this.http.get<ApiResponse<Property[]>>(`${this.apiUrl}/properties/pending`);
  }

  approveOrRejectProperty(propertyId: number, req: PropertyApprovalRequest): Observable<ApiResponse<Property>> {
    return this.http.post<ApiResponse<Property>>(`${this.apiUrl}/properties/${propertyId}/approval`, req);
  }

  // Builder KYC Verification
  getBuilderProfiles(): Observable<ApiResponse<BuilderProfile[]>> {
    return this.http.get<ApiResponse<BuilderProfile[]>>(`${this.apiUrl}/builders`);
  }

  verifyBuilder(builderId: number, req: BuilderVerificationRequest): Observable<ApiResponse<BuilderProfile>> {
    return this.http.post<ApiResponse<BuilderProfile>>(`${this.apiUrl}/builders/${builderId}/verification`, req);
  }

  // Audit Logs
  getAuditLogs(): Observable<ApiResponse<AuditLogDto[]>> {
    return this.http.get<ApiResponse<AuditLogDto[]>>(`${this.apiUrl}/audit-logs`);
  }
}
