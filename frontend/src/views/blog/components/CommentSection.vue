<script lang="ts" setup>
import type { CommentDetail, CreateCommentRequestVo } from '@/types/blog';
import { getAvatarLink } from '@/utils/media';
import { onMounted, onUnmounted, ref } from 'vue';
import router from '@/router';
import { useMarked, generateCodeBlockCopyButton } from '@/composables/marked';
const { convertMarkdownToHtml } = useMarked();
import { useUserStore } from '@/stores/users';
import type { R } from '@/types/common';
import { computedAsync } from '@vueuse/core';
import axios from '@/axios';
import {
    API_BLOG_COMMENT_ALL,
    API_BLOG_COMMENT_THUMBS_DOWN,
    API_BLOG_COMMENT_THUMBS_NONE,
    API_BLOG_COMMENT_THUMBS_UP,
} from '@/constants/api';
import { useRoute } from 'vue-router';
import { setMaxWidthToNaturalWidth } from '@/utils/image';
import DOMPurify from 'dompurify';
import { ResponseCode } from '@/constants/response';

const props = defineProps<{
    comments: CommentDetail[];
    articleId: string;
}>();

const emit = defineEmits<{
    (e: 'update'): void;
}>();
const route = useRoute();
const userStore = useUserStore();
const isEditingComment = ref(true);
const newCommentContent = ref('');
const newCommentParentId = ref<string | null>(null);
const newCommentRenderResult = ref('');

const commentBodyArray = computedAsync(async () => {
    const array = [];
    for (const comment of props.comments) {
        const commentBody = await convertMarkdownToHtml(comment.content);
        array.push(commentBody);
    }
    return array;
}, []);

const isCommentSubmitting = ref(false);

const jumpToCommentAnchor = () => {
    if (!route.hash) {
        return;
    }
    const targetElement = document.querySelector(route.hash);
    if (targetElement) {
        targetElement.scrollIntoView({ behavior: 'auto' });
    }
};

let timer: number;
onUnmounted(() => {
    window.clearInterval(timer);
});

onMounted(() => {
    timer = window.setInterval(async () => {
        newCommentRenderResult.value = await convertMarkdownToHtml(newCommentContent.value);
    }, 1000);
    jumpToCommentAnchor();
    setMaxWidthToNaturalWidth('.comment-body');
    generateCodeBlockCopyButton('.comment-body');
});

const scrollToBottom = () => {
    window.scrollTo({
        top: document.body.scrollHeight,
        behavior: 'smooth',
    });
};

const createNewComment = async () => {
    isCommentSubmitting.value = true;
    const data: CreateCommentRequestVo = {
        parentId: newCommentParentId.value,
        articleId: props.articleId,
        content: newCommentContent.value,
    };
    const response: R<object> = (await axios.post(API_BLOG_COMMENT_ALL, data)).data;
    isCommentSubmitting.value = false;
    if (response.code !== ResponseCode.SUCCESS) {
        return;
    }
    emit('update');
    newCommentContent.value = '';
};

const cleanThumbs = async (comment: CommentDetail) => {
    const response: R<object> = (
        await axios.post(API_BLOG_COMMENT_THUMBS_NONE.replace('{id}', comment.id), {})
    ).data;
    if (response.code !== ResponseCode.SUCCESS) {
        return;
    }
    comment.thumbsUpUserIds = comment.thumbsUpUserIds.filter(userId => userId !== userStore.id);
    comment.thumbsDownUserIds = comment.thumbsDownUserIds.filter(userId => userId !== userStore.id);
};

const toggleThumbsUp = async (comment: CommentDetail) => {
    if (comment.thumbsUpUserIds.includes(userStore.id)) {
        await cleanThumbs(comment);
    } else {
        const response: R<object> = (
            await axios.post(API_BLOG_COMMENT_THUMBS_UP.replace('{id}', comment.id), {})
        ).data;
        if (response.code !== ResponseCode.SUCCESS) {
            return;
        }
        comment.thumbsUpUserIds.push(userStore.id);
        comment.thumbsDownUserIds = comment.thumbsDownUserIds.filter(
            userId => userId !== userStore.id
        );
    }
};

const toggleThumbsDown = async (comment: CommentDetail) => {
    if (comment.thumbsDownUserIds.includes(userStore.id)) {
        await cleanThumbs(comment);
    } else {
        const response: R<object> = (
            await axios.post(API_BLOG_COMMENT_THUMBS_DOWN.replace('{id}', comment.id), {})
        ).data;
        if (response.code !== ResponseCode.SUCCESS) {
            return;
        }
        comment.thumbsDownUserIds.push(userStore.id);
        comment.thumbsUpUserIds = comment.thumbsUpUserIds.filter(userId => userId !== userStore.id);
    }
};
</script>

<template>
    <div>
        <div class="flex flex-col space-y-4 mb-6">
            <template v-for="(comment, index) in comments" :key="comment.id">
                <div :id="`comment-${comment.id}`" class="flex space-x-8 items-start pb-4">
                    <div class="avatar p-2">
                        <div class="w-12 rounded-full">
                            <img
                                :alt="`${comment.nickname}的头像`"
                                :src="getAvatarLink(comment.avatar)" />
                        </div>
                    </div>
                    <div class="w-full overflow-hidden rounded-xl border-2">
                        <div class="flex justify-between bg-blue-50 dark:bg-gray-700 p-2">
                            <div class="flex space-x-2">
                                <div class="font-bold">{{ comment.nickname }}</div>
                                <div>
                                    评论于 <span>{{ comment.updateTime }}</span>
                                </div>
                            </div>
                            <div class="flex items-center space-x-2">
                                <div
                                    :class="{
                                        'text-orange-400': comment.thumbsUpUserIds.includes(
                                            userStore.id
                                        ),
                                    }"
                                    class="flex items-center space-x-1 cursor-pointer"
                                    @click="toggleThumbsUp(comment)">
                                    <font-awesome-icon :icon="['fas', 'thumbs-up']" />
                                    <span>{{ comment.thumbsUpUserIds.length }}</span>
                                </div>
                                <div
                                    :class="{
                                        'text-orange-400': comment.thumbsDownUserIds.includes(
                                            userStore.id
                                        ),
                                    }"
                                    class="flex items-center space-x-1 cursor-pointer"
                                    @click="toggleThumbsDown(comment)">
                                    <font-awesome-icon :icon="['fas', 'thumbs-down']" />
                                    <span>{{ comment.thumbsDownUserIds.length }}</span>
                                </div>
                                <div
                                    class="flex items-center space-x-1 cursor-pointer"
                                    @click="
                                        newCommentParentId = comment.id;
                                        scrollToBottom();
                                    ">
                                    <font-awesome-icon :icon="['fas', 'reply']" />
                                </div>
                            </div>
                        </div>
                        <div class="p-2">
                            <a
                                v-if="comment.parentId"
                                :href="`#comment-${comment.parentId}`"
                                class="text-yellow-700 dark:text-yellow-600">
                                @<span>{{
                                    comments.filter(c => c.id == comment.parentId)[0].nickname
                                }}</span>
                            </a>
                            <div
                                class="comment-body"
                                v-html="DOMPurify.sanitize(commentBodyArray[index])"></div>
                        </div>
                    </div>
                </div>
            </template>
        </div>
        <div class="flex space-x-8 items-start">
            <div class="avatar p-2">
                <div class="w-12 rounded-full">
                    <img :alt="`${userStore.nickname}的头像`" :src="userStore.avatarLink" />
                </div>
            </div>
            <div class="flex-1 rounded-xl border-2 overflow-hidden">
                <div class="flex bg-slate-100 dark:bg-slate-600 space-x-1 rounded-t-xl p-2">
                    <div
                        :class="{
                            'bg-blue-200': isEditingComment,
                            'dark:bg-gray-700': isEditingComment,
                        }"
                        class="py-2 px-3 cursor-pointer rounded-xl"
                        @click="isEditingComment = !isEditingComment">
                        编写
                    </div>
                    <div
                        :class="{
                            'bg-blue-200': !isEditingComment,
                            'dark:bg-gray-700': !isEditingComment,
                        }"
                        class="py-2 px-3 cursor-pointer rounded-xl"
                        @click="isEditingComment = !isEditingComment">
                        预览
                    </div>
                </div>
                <div class="p-2">
                    <div v-if="newCommentParentId" class="flex space-x-1 items-center mb-2">
                        <a
                            v-if="newCommentParentId"
                            :href="`#comment-${newCommentParentId}`"
                            class="text-yellow-700 dark:text-yellow-600">
                            @<cite class="not-italic">{{
                                comments.filter(c => c.id == newCommentParentId)[0].nickname
                            }}</cite>
                        </a>
                        <font-awesome-icon
                            :icon="['fas', 'xmark']"
                            class="text-xl"
                            @click="newCommentParentId = null" />
                    </div>

                    <div class="p-2 min-h-[11rem]">
                        <div v-if="isEditingComment">
                            <textarea
                                v-model="newCommentContent"
                                class="bg-transparent w-full h-40 p-2 rounded-2xl input"
                                placeholder="留条评论呗～" />
                        </div>
                        <div v-else>
                            <div
                                class="comment-body"
                                v-html="DOMPurify.sanitize(newCommentRenderResult)"></div>
                        </div>
                    </div>
                </div>
                <div class="flex justify-end p-2 pt-0">
                    <div
                        v-if="userStore.id"
                        class="btn"
                        @click="!isCommentSubmitting && createNewComment()">
                        <span v-if="!isCommentSubmitting">评论</span>
                        <span v-else class="loading loading-spinner loading-sm"></span>
                    </div>
                    <div v-else class="btn" @click="router.push({ name: 'auth-login' })">登录</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@use '@/assets/css/others' as *;

.comment-body {
    @include markdown();
    @apply text-base;
    :deep(.katex) {
        @apply text-lg;
    }

    :deep(.katex:has(math[display='block'])) {
        @apply text-xl block py-3 overflow-x-scroll;
    }

    :deep(h1) {
        @apply text-2xl mt-3 mb-2;
        &::before {
            content: '#';
            @apply text-gray-500 mr-2;
        }
    }

    :deep(h2) {
        @apply text-xl mt-2 mb-2;
        &::before {
            content: '##';
            @apply text-gray-500 mr-2;
        }
    }

    :deep(h3) {
        @apply text-lg mt-1 mb-1;
        &::before {
            content: '###';
            @apply text-gray-500 mr-2;
        }
    }

    :deep(blockquote) {
        @apply pt-2 pr-0 pb-2 pl-3 mb-2 rounded-none border-0 border-l-[0.35rem] border-solid border-yellow-300;
    }

    :deep(table) {
        @apply my-0 pt-3;
        thead th,
        tbody td {
            @apply p-[6px];
        }
    }
}
</style>
