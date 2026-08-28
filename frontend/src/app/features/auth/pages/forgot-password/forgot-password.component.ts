import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>Forgot Password</h2>
        <p class="subtitle">Enter your registered email to receive reset instructions</p>

        <div *ngIf="errorMessage" class="error-banner">{{ errorMessage }}</div>
        <div *ngIf="successMessage" class="success-banner">{{ successMessage }}</div>

        <form (ngSubmit)="onForgot()" *ngIf="!submitted">
          <div class="form-group">
            <label>Registered Email</label>
            <input type="email" [(ngModel)]="email" name="email" required placeholder="user@homeslot.com">
          </div>

          <button type="submit" [disabled]="loading" class="btn-primary">
            {{ loading ? 'Sending...' : 'Send Reset Link / OTP' }}
          </button>
        </form>

        <div *ngIf="submitted" class="reset-form">
          <div class="form-group">
            <label>Enter OTP Code</label>
            <input type="text" [(ngModel)]="otpCode" name="otpCode" required placeholder="123456">
          </div>
          <div class="form-group">
            <label>New Password</label>
            <input type="password" [(ngModel)]="newPassword" name="newPassword" required placeholder="New password">
          </div>
          <button (click)="onReset()" class="btn-primary">Reset Password</button>
        </div>

        <div class="auth-footer">
          <p><a routerLink="/auth/login">Back to Login</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; padding: 20px; font-family: 'Segoe UI', sans-serif; }
    .auth-card { background: #ffffff; padding: 36px; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.08); width: 100%; max-width: 420px; border: 1px solid #e2e8f0; }
    h2 { margin: 0 0 8px; color: #1e293b; text-align: center; }
    .subtitle { color: #64748b; text-align: center; margin-bottom: 20px; font-size: 14px; }
    .form-group { margin-bottom: 16px; }
    label { display: block; margin-bottom: 4px; font-weight: 600; color: #334155; font-size: 13px; }
    input { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 14px; box-sizing: border-box; }
    .btn-primary { width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 6px; font-size: 15px; font-weight: 600; cursor: pointer; }
    .error-banner { background: #fef2f2; color: #dc2626; padding: 10px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; text-align: center; }
    .success-banner { background: #ecfdf5; color: #047857; padding: 10px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; text-align: center; }
    .auth-footer { margin-top: 20px; text-align: center; font-size: 13px; }
    .auth-footer a { color: #2563eb; text-decoration: none; font-weight: 600; }
  `]
})
export class ForgotPasswordComponent {
  private authService = inject(AuthService);

  email = '';
  otpCode = '';
  newPassword = '';
  loading = false;
  submitted = false;
  errorMessage = '';
  successMessage = '';

  onForgot(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.forgotPassword({ email: this.email }).subscribe({
      next: () => {
        this.loading = false;
        this.submitted = true;
        this.successMessage = 'OTP sent to your email.';
      },
      error: (err: any) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Request failed.';
      }
    });
  }

  onReset(): void {
    this.authService.resetPassword({
      email: this.email,
      tokenOrOtp: this.otpCode,
      newPassword: this.newPassword
    }).subscribe({
      next: () => {
        alert('Password reset successful! Please login.');
      },
      error: (err: any) => {
        this.errorMessage = err.error?.message || 'Password reset failed.';
      }
    });
  }
}
