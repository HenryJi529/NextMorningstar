import "./assets/css/tailwindcss.css"
import "./assets/css/fonts.scss"

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createHead } from '@vueuse/head';
import { printPlugin } from 'vue-print-next';

/* import the fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'
/* import font awesome icon component */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
/* import all icons */
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'
import { fab } from '@fortawesome/free-brands-svg-icons'

import App from './App.vue'
import router from './router'

/* add icons to the library */
library.add(fas, far, fab)
const app = createApp(App)
const head = createHead();

app.use(head);
app.use(createPinia())
app.use(router)
app.use(printPlugin);
app.component('font-awesome-icon', FontAwesomeIcon)
app.mount('#app')
