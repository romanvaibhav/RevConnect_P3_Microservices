/*
"The Angular AuthService sends a POST request to the backend:"

login(credentials): Observable<AuthResponse> {
  return this.http.post(`/api/auth/login`, credentials).pipe(
    tap(response => this.handleAuthResponse(response))
  )
}
"On success, handleAuthResponse() stores the token and user in localStorage and sets the authenticated state."
*/



import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { User, LoginRequest, AuthResponse } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl = `${environment.apiUrl}/auth`;
  private usersUrl = `${environment.apiUrl}/users`;

  currentUser = signal<User | null>(null);
  isAuthenticated = signal<boolean>(false);

  constructor(private http: HttpClient, private router: Router) {
    this.loadUserFromStorage();
  }

  // POST /api/users/register  → UserDTO (no token — redirect to login after)
  register(user: User): Observable<any> {
    const payload = {
      name: user.name,
      username: user.username,
      email: user.email,
      password: user.password,
      role: user.role,
      bio: user.bio || undefined,
      location: user.location || undefined,
      website: user.website || undefined
    };
    return this.http.post<any>(`${this.usersUrl}/register`, payload);
  }

  // POST /api/auth/login  → AuthResponse { token, user }
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authUrl}/login`, credentials).pipe(
      tap(response => this.handleAuthResponse(response))
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUserId(): number | null {
    const id = this.currentUser()?.id;
    return id != null ? Number(id) : null;
  }

  getCurrentUser(): User | null {
    return this.currentUser();
  }

  getUserRole(): string | null {
    return this.currentUser()?.role || null;
  }

  isCreatorOrBusiness(): boolean {
    const role = this.getUserRole();
    return role === 'CREATOR' || role === 'BUSINESS';
  }

  private handleAuthResponse(response: AuthResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('user', JSON.stringify(response.user));
    this.currentUser.set(response.user);
    this.isAuthenticated.set(true);

    // Migrate pending avatar (set during registration before userId was known)
    const userId = response.user?.id;
    const username = response.user?.username;
    if (userId && username) {
      const pendingKey = 'rc_avatar_pending_' + username;
      const pending = localStorage.getItem(pendingKey);
      if (pending) {
        localStorage.setItem('rc_avatar_' + userId, pending);
        localStorage.removeItem(pendingKey);
      }
    }
  }

  private loadUserFromStorage(): void {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    if (token && userStr) {
      try {
        this.currentUser.set(JSON.parse(userStr));
        this.isAuthenticated.set(true);
      } catch {
        this.logout();
      }
    }
  }
}
