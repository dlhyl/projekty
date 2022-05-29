import { boot } from 'quasar/wrappers';
import { SocketManager } from 'src/services/SocketManager';

const io = SocketManager.createManager(process.env.API_URL);

export default boot((params) => {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
  SocketManager.boot(params);
});

export { io };
