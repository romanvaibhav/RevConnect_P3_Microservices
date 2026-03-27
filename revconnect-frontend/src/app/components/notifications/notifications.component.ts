import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NotificationService, NotificationPreferences } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';
import { Notification } from '../../models/connection.model';

type Tab = 'all' | 'unread' | 'preferences';

const TYPE_LABELS: Record<string, string> = {
  CONNECTION_REQUEST: 'Connection Requests',
  CONNECTION_ACCEPTED: 'Accepted Connections',
  FOLLOW: 'New Followers',
  LIKE: 'Likes',
  COMMENT: 'Comments',
  SHARE: 'Shares'
};

const TYPE_ICONS: Record<string, string> = {
  CONNECTION_REQUEST: '🤝',
  CONNECTION_ACCEPTED: '✅',
  FOLLOW: '👤',
  LIKE: '❤️',
  COMMENT: '💬',
  SHARE: '🔄'
};

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {
  notifications: Notification[] = [];
  isLoading = false;
  activeTab: Tab = 'all';

  preferences: NotificationPreferences = this.notificationService.getPreferences();
  prefKeys = Object.keys(TYPE_LABELS) as (keyof NotificationPreferences)[];
  typeLabels = TYPE_LABELS;
  typeIcons = TYPE_ICONS;

  prefSaved = false;

  constructor(
    private notificationService: NotificationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      setTimeout(() => this.loadNotifications(), 300);
      return;
    }
    this.isLoading = true;

    this.notificationService.getMyNotifications(userId).subscribe({
      next: (list) => {
        this.notifications = list;
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  get filteredNotifications(): Notification[] {
    const prefs = this.notificationService.getPreferences();
    const visible = this.notifications.filter(n => (prefs as any)[n.type] !== false);
    return this.activeTab === 'unread' ? visible.filter(n => !n.read) : visible;
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
  }

  markAsRead(n: Notification): void {
    if (!n.id || n.read) return;
    this.notificationService.markAsRead(n.id).subscribe({
      next: () => { n.read = true; },
      error: () => {}
    });
  }

  markAllAsRead(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.notificationService.markAllAsRead(userId).subscribe({
      next: () => { this.notifications.forEach(n => n.read = true); },
      error: () => {}
    });
  }

  deleteNotification(n: Notification, event: Event): void {
    event.stopPropagation();
    if (!n.id) return;
    this.notificationService.deleteNotification(n.id).subscribe({
      next: () => { this.notifications = this.notifications.filter(x => x.id !== n.id); },
      error: () => {}
    });
  }

  savePreferences(): void {
    this.notificationService.savePreferences(this.preferences);
    this.prefSaved = true;
    setTimeout(() => this.prefSaved = false, 2500);
  }

  getIcon(type: string): string {
    return TYPE_ICONS[type] || '🔔';
  }

  getTypeLabel(type: string): string {
    return TYPE_LABELS[type] || type;
  }

  formatDate(date: string | undefined): string {
    if (!date) return '';
    const d = new Date(date), now = new Date();
    const diff = Math.floor((now.getTime() - d.getTime()) / 60000);
    if (diff < 1) return 'Just now';
    if (diff < 60) return `${diff}m ago`;
    if (diff < 1440) return `${Math.floor(diff / 60)}h ago`;
    if (diff < 10080) return `${Math.floor(diff / 1440)}d ago`;
    return d.toLocaleDateString();
  }
}
