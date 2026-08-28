import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-register-builder',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>Register as Builder</h2>
        <p class="subtitle">List and manage your property inventory</p>

        <div *ngIf="errorMessage" class="error-banner">{{ errorMessage }}</div>
        <div *ngIf="successMessage" class="success-banner">{{ successMessage }}</div>

        <form (ngSubmit)="onRegister()" *ngIf="!showOtp">
          <div class="form-group">
            <label>Company Name</label>
            <input type="text" [(ngModel)]="companyName" name="companyName" required placeholder="Apex Developers Pvt Ltd">
          </div>

          <div class="form-group">
            <label>Contact Person Name</label>
            <input type="text" [(ngModel)]="contactPersonName" name="contactPersonName" required placeholder="Jane Smith">
          </div>

          <div class="form-group">
            <label>Email Address</label>
            <input type="email" [(ngModel)]="email" name="email" required placeholder="builder@apexdev.com">
          </div>

          <div class="form-group">
            <label>Phone Number</label>
            <input type="text" [(ngModel)]="phone" name="phone" required placeholder="+1234567890">
          </div>

          <div class="form-group">
            <label>Business License Number (Optional)</label>
            <input type="text" [(ngModel)]="businessLicenseNumber" name="businessLicenseNumber" placeholder="LIC-99887766">
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" [(ngModel)]="password" name="password" required placeholder="Minimum 8 characters">
          </div>

          <button type="submit" [disabled]="loading" class="btn-primary">
            {{ loading ? 'Registering Builder Account...' : 'Register Builder' }}
          </button>
        </form>

        <div *ngIf="showOtp" class="otp-box">
          <h3>Verify OTP</h3>
          <p class="subtitle">Enter 6-digit numeric OTP sent to {{ phone }}</p>
          <input type="text" [(ngModel)]="otpCode" maxlength="6" class="otp-input" placeholder="123456">
          <button (click)="onVerifyOtp()" class="btn-primary" style="margin-top: 15px;">Verify & Proceed</button>
        </div>

        <div class="auth-footer">
          <p>Already have an account? <a routerLink="/auth/login">Login here</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; padding: 20px; font-family: 'Segoe UI', sans-serif; }
    .auth-card { background: #ffffff; padding: 36px; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.08); width: 100%; max-width: 480px; border: 1px solid #e2e8f0; }
    h2, h3 { margin: 0 0 8px; color: #1e293b; text-align: center; }
    .subtitle { color: #64748b; text-align: center; margin-bottom: 20px; font-size: 14px; }
    .form-group { margin-bottom: 16px; }
    label { display: block; margin-bottom: 4px; font-weight: 600; color: #334155; font-size: 13px; }
    input { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 14px; box-sizing: border-box; }
    .otp-input { letter-spacing: 8px; text-align: center; font-size: 24px; font-weight: bold; }
    .btn-primary { width: 100%; padding: 12px; background: #7c3aed; color: white; border: none; border-radius: 6px; font-size: 15px; font-weight: 600; cursor: pointer; }
    .btn-primary:hover { background: #6d28d9; }
    .error-banner { background: #fef2f2; color: #dc2626; padding: 10px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; text-align: center; }
    .success-banner { background: #ecfdf5; color: #047857; padding: 10px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; text-align: center; }
    .auth-footer { margin-top: 20px; text-align: center; font-size: 13px; }
    .auth-footer a { color: #2563eb; text-decoration: none; font-weight: 600; }
  `]
})
export class RegisterBuilderComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  companyName = '';
  contactPersonName = '';
  email = '';
  phone = '';
  businessLicenseNumber = '';
  password = '';

  loading = false;
  showOtp = false;
  otpCode = '';
  errorMessage = '';
  successMessage = '';

  onRegister(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.registerBuilder({
      companyName: this.companyName,
      contactPersonName: this.contactPersonName,
      email: this.email,
      phone: this.phone,
      businessLicenseNumber: this.businessLicenseNumber,
      password: this.password
    }).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.successMessage = 'Builder registered! Verification OTP sent.';
        this.showOtp = true;
      },
      error: (err: any) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Builder registration failed.';
      }
    });
  }

  onVerifyOtp(): void {
    this.authService.verifyOtp({
      target: this.phone,
      otpCode: this.otpCode,
      purpose: 'VERIFY_PHONE'
    }).subscribe({
      next: (res: any) => {
        alert('Builder phone verified successfully! Please login.');
        this.router.navigate(['/auth/login']);
      },
      error: (err: any) => {
        this.errorMessage = 'Invalid OTP code.';
      }
    });
  }
}
