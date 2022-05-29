<template>
  <q-dialog v-model="dialogVisible">
    <q-card style="min-width: 350px">
      <q-card-section>
        <div class="text-h6">User list</div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <q-list
          dense
          padding
          separator
          class="rounded-borders scroll q-pa-md"
          style="max-height: 350px"
        >
          <q-item v-for="user in userList" :key="user.username">
            <q-item-section>
              <div class="flex row items-center justify-start q-py-xs">
                <q-avatar rounded size="md" class="q-mr-md">
                  <profile-image :image="user.image" />
                  <q-badge
                    :color="getUserStatusColor(user)"
                    floating
                    rounded
                  ></q-badge>
                </q-avatar>
                <div class="text-left">
                  <div
                    class="text-bold"
                    style="font-size: 1rem; line-height: 1.3rem"
                  >
                    {{ getFullName(user) }}
                  </div>
                  <div class="text-caption" style="line-height: 0.8rem">
                    @{{ user.username }}
                  </div>
                </div>
              </div>
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn label="OK" color="primary" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import { User } from 'src/contracts';
import { getFullName, getStatusColor } from 'src/helpers';
import { useStore } from 'src/store';
import { computed, defineComponent } from 'vue';
import ProfileImage from './ProfileImage.vue';

export default defineComponent({
  setup() {
    const store = useStore();
    const dialogVisible = computed({
      get: () => store.state.commands.userlistDialog,
      set: () => {
        store.commit('commands/SET_USERLIST', false);
      },
    });
    const userList = computed(() => store.state.channels.activeChannelUsers);
    const getUserStatusColor = (user: User) =>
      getStatusColor(store.state.channels.userStatus[user.id]);
    return { dialogVisible, userList, getUserStatusColor, getFullName };
  },
  components: { ProfileImage },
});
</script>
