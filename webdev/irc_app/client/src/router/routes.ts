import { StateInterface } from 'src/store';
import { RouteRecordRaw } from 'vue-router';
import { Store as VuexStore } from 'vuex';

const getRoutes: (store: VuexStore<StateInterface>) => RouteRecordRaw[] = (
  store
) => [
  {
    path: '/',
    component: () => import('layouts/HomeLayout.vue'),
    children: [
      {
        name: 'home',
        path: '',
        meta: { guestOnly: true },
        component: () => import('src/pages/HomePage.vue'),
      },
      {
        name: 'login',
        path: 'login',
        meta: { guestOnly: true },
        component: () => import('src/pages/Auth/Login.vue'),
      },
      {
        name: 'register',
        path: 'register',
        meta: { guestOnly: true },
        component: () => import('src/pages/Auth/Register.vue'),
      },
    ],
  },
  {
    path: '/messages',
    meta: { requiresAuth: true },
    component: () => import('src/layouts/MessagesLayout.vue'),
    children: [
      {
        path: '',
        name: 'channels',
        component: () => import('src/pages/Messages/LandingPage.vue'),
      },
    ],
  },
  {
    path: '/c/:channelName',
    meta: { requiresAuth: true },
    component: () => import('src/layouts/MessagesLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('src/pages/Messages/Channel.vue'),
        beforeEnter: async (to, from, next) => {
          const validChannel = (await store.dispatch(
            'channels/setActiveChannel',
            to.params.channelName
          )) as boolean;
          if (!validChannel) {
            next({ name: 'not-found' });
          } else next();
        },
      },
    ],
  },
  {
    path: '/:catchAll(.*)*',
    name: 'not-found',
    component: () => import('pages/Error404.vue'),
  },
];

export default getRoutes;
