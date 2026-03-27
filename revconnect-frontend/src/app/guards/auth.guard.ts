import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Functional Route Guard (Angular 15+ style)
// This guard protects routes that require authentication
export const authGuard: CanActivateFn = (route, state) => {

  // Inject required services manually (since this is not a class)
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user is logged in
  if (authService.isAuthenticated()) {
    // Allow access to the route
    return true;
  }

  // If not authenticated → redirect to login page
  router.navigate(['/login']);

  // Block access to the requested route
  return false;
};