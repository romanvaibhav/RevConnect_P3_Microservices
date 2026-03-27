export interface Connection {
  id?: number;
  requesterId: number;
  receiverId: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt?: string;
  respondedAt?: string;
}

export interface Follow {
  id?: number;
  followerId: number;
  followingId: number;
  createdAt?: string;
}

export interface Notification {
  id?: number;
  senderId?: number;
  receiverId?: number;
  type: string;
  postId?: number;
  message: string;
  read: boolean;
  createdAt?: string;
}
