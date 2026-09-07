# Phase Status

## Completed phases

### Phase 1 – Requirement Analysis & Technical Planning
- **Dates**: 09 Aug 2026 – 13 Aug 2026
- **Status**: Completed, submitted, awaiting coordinator sign-off
- **Deliverables**:
  - `docs/phase1/Phase1_Technical_Requirement_Analysis_Mihir_Dadwal.docx`
  - `docs/phase1/Weekly_Status_Report_Week1_Mihir_Dadwal.docx`

### Phase 2 – Authentication & User Management
- **Dates**: 14 Aug 2026 – 18 Aug 2026
- **Status**: Completed, fully tested & verified
- **Deliverables**:
  - `backend/` — Spring Boot 3.x Auth API (JWT 15-min access + 7-day refresh rotation, BCrypt cost 12, Redis OTP, RBAC)
  - `frontend/` — Angular 17+ Auth UI (Buyer & Builder Registration, Multi-role Login, OTP Modal, Forgot/Reset Password, RBAC guards)
  - `docs/phase2/Weekly_Status_Report_Week2_Mihir_Dadwal.docx` — Week 2 status report submitted to Vaishali Pujari
  - `docs/phase2/screenshots/` — Visual proof of UI screens, Postman API executions, and DB tables

### Phase 3 – Buyer & Property Search Module
- **Dates**: 19 Aug 2026 – 25 Aug 2026
- **Status**: Completed, fully tested & verified
- **Deliverables**:
  - `backend/` — Spring Boot 3.x Property & Buyer APIs (Multi-criteria search JPA Specifications, Favorites, Recently Viewed, Reviews, Builder Inquiries, Site Visit Booking, Brochure HTML/PDF download, Seed Initializer)
  - `frontend/` — Angular 17+ Property UI (`PropertyListComponent` with Grid/List/Map toggle, `PropertyDetailComponent` with gallery, brochure, site visit & builder inquiry modals, review submission, `PropertyCompareComponent` matrix, Leaflet `PropertyMapComponent`, and upgraded `BuyerDashboardComponent` portal)
  - `docs/phase3/Weekly_Status_Report_Week3_Mihir_Dadwal.md` — Week 3 status report

## Active phase

### Phase 4 – Builder & Property Management Module
- **Dates**: 26 Aug 2026 – 01 Sep 2026
- **Status**: Completed, fully tested & verified
- **Deliverables**:
  - `backend/` — Spring Boot 3.x Builder APIs (`/api/v1/builder/**` protected by `@PreAuthorize("hasAnyAuthority('ROLE_BUILDER', 'ROLE_ADMIN')")`, Property creation/editing/deletion, Builder Inquiry replies, Site visit status updates, Builder Profile management)
  - `frontend/` — Angular 17+ Builder Portal (`BuilderDashboardComponent` with Overview metrics, My Properties inventory grid, Leads & Inquiries reply modal, Site Visit agenda list, Company Profile settings, and Add/Edit Property Modal)
  - `docs/phase4/Weekly_Status_Report_Week4_Mihir_Dadwal.md` & `.docx` — Week 4 status report submitted to Vaishali Pujari
  - `docs/phase4/screenshots/` — Visual proof of Builder Portal dashboard & recorded WebP demo

## Active phase

### Phase 5 – Admin, Approval & Platform Management Module
- **Dates**: 02 Sep 2026 – 08 Sep 2026
- **Scope** (from Task Assignment):
  - Admin Dashboard & System Analytics Overview
  - Builder KYC & Account Verification Workflow
  - Property Listing Approval / Rejection Queue
  - User & Role Access Management (Buyers, Builders, Admins)
  - Audit Logging & Platform Compliance Reports

## Upcoming phases (from Task Assignment)

- **Phase 6** (09–14 Sep): Communication, Admin Panel & Testing
- **Phase 7** (15–19 Sep): Final Integration, Deployment & Handover

## Monthly reports submitted
(None yet. First monthly report due after Week 4 wraps.)

## Decisions log
- **Phase 2 Monorepo**: Monorepo selected for atomic commit history between Spring Boot backend and Angular 17 frontend.
- **BCrypt Security**: Password hashing enforced at cost factor 12.
- **Token Policy**: 15-min JWT access token expiration with 7-day refresh token rotation.
- **Phase 3 Leaflet Integration**: Integrated OpenStreetMap & Leaflet JS for property map pins and location preview cards without external key dependencies.