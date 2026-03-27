export interface User {
  id?: number;
  name: string;
  username: string;
  email: string;
  password?: string;
  role: 'USER' | 'CREATOR' | 'BUSINESS';
  bio?: string;
  location?: string;
  website?: string;
  profilePicturePath?: string;
  accountPrivacy: 'PUBLIC' | 'PRIVATE';
  createdAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}
