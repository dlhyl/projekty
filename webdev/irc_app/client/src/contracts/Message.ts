import { User } from './Auth';

export type RawMessage = string;

export interface SerializedMessage {
  userId: number;
  channelId: number;
  text: string;
  createdAt: string;
  updatedAt: string;
  id: number;
  user: User;
}

export interface RTMessage {
  [user: number]: string;
}
