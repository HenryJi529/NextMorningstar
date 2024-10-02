<script lang="ts" setup>
import {onMounted, ref} from "vue";
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
const isImageModalOpen = ref(false);
const selectedImageLink = ref("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7")
const setImageClickAction = () => {
    setTimeout(() => {
        let imageElements = document.querySelectorAll(".article-body img") as NodeListOf<HTMLImageElement>;
        imageElements.forEach((element) => {
            element.addEventListener("click", () => {
                selectedImageLink.value = element.src;
                isImageModalOpen.value = true;
            });
        })
    }, 100)
}

onMounted(() => {
    setMaxWidthToNaturalWidth(".article-body");
    generateCodeBlockCopyButton(".article-body");
    setImageClickAction();
})

</script>

<template>
    <article>
        <div class="text-center text-4xl pb-2">{{ articleDetail.article.title }}</div>
        <article-info-bar :article-detail="articleDetail" class="text-base pb-2"/>
        <div v-show="isImageModalOpen"
             class="fixed w-screen h-screen top-0 left-0 z-10 bg-gray-800/90 flex items-center justify-center">
            <div class="rounded-full absolute right-8 top-8 cursor-pointer z-20" @click="isImageModalOpen = false">
                <font-awesome-icon :icon="['fas', 'xmark']" class="text-4xl text-stone-200 dark:text-stone-700"/>
            </div>
            <img :src="selectedImageLink" alt="大图"/>
        </div>
        <div :class="{'select-none': !userStore.isAuthenticated}" class="article-body" v-html="articleBody"></div>
    </article>
</template>

<style lang="scss" scoped>
@use "@/assets/css/others.scss" as *;

.article-body {
    @include markdown();
    @apply text-lg;
    :deep(.katex) {
        @apply text-xl;
    }

    :deep(.katex:has(math[display="block"])) {
        @apply text-2xl block py-4 overflow-x-scroll;
    }

    :deep(h2) {
        @apply text-3xl mt-6 mb-3;
        &::before {
            content: "#";
            @apply text-gray-500 mr-2;
        }
    }

    :deep(h3) {
        @apply text-2xl mt-5 mb-2;
        &::before {
            content: "##";
            @apply text-gray-500 mr-2;
        }
    }

    :deep(h4) {
        @apply text-xl mt-4 mb-1;
        &::before {
            content: "###";
            @apply text-gray-500 mr-2;
        }
    }

    :deep(h5) {
        @apply text-lg mt-3 mb-1;
        &::before {
            content: "####";
            @apply text-gray-500 mr-2;
        }
    }

    :deep(blockquote) {
        @apply pt-2 pr-0 pb-2 pl-3 mb-2 rounded-none border-0 border-l-[0.35rem] border-solid border-yellow-300;
    }

    :deep(table) {
        @apply my-0 pt-5;
        thead th, tbody td {
            @apply p-[10px];
        }
    }
}
</style>