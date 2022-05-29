<template>
  <q-toolbar class="bg-grey-3 q-py-sm text-black row">
    <Mentionable
      class="col-grow q-mr-sm"
      :keys="['@', '＠']"
      :items="activeChannelUsers"
      insert-space
      @open="mentionOpened = true"
      @close="mentionOpened = false"
    >
      <template #no-result>
        <div class="q-pa-sm">No users found</div>
      </template>

      <template #item="{ item }">
        <q-avatar rounded size="md" class="q-mr-sm">
          <profile-image :image="item.image" />
        </q-avatar>
        <div>
          <span class="text-caption block">@{{ item.value }}</span>
          <span class="user-fullname block">{{ item.name }}</span>
        </div>
      </template>

      <q-input
        v-model="message"
        :disable="loading"
        standout="bg-grey-6 text-white"
        bg-color="grey-4"
        dense
        @keydown.enter="sendMessage"
        spellcheck="false"
      >
        <template v-slot:after>
          <q-btn
            round
            dense
            flat
            icon="send"
            @click="sendMessage"
            :disable="loading"
          />
        </template>
      </q-input>
    </Mentionable>
  </q-toolbar>
</template>

<script lang="ts">
import 'floating-vue/dist/style.css';
import { Channel } from 'src/contracts';
import { getFullName } from 'src/helpers';
import { useStore } from 'src/store';
import { computed, defineComponent, ref, watch } from 'vue';
import { Mentionable } from 'vue-mention';
import { useRouter } from 'vue-router';
import ProfileImage from './ProfileImage.vue';

export default defineComponent({
  setup() {
    const store = useStore();
    const $router = useRouter();
    const message = ref('');
    const loading = ref(false);
    const mentionOpened = ref(false);

    const activeChannel = computed(() => {
      return store.state.channels.active as Channel;
    });

    const activeChannelUsers = computed(() =>
      store.state.channels.activeChannelUsers.map((user) => {
        return {
          value: user.username,
          name: getFullName(user),
          image: user.image,
        };
      })
    );

    watch(message, async (newValue, oldValue) => {
      if (newValue !== oldValue && activeChannel.value !== null) {
        await store.dispatch(
          'channels/sendRT',
          newValue === '' ? null : newValue
        );
      }
    });

    const sendMessage = async () => {
      if (mentionOpened.value) return;

      loading.value = true;
      if (message.value.trim() !== '') {
        if (
          !(await store.dispatch('commands/checkCommands', {
            message: message.value,
            router: $router,
          }))
        ) {
          if (activeChannel.value !== null)
            await store.dispatch('channels/addMessage', {
              channel: activeChannel.value.channelname,
              message: message.value,
            });
        }
      }
      message.value = '';
      loading.value = false;
    };

    return { message, activeChannelUsers, mentionOpened, sendMessage, loading };
  },
  components: { Mentionable, ProfileImage },
});
</script>

<style lang="sass">
.mention-item
  padding: 4px 10px
  border-radius: 4px
  display: flex
  align-items: center

  .user-fullname
    font-size: 0.9rem
    line-height: 1.05rem

.mention-selected
  background: purple
  color: white
</style>
