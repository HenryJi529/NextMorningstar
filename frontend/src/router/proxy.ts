import type {RouteRecordRaw} from "vue-router";


export default [
    {
        path: 'node',
        children: [
            {
                path: 'add',
                name: 'proxy-manage-node-add',
                component: () => import("@/views/proxy/NodeManageAddView.vue")
            },
            {
                path: 'list',
                name: 'proxy-manage-node-list',
                component: () => import("@/views/proxy/NodeManageListView.vue")
            },
            {
                path: 'edit/:id',
                name: 'proxy-manage-node-edit',
                component: () => import("@/views/proxy/NodeManageEditView.vue")
            }
        ]
    },
    {
        path: 'sub',
        children: [
            {
                path: 'add',
                name: 'proxy-manage-sub-add',
                component: () => import("@/views/proxy/SubManageAddView.vue")
            },
            {
                path: 'list',
                name: 'proxy-manage-sub-list',
                component: () => import("@/views/proxy/SubManageListView.vue")
            },
            {
                path: 'edit/:id',
                name: 'proxy-manage-sub-edit',
                component: () => import("@/views/proxy/SubManageEditView.vue")
            }
        ]
    },
    {
        path: 'token',
        children: [
            {
                path: 'add',
                name: 'proxy-manage-token-add',
                component: () => import("@/views/proxy/TokenManageAddView.vue")
            },
            {
                path: 'list',
                name: 'proxy-manage-token-list',
                component: () => import("@/views/proxy/TokenManageListView.vue")
            },
            {
                path: 'edit/:id',
                name: 'proxy-manage-token-edit',
                component: () => import("@/views/proxy/TokenManageEditView.vue")
            }
        ]
    }
] as RouteRecordRaw[];