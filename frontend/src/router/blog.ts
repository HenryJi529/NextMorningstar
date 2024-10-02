import type {RouteRecordRaw} from "vue-router";
import {BLOG_ARTICLE_MANAGE, BLOG_CATEGORY_MANAGE, BLOG_TAG_MANAGE} from "@/constants/PermissionConstant";


export default [
    {
        path: '',
        component: () => import("@/views/blog/ListView.vue"),
        name: 'blog-list',
    },
    {
        path: 'contact',
        component: () => import("@/views/blog/ContactView.vue"),
        name: 'blog-contact'
    },
    {
        path: 'manage',
        component: () => import("@/views/blog/ManageView.vue"),
        name: 'blog-manage',
        meta: {
            requiresAuth: true,
            permissions: [
                BLOG_ARTICLE_MANAGE,
                BLOG_CATEGORY_MANAGE,
                BLOG_TAG_MANAGE,
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
                        component: () => import("@/views/blog/manage/ArticleManageAddView.vue")
                    },
                    {
                        path: 'list',
                        name: 'blog-manage-article-list',
                        component: () => import("@/views/blog/manage/ArticleManageListView.vue")
                    },
                    {
                        path: 'edit/:id',
                        name: 'blog-manage-article-edit',
                        component: () => import("@/views/blog/manage/ArticleManageEditView.vue")
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
                        component: () => import("@/views/blog/manage/CategoryManageAddView.vue")
                    },
                    {
                        path: 'list',
                        name: 'blog-manage-category-list',
                        component: () => import("@/views/blog/manage/CategoryManageListView.vue")
                    },
                    {
                        path: 'edit/:id',
                        name: 'blog-manage-category-edit',
                        component: () => import("@/views/blog/manage/CategoryManageEditView.vue")
                    }
                ]
            },
            {
                path: 'tag',
                meta: {
                    permissions: [
                        BLOG_TAG_MANAGE,
                    ]
                },
                children: [
                    {
                        path: 'add',
                        name: 'blog-manage-tag-add',
                        component: () => import("@/views/blog/manage/TagManageAddView.vue")
                    },
                    {
                        path: 'list',
                        name: 'blog-manage-tag-list',
                        component: () => import("@/views/blog/manage/TagManageListView.vue")
                    },
                    {
                        path: 'edit/:id',
                        name: 'blog-manage-tag-edit',
                        component: () => import("@/views/blog/manage/TagManageEditView.vue")
                    }
                ]
            },
        ]
    },
    {
        path: ':id',
        component: () => import("@/views/blog/DetailView.vue"),
        name: 'blog-detail',
    }
] as RouteRecordRaw[];