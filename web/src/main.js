// VueJS
import { createApp } from 'vue'
import App from './App.vue'
// Vue Router
import router from './router'
// PrimeVue
import PrimeVue from 'primevue/config'
import 'primevue/resources/themes/saga-blue/theme.css'
import 'primevue/resources/primevue.min.css'
import 'primeicons/primeicons.css'
// PrimeFlex
import 'primeflex/primeflex.css';
// FontAwesome
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faPlaneDeparture } from "@fortawesome/free-solid-svg-icons";
library.add(faPlaneDeparture)

createApp(App)
  .use(router)
  .use(PrimeVue, {ripple: true})
  .component("font-awesome-icon", FontAwesomeIcon)
  .mount('#app')