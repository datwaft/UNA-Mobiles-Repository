// VueJS
import { createApp } from 'vue'
import App from './App.vue'
// Vue Router
import router from './router'
// PrimeVue
import PrimeVue from 'primevue/config'

createApp(App)
  .use(router)
  .use(PrimeVue)
  .mount('#app')
