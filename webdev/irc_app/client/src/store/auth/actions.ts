import { LoginCredentials, RegisterData } from 'src/contracts';
import { authManager, authService } from 'src/services';
import { ActionTree } from 'vuex';
import { StateInterface } from '../index';
import { AuthStateInterface } from './state';

const actions: ActionTree<AuthStateInterface, StateInterface> = {
  async check({ commit, state, dispatch, rootState }) {
    try {
      commit('AUTH_START');
      const user = await authService.me();

      if (user?.id !== state.user?.id) {
        for (const channel of user?.privateChannels || []) {
          await dispatch('channels/join', channel, { root: true });
        }
        for (const channel of user?.publicChannels || []) {
          await dispatch('channels/join', channel, { root: true });
        }
        commit('channels/SET_INVITATIONS', user?.invitations, { root: true });
      }
      if (user && !(user.id in rootState.channels.userStatus))
        commit(
          'channels/USER_STATUS',
          { userid: user.id, status: 'online' },
          { root: true }
        );
      commit('AUTH_SUCCESS', user);
      return user !== null;
    } catch (err) {
      commit('AUTH_ERROR', err);
      throw err;
    }
  },
  async register({ commit }, form: RegisterData) {
    try {
      commit('AUTH_START');
      const user = await authService.register(form);
      commit('AUTH_SUCCESS', null);
      return user;
    } catch (err) {
      commit('AUTH_ERROR', err);
      throw err;
    }
  },
  async login({ commit }, credentials: LoginCredentials) {
    try {
      commit('AUTH_START');
      const apiToken = await authService.login(credentials);
      commit('AUTH_SUCCESS', null);
      // save api token to local storage and notify listeners
      authManager.setToken(apiToken.token);
      return apiToken;
    } catch (err) {
      commit('AUTH_ERROR', err);
      throw err;
    }
  },
  async logout({ commit, dispatch }) {
    try {
      commit('AUTH_START');
      await authService.logout();
      await dispatch('channels/leave', null, { root: true });
      commit('AUTH_SUCCESS', null);
      // remove api token and notify listeners
      authManager.removeToken();
    } catch (err) {
      commit('AUTH_ERROR', err);
      throw err;
    }
  },
};

export default actions;
