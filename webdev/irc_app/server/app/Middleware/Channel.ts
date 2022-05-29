import { Exception } from '@adonisjs/core/build/standalone';
import { WsContextContract } from '@ioc:Ruby184/Socket.IO/WsContext';
import User from 'App/Models/User';

export default class ChannelMiddleware {
  public async wsHandle(
    { auth, params }: WsContextContract,
    next: () => Promise<void>
  ) {
    const user = auth.user as User;
    const hasAccessToChannel = await user
      .related('channels')
      .query()
      .where('channelname', params.name);

    if (!hasAccessToChannel.length) throw new Exception('Access denied.', 500);
    await next();
  }
}
