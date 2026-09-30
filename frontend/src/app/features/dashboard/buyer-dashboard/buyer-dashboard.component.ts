import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TokenService } from '../../../core/services/token.service';
import { BuyerService } from '../../../core/services/buyer.service';
import {
  Property,
  PropertyInquiry,
  SiteVisit,
  BuyerDashboardMetrics,
  BuyerProfile
} from '../../../core/models/property.model';

import { NotificationCenterComponent } from '../../../shared/components/notification-center/notification-center.component';

@Component({
  selector: 'app-buyer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NotificationCenterComponent],
  templateUrl: './buyer-dashboard.component.html',
  styleUrls: ['./buyer-dashboard.component.css']
})
export class BuyerDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private tokenService = inject(TokenService);
  private buyerService = inject(BuyerService);
  private router = inject(Router);

  user = this.tokenService.getUser();
  activeTab: 'overview' | 'favorites' | 'visits' | 'inquiries' | 'history' | 'profile' = 'overview';

  metrics: BuyerDashboardMetrics = {
    totalFavorites: 0,
    totalSiteVisits: 0,
    totalInquiries: 0,
    totalRecentlyViewed: 0
  };

  favorites: Property[] = [];
  siteVisits: SiteVisit[] = [];
  inquiries: PropertyInquiry[] = [];
  recentlyViewed: Property[] = [];
  profile: BuyerProfile = {
    fullName: '',
    city: ''
  };

  toastMessage: string | null = null;
  loading: boolean = false;

  ngOnInit(): void {
    this.loadMetrics();
    this.loadFavorites();
    this.loadSiteVisits();
    this.loadInquiries();
    this.loadRecentlyViewed();
    this.loadProfile();
  }

  loadMetrics(): void {
    this.buyerService.getDashboardMetrics().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.metrics = res.data;
        }
      }
    });
  }

  loadFavorites(): void {
    this.buyerService.getFavoriteProperties().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.favorites = res.data;
        }
      }
    });
  }

  loadSiteVisits(): void {
    this.buyerService.getBuyerSiteVisits().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.siteVisits = res.data;
        }
      }
    });
  }

  loadInquiries(): void {
    this.buyerService.getBuyerInquiries().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.inquiries = res.data;
        }
      }
    });
  }

  loadRecentlyViewed(): void {
    this.buyerService.getRecentlyViewed().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.recentlyViewed = res.data;
        }
      }
    });
  }

  loadProfile(): void {
    this.buyerService.getProfile().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.profile = res.data;
        }
      }
    });
  }

  removeFavorite(propertyId: number): void {
    this.buyerService.removeFavorite(propertyId).subscribe({
      next: () => {
        this.favorites = this.favorites.filter(p => p.id !== propertyId);
        this.metrics.totalFavorites = Math.max(0, this.metrics.totalFavorites - 1);
        this.showToast('Removed from saved properties');
      }
    });
  }

  cancelVisit(visitId: number): void {
    this.buyerService.cancelSiteVisit(visitId).subscribe({
      next: () => {
        const v = this.siteVisits.find(item => item.id === visitId);
        if (v) v.status = 'CANCELLED';
        this.showToast('Site visit cancelled');
      }
    });
  }

  clearHistory(): void {
    this.buyerService.clearRecentlyViewed().subscribe({
      next: () => {
        this.recentlyViewed = [];
        this.metrics.totalRecentlyViewed = 0;
        this.showToast('Recently viewed history cleared');
      }
    });
  }

  saveProfile(): void {
    this.buyerService.updateProfile(this.profile).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.profile = res.data;
          this.showToast('Profile and preferences updated successfully!');
        }
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  showToast(msg: string): void {
    this.toastMessage = msg;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }
}
