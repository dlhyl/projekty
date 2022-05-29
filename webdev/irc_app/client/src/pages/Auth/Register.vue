<template>
  <q-page>
    <q-card class="auth-card fixed-center bg-white text-primary shadow-10">
      <q-card-section>
        <div class="text-h5 text-bold">Register</div>
      </q-card-section>

      <q-card-section>
        <q-form
          class="q-gutter-y-sm text-white"
          @reset="resetFields"
          @submit.stop="submitRegister"
        >
          <div class="row q-gutter-x-md">
            <q-input
              filled
              class="col"
              name="firstName"
              id="firstName"
              v-model.trim="firstname"
              label="First Name"
              @focus="v$.firstname.$reset()"
              :error="v$.firstname.$error"
              :error-message="(v$.firstname.$errors[0] || {}).$message"
              lazy-rules="ondemand"
            />
            <q-input
              filled
              class="col"
              name="lastName"
              id="lastName"
              v-model.trim="lastname"
              label="Last Name"
              @focus="v$.lastname.$reset()"
              :error="v$.lastname.$error"
              :error-message="(v$.lastname.$errors[0] || {}).$message"
              lazy-rules="ondemand"
            />
          </div>
          <q-input
            filled
            name="email"
            id="email"
            v-model.trim="email"
            label="Email"
            @focus="v$.email.$reset()"
            :error="v$.email.$error"
            :error-message="(v$.email.$errors[0] || {}).$message"
            lazy-rules="ondemand"
          />
          <q-input
            filled
            name="userName"
            id
            v-model.trim="username"
            label="User Name"
            @focus="v$.username.$reset()"
            :error="v$.username.$error"
            :error-message="(v$.username.$errors[0] || {}).$message"
            lazy-rules="ondemand"
          />
          <q-input
            filled
            name="password"
            id="password"
            v-model="password"
            label="Password"
            :type="showPassword ? 'text' : 'password'"
            bottom-slots
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
          <q-input
            filled
            name="password_confirmation"
            id="password_confirmation"
            v-model="passwordConfirmation"
            label="Confirm Password"
            :type="showPassword ? 'text' : 'password'"
            bottom-slots
            @focus="v$.passwordConfirmation.$reset()"
            :error="v$.passwordConfirmation.$error"
            :error-message="(v$.passwordConfirmation.$errors[0] || {}).$message"
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
            Already have an account?
            <router-link :to="{ name: 'login' }">Login.</router-link>
          </div>
          <div>
            <q-btn
              label="Register"
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
import useVuelidate, { ValidationRule } from '@vuelidate/core';
import {
  alpha,
  and,
  email,
  helpers,
  maxLength,
  minLength,
  required,
  sameAs,
} from '@vuelidate/validators';
import { ErrorResponseValidation } from 'src/contracts';
import { useStore } from 'src/store';
import { computed, defineComponent, reactive, ref, toRef, toRefs } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  setup() {
    const $store = useStore();
    const $router = useRouter();
    const $externalResults = reactive({});
    const form = reactive({
      username: '',
      firstname: '',
      lastname: '',
      password: '',
      passwordConfirmation: '',
      email: '',
    });
    const showPassword = ref(false);

    const rules = {
      firstname: {
        required: helpers.withMessage('Firstname cannot be empty', required),
        alpha: helpers.withMessage(
          'Firstname must contain only letters',
          alpha
        ),
      },
      lastname: {
        required: helpers.withMessage('Lastname cannot be empty', required),
        alpha: helpers.withMessage('Lastname must contain only letters', alpha),
      },
      password: {
        required: helpers.withMessage('Password cannot be empty', required),
        minLength: helpers.withMessage(
          'Password must be at least 6 characters',
          minLength(6)
        ),
      },
      passwordConfirmation: {
        required: helpers.withMessage('Password cannot be empty', required),
        minLength: helpers.withMessage(
          'Password must be at least 6 characters',
          minLength(6)
        ),
        sameAs: helpers.withMessage(
          'Passwords do not match',
          sameAs(toRef(form, 'password'))
        ),
      },
      username: {
        required: helpers.withMessage('Username cannot be empty', required),
        between: helpers.withMessage(
          'Username must have 5-20 characters',
          and(minLength(5), maxLength(20))
        ),
        alphanum: helpers.withMessage(
          'Username must contain only letter, digits and undescore',
          helpers.regex(new RegExp(/^\w+$/)) as ValidationRule<unknown>
        ),
        startwithletter: helpers.withMessage(
          'Username must start with letter',
          helpers.regex(new RegExp(/^[A-Za-z]\w+$/)) as ValidationRule<unknown>
        ),
      },
      email: {
        required: helpers.withMessage('E-mail cannot be empty', required),
        email: helpers.withMessage(
          ({ $model }) => $model + ' is not valid e-mail.',
          email
        ),
      },
    };

    const v$ = useVuelidate(rules, form, {
      $lazy: true,
      $externalResults,
    });

    const submitRegister = async () => {
      v$.value.$clearExternalResults();
      const isValid = await v$.value.$validate();
      if (isValid) {
        $store
          .dispatch('auth/register', form)
          .then(() => $router.push(redirectPath.value))
          .catch((err: ErrorResponseValidation) => {
            Object.assign($externalResults, err.response.data.errors);
          });
      }
    };

    const resetFields = () => {
      form.email = '';
      form.firstname = '';
      form.lastname = '';
      form.password = '';
      form.passwordConfirmation = '';
      form.username = '';
    };

    const redirectPath = computed(() => {
      return { name: 'login' };
    });

    const loading = computed(() => {
      return $store.state.auth.status === 'pending';
    });

    return {
      ...toRefs(form),
      loading,
      showPassword,
      submitRegister,
      resetFields,
      v$,
    };
  },
});
</script>

<style scoped>
.auth-card {
  border-radius: 1rem;
  width: 450px;
}
</style>
