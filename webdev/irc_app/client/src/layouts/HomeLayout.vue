<template>
  <q-layout>
    <q-page-container style="height: 100vh">
      <q-header
        ><q-toolbar>
          <q-btn
            :ripple="false"
            stretch
            flat
            label="Home"
            no-caps
            icon="home"
            :to="{ name: 'home' }"
            :class="
              $router.currentRoute.value.name === 'home' && 'text-positive'
            "
          ></q-btn>
          <q-btn
            :ripple="false"
            stretch
            flat
            label="Login"
            no-caps
            to="/login"
            icon="login"
            :class="
              $router.currentRoute.value.name === 'login' && 'text-positive'
            "
          />
          <q-btn
            :ripple="false"
            stretch
            flat
            label="Register"
            no-caps
            to="/register"
            icon="account_circle"
            :class="
              $router.currentRoute.value.name === 'register' && 'text-positive'
            "
          /> </q-toolbar
      ></q-header>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  setup() {
    return {};
  },
});
</script>

<style scoped>
.q-page-container {
  background: rgb(0, 0, 0);
  background: linear-gradient(
    60deg,
    rgb(76, 43, 85) 20%,
    rgb(120, 106, 207) 100%
  );
  color: #fefefe;
}
.q-header {
  width: fit-content;
  margin: auto;
  border-radius: 0.5rem;
  margin-top: 1rem;
}

.menu-active {
  color: black !important;
}
</style>
