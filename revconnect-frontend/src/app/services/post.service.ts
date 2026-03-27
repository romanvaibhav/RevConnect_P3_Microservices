import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Post } from '../models/post.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}

  // POST /api/posts
  createPost(post: Post & { hashtags?: string[]; scheduledAt?: string; taggedProductIds?: number[] }): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, {
      content: post.content,
      postType: post.postType || 'REGULAR',
      callToAction: post.callToAction || null,
      mediaPath: post.mediaPath || null,
      hashtags: post.hashtags || null,
      scheduledAt: post.scheduledAt || null,
      taggedProductIds: post.taggedProductIds || null
    });
  }

  // GET /api/posts/{postId}
  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  // GET /api/posts/user/{userId}
  getPostsByUser(userId: number): Observable<Post[]> {
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`).pipe(
      map(r => r.content || r)
    );
  }

  // PUT /api/posts/{postId}
  updatePost(id: number, content: string, hashtags?: string[]): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, { content, hashtags: hashtags || null });
  }

  // DELETE /api/posts/{postId}
  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // POST /api/posts/{postId}/share
  sharePost(postId: number): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/${postId}/share`, {});
  }

  // POST /api/posts/{postId}/pin
  pinPost(postId: number): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/${postId}/pin`, {});
  }

  // DELETE /api/posts/{postId}/pin
  unpinPost(postId: number): Observable<Post> {
    return this.http.delete<Post>(`${this.apiUrl}/${postId}/pin`);
  }

  // GET /api/posts/scheduled
  getScheduledPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/scheduled`);
  }

  // GET /api/posts/pinned/{userId}
  getPinnedPosts(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/pinned/${userId}`);
  }

  // ---- Search (users) ----
  searchUsers(keyword: string): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/users/search`, { params: { query: keyword } });
  }

  // ---- File upload ----
  uploadPostMedia(file: File): Observable<{ mediaPath: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ mediaPath: string }>(`${this.apiUrl}/upload-media`, formData);
  }

  uploadFile(file: File): Observable<any> {
    return this.uploadPostMedia(file);
  }
}
