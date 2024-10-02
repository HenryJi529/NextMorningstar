<script lang="ts" setup>
import {useRoute} from "vue-router";
import {ref} from "vue";
import type {ArticleDetail, UpdateArticleRequestVo} from "@/types/blog";
import {API_BLOG_ARTICLE_DETAIL} from "@/constants/ApiConstant";
import CommonManageEditForm from "@/components/common/CommonManageEditForm.vue";
import ArticleFields from "@/components/blog/ArticleFields.vue";

const route = useRoute();

const resolver = (articleDetail: ArticleDetail) => {
    return {
        title: articleDetail.article.title,
        content: articleDetail.article.content,
        categoryId: articleDetail.category.id,
        tagIds: articleDetail.tags.map(tag => tag.id),
    }
}

const data = ref<UpdateArticleRequestVo>({
    id: route.params.id as string,
    title: "",
    content: "",
    categoryId: 0,
    tagIds: []
});

</script>

<template>
    <common-manage-edit-form :api="API_BLOG_ARTICLE_DETAIL" :data="data" :resolver="resolver" app="blog"
                             resource="article">
        <template #fields>
            <article-fields :data="data"/>
        </template>
    </common-manage-edit-form>
</template>

<style lang="scss" scoped>

</style>