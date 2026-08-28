import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PropertyService } from '../../../core/services/property.service';
import { BuyerService } from '../../../core/services/buyer.service';
import { AuthService } from '../../../core/services/auth.service';
import { Property, PropertySearchRequest } from '../../../core/models/property.model';
import { PropertyMapComponent } from '../property-map/property-map.component';

@Component({
  selector: 'app-property-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, PropertyMapComponent],
  templateUrl: './property-list.component.html',
  styleUrls: ['./property-list.component.css']
})
export class PropertyListComponent implements OnInit {
  private propertyService = inject(PropertyService);
  private buyerService = inject(BuyerService);
  public authService = inject(AuthService);
  private router = inject(Router);

  properties: Property[] = [];
  totalProperties: number = 0;
  totalPages: number = 0;
  loading: boolean = false;

  // View state
  viewMode: 'grid' | 'list' | 'map' = 'grid';
  selectedForCompare: Property[] = [];

  // Filter Form Controls
  searchReq: PropertySearchRequest = {
    keyword: '',
    city: '',
    minPrice: undefined,
    maxPrice: undefined,
    propertyType: '',
    bhk: undefined,
    status: '',
    listingType: '',
    sortBy: 'newest',
    page: 0,
    size: 12
  };

  cities: string[] = ['All Cities', 'Hyderabad', 'Bangalore', 'Mumbai', 'Kochi', 'Delhi NCR', 'Pune', 'Chennai'];
  propertyTypes: string[] = ['All Types', 'APARTMENT', 'VILLA', 'PENTHOUSE', 'PLOT', 'COMMERCIAL'];
  bhkOptions: number[] = [1, 2, 3, 4, 5];
  statusOptions: string[] = ['All Statuses', 'READY_TO_MOVE', 'UNDER_CONSTRUCTION', 'NEW_LAUNCH'];

  // Message notifications
  toastMessage: string | null = null;

  ngOnInit(): void {
    this.loadProperties();
  }

  loadProperties(): void {
    this.loading = true;
    this.propertyService.searchProperties(this.searchReq).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.properties = res.data.content;
          this.totalProperties = res.data.totalElements;
          this.totalPages = res.data.totalPages;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load properties', err);
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.searchReq.page = 0;
    this.loadProperties();
  }

  resetFilters(): void {
    this.searchReq = {
      keyword: '',
      city: '',
      minPrice: undefined,
      maxPrice: undefined,
      propertyType: '',
      bhk: undefined,
      status: '',
      listingType: '',
      sortBy: 'newest',
      page: 0,
      size: 12
    };
    this.loadProperties();
  }

  onPageChange(page: number): void {
    this.searchReq.page = page;
    this.loadProperties();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  toggleFavorite(p: Property, event: Event): void {
    event.stopPropagation();
    if (!this.authService.isLoggedIn()) {
      this.showToast('Please login to save favorites.');
      this.router.navigate(['/auth/login']);
      return;
    }

    if (p.isFavorite) {
      this.buyerService.removeFavorite(p.id).subscribe({
        next: () => {
          p.isFavorite = false;
          this.showToast('Removed from saved properties.');
        }
      });
    } else {
      this.buyerService.addFavorite(p.id).subscribe({
        next: () => {
          p.isFavorite = true;
          this.showToast('Property added to favorites!');
        }
      });
    }
  }

  toggleCompare(p: Property, event: Event): void {
    event.stopPropagation();
    const idx = this.selectedForCompare.findIndex(item => item.id === p.id);
    if (idx > -1) {
      this.selectedForCompare.splice(idx, 1);
    } else {
      if (this.selectedForCompare.length >= 4) {
        this.showToast('You can compare a maximum of 4 properties side-by-side.');
        return;
      }
      this.selectedForCompare.push(p);
    }
  }

  isCompared(p: Property): boolean {
    return this.selectedForCompare.some(item => item.id === p.id);
  }

  goToCompare(): void {
    if (this.selectedForCompare.length < 2) {
      this.showToast('Please select at least 2 properties to compare.');
      return;
    }
    const ids = this.selectedForCompare.map(p => p.id).join(',');
    this.router.navigate(['/properties/compare'], { queryParams: { ids } });
  }

  showToast(msg: string): void {
    this.toastMessage = msg;
    setTimeout(() => {
      this.toastMessage = null;
    }, 3500);
  }
}
