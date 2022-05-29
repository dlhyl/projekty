import { Notify } from 'quasar';
import { api } from 'src/boot/axios';
import {
  Channel,
  ChannelType,
  Invitation,
  RawMessage,
  RTMessage,
  SerializedMessage,
  User,
} from 'src/contracts';
import { isMentioned } from 'src/helpers';
import { notify } from './NotificationService';
import { BootParams, SocketManager } from './SocketManager';

// creating instance of this class automatically connects to given socket.io namespace
// subscribe is called with boot params, so you can use it to dispatch actions for socket events
// you have access to socket.io socket using this.socket
class ChannelSocketManager extends SocketManager {
  public subscribe({ store, router }: BootParams): void {
    const channel = this.namespace.split('/').pop() as string;

    this.socket.on('message', (message: SerializedMessage) => {
      console.log(this.socket);
      store.commit('channels/SET_LASTMSG', { channel, message });
      store.commit('channels/NEW_MESSAGE', { channel, message });
      if (
        store.state.channels.userStatus[store.state.auth.user?.id || 0] ===
          'online' &&
        ((store.state.channels.notifications === 'mentions' &&
          isMentioned(message.text, store.state.auth.user?.username || '')) ||
          store.state.channels.notifications === 'all')
      )
        notify(message.user, message.text);
    });

    this.socket.on('destroyed', async () => {
      const Channel =
        channel in store.state.channels.privateChannels
          ? store.state.channels.privateChannels[channel]
          : store.state.channels.publicChannels[channel];
      if (store.state.channels.active?.channelname === channel) {
        await router.push({ name: 'home' });
        Notify.create({
          type: 'warning',
          message: `The channel ${Channel.channelname} has been deleted by the owner.`,
        });
      }
      await store.dispatch('channels/leave', Channel);
    });
    this.socket.on(
      'updateMessage',
      ({ user, message }: { user: number; message: string | null }) => {
        store.commit('channels/SET_RTMESSAGE', { user, message });
      }
    );
    this.socket.on('leave', (username: string) => {
      console.log('socket receive leave...');
      if (username === store.state.auth.user?.username)
        void store
          .dispatch(
            'channels/leave',
            channel in store.state.channels.privateChannels
              ? store.state.channels.privateChannels[channel]
              : store.state.channels.publicChannels[channel]
          )
          .then(() => router.push({ name: 'home' }));
      store.commit('channels/REMOVE_USER', username);
    });
    this.socket.on('leaveChannel', async () => {
      await store.dispatch('channels/leave', channel);
    });
  }

  public addMessage(message: RawMessage): Promise<SerializedMessage> {
    return this.emitAsync('addMessage', message);
  }

  public loadMessages(
    index: number
  ): Promise<{ data: SerializedMessage[]; hasMore: boolean }> {
    return this.emitAsync('loadMessages', index);
  }

  public loadLastMessage(): Promise<SerializedMessage> {
    return this.emitAsync('loadLastMessage');
  }

  public laodUsers(): Promise<User[]> {
    return this.emitAsync('loadUsers');
  }

  public setNotifications(state: boolean): Promise<void> {
    return this.emitAsync('setNotifications', state);
  }

  public leave(): Promise<void> {
    return this.emitAsync('leave');
  }

  public joinActive(): Promise<RTMessage[]> {
    return this.emitAsync('joinActive');
  }

  public leaveActive(): Promise<void> {
    return this.emitAsync('leaveActive');
  }

  public sendRealTimeMessage(message: string): Promise<void> {
    return this.emitAsync('updateMessage', message);
  }

  public kick(username: string): Promise<void> {
    return this.emitAsync('kick', username);
  }
}

class ChannelService {
  private channels: Map<string, ChannelSocketManager> = new Map();

  public join(name: string): ChannelSocketManager {
    if (this.channels.has(name)) {
      throw new Error(`User is already joined in channel "${name}"`);
    }

    // connect to given channel namespace
    const channel = new ChannelSocketManager(`/channels/${name}`);
    this.channels.set(name, channel);
    return channel;
  }

  public leave(name: string): boolean {
    const channel = this.channels.get(name);

    if (!channel) {
      return false;
    }

    // disconnect namespace and remove references to socket
    channel.destroy();
    return this.channels.delete(name);
  }

  public in(name: string): ChannelSocketManager | undefined {
    return this.channels.get(name);
  }

  public all(): ChannelSocketManager[] {
    return Array.from(this.channels.values());
  }

  public async create(
    channelname: string,
    type: ChannelType
  ): Promise<Channel> {
    const response = await api.post('channel/create', {
      channelname,
      type,
    });
    return response.data as Channel;
  }

  public async joinOrCreate(
    channelname: string,
    type: ChannelType
  ): Promise<Channel> {
    const response = await api.post('channel/joinOrCreate', {
      channelname,
      type,
    });
    return response.data as Channel;
  }

  public async invite(
    channelname: string,
    username: string
  ): Promise<Invitation> {
    const response = await api.post('channel/invite', {
      channelname,
      username,
    });
    return response.data as Invitation;
  }

  public async revoke(
    channelname: string,
    username: string
  ): Promise<Invitation> {
    const response = await api.post('channel/revoke', {
      channelname,
      username,
    });
    return response.data as Invitation;
  }

  async handleInvitation(channelname: string, confirm: boolean) {
    const response = await api.post('channel/handleInvite', {
      channelname,
      confirm,
    });
    return response.data as Channel;
  }

  public async kick(channelname: string, username: string): Promise<boolean> {
    const response = await api.post('channel/kick', {
      channelname,
      username,
    });
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    return response.data.kicked as boolean;
  }
}

export default new ChannelService();
