<template>
  <q-dialog v-model="showDialog">
    <q-card style="min-width: 400px">
      <q-card-section>
        <div class="text-h6">
          New
          {{ capitalizedType }}
          Channel
        </div>
      </q-card-section>
      <q-form class="q-gutter-md" @submit="submitDialog">
        <q-card-section>
          <q-input
            class="q-mb-md"
            filled
            v-model="channelname"
            label="Channel name*"
            hint="The unique name of your channel"
            lazy-rules="ondemand"
            @focus="v$.channelname.$reset()"
            :error="v$.channelname.$error"
            :error-message="(v$.channelname.$errors[0] || {}).$message"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" v-close-popup />
          <q-btn
            flat
            label="Submit"
            type="submit"
            class="bg-primary text-white"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import useVuelidate, { ValidationRule } from '@vuelidate/core';
import {
  and,
  helpers,
  maxLength,
  minLength,
  required,
} from '@vuelidate/validators';
import { useQuasar } from 'quasar';
import { ErrorResponseValidation } from 'src/contracts';
import { useStore } from 'src/store';
import { computed, defineComponent, reactive, watch } from 'vue';

export default defineComponent({
  setup() {
    const store = useStore();
    const $q = useQuasar();

    const showDialog = computed({
      get: () => store.state.commands.newChannelDialog.visible,
      set: (value: boolean) => {
        store.commit('commands/SET_NEWCHANNELDIALOG', value);
      },
    });

    const channelname = computed({
      get: () => store.state.commands.newChannelDialog.channelname,
      set: (value: string) => {
        store.commit('commands/SET_NEWCHANNELDIALOGPROPS', {
          channelname: value,
        });
      },
    });
    const type = computed(() => store.state.commands.newChannelDialog.type);

    watch(showDialog, (newValue: boolean) => {
      if (newValue) v$.value.$reset();
    });

    const $externalResults = reactive({});
    const rules = {
      channelname: {
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
    };
    const v$ = useVuelidate(
      rules,
      { channelname },
      {
        $lazy: true,
        $externalResults,
      }
    );

    const submitDialog = async () => {
      v$.value.$clearExternalResults();
      const isValid = await v$.value.$validate();
      if (isValid) {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        store
          .dispatch('channels/create', {
            channelname: channelname.value,
            type: type.value,
          })
          .then(() => {
            $q.notify({
              color: 'green-4',
              textColor: 'white',
              icon: 'check',
              message: `Created New Channel ${channelname.value}`,
            });
            showDialog.value = false;
          })
          .catch((err: ErrorResponseValidation) => {
            Object.assign($externalResults, err.response.data.errors);
          });
      }
    };

    const capitalizedType = type.value.replace(/\b\w/g, (l) => l.toUpperCase());

    return {
      channelname,
      submitDialog,
      showDialog,
      capitalizedType,
      v$,
    };
  },
});
</script>

<style scoped></style>
