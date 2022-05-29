<template>
  <q-item
    v-if="invitations.length > 0"
    class="channel-dropdown bg-negative text-white"
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
        >Invitations</q-item-label
      >
    </q-item-section>
  </q-item>
  <q-list :class="[!showDropdown && 'hidden-list', 'q-pb-sm', 'outline']">
    <template v-for="channel in invitations" :key="channel.id">
      <q-item hoverable class="invited-channel bg-grey-3 q-px-md q-py-none">
        <q-item-section>
          <q-item-label class="channel-name">{{
            channel.channelname
          }}</q-item-label>
        </q-item-section>
        <q-item-section side class="inv-controls">
          <q-btn
            size="sm"
            class="q-mr-sm"
            dense
            color="primary"
            icon="done"
            @click="acceptInvitation(channel.channelname)"
          />
          <q-btn
            size="sm"
            dense
            color="warning"
            icon="close"
            @click="rejectInvitation(channel.channelname)"
          />
        </q-item-section>
      </q-item>
    </template>
  </q-list>
</template>

<script lang="ts">
import { useQuasar } from 'quasar';
import { ErrorResponseValidation } from 'src/contracts';
import { useStore } from 'src/store';
import { computed, defineComponent, ref } from 'vue';

export default defineComponent({
  setup() {
    const $store = useStore();
    const $q = useQuasar();
    const invitations = computed(() => $store.state.channels.invitations);
    const showDropdown = ref(true);

    const acceptInvitation = (channelname: string) => {
      $store
        .dispatch('channels/handleInvitation', { channelname, confirm: true })
        .catch((err: ErrorResponseValidation) =>
          $q.notify({
            type: 'warning',
            message: Object.values(err.response.data.errors)
              .map((errors) => errors.join(''))
              .join(''),
          })
        );
    };
    const rejectInvitation = (channelname: string) => {
      $store
        .dispatch('channels/handleInvitation', { channelname, confirm: false })
        .catch((err: ErrorResponseValidation) => {
          $q.notify({
            type: 'warning',
            message: Object.values(err.response.data.errors)
              .map((errors) => errors.join(''))
              .join(''),
          });
        });
    };

    return { invitations, showDropdown, acceptInvitation, rejectInvitation };
  },
});
</script>

<style lang="sass">
.invited-channel
  min-height: 45px
  height: 45px
  margin-bottom: 2px
  .inv-controls
    align-items: center!important
    flex-direction: row!important

.channel-name
  font-size: 1rem
  font-weight: 600
</style>
