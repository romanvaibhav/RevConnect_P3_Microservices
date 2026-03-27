import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Post, Comment } from '../../models/post.model';
import { PostService } from '../../services/post.service';
import { InteractionService } from '../../services/interaction.service';
import { AuthService } from '../../services/auth.service';
import { AnalyticsService } from '../../services/analytics.service';
import { SavedPostsService } from '../../services/saved-posts.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.css']
})
export class PostCardComponent implements OnInit {
  @Input() post!: Post;
  @Output() postDeleted = new EventEmitter<number>();
  @Output() postUpdated = new EventEmitter<Post>();
  @Output() postShared = new EventEmitter<Post>();

  comments: Comment[] = [];
  newCommentContent = '';
  showComments = false;

  likeCount = 0;
  commentCount = 0;
  isLiked = false;
  isLikeLoading = false;

  isEditing = false;
  editedContent = '';
  editedHashtags = '';

  shareSuccess = false;
  shareError = '';
  isSaved = false;

  constructor(
    private postService: PostService,
    private interactionService: InteractionService,
    public authService: AuthService,
    private analyticsService: AnalyticsService,
    private savedPostsService: SavedPostsService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    if (!this.post.id) return;
    this.isSaved = this.savedPostsService.isSaved(this.post.id);

    // Fetch like count, comment count, liked status, and comments all at once on load
    forkJoin({
      likeCount: this.interactionService.getLikeCount(this.post.id).pipe(catchError(() => of(0))),
      commentCount: this.interactionService.getCommentCount(this.post.id).pipe(catchError(() => of(0))),
      isLiked: this.interactionService.isLikedByUser(this.post.id).pipe(catchError(() => of(false))),
      comments: this.interactionService.getCommentsByPost(this.post.id).pipe(catchError(() => of([] as Comment[])))
    }).subscribe({
      next: ({ likeCount, commentCount, isLiked, comments }) => {
        this.likeCount = likeCount as number;
        this.commentCount = commentCount as number;
        this.isLiked = isLiked as boolean;
        this.comments = comments as Comment[];
      },
      error: () => {}
    });

    // Track view event for analytics (fire-and-forget)
    if (!this.isOwnPost && this.post.authorId) {
      this.analyticsService.trackEvent(this.post.id, 'VIEW', this.post.authorId).subscribe({ error: () => {} });
    }
  }

  get isOwnPost(): boolean {
    return Number(this.authService.getCurrentUserId()) === Number(this.post.authorId);
  }

  get isAdvancedUser(): boolean {
    const role = this.authService.getCurrentUser()?.role;
    return role === 'CREATOR' || role === 'BUSINESS';
  }

  get isScheduled(): boolean {
    return !!(this.post.scheduledAt && !this.post.isPublished);
  }

  toggleLike(): void {
    if (this.isLikeLoading) return;
    this.isLikeLoading = true;
    const currentUserId = this.authService.getCurrentUserId();

    if (this.isLiked) {
      this.interactionService.unlikePost(this.post.id!).subscribe({
        next: () => {
          this.isLiked = false;
          this.likeCount = Math.max(0, this.likeCount - 1);
          this.isLikeLoading = false;
          if (this.post.authorId) {
            this.analyticsService.trackEvent(this.post.id!, 'UNLIKE', this.post.authorId).subscribe({ error: () => {} });
          }
        },
        error: () => { this.isLikeLoading = false; }
      });
    } else {
      this.interactionService.likePost(this.post.id!).subscribe({
        next: () => {
          this.isLiked = true;
          this.likeCount++;
          this.isLikeLoading = false;
          if (this.post.authorId) {
            this.analyticsService.trackEvent(this.post.id!, 'LIKE', this.post.authorId).subscribe({ error: () => {} });
          }
          if (currentUserId && this.post.authorId && Number(currentUserId) !== Number(this.post.authorId)) {
            this.sendNotification(this.post.authorId, 'LIKE',
              `${this.authService.getCurrentUser()?.username || 'Someone'} liked your post`, this.post.id);
          }
        },
        error: () => { this.isLikeLoading = false; }
      });
    }
  }

  // Toggle comment section visibility — comments are already loaded on init
  toggleComments(): void {
    this.showComments = !this.showComments;
  }

  addComment(): void {
    if (!this.newCommentContent.trim()) return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.interactionService.addComment({ content: this.newCommentContent, userId, postId: this.post.id! }).subscribe({
      next: (c) => {
        this.comments.unshift(c);
        this.newCommentContent = '';
        this.commentCount++;
        if (this.post.authorId) {
          this.analyticsService.trackEvent(this.post.id!, 'COMMENT', this.post.authorId).subscribe({ error: () => {} });
        }
        if (this.post.authorId && Number(userId) !== Number(this.post.authorId)) {
          this.sendNotification(this.post.authorId, 'COMMENT',
            `${this.authService.getCurrentUser()?.username || 'Someone'} commented on your post`, this.post.id);
        }
      },
      error: () => {}
    });
  }

  deleteComment(id: number | undefined): void {
    if (!id || !confirm('Delete this comment?')) return;
    this.interactionService.deleteComment(id).subscribe({
      next: () => {
        this.comments = this.comments.filter(c => c.id !== id);
        this.commentCount = Math.max(0, this.commentCount - 1);
      },
      error: () => {}
    });
  }

  canDeleteComment(c: Comment): boolean {
    return this.authService.getCurrentUserId() === c.userId;
  }

  startEdit(): void {
    this.isEditing = true;
    this.editedContent = this.post.content;
    this.editedHashtags = (this.post.hashtags || []).map(h => h.replace('#', '')).join(', ');
  }

  cancelEdit(): void { this.isEditing = false; }

  saveEdit(): void {
    if (!this.editedContent.trim()) return;
    const tags = this.editedHashtags
      .split(/[\s,]+/)
      .map(t => t.trim())
      .filter(t => t.length > 0)
      .map(t => t.startsWith('#') ? t : '#' + t);

    this.postService.updatePost(this.post.id!, this.editedContent, tags.length ? tags : undefined).subscribe({
      next: (updated) => {
        this.post = updated;
        this.isEditing = false;
        this.postUpdated.emit(updated);
      },
      error: () => {}
    });
  }

  deletePost(): void {
    if (!confirm('Delete this post?')) return;
    this.postService.deletePost(this.post.id!).subscribe({
      next: () => this.postDeleted.emit(this.post.id),
      error: () => {}
    });
  }

  sharePost(): void {
    if (!confirm(`Repost this post by ${this.post.authorName || 'this user'} to your feed?`)) return;
    const currentUserId = this.authService.getCurrentUserId();
    this.postService.sharePost(this.post.id!).subscribe({
      next: (sharedPost) => {
        this.shareSuccess = true;
        setTimeout(() => this.shareSuccess = false, 3000);
        this.postShared.emit(sharedPost);
        if (this.post.authorId) {
          this.analyticsService.trackEvent(this.post.id!, 'SHARE', this.post.authorId).subscribe({ error: () => {} });
        }
        if (currentUserId && this.post.authorId && Number(currentUserId) !== Number(this.post.authorId)) {
          this.sendNotification(this.post.authorId, 'SHARE',
            `${this.authService.getCurrentUser()?.username || 'Someone'} reposted your post`, this.post.id);
        }
      },
      error: () => {
        this.shareError = 'Failed to repost';
        setTimeout(() => this.shareError = '', 3000);
      }
    });
  }

  private sendNotification(receiverId: number, type: string, message: string, postId?: number): void {
    const senderId = this.authService.getCurrentUserId();
    if (!senderId) return;
    this.http.post(`${environment.apiUrl}/notifications`, {
      senderId, receiverId, type, message, postId: postId || null
    }).subscribe({ error: () => {} });
  }

  formatDate(date: string | undefined): string {
    if (!date) return '';
    const normalized = date.endsWith('Z') || date.includes('+') ? date : date + 'Z';
    const d = new Date(normalized), now = new Date();
    const diff = Math.floor((now.getTime() - d.getTime()) / 60000);
    if (diff < 1) return 'Just now';
    if (diff < 60) return `${diff}m ago`;
    if (diff < 1440) return `${Math.floor(diff / 60)}h ago`;
    if (diff < 10080) return `${Math.floor(diff / 1440)}d ago`;
    return d.toLocaleDateString();
  }

  isImage(p: string): boolean {
    if (!p) return false;
    return /\.(jpg|jpeg|png|gif|webp|svg)$/i.test(p) ||
      (p.startsWith('http') && !this.isVideo(p) && !this.isDocument(p));
  }

  isVideo(p: string): boolean { return /\.(mp4|webm|ogg|mov|avi)$/i.test(p || ''); }
  isDocument(p: string): boolean { return /\.(pdf|doc|docx|txt|xls|xlsx|ppt|pptx)$/i.test(p || ''); }

  getMediaUrl(p: string): string {
    if (!p) return '';
    return p.startsWith('http') ? p : `http://localhost:8080${p}`;
  }

  getFileName(p: string): string { return p ? p.split('/').pop() || 'Document' : 'Document'; }
  onMediaError(e: any): void { e.target.style.display = 'none'; }

  getCtaText(): string {
    const map: Record<string, string> = {
      LEARN_MORE: 'Learn More', SHOP_NOW: 'Shop Now',
      CONTACT_US: 'Contact Us', SIGN_UP: 'Sign Up'
    };
    return map[this.post.callToAction || ''] || '';
  }

  toggleSave(): void {
    this.isSaved = this.savedPostsService.toggle(this.post);
  }
}
