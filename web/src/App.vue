<template>
  <Toast />
  <template v-if="userData == null">
    <Login v-model:visible="displayLogin" :userSocket="userSocket" />
    <Register v-model:visible="displayRegister" :userSocket="userSocket" />
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
      <template v-if="userData == null">
        <span class="p-buttonset">
          <Button icon="pi pi-user-plus" label="Register" @click="displayRegister = true" />
          &nbsp;
          <Button icon="pi pi-sign-in" label="Login" @click="displayLogin = true" />
        </span>
      </template>
      <template v-else>
        <Button icon="pi pi-sign-out" label="Logout" @click="logout()" />
      </template>
    </template>
  </Menubar>
  <router-view :data="userInformation" :user="userData" :socket="userSocket"/>
</template>

<script>
import Toast from 'primevue/toast'
import Menubar from 'primevue/menubar'
import Button from 'primevue/button'
import Login from '@/components/Login'
import Register from '@/components/Register'
import ConfirmDialog from 'primevue/confirmdialog';

export default {
  name: 'App',
  components: {
    Toast,
    Menubar,
    Button,
    Login,
    ConfirmDialog,
    Register,
  },
  data() {
    return {
      brandName: 'Patitos Airlines',
      items: [
        { icon: 'pi pi-home', label: 'Home', to: '/' },
        { icon: 'pi pi-user', label: 'User options', visible: () => this.userData != null && this.userData.authorization == 'user', items: [
            { label: 'User information', to: '/user/userinformation' },
            { label: 'Purchase history' },
          ]
        },
        { icon: 'pi pi-info-circle', label: 'About', items: [
            { label: 'History', to: '/about/history' },
            { label: 'Contact Information', to: '/about/contact' },
            { label: 'Institutional Reference', to: '/about/reference' },
          ]
        },
      ],
      displayLogin: false,
      displayRegister: false,

      userSocket: null,
      userData: null,
      userInformation: null,
    }
  },
  mounted() {
    this.userSocket = new WebSocket("ws://localhost:8099/server/user")
    this.userSocket.onmessage = (event) => {
      let data = JSON.parse(event.data)
      switch (data.action) {
        case 'LOGIN': this.handleLogIn(data); break;
        case 'REGISTER': this.handleRegister(data); break;
        case 'GET': this.handleGet(data); break;
        case 'UPDATE': this.handleUpdate(data); break;
        case 'LOGOUT': this.handleLogOut(); break;
        case 'ERROR': this.handleError(data); break;
      }
    }
    this.userSocket.onerror = () => {
      this.$toast.add({
        severity: "error",
        summary: "Server connection error",
        detail: "The connection to the server couldn't be made",
        life: 3000,
      })
    }
    this.userSocket.onclose = () => {
      this.userData = null
    }
  },
  methods: {
    logout() {
      this.$confirm.require({
        message: 'Are you sure you want to logout?',
        header: 'Are you sure?',
        icon: 'pi pi-sign-out',
        accept: () => {
          this.userSocket.send(JSON.stringify({
            action: 'LOGOUT',
          }))
        },
      })
    },
    handleLogIn(data) {
      this.userData = {
        username: data.username,
        authorization: data.authorization,
      }
      this.$toast.add({
        severity: "success",
        summary: "Success",
        detail: `Logged in as "${data.username}"`,
        life: 3000,
      })
      this.displayLogin =  false
      this.displayRegister =  false
    },
    handleRegister() {
      this.$toast.add({
        severity: "success",
        summary: "Success",
        detail: "Registration made successfully",
        life: 3000,
      })
    },
    handleGet(data) {
      this.userInformation = data.value
    },
    handleUpdate() {
      this.$toast.add({
        severity: "success",
        summary: "Success",
        detail: "Update was made successfully",
        life: 3000,
      })
    },
    handleLogOut() {
      this.userData = null
      this.$toast.add({
        severity: "success",
        summary: "Success",
        detail: "Logged out",
        life: 3000,
      })
    },
    handleError(data) {
      let summary = "Authentication error"
      let detail = data.message
      if (data.type && data.type == "DUPLICATE") {
        summary = "Registration error"
        detail = "Username already exists"
      }
      if (data.type && data.type == "CREDENTIALS") {
        summary = "Credentials error"
        detail = "Invalid credentials"
      }
      this.$toast.add({
        severity: "error",
        summary: summary,
        detail: detail,
        life: 3000,
      })
    }
  }
}
</script>

<style>
body {
  margin: 0;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
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
  margin-right: .25rem;
  user-select: none;
}
</style>
