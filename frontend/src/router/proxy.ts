import type {RouteRecordRaw} from "vue-router";
import NodeManageAddView from "@/views/proxy/NodeManageAddView.vue";
import SubManageAddView from "@/views/proxy/SubManageAddView.vue";
import SubManageEditView from "@/views/proxy/SubManageEditView.vue";
import SubManageListView from "@/views/proxy/SubManageListView.vue";
import NodeManageListView from "@/views/proxy/NodeManageListView.vue";
import NodeManageEditView from "@/views/proxy/NodeManageEditView.vue";
import TokenManageEditView from "@/views/proxy/TokenManageEditView.vue";
import TokenManageListView from "@/views/proxy/TokenManageListView.vue";
import TokenManageAddView from "@/views/proxy/TokenManageAddView.vue";

export default [
    {
        path: 'node',
        children: [
            {
                path: 'add',
                name: 'proxy-manage-node-add',
                component: NodeManageAddView
            },
            {
                path: 'list',
                name: 'proxy-manage-node-list',
                component: NodeManageListView
            },
            {
                path: 'edit/:id',
                name: 'proxy-manage-node-edit',
                component: NodeManageEditView
            }
        ]
    },
    {
        path: 'sub',
        children: [
            {
                path: 'add',
                name: 'proxy-manage-sub-add',
                component: SubManageAddView
            },
            {
                path: 'list',
                name: 'proxy-manage-sub-list',
                component: SubManageListView
            },
            {
                path: 'edit/:id',
                name: 'proxy-manage-sub-edit',
                component: SubManageEditView
            }
        ]
    },
    {
        path: 'token',
        children: [
            {
                path: 'add',
                name: 'proxy-manage-token-add',
                component: TokenManageAddView
            },
            {
                path: 'list',
                name: 'proxy-manage-token-list',
                component: TokenManageListView
            },
            {
                path: 'edit/:id',
                name: 'proxy-manage-token-edit',
                component: TokenManageEditView
            }
        ]
    }
] as RouteRecordRaw[];