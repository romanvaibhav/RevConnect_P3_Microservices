/*
11. **scheduled-post.service.ts** - Scheduled Post Service

**Purpose:** Handles post scheduling

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `schedulePost(post)` | Schedule a post | POST `/api/scheduled-posts` |
| `getMyScheduledPosts()` | Get my scheduled posts | GET `/api/scheduled-posts` |
| `deleteScheduledPost(id)` | Cancel scheduled post | DELETE `/api/scheduled-posts/{id}` |

**Used By:** FeedComponent

*/import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ScheduledPost {
  id?: number;
  userId?: number;
  content: string;
  scheduledTime: string;
  posted?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ScheduledPostService {
  private apiUrl = `${environment.apiUrl}/scheduled-posts`;

  constructor(private http: HttpClient) {}

  schedulePost(post: ScheduledPost): Observable<ScheduledPost> {
    return this.http.post<ScheduledPost>(this.apiUrl, post);
  }

  getMyScheduledPosts(): Observable<ScheduledPost[]> {
    return this.http.get<ScheduledPost[]>(this.apiUrl);
  }

  deleteScheduledPost(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
