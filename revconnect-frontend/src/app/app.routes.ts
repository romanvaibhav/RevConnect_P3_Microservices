import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/landing/landing.component').then(m => m.LandingComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'profile-setup',
    loadComponent: () => import('./components/profile-setup/profile-setup.component').then(m => m.ProfileSetupComponent),
    canActivate: [authGuard]
  },
  {
    path: 'feed',
    loadComponent: () => import('./components/feed/feed.component').then(m => m.FeedComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile/:id',
    loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  {
    path: 'connections',
    loadComponent: () => import('./components/connections/connections.component').then(m => m.ConnectionsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'notifications',
    loadComponent: () => import('./components/notifications/notifications.component').then(m => m.NotificationsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'search',
    loadComponent: () => import('./components/search/search.component').then(m => m.SearchComponent),
    canActivate: [authGuard]
  },
  {
    path: 'analytics',
    loadComponent: () => import('./components/analytics/analytics.component').then(m => m.AnalyticsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'business/:userId',
    loadComponent: () => import('./components/business-page/business-page.component').then(m => m.BusinessPageComponent),
    canActivate: [authGuard]
  },
  {
    path: 'business-dashboard',
    loadComponent: () => import('./components/business-dashboard/business-dashboard.component').then(m => m.BusinessDashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'saved',
    loadComponent: () => import('./components/saved-posts/saved-posts.component').then(m => m.SavedPostsComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];

//This file defines the navigation structure and route protection of the entire Angular application.
