/* eslint-disable @typescript-eslint/no-unsafe-argument */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
import { Channel, ChannelType, RawMessage, Status } from 'src/contracts';
import { activityService, channelService } from 'src/services';
import { ActionTree } from 'vuex';
import { StateInterface } from '../index';
import { ChannelsStateInterface } from './state';

const actions: ActionTree<ChannelsStateInterface, StateInterface> = {
  async join({ commit }, channel: Channel) {
    try {
      commit('LOADING_START');
      const lastMessage = await channelService
        .join(channel.channelname)
        .loadLastMessage();
      commit('LOADING_SUCCESS', { channel, message: lastMessage });
    } catch (err) {
      commit('LOADING_ERROR', err);
      console.log(err);
      throw err;
    }
  },

  async loadMessages(
    { commit },
    { channel, index }: { channel: Channel; index: number }
  ) {
    try {
      const messagesResponse = await channelService
        .in(channel.channelname)
        ?.loadMessages(index);
      if (messagesResponse?.data.length)
        commit('LOAD_MSGS', messagesResponse.data);
      return messagesResponse?.hasMore;
    } catch (err) {
      console.log(err);
      throw err;
    }
  },

  async leave({ getters, commit, state, dispatch }, channel: Channel | null) {
    const leaving: Channel[] =
      channel !== null
        ? [channel]
        : (getters.joinedPrivateChannels as Channel[]).concat(
            getters.joinedPublicChannels as Channel[]
          );

    for (const c of leaving) {
      if (c === state.active) await dispatch('leaveActive', c.channelname);
      channelService.leave(c.channelname);
      commit('CLEAR_CHANNEL', c.channelname);
    }
  },

  async leaveActive({ commit }, channel: Channel) {
    await channelService.in(channel.channelname)?.leaveActive();
    commit('CLEAR_ACTIVE');
  },

  async addMessage(
    { commit },
    { channel, message }: { channel: string; message: RawMessage }
  ) {
    const newMessage = await channelService.in(channel)?.addMessage(message);
    commit('SET_LASTMSG', { channel, message: newMessage });
    commit('NEW_MESSAGE', { channel, message: newMessage });
  },

  async setUserStatus(
    { commit, state },
    { userid, status }: { userid: number; status: Status }
  ) {
    if (!state.userStatus[userid] || state.userStatus[userid] === status)
      return;

    activityService.changeStatus(status);

    if (
      state.userStatus[userid] === 'offline' ||
      status === 'offline' ||
      status === 'online'
    ) {
      for (const socket of channelService.all())
        await socket.setNotifications(status === 'online');
    }

    if (state.userStatus[userid] === 'offline')
      for (const socket of channelService.all()) {
        commit('SET_LASTMSG', {
          channel: socket.namespace.split('/').pop() as string,
          message: await socket.loadLastMessage(),
        });
        if (
          state.active &&
          state.active?.channelname === socket.namespace.split('/').pop()
        ) {
          commit('RESET_ACTIVE');
        }
      }
    commit('USER_STATUS', { userid, status });
  },

  async setActiveChannel(
    { state, commit },
    channelName: string
  ): Promise<boolean> {
    if (state.active?.channelname === channelName) return true;
    if (
      channelName in state.privateChannels ||
      channelName in state.publicChannels
    ) {
      commit('SET_ACTIVE', channelName);
      const activeChannelUsers = await channelService
        .in(channelName)
        ?.laodUsers();
      if (activeChannelUsers) commit('SET_USERS', activeChannelUsers);
      const rtmessages = await channelService.in(channelName)?.joinActive();
      rtmessages?.forEach((msg) => {
        commit('SET_RTMESSAGE', msg);
      });
      return true;
    }
    return false;
  },
  async sendRT({ state }, message: string) {
    await channelService
      .in(state.active?.channelname || '')
      ?.sendRealTimeMessage(message);
  },
  async create(
    { dispatch },
    { channelname, type }: { channelname: string; type: ChannelType }
  ): Promise<unknown> {
    return channelService.create(channelname, type).then((channel: Channel) => {
      return dispatch('join', channel).then(() => channel);
    });
  },
  async joinOrCreate(
    { dispatch },
    { channelname, type }: { channelname: string; type: ChannelType }
  ): Promise<unknown> {
    return channelService
      .joinOrCreate(channelname, type)
      .then((channel: Channel) => {
        return dispatch('join', channel).then(() => channel);
      });
  },
  invite(
    {},
    { username, channelname }: { channelname: string; username: string }
  ) {
    return channelService.invite(channelname, username).then(async (inv) => {
      await activityService.invite(username, inv);
      return inv;
    });
  },
  revoke(
    {},
    { username, channelname }: { channelname: string; username: string }
  ) {
    return channelService.revoke(channelname, username).then(async (inv) => {
      await activityService.revoke(username, inv);
      return inv;
    });
  },
  handleInvitation(
    { dispatch, commit },
    { channelname, confirm }: { channelname: string; confirm: boolean }
  ) {
    return channelService
      .handleInvitation(channelname, confirm)
      .then(async (channel: Channel) => {
        if (confirm) await dispatch('join', channel);
        commit('REMOVE_INVITATION', channel);
      });
  },
  kick(
    { commit },
    { username, channelname }: { channelname: string; username: string }
  ) {
    return channelService.kick(channelname, username).then(async (kicked) => {
      if (kicked) {
        console.log('socket kick..');
        await channelService.in(channelname)?.kick(username);
        commit('REMOVE_USER', username);
      }
      return kicked;
    });
  },
};

export default actions;
