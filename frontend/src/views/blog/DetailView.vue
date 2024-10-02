<script setup lang="ts">
import {useRoute} from "vue-router";
import type {PageResult, R} from "@/types/common";
import type {ArticleDetail, CommentDetail} from "@/types/blog";
import axios from "axios";
import {API_BLOG_ARTICLE_COMMENT, API_BLOG_ARTICLE_DETAIL} from "@/constants/ApiConstant";
import {onMounted, ref, watch} from "vue";
import ArticleSection from "@/components/blog/ArticleSection.vue";
import CommentSection from "@/components/blog/CommentSection.vue";
import MainBody from "@/components/blog/MainBody.vue";
import TocSection from "@/components/blog/TocSection.vue";
import {getMarked, getTocRender, TocHandler, type TocTreeNode, removeLineBreakInBlockFormulas} from "@/utils/handleMarked";

const route = useRoute();

const articleDetail = ref<ArticleDetail>();
const comments = ref<CommentDetail[]>();
const articleNotExist = ref<boolean>(false);
const articleBody = ref<string>();
const commentBodyArray = ref<string[]>([]);
const tocTree = ref<TocTreeNode[]>();


const loadArticle = async () => {
    const marked = getMarked();
    const tocHandler = new TocHandler();
    const tocRender = getTocRender(tocHandler);
    marked.use({ renderer: tocRender });

    const id = route.params.id as string;
    const response: R<ArticleDetail> = (await axios.get(API_BLOG_ARTICLE_DETAIL.replace('{id}', id))).data;
    if(response.code != 1){
        articleNotExist.value = true;
        return;
    }

    articleDetail.value = response.data;
    articleBody.value = await marked.parse(removeLineBreakInBlockFormulas(articleDetail.value.article.content));
    tocTree.value = tocHandler.getTree();
}


const loadComments = async () => {
    const marked = getMarked();

    const id = route.params.id as string;
    const url = `${API_BLOG_ARTICLE_COMMENT.replace('{id}', id)}?pageNum=1&pageSize=99999`;
    const response: R<PageResult<CommentDetail>> = (await axios.get(url)).data;
    comments.value = response.data.records;
    commentBodyArray.value = [];
    for(let i=0; i < comments.value.length; i++){
        commentBodyArray.value.push(await marked.parse(removeLineBreakInBlockFormulas(comments.value[i].content)));
    }
}


const loadStuff = ()=>{
    loadArticle();
    loadComments();
}

const updateComments = async () => {
    console.log("更新评论列表");
    await loadComments();
}


watch(()=>route.params.id, loadStuff);


onMounted(()=>{
    loadStuff();
})

</script>

<template>
    <main-body>
        <template #custom-main-part>
            <div>
                <div v-if="articleNotExist" class="h-[30rem] flex justify-center items-center">
                    <div class="text-3xl">
                        文章不存在
                    </div>
                </div>
                <div v-else class="p-6">
                    <div v-if="articleDetail && articleBody">
                        <article-section :article-detail="articleDetail" :article-body="articleBody"/>
                    </div>
                    <div class="divider"></div>
                    <div v-if="comments && commentBodyArray.length == comments.length && articleDetail">
                        <comment-section @update="updateComments" :comments="comments" :comment-body-array="commentBodyArray" :article-id="articleDetail.article.id"/>
                    </div>
                </div>
            </div>
        </template>
        <template #custom-side-bar>
            <div v-if="tocTree && tocTree.length > 0" class="article-toc">
                <div class="text-3xl lg:text-xl pb-2 text-center lg:text-left">目录</div>
                <toc-section :toc-tree="tocTree" class="text-xl lg:text-base"/>
                <div class="divider"></div>
            </div>
        </template>
    </main-body>
</template>

<style scoped lang="scss">

</style>