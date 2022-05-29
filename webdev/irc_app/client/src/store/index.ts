import { store } from 'quasar/wrappers';
import { InjectionKey } from 'vue';
import {
  createStore,
  Store as VuexStore,
  useStore as vuexUseStore,
} from 'vuex';
import auth from './auth';
import { AuthStateInterface } from './auth/state';
import channels from './channels';
import { ChannelsStateInterface } from './channels/state';
import commands from './commands';
import { CommandsInterface } from './commands/state';

export interface StateInterface {
  commands: CommandsInterface;
  auth: AuthStateInterface;
  channels: ChannelsStateInterface;
}

// provide typings for `useStore` helper
export const storeKey: InjectionKey<VuexStore<StateInterface>> =
  Symbol('vuex-key');

export default store(function (/* { ssrContext } */) {
  const Store = createStore<StateInterface>({
    modules: {
      commands,
      auth,
      channels,
    },

    // enable strict mode (adds overhead!)
    // for dev mode and --debug builds only
    strict: !!process.env.DEBUGGING,
  });

  return Store;
});

export function useStore() {
  return vuexUseStore(storeKey);
}
