import {
  Channel,
  Invitation,
  SerializedMessage,
  Status,
  User,
} from 'src/contracts';
import { MutationTree } from 'vuex';
import { ChannelsStateInterface } from './state';

const mutation: MutationTree<ChannelsStateInterface> = {
  LOADING_START(state) {
    state.loading = true;
    state.error = null;
  },
  LOADING_SUCCESS(
    state,
    { channel, message }: { channel: Channel; message: SerializedMessage }
  ) {
    state.loading = false;
    channel.lastMessage = message;
    state[channel.type === 'private' ? 'privateChannels' : 'publicChannels'][
      channel.channelname
    ] = channel;
  },
  LOAD_MSGS(state, messages: SerializedMessage[]) {
    console.log('load');
    state.activeMessages = [...messages, ...state.activeMessages];
  },
  LOADING_ERROR(state, error: Error) {
    state.loading = false;
    state.error = error;
  },
  CLEAR_CHANNEL(state, channelname: string) {
    if (channelname in state.privateChannels)
      delete state.privateChannels[channelname];
    else delete state.publicChannels[channelname];
  },
  SET_ACTIVE(state, channelname: string) {
    state.activeMessages = [];
    state.active =
      channelname in state.privateChannels
        ? state.privateChannels[channelname]
        : state.publicChannels[channelname];
  },
  CLEAR_ACTIVE(state) {
    state.active = null;
    state.activeMessages = [];
  },
  RESET_ACTIVE(state) {
    state.activeMessages = [];
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    if (state.active) state.active.updatedAt += 1;
  },
  SET_LASTMSG(
    state,
    { channel, message }: { channel: string; message: SerializedMessage }
  ) {
    if (channel in state.privateChannels)
      state.privateChannels[channel].lastMessage = message;
    else state.publicChannels[channel].lastMessage = message;
  },
  NEW_MESSAGE(
    state,
    { channel, message }: { channel: string; message: SerializedMessage }
  ) {
    console.log('NEW MESSAGE');
    if (channel === state.active?.channelname)
      state.activeMessages.push(message);
  },
  USER_STATUS(state, { userid, status }: { userid: number; status: Status }) {
    console.log(userid, status);
    state.userStatus[userid] = status;
  },
  SET_USERS(state, users: User[]) {
    state.activeChannelUsers = users;
  },
  REMOVE_USER(state, username: string) {
    const index = state.activeChannelUsers.findIndex(
      (u) => u.username === username
    );
    if (index !== -1) state.activeChannelUsers.splice(index, 1);
  },
  SET_RTMESSAGE(
    state,
    { user, message }: { user: number; message: string | null }
  ) {
    if (message === null) delete state.rtMessages[user];
    else state.rtMessages[user] = message;
  },
  SET_INVITATIONS(state, invitations: Invitation[]) {
    state.invitations = invitations.map((inv) => inv.channel);
  },
  ADD_INVITATION(state, invitation: Invitation) {
    state.invitations.push(invitation.channel);
  },
  REMOVE_INVITATION(state, channel: Channel) {
    const index = state.invitations.findIndex((item) => item.id === channel.id);
    if (index > -1) {
      state.invitations.splice(index, 1);
    }
  },
  SET_NOTIFICATION(state, notification: string) {
    state.notifications = notification;
  },
};

export default mutation;
