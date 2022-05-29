import { User } from 'src/contracts';
import { MutationTree } from 'vuex';
import { AuthStateInterface } from './state';

const mutation: MutationTree<AuthStateInterface> = {
  AUTH_START(state: AuthStateInterface) {
    state.status = 'pending';
    state.errors = [];
  },
  AUTH_SUCCESS(state: AuthStateInterface, user: User | null) {
    state.status = 'success';
    state.user = user;
  },
  AUTH_ERROR(
    state: AuthStateInterface,
    errors: { message: string; field?: string }[]
  ) {
    state.status = 'error';
    state.errors = errors;
  },
};

export default mutation;
