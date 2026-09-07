export interface Property {
  id: number;
  title: string;
  description: string;
  propertyType: string; // APARTMENT, VILLA, PLOT, PENTHOUSE, COMMERCIAL
  listingType: string; // BUY, RENT
  price: number;
  bhk: number;
  bathrooms: number;
  areaSqft: number;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  latitude: number;
  longitude: number;
  status: string; // READY_TO_MOVE, UNDER_CONSTRUCTION, NEW_LAUNCH
  amenities: string;
  coverImageUrl: string;
  imageUrls: string;
  brochureUrl: string;
  builderId: number;
  builderName: string;
  avgRating: number;
  reviewCount: number;
  isFavorite?: boolean;
  createdAt: string;
}

export interface PropertySearchRequest {
  keyword?: string;
  city?: string;
  minPrice?: number;
  maxPrice?: number;
  propertyType?: string;
  bhk?: number;
  status?: string;
  listingType?: string;
  amenity?: string;
  sortBy?: string;
  page?: number;
  size?: number;
}

export interface PropertyReview {
  id: number;
  propertyId: number;
  userId: number;
  userFullName: string;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface CreateReviewRequest {
  rating: number;
  comment: string;
}

export interface PropertyInquiry {
  id: number;
  propertyId: number;
  propertyTitle: string;
  builderId: number;
  name: string;
  email: string;
  phone: string;
  subject?: string;
  message: string;
  status: string;
  replyMessage?: string;
  repliedAt?: string;
  createdAt: string;
}

export interface CreateInquiryRequest {
  propertyId: number;
  name: string;
  email: string;
  phone: string;
  subject?: string;
  message: string;
}

export interface ReplyInquiryRequest {
  replyMessage: string;
}

export interface SiteVisit {
  id: number;
  propertyId: number;
  propertyTitle: string;
  propertyAddress: string;
  coverImageUrl: string;
  visitDate: string;
  timeSlot: string;
  status: string;
  notes?: string;
  createdAt: string;
}

export interface CreateSiteVisitRequest {
  propertyId: number;
  visitDate: string;
  timeSlot: string;
  notes?: string;
}

export interface BuyerDashboardMetrics {
  totalFavorites: number;
  totalSiteVisits: number;
  totalInquiries: number;
  totalRecentlyViewed: number;
}

export interface BuyerProfile {
  id?: number;
  userId?: number;
  email?: string;
  phone?: string;
  fullName: string;
  city: string;
  budgetMin?: number;
  budgetMax?: number;
  preferredPropertyType?: string;
  preferredBhk?: number;
}

export interface CreatePropertyRequest {
  title: string;
  description?: string;
  propertyType: string;
  listingType: string;
  price: number;
  bhk?: number;
  bathrooms?: number;
  areaSqft?: number;
  address: string;
  city: string;
  state?: string;
  zipCode?: string;
  latitude?: number;
  longitude?: number;
  status: string;
  amenities?: string;
  coverImageUrl?: string;
  imageUrls?: string;
  brochureUrl?: string;
}

export interface UpdatePropertyRequest {
  title?: string;
  description?: string;
  propertyType?: string;
  listingType?: string;
  price?: number;
  bhk?: number;
  bathrooms?: number;
  areaSqft?: number;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  latitude?: number;
  longitude?: number;
  status?: string;
  amenities?: string;
  coverImageUrl?: string;
  imageUrls?: string;
  brochureUrl?: string;
}

export interface BuilderDashboardMetrics {
  totalProperties: number;
  activeListings: number;
  totalInquiries: number;
  pendingInquiries: number;
  totalSiteVisits: number;
  upcomingSiteVisits: number;
}

export interface BuilderProfile {
  id?: number;
  userId?: number;
  email?: string;
  phone?: string;
  companyName: string;
  contactPersonName: string;
  businessLicenseNumber?: string;
}

