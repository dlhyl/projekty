import { Channel } from 'src/contracts';
import { GetterTree } from 'vuex';
import { StateInterface } from '../index';
import { ChannelsStateInterface } from './state';

const getters: GetterTree<ChannelsStateInterface, StateInterface> = {
  joinedPrivateChannels(context): Channel[] {
    return Object.values(context.privateChannels);
  },
  joinedPublicChannels(context): Channel[] {
    return Object.values(context.publicChannels);
  },
};

export default getters;
