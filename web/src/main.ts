// VueJS
import { createApp } from 'vue'
import App from './App.vue'
// Vue Router
import router from './router'
// PrimeVue
import PrimeVue from 'primevue/config'
require('primevue/resources/themes/saga-blue/theme.css')
require('primevue/resources/primevue.min.css')
require('primeicons/primeicons.css')

createApp(App)
  .use(router)
  .use(PrimeVue, {ripple: true})
  .mount('#app')
