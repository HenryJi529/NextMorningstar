import type { RouteRecordRaw } from 'vue-router';
import { Permission } from '@/constants/auth';

export default [
    {
        path: 'user',
        meta: {
            permissions: [Permission.SYS_USER_MANAGE],
        },
        children: [
            {
                path: 'list',
                name: 'admin-manage-user-list',
                component: () => import('@/views/admin/UserManageListView.vue'),
            },
            {
                path: 'edit/:id',
                name: 'admin-manage-user-edit',
                component: () => import('@/views/admin/UserManageEditView.vue'),
            },
        ],
    },
] as RouteRecordRaw[];
