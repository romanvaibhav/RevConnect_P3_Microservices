import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { FollowService } from '../../services/follow.service';
import { Post } from '../../models/post.model';
import { PostCardComponent } from '../post-card/post-card.component';

type PostTab = 'updates' | 'announcements' | 'promotional' | 'all';

@Component({
  selector: 'app-business-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PostCardComponent],
  templateUrl: './business-dashboard.component.html',
  styleUrls: ['./business-dashboard.component.css']
})
export class BusinessDashboardComponent implements OnInit {
  accessDenied = false;
  isLoading = false;
  activeTab: PostTab = 'all';

  posts: Post[] = [];
  followerCount = 0;

  // Compose form
  showCompose = false;
  composeType: 'REGULAR' | 'ANNOUNCEMENT' | 'PROMOTIONAL' = 'REGULAR';
  composeContent = '';
  composeCta: string = '';
  composeHashtags = '';
  composeScheduledAt = '';
  isPosting = false;
  postSuccess = '';
  postError = '';

  readonly ctaOptions = [
    { value: '', label: 'No CTA' },
    { value: 'LEARN_MORE', label: 'Learn More' },
    { value: 'SHOP_NOW', label: 'Shop Now' },
    { value: 'CONTACT_US', label: 'Contact Us' },
    { value: 'SIGN_UP', label: 'Sign Up' }
  ];

  constructor(
    private postService: PostService,
    public authService: AuthService,
    private followService: FollowService
  ) {}

  ngOnInit(): void {
    if (this.authService.getUserRole() !== 'BUSINESS') {
      this.accessDenied = true;
      return;
    }
    this.loadData();
  }

  loadData(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      setTimeout(() => this.loadData(), 300);
      return;
    }
    this.isLoading = true;

    this.postService.getPostsByUser(userId).subscribe({
      next: (posts) => { this.posts = posts; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });

    this.followService.getFollowers(userId).subscribe({
      next: (ids) => this.followerCount = ids.length,
      error: () => {}
    });
  }

  get filteredPosts(): Post[] {
    if (this.activeTab === 'all') return this.posts;
    if (this.activeTab === 'updates') return this.posts.filter(p => p.postType === 'REGULAR');
    if (this.activeTab === 'announcements') return this.posts.filter(p => p.postType === 'ANNOUNCEMENT');
    if (this.activeTab === 'promotional') return this.posts.filter(p => p.postType === 'PROMOTIONAL');
    return this.posts;
  }

  submitPost(): void {
    if (!this.composeContent.trim()) return;
    this.isPosting = true;
    this.postError = '';

    const hashtags = this.composeHashtags
      .split(/[\s,]+/).map(t => t.trim()).filter(t => t)
      .map(t => t.startsWith('#') ? t : '#' + t);

    const payload: any = {
      content: this.composeContent,
      postType: this.composeType,
      callToAction: this.composeCta || null,
      hashtags: hashtags.length ? hashtags : null,
      scheduledAt: this.composeScheduledAt || null
    };

    this.postService.createPost(payload).subscribe({
      next: (post) => {
        this.posts.unshift(post);
        this.postSuccess = this.composeScheduledAt ? 'Post scheduled!' : 'Post published!';
        this.resetCompose();
        this.isPosting = false;
        setTimeout(() => this.postSuccess = '', 3000);
      },
      error: () => {
        this.postError = 'Failed to publish post.';
        this.isPosting = false;
      }
    });
  }

  resetCompose(): void {
    this.composeContent = '';
    this.composeCta = '';
    this.composeHashtags = '';
    this.composeScheduledAt = '';
    this.composeType = 'REGULAR';
    this.showCompose = false;
  }

  onPostDeleted(id: number): void { this.posts = this.posts.filter(p => p.id !== id); }
  onPostUpdated(p: Post): void {
    const i = this.posts.findIndex(x => x.id === p.id);
    if (i !== -1) this.posts[i] = p;
  }

  get publishedCount(): number { return this.posts.filter(p => p.isPublished !== false).length; }
  get scheduledCount(): number { return this.posts.filter(p => p.isPublished === false).length; }
  get announcementCount(): number { return this.posts.filter(p => p.postType === 'ANNOUNCEMENT').length; }
  get promotionalCount(): number { return this.posts.filter(p => p.postType === 'PROMOTIONAL').length; }
}
