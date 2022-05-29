import { Channel, SerializedMessage, Status, User } from 'src/contracts';

export interface ChannelsStateInterface {
  loading: boolean;
  error: Error | null;
  privateChannels: { [channelname: string]: Channel };
  publicChannels: { [channelname: string]: Channel };
  invitations: Channel[];
  userStatus: { [userid: number]: Status };
  activeChannelUsers: User[];
  rtMessages: { [userid: number]: string };
  activeMessages: SerializedMessage[];
  active: Channel | null;
  notifications: string;
}

function state(): ChannelsStateInterface {
  return {
    loading: false,
    error: null,
    userStatus: {},
    privateChannels: {},
    publicChannels: {},
    invitations: [],
    rtMessages: {},
    activeMessages: [],
    activeChannelUsers: [],
    active: null,
    notifications: 'all',
  };
}

export default state;
