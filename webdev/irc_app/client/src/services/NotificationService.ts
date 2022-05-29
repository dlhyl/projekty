import { AppVisibility } from 'quasar';
import { User } from 'src/contracts';
import { getFullName } from 'src/helpers';

export function notify(sender: User, message: string) {
  if (AppVisibility.appVisible) return;

  if (!('Notification' in window)) {
    alert('This browser does not support desktop notification');
  } else if (Notification.permission === 'granted') {
    createNotification(getFullName(sender), message);
  } else if (Notification.permission !== 'denied') {
    void Notification.requestPermission().then(function (permission) {
      if (permission === 'granted') {
        createNotification(getFullName(sender), message);
      }
    });
  }
}

function createNotification(senderName: string, message: string) {
  return new Notification(
    message.length > 33 ? message.substring(0, 30) + '...' : message,
    {
      body: senderName,
    }
  );
}
