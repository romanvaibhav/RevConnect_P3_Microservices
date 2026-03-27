import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ConnectionService } from '../../services/connection.service';
import { FollowService, Follow } from '../../services/follow.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { Connection } from '../../models/connection.model';
import { User } from '../../models/user.model';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface ConnectionUser {
  connection: Connection;
  user: User | null;
}

export interface FollowUser {
  followId?: number;
  userId: number;
  user: User | null;
}

type Tab = 'connections' | 'received' | 'sent' | 'followers' | 'following' | 'follow-requests';

@Component({
  selector: 'app-connections',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './connections.component.html',
  styleUrls: ['./connections.component.css']
})
export class ConnectionsComponent implements OnInit {
  activeTab: Tab = 'connections';

  myConnections: ConnectionUser[] = [];
  pendingReceived: ConnectionUser[] = [];
  pendingSent: ConnectionUser[] = [];
  followers: FollowUser[] = [];
  following: FollowUser[] = [];
  pendingFollowRequests: FollowUser[] = [];

  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private connectionService: ConnectionService,
    private followService: FollowService,
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      setTimeout(() => this.loadAll(), 300);
      return;
    }
    this.isLoading = true;

    this.connectionService.getMyConnections(userId).subscribe({
      next: (conns) => this.resolveConnectionUsers(conns, userId, 'connections'),
      error: () => {}
    });

    this.connectionService.getPendingReceived().subscribe({
      next: (conns) => this.resolveConnectionUsers(conns, userId, 'received'),
      error: () => {}
    });

    this.connectionService.getPendingSent().subscribe({
      next: (conns) => this.resolveConnectionUsers(conns, userId, 'sent'),
      error: () => {}
    });

    this.followService.getFollowers(userId).subscribe({
      next: (ids) => this.resolveFollowUsers(ids, 'followers'),
      error: () => {}
    });

    this.followService.getFollowing(userId).subscribe({
      next: (ids) => this.resolveFollowUsers(ids, 'following'),
      error: () => { this.isLoading = false; }
    });

    this.followService.getPendingFollowRequests().subscribe({
      next: (follows) => this.resolvePendingFollowUsers(follows),
      error: () => {}
    });
  }

  private resolveConnectionUsers(conns: Connection[], currentUserId: number, target: 'connections' | 'received' | 'sent'): void {
    if (conns.length === 0) {
      if (target === 'connections') this.myConnections = [];
      else if (target === 'received') this.pendingReceived = [];
      else this.pendingSent = [];
      return;
    }

    const requests = conns.map(c => {
      const otherId = c.requesterId === currentUserId ? c.receiverId : c.requesterId;
      return this.userService.getUserById(otherId).pipe(
        catchError(() => of(null)),
        map(user => ({ connection: c, user } as ConnectionUser))
      );
    });

    forkJoin(requests).subscribe(results => {
      if (target === 'connections') this.myConnections = results;
      else if (target === 'received') this.pendingReceived = results;
      else this.pendingSent = results;
    });
  }

  private resolveFollowUsers(ids: number[], target: 'followers' | 'following'): void {
    if (ids.length === 0) {
      if (target === 'followers') this.followers = [];
      else this.following = [];
      return;
    }

    const requests = ids.map(id =>
      this.userService.getUserById(id).pipe(
        catchError(() => of(null)),
        map(user => ({ userId: id, user } as FollowUser))
      )
    );

    forkJoin(requests).subscribe(results => {
      if (target === 'followers') this.followers = results;
      else this.following = results;
      this.isLoading = false;
    });
  }

  private resolvePendingFollowUsers(follows: Follow[]): void {
    if (follows.length === 0) { this.pendingFollowRequests = []; return; }
    const requests = follows.map(f =>
      this.userService.getUserById(f.followerId).pipe(
        catchError(() => of(null)),
        map(user => ({ followId: f.id, userId: f.followerId, user } as FollowUser))
      )
    );
    forkJoin(requests).subscribe(results => { this.pendingFollowRequests = results; });
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
  }

  acceptConnection(item: ConnectionUser): void {
    if (!item.connection.id) return;
    this.connectionService.acceptConnection(item.connection.id).subscribe({
      next: () => {
        this.pendingReceived = this.pendingReceived.filter(i => i.connection.id !== item.connection.id);
        item.connection.status = 'ACCEPTED';
        this.myConnections.push(item);
        this.showSuccess('Connection accepted!');
      },
      error: () => this.showError('Failed to accept connection')
    });
  }

  rejectConnection(item: ConnectionUser): void {
    if (!item.connection.id) return;
    this.connectionService.rejectConnection(item.connection.id).subscribe({
      next: () => {
        this.pendingReceived = this.pendingReceived.filter(i => i.connection.id !== item.connection.id);
        this.showSuccess('Connection request declined.');
      },
      error: () => this.showError('Failed to reject connection')
    });
  }

  cancelRequest(item: ConnectionUser): void {
    if (!item.connection.id) return;
    this.connectionService.removeConnection(item.connection.id).subscribe({
      next: () => {
        this.pendingSent = this.pendingSent.filter(i => i.connection.id !== item.connection.id);
        this.showSuccess('Request cancelled.');
      },
      error: () => this.showError('Failed to cancel request')
    });
  }

  removeConnection(item: ConnectionUser): void {
    if (!item.connection.id || !confirm('Remove this connection?')) return;
    this.connectionService.removeConnection(item.connection.id).subscribe({
      next: () => {
        this.myConnections = this.myConnections.filter(i => i.connection.id !== item.connection.id);
        this.showSuccess('Connection removed.');
      },
      error: () => this.showError('Failed to remove connection')
    });
  }

  acceptFollowRequest(item: FollowUser): void {
    if (!item.followId) return;
    this.followService.acceptFollow(item.followId).subscribe({
      next: () => {
        this.pendingFollowRequests = this.pendingFollowRequests.filter(f => f.followId !== item.followId);
        this.followers.push({ userId: item.userId, user: item.user });
        this.showSuccess('Follow request accepted!');
      },
      error: () => this.showError('Failed to accept follow request')
    });
  }

  rejectFollowRequest(item: FollowUser): void {
    if (!item.followId) return;
    this.followService.rejectFollow(item.followId).subscribe({
      next: () => {
        this.pendingFollowRequests = this.pendingFollowRequests.filter(f => f.followId !== item.followId);
        this.showSuccess('Follow request declined.');
      },
      error: () => this.showError('Failed to decline follow request')
    });
  }

  unfollowUser(item: FollowUser): void {    const currentId = this.authService.getCurrentUserId();
    if (!currentId || !confirm('Unfollow this user?')) return;
    this.followService.unfollowUser(currentId, item.userId).subscribe({
      next: () => {
        this.following = this.following.filter(f => f.userId !== item.userId);
        this.showSuccess('Unfollowed.');
      },
      error: () => this.showError('Failed to unfollow')
    });
  }

  getInitial(user: User | null): string {
    return (user?.name || user?.username || 'U').charAt(0).toUpperCase();
  }

  private showSuccess(msg: string): void {
    this.successMessage = msg;
    this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 3000);
  }

  private showError(msg: string): void {
    this.errorMessage = msg;
    setTimeout(() => this.errorMessage = '', 4000);
  }
}
