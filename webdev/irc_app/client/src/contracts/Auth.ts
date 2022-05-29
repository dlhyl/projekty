import { Channel } from '.';

export interface ApiToken {
  type: 'bearer';
  token: string;
  expires_at?: string;
  expires_in?: number;
}

export interface RegisterData {
  email: string;
  password: string;
  passwordConfirmation: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
  remember: boolean;
}

export type Status = 'online' | 'dnd' | 'offline';
export type Notifications = 'all' | 'mentions' | 'none';

export interface User {
  id: number;
  username: string;
  firstname: string;
  lastname: string;
  image: string | null;
  email: string;
  createdAt: string;
  updatedAt: string;
  privateChannels: Channel[];
  publicChannels: Channel[];
  invitations: Invitation[];
}

export interface Invitation {
  id: number;
  userId: number;
  channelId: number;
  status: string;
  channel: Channel;
  user?: User;
}
