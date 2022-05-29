<template>
  <q-list class="q-pb-sm">
    <template v-for="channel in channels" :key="channel.id">
      <q-item
        clickable
        :to="`/c/${channel.channelname}`"
        @click="setActiveChannel(channel.channelname)"
        :class="[
          // channel.invitations.includes(activeUser) && 'invited-channel',
          'channel bg-grey-3 q-px-sm q-py-none',
        ]"
        :active-class="'active-channel'"
      >
        <q-item-section side class="relative-position">
          <q-icon
            size="sm"
            color="grey-5"
            :name="getChannelIcon(channel.type)"
          />
          <q-badge
            v-if="channel.kickCount > 0"
            floating
            color="orange"
            text-color="white"
            :label="channel.kickCount"
            class="badge-kickcounter"
          />
        </q-item-section>
        <q-item-section>
          <q-item-label class="channel-name">{{
            channel.channelname
          }}</q-item-label>
          <div v-if="channel.lastMessage !== null" class="message-preview">
            {{
              channel.lastMessage.user.id === activeUser.id
                ? 'You'
                : channel.lastMessage.user.firstname
            }}: {{ channel.lastMessage.text }}
          </div>
        </q-item-section>
        <q-item-section side class="channel-dropdown">
          <q-btn-dropdown
            @click.stop.prevent
            flat
            dense
            right
            size="sm"
            color="grey-6"
            dropdown-icon="more_vert"
            no-icon-animation
            menu-anchor="bottom middle"
            menu-self="top middle"
          >
            <q-list>
              <q-item clickable @click="leaveChannel(channel)">
                <q-item-section>
                  <q-item-label>Leave Channel</q-item-label>
                </q-item-section>
              </q-item>
              <q-item
                clickable
                @click="deleteChannel(channel)"
                v-if="channel.ownerId === activeUser.id"
              >
                <q-item-section>
                  <q-item-label>Delete Channel</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-btn-dropdown>

          <!-- <q-badge
            floating
            v-if="channel.invitations.includes(activeUser)"
            style="left: 3px; top: 3px; right: unset"
          >
            <q-icon name="mail"></q-icon>
          </q-badge> -->
        </q-item-section>
      </q-item>
    </template>
  </q-list>
</template>

<script lang="ts">
import { Channel, ChannelType, User } from 'src/contracts';
import { useStore } from 'src/store';
import { defineComponent, PropType, toRef } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  props: {
    channelList: {
      type: Object as PropType<Channel[]>,
      required: true,
    },
  },
  setup(props) {
    const channels = toRef(props, 'channelList');
    const store = useStore();
    const router = useRouter();
    const activeUser = store.state.auth.user as User;

    const setActiveChannel = async (name: string) => {
      await store.dispatch('channels/setActiveChannel', name);
    };

    const getChannelIcon = (channeltype: ChannelType): string => {
      return channeltype === 'private' ? 'lock' : 'people';
    };

    const leaveChannel = async (channel: Channel) => {
      await store.dispatch('commands/leaveChannel', { router, channel });
    };
    const deleteChannel = async (channel: Channel) => {
      await store.dispatch('commands/deleteChannel', { router, channel });
    };

    return {
      channels,
      activeUser,
      setActiveChannel,
      getChannelIcon,
      leaveChannel,
      deleteChannel,
    };
  },
});
</script>

<style lang="sass">
.invited-channel:not(.active-channel)
  color: var(--q-info)

  .q-badge .q-icon
    color: var(--q-info)

.active-channel
  background-color: var(--q-primary)!important

  .q-icon
    color: white !important

  color: white !important

.invited-channel
  .q-badge .q-icon
    color: white !important

  &.active-channel .q-badge, &:not(.active-channel)
    background-color: var(--q-negative)

.channel-name
  font-size: 1rem
  font-weight: 600

.message-preview
  width: 200px
  font-size: 0.9em
  overflow: hidden
  white-space: nowrap
  text-overflow: ellipsis

.channel
  min-height: 45px
  height: 45px
  margin-bottom: 2px
  .channel-dropdown
    padding-left: 0px!important
  .badge-kickcounter
    left: -5px
    top: 3px
    right: unset
    padding: 1px 4px
</style>
