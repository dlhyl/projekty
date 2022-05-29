import type {
  MessageRepositoryContract,
  SerializedMessage,
  SerializedUser,
} from '@ioc:Repositories/MessageRepository';
import Channel from 'App/Models/Channel';

export default class MessageRepository implements MessageRepositoryContract {
  public async getMessages(
    channelName: string,
    index: number
  ): Promise<{ data: SerializedMessage[]; hasMore: boolean }> {
    const perPage = 10;
    const channel = await Channel.query()
      .where('channelname', channelName)
      .preload('messages', (query) =>
        query
          .orderBy('id', 'desc')
          .offset((index - 1) * perPage)
          .limit(perPage)
          .preload('user')
      )
      .firstOrFail();
    const serializedMessages = channel.messages.map(
      (message) => message.serialize() as SerializedMessage
    );
    serializedMessages.reverse();

    return {
      data: serializedMessages,
      hasMore: channel.messages.length === perPage,
    };
  }

  public async getLastMessage(channelName: string): Promise<SerializedMessage> {
    const channel = await Channel.query()
      .where('channelname', channelName)
      .preload('lastMessage', (query) => query.preload('user'))
      .firstOrFail();

    return (
      channel.lastMessage.map(
        (message) => message.serialize() as SerializedMessage
      )[0] || null
    );
  }

  public async getUsers(channelName: string): Promise<SerializedUser[]> {
    const channel = await Channel.query()
      .where('channelname', channelName)
      .preload('users')
      .firstOrFail();
    return channel.users.map((user) => user.serialize() as SerializedUser);
  }

  public async create(
    channelName: string,
    userId: number,
    text: string
  ): Promise<SerializedMessage> {
    const channel = await Channel.findByOrFail('channelname', channelName);
    const message = await channel.related('messages').create({ userId, text });
    await message.load('user');

    return message.serialize() as SerializedMessage;
  }
}
