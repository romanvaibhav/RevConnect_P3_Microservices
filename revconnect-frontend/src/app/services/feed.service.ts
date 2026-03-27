import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of, defer } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { Post } from '../models/post.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';
import { PostService } from './post.service';

@Injectable({ providedIn: 'root' })
export class FeedService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private postService: PostService
  ) {}

  /**
   * Build feed client-side:
   * 1. Fetch accepted following IDs from social-service
   * 2. Fetch own posts + each followed user's posts from post-service
   * 3. Merge, deduplicate, sort by createdAt desc
   *
   * Uses defer() so userId is read at subscription time (handles page refresh).
   */
  getFeed(postType?: string): Observable<Post[]> {
    return defer(() => {
      const userId = this.authService.getCurrentUserId();
      if (!userId) return of([] as Post[]);

      // Get following IDs (ACCEPTED only)
      return this.http.get<number[]>(`${this.apiUrl}/follows/following/${userId}`).pipe(
        catchError(() => of([] as number[])),
        switchMap((followingIds: number[]) => {
          const authorIds = [...new Set([userId, ...followingIds])];

          // Fetch posts for all author IDs in parallel using PostService
          const requests = authorIds.map(id =>
            this.postService.getPostsByUser(id).pipe(
              catchError(() => of([] as Post[]))
            )
          );

          return forkJoin(requests).pipe(
            map((results: Post[][]) => {
              // Flatten
              let all: Post[] = results.flat();

              // Filter by postType if set
              if (postType) {
                all = all.filter(p => p.postType === postType.toUpperCase());
              }

              // Deduplicate by id
              const seen = new Set<number>();
              all = all.filter(p => {
                if (p.id == null || seen.has(p.id)) return false;
                seen.add(p.id);
                return true;
              });

              // Sort by createdAt descending
              all.sort((a, b) => {
                const ta = a.createdAt ? new Date(a.createdAt + (a.createdAt.endsWith('Z') ? '' : 'Z')).getTime() : 0;
                const tb = b.createdAt ? new Date(b.createdAt + (b.createdAt.endsWith('Z') ? '' : 'Z')).getTime() : 0;
                return tb - ta;
              });

              return all;
            })
          );
        })
      );
    });
  }

  // GET /api/feed/hashtag/{tag} — still uses feed-service for hashtag search
  searchByHashtag(tag: string): Observable<Post[]> {
    const clean = tag.startsWith('#') ? tag.substring(1) : tag;
    return this.http.get<Post[]>(`${this.apiUrl}/feed/hashtag/${clean}`).pipe(
      catchError(() => of([] as Post[]))
    );
  }

  // GET /api/posts/search?keyword=...
  searchPosts(keyword: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts/search`, { params: { keyword } }).pipe(
      catchError(() => of([] as Post[]))
    );
  }

  // GET /api/feed/trending-hashtags?limit=10
  getTrendingHashtags(limit = 10): Observable<{ name: string; count: number }[]> {
    return this.http.get<{ name: string; count: number }[]>(
      `${this.apiUrl}/feed/trending-hashtags`, { params: { limit } }
    ).pipe(catchError(() => of([])));
  }
}
