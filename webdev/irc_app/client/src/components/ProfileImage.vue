<template>
  <img v-if="image !== null" :src="image" />
  <q-icon
    v-else
    name="person"
    class="text-grey-2 bg-grey-5 full-width full-height"
    size="md"
  />
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  props: {
    image: {
      default: null,
      type: String,
    },
  },
  setup() {
    return {};
  },
});
</script>
