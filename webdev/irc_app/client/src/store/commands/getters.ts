import { GetterTree } from 'vuex';
import { StateInterface } from '../index';
import { CommandsInterface } from './state';

const getters: GetterTree<CommandsInterface, StateInterface> = {
  someGetter(/* context */) {
    // your code
  },
};

export default getters;
