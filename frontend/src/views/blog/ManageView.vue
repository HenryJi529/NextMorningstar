<script setup lang="ts">
import CommonManagePanel from "@/components/common/CommonManagePanel.vue";
import type {NavItem4Manage} from "@/types/common";

const items: NavItem4Manage[] = [
    {
        cnName: '文章',
        enName: 'article',
        listRouteName: 'blog-manage-article-list',
        editRouteName: 'blog-manage-article-edit',
        iconName: 'fa-brands fa-markdown'
    },
    {
        cnName: '分类',
        enName: 'category',
        listRouteName: 'blog-manage-category-list',
        editRouteName: 'blog-manage-category-edit',
        iconName: 'folder'
    },
    {
        cnName: '标签',
        enName: 'tag',
        listRouteName: 'blog-manage-tag-list',
        editRouteName: 'blog-manage-tag-edit',
        iconName: 'tag'
    }
]

</script>

<template>
    <common-manage-panel :items="items"></common-manage-panel>
</template>

<style scoped lang="scss">
</style>