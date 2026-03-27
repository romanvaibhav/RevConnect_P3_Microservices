import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Notification } from '../models/connection.model';
import { environment } from '../../environments/environment';

export interface NotificationPreferences {
  CONNECTION_REQUEST: boolean;
  CONNECTION_ACCEPTED: boolean;
  FOLLOW: boolean;
  LIKE: boolean;
  COMMENT: boolean;
  SHARE: boolean;
}

const PREFS_KEY = 'notification_preferences';

const DEFAULT_PREFS: NotificationPreferences = {
  CONNECTION_REQUEST: true,
  CONNECTION_ACCEPTED: true,
  FOLLOW: true,
  LIKE: true,
  COMMENT: true,
  SHARE: true
};

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getMyNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`).pipe(
      map(res => res.content || res)
    );
  }

  getUnreadNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${userId}/unread`);
  }

  getUnreadCount(userId: number): Observable<number> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/user/${userId}/unread-count`).pipe(
      map(r => r.count)
    );
  }

  markAsRead(id: number): Observable<Notification> {
    return this.http.put<Notification>(`${this.apiUrl}/${id}/read`, {});
  }

  markAllAsRead(userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/user/${userId}/read-all`, {});
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ── Preferences (localStorage) ──

  getPreferences(): NotificationPreferences {
    try {
      const stored = localStorage.getItem(PREFS_KEY);
      return stored ? { ...DEFAULT_PREFS, ...JSON.parse(stored) } : { ...DEFAULT_PREFS };
    } catch {
      return { ...DEFAULT_PREFS };
    }
  }

  savePreferences(prefs: NotificationPreferences): void {
    localStorage.setItem(PREFS_KEY, JSON.stringify(prefs));
  }

  isTypeEnabled(type: string): boolean {
    const prefs = this.getPreferences();
    return (prefs as any)[type] !== false;
  }
}
