import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SocialLink {
  platform: string;
  url: string;
}

export interface ExternalLink {
  label: string;
  url: string;
}

export interface CreatorProfile {
  id?: number;
  userId?: number;
  creatorName?: string;
  contentCategory?: string;
  detailedBio?: string;
  contactEmail?: string;
  contactPhone?: string;
  websiteUrl?: string;
  portfolioUrl?: string;
  socialLinks?: string;   // JSON string stored in backend
  externalLinks?: string; // JSON string stored in backend
  subscriberCount?: number;
  // Parsed helpers (not sent to backend)
  parsedSocialLinks?: SocialLink[];
  parsedExternalLinks?: ExternalLink[];
}

@Injectable({ providedIn: 'root' })
export class CreatorService {
  constructor(private http: HttpClient) {}

  saveProfile(userId: number, profile: CreatorProfile): Observable<CreatorProfile> {
    // Serialize parsed arrays back to JSON strings before sending
    const payload = { ...profile };
    if (payload.parsedSocialLinks !== undefined) {
      payload.socialLinks = JSON.stringify(payload.parsedSocialLinks);
      delete payload.parsedSocialLinks;
    }
    if (payload.parsedExternalLinks !== undefined) {
      payload.externalLinks = JSON.stringify(payload.parsedExternalLinks);
      delete payload.parsedExternalLinks;
    }
    return this.http.post<CreatorProfile>(`${environment.apiUrl}/users/${userId}/creator-profile`, payload);
  }

  /** Parse JSON string fields into typed arrays */
  static parse(profile: CreatorProfile): CreatorProfile {
    return {
      ...profile,
      parsedSocialLinks: profile.socialLinks ? JSON.parse(profile.socialLinks) : [],
      parsedExternalLinks: profile.externalLinks ? JSON.parse(profile.externalLinks) : []
    };
  }
}
