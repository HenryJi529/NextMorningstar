import "./assets/css/tailwindcss.css"
import "./assets/css/ant.scss"
import "./assets/css/fonts.scss"
import "./assets/css/hljsText.scss"
import "./assets/css/others.scss"

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createHead } from '@vueuse/head';
import { printPlugin } from 'vue-print-next';
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'
import { fab } from '@fortawesome/free-brands-svg-icons'
import Antd from 'ant-design-vue';

import App from './App.vue'
import router from './router'


library.add(fas, far, fab)

const app = createApp(App);
const head = createHead();
app.use(head);
app.use(createPinia());
app.use(router);
app.use(printPlugin);
app.use(Antd);
app.component('font-awesome-icon', FontAwesomeIcon)
app.mount('#app')
