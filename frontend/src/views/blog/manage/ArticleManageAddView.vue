<script setup lang="ts">
import {ref} from "vue";
import {API_BLOG_ARTICLE_ALL} from "@/constants/ApiConstant";
import type {CreateArticleRequestVo} from "@/types/blog";
import CommonManageAddForm from "@/components/common/CommonManageAddForm.vue";
import ArticleFields from "@/components/blog/ArticleFields.vue";

const data = ref<CreateArticleRequestVo>({
    title: "",
    content: "",
    categoryId: 0,
    tagIds: []
});

</script>

<template>
    <common-manage-add-form app="blog" resource="article" :api="API_BLOG_ARTICLE_ALL" :data="data">
        <template #fields>
            <article-fields :data="data"/>
        </template>
    </common-manage-add-form>
</template>

<style scoped lang="scss">

</style>