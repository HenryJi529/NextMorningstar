<script lang="ts" setup>

import {useRoute, useRouter} from "vue-router";
import {onMounted, reactive, ref, watch} from "vue";
import axios from '@/axios/index';
import {
    API_BLOG_ARTICLE_ALL,
    API_BLOG_ARTICLE_CATEGORY,
    API_BLOG_ARTICLE_SEARCH,
    API_BLOG_ARTICLE_TAG
} from "@/constants/ApiConstant";
import type {PageResult, R} from "@/types/common";
import type {ArticleDetail} from "@/types/blog";
import ArticleInfoBar from "@/components/blog/ArticleInfoBar.vue";
import CommonGuestArea from "@/components/blog/CommonGuestArea.vue";
import {getFirstParam} from "@/utils/handleHttp";

const route = useRoute();
const router = useRouter();

const DEFAULT_PAGE_SIZE = 7;
const DEFAULT_PAGE_NUM = 1;

const queryParams = reactive<{
    term?: string,
    pageNum: number
    categoryId?: number,
    tagId?: number,
}>({
    pageNum: DEFAULT_PAGE_NUM,
})

const clearQueryParams = () => {
    queryParams.term = undefined;
    queryParams.pageNum = DEFAULT_PAGE_NUM;
    queryParams.categoryId = undefined
    queryParams.tagId = undefined
}

const parseQueryParams = () => {
    if ("term" in route.query) {
        queryParams.term = getFirstParam(route.query.term as string[] | string);
    }
    if ("pageNum" in route.query) {
        queryParams.pageNum = parseInt(getFirstParam(route.query.pageNum as string[] | string));
    }
    if ("categoryId" in route.query) {
        queryParams.categoryId = parseInt(getFirstParam(route.query.categoryId as string[] | string));
    }
    if ("tagId" in route.query) {
        queryParams.tagId = parseInt(getFirstParam(route.query.tagId as string[] | string));
    }
}

const pageResult = ref<PageResult<ArticleDetail>>();

const getArticleDetailList = async () => {
    let url;
    if (queryParams.term) {
        url = `${API_BLOG_ARTICLE_SEARCH}?term=${queryParams.term}&pageSize=${DEFAULT_PAGE_SIZE}&pageNum=${queryParams.pageNum}`;
    } else if (queryParams.categoryId) {
        url = `${API_BLOG_ARTICLE_CATEGORY.replace('{id}', queryParams.categoryId.toString())}?pageSize=${DEFAULT_PAGE_SIZE}&pageNum=${queryParams.pageNum}`;
    } else if (queryParams.tagId) {
        url = `${API_BLOG_ARTICLE_TAG.replace('{id}', queryParams.tagId.toString())}?pageSize=${DEFAULT_PAGE_SIZE}&pageNum=${queryParams.pageNum}`;
    } else {
        url = `${API_BLOG_ARTICLE_ALL}?pageSize=${DEFAULT_PAGE_SIZE}&pageNum=${queryParams.pageNum}`;
    }
    const response: R<PageResult<ArticleDetail>> = (await axios.get(url)).data;
    console.log(response)
    pageResult.value = response.data;
}

const handleQuery = async () => {
    parseQueryParams();
    if (isNaN(queryParams.pageNum) || queryParams.pageNum < 0) {
        // 矫正分页参数参数
        queryParams.pageNum = DEFAULT_PAGE_NUM;
        await router.push({name: 'blog-list', query: queryParams})
    } else {
        await getArticleDetailList();
    }
}

const viewPrePage = async () => {
    queryParams.pageNum--;
    await router.push({name: 'blog-list', query: queryParams})
}

const viewNextPage = async () => {
    queryParams.pageNum++;
    await router.push({name: 'blog-list', query: queryParams})
}


watch(() => route.fullPath, async () => {
    clearQueryParams();
    await handleQuery();
});

onMounted(async () => {
    await handleQuery();
})


</script>

<template>
    <common-guest-area>
        <template #custom-main-part>
            <div v-if="pageResult" class="h-full">
                <div v-if="pageResult.currentPageSize > 0" class="h-full px-4 flex flex-col">
                    <!-- 文章列表 -->
                    <div class="py-4 flex flex-col space-y-4 flex-1">
                        <template v-for="articleDetail in pageResult.records" :key="articleDetail.article.id">
                            <div class="overflow-hidden w-full">
                                <div class="cursor-pointer"
                                     @click="router.push({name: 'blog-detail', params: {id: articleDetail.article.id}})">
                                    <div class="text-center text-2xl hover:text-primary"
                                         v-html="articleDetail.article.title"></div>
                                </div>
                                <article-info-bar :article-detail="articleDetail" class="text-sm mb-1"/>
                                <div class="indent-8 break-words select-none"
                                     v-html="articleDetail.article.content"></div>
                                <div class="text-center flex justify-center items-center space-x-2 cursor-pointer mt-2"
                                     @click="router.push({name: 'blog-detail', params: {id: articleDetail.article.id}})">
                                    <span class="italic hover:animate-pulse">继续阅读</span>
                                    <font-awesome-icon :icon="['fas', 'arrow-right']"/>
                                </div>
                            </div>
                        </template>
                    </div>
                    <!-- 分页 -->
                    <div v-if="pageResult" class="flex justify-between text-xl p-2 px-4 pb-10">
                        <div :class="{'invisible': pageResult.pageNum == 1}"
                             class="cursor-pointer flex items-center space-x-1"
                             @click="viewPrePage">
                            <font-awesome-icon :icon="['fas', 'angles-left']"/>
                            <span class="font-bold">上一页</span>
                        </div>
                        <div class="font-bold cursor-pointer">
                            {{ pageResult.pageNum }} / {{ pageResult.totalPageNum }}
                        </div>
                        <div :class="{'invisible': pageResult.pageNum == pageResult.totalPageNum}"
                             class="cursor-pointer flex items-center space-x-1"
                             @click="viewNextPage">
                            <span class="font-bold">下一页</span>
                            <font-awesome-icon :icon="['fas', 'angles-right']"/>
                        </div>
                    </div>
                </div>
                <div v-else class="h-32 flex justify-center items-center">
                    <div class="text-center font-bold text-6xl">
                        <div>词条不存在</div>
                    </div>
                </div>
            </div>
        </template>
    </common-guest-area>
</template>

<style lang="scss" scoped>

</style>