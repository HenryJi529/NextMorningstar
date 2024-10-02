import type {RouteRecordRaw} from "vue-router";


export default [
    {
        path: 'login',
        component: () => import("@/views/auth/LoginView.vue"),
        name: 'auth-login',
    },
    {
        path: 'register',
        component: () => import("@/views/auth/RegisterView.vue"),
        name: 'auth-register'
    },
    {
        path: 'oauth2/github',
        component: () => import("@/views/auth/GithubOAuthView.vue"),
        name: 'auth-github-oauth2'
    },
    {
        path: 'profile',
        component: () => import("@/views/auth/ProfileView.vue"),
        name: 'auth-profile',
        meta: {
            requiresAuth: true,
        }
    },
    {
        path: 'recover/initiate',
        component: () => import("@/views/auth/InitiateRecoveryView.vue"),
        name: 'auth-recover-initiate'
    },
    {
        path: 'recover/complete',
        component: () => import("@/views/auth/CompleteRecoveryView.vue"),
        name: 'auth-recover-complete'
    }
] as RouteRecordRaw[];