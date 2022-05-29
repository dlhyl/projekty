// precacheAndRoute(self.__WB_MANIFEST);
// registerRoute(new NavigationRoute(createHandlerBoundToURL('/index.html')));
// setCacheNameDetails({ prefix: 'pwa-offline' });

/**
 * The workboxSW.precacheAndRoute() method efficiently caches and responds to
 * requests for URLs in the manifest.
 *
 */

import { clientsClaim, setCacheNameDetails } from 'workbox-core';
import { createHandlerBoundToURL, precacheAndRoute } from 'workbox-precaching';
import { NavigationRoute, registerRoute } from 'workbox-routing';

setCacheNameDetails({
  prefix: 'vpwa',
});
// eslint-disable-next-line @typescript-eslint/no-unsafe-call
self.skipWaiting();
clientsClaim();
/**
 * The precacheAndRoute() method efficiently caches and responds to
 * requests for URLs in the manifest.
 * See https://goo.gl/S9QRab
 */

precacheAndRoute(
  [
    {
      url: 'app.js',
    },
    {
      url: 'fonts/flUhRq6tzZclQEJ-Vdg-IuiaDsNa.1dd1bb36.woff',
    },
    {
      url: 'fonts/flUhRq6tzZclQEJ-Vdg-IuiaDsNcIhQ8tQ.f54bbe10.woff2',
    },
    {
      url: 'index.html',
    },
    {
      url: 'login',
    },
    {
      url: 'manifest.json',
    },
    {
      url: 'src_layouts_HomeLayout_vue.js',
    },
    {
      url: 'src_pages_Auth_Login_vue.js',
    },
    {
      url: 'src_pages_Auth_Register_vue.js',
    },
    {
      url: 'src_pages_Error404_vue.js',
    },
    {
      url: 'vendor.js',
    },
  ],
  {}
);
registerRoute(
  new NavigationRoute(createHandlerBoundToURL('/index.html'), {
    denylist: [/service-worker\.js$/, /workbox-(.)*\.js$/],
  })
);
