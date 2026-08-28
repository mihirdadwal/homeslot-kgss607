import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    const expectedRoles = route.data['roles'] as Array<string>;
    if (expectedRoles && expectedRoles.length > 0) {
      const userRole = authService.getUserRole();
      if (userRole && expectedRoles.includes(userRole)) {
        return true;
      } else {
        // Redirect unauthorized access to home/login
        router.navigate(['/auth/login']);
        return false;
      }
    }
    return true;
  }

  router.navigate(['/auth/login']);
  return false;
};
