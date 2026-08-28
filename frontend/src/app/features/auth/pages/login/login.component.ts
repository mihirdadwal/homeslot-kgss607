import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>HomeSlot Login</h2>
        <p class="subtitle">Access your account (Buyer, Builder, Admin)</p>

        <div *ngIf="errorMessage" class="error-banner">
          {{ errorMessage }}
        </div>

        <form (ngSubmit)="onLogin()">
          <div class="form-group">
            <label>Email or Phone Number</label>
            <input type="text" [(ngModel)]="emailOrPhone" name="emailOrPhone" required placeholder="user@homeslot.com or +1234567890">
          </div>

          <div class="form-group">
            <label>Password</label>
            <input type="password" [(ngModel)]="password" name="password" required placeholder="••••••••">
          </div>

          <button type="submit" [disabled]="loading" class="btn-primary">
            {{ loading ? 'Authenticating...' : 'Sign In' }}
          </button>
        </form>

        <div class="auth-footer">
          <p><a routerLink="/auth/forgot-password">Forgot password?</a></p>
          <p>Don't have an account? <a routerLink="/auth/register-buyer">Register as Buyer</a> | <a routerLink="/auth/register-builder">Register as Builder</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; padding: 20px; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
    .auth-card { background: #ffffff; padding: 40px; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.08); width: 100%; max-width: 440px; border: 1px solid #e2e8f0; }
    h2 { margin: 0 0 8px; color: #1e293b; font-size: 24px; text-align: center; font-weight: 700; }
    .subtitle { color: #64748b; text-align: center; margin-bottom: 24px; font-size: 14px; }
    .form-group { margin-bottom: 20px; }
    label { display: block; margin-bottom: 6px; font-weight: 600; color: #334155; font-size: 14px; }
    input { width: 100%; padding: 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 14px; box-sizing: border-box; }
    input:focus { border-color: #2563eb; outline: none; box-shadow: 0 0 0 3px rgba(37,99,235,0.1); }
    .btn-primary { width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 6px; font-size: 16px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
    .btn-primary:hover { background: #1d4ed8; }
    .error-banner { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; padding: 10px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; text-align: center; }
    .auth-footer { margin-top: 24px; text-align: center; font-size: 13px; color: #64748b; line-height: 1.6; }
    .auth-footer a { color: #2563eb; text-decoration: none; font-weight: 600; }
  `]
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  emailOrPhone = '';
  password = '';
  loading = false;
  errorMessage = '';

  onLogin(): void {
    if (!this.emailOrPhone || !this.password) {
      this.errorMessage = 'Please enter credentials';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login({ emailOrPhone: this.emailOrPhone, password: this.password }).subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res.success) {
          const role = res.data.role;
          if (role === 'ROLE_BUYER') this.router.navigate(['/dashboard/buyer']);
          else if (role === 'ROLE_BUILDER') this.router.navigate(['/dashboard/builder']);
          else if (role === 'ROLE_ADMIN') this.router.navigate(['/dashboard/admin']);
        }
      },
      error: (err: any) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Login failed. Invalid credentials.';
      }
    });
  }
}
