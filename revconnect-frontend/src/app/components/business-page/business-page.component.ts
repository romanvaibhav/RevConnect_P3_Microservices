import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { FollowService } from '../../services/follow.service';
import { PostService } from '../../services/post.service';
import { BusinessService, BusinessProfile } from '../../services/business.service';
import { User } from '../../models/user.model';
import { Post } from '../../models/post.model';
import { PostCardComponent } from '../post-card/post-card.component';

@Component({
  selector: 'app-business-page',
  standalone: true,
  imports: [CommonModule, RouterLink, PostCardComponent],
  templateUrl: './business-page.component.html',
  styleUrls: ['./business-page.component.css']
})
export class BusinessPageComponent implements OnInit {
  user: User | null = null;
  businessProfile: BusinessProfile | null = null;
  posts: Post[] = [];
  followerCount = 0;
  isFollowing = false;
  isOwnPage = false;
  isLoading = true;
  activeTab: 'posts' | 'about' = 'posts';

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    public authService: AuthService,
    private followService: FollowService,
    private postService: PostService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(p => this.load(+p['userId']));
  }

  load(userId: number): void {
    this.isLoading = true;
    const currentId = this.authService.getCurrentUserId();
    this.isOwnPage = currentId === userId;

    this.userService.getUserProfile(userId).subscribe({
      next: (data) => {
        this.user = data.user;
        this.businessProfile = data.businessProfile
          ? BusinessService.parse(data.businessProfile) : null;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });

    this.postService.getPostsByUser(userId).subscribe({
      next: (posts) => this.posts = posts.filter(p => p.isPublished !== false),
      error: () => {}
    });

    this.followService.getFollowers(userId).subscribe({
      next: (ids) => this.followerCount = ids.length,
      error: () => {}
    });

    if (currentId && currentId !== userId) {
      this.followService.checkFollowStatus(currentId, userId).subscribe({
        next: (r) => this.isFollowing = r.isFollowing,
        error: () => {}
      });
    }
  }

  follow(): void {
    const currentId = this.authService.getCurrentUserId();
    if (!currentId || !this.user?.id) return;
    this.followService.followUser(currentId, this.user.id).subscribe({
      next: () => { this.isFollowing = true; this.followerCount++; },
      error: () => {}
    });
  }

  unfollow(): void {
    const currentId = this.authService.getCurrentUserId();
    if (!currentId || !this.user?.id) return;
    this.followService.unfollowUser(currentId, this.user.id).subscribe({
      next: () => { this.isFollowing = false; this.followerCount--; },
      error: () => {}
    });
  }

  onPostDeleted(id: number): void { this.posts = this.posts.filter(p => p.id !== id); }
  onPostUpdated(p: Post): void {
    const i = this.posts.findIndex(x => x.id === p.id);
    if (i !== -1) this.posts[i] = p;
  }

  parsedBusinessHoursEntries(jsonStr?: string): { day: string; hours: string }[] {
    if (!jsonStr) return [];
    try {
      const obj = JSON.parse(jsonStr);
      return Object.keys(obj).filter(d => obj[d]).map(d => ({ day: d, hours: obj[d] }));
    } catch { return []; }
  }
}
