import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PostService } from '../../services/post.service';
import { FeedService } from '../../services/feed.service';
import { AuthService } from '../../services/auth.service';
import { ProfilePictureService } from '../../services/profile-picture.service';
import { Post } from '../../models/post.model';
import { PostCardComponent } from '../post-card/post-card.component';
import { timer } from 'rxjs';

type FeedTab = 'feed' | 'trending' | 'scheduled';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PostCardComponent],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  trendingHashtags: { name: string; count: number }[] = [];
  scheduledPosts: Post[] = [];

  isLoading = false;
  errorMessage = '';
  successMessage = '';
  repostMessage = '';

  activeTab: FeedTab = 'feed';

  // Filters
  filterPostType = '';
  filterUserType = '';
  showFilters = false;

  // Hashtag search
  hashtagSearch = '';
  activeHashtag = '';

  // Create post form
  newPostContent = '';
  postType = 'REGULAR';
  callToAction = '';
  showAdvancedOptions = false;
  isPosting = false;
  hashtagInput = '';

  // Advanced (CREATOR/BUSINESS only)
  scheduledAt = '';
  taggedProductInput = '';
  taggedProductIds: number[] = [];

  // Post box extras
  charCount = 0;
  showEmojiPicker = false;
  emojis = [
    '😀','😁','😂','🤣','😍','🥰','😎','🤩','😏','🙄',
    '😢','😭','😡','🤬','😱','🤯','🥳','🤔','🤗','😴',
    '👍','👎','👏','🙌','🤝','💪','🫶','❤️','🔥','✅',
    '🚀','💡','🎉','🎯','💼','📊','📢','🌟','🏆','💬',
    '🍕','☕','🎵','📸','🌍','💻','📱','🎮','⚽','🌈'
  ];

  // Media attachment
  selectedFile: File | null = null;
  selectedFileUrl: string = '';
  isImageFile = false;

  constructor(
    private postService: PostService,
    private feedService: FeedService,
    public authService: AuthService,
    private profilePicService: ProfilePictureService
  ) {}

  get currentAvatarUrl(): string | null {
    return this.profilePicService.get(this.authService.getCurrentUserId());
  }

  get currentRole(): string {
    return this.authService.getCurrentUser()?.role || 'USER';
  }

  get isAdvancedUser(): boolean {
    return this.currentRole === 'CREATOR' || this.currentRole === 'BUSINESS';
  }

  ngOnInit(): void {
    // Small delay on init to ensure auth state is loaded from storage on page refresh
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.loadFeed();
    } else {
      // Retry after 200ms — handles edge case where storage load is async
      timer(200).subscribe(() => this.loadFeed());
    }
    this.loadTrendingHashtags();
    if (this.isAdvancedUser) this.loadScheduledPosts();
  }

  loadFeed(): void {
    this.isLoading = true;
    this.activeHashtag = '';
    const postType = this.filterPostType || undefined;
    this.feedService.getFeed(postType).subscribe({
      next: (posts) => {
        this.posts = this.sortFeed(posts);
        this.applyUserTypeFilter();
        this.isLoading = false;
        // If no posts returned and user is authenticated, retry once after 500ms
        // (handles race condition on first login redirect)
        if (posts.length === 0 && this.authService.getCurrentUserId()) {
          timer(500).subscribe(() => {
            this.feedService.getFeed(postType).subscribe({
              next: (retryPosts) => {
                if (retryPosts.length > 0) {
                  this.posts = this.sortFeed(retryPosts);
                  this.applyUserTypeFilter();
                }
              },
              error: () => {}
            });
          });
        }
      },
      error: () => { this.isLoading = false; }
    });
  }

  // Sort posts by createdAt descending
  sortFeed(posts: Post[]): Post[] {
    return [...posts].sort((a, b) => {
      const ta = a.createdAt ? new Date(a.createdAt + (a.createdAt.endsWith('Z') ? '' : 'Z')).getTime() : 0;
      const tb = b.createdAt ? new Date(b.createdAt + (b.createdAt.endsWith('Z') ? '' : 'Z')).getTime() : 0;
      return tb - ta;
    });
  }

  applyUserTypeFilter(): void {
    if (!this.filterUserType) { this.filteredPosts = this.sortFeed([...this.posts]); return; }
    const currentUser = this.authService.getCurrentUser();
    if (this.filterUserType === currentUser?.role) {
      this.filteredPosts = this.sortFeed(this.posts.filter(p => Number(p.authorId) === Number(currentUser?.id)));
    } else {
      this.filteredPosts = this.sortFeed([...this.posts]);
    }
  }

  applyFilters(): void {
    this.isLoading = true;
    this.activeHashtag = '';
    this.loadFeed();
    this.showFilters = false;
  }

  clearFilters(): void {
    this.filterPostType = '';
    this.filterUserType = '';
    this.activeHashtag = '';
    this.loadFeed();
    this.showFilters = false;
  }

  loadTrendingHashtags(): void {
    this.feedService.getTrendingHashtags(10).subscribe({
      next: (tags) => this.trendingHashtags = tags,
      error: () => {}
    });
  }

  loadScheduledPosts(): void {
    this.postService.getScheduledPosts().subscribe({
      next: (posts) => this.scheduledPosts = posts,
      error: () => {}
    });
  }

  searchByHashtag(tag: string): void {
    const clean = tag.startsWith('#') ? tag.substring(1) : tag;
    this.activeHashtag = '#' + clean;
    this.activeTab = 'feed';
    this.isLoading = true;
    this.filterPostType = '';
    this.filterUserType = '';
    this.feedService.searchByHashtag(clean).subscribe({
      next: (posts) => {
        this.posts = this.sortFeed(posts);
        this.filteredPosts = this.sortFeed(posts);
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  setTab(tab: FeedTab): void {
    this.activeTab = tab;
    if (tab === 'trending') this.loadTrendingHashtags();
    if (tab === 'scheduled') this.loadScheduledPosts();
  }

  get displayPosts(): Post[] { return this.filteredPosts; }

  get hasActiveFilters(): boolean {
    return !!(this.filterPostType || this.filterUserType || this.activeHashtag);
  }

  parseTaggedProducts(): void {
    this.taggedProductIds = this.taggedProductInput
      .split(/[\s,]+/)
      .map(s => parseInt(s.trim(), 10))
      .filter(n => !isNaN(n) && n > 0);
  }

  createPost(): void {
    if (!this.newPostContent.trim()) {
      this.errorMessage = 'Post content cannot be empty';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    const explicitTags = this.hashtagInput
      .split(/[\s,]+/)
      .map(t => t.trim()).filter(t => t.length > 0)
      .map(t => t.startsWith('#') ? t : '#' + t);

    this.parseTaggedProducts();
    this.isPosting = true;
    this.errorMessage = '';

    const doCreate = (mediaPath?: string) => {
      const post: any = {
        content: this.newPostContent,
        authorId: userId,
        postType: this.postType,
        callToAction: this.callToAction || undefined,
        hashtags: explicitTags.length > 0 ? explicitTags : undefined,
        scheduledAt: this.scheduledAt || undefined,
        taggedProductIds: this.taggedProductIds.length > 0 ? this.taggedProductIds : undefined,
        mediaPath: mediaPath || undefined
      };

      this.postService.createPost(post).subscribe({
        next: (created) => {
          if (created.scheduledAt) {
            this.scheduledPosts.unshift(created);
            this.successMessage = 'Post scheduled!';
          } else {
            this.posts.unshift(created);
            this.filteredPosts.unshift(created);
            this.successMessage = 'Post created!';
          }
          this.resetForm();
          this.isPosting = false;
          setTimeout(() => this.successMessage = '', 3000);
          this.loadTrendingHashtags();
        },
        error: (e) => {
          this.errorMessage = e.error?.error || 'Failed to create post';
          this.isPosting = false;
        }
      });
    };

    if (this.selectedFile) {
      this.postService.uploadPostMedia(this.selectedFile).subscribe({
        next: (res) => doCreate('http://localhost:8080' + res.mediaPath),
        error: () => doCreate() // if upload fails, post without image
      });
    } else {
      doCreate();
    }
  }

  resetForm(): void {
    this.newPostContent = '';
    this.hashtagInput = '';
    this.postType = 'REGULAR';
    this.callToAction = '';
    this.scheduledAt = '';
    this.taggedProductInput = '';
    this.taggedProductIds = [];
    this.showAdvancedOptions = false;
    this.charCount = 0;
    this.showEmojiPicker = false;
    this.removeMedia();
  }

  onContentInput(): void {
    this.charCount = this.newPostContent.length;
  }

  toggleEmojiPicker(): void {
    this.showEmojiPicker = !this.showEmojiPicker;
  }

  insertEmoji(emoji: string): void {
    this.newPostContent += emoji;
    this.charCount = this.newPostContent.length;
    this.showEmojiPicker = false;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.selectedFile = file;
    this.isImageFile = file.type.startsWith('image/');
    const reader = new FileReader();
    reader.onload = (e) => this.selectedFileUrl = e.target?.result as string;
    reader.readAsDataURL(file);
    // Reset input so same file can be re-selected
    input.value = '';
  }

  removeMedia(): void {
    this.selectedFile = null;
    this.selectedFileUrl = '';
    this.isImageFile = false;
  }

  onPostDeleted(postId: number): void {
    this.posts = this.posts.filter(p => p.id !== postId);
    this.filteredPosts = this.filteredPosts.filter(p => p.id !== postId);
    this.scheduledPosts = this.scheduledPosts.filter(p => p.id !== postId);
  }

  onPostShared(sharedPost: Post): void {
    this.posts.unshift(sharedPost);
    this.filteredPosts.unshift(sharedPost);
    this.repostMessage = '✅ Reposted successfully!';
    setTimeout(() => this.repostMessage = '', 3000);
  }

  onPostUpdated(updatedPost: Post): void {
    const updateArr = (arr: Post[]) => {
      const idx = arr.findIndex(p => p.id === updatedPost.id);
      if (idx !== -1) arr[idx] = updatedPost;
    };
    updateArr(this.posts);
    updateArr(this.scheduledPosts);
    // Re-sort so pinned posts float to top
    this.posts = this.sortFeed(this.posts);
    this.applyUserTypeFilter();
  }

  formatScheduled(dt: string): string {
    if (!dt) return '';
    return new Date(dt).toLocaleString();
  }

  get minScheduleDate(): string {
    const d = new Date();
    d.setMinutes(d.getMinutes() + 5);
    return d.toISOString().slice(0, 16);
  }
}
