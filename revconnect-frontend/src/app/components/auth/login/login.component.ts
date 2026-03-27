/*
"When user clicks the Login button, onSubmit() is called."

this.authService.login(this.credentials).subscribe({
  next: () => {
    setTimeout(() => this.router.navigate(['/feed']), 1000);
  }
})
"It calls authService.login() and on success, redirects to /feed after 1 second."
*/

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { LoginRequest } from '../../../models/user.model';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  credentials: LoginRequest = { username: '', password: '' };
  selectedRole: 'USER' | 'CREATOR' | 'BUSINESS' = 'USER';

  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.credentials.username || !this.credentials.password) {
      this.errorMessage = 'Please fill in all fields';
      return;
    }

    this.isLoading = true;

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.successMessage = 'Login successful! Redirecting...';
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/feed']), 1000);
      },
      error: (error) => {
        this.isLoading = false;
        if (error.status === 401 || error.status === 403) {
          this.errorMessage = 'Invalid username or password. Please try again.';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Login failed. Please check your credentials.';
        }
      }
    });
  }
}
