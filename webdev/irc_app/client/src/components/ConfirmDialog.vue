<template>
  <q-dialog v-model="dialog" persistent>
    <q-card>
      <q-card-section class="row items-center q-pa-lg q-pb-sm">
        <q-icon name="warning" left size="lg" color="primary" />
        <div>
          <div class="text-subtitle1" v-if="typeof title === 'string'">
            {{ title }}
          </div>
          <template v-else v-for="(text, index) in title">
            <div class="text-subtitle1" :key="index" v-if="text !== ''">
              {{ text }}
            </div>
          </template>
        </div>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn label="Cancel" color="negative" v-close-popup />
        <q-btn label="OK" color="primary" @click="confirmFn" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue';
import { useStore } from '../store';

export default defineComponent({
  setup() {
    const $store = useStore();
    const dialog = computed({
      get: () => $store.state.commands.confirmDialog.visible,
      set: (value: boolean) => {
        $store.commit('commands/SET_CONFIRMDIALOG', value);
      },
    });
    const title = computed(() => $store.state.commands.confirmDialog.title);
    const confirmFn = computed(() => {
      return () => {
        $store.state.commands.confirmDialog.fn();
        $store.commit('commands/SET_CONFIRMDIALOG', false);
      };
    });

    return { dialog, title, confirmFn };
  },
});
</script>

<style scoped></style>
