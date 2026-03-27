/*
3. **user.service.ts** - User Management Service

**Purpose:** Handles user profile operations

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `getAllUsers()` | Get all users | GET `/api/users` |
| `getUserById(id)` | Get user by ID | GET `/api/users/{id}` |
| `updateUser(id, user)` | Update user profile | PUT `/api/users/{id}` |
| `deleteUser(id)` | Delete user account | DELETE `/api/users/{id}` |
| `searchUsers(query)` | Search users | GET `/api/search/users` |

**How It Works:**
```typescript
// Get user profile
getUserById(id) {
  1. Send GET request to backend
  2. Backend queries database
  3. Return user data
  4. Component displays profile
}
```

**Used By:** ProfileComponent, SearchComponent, ConnectionsComponent

**Example Usage:**
```typescript
// In profile.component.ts
this.userService.getUserById(userId).subscribe(user => {
  this.userProfile = user;
  this.displayProfile();
});


*/



import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  // GET /api/users/{userId}/profile → { user, creatorProfile?, businessProfile? }
  getUserProfile(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/profile`);
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }

  searchUsers(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/users/search`, { params: { query } });
  }

  changePassword(userId: number, currentPassword: string, newPassword: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}/change-password`, { currentPassword, newPassword });
  }
}
