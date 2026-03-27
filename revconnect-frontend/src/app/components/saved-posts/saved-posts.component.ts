import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Post } from '../../models/post.model';
import { SavedPostsService } from '../../services/saved-posts.service';
import { PostCardComponent } from '../post-card/post-card.component';

@Component({
  selector: 'app-saved-posts',
  standalone: true,
  imports: [CommonModule, RouterLink, PostCardComponent],
  templateUrl: './saved-posts.component.html',
  styleUrls: ['./saved-posts.component.css']
})
export class SavedPostsComponent implements OnInit {
  posts: Post[] = [];

  constructor(private savedService: SavedPostsService) {}

  ngOnInit(): void {
    this.posts = this.savedService.getAll();
  }

  onPostDeleted(postId: number): void {
    this.savedService.unsave(postId);
    this.posts = this.posts.filter(p => p.id !== postId);
  }

  onPostUpdated(updated: Post): void {
    const idx = this.posts.findIndex(p => p.id === updated.id);
    if (idx !== -1) this.posts[idx] = updated;
  }
}
