import { Status, User } from 'src/contracts';

export const getStatusText = (status: Status): string => {
  switch (status) {
    case 'dnd':
      return 'Do Not Disturb';
    case 'offline':
      return 'Offline';
    case 'online':
      return 'Active';
    default:
      return 'Offline';
  }
};

export const getStatusColor = (status: Status): string => {
  switch (status) {
    case 'dnd':
      return 'orange';
    case 'offline':
      return 'red';
    case 'online':
      return 'green';
    default:
      return 'red';
  }
};

export const getFullName = (user: User): string => {
  return user.firstname + ' ' + user.lastname;
};

export const isMentioned = (text: string, username: string) =>
  new RegExp(`\\B@${username}`).test(text);
