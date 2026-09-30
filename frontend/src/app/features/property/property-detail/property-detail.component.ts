import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PropertyService } from '../../../core/services/property.service';
import { BuyerService } from '../../../core/services/buyer.service';
import { AuthService } from '../../../core/services/auth.service';
import {
  Property,
  PropertyReview,
  CreateReviewRequest,
  CreateInquiryRequest,
  CreateSiteVisitRequest
} from '../../../core/models/property.model';

import { ChatModalComponent } from '../../../shared/components/chat-modal/chat-modal.component';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ChatModalComponent],
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css']
})
export class PropertyDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private propertyService = inject(PropertyService);
  private buyerService = inject(BuyerService);
  public authService = inject(AuthService);

  propertyId!: number;
  property: Property | null = null;
  reviews: PropertyReview[] = [];
  loading: boolean = true;
  activeImage: string = '';

  // Modals state
  showVisitModal: boolean = false;
  showInquiryModal: boolean = false;
  showShareModal: boolean = false;
  showChatModal: boolean = false;
  toastMessage: string | null = null;

  // Forms
  visitForm: CreateSiteVisitRequest = {
    propertyId: 0,
    visitDate: '',
    timeSlot: '10:00 AM - 11:00 AM',
    notes: ''
  };

  timeSlots: string[] = [
    '09:00 AM - 10:00 AM',
    '10:00 AM - 11:00 AM',
    '11:00 AM - 12:00 PM',
    '02:00 PM - 03:00 PM',
    '04:00 PM - 05:00 PM',
    '05:00 PM - 06:00 PM'
  ];

  inquiryForm: CreateInquiryRequest = {
    propertyId: 0,
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: ''
  };

  newReview: CreateReviewRequest = {
    rating: 5,
    comment: ''
  };

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.propertyId = +params['id'];
      if (this.propertyId) {
        this.loadPropertyDetails();
        this.loadReviews();
      }
    });

    // Set today as min visit date
    const today = new Date().toISOString().split('T')[0];
    this.visitForm.visitDate = today;
  }

  loadPropertyDetails(): void {
    this.loading = true;
    this.propertyService.getPropertyById(this.propertyId).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.property = res.data;
          this.activeImage = this.property.coverImageUrl;
          this.visitForm.propertyId = this.property.id;
          this.inquiryForm.propertyId = this.property.id;
          this.inquiryForm.subject = `Inquiry regarding ${this.property.title}`;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadReviews(): void {
    this.propertyService.getPropertyReviews(this.propertyId).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.reviews = res.data;
        }
      }
    });
  }

  get amenitiesList(): string[] {
    if (!this.property?.amenities) return [];
    return this.property.amenities.split(',').map(a => a.trim());
  }

  get galleryImages(): string[] {
    if (!this.property) return [];
    const list = [this.property.coverImageUrl];
    if (this.property.imageUrls) {
      const split = this.property.imageUrls.split(',').map(u => u.trim());
      split.forEach(u => {
        if (u && !list.includes(u)) list.push(u);
      });
    }
    return list;
  }

  toggleFavorite(): void {
    if (!this.authService.isLoggedIn()) {
      this.showToast('Please login to save property.');
      this.router.navigate(['/auth/login']);
      return;
    }

    if (!this.property) return;

    if (this.property.isFavorite) {
      this.buyerService.removeFavorite(this.property.id).subscribe({
        next: () => {
          this.property!.isFavorite = false;
          this.showToast('Removed from saved properties.');
        }
      });
    } else {
      this.buyerService.addFavorite(this.property.id).subscribe({
        next: () => {
          this.property!.isFavorite = true;
          this.showToast('Property added to favorites!');
        }
      });
    }
  }

  submitSiteVisit(): void {
    if (!this.authService.isLoggedIn()) {
      this.showToast('Please login to schedule site visit.');
      this.router.navigate(['/auth/login']);
      return;
    }

    this.buyerService.scheduleSiteVisit(this.visitForm).subscribe({
      next: (res) => {
        if (res.success) {
          this.showToast('Site visit scheduled successfully!');
          this.showVisitModal = false;
        }
      },
      error: (err) => {
        this.showToast(err.error?.message || 'Failed to schedule visit.');
      }
    });
  }

  submitInquiry(): void {
    if (!this.authService.isLoggedIn()) {
      this.showToast('Please login to contact builder.');
      this.router.navigate(['/auth/login']);
      return;
    }

    this.buyerService.createInquiry(this.inquiryForm).subscribe({
      next: (res) => {
        if (res.success) {
          this.showToast('Inquiry sent to builder successfully!');
          this.showInquiryModal = false;
        }
      },
      error: (err) => {
        this.showToast(err.error?.message || 'Failed to send inquiry.');
      }
    });
  }

  submitReview(): void {
    if (!this.authService.isLoggedIn()) {
      this.showToast('Please login to post a review.');
      this.router.navigate(['/auth/login']);
      return;
    }

    if (!this.newReview.comment.trim()) {
      this.showToast('Please write a review comment.');
      return;
    }

    this.propertyService.addReview(this.propertyId, this.newReview).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.showToast('Review submitted successfully!');
          this.reviews.unshift(res.data);
          this.newReview.comment = '';
          this.loadPropertyDetails(); // refresh rating count
        }
      }
    });
  }

  downloadBrochure(): void {
    const url = this.propertyService.getBrochureUrl(this.propertyId);
    window.open(url, '_blank');
  }

  copyLink(): void {
    navigator.clipboard.writeText(window.location.href);
    this.showToast('Listing URL copied to clipboard!');
    this.showShareModal = false;
  }

  shareWhatsApp(): void {
    const text = encodeURIComponent(`Check out this property on HomeSlot: ${this.property?.title} - ${window.location.href}`);
    window.open(`https://api.whatsapp.com/send?text=${text}`, '_blank');
  }

  showToast(msg: string): void {
    this.toastMessage = msg;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }
}
