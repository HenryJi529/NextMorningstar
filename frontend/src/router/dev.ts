import type { RouteRecordRaw } from 'vue-router';
import { Role } from '@/constants/auth';

export default [
    {
        path: '',
        name: 'dev-project',
        component: () => import('@/views/dev/ProjectView.vue'),
    },
    {
        path: 'admin',
        name: 'dev-admin',
        component: () => import('@/views/dev/AdminView.vue'),
        meta: {
            permissions: [Role.DEV_ADMIN],
        },
    },
    {
        path: 'about',
        name: 'dev-about',
        component: () => import('@/views/dev/AboutView.vue'),
    },
] as RouteRecordRaw[];
