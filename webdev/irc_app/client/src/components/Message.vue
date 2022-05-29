<template>
  <q-chat-message
    :bg-color="
      isMentioned(msg.text, activeUser.username) ? 'secondary' : 'primary'
    "
    text-color="white"
    :name="msg.user.firstname"
    :text="[msg.text]"
    :stamp="new Date(msg.createdAt).toLocaleTimeString()"
  >
    <template v-slot:avatar>
      <q-avatar rounded size="lg" class="q-mr-md">
        <profile-image :image="msg.user.image" />
        <q-badge :color="statusColor" floating rounded></q-badge>
      </q-avatar>
    </template>
  </q-chat-message>
</template>

<script lang="ts">
import { SerializedMessage as Message, User } from 'src/contracts';
import { getStatusColor, isMentioned } from 'src/helpers';
import { useStore } from 'src/store';
import { computed, defineComponent, PropType, toRef } from 'vue';
import ProfileImage from './ProfileImage.vue';

export default defineComponent({
  props: {
    message: {
      type: Object as PropType<Message>,
      required: true,
    },
  },
  setup(props) {
    const msg = toRef(props, 'message');
    const store = useStore();
    const activeUser = computed(() => {
      return store.state.auth.user as User;
    });
    const statusColor = computed(() =>
      getStatusColor(store.state.channels.userStatus[msg.value.userId])
    );
    return {
      msg,
      activeUser,
      statusColor,
      isMentioned,
    };
  },
  components: { ProfileImage },
});
</script>

<style scoped></style>
