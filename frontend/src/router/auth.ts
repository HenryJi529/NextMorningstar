import type {RouteRecordRaw} from "vue-router";
import LoginForm from "@/components/auth/LoginForm.vue";
import RegisterForm from "@/components/auth/RegisterForm.vue";


export default [
    {
        path: 'login',
        component: LoginForm,
        name: 'auth-login',
    },
    {
        path: 'register',
        component: RegisterForm,
        name: 'auth-register'
    }
] as RouteRecordRaw[];