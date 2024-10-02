<script setup lang="ts">
import {useRouter} from "vue-router";
import type {ArticleDetail} from "@/types/blog";

const router = useRouter();

defineProps<{
    articleDetail: ArticleDetail;
}>()
</script>

<template>
    <div class="flex flex-wrap justify-center space-x-2">
        <div class="flex space-x-1 items-center" @click="router.push({name: 'blog-list', query: {categoryId: articleDetail.category.id}})">
            <font-awesome-icon :icon="['fas', 'folder']" />
            <span class="cursor-pointer hover:text-primary">{{articleDetail.category.name}}</span>
        </div>
        <div class="flex space-x-1 items-center" v-if="articleDetail.tags.length > 0">
            <font-awesome-icon :icon="['fas', 'tag']" />
            <div>
                <template v-for="(tag, index) in articleDetail.tags">
                    <span v-if="index != 0" class="px-1">|</span>
                    <span class="cursor-pointer hover:text-primary" @click="router.push({name: 'blog-list', query: {tagId: tag.id}})">
                        {{tag.name}}
                    </span>
                </template>
            </div>
        </div>
        <div class="flex space-x-1 items-center">
            <font-awesome-icon :icon="['fas', 'eye']" />
            <span>{{articleDetail.article.views}}</span>
        </div>
        <div class="flex space-x-1 items-center">
            <font-awesome-icon :icon="['fas', 'pencil']" />
            <span>{{articleDetail.article.createTime.split(" ")[0]}}</span>
        </div>
        <div class="flex space-x-1 items-center">
            <font-awesome-icon :icon="['fas', 'rotate']" />
            <span>{{articleDetail.article.updateTime.split(" ")[0]}}</span>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>