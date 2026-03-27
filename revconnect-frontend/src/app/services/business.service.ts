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

export interface BusinessHours {
  [day: string]: string; // e.g. { "Mon": "9am-5pm", "Tue": "9am-5pm" }
}

export interface BusinessProfile {
  id?: number;
  userId?: number;
  companyName?: string;
  industry?: string;
  companySize?: string;
  description?: string;
  contactEmail?: string;
  phoneNumber?: string;
  businessAddress?: string;
  city?: string;
  country?: string;
  websiteUrl?: string;
  socialLinks?: string;    // JSON string
  businessHours?: string;  // JSON string
  externalLinks?: string;  // JSON string
  // Parsed helpers (not sent to backend)
  parsedSocialLinks?: SocialLink[];
  parsedBusinessHours?: BusinessHours;
  parsedExternalLinks?: ExternalLink[];
}

export const DAYS_OF_WEEK = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

@Injectable({ providedIn: 'root' })
export class BusinessService {
  constructor(private http: HttpClient) {}

  saveProfile(userId: number, profile: BusinessProfile): Observable<BusinessProfile> {
    const payload = { ...profile };
    if (payload.parsedSocialLinks !== undefined) {
      payload.socialLinks = JSON.stringify(payload.parsedSocialLinks);
      delete payload.parsedSocialLinks;
    }
    if (payload.parsedBusinessHours !== undefined) {
      payload.businessHours = JSON.stringify(payload.parsedBusinessHours);
      delete payload.parsedBusinessHours;
    }
    if (payload.parsedExternalLinks !== undefined) {
      payload.externalLinks = JSON.stringify(payload.parsedExternalLinks);
      delete payload.parsedExternalLinks;
    }
    return this.http.post<BusinessProfile>(`${environment.apiUrl}/users/${userId}/business-profile`, payload);
  }

  static parse(profile: BusinessProfile): BusinessProfile {
    return {
      ...profile,
      parsedSocialLinks: profile.socialLinks ? JSON.parse(profile.socialLinks) : [],
      parsedBusinessHours: profile.businessHours ? JSON.parse(profile.businessHours) : {},
      parsedExternalLinks: profile.externalLinks ? JSON.parse(profile.externalLinks) : []
    };
  }
}
