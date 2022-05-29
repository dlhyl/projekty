import type { AxiosRequestConfig } from 'axios';
import { api } from 'src/boot/axios';
import type {
  ApiToken,
  LoginCredentials,
  RegisterData,
  User,
} from 'src/contracts';

class AuthService {
  async me(dontTriggerLogout = false): Promise<User | null> {
    return api
      .get('auth/me', { dontTriggerLogout } as AxiosRequestConfig)
      .then((response) => response.data as User)
      .catch(() => {
        return null;
      });
  }

  async register(data: RegisterData): Promise<User> {
    const response = await api.post<User>('auth/register', data);
    return response.data;
  }

  async login(credentials: LoginCredentials): Promise<ApiToken> {
    const response = await api.post<ApiToken>('auth/login', credentials);
    return response.data;
  }

  async logout(): Promise<void> {
    await api.post('auth/logout');
  }
}

export default new AuthService();
