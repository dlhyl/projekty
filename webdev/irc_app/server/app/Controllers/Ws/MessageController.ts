import { inject } from '@adonisjs/core/build/standalone';
import type { MessageRepositoryContract } from '@ioc:Repositories/MessageRepository';
import type { WsContextContract } from '@ioc:Ruby184/Socket.IO/WsContext';
import Channel from 'App/Models/Channel';
import User from 'App/Models/User';

// inject repository from container to controller constructor
// we do so because we can extract database specific storage to another class
// and also to prevent big controller methods doing everything
// controler method just gets data (validates it) and calls repository
// also we can then test standalone repository without controller
// implementation is bind into container inside providers/AppProvider.ts
@inject(['Repositories/MessageRepository'])
export default class MessageController {
  private getUserRoom(user: User): string {
    return `user:${user.id}`;
  }
  private RealTimeMessages = new Map<number, string>();

  constructor(private messageRepository: MessageRepositoryContract) {}

  public async loadMessages({ params }: WsContextContract, index: number) {
    return this.messageRepository.getMessages(params.name, index);
  }

  public async loadUsers({ params }: WsContextContract) {
    return this.messageRepository.getUsers(params.name);
  }

  public async loadLastMessage({ params }: WsContextContract) {
    return this.messageRepository.getLastMessage(params.name);
  }

  public async addMessage(
    { params, socket, auth }: WsContextContract,
    content: string
  ) {
    const message = await this.messageRepository.create(
      params.name,
      auth.user!.id,
      content
    );
    socket.broadcast.except('offline').emit('message', message);
    return message;
  }

  public async setNotifications(
    { socket }: WsContextContract,
    payload: boolean
  ) {
    if (payload) socket.leave('offline');
    else socket.join('offline');
  }

  public async leave({ params, auth, socket }: WsContextContract) {
    const channel = await Channel.findBy('channelname', params.name);
    if (channel?.ownerId === auth.user?.id) {
      socket.broadcast.emit('destroyed');
      return channel?.delete();
    } else {
      if (this.RealTimeMessages.has(auth.user!.id)) {
        this.RealTimeMessages.delete(auth.user!.id);
        socket
          .to('active')
          .except(this.getUserRoom(auth.user!))
          .emit('updateMessage', { user: auth.user!.id, message: null });
      }
      socket.to(this.getUserRoom(auth.user!)).emit('leaveChannel');
      socket.to('active').emit('leave', auth.user!.username);
      socket.leave('active');
      socket.leave(this.getUserRoom(auth.user!));
      return channel?.related('users').detach([auth.user!.id]);
    }
  }

  public async joinActive({ socket, auth }: WsContextContract) {
    socket.join('active');
    socket.join(this.getUserRoom(auth.user!));
    return Array.from(this.RealTimeMessages.entries());
  }

  public async leaveActive({ socket, auth }: WsContextContract) {
    if (this.RealTimeMessages.has(auth.user!.id)) {
      this.RealTimeMessages.delete(auth.user!.id);
      socket
        .to('active')
        .except(this.getUserRoom(auth.user!))
        .emit('updateMessage', { user: auth.user!.id, message: null });
    }
    socket.leave('active');
    socket.leave(this.getUserRoom(auth.user!));
  }

  public async updateMessage(
    { socket, auth }: WsContextContract,
    message: string
  ) {
    this.RealTimeMessages.set(auth.user!.id, message);
    socket
      .to('active')
      .except(this.getUserRoom(auth.user!))
      .except('offline')
      .emit('updateMessage', { user: auth.user!.id, message });
  }

  public async kick({ socket }: WsContextContract, username: string) {
    socket.broadcast.emit('leave', username);
  }
}
