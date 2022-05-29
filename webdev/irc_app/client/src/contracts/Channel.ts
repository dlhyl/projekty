import { User } from './Auth';
import { SerializedMessage } from './Message';

export type ChannelType = 'private' | 'public';

export interface Channel {
  ownerId: number;
  channelname: string;
  type: ChannelType;
  createdAt: string;
  updatedAt: string;
  id: number;
  kickCount: number;
  lastMessage: SerializedMessage;
}

export interface BlackList {
  user: User;
  channel: Channel;
  blacklistedUser: User;
}
