<template>
  <div class="q-mx-md q-mb-md">
    <q-btn-dropdown flat no-caps class="q-py-sm full-width bg-grey-4">
      <div class="q-pa-md q-gutter-sm">
        <div class="row no-wrap">
          <q-avatar rounded size="xl" class="q-mr-sm">
            <profile-image :image="activeUser?.image" />
          </q-avatar>
          <div class="text-left">
            <div class="text-bold" style="font-size: 1rem; line-height: 1.1rem">
              {{ fullName }}
            </div>
            <div class="text-caption">@{{ activeUser.username }}</div>
            <div class="text-caption">
              <q-badge :color="statusColor" rounded class="q-mr-sm">{{
                statusText
              }}</q-badge>
            </div>
          </div>
        </div>
        <div>
          <q-expansion-item
            dense
            dense-toggle
            icon="mood"
            label="Change status"
          >
            <q-list dense>
              <q-item
                clickable
                v-ripple
                :active="status === 'online'"
                @click="status = 'online'"
                active-class="text-bold text-green"
              >
                <q-item-section>
                  <q-item-label>
                    <q-badge color="green" rounded class="q-mr-sm"></q-badge
                    ><span>Active</span>
                  </q-item-label>
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="status === 'dnd'"
                @click="status = 'dnd'"
                active-class="text-bold text-orange"
              >
                <q-item-section>
                  <q-item-label>
                    <q-badge color="orange" rounded class="q-mr-sm"></q-badge
                    ><span>Do Not Disturb</span>
                  </q-item-label>
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="status === 'offline'"
                @click="status = 'offline'"
                active-class="text-bold text-red"
              >
                <q-item-section>
                  <q-item-label>
                    <q-badge color="red" rounded class="q-mr-sm"></q-badge
                    ><span>Offline</span>
                  </q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-expansion-item>

          <q-expansion-item
            dense
            dense-toggle
            icon="notifications"
            label="Notifications"
          >
            <q-list dense>
              <q-item
                clickable
                v-ripple
                :active="notifications === 'all'"
                @click="notifications = 'all'"
                active-class="text-bold text-black"
              >
                <q-item-section>
                  <q-item-label> All new messages</q-item-label>
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="notifications === 'mentions'"
                @click="notifications = 'mentions'"
                active-class="text-bold text-black"
              >
                <q-item-section>
                  <q-item-label> Only mentions</q-item-label>
                </q-item-section>
              </q-item>

              <q-item
                clickable
                v-ripple
                :active="notifications === 'none'"
                @click="notifications = 'none'"
                active-class="text-bold text-black"
              >
                <q-item-section>
                  <q-item-label>None</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-expansion-item>

          <q-separator spaced />
          <q-item dense flat clickable v-ripple @click="logout"
            ><q-item-section>
              <q-item-label class="text-bold text-red"
                >Log Out</q-item-label
              ></q-item-section
            >
          </q-item>
        </div>
      </div>

      <template v-slot:label>
        <div class="flex row items-center justify-center">
          <q-avatar rounded size="md" class="q-mr-md">
            <profile-image :image="activeUser?.image" />
            <q-badge :color="statusColor" floating rounded></q-badge>
          </q-avatar>
          <div class="text-left">
            <div class="text-bold" style="font-size: 1rem; line-height: 1.3rem">
              {{ fullName }}
            </div>
            <div class="text-caption">@{{ activeUser.username }}</div>
          </div>
        </div>
      </template>
    </q-btn-dropdown>
  </div>
</template>

<script lang="ts">
import { getFullName, getStatusColor, getStatusText } from 'src/helpers';
import { computed, defineComponent } from 'vue';
import { RouteLocationRaw, useRouter } from 'vue-router';
import { User } from '../contracts';
import { useStore } from '../store';
import ProfileImage from './ProfileImage.vue';

export default defineComponent({
  setup() {
    const $store = useStore();
    const $router = useRouter();

    const activeUser = computed(() => $store.state.auth.user as User);
    const status = computed({
      get: () => $store.state.channels.userStatus[activeUser.value.id],
      set: (status) =>
        $store.dispatch('channels/setUserStatus', {
          userid: activeUser.value.id,
          status: status,
        }),
    });
    const notifications = computed({
      get: () => $store.state.channels.notifications,
      set: (value) => $store.commit('channels/SET_NOTIFICATION', value),
    });

    const logout = () => {
      $store
        .dispatch('auth/logout')
        .then(() => $router.push({ name: 'home' } as RouteLocationRaw))
        .catch((error) => {
          console.log(error);
        });
    };

    const statusColor = computed(() => getStatusColor(status.value));

    const statusText = computed(() => getStatusText(status.value));

    const fullName = computed(() => getFullName(activeUser.value));

    return {
      activeUser,
      notifications,
      status,
      logout,
      statusColor,
      statusText,
      fullName,
    };
  },
  components: {
    ProfileImage,
  },
});
</script>

<style scoped></style>
