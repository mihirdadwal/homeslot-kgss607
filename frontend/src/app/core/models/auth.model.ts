export interface User {
  id: number;
  email: string;
  phone: string;
  role: 'ROLE_BUYER' | 'ROLE_BUILDER' | 'ROLE_ADMIN';
  isEmailVerified: boolean;
  isPhoneVerified: boolean;
  isActive: boolean;
  fullName?: string;
  city?: string;
  companyName?: string;
  contactPersonName?: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInMs: number;
  userId: number;
  email: string;
  role: 'ROLE_BUYER' | 'ROLE_BUILDER' | 'ROLE_ADMIN';
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
