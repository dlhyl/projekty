import axios, { AxiosError } from 'axios';
import { authManager } from 'src/services';

const api = axios.create({
  baseURL: process.env.API_URL,
  withCredentials: true,
  headers: {},
});

const DEBUG = process.env.NODE_ENV === 'development';

// add interceptor to add authorization header for api calls
api.interceptors.request.use(
  (config) => {
    const token = authManager.getToken();

    if (token !== null) {
      if (config.headers === undefined) {
        config.headers = {};
      }
      config.headers.Authorization = `Bearer ${token}`;
    }

    if (DEBUG) {
      console.info('-> ', config);
    }

    return config;
  },
  (error) => {
    if (DEBUG) {
      console.error('-> ', error);
    }

    return Promise.reject(error);
  }
);

// add interceptor for response to trigger logout
api.interceptors.response.use(
  (response) => {
    if (DEBUG) {
      console.info('<- ', response);
    }

    return response;
  },
  (error: AxiosError) => {
    if (DEBUG) {
      console.error('<- ', error.response);
    }

    // server api request returned unathorized response so we trrigger logout
    if (
      error.response != undefined &&
      error.response.status === 401
      // !error.response.config.dontTriggerLogout
    ) {
      authManager.logout();
    }

    return Promise.reject(error);
  }
);

export { api };
