import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { CreatorService, CreatorProfile } from '../../services/creator.service';
import { BusinessService, BusinessProfile } from '../../services/business.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile-setup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-setup.component.html',
  styleUrls: ['./profile-setup.component.css']
})
export class ProfileSetupComponent implements OnInit {
  currentUser: User | null = null;
  userRole = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Basic profile fields (all roles)
  basicProfile: { name: string; bio: string; location: string; website: string; accountPrivacy: 'PUBLIC' | 'PRIVATE' } = {
    name: '', bio: '', location: '', website: '', accountPrivacy: 'PUBLIC'
  };

  // Creator-specific fields (matches backend CreatorProfile entity)
  creatorProfile: CreatorProfile = { contentCategory: '', portfolioUrl: '', subscriberCount: undefined };

  // Business-specific fields (matches backend BusinessProfile entity)
  businessProfile: BusinessProfile = {
    companyName: '', industry: '', companySize: '', description: '', contactEmail: '', phoneNumber: ''
  };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private creatorService: CreatorService,
    private businessService: BusinessService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) { this.router.navigate(['/login']); return; }

    this.userRole = this.currentUser.role;
    // Pre-fill basic fields from stored user
    this.basicProfile.name = this.currentUser.name || '';
    this.basicProfile.bio = this.currentUser.bio || '';
    this.basicProfile.location = this.currentUser.location || '';
    this.basicProfile.website = this.currentUser.website || '';
    this.basicProfile.accountPrivacy = (this.currentUser.accountPrivacy as 'PUBLIC' | 'PRIVATE') || 'PUBLIC';
  }

  onSubmit(): void {
    if (!this.currentUser?.id) return;
    this.isLoading = true;
    this.errorMessage = '';

    // Step 1: save basic profile
    this.userService.updateUser(this.currentUser.id, this.basicProfile).subscribe({
      next: (updatedUser) => {
        // Update stored user
        localStorage.setItem('user', JSON.stringify({ ...this.currentUser, ...updatedUser }));

        // Step 2: save role-specific profile
        if (this.userRole === 'CREATOR') {
          this.creatorService.saveProfile(this.currentUser!.id!, this.creatorProfile).subscribe({
            next: () => this.onSaveSuccess(),
            error: (e) => this.onSaveError(e)
          });
        } else if (this.userRole === 'BUSINESS') {
          this.businessService.saveProfile(this.currentUser!.id!, this.businessProfile).subscribe({
            next: () => this.onSaveSuccess(),
            error: (e) => this.onSaveError(e)
          });
        } else {
          this.onSaveSuccess();
        }
      },
      error: (e) => this.onSaveError(e)
    });
  }

  private onSaveSuccess(): void {
    this.successMessage = 'Profile saved!';
    this.isLoading = false;
    setTimeout(() => this.router.navigate(['/profile', this.currentUser!.id]), 1200);
  }

  private onSaveError(e: any): void {
    this.errorMessage = e.error?.error || e.error?.message || 'Failed to save profile';
    this.isLoading = false;
  }

  skip(): void {
    this.router.navigate(['/feed']);
  }
}
