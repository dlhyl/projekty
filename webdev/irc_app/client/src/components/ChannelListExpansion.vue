<template>
  <q-item
    class="channel-dropdown bg-negative text-white"
    :label="channelType + ' channels'"
    clickable
    @click.stop.prevent="showDropdown = !showDropdown"
  >
    <q-item-section avatar>
      <q-icon
        name="expand_more"
        :class="!showDropdown && 'rotate-180'"
        style="transition: transform 0.25s"
      ></q-icon>
    </q-item-section>
    <q-item-section>
      <q-item-label class="text-weight-medium text-uppercase"
        >{{ channelType }} channels</q-item-label
      >
    </q-item-section>
    <q-item-section side>
      <q-btn
        size="0.8rem"
        color="grey-3"
        text-color="grey-7"
        unelevated
        padding="xs"
        icon="add"
        @click.stop.prevent="openChannelDialog(channelType)"
    /></q-item-section>
  </q-item>

  <channel-list
    :channelList="channelList"
    :class="!showDropdown && 'hidden-list'"
  />
</template>

<script lang="ts">
import { Channel, ChannelType } from 'src/contracts/Channel';
import { useStore } from 'src/store';
import { defineComponent, PropType, ref, toRef } from 'vue';
import ChannelList from './ChannelList.vue';

export default defineComponent({
  props: {
    type: {
      type: String,
      required: true,
    },
    channels: {
      type: Object as PropType<Channel[]>,
      required: true,
    },
  },
  components: {
    ChannelList,
  },
  setup(props) {
    const $store = useStore();
    const openChannelDialog = (channelType: ChannelType) => {
      $store.commit('commands/SET_NEWCHANNELDIALOGPROPS', {
        type: channelType,
        channelname: '',
      });
      $store.commit('commands/SET_NEWCHANNELDIALOG', true);
    };

    const channelType = toRef(props, 'type');
    const channelList = toRef(props, 'channels');
    const showDropdown = ref(true);

    return {
      openChannelDialog,
      channelType,
      channelList,
      showDropdown,
    };
  },
});
</script>

<style lang="sass">
.hidden-list
  .channel:not(.active-channel)
    display: none!important
</style>
