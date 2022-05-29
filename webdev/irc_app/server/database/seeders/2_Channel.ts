import BaseSeeder from '@ioc:Adonis/Lucid/Seeder';
import Channel from 'App/Models/Channel';

export default class ChannelSeeder extends BaseSeeder {
  public async run() {
    const channel = await Channel.create({
      channelname: 'channel1',
      ownerId: 1,
      type: 'public',
    });
    channel.related('users').attach([1]);

    const privatechannel = await Channel.create({
      channelname: 'privateVPWA',
      ownerId: 1,
      type: 'private',
    });
    privatechannel.related('users').attach([1, 2]);
  }
}
