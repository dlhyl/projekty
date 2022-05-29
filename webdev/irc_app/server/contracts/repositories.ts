// here we are declaring our MessageRepository types for Repositories/MessageRepository

// container binding. See providers/AppProvider.ts for how we are binding the implementation
declare module '@ioc:Repositories/MessageRepository' {
  export interface SerializedUser {
    id: number;
    email: string;
    username: string;
    firstname: string;
    lastname: string;
    image: string;
    createdAt: string;
    updatedAt: string;
  }

  export interface SerializedMessage {
    userId: number;
    channelId: number;
    text: string;
    createdAt: string;
    updatedAt: string;
    id: number;
    user: SerializedUser;
  }

  export interface MessageRepositoryContract {
    getUsers(channelName: string): Promise<SerializedUser[]>;
    getMessages(
      channelName: string,
      index: number
    ): Promise<{ data: SerializedMessage[]; hasMore: boolean }>;
    getLastMessage(channelName: string): Promise<SerializedMessage>;
    create(
      channelName: string,
      userId: number,
      text: string
    ): Promise<SerializedMessage>;
  }

  const MessageRepository: MessageRepositoryContract;
  export default MessageRepository;
}
