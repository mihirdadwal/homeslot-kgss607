import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TokenService } from '../../../core/services/token.service';
import { AdminService } from '../../../core/services/admin.service';
import {
  AdminDashboardMetrics,
  UserManagementDto,
  Property,
  BuilderProfile,
  AuditLogDto
} from '../../../core/models/property.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private tokenService = inject(TokenService);
  private adminService = inject(AdminService);
  private router = inject(Router);

  user = this.tokenService.getUser();
  activeTab: 'overview' | 'approvals' | 'builders' | 'users' | 'audits' = 'overview';

  metrics: AdminDashboardMetrics = {
    totalUsers: 0,
    totalBuyers: 0,
    totalBuilders: 0,
    totalProperties: 0,
    pendingPropertyApprovals: 0,
    pendingBuilderVerifications: 0,
    totalInquiries: 0,
    totalSiteVisits: 0
  };

  pendingProperties: Property[] = [];
  builders: BuilderProfile[] = [];
  users: UserManagementDto[] = [];
  auditLogs: AuditLogDto[] = [];

  // User Filter
  userRoleFilter: 'ALL' | 'ROLE_BUYER' | 'ROLE_BUILDER' | 'ROLE_ADMIN' = 'ALL';
  userSearchQuery = '';

  // Alert
  alertMessage: string | null = null;
  alertType: 'success' | 'danger' | 'info' = 'info';

  // Modal State for Property Rejection Reason / Verification Remarks
  showReasonModal = false;
  reasonModalTitle = '';
  reasonText = '';
  pendingActionType: 'REJECT_PROP' | 'REJECT_BUILDER' | null = null;
  selectedItemId: number | null = null;

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.loadMetrics();
    this.loadPendingProperties();
    this.loadBuilders();
    this.loadUsers();
    this.loadAuditLogs();
  }

  loadMetrics(): void {
    this.adminService.getDashboardMetrics().subscribe({
      next: (res) => {
        if (res.data) this.metrics = res.data;
      },
      error: (err) => console.error('Failed to load admin metrics', err)
    });
  }

  loadPendingProperties(): void {
    this.adminService.getPendingProperties().subscribe({
      next: (res) => {
        this.pendingProperties = res.data || [];
      },
      error: (err) => console.error('Failed to load pending properties', err)
    });
  }

  loadBuilders(): void {
    this.adminService.getBuilderProfiles().subscribe({
      next: (res) => {
        this.builders = res.data || [];
      },
      error: (err) => console.error('Failed to load builders', err)
    });
  }

  loadUsers(): void {
    this.adminService.getAllUsers().subscribe({
      next: (res) => {
        this.users = res.data || [];
      },
      error: (err) => console.error('Failed to load users', err)
    });
  }

  loadAuditLogs(): void {
    this.adminService.getAuditLogs().subscribe({
      next: (res) => {
        this.auditLogs = res.data || [];
      },
      error: (err) => console.error('Failed to load audit logs', err)
    });
  }

  setActiveTab(tab: 'overview' | 'approvals' | 'builders' | 'users' | 'audits'): void {
    this.activeTab = tab;
  }

  // --- PROPERTY MODERATION ---

  approveProperty(id: number, title: string): void {
    this.adminService.approveOrRejectProperty(id, { approvalStatus: 'APPROVED' }).subscribe({
      next: () => {
        this.showAlert(`Property "${title}" has been APPROVED and published live!`, 'success');
        this.loadAllData();
      },
      error: (err) => {
        console.error(err);
        this.showAlert('Failed to approve property.', 'danger');
      }
    });
  }

  openRejectPropertyModal(id: number, title: string): void {
    this.selectedItemId = id;
    this.pendingActionType = 'REJECT_PROP';
    this.reasonModalTitle = `Reject Property Listing: ${title}`;
    this.reasonText = '';
    this.showReasonModal = true;
  }

  // --- BUILDER KYC VERIFICATION ---

  verifyBuilder(id: number | undefined, companyName: string): void {
    if (!id) return;
    this.adminService.verifyBuilder(id, { verificationStatus: 'VERIFIED' }).subscribe({
      next: () => {
        this.showAlert(`Builder "${companyName}" has been VERIFIED!`, 'success');
        this.loadAllData();
      },
      error: (err) => {
        console.error(err);
        this.showAlert('Failed to verify builder.', 'danger');
      }
    });
  }

  openRejectBuilderModal(id: number | undefined, companyName: string): void {
    if (!id) return;
    this.selectedItemId = id;
    this.pendingActionType = 'REJECT_BUILDER';
    this.reasonModalTitle = `Reject Builder KYC: ${companyName}`;
    this.reasonText = '';
    this.showReasonModal = true;
  }

  formatStatus(status: string | undefined): string {
    return status ? status.replace(/_/g, ' ') : '';
  }

  submitReasonModal(): void {
    if (!this.selectedItemId || !this.pendingActionType) return;

    if (this.pendingActionType === 'REJECT_PROP') {
      this.adminService.approveOrRejectProperty(this.selectedItemId, {
        approvalStatus: 'REJECTED',
        rejectionReason: this.reasonText || 'Listing does not meet quality guidelines'
      }).subscribe({
        next: () => {
          this.showAlert('Property listing marked REJECTED.', 'info');
          this.closeReasonModal();
          this.loadAllData();
        },
        error: (err) => {
          console.error(err);
          this.showAlert('Failed to reject property.', 'danger');
        }
      });
    } else if (this.pendingActionType === 'REJECT_BUILDER') {
      this.adminService.verifyBuilder(this.selectedItemId, {
        verificationStatus: 'REJECTED',
        remarks: this.reasonText || 'KYC documentation incomplete'
      }).subscribe({
        next: () => {
          this.showAlert('Builder account marked REJECTED.', 'info');
          this.closeReasonModal();
          this.loadAllData();
        },
        error: (err) => {
          console.error(err);
          this.showAlert('Failed to reject builder.', 'danger');
        }
      });
    }
  }

  closeReasonModal(): void {
    this.showReasonModal = false;
    this.selectedItemId = null;
    this.pendingActionType = null;
    this.reasonText = '';
  }

  // --- USER GOVERNANCE ---

  toggleUserActive(u: UserManagementDto): void {
    const nextState = !u.active;
    this.adminService.updateUserStatus(u.id, { active: nextState }).subscribe({
      next: () => {
        u.active = nextState;
        this.showAlert(`User ${u.email} account state set to ${nextState ? 'ACTIVE' : 'DEACTIVATED'}.`, 'info');
        this.loadAuditLogs();
      },
      error: (err) => {
        console.error(err);
        this.showAlert('Failed to update user status.', 'danger');
      }
    });
  }

  get filteredUsers(): UserManagementDto[] {
    return this.users.filter(u => {
      const matchesRole = this.userRoleFilter === 'ALL' || u.role === this.userRoleFilter;
      const q = this.userSearchQuery.trim().toLowerCase();
      const matchesSearch = !q || u.email.toLowerCase().includes(q) || (u.nameOrCompany && u.nameOrCompany.toLowerCase().includes(q));
      return matchesRole && matchesSearch;
    });
  }

  formatRole(role: string): string {
    return role ? role.replace('ROLE_', '') : '';
  }

  showAlert(message: string, type: 'success' | 'danger' | 'info'): void {
    this.alertMessage = message;
    this.alertType = type;
    setTimeout(() => {
      this.alertMessage = null;
    }, 4000);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
