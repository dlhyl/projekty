import type { WsContextContract } from '@ioc:Ruby184/Socket.IO/WsContext';
import Channel from 'App/Models/Channel';
import Invitation from 'App/Models/Invitation';
import User from 'App/Models/User';

export default class ActivityController {
  public static userStatus: Map<number, string> = new Map();

  private getUserRoom(user: User): string {
    return `user:${user.id}`;
  }
  private getChannelRoom(channel: Channel): string {
    return `channel:${channel.id}`;
  }

  public async onConnected({ socket, auth, logger }: WsContextContract) {
    console.log('activity connected', auth.user?.id);
    // all connections for the same authenticated user will be in the room
    const userRoom = this.getUserRoom(auth.user!);
    const userSockets = await socket.in(userRoom).allSockets();

    await auth.user!.load('channels');
    const channelRooms = auth.user!.channels.map((ch) =>
      this.getChannelRoom(ch)
    );
    socket.join(channelRooms);
    socket.join(userRoom);
    socket.data.userId = auth.user!.id;
    console.log(
      Object.fromEntries(ActivityController.userStatus.entries()),
      auth.user!.id
    );
    if (!ActivityController.userStatus.has(auth.user!.id)) {
      ActivityController.userStatus.set(auth.user!.id, 'online');
      console.log(
        Object.fromEntries(ActivityController.userStatus.entries()),
        auth.user!.id
      );
    }
    // this is first connection for given user
    if (channelRooms.length > 0) {
      if (userSockets.size === 0)
        socket.to(channelRooms).emit('user:online', auth.user);

      const allSockets = await socket.in(channelRooms).fetchSockets();
      const onlineUsers = new Map<number, string>();
      for (const remoteSocket of allSockets) {
        onlineUsers.set(
          remoteSocket.data.userId,
          ActivityController.userStatus.get(remoteSocket.data.userId) || '?'
        );
      }
      socket.emit('user:list', Object.fromEntries(onlineUsers.entries()));
    }
    logger.info('new websocket connection');
  }

  // see https://socket.io/get-started/private-messaging-part-2/#disconnection-handler
  public async onDisconnected(
    { socket, auth, logger }: WsContextContract,
    reason: string
  ) {
    const userRoom = this.getUserRoom(auth.user!);
    const userSockets = await socket.in(userRoom).allSockets();

    // user is disconnected
    if (userSockets.size === 0) {
      // notify other users
      await auth.user!.load('channels');
      const channelRooms = auth.user!.channels.map((ch) =>
        this.getChannelRoom(ch)
      );
      if (channelRooms.length > 0)
        socket.to(channelRooms).emit('user:offline', auth.user);
      ActivityController.userStatus.delete(auth.user!.id);
      console.log(
        Object.fromEntries(ActivityController.userStatus.entries()),
        auth.user!.id
      );
    }

    logger.info('websocket disconnected', reason);
  }

  public async changeStatus(
    { socket, auth }: WsContextContract,
    status: string
  ) {
    await auth.user!.load('channels');
    const channelRooms = auth.user!.channels.map((ch) =>
      this.getChannelRoom(ch)
    );
    ActivityController.userStatus.set(auth.user!.id, status);
    console.log(
      Object.fromEntries(ActivityController.userStatus.entries()),
      auth.user!.id
    );
    if (channelRooms.length)
      socket.to(channelRooms).emit(`user:${status}`, auth.user);
  }

  public async invite(
    { socket, auth, response },
    { username, invitation }: { username: string; invitation: Invitation }
  ) {
    const user = await User.findBy('username', username);
    socket.in(this.getUserRoom(user!)).emit('getInvitation', invitation);
  }

  public async revoke(
    { socket, auth, response },
    { username, invitation }: { username: string; invitation: Invitation }
  ) {
    const user = await User.findBy('username', username);
    socket.in(this.getUserRoom(user!)).emit('revokeInvitation', invitation);
  }
}
