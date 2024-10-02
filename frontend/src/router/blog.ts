import type {RouteRecordRaw} from "vue-router";
import DetailView from "@/views/blog/DetailView.vue";
import ListView from "@/views/blog/ListView.vue";
import ContactView from "@/views/blog/ContactView.vue";
import EditView from "@/views/blog/EditView.vue";
import {BLOG_ARTICLE_MANAGE, BLOG_CATEGORY_MANAGE, BLOG_TAG_NAME} from "@/constants/PermissionConstant";

export default [
    {
        path: '',
        component: ListView,
        name: 'blog-list',
    },
    {
        path: 'contact',
        component: ContactView,
        name: 'blog-contact'
    },
    {
        path: 'edit',
        component: EditView,
        name: 'blog-edit',
        meta: {
            permissions: [
                BLOG_CATEGORY_MANAGE,
                BLOG_TAG_NAME,
                BLOG_ARTICLE_MANAGE
            ]
        }
    },
    {
        path: ':id',
        component: DetailView,
        name: 'blog-detail',
    }
] as RouteRecordRaw[];