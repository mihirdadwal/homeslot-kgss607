import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ApiResponse, AuthTokenResponse, User } from '../models/auth.model';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private tokenService = inject(TokenService);
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  registerBuyer(payload: any): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.apiUrl}/register/buyer`, payload);
  }

  registerBuilder(payload: any): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.apiUrl}/register/builder`, payload);
  }

  login(payload: any): Observable<ApiResponse<AuthTokenResponse>> {
    return this.http.post<ApiResponse<AuthTokenResponse>>(`${this.apiUrl}/login`, payload).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.tokenService.setTokens(res.data.accessToken, res.data.refreshToken);
          this.tokenService.setUser({
            userId: res.data.userId,
            email: res.data.email,
            role: res.data.role
          });
        }
      })
    );
  }

  verifyOtp(payload: any): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(`${this.apiUrl}/verify-otp`, payload);
  }

  forgotPassword(payload: any): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(`${this.apiUrl}/forgot-password`, payload);
  }

  resetPassword(payload: any): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(`${this.apiUrl}/reset-password`, payload);
  }

  refreshToken(): Observable<ApiResponse<AuthTokenResponse>> {
    const refreshToken = this.tokenService.getRefreshToken();
    return this.http.post<ApiResponse<AuthTokenResponse>>(`${this.apiUrl}/refresh-token`, { refreshToken }).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.tokenService.setTokens(res.data.accessToken, res.data.refreshToken);
        }
      })
    );
  }

  logout(): void {
    this.tokenService.clearTokens();
  }

  isLoggedIn(): boolean {
    return !!this.tokenService.getAccessToken();
  }

  getUserRole(): string | null {
    const user = this.tokenService.getUser();
    return user ? user.role : null;
  }
}
