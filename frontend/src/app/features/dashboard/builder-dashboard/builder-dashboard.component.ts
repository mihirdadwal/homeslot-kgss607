import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { TokenService } from '../../../core/services/token.service';
import { BuilderService } from '../../../core/services/builder.service';
import {
  Property,
  CreatePropertyRequest,
  UpdatePropertyRequest,
  PropertyInquiry,
  SiteVisit,
  BuilderDashboardMetrics,
  BuilderProfile
} from '../../../core/models/property.model';

import { NotificationCenterComponent } from '../../../shared/components/notification-center/notification-center.component';

@Component({
  selector: 'app-builder-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NotificationCenterComponent],
  templateUrl: './builder-dashboard.component.html',
  styleUrls: ['./builder-dashboard.component.css']
})
export class BuilderDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private tokenService = inject(TokenService);
  private builderService = inject(BuilderService);
  private router = inject(Router);

  user = this.tokenService.getUser();
  activeTab: 'overview' | 'properties' | 'inquiries' | 'site-visits' | 'profile' = 'overview';

  metrics: BuilderDashboardMetrics = {
    totalProperties: 0,
    activeListings: 0,
    totalInquiries: 0,
    pendingInquiries: 0,
    totalSiteVisits: 0,
    upcomingSiteVisits: 0
  };

  properties: Property[] = [];
  inquiries: PropertyInquiry[] = [];
  siteVisits: SiteVisit[] = [];
  profile: BuilderProfile = {
    companyName: '',
    contactPersonName: '',
    businessLicenseNumber: ''
  };

  // Filter state for inquiries
  inquiryFilter: 'ALL' | 'PENDING' | 'REPLIED' = 'ALL';

  // Notification / Alert message
  alertMessage: string | null = null;
  alertType: 'success' | 'danger' | 'info' = 'info';

  // Property Modal state
  showPropertyModal = false;
  isEditMode = false;
  selectedPropertyId: number | null = null;

  propertyForm: CreatePropertyRequest = {
    title: '',
    description: '',
    propertyType: 'APARTMENT',
    listingType: 'BUY',
    price: 0,
    bhk: 2,
    bathrooms: 2,
    areaSqft: 1200,
    address: '',
    city: 'Mumbai',
    state: 'Maharashtra',
    zipCode: '400001',
    latitude: 19.0760,
    longitude: 72.8777,
    status: 'READY_TO_MOVE',
    amenities: 'Gym, Swimming Pool, Parking, 24x7 Security, Power Backup',
    coverImageUrl: 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800',
    imageUrls: 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800,https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800',
    brochureUrl: ''
  };

  // Reply Inquiry Modal state
  showReplyModal = false;
  selectedInquiry: PropertyInquiry | null = null;
  replyMessageText = '';

  // Loading indicator
  isLoading = false;

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.isLoading = true;
    this.loadMetrics();
    this.loadProperties();
    this.loadInquiries();
    this.loadSiteVisits();
    this.loadProfile();
  }

  loadMetrics(): void {
    this.builderService.getDashboardMetrics().subscribe({
      next: (res) => {
        if (res.data) {
          this.metrics = res.data;
        }
      },
      error: (err) => console.error('Failed to load builder metrics', err)
    });
  }

  loadProperties(): void {
    this.builderService.getBuilderProperties().subscribe({
      next: (res) => {
        this.properties = res.data || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load properties', err);
        this.isLoading = false;
      }
    });
  }

  loadInquiries(): void {
    this.builderService.getBuilderInquiries().subscribe({
      next: (res) => {
        this.inquiries = res.data || [];
      },
      error: (err) => console.error('Failed to load inquiries', err)
    });
  }

  loadSiteVisits(): void {
    this.builderService.getBuilderSiteVisits().subscribe({
      next: (res) => {
        this.siteVisits = res.data || [];
      },
      error: (err) => console.error('Failed to load site visits', err)
    });
  }

  loadProfile(): void {
    this.builderService.getBuilderProfile().subscribe({
      next: (res) => {
        if (res.data) {
          this.profile = res.data;
        }
      },
      error: (err) => console.error('Failed to load builder profile', err)
    });
  }

  setActiveTab(tab: 'overview' | 'properties' | 'inquiries' | 'site-visits' | 'profile'): void {
    this.activeTab = tab;
  }

  // --- PROPERTY MODAL HANDLERS ---

  openAddPropertyModal(): void {
    this.isEditMode = false;
    this.selectedPropertyId = null;
    this.propertyForm = {
      title: '',
      description: '',
      propertyType: 'APARTMENT',
      listingType: 'BUY',
      price: 7500000,
      bhk: 3,
      bathrooms: 2,
      areaSqft: 1450,
      address: '',
      city: 'Mumbai',
      state: 'Maharashtra',
      zipCode: '400001',
      latitude: 19.0760,
      longitude: 72.8777,
      status: 'READY_TO_MOVE',
      amenities: 'Clubhouse, Swimming Pool, Gym, Children Play Area, Security',
      coverImageUrl: 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800',
      imageUrls: 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800',
      brochureUrl: ''
    };
    this.showPropertyModal = true;
  }

  openEditPropertyModal(prop: Property): void {
    this.isEditMode = true;
    this.selectedPropertyId = prop.id;
    this.propertyForm = {
      title: prop.title,
      description: prop.description,
      propertyType: prop.propertyType,
      listingType: prop.listingType,
      price: prop.price,
      bhk: prop.bhk,
      bathrooms: prop.bathrooms,
      areaSqft: prop.areaSqft,
      address: prop.address,
      city: prop.city,
      state: prop.state,
      zipCode: prop.zipCode,
      latitude: prop.latitude,
      longitude: prop.longitude,
      status: prop.status,
      amenities: prop.amenities,
      coverImageUrl: prop.coverImageUrl,
      imageUrls: prop.imageUrls,
      brochureUrl: prop.brochureUrl
    };
    this.showPropertyModal = true;
  }

  closePropertyModal(): void {
    this.showPropertyModal = false;
  }

  saveProperty(): void {
    if (!this.propertyForm.title || !this.propertyForm.price || !this.propertyForm.city) {
      this.showAlert('Please fill in required fields (Title, Price, City).', 'danger');
      return;
    }

    if (this.isEditMode && this.selectedPropertyId) {
      this.builderService.updateProperty(this.selectedPropertyId, this.propertyForm).subscribe({
        next: () => {
          this.showAlert('Property updated successfully!', 'success');
          this.closePropertyModal();
          this.loadAllData();
        },
        error: (err) => {
          console.error(err);
          this.showAlert('Failed to update property.', 'danger');
        }
      });
    } else {
      this.builderService.createProperty(this.propertyForm).subscribe({
        next: () => {
          this.showAlert('New property created successfully!', 'success');
          this.closePropertyModal();
          this.loadAllData();
        },
        error: (err) => {
          console.error(err);
          this.showAlert('Failed to create property.', 'danger');
        }
      });
    }
  }

  deleteProperty(id: number, title: string): void {
    if (confirm(`Are you sure you want to delete "${title}"?`)) {
      this.builderService.deleteProperty(id).subscribe({
        next: () => {
          this.showAlert('Property deleted successfully.', 'info');
          this.loadAllData();
        },
        error: (err) => {
          console.error(err);
          this.showAlert('Failed to delete property.', 'danger');
        }
      });
    }
  }

  // --- REPLY INQUIRY MODAL HANDLERS ---

  openReplyModal(inquiry: PropertyInquiry): void {
    this.selectedInquiry = inquiry;
    this.replyMessageText = inquiry.replyMessage || '';
    this.showReplyModal = true;
  }

  closeReplyModal(): void {
    this.showReplyModal = false;
    this.selectedInquiry = null;
    this.replyMessageText = '';
  }

  sendReply(): void {
    if (!this.selectedInquiry || !this.replyMessageText.trim()) {
      this.showAlert('Reply message cannot be empty.', 'danger');
      return;
    }

    this.builderService.replyInquiry(this.selectedInquiry.id, { replyMessage: this.replyMessageText.trim() }).subscribe({
      next: () => {
        this.showAlert('Response sent to lead successfully!', 'success');
        this.closeReplyModal();
        this.loadInquiries();
        this.loadMetrics();
      },
      error: (err) => {
        console.error(err);
        this.showAlert('Failed to send reply.', 'danger');
      }
    });
  }

  // --- SITE VISIT STATUS UPDATE ---

  updateVisitStatus(visitId: number, status: string): void {
    this.builderService.updateSiteVisitStatus(visitId, status).subscribe({
      next: () => {
        this.showAlert(`Site visit updated to ${status}.`, 'success');
        this.loadSiteVisits();
        this.loadMetrics();
      },
      error: (err) => {
        console.error(err);
        this.showAlert('Failed to update visit status.', 'danger');
      }
    });
  }

  // --- PROFILE UPDATE ---

  saveProfile(): void {
    this.builderService.updateBuilderProfile(this.profile).subscribe({
      next: (res) => {
        if (res.data) {
          this.profile = res.data;
          this.showAlert('Builder profile saved successfully!', 'success');
        }
      },
      error: (err) => {
        console.error(err);
        this.showAlert('Failed to update profile.', 'danger');
      }
    });
  }

  // --- HELPERS ---

  get filteredInquiries(): PropertyInquiry[] {
    if (this.inquiryFilter === 'PENDING') {
      return this.inquiries.filter(i => i.status === 'PENDING');
    }
    if (this.inquiryFilter === 'REPLIED') {
      return this.inquiries.filter(i => i.status === 'REPLIED');
    }
    return this.inquiries;
  }

  showAlert(message: string, type: 'success' | 'danger' | 'info'): void {
    this.alertMessage = message;
    this.alertType = type;
    setTimeout(() => {
      this.alertMessage = null;
    }, 4000);
  }

  formatStatus(status: string): string {
    return status ? status.replace(/_/g, ' ') : '';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
