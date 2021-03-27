<template>
  <Menubar :model="items">
    <template #start>
      <div class="logo">
        <font-awesome-icon icon="plane-departure" />
        {{ brandName }}
      </div>
    </template>
  </Menubar>
  <router-view/>
</template>

<script>
import Menubar from 'primevue/menubar'

export default {
  name: 'App',
  components: {
    Menubar,
  },
  data() {
    return {
      brandName: 'Patitos Airlines',
      items: [
        { icon: 'pi pi-home', label: 'Home', to: '/' },
        { icon: 'pi pi-info-circle', label: 'About', items: [
          { label: 'History', to: '/about/history' },
          { label: 'Contact Information', to: '/about/contact' },
          { label: 'Institutional Reference', to: '/about/reference' },
        ]},
      ],
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
