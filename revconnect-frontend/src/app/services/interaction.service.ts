import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Comment } from '../models/post.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class InteractionService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ---- Likes ----

  likePost(postId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/likes`, {}, { params: { postId } });
  }

  unlikePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/likes`, { params: { postId } });
  }

  getLikeCount(postId: number): Observable<number> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/likes/post/${postId}/count`).pipe(
      map(r => r.count)
    );
  }

  isLikedByUser(postId: number): Observable<boolean> {
    return this.http.get<{ liked: boolean }>(`${this.baseUrl}/likes/post/${postId}/user`).pipe(
      map(r => r.liked)
    );
  }

  // ---- Comments ----

  addComment(comment: Comment): Observable<Comment> {
    return this.http.post<Comment>(`${this.baseUrl}/comments`, {
      postId: comment.postId,
      content: comment.content
    });
  }

  getCommentsByPost(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/comments/post/${postId}`);
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/comments/${id}`);
  }

  getCommentCount(postId: number): Observable<number> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/comments/post/${postId}/count`).pipe(
      map(r => r.count)
    );
  }
}
