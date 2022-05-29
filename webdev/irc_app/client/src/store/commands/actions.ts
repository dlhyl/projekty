import { Notify } from 'quasar';
import { Channel, ErrorResponseValidation } from 'src/contracts';
import { channelService } from 'src/services';
import { Router } from 'vue-router';
import { ActionTree } from 'vuex';
import { StateInterface } from '../index';
import { CommandsInterface } from './state';

const actions: ActionTree<CommandsInterface, StateInterface> = {
  async checkCommands(
    { commit, dispatch, rootState },
    { message, router }: { message: string; router: Router }
  ): Promise<boolean> {
    switch (message) {
      case '/list':
        if (!(await dispatch('isActiveChannel'))) return true;
        commit('SET_USERLIST', true);
        return true;
      case '/cancel':
        if (!(await dispatch('isActiveChannel'))) return true;
        await dispatch('leaveChannel', {
          router,
          channel: rootState.channels.active,
        });
        return true;
      case '/quit':
        if (!(await dispatch('isActiveChannel'))) return true;
        await dispatch('deleteChannel', {
          router,
          channel: rootState.channels.active,
        });
        return true;
      default:
        // join channel match
        if (new RegExp(/^\/join( .*)?$/).test(message)) {
          await dispatch('joinChannel', message);
          return true;
        } else if (new RegExp(/^\/invite( .*)?$/).test(message)) {
          if (!(await dispatch('isActiveChannel'))) return true;
          await dispatch('invite', message);
          return true;
        } else if (new RegExp(/^\/revoke( .*)?$/).test(message)) {
          if (!(await dispatch('isActiveChannel'))) return true;
          await dispatch('revoke', message);
          return true;
        } else if (new RegExp(/^\/kick( .*)?$/).test(message)) {
          if (!(await dispatch('isActiveChannel'))) return true;
          await dispatch('kick', message);
          return true;
        }
        return false;
    }
  },
  confirmedHandle(
    { commit },
    {
      title,
      fn,
    }: { title: string | string[]; fn: (...args: unknown[]) => void }
  ) {
    commit('SET_DIALOGPROPS', { title, fn });
    commit('SET_CONFIRMDIALOG', true);
  },
  async leaveChannel(
    { dispatch, rootState },
    { router, channel }: { router: Router; channel: Channel }
  ) {
    await dispatch('confirmedHandle', {
      title: [
        `${
          channel.ownerId === rootState.auth.user?.id
            ? "The channel will be deleted because you're the owner."
            : ''
        }`,
        `Are you sure you want to leave channel ${channel.channelname || ''}?`,
      ],
      fn: async () => {
        await channelService.in(channel.channelname)?.leave();
        await router.push({ name: 'home' });
        await dispatch('channels/leave', channel, {
          root: true,
        });
      },
    });
  },
  async deleteChannel(
    { dispatch, rootState },
    { router, channel }: { router: Router; channel: Channel }
  ) {
    if (rootState.auth.user?.id === channel.ownerId)
      await dispatch('confirmedHandle', {
        title: ['Are you sure you want to delete the channel?'],
        fn: async () => {
          await channelService.in(channel.channelname)?.leave();
          await router.push({ name: 'home' });
          await dispatch('channels/leave', channel, {
            root: true,
          });
        },
      });
  },
  joinChannel({ dispatch }, message: string) {
    if (!new RegExp(/^\/join +\w+( +private)?$/).test(message)) {
      Notify.create({
        type: 'warning',
        message: "Wrong command syntax. Use '/join channelname [private]'",
      });
      return;
    }
    const matches = message.match(new RegExp(/^\/join +(\w+)( +private)?$/));
    const channelname = matches ? matches[1] : '';
    const isPrivate = matches ? !!matches[2] : '';

    if (isPrivate) {
      dispatch(
        'channels/create',
        {
          channelname,
          type: 'private',
        },
        { root: true }
      ).catch((err: ErrorResponseValidation) => {
        Notify.create({
          type: 'warning',
          message: err.response.data.errors.channelname.join(''),
        });
      });
    } else {
      dispatch(
        'channels/joinOrCreate',
        {
          channelname,
          type: 'public',
        },
        { root: true }
      ).catch((err: ErrorResponseValidation) => {
        Notify.create({
          type: 'warning',
          message: err.response.data.errors.channelname.join(''),
        });
      });
    }
  },
  invite({ dispatch, rootState }, message: string) {
    if (!new RegExp(/^\/invite +\w+$/).test(message)) {
      Notify.create({
        type: 'warning',
        message: "Wrong command syntax. Use '/invite username'",
      });
      return;
    }
    const username = (message.match(new RegExp(/^\/invite +(\w+)$/)) || [
      '',
    ])[1];
    const channel = rootState.channels.active;
    const sender = rootState.auth.user;
    if (channel?.type === 'private' && channel?.ownerId !== sender?.id) {
      return Notify.create({
        type: 'warning',
        message: 'Only owner can invite users to private channels.',
      });
    }
    dispatch(
      'channels/invite',
      { username, channelname: channel?.channelname },
      { root: true }
    )
      .then(() =>
        Notify.create({
          type: 'positive',
          message: 'User invited to the channel',
        })
      )
      .catch((err: ErrorResponseValidation) => {
        Notify.create({
          type: 'warning',
          message: Object.values(err.response.data.errors)
            .map((errors) => errors.join(''))
            .join(''),
        });
      });
  },
  isActiveChannel({ rootState }): boolean {
    if (rootState.channels.active === null) {
      Notify.create({
        type: 'warning',
        message: 'Please, use command only in channel.',
      });
      return false;
    }
    return true;
  },
  revoke({ dispatch, rootState }, message: string) {
    if (!new RegExp(/^\/revoke +\w+$/).test(message)) {
      Notify.create({
        type: 'warning',
        message: "Wrong command syntax. Use '/revoke username'",
      });
      return;
    }
    const username = (message.match(new RegExp(/^\/revoke +(\w+)$/)) || [])[1];
    const channel = rootState.channels.active;
    const sender = rootState.auth.user;
    if (channel?.type === 'private' && channel?.ownerId !== sender?.id) {
      return Notify.create({
        type: 'warning',
        message: "Only owner can revoke user's invitation",
      });
    } else if (channel?.type === 'public') {
      return Notify.create({
        type: 'warning',
        message: 'Revoke command can be used only in private channels',
      });
    }
    dispatch(
      'channels/revoke',
      { username, channelname: channel?.channelname },
      { root: true }
    )
      .then(() =>
        Notify.create({
          type: 'positive',
          message: "User's invitation has been revoked",
        })
      )
      .catch((err: ErrorResponseValidation) => {
        Notify.create({
          type: 'warning',
          message: Object.values(err.response.data.errors)
            .map((errors) => errors.join(''))
            .join(''),
        });
      });
  },
  kick({ dispatch, rootState }, message: string) {
    if (!new RegExp(/^\/kick +\w+$/).test(message)) {
      Notify.create({
        type: 'warning',
        message: "Wrong command syntax. Use '/kick username'",
      });
      return;
    }
    const username = (message.match(new RegExp(/^\/kick +(\w+)$/)) || [])[1];
    const channel = rootState.channels.active;
    if (channel?.type === 'private')
      return Notify.create({
        type: 'warning',
        message: 'Kick command can be used only in public channels',
      });

    if (username === rootState.auth.user?.username)
      return Notify.create({
        type: 'warning',
        message: 'You can not kick yourself.',
      });
    dispatch(
      'channels/kick',
      { username, channelname: channel?.channelname },
      { root: true }
    )
      .then(() =>
        Notify.create({
          type: 'positive',
          message: 'User has been kicked.',
        })
      )
      .catch((err: ErrorResponseValidation) => {
        Notify.create({
          type: 'warning',
          message: Object.values(err.response.data.errors)
            .map((errors) => errors.join(''))
            .join(''),
        });
      });
  },
};

export default actions;
