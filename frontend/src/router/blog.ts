import type {RouteRecordRaw} from "vue-router";
import DetailView from "@/views/blog/DetailView.vue";
import ListView from "@/views/blog/ListView.vue";
import ContactView from "@/views/blog/ContactView.vue";
import ManageView from "@/views/blog/ManageView.vue";
import {BLOG_ARTICLE_MANAGE, BLOG_CATEGORY_MANAGE, BLOG_TAG_NAME} from "@/constants/PermissionConstant";
import ArticleManageListView from "@/views/blog/manage/ArticleManageListView.vue";
import CategoryManageListView from "@/views/blog/manage/CategoryManageListView.vue";
import TagManageListView from "@/views/blog/manage/TagManageListView.vue";
import ArticleManageEditView from "@/views/blog/manage/ArticleManageEditView.vue";
import CategoryManageEditView from "@/views/blog/manage/CategoryManageEditView.vue";
import TagManageEditView from "@/views/blog/manage/TagManageEditView.vue";
import ArticleManageAddView from "@/views/blog/manage/ArticleManageAddView.vue";
import CategoryManageAddView from "@/views/blog/manage/CategoryManageAddView.vue";
import TagManageAddView from "@/views/blog/manage/TagManageAddView.vue";
import {ROLE_BLOG_ADMIN} from "@/constants/RoleConstant";

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
        path: 'manage',
        component: ManageView,
        name: 'blog-manage',
        meta: {
            permissions: [
                ROLE_BLOG_ADMIN
            ]
        },
        children: [
            {
                path: 'article',
                meta: {
                    permissions: [
                        BLOG_ARTICLE_MANAGE
                    ]
                },
                children: [
                    {
                        path: 'add',
                        name: 'blog-manage-article-add',
                        component: ArticleManageAddView
                    },
                    {
                        path: 'list',
                        name: 'blog-manage-article-list',
                        component: ArticleManageListView
                    },
                    {
                        path: 'edit/:id',
                        name: 'blog-manage-article-edit',
                        component: ArticleManageEditView
                    }
                ]
            },
            {
                path: 'category',
                meta: {
                    permissions: [
                        BLOG_CATEGORY_MANAGE,
                    ]
                },
                children: [
                    {
                        path: 'add',
                        name: 'blog-manage-category-add',
                        component: CategoryManageAddView
                    },
                    {
                        path: 'list',
                        name: 'blog-manage-category-list',
                        component: CategoryManageListView
                    },
                    {
                        path: 'edit/:id',
                        name: 'blog-manage-category-edit',
                        component: CategoryManageEditView
                    }
                ]
            },
            {
                path: 'tag',
                meta: {
                    permissions: [
                        BLOG_TAG_NAME,
                    ]
                },
                children: [
                    {
                        path: 'add',
                        name: 'blog-manage-tag-add',
                        component: TagManageAddView
                    },
                    {
                        path: 'list',
                        name: 'blog-manage-tag-list',
                        component: TagManageListView
                    },
                    {
                        path: 'edit/:id',
                        name: 'blog-manage-tag-edit',
                        component: TagManageEditView
                    }
                ]
            },
        ]
    },
    {
        path: ':id',
        component: DetailView,
        name: 'blog-detail',
    }
] as RouteRecordRaw[];