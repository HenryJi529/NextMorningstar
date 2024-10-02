<script lang="ts" setup>
import type { ListResourceConfig, PageResult, R } from '@/types/common';
import type { ArticleDetail, ArticleVerbose } from '@/types/blog';
import axios from '@/axios/index';
import { API_BLOG_ARTICLE_ALL, API_BLOG_ARTICLE_DETAIL } from '@/constants/api';
import { onMounted, ref } from 'vue';
import CommonManageListTable from '@/components/manage/CommonManageListTable.vue';

const config: ListResourceConfig = {
    identityField: 'title',
    defaultSortOrders: ['updated_desc'],
    itemConfigs: [
        {
            name: 'title',
            sortable: true,
            class: 'flex-1',
            center: true,
        },
        {
            name: 'category',
            sortable: true,
            class: 'flex-1',
            center: true,
        },
        {
            name: 'tags',
            sortable: false,
            class: 'hidden md:flex md:flex-1',
            center: true,
        },
        {
            name: 'views',
            sortable: true,
            class: 'w-[6.5rem] hidden md:flex',
            center: true,
        },
        {
            name: 'created',
            sortable: true,
            class: 'hidden md:flex lg:w-32',
            center: true,
        },
        {
            name: 'updated',
            sortable: true,
            class: 'w-32',
            center: true,
        },
    ],
};

let originItems: ArticleVerbose[];
const isLoaded = ref(false);

onMounted(async () => {
    const response: R<PageResult<ArticleDetail>> = (
        await axios.get(`${API_BLOG_ARTICLE_ALL}?pageNum=1&pageSize=999`)
    ).data;
    originItems = response.data.records.map(articleDetail => {
        return {
            id: articleDetail.article.id,
            title: articleDetail.article.title,
            category: articleDetail.category?.name,
            tags: articleDetail.tags.map(tag => tag.name),
            views: articleDetail.article.views,
            created: articleDetail.article.createTime,
            updated: articleDetail.article.updateTime,
        };
    });
    isLoaded.value = true;
});
</script>

<template>
    <div v-if="isLoaded">
        <common-manage-list-table
            :config="config"
            :delete-api="API_BLOG_ARTICLE_DETAIL"
            :origin-items="originItems"
            app="blog"
            resource="article" />
    </div>
</template>

<style lang="scss" scoped></style>
