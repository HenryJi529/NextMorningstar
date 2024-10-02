import type {RouteRecordRaw} from "vue-router";
import {SYS_USER_MANAGE} from "@/constants/PermissionConstant";

export default [
    {
        path: 'user',
        meta: {
            permissions: [
                SYS_USER_MANAGE
            ]
        },
        children: [
            {
                path: 'list',
                name: 'admin-manage-user-list',
                component: () => import("@/views/admin/UserManageListView.vue")
            },
            {
                path: 'edit/:id',
                name: 'admin-manage-user-edit',
                component: () => import("@/views/admin/UserManageEditView.vue")
            }
        ]
    }
] as RouteRecordRaw[];