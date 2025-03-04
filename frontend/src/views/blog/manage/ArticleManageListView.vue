<script setup lang="ts">
import type {PageResult, R} from "@/types/common";
import type {ListResourceConfig, ArticleDetail, ArticleVerbose} from "@/types/blog";
import axios from "axios";
import {API_BLOG_ARTICLE_ALL, API_BLOG_ARTICLE_DETAIL} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";


let config : ListResourceConfig = {
    identityField: "title",
    defaultSortOrders: ['updated_desc'],
    itemConfigs: [
        {
            name: "title",
            sort: true,
            width: "flex-1",
            center: true,
        },
        {
            name: "category",
            sort: true,
            width: "flex-1",
            center: true,
        },
        {
            name: "tags",
            sort: false,
            width: "hidden md:block md:flex-1",
            center: true,
        },
        {
            name: "views",
            sort: true,
            width: "w-[6.5rem] hidden md:block",
            center: true,
        },
        {
            name: "created",
            sort: true,
            width: "hidden md:block lg:w-32",
            center: true,
        },
        {
            name: "updated",
            sort: true,
            width: "w-32",
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
            app="blog" resource="article"
            :delete-api="API_BLOG_ARTICLE_DETAIL"
            :origin-items="originItems"
            :config="config"/>
    </div>
</template>

<style scoped lang="scss">

</style>