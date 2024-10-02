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
import {
    faUser as fasUser, faAt as fasAt, faCopyright as fasCopyright, faBars as fasBars,
    faUpload as fasUpload, faDownload as fasDownload, faPlus as fasPlus, faPenToSquare as fasPenToSquare,
    faXmark as fasXmark, faRotate as fasRotate, faKey as fasKey, faLink as fasLink,
    faGraduationCap as fasGraduationCap, faEyeSlash as fasEyeSlash, faEye as fasEye,
    faCompass as fasCompass, faBell as fasBell, faQrcode as fasQrcode, faFire as fasFire,
    faStar as fasStar, faEnvelope as fasEnvelope, faMask as fasMask, faCode as fasCode,
    faUsers as fasUsers, faHammer as fasHammer, faRocket as fasRocket, faPencil as fasPencil,
    faAngleUp as fasAngleUp, faTag as fasTag, faCirclePlus as fasCirclePlus, faShield as fasShield,
    faRss as fasRss, faFolder as fasFolder, faThumbsDown as fasThumbsDown, faThumbsUp as fasThumbsUp,
    faSun as fasSun, faMoon as fasMoon, faArrowDown91 as fasArrowDown91, faArrowDown19 as fasArrowDown19,
    faArrowRight as fasArrowRight, faAngleRight as fasAngleRight, faAngleLeft as fasAngleLeft,
    faLock as fasLock, faLockOpen as fasLockOpen, faBookOpen as fasBookOpen, faReply as fasReply,
    faUmbrellaBeach as fasUmbrellaBeach, faGear as fasGear, faDice as fasDice,
    faHouse as fasHouse, faHeart as fasHeart, faImage as fasImage, faServer as fasServer,
    faClipboard as fasClipboard, faClipboardCheck as fasClipboardCheck, faCircleMinus as fasCircleMinus,
    faPaperPlane as fasPaperPlane, faLocationDot as fasLocationDot, faBriefcase as fasBriefcase,
    faScrewdriverWrench as fasScrewdriverWrench, faDiagramProject as fasDiagramProject, faTrophy as fasTrophy,
    faAnglesLeft as fasAnglesLeft, faAnglesRight as fasAnglesRight, faCircleQuestion as fasCircleQuestion,
    faTrash as fasTrash
} from "@fortawesome/free-solid-svg-icons";
import {faLightbulb as farLightbulb, faCompass as farCompass} from "@fortawesome/free-regular-svg-icons";
import {faGithub as fabGithub, faSearchengin as fabSearchengin, faBlackTie as fabBlackTie} from "@fortawesome/free-brands-svg-icons";
library.add(
    fasAngleLeft, fasAngleRight, fasAnglesLeft, fasAnglesRight, fasAngleUp, fasArrowDown19, fasArrowDown91, fasArrowRight, fasAt,
    fasBars, fasBell, fasBookOpen, fasBriefcase,
    fasCircleMinus, fasCirclePlus, fasCircleQuestion, fasClipboard, fasClipboardCheck, fasCode, fasCompass, fasCopyright,
    fasDiagramProject, fasDice, fasDownload,
    fasEnvelope, fasEye, fasEyeSlash,
    fasFire, fasFolder,
    fasGear, fasGraduationCap,
    fasHammer, fasHeart, fasHouse,
    fasImage,
    fasKey,
    fasLink, fasLocationDot, fasLock, fasLockOpen,
    fasMask, fasMoon,
    fasPaperPlane, fasPencil, fasPenToSquare, fasPlus,
    fasQrcode,
    fasReply, fasRocket, fasRotate, fasRss,
    fasScrewdriverWrench, fasServer, fasShield, fasStar, fasSun,
    fasTag, fasThumbsDown, fasThumbsUp, fasTrash, fasTrophy,
    fasUmbrellaBeach, fasUpload, fasUser, fasUsers,
    fasXmark,

    farLightbulb, farCompass,
    fabGithub, fabSearchengin, fabBlackTie
)

import App from './App.vue'
import router from './router'


const app = createApp(App);
const head = createHead();
app.use(head);
app.use(createPinia());
app.use(router);
app.use(printPlugin);
app.component('font-awesome-icon', FontAwesomeIcon)
app.mount('#app')
