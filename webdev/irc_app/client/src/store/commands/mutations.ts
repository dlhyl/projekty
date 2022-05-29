import { ChannelType } from 'src/contracts';
import { MutationTree } from 'vuex';
import { CommandsInterface } from './state';

const mutation: MutationTree<CommandsInterface> = {
  SET_USERLIST(state: CommandsInterface, userlist: boolean) {
    state.userlistDialog = userlist;
  },
  SET_CONFIRMDIALOG(state: CommandsInterface, value: boolean) {
    state.confirmDialog.visible = value;
  },
  SET_DIALOGPROPS(
    state: CommandsInterface,
    {
      title,
      fn,
    }: { title: string | string[]; fn: (...args: unknown[]) => void }
  ) {
    state.confirmDialog.title = title;
    state.confirmDialog.fn = fn;
  },
  SET_NEWCHANNELDIALOG(state: CommandsInterface, value: boolean) {
    state.newChannelDialog.visible = value;
  },
  SET_NEWCHANNELDIALOGPROPS(
    state: CommandsInterface,
    {
      channelname,
      type,
    }: {
      channelname?: string;
      type?: ChannelType;
    }
  ) {
    if (typeof channelname !== 'undefined')
      state.newChannelDialog.channelname = channelname;
    if (typeof type !== 'undefined') state.newChannelDialog.type = type;
  },
};

export default mutation;
