/*
|--------------------------------------------------------------------------
| Websocket events
|--------------------------------------------------------------------------
|
| This file is dedicated for defining websocket namespaces and event handlers.
|
*/

import Ws from '@ioc:Ruby184/Socket.IO/Ws';

Ws.namespace('/')
  .connected('ActivityController.onConnected')
  .disconnected('ActivityController.onDisconnected')
  .on('invite', 'ActivityController.invite')
  .on('revoke', 'ActivityController.revoke')
  .on('changeStatus', 'ActivityController.changeStatus');

// this is dynamic namespace, in controller methods we can use params.name
Ws.namespace('channels/:name')
  .middleware('channel')
  .on('loadUsers', 'MessageController.loadUsers')
  .on('loadMessages', 'MessageController.loadMessages')
  .on('loadLastMessage', 'MessageController.loadLastMessage')
  .on('addMessage', 'MessageController.addMessage')
  .on('setNotifications', 'MessageController.setNotifications')
  .on('leave', 'MessageController.leave')
  .on('joinActive', 'MessageController.joinActive')
  .on('updateMessage', 'MessageController.updateMessage')
  .on('leaveActive', 'MessageController.leaveActive')
  .on('kick', 'MessageController.kick')
  .disconnected('MessageController.leaveActive');
