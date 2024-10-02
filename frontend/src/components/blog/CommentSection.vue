<script setup lang="ts">
import type {CommentDetail, CreateCommentRequestVo} from "@/types/blog";
import {getAvatarLink} from "@/utils/handleMedia";
import {onMounted, ref} from "vue";
import router from "@/router";
import {generateCodeBlockCopyButton, getMarked} from "@/utils/handleMarked";
import {useUserStore} from "@/stores/users";
import type {R} from "@/types/common";
import axios from "axios";
import {
    API_BLOG_COMMENT_ALL,
    API_BLOG_COMMENT_THUMBS_DOWN,
    API_BLOG_COMMENT_THUMBS_NONE,
    API_BLOG_COMMENT_THUMBS_UP
} from "@/constants/ApiConstant";
import markedKatex from "marked-katex-extension";
import {TOKEN} from "@/constants/LocalStorageConstant";
import {useRoute} from "vue-router";

const props = defineProps<{
    comments: CommentDetail[];
    commentBodyArray: string[];
    articleId: string;
}>()

const emit = defineEmits(['update'])
const route = useRoute();
const userStore = useUserStore();
const isEditingComment = ref(true);
const newCommentContent = ref("");
const newCommentParentId = ref<string|null>(null);
const newCommentRenderResult = ref("");
const marked = getMarked();
marked.use(markedKatex({
    throwOnError: false,
    output: "mathml"
}));

const isCommentSubmitting = ref(false);

const jumpToCommentAnchor = () => {
    if(!route.hash){
        return;
    }
    const targetElement = document.querySelector(route.hash);
    if (targetElement) {
        targetElement.scrollIntoView({ behavior: 'auto' });
    }
}

setInterval(async ()=>{
    newCommentRenderResult.value = await marked.parse(newCommentContent.value);
},1000);

onMounted(()=>{
    jumpToCommentAnchor();
    generateCodeBlockCopyButton("comment-body");
})

const scrollToBottom = () => {
    window.scrollTo({
        top: document.body.scrollHeight,
        behavior: 'smooth'
    });
}

const createNewComment = async () => {
    isCommentSubmitting.value = true;
    const data: CreateCommentRequestVo = {
        parentId: newCommentParentId.value,
        articleId: props.articleId,
        content: newCommentContent.value,
    }
    const response: R<object> = (await axios.post(
        API_BLOG_COMMENT_ALL,
        data,
        {
            headers: {
                Authorization: localStorage.getItem(TOKEN)
            }
        }
    )).data;
    isCommentSubmitting.value = false;
    if (response.code != 1){
        return;
    }
    emit("update");
    newCommentContent.value = "";
}

const cleanThumbs = async (comment: CommentDetail) => {
    const response: R<object> = (await axios.post(API_BLOG_COMMENT_THUMBS_NONE.replace('{id}', comment.id), {}, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data;
    if (response.code != 1){
        return;
    }
    comment.thumbsUpUserIds = comment.thumbsUpUserIds.filter(userId => userId !== userStore.id);
    comment.thumbsDownUserIds = comment.thumbsDownUserIds.filter(userId => userId !== userStore.id);
}

const toggleThumbsUp = async (comment: CommentDetail) => {
    if(comment.thumbsUpUserIds.includes(userStore.id)){
        await cleanThumbs(comment);
    }else{
        const response: R<object> = (await axios.post(API_BLOG_COMMENT_THUMBS_UP.replace('{id}', comment.id), {}, {
            headers: {
                Authorization: localStorage.getItem(TOKEN)
            }
        })).data;
        if (response.code != 1){
            return;
        }
        comment.thumbsUpUserIds.push(userStore.id);
        comment.thumbsDownUserIds = comment.thumbsDownUserIds.filter(userId => userId !== userStore.id);
    }
}


const toggleThumbsDown = async (comment: CommentDetail) => {
    if(comment.thumbsDownUserIds.includes(userStore.id)){
        await cleanThumbs(comment);
    }else{
        const response: R<object> = (await axios.post(API_BLOG_COMMENT_THUMBS_DOWN.replace('{id}', comment.id), {}, {
            headers: {
                Authorization: localStorage.getItem(TOKEN)
            }
        })).data;
        if (response.code != 1){
            return;
        }
        comment.thumbsDownUserIds.push(userStore.id);
        comment.thumbsUpUserIds = comment.thumbsUpUserIds.filter(userId => userId !== userStore.id);
    }
}


</script>

<template>
    <div>
        <div class="flex flex-col space-y-4 mb-6">
            <template v-for="(comment, index) in comments">
                <div class="flex space-x-8 items-start pb-4" :id="`comment-${comment.id}`">
                    <div class="avatar p-2">
                        <div class="w-12 rounded-full">
                            <img :src="comment.avatar ? getAvatarLink(comment.avatar): '/media/blog/img/avatar.svg'" :alt="`${comment.nickname}的头像`"/>
                        </div>
                    </div>
                    <div class="w-full overflow-hidden rounded-xl border-2">
                        <div class="flex justify-between bg-blue-50 dark:bg-gray-700 p-2">
                            <div class="flex space-x-2">
                                <div class="font-bold">{{comment.nickname}}</div>
                                <div>评论于 <span>{{comment.updateTime}}</span></div>
                            </div>
                            <div class="flex items-center space-x-2">
                                <div class="flex items-center space-x-1 cursor-pointer" :class="{'text-orange-400': comment.thumbsUpUserIds.includes(userStore.id)}" @click="toggleThumbsUp(comment)">
                                    <font-awesome-icon :icon="['fas', 'thumbs-up']" />
                                    <span>{{comment.thumbsUpUserIds.length}}</span>
                                </div>
                                <div class="flex items-center space-x-1 cursor-pointer" :class="{'text-orange-400': comment.thumbsDownUserIds.includes(userStore.id)}" @click="toggleThumbsDown(comment)">
                                    <font-awesome-icon :icon="['fas', 'thumbs-down']" />
                                    <span>{{comment.thumbsDownUserIds.length}}</span>
                                </div>
                                <div class="flex items-center space-x-1 cursor-pointer" @click="newCommentParentId = comment.id;scrollToBottom()">
                                    <font-awesome-icon :icon="['fas', 'reply']" />
                                </div>
                            </div>
                        </div>
                        <div class="p-2">
                            <a :href="`#comment-${comment.parentId}`" v-if="comment.parentId" class="text-yellow-700 dark:text-yellow-600">
                                @<span>{{comments.filter(c => c.id == comment.parentId)[0].nickname}}</span>
                            </a>
                            <div v-html="commentBodyArray[index]" class="comment-body"></div>
                        </div>
                    </div>
                </div>
            </template>
        </div>
        <div class="flex space-x-8 items-start">
            <div class="avatar p-2">
                <div class="w-12 rounded-full">
                    <img :src="userStore.avatar ? userStore.avatarLink: '/media/blog/img/avatar.svg'" :alt="`${userStore.nickname}的头像`"/>
                </div>
            </div>
            <div class="flex-1 rounded-xl border-2 overflow-hidden">
                <div class="flex bg-slate-100 dark:bg-slate-600 space-x-1 rounded-t-xl p-2">
                    <div class="py-2 px-3 cursor-pointer rounded-xl" :class="{'bg-blue-200': isEditingComment, 'dark:bg-gray-700': isEditingComment}" @click="isEditingComment = !isEditingComment">编写</div>
                    <div class="py-2 px-3 cursor-pointer rounded-xl" :class="{'bg-blue-200': !isEditingComment, 'dark:bg-gray-700': !isEditingComment}" @click="isEditingComment = !isEditingComment">预览</div>
                </div>
                <div class="p-2">
                    <div class="flex space-x-1 items-center mb-2" v-if="newCommentParentId">
                        <a :href="`#comment-${newCommentParentId}`" v-if="newCommentParentId" class="text-yellow-700 dark:text-yellow-600">
                            @<span>{{comments.filter(c => c.id == newCommentParentId)[0].nickname}}</span>
                        </a>
                        <font-awesome-icon :icon="['fas', 'xmark']" class="text-xl" @click="newCommentParentId = null"/>
                    </div>

                    <div v-if="isEditingComment">
                        <textarea v-model="newCommentContent" class="bg-transparent w-full h-40 p-2 rounded-2xl input" placeholder="留条评论呗～" />
                    </div>
                    <div v-else class="p-2">
                        <div v-html="newCommentRenderResult" class="comment-body"></div>
                    </div>
                </div>
                <div class="flex justify-end p-2 pt-0">
                    <div class="btn" v-if="userStore.id">
                        <span v-if="!isCommentSubmitting" @click="createNewComment">评论</span>
                        <span v-else class="loading loading-spinner loading-sm"></span>
                    </div>
                    <div class="btn" v-else @click="router.push({name: 'auth-login'})">登录</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import "@/assets/css/others.scss";

.comment-body{
    @include markdown();
    @apply text-base;
    ::v-deep(.katex) {
        @apply text-lg;
    }
    ::v-deep(.katex:has(math[display="block"])) {
        @apply text-xl block py-3 overflow-x-scroll;
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
    ::v-deep(h1) {
        @apply text-2xl mt-3 mb-2;
        &::before {
            content: "#";
            @apply text-gray-500 mr-2;
        }
    }
    ::v-deep(h2) {
        @apply text-xl mt-2 mb-2;
        &::before {
            content: "##";
            @apply text-gray-500 mr-2;
        }
    }
    ::v-deep(h3) {
        @apply text-lg mt-1 mb-1;
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
        @apply my-0 pt-3;
        thead th, tbody td {
            @apply p-[6px];
        }
    }
}
</style>