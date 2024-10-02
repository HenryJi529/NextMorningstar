import LoginForm from "@/components/auth/LoginForm.vue";
import RegisterForm from "@/components/auth/RegisterForm.vue";

export default [
    {
        path: 'login',
        component: LoginForm,
    },
    {
        path: 'register',
        component: RegisterForm,
    }
]