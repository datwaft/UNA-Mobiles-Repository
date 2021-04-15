<template>
  <Toast />
  <template v-if="!session">
    <Login v-model:visible="displayLogin" />
    <Register v-model:visible="displayRegister" />
  </template>
  <template v-else>
    <ConfirmDialog />
  </template>
  <Menubar :model="items">
    <template #start>
      <div class="logo">
        <font-awesome-icon icon="plane-departure" />
        {{ brandName }}
      </div>
    </template>
    <template #end>
      <template v-if="!session">
        <span class="p-buttonset">
          <Button
            icon="pi pi-user-plus"
            label="Register"
            @click="displayRegister = true"
          />
          &nbsp;
          <Button
            icon="pi pi-sign-in"
            label="Login"
            @click="displayLogin = true"
          />
        </span>
      </template>
      <template v-else>
        <div class="username p-text-uppercase">
          {{ session?.username }}
        </div>
        <Button icon="pi pi-sign-out" label="Logout" @click="logout()" />
      </template>
    </template>
  </Menubar>
  <router-view />
</template>

<script>
import { mapState } from "vuex";

import Toast from "primevue/toast";
import Menubar from "primevue/menubar";
import Button from "primevue/button";
import ConfirmDialog from "primevue/confirmdialog";

import Login from "@/components/Login";
import Register from "@/components/Register";

export default {
  name: "App",
  components: {
    Toast,
    Menubar,
    Button,
    ConfirmDialog,
    Login,
    Register,
  },
  data() {
    return {
      brandName: "Patitos Airlines",
      items: [
        { icon: "pi pi-home", label: "Home", to: "/" },
        {
          icon: "pi pi-user",
          label: "User options",
          visible: () => this.session?.authorization == "user",
          items: [
            { label: "User information", to: "/user/user-information" },
            { label: "Purchase history", to: "/user/purchase-history" },
          ],
        },
        {
          icon: "pi pi-key",
          label: "Administrator options",
          visible: () => this.session?.authorization == "admin",
          items: [
            { label: "Plane types", to: "/admin/plane-type" },
            { label: "Planes", to: "/admin/plane" },
            { label: "Routes", to: "/admin/route" },
            { label: "Schedules", to: "/admin/schedule" },
            { label: "Flights", to: "/admin/flight" },
            { label: "Reports", to: "/admin/report" },
          ],
        },
        {
          icon: "pi pi-info-circle",
          label: "About",
          items: [
            { label: "History", to: "/about/history" },
            { label: "Contact Information", to: "/about/contact" },
            { label: "Institutional Reference", to: "/about/reference" },
          ],
        },
      ],
      displayLogin: false,
      displayRegister: false,
    };
  },
  computed: {
    ...mapState({ toast: (state) => state.message }),
    ...mapState("session", { session: (state) => state.session }),
  },
  watch: {
    toast(newValue) {
      if (!newValue) return;
      this.$toast.add({
        severity: newValue.severity,
        summary: newValue.summary,
        detail: newValue.detail,
        life: newValue?.life,
      });
      if (newValue.severity == "success") {
        this.displayRegister = false;
      }
    },
    session(newValue) {
      if (newValue) {
        this.displayLogin = false;
        this.displayRegister = false;
      }
    },
  },
  methods: {
    logout() {
      this.$confirm.require({
        message: "Are you sure you want to logout?",
        header: "Are you sure?",
        icon: "pi pi-sign-out",
        accept: () => {
          this.$store.dispatch("session/sendMessage", {
            action: "LOGOUT",
          });
        },
      });
    },
  },
};
</script>

<style>
body {
  margin: 0;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica,
    Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.header {
  font-size: 2rem;
  font-weight: bold;
  color: var(--primary-color);
}

.window {
  margin: 0.5rem;
}

a {
  color: var(--text-color);
  text-decoration: none;
}
</style>

<style scoped>
.logo {
  font-size: 1.25rem;
  font-weight: bold;
  color: var(--primary-color);
  margin-right: 0.25rem;
  user-select: none;
}

.username {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  font-size: 1.25rem;
  font-weight: bold;
  color: var(--secondary-color);
  user-select: none;
  margin-right: 1rem;
}
</style>
