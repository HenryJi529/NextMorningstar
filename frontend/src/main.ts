import "./assets/css/tailwindcss.css"
import "./assets/css/ant.scss"
import "./assets/css/fonts.scss"
import "./assets/css/hljsText.scss"
import "./assets/css/others.scss"

import 'nprogress/nprogress.css'

import {createApp} from 'vue'
import {createPinia} from 'pinia'
import {createHead} from '@vueuse/head';
import {printPlugin} from 'vue-print-next';
import {library} from '@fortawesome/fontawesome-svg-core'
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'
import {
    faAngleLeft as fasAngleLeft,
    faAngleRight as fasAngleRight,
    faAnglesLeft as fasAnglesLeft,
    faAnglesRight as fasAnglesRight,
    faAngleUp as fasAngleUp,
    faArrowDown19 as fasArrowDown19,
    faArrowDown91 as fasArrowDown91,
    faArrowRight as fasArrowRight,
    faAt as fasAt,
    faBars as fasBars,
    faBell as fasBell,
    faBookOpen as fasBookOpen,
    faBriefcase as fasBriefcase,
    faCircleDown as fasCircleDown,
    faCircleMinus as fasCircleMinus,
    faCirclePlus as fasCirclePlus,
    faCircleQuestion as fasCircleQuestion,
    faCircleXmark as fasCircleXmark,
    faClipboard as fasClipboard,
    faClipboardCheck as fasClipboardCheck,
    faCode as fasCode,
    faCompass as fasCompass,
    faCopyright as fasCopyright,
    faDiagramProject as fasDiagramProject,
    faDice as fasDice,
    faDownload as fasDownload,
    faEnvelope as fasEnvelope,
    faEye as fasEye,
    faEyeSlash as fasEyeSlash,
    faFire as fasFire,
    faFolder as fasFolder,
    faGear as fasGear,
    faGraduationCap as fasGraduationCap,
    faHammer as fasHammer,
    faHeart as fasHeart,
    faHouse as fasHouse,
    faImage as fasImage,
    faKey as fasKey,
    faLink as fasLink,
    faLocationDot as fasLocationDot,
    faLock as fasLock,
    faLockOpen as fasLockOpen,
    faMask as fasMask,
    faMoon as fasMoon,
    faPaperPlane as fasPaperPlane,
    faPencil as fasPencil,
    faPenToSquare as fasPenToSquare,
    faPlus as fasPlus,
    faPrint as fasPrint,
    faQrcode as fasQrcode,
    faReply as fasReply,
    faRocket as fasRocket,
    faRotate as fasRotate,
    faRss as fasRss,
    faScrewdriverWrench as fasScrewdriverWrench,
    faServer as fasServer,
    faShareNodes as fasShareNodes,
    faShield as fasShield,
    faStar as fasStar,
    faSun as fasSun,
    faTag as fasTag,
    faThumbsDown as fasThumbsDown,
    faThumbsUp as fasThumbsUp,
    faTrash as fasTrash,
    faTrophy as fasTrophy,
    faUmbrellaBeach as fasUmbrellaBeach,
    faUpload as fasUpload,
    faUser as fasUser,
    faUsers as fasUsers,
    faXmark as fasXmark
} from "@fortawesome/free-solid-svg-icons";
import {faCompass as farCompass, faLightbulb as farLightbulb} from "@fortawesome/free-regular-svg-icons";
import {
    faBlackTie as fabBlackTie,
    faGithub as fabGithub,
    faMarkdown as fabMarkdown,
    faSearchengin as fabSearchengin
} from "@fortawesome/free-brands-svg-icons";

import App from './App.vue'
import router from './router'


library.add(
    fasAngleLeft, fasAngleRight, fasAnglesLeft, fasAnglesRight, fasAngleUp, fasArrowDown19, fasArrowDown91, fasArrowRight, fasAt,
    fasBars, fasBell, fasBookOpen, fasBriefcase,
    fasCircleDown, fasCircleMinus, fasCirclePlus, fasCircleQuestion, fasCircleXmark, fasClipboard, fasClipboardCheck, fasCode, fasCompass, fasCopyright,
    fasDiagramProject, fasDice, fasDownload,
    fasEnvelope, fasEye, fasEyeSlash,
    fasFire, fasFolder,
    fasGear, fasGraduationCap,
    fasHammer, fasHeart, fasHouse,
    fasImage,
    fasKey,
    fasLink, fasLocationDot, fasLock, fasLockOpen,
    fasMask, fasMoon,
    fasPaperPlane, fasPencil, fasPenToSquare, fasPlus, fasPrint,
    fasQrcode,
    fasReply, fasRocket, fasRotate, fasRss,
    fasScrewdriverWrench, fasServer, fasShield, fasStar, fasSun, fasShareNodes,
    fasTag, fasThumbsDown, fasThumbsUp, fasTrash, fasTrophy,
    fasUmbrellaBeach, fasUpload, fasUser, fasUsers,
    fasXmark,

    farLightbulb, farCompass,
    fabGithub, fabSearchengin, fabBlackTie, fabMarkdown
)


const app = createApp(App);
const head = createHead();
app.use(head);
app.use(createPinia());
app.use(router);
app.use(printPlugin);
app.component('font-awesome-icon', FontAwesomeIcon)
app.mount('#app')
