<template>
  <q-page class="full-height text-center row justify-center q-pt-lg scroll">
    <div class="q-pa-lgrow col" style="max-width: 500px">
      <q-list separator>
        <q-item>
          <q-item-section>
            <q-item-label class="text-h6 text-primary text-bold"
              >Congratulations! You are now logged in.</q-item-label
            >
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label style="font-weight: bold"
              >Here are som instructions to begin with!
            </q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label>Channel with invitation is highlighted</q-item-label>
          </q-item-section>
        </q-item>

        <q-space></q-space>

        <q-item class="q-pt-lg">
          <q-item-section>
            <q-item-label overline style="font-weight: bold">
              [optional] stands for optional setting
            </q-item-label>
            <q-item-label style="font-weight: bold">Command list</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/join channelName [private]</q-item-label>
            <q-item-label>Create channel</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/join channelName</q-item-label>
            <q-item-label>Join existing public channel</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/invite nickName</q-item-label>
            <q-item-label>Channel invitation</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/kick nickName</q-item-label>
            <q-item-label>Kick someone from channel</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/quit</q-item-label>
            <q-item-label>Close channel by admin</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/cancel</q-item-label>
            <q-item-label>Revoke your membership in channel</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/list</q-item-label>
            <q-item-label>List of members in channel</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>/revoke nickName</q-item-label>
            <q-item-label
              >Remove user from private channel by admin</q-item-label
            >
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section>
            <q-item-label overline>@nickname</q-item-label>
            <q-item-label>Mention someone in channel</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
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
