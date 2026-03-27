import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';
import { FeedService } from '../../services/feed.service';
import { User } from '../../models/user.model';
import { Post } from '../../models/post.model';
import { PostCardComponent } from '../post-card/post-card.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PostCardComponent],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {
  searchQuery = '';
  searchType: 'users' | 'posts' | 'hashtags' = 'users';

  users: User[] = [];
  posts: Post[] = [];

  isLoading = false;
  hasSearched = false;

  constructor(
    private userService: UserService,
    private feedService: FeedService
  ) {}

  search(): void {
    if (!this.searchQuery.trim()) return;
    this.isLoading = true;
    this.hasSearched = true;
    this.users = [];
    this.posts = [];

    switch (this.searchType) {
      case 'users':    this.searchUsers();    break;
      case 'posts':    this.searchPosts();    break;
      case 'hashtags': this.searchHashtags(); break;
    }
  }

  searchUsers(): void {
    this.userService.searchUsers(this.searchQuery).subscribe({
      next: (users) => { this.users = users; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  searchPosts(): void {
    this.feedService.searchPosts(this.searchQuery).subscribe({
      next: (posts) => { this.posts = posts; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  searchHashtags(): void {
    const tag = this.searchQuery.startsWith('#')
      ? this.searchQuery.substring(1)
      : this.searchQuery;
    this.feedService.searchByHashtag(tag).subscribe({
      next: (posts) => { this.posts = posts; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  onPostDeleted(postId: number): void {
    this.posts = this.posts.filter(p => p.id !== postId);
  }
}
