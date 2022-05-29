import { ChannelType } from 'src/contracts';

export interface CommandsInterface {
  userlistDialog: boolean;
  confirmDialog: {
    visible: boolean;
    fn: (...args: unknown[]) => void;
    title: string | string[];
  };
  newChannelDialog: {
    visible: boolean;
    channelname: string;
    type: ChannelType;
  };
}

function state(): CommandsInterface {
  return {
    userlistDialog: false,
    confirmDialog: {
      visible: false,
      fn: () => undefined,
      title: '',
    },
    newChannelDialog: {
      visible: false,
      channelname: '',
      type: 'private',
    },
  };
}

export default state;
