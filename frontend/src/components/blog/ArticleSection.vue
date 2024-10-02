<script setup lang="ts">
import {onMounted} from "vue";
import type {ArticleDetail} from "@/types/blog";
import ArticleInfoBar from "@/components/blog/ArticleInfoBar.vue";
import {generateCodeBlockCopyButton} from "@/utils/handleMarked";

defineProps<{
    articleDetail: ArticleDetail;
    articleBody: string;
}>()

onMounted(()=>{
    generateCodeBlockCopyButton("article-body");
})

</script>

<template>
    <article>
        <div class="text-center text-4xl pb-2">{{articleDetail.article.title}}</div>
        <article-info-bar :article-detail="articleDetail" class="text-base pb-2"/>
        <div v-html="articleBody" class="article-body"></div>
    </article>
</template>

<style scoped lang="scss">
@import "@/assets/css/others.scss";

.article-body {
    @include markdown();
    @apply text-lg;
    ::v-deep(.katex) {
        @apply text-xl;
    }
    ::v-deep(.katex:has(math[display="block"])) {
        @apply text-3xl block py-4 overflow-scroll;
    }
    ::v-deep(p){
        @apply mb-4;
    }
    ::v-deep(ul), ::v-deep(ol){
        @apply pl-8;
    }
    ::v-deep(ol ol), ::v-deep(ul ol), ::v-deep(ol ul), ::v-deep(ul ul){
        @apply pl-6;
    }
    ::v-deep(ol) {
        li {
            @apply mb-[0.2rem];
        }
    }
    ::v-deep(ul) {
        li {
            @apply mb-[0.2rem];
        }
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
    ::v-deep(img) {
        @apply block w-10/12 mx-auto my-2;
    }
    ::v-deep(code:not(pre code)){
        @apply mx-[0.2rem];
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