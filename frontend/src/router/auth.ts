import type {RouteRecordRaw} from "vue-router";
import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import GithubOAuthView from "@/views/auth/GithubOAuthView.vue";
import InitiateRecoveryView from "@/views/auth/InitiateRecoveryView.vue";
import CompleteRecoveryView from "@/views/auth/CompleteRecoveryView.vue";
import ProfileView from "@/views/auth/ProfileView.vue";


export default [
    {
        path: 'login',
        component: LoginView,
        name: 'auth-login',
    },
    {
        path: 'register',
        component: RegisterView,
        name: 'auth-register'
    },
    {
        path: 'oauth2/github',
        component: GithubOAuthView,
        name: 'auth-github-oauth2'
    },
    {
        path: 'profile',
        component: ProfileView,
        name: 'auth-profile'
    },
    {
        path: 'recover/initiate',
        component: InitiateRecoveryView,
        name: 'auth-recover-initiate'
    },
    {
        path: 'recover/complete',
        component: CompleteRecoveryView,
        name: 'auth-recover-complete'
    }
] as RouteRecordRaw[];