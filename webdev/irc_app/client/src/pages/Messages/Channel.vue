<template>
  <q-page
    class="row items-center justify-evenly bg-grey-2"
    style="height: 100%"
  >
    <div
      style="
        height: 100%;
        overflow: hidden;
        display: flex;
        flex-direction: column;
        flex-grow: 1;
      "
    >
      <q-infinite-scroll
        ref="infiniteScroll"
        id="message-container"
        class="q-px-md q-my-sm"
        :offset="60"
        @load="onLoad"
        reverse
        style="overflow-y: auto"
        scroll-target="#message-container"
      >
        <q-chat-message
          class="q-py-lg nomessages"
          v-if="!messageLoading"
          label="No more messages to load."
        />

        <template v-slot:loading>
          <div class="row justify-center q-py-lg">
            <q-spinner color="primary" name="dots" size="30px" />
          </div>
        </template>

        <template v-for="(message, index) in messages" :key="message.id">
          <template
            v-if="
              (index > 0 &&
                !isSameDay(message.createdAt, messages[index - 1].createdAt)) ||
              index == 0
            "
          >
            <q-chat-message
              :label="new Date(message.createdAt).toLocaleDateString()"
            />
          </template>
          <message-vue :message="message" />
        </template>

        <q-chat-message
          id="rtmsg"
          bg-color="grey-4"
          color="black"
          class="q-mt-md"
          v-if="rtUsers.length > 0"
        >
          <div>
            <q-popup-proxy
              self="bottom middle"
              anchor="top middle"
              :no-parent-event="true"
              v-model="rtShown"
              class="q-pa-sm"
              max-width="350px"
              style="overflow-wrap: break-word"
            >
              {{ rtMessages[rtActiveUser] }}
            </q-popup-proxy>
            <q-spinner-dots color="black" size="sm" class="q-mr-sm" />
            <template v-for="(user, index) in rtUsers" :key="user.id">
              {{ index > 0 ? ',' : '' }}
              <span
                @click.stop.prevent="
                  rtActiveUser = user.id;
                  rtShown = true;
                "
                :class="['text-primary cursor-pointer']"
              >
                {{ user.firstname }}
              </span>
            </template>
            {{ rtUsers.length === 1 ? 'is' : 'are' }}
            typing a message
          </div>
        </q-chat-message>
      </q-infinite-scroll>
    </div>
  </q-page>
</template>

<script lang="ts">
import { QInfiniteScroll } from 'quasar';
import MessageVue from 'src/components/Message.vue';
import { Channel } from 'src/contracts';
import { useStore } from 'src/store';
import { computed, defineComponent, nextTick, Ref, ref, watch } from 'vue';

export default defineComponent({
  name: 'Channel',
  setup() {
    const store = useStore();
    const messageLoading = ref(true);

    const onLoad = (_index: number, done: (stop?: boolean) => void) => {
      store
        .dispatch('channels/loadMessages', {
          channel: activeChannel.value,
          index: _index,
        })
        .then((hasMore: boolean) => {
          messageLoading.value = hasMore;
          done(!hasMore);
        })
        .catch((err) => console.log(err));
    };

    const mentionOpened = ref(false);

    const activeChannel = computed(() => {
      return store.state.channels.active as Channel;
    });

    const messages = computed(() => {
      return store.state.channels.activeMessages;
    });

    watch(
      () => [activeChannel.value, messages.value.length === 0],
      (newValue, oldValue) => {
        if (
          (newValue[0] as Channel).id !== (oldValue[0] as Channel).id ||
          (newValue[1] !== oldValue[1] && newValue[1])
        ) {
          infiniteScroll.value.reset();
          infiniteScroll.value.resume();
        }
      }
    );

    const infiniteScroll = ref() as Ref<QInfiniteScroll>;

    const isSameDay = (date1: string, date2: string): boolean => {
      const d1 = new Date(date1);
      const d2 = new Date(date2);
      return d1.toDateString() == d2.toDateString();
    };

    const rtMessages = computed(() => {
      return store.state.channels.rtMessages;
    });

    const rtUsers = computed(() => {
      return Object.keys(store.state.channels.rtMessages).map((id) =>
        store.state.channels.activeChannelUsers.find(
          (u) => u.id.toString() === id
        )
      );
    });

    watch(
      rtUsers,
      (newV, oldV) => {
        if (newV.length < oldV.length) {
          rtShown.value = false;
          rtActiveUser.value = null;
        }
      },
      { deep: true }
    );
    const rtActiveUser = ref(null as string | null);
    const rtShown = ref(false);

    watch(
      () => [messages.value.length, rtUsers.value.length > 0],
      async () => {
        await nextTick();
        const msgContainer = <HTMLElement>(
          document.getElementById('message-container')
        );
        const lastMessage = <HTMLElement>msgContainer.lastElementChild;
        if (
          msgContainer.scrollHeight -
            msgContainer.offsetHeight -
            msgContainer.scrollTop <=
          2 * lastMessage.offsetHeight
        ) {
          msgContainer.scrollTop = msgContainer.scrollHeight;
        }
      }
    );

    return {
      onLoad,
      mentionOpened,
      activeChannel,
      messageLoading,
      rtMessages,
      rtUsers,
      rtActiveUser,
      rtShown,
      // showRTMessage,
      // RTMessage,
      // RTMessageUser,
      messages,
      infiniteScroll,
      isSameDay,
    };
  },
  components: {
    MessageVue,
  },
});
</script>

<style lang="sass">
.nomessages
  .q-message-label
    margin: 0
</style>
