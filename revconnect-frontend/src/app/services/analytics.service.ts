import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PostAnalytics {
  id: number;
  postId: number;
  authorId?: number;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  engagementRate?: number;
  lastUpdated?: string;
}

export interface AnalyticsSummary {
  totalPosts: number;
  totalViews: number;
  totalLikes: number;
  totalComments: number;
  totalShares: number;
  totalEngagements?: number;
  avgEngagementRate?: number;
}

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private apiUrl = `${environment.apiUrl}/analytics`;

  constructor(private http: HttpClient) {}

  getPostAnalytics(postId: number): Observable<PostAnalytics> {
    return this.http.get<PostAnalytics>(`${this.apiUrl}/post/${postId}`);
  }

  getAllAnalytics(): Observable<PostAnalytics[]> {
    return this.http.get<PostAnalytics[]>(`${this.apiUrl}/all`);
  }

  getAnalyticsSummary(): Observable<AnalyticsSummary> {
    return this.http.get<AnalyticsSummary>(`${this.apiUrl}/summary`);
  }

  getAnalyticsByUser(userId: number): Observable<PostAnalytics[]> {
    return this.http.get<PostAnalytics[]>(`${this.apiUrl}/user/${userId}/posts`);
  }

  getUserSummary(userId: number): Observable<AnalyticsSummary> {
    return this.http.get<AnalyticsSummary>(`${this.apiUrl}/user/${userId}/summary`);
  }

  trackEvent(postId: number, eventType: string, authorId?: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/track`, { postId, eventType, authorId });
  }
}
