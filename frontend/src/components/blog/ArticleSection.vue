<script setup lang="ts">
import {onMounted} from "vue";
import type {ArticleDetail} from "@/types/blog";
import ArticleInfoBar from "@/components/blog/ArticleInfoBar.vue";
import {generateCodeBlockCopyButton} from "@/utils/handleMarked";
import {useUserStore} from "@/stores/users";
import {setMaxWidthToNaturalWidth} from "@/utils/handleImage";

defineProps<{
    articleDetail: ArticleDetail;
    articleBody: string;
}>()

const userStore = useUserStore();

onMounted(()=>{
    setMaxWidthToNaturalWidth();
    generateCodeBlockCopyButton("article-body");
})

</script>

<template>
    <article>
        <div class="text-center text-4xl pb-2">{{articleDetail.article.title}}</div>
        <article-info-bar :article-detail="articleDetail" class="text-base pb-2"/>
        <div v-html="articleBody" class="article-body" :class="{'select-none': !userStore.isAuthenticated}"></div>
    </article>
</template>

<style scoped lang="scss">
@use "@/assets/css/others.scss" as *;

.article-body {
    @include markdown();
    @apply text-lg;
    ::v-deep(.katex) {
        @apply text-xl;
    }
    ::v-deep(.katex:has(math[display="block"])) {
        @apply text-2xl block py-4 overflow-x-scroll;
    }
    ::v-deep(h2) {
        @apply text-3xl mt-6 mb-3;
        &::before {
            content: "#";
            @apply text-gray-500 mr-2;
        }
    }
    ::v-deep(h3) {
        @apply text-2xl mt-5 mb-2;
        &::before {
            content: "##";
            @apply text-gray-500 mr-2;
        }
    }
    ::v-deep(h4) {
        @apply text-xl mt-4 mb-1;
        &::before {
            content: "###";
            @apply text-gray-500 mr-2;
        }
    }
    ::v-deep(h5) {
        @apply text-lg mt-3 mb-1;
        &::before {
            content: "####";
            @apply text-gray-500 mr-2;
        }
    }
    ::v-deep(blockquote) {
        @apply pt-2 pr-0 pb-2 pl-3 mb-2 rounded-none border-0 border-l-[0.35rem] border-solid border-yellow-300;
    }
    ::v-deep(table) {
        @apply my-0 pt-5;
        thead th, tbody td {
            @apply p-[10px];
        }
    }
}
</style>