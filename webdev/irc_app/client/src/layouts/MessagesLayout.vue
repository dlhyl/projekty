<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar class="bg-grey-3 text-black">
        <q-btn
          round
          flat
          icon="keyboard_arrow_left"
          class="drawer_icon q-mr-sm"
          @click="toggleLeftDrawer"
        />
        <span class="text-h6 q-pl-md text-primary text-bold">{{
          activeChannel?.channelname || 'Welcome'
        }}</span>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered :breakpoint="717">
      <div class="q-py-md q-px-sm column full-height">
        <profile-button />
        <q-scroll-area class="col" style="height: 200px">
          <invitations-list />
          <channel-list-expansion type="private" :channels="privateChannels" />
          <channel-list-expansion type="public" :channels="publicChannels" />
        </q-scroll-area>
      </div>
    </q-drawer>

    <q-page-container style="height: 100vh">
      <router-view />
    </q-page-container>

    <q-footer>
      <command-input />
    </q-footer>

    <channel-dialog />
    <user-list-dialog />
    <confirm-dialog />
  </q-layout>
</template>

<script lang="ts">
import ChannelListExpansion from 'src/components/ChannelListExpansion.vue';
import CommandInput from 'src/components/CommandInput.vue';
import ConfirmDialog from 'src/components/ConfirmDialog.vue';
import InvitationsList from 'src/components/InvitationsList.vue';
import ChannelDialog from 'src/components/NewChannelDialog.vue';
import ProfileButton from 'src/components/ProfileButton.vue';
import UserListDialog from 'src/components/UserListDialog.vue';
import { Channel } from 'src/contracts';
import { useStore } from 'src/store';
import { computed, defineComponent, ref } from 'vue';

export default defineComponent({
  name: 'MessageLayout',

  setup() {
    const store = useStore();

    const activeChannel = computed(() => {
      return store.state.channels.active as Channel;
    });

    const privateChannels = computed(() => {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-member-access
      return store.getters['channels/joinedPrivateChannels'];
    });
    const publicChannels = computed(() => {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-member-access
      return store.getters['channels/joinedPublicChannels'];
    });

    const leftDrawerOpen = ref(false);
    const toggleLeftDrawer = () => {
      leftDrawerOpen.value = !leftDrawerOpen.value;
    };

    return {
      leftDrawerOpen,
      toggleLeftDrawer,
      activeChannel,
      privateChannels,
      publicChannels,
    };
  },
  components: {
    ProfileButton,
    ChannelListExpansion,
    CommandInput,
    ChannelDialog,
    UserListDialog,
    ConfirmDialog,
    InvitationsList,
  },
});
</script>

<style lang="sass">
.q-btn-dropdown .q-btn__content
  justify-content: space-between

@media (min-width: 701px)
  .drawer_icon
    display: none
</style>
