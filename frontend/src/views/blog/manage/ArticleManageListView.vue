<script lang="ts" setup>
import type {ListResourceConfig, PageResult, R} from "@/types/common";
import type {ArticleDetail, ArticleVerbose} from "@/types/blog";
import axios from '@/axios/index';
import {API_BLOG_ARTICLE_ALL, API_BLOG_ARTICLE_DETAIL} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";


let config: ListResourceConfig = {
    identityField: "title",
    defaultSortOrders: ['updated_desc'],
    itemConfigs: [
        {
            name: "title",
            sort: true,
            class: "flex-1",
            center: true,
        },
        {
            name: "category",
            sort: true,
            class: "flex-1",
            center: true,
        },
        {
            name: "tags",
            sort: false,
            class: "hidden md:flex md:flex-1",
            center: true,
        },
        {
            name: "views",
            sort: true,
            class: "w-[6.5rem] hidden md:flex",
            center: true,
        },
        {
            name: "created",
            sort: true,
            class: "hidden md:flex lg:w-32",
            center: true,
        },
        {
            name: "updated",
            sort: true,
            class: "w-32",
            center: true,
        },
    ]
}

let originItems: ArticleVerbose[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<PageResult<ArticleDetail>> = (await axios.get(`${API_BLOG_ARTICLE_ALL}?pageNum=1&pageSize=999`)).data;
    originItems = response.data.records.map(articleDetail => {
        return {
            id: articleDetail.article.id,
            title: articleDetail.article.title,
            category: articleDetail.category?.name,
            tags: articleDetail.tags.map(tag => tag.name),
            views: articleDetail.article.views,
            created: articleDetail.article.createTime,
            updated: articleDetail.article.updateTime
        }
    });
    loaded.value = true;
})
</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            :config="config" :delete-api="API_BLOG_ARTICLE_DETAIL"
            :origin-items="originItems"
            app="blog"
            resource="article"/>
    </div>
</template>

<style lang="scss" scoped>

</style>