import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/pages/login/login.component';
import { RegisterBuyerComponent } from './features/auth/pages/register-buyer/register-buyer.component';
import { RegisterBuilderComponent } from './features/auth/pages/register-builder/register-builder.component';
import { ForgotPasswordComponent } from './features/auth/pages/forgot-password/forgot-password.component';
import { BuyerDashboardComponent } from './features/dashboard/buyer-dashboard/buyer-dashboard.component';
import { BuilderDashboardComponent } from './features/dashboard/builder-dashboard/builder-dashboard.component';
import { AdminDashboardComponent } from './features/dashboard/admin-dashboard/admin-dashboard.component';
import { PropertyListComponent } from './features/property/property-list/property-list.component';
import { PropertyDetailComponent } from './features/property/property-detail/property-detail.component';
import { PropertyCompareComponent } from './features/property/property-compare/property-compare.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'properties', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register-buyer', component: RegisterBuyerComponent },
  { path: 'auth/register-builder', component: RegisterBuilderComponent },
  { path: 'auth/forgot-password', component: ForgotPasswordComponent },

  // Property Search & Showcase Routes
  { path: 'properties', component: PropertyListComponent },
  { path: 'properties/compare', component: PropertyCompareComponent },
  { path: 'properties/:id', component: PropertyDetailComponent },

  // Role-Protected Dashboard Shells
  {
    path: 'dashboard/buyer',
    component: BuyerDashboardComponent,
    canActivate: [authGuard],
    data: { roles: ['ROLE_BUYER'] }
  },
  {
    path: 'dashboard/builder',
    component: BuilderDashboardComponent,
    canActivate: [authGuard],
    data: { roles: ['ROLE_BUILDER'] }
  },
  {
    path: 'dashboard/admin',
    component: AdminDashboardComponent,
    canActivate: [authGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },

  { path: '**', redirectTo: 'properties' }
];
