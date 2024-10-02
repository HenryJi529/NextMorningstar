
import type {RouteRecordRaw} from "vue-router";
import UserManageListView from "@/views/admin/UserManageListView.vue";
import UserManageEditView from "@/views/admin/UserManageEditView.vue";

export default [
    {
        path: 'user',
        children: [
            {
                path: 'list',
                name: 'admin-manage-user-list',
                component: UserManageListView
            },
            {
                path: 'edit/:id',
                name: 'admin-manage-user-edit',
                component: UserManageEditView
            }
        ]
    }
] as RouteRecordRaw[];