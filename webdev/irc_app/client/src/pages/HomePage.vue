<template>
  <q-page>
    <div
      class="row full-height full-width justify-center items-center text-center"
    >
      <div class="fixed-center q-pb-lg">
        <h3 class="q-mt-none q-mb-md">Communicate with your friends</h3>
        <p class="text-h5 q-mb-xl text-white">Easily chat with everyone.</p>
        <q-btn
          size="lg"
          color="info"
          class="q-mx-md q-px-lg"
          to="/register"
          rounded
          >Sign Up</q-btn
        >
        <q-btn
          size="lg"
          color="secondary"
          class="q-mx-md q-px-lg"
          to="/login"
          rounded
          >Login</q-btn
        >
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  setup() {
    return {};
  },
});
</script>

<style scoped></style>
