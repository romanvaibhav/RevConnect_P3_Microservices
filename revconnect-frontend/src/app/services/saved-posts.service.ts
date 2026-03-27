import { Injectable } from '@angular/core';
import { Post } from '../models/post.model';

const STORAGE_KEY = 'rc_saved_posts';

@Injectable({ providedIn: 'root' })
export class SavedPostsService {

  private load(): Post[] {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
    } catch {
      return [];
    }
  }

  private persist(posts: Post[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(posts));
  }

  getAll(): Post[] {
    return this.load();
  }

  isSaved(postId: number): boolean {
    return this.load().some(p => p.id === postId);
  }

  save(post: Post): void {
    const posts = this.load();
    if (!posts.some(p => p.id === post.id)) {
      posts.unshift(post);
      this.persist(posts);
    }
  }

  unsave(postId: number): void {
    this.persist(this.load().filter(p => p.id !== postId));
  }

  toggle(post: Post): boolean {
    if (this.isSaved(post.id!)) {
      this.unsave(post.id!);
      return false;
    } else {
      this.save(post);
      return true;
    }
  }
}
