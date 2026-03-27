import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AnalyticsService, PostAnalytics, AnalyticsSummary } from '../../services/analytics.service';
import { FollowService } from '../../services/follow.service';
import { AuthService } from '../../services/auth.service';

interface PostRow {
  postId: number;
  views: number;
  likes: number;
  comments: number;
  shares: number;
  engagementRate: number;
  lastUpdated?: string;
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {
  activeTab: 'overview' | 'posts' | 'engagement' = 'overview';
  isLoading = false;
  accessDenied = false;

  summary: AnalyticsSummary | null = null;
  postRows: PostRow[] = [];
  followerCount = 0;

  sortField: keyof PostRow = 'views';
  sortAsc = false;

  constructor(
    private analyticsService: AnalyticsService,
    private followService: FollowService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const role = this.authService.getUserRole();
    if (role !== 'CREATOR' && role !== 'BUSINESS') {
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

    this.analyticsService.getUserSummary(userId).subscribe({
      next: (s) => { this.summary = s; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });

    this.analyticsService.getAnalyticsByUser(userId).subscribe({
      next: (list) => {
        this.postRows = list.map(a => ({
          postId: a.postId,
          views: a.viewCount,
          likes: a.likeCount,
          comments: a.commentCount,
          shares: a.shareCount,
          engagementRate: a.engagementRate ?? 0,
          lastUpdated: a.lastUpdated
        }));
        this.sortRows();
      },
      error: () => {}
    });

    this.followService.getFollowers(userId).subscribe({
      next: (ids) => this.followerCount = ids.length,
      error: () => {}
    });
  }

  setTab(tab: 'overview' | 'posts' | 'engagement'): void {
    this.activeTab = tab;
  }

  sortBy(field: keyof PostRow): void {
    if (this.sortField === field) {
      this.sortAsc = !this.sortAsc;
    } else {
      this.sortField = field;
      this.sortAsc = false;
    }
    this.sortRows();
  }

  sortRows(): void {
    this.postRows.sort((a, b) => {
      const av = a[this.sortField] as number;
      const bv = b[this.sortField] as number;
      return this.sortAsc ? av - bv : bv - av;
    });
  }

  get totalEngagements(): number {
    return this.summary
      ? (this.summary.totalEngagements ?? (this.summary.totalLikes + this.summary.totalComments + this.summary.totalShares))
      : 0;
  }

  get engagementRate(): number {
    return this.summary?.avgEngagementRate ?? 0;
  }

  barWidth(value: number, max: number): string {
    if (!max) return '0%';
    return Math.round((value / max) * 100) + '%';
  }

  get maxViews(): number {
    return Math.max(...this.postRows.map(r => r.views), 1);
  }
}
