<template>
  <q-page>
    <q-card class="auth-card fixed-center bg-white text-primary shadow-10">
      <q-card-section>
        <div class="text-h5 text-bold">Login</div>
      </q-card-section>

      <q-card-section>
        <q-form
          class="q-gutter-md text-white"
          @reset="resetLogin"
          @submit.stop="submitLogin"
        >
          <q-input
            filled
            v-model.trim="email"
            name="email"
            label="E-mail"
            @focus="v$.email.$reset()"
            :error="v$.email.$error"
            :error-message="(v$.email.$errors[0] || {}).$message"
            lazy-rules="ondemand"
          />

          <q-input
            filled
            :type="showPassword ? 'text' : 'password'"
            name="password"
            v-model="password"
            label="Password"
            @focus="v$.password.$reset()"
            :error="v$.password.$error"
            :error-message="(v$.password.$errors[0] || {}).$message"
            lazy-rules="ondemand"
          >
            <template v-slot:append>
              <q-icon
                :name="showPassword ? 'visibility' : 'visibility_off'"
                class="cursor-pointer"
                @click="showPassword = !showPassword"
              />
            </template>
          </q-input>

          <div class="text-small text-black q-pb-md">
            Don't have an account?
            <router-link :to="{ name: 'register' }">Sign up.</router-link>
          </div>

          <div>
            <q-btn
              label="Login"
              type="submit"
              color="secondary"
              :loading="loading"
            />
            <q-btn label="Reset" type="reset" color="info" class="q-ml-sm" />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script lang="ts">
import useVuelidate from '@vuelidate/core';
import { email, helpers, minLength, required } from '@vuelidate/validators';
import { ErrorResponseValidation } from 'src/contracts';
import { useStore } from 'src/store';
import { computed, defineComponent, reactive, ref, toRefs } from 'vue';
import { RouteLocationRaw, useRoute, useRouter } from 'vue-router';

export default defineComponent({
  setup() {
    const $store = useStore();
    const $route = useRoute();
    const $router = useRouter();
    const $externalResults = reactive({});
    const rules = {
      email: {
        required: helpers.withMessage('E-mail cannot be empty', required),
        email: helpers.withMessage(
          ({ $model }) => $model + ' is not valid e-mail.',
          email
        ),
      },
      password: {
        required: helpers.withMessage('Password cannot be empty', required),
        minLength: helpers.withMessage(
          'Password must be at least 6 characters',
          minLength(6)
        ),
      },
    };
    const credentials = reactive({ email: '', password: '' });
    const showPassword = ref(false);
    const v$ = useVuelidate(rules, credentials, {
      $lazy: true,
      $externalResults,
    });
    const redirectPath = computed(() => {
      return (
        ($route.query.redirect as string) ||
        ({ name: 'channels' } as RouteLocationRaw)
      );
    });
    const loading = computed(() => {
      return $store.state.auth.status === 'pending';
    });

    const submitLogin = async () => {
      v$.value.$clearExternalResults();
      const isValid = await v$.value.$validate();
      if (isValid) {
        $store
          .dispatch('auth/login', credentials)
          .then(() => $router.push(redirectPath.value))
          .catch((err: ErrorResponseValidation) => {
            Object.assign($externalResults, err.response.data.errors);
          });
      }
    };

    const resetLogin = () => {
      credentials.email = '';
      credentials.password = '';
    };

    return {
      ...toRefs(credentials),
      showPassword,
      loading,
      submitLogin,
      resetLogin,
      v$,
    };
  },
});
</script>

<style lang="sass" scoped>
.auth-card
  border-radius: 1rem
  min-width: 350px
</style>
