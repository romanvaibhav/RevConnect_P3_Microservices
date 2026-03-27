import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

const KEY_PREFIX = 'rc_avatar_';
const API_BASE = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class ProfilePictureService {

  constructor(private http: HttpClient) {}

  // Upload to backend and cache the returned URL in localStorage
  uploadToBackend(userId: number, file: File): Observable<{ profilePicturePath: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ profilePicturePath: string }>(
      `${API_BASE}/api/users/${userId}/profile-picture`, formData
    ).pipe(
      tap(res => {
        // Cache the backend URL so it's available immediately
        this.set(userId, API_BASE + res.profilePicturePath);
      })
    );
  }

  // Store avatar URL keyed by userId (can be base64 or backend URL)
  set(userId: number, url: string): void {
    localStorage.setItem(KEY_PREFIX + userId, url);
  }

  get(userId: number | undefined | null): string | null {
    if (!userId) return null;
    return localStorage.getItem(KEY_PREFIX + userId);
  }

  remove(userId: number): void {
    localStorage.removeItem(KEY_PREFIX + userId);
  }

  // Read a File and return base64 data URL (used for instant preview)
  readFile(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => resolve(e.target?.result as string);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }
}
