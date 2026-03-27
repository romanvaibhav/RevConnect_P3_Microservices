import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface Follow {
  id: number;
  followerId: number;
  followingId: number;
  status: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class FollowService {
  private apiUrl = `${environment.apiUrl}/follows`;

  constructor(private http: HttpClient, private authService: AuthService) {}

  // Sends a PENDING follow request
  followUser(followerId: number, followingId: number): Observable<Follow> {
    return this.http.post<Follow>(this.apiUrl, { followingId }).pipe(
      tap(() => {
        const username = this.authService.getCurrentUser()?.username || 'Someone';
        this.http.post(`${environment.apiUrl}/notifications`, {
          senderId: followerId,
          receiverId: followingId,
          type: 'FOLLOW',
          message: `${username} sent you a follow request`,
          postId: null
        }).subscribe({ error: () => {} });
      })
    );
  }

  unfollowUser(followerId: number, followingId: number): Observable<void> {
    return this.http.delete<void>(this.apiUrl, { params: { followingId } });
  }

  acceptFollow(followId: number): Observable<Follow> {
    return this.http.put<Follow>(`${this.apiUrl}/${followId}/accept`, {});
  }

  rejectFollow(followId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${followId}/reject`);
  }

  getPendingFollowRequests(): Observable<Follow[]> {
    return this.http.get<Follow[]>(`${this.apiUrl}/pending/received`);
  }

  getPendingFollowCount(): Observable<number> {
    return this.getPendingFollowRequests().pipe(map(r => r.length));
  }

  getFollowers(userId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/followers/${userId}`);
  }

  getFollowing(userId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/following/${userId}`);
  }

  checkFollowStatus(followerId: number, followingId: number): Observable<{ isFollowing: boolean }> {
    return this.http.get<boolean>(`${this.apiUrl}/check`, { params: { followerId, followingId } }).pipe(
      map(result => ({ isFollowing: result }))
    );
  }

  // Returns NONE | PENDING | ACCEPTED
  getFollowStatus(followerId: number, followingId: number): Observable<string> {
    return this.http.get<{ status: string }>(`${this.apiUrl}/status`, { params: { followerId, followingId } }).pipe(
      map(r => r.status)
    );
  }
}
