import { Component, computed, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { ConnectionService } from '../../services/connection.service';
import { FollowService } from '../../services/follow.service';
import { ProfilePictureService } from '../../services/profile-picture.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

  isAuthenticated = computed(() => this.authService.isAuthenticated());
  currentUser = computed(() => this.authService.currentUser());
  unreadCount = 0;
  pendingRequestsCount = 0;

  private pollInterval: any;

  constructor(
    public authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private connectionService: ConnectionService,
    private followService: FollowService,
    private profilePicService: ProfilePictureService
  ) {}

  get currentAvatarUrl(): string | null {
    return this.profilePicService.get(this.authService.getCurrentUserId());
  }

  ngOnInit(): void {
    // Wait for auth to be ready before starting polling
    if (this.authService.getCurrentUserId()) {
      this.startPolling();
    } else {
      // Retry after 300ms — handles page refresh where storage load may lag
      setTimeout(() => this.startPolling(), 300);
    }
  }

  private startPolling(): void {
    if (this.pollInterval) return;
    this.loadUnreadCount();
    this.loadPendingRequestsCount();
    this.pollInterval = setInterval(() => {
      if (this.isAuthenticated()) {
        this.loadUnreadCount();
        this.loadPendingRequestsCount();
      }
    }, 10000);
  }

  ngOnDestroy(): void {
    if (this.pollInterval) clearInterval(this.pollInterval);
  }

  loadUnreadCount(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.notificationService.getUnreadCount(userId).subscribe({
      next: (count) => this.unreadCount = count,
      error: () => {}
    });
  }

  loadPendingRequestsCount(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    forkJoin({
      connections: this.connectionService.getPendingReceivedCount().pipe(catchError(() => of(0))),
      follows: this.followService.getPendingFollowCount().pipe(catchError(() => of(0)))
    }).subscribe({
      next: ({ connections, follows }) => {
        this.pendingRequestsCount = connections + follows;
      },
      error: () => {}
    });
  }

  isCreatorOrBusiness(): boolean {
    return this.authService.isCreatorOrBusiness();
  }

  isBusiness(): boolean {
    return this.authService.getUserRole() === 'BUSINESS';
  }

  logout(): void {
    if (this.pollInterval) clearInterval(this.pollInterval);
    this.authService.logout();
  }

  navigateToProfile(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.router.navigate(['/profile', userId]);
    }
  }
}
