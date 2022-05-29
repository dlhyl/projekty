import { Invitation, Status, User } from 'src/contracts';
import { authManager } from '.';
import { BootParams, SocketManager } from './SocketManager';

class ActivitySocketManager extends SocketManager {
  public subscribe({ store }: BootParams): void {
    this.socket.on('user:list', (onlineUsers: { [userid: number]: string }) => {
      Object.entries(onlineUsers).forEach(([user, status]) => {
        console.log(user, status);
        store.commit('channels/USER_STATUS', {
          userid: user,
          status: 'online',
        });
      });
      console.log('Online users list', onlineUsers);
    });

    this.socket.on('user:online', (user: User) => {
      console.log('online', user.id);
      store.commit('channels/USER_STATUS', {
        userid: user.id,
        status: 'online',
      });
    });

    this.socket.on('user:dnd', (user: User) => {
      console.log('dnd', user.id);

      store.commit('channels/USER_STATUS', {
        userid: user.id,
        status: 'dnd',
      });
    });

    this.socket.on('user:offline', (user: User) => {
      console.log('offline', user.id);

      store.commit('channels/USER_STATUS', {
        userid: user.id,
        status: 'offline',
      });
    });

    this.socket.on('getInvitation', (inv: Invitation) => {
      store.commit('channels/ADD_INVITATION', inv);
    });

    this.socket.on('revokeInvitation', (inv: Invitation) => {
      store.commit('channels/REMOVE_INVITATION', inv.channel);
    });

    authManager.onChange((token) => {
      if (token) {
        this.socket.connect();
      } else {
        this.socket.disconnect();
      }
    });
  }

  public changeStatus(status: Status): void {
    void this.emitAsync('changeStatus', status);
  }

  public invite(username: string, invitation: Invitation): Promise<Invitation> {
    return this.emitAsync('invite', { username, invitation });
  }

  public revoke(username: string, invitation: Invitation): Promise<Invitation> {
    return this.emitAsync('revoke', { username, invitation });
  }
}

export default new ActivitySocketManager('/');
