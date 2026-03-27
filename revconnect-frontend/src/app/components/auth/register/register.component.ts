import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ProfilePictureService } from '../../../services/profile-picture.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  user: User = {
    name: '', username: '', email: '', password: '',
    role: 'USER', accountPrivacy: 'PUBLIC'
  };

  confirmPassword = '';
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  avatarPreview: string = '';
  private avatarDataUrl: string = '';
  private avatarFile: File | null = null;

  constructor(
    private authService: AuthService,
    private profilePicService: ProfilePictureService,
    private router: Router
  ) {}

  async onAvatarSelected(event: Event): Promise<void> {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.avatarFile = file;
    this.avatarDataUrl = await this.profilePicService.readFile(file);
    this.avatarPreview = this.avatarDataUrl;
  }

  onSubmit(): void {
    this.errorMessage = '';

    if (!this.user.name || !this.user.username || !this.user.email || !this.user.password) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }
    if (this.user.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }
    if (this.user.password.length < 8) {
      this.errorMessage = 'Password must be at least 8 characters';
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.user.email)) {
      this.errorMessage = 'Please enter a valid email address';
      return;
    }
    const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;
    if (!usernameRegex.test(this.user.username)) {
      this.errorMessage = 'Username must be 3-20 characters (letters, numbers, underscore only)';
      return;
    }

    this.isLoading = true;

    this.authService.register(this.user).subscribe({
      next: (res) => {
        const userId = res?.id || res?.user?.id;
        if (this.avatarFile && userId) {
          // Upload avatar to backend
          this.profilePicService.uploadToBackend(userId, this.avatarFile).subscribe({
            next: () => {},
            error: () => {
              // Fallback: store base64 locally if backend upload fails
              if (this.avatarDataUrl) this.profilePicService.set(userId, this.avatarDataUrl);
            }
          });
        } else if (this.avatarDataUrl) {
          // No userId yet — store pending by username
          if (userId) {
            this.profilePicService.set(userId, this.avatarDataUrl);
          } else {
            localStorage.setItem('rc_avatar_pending_' + this.user.username, this.avatarDataUrl);
          }
        }
        this.successMessage = 'Registration successful! Redirecting to login...';
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (error) => {
        if (error.status === 409 || error.error?.message?.toLowerCase().includes('exist')) {
          this.errorMessage = 'Username or email already exists. Please try different credentials.';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Registration failed. Please try again.';
        }
        this.isLoading = false;
      }
    });
  }
}