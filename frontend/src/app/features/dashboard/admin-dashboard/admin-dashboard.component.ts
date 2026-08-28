import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TokenService } from '../../../core/services/token.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-layout">
      <nav class="navbar">
        <div class="logo">HomeSlot <span>Admin Operations</span></div>
        <div class="user-info">
          <span>Welcome, {{ user?.email }} (Admin)</span>
          <button (click)="logout()" class="btn-logout">Logout</button>
        </div>
      </nav>
      <main class="content">
        <div class="welcome-card">
          <h1>Admin Control Panel</h1>
          <p>Phase 2 Authentication & System Governance verified successfully.</p>
          <div class="badge">ROLE_ADMIN Authorized</div>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .dashboard-layout { font-family: 'Segoe UI', sans-serif; background: #f8fafc; min-height: 100vh; }
    .navbar { display: flex; justify-content: space-between; align-items: center; background: #1e293b; color: white; padding: 16px 32px; }
    .logo { font-size: 20px; font-weight: bold; }
    .logo span { font-weight: normal; font-size: 14px; opacity: 0.9; }
    .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; margin-left: 16px; }
    .content { padding: 40px; max-width: 1000px; margin: 0 auto; }
    .welcome-card { background: white; padding: 32px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
    .badge { display: inline-block; background: #f1f5f9; color: #0f172a; padding: 6px 12px; border-radius: 20px; font-weight: 600; font-size: 13px; margin-top: 12px; }
  `]
})
export class AdminDashboardComponent {
  private authService = inject(AuthService);
  private tokenService = inject(TokenService);
  private router = inject(Router);

  user = this.tokenService.getUser();

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
