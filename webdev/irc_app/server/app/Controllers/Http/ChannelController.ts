import type { HttpContextContract } from '@ioc:Adonis/Core/HttpContext';
import Blacklist from 'App/Models/Blacklist';
import Channel, { ChannelType } from 'App/Models/Channel';
import Invitation from 'App/Models/Invitation';
import User from 'App/Models/User';
import CreateChannelValidator from 'App/Validators/CreateChannelValidator';
import JoinChannelValidator from 'App/Validators/JoinChannelValidator';

export default class ChannelController {
  public async create({ response, request, auth }: HttpContextContract) {
    const data = await request.validate(CreateChannelValidator);
    const channelname = data.channelname;
    if ((await Channel.findBy('channelname', channelname)) !== null)
      return response.badRequest({
        errors: { channelname: ['Channel already exists'] },
      });
    const type = data.type as ChannelType;
    const channel = await Channel.create({
      channelname: channelname,
      ownerId: auth.user!.id,
      type: type,
    });
    await channel.related('users').attach([auth.user!.id]);
    response.send(channel);
  }

  public async joinOrCreate({ response, request, auth }: HttpContextContract) {
    const data = await request.validate(JoinChannelValidator);
    const channelname = data.channelname;
    const type = data.type as ChannelType;

    var channel = await Channel.firstOrCreate(
      { channelname: channelname },
      { channelname: channelname, ownerId: auth.user!.id, type: type }
    );
    if (channel.$isLocal) {
      if (
        (await channel
          .related('users')
          .query()
          .where('user_id', auth.user!.id)
          .first()) !== null
      )
        return response.badRequest({
          errors: { channelname: ['User has already joined the channel'] },
        });
    }
    await channel.related('users').attach([auth.user!.id]);
    response.send(channel);
  }

  public async invite({ response, auth, request }: HttpContextContract) {
    const username = request.input('username');
    const channelname = request.input('channelname');
    const user = await User.findBy('username', username);
    const channel = await Channel.findBy('channelname', channelname);
    if (channel !== null && user !== null) {
      // check if user is already in channel
      if (
        await user
          .related('channels')
          .query()
          .where('channel_id', channel.id)
          .first()
      ) {
        return response.badRequest({
          errors: { username: ['User has already joined the channel'] },
        });
      }
      const kickCount: number = (
        await Blacklist.query()
          .where('channelId', channel.id)
          .where('blacklistedId', user.id)
          .count('*', 'total')
      )[0].$extras.total;
      // check if user is already invited
      if (
        await user
          .related('invitations')
          .query()
          .where('channelId', channel.id)
          .where('status', 'invited')
          .first()
      ) {
        return response.badRequest({
          errors: { username: ['User has already been invited'] },
        });
      }
      if (kickCount >= 3 && auth.user!.id !== channel.ownerId) {
        return response.badRequest({
          errors: { username: ['User has been banned'] },
        });
      } else if (kickCount >= 3 && auth.user!.id === channel.ownerId) {
        await Blacklist.query()
          .where('channelId', channel.id)
          .where('blacklistedId', user.id)
          .delete();
      }
      const inv = await Invitation.create({
        channelId: channel.id,
        userId: user.id,
      });
      await inv.load('channel');

      return response.send(inv);
    } else {
      return response.badRequest({
        errors: { username: ['User not found'] },
      });
    }
  }

  public async revoke({ response, request }: HttpContextContract) {
    const username = request.input('username');
    const channelname = request.input('channelname');
    const inv = await Invitation.query()
      .whereHas('user', (userQuery) => userQuery.where('username', username))
      .andWhereHas('channel', (channelQuery) =>
        channelQuery.where('channelname', channelname)
      )
      .where('status', 'invited')
      .preload('channel')
      .first();
    if (inv === null)
      return response.badRequest({
        errors: { inv: ['Invitation not found'] },
      });

    inv.status = 'revoked';
    inv.save();
    return inv;
  }

  public async handleInvite({ response, auth, request }: HttpContextContract) {
    const channelname = request.input('channelname');
    const confirm = request.input('confirm');
    const user = auth.user!;
    const channel = await Channel.findBy('channelname', channelname);
    const inv = await user
      .related('invitations')
      .query()
      .where('channel_id', channel!.id)
      .where('status', 'invited')
      .first();
    if (inv === null)
      return response.badRequest({
        errors: { invitation: ['Invitation not found'] },
      });
    if (confirm) {
      inv.status = 'accepted';
      user.related('channels').attach([channel!.id]);
      inv.save();
    } else {
      inv.status = 'rejected';
      inv.save();
    }
    return channel;
  }

  public async kick({ response, auth, request }: HttpContextContract) {
    const username = request.input('username');
    const channelname = request.input('channelname');

    const user = await User.findBy('username', username);
    const channel = await Channel.findBy('channelname', channelname);
    if (channel === null || user === null)
      return response.badRequest({
        errors: { username: ['User not found'] },
      });

    if (user.id === channel.ownerId)
      return response.badRequest({
        errors: {
          user: ['Owner cannot be kicked.'],
        },
      });

    if (
      (await user
        .related('channels')
        .query()
        .where('channel_id', channel.id)
        .first()) === null
    ) {
      return response.badRequest({
        errors: {
          user: ['User is not in the channel.'],
        },
      });
    }
    const kickByUser = await Blacklist.query()
      .where('userId', auth.user!.id)
      .where('channelId', channel.id)
      .where('blacklistedId', user.id)
      .first();

    if (channel.type === 'private')
      return response.badRequest({
        errors: {
          command: ['Kick command can be used only in public channels'],
        },
      });

    if (kickByUser !== null)
      return response.badRequest({
        errors: { user: ['User has already been kicked by you.'] },
      });

    // owner of the channel kicks instantly
    if (auth.user!.id === channel.ownerId) {
      for (let i = 0; i < 3; i++) {
        await Blacklist.create({
          userId: channel.ownerId,
          channelId: channel.id,
          blacklistedId: user.id,
        });
      }
    } else {
      await Blacklist.create({
        userId: auth.user!.id,
        channelId: channel.id,
        blacklistedId: user.id,
      });
    }

    const kickCount: number = (
      await Blacklist.query()
        .where('channelId', channel.id)
        .where('blacklistedId', user.id)
        .count('*', 'total')
    )[0].$extras.total;

    if (kickCount >= 3) {
      await user.related('channels').detach([channel.id]);
    }
    response.json({ kicked: kickCount >= 3 });
  }
}
