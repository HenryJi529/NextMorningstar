<script setup lang="ts">
import {useRoute} from "vue-router";
import html2pdf from 'html2pdf.js';
import useClipboard from "vue-clipboard3";
const { toClipboard } = useClipboard();
import type {PageResult, R} from "@/types/common";
import type {ArticleDetail, CommentDetail} from "@/types/blog";
import axios from "axios";
import {API_BLOG_ARTICLE_COMMENT, API_BLOG_ARTICLE_DETAIL, API_COMMON_QRCODE} from "@/constants/ApiConstant";
import {onMounted, ref, watch} from "vue";
import ArticleSection from "@/components/blog/ArticleSection.vue";
import CommentSection from "@/components/blog/CommentSection.vue";
import CommonGuestArea from "@/components/blog/CommonGuestArea.vue";
import TocSection from "@/components/blog/TocSection.vue";
import {getMarked, getTocRender, TocHandler, type TocTreeNode, removeLineBreakInBlockFormulas} from "@/utils/handleMarked";
import {hasAnyPermission} from "@/utils/handlePermission";
import {BLOG_ARTICLE_MANAGE} from "@/constants/PermissionConstant";
import AlertSuccess from "@/components/common/AlertSuccess.vue";
import {VuePrintNext} from "vue-print-next";

const route = useRoute();

const articleDetail = ref<ArticleDetail>();
const comments = ref<CommentDetail[]>();
const articleNotExist = ref<boolean>(false);
const articleBody = ref<string>();
const tocTree = ref<TocTreeNode[]>();
const sharedByLink = ref(false);
const sharedByQrcode = ref(false);
const articleQrcode = ref("");

const marked = getMarked();
const tocHandler = new TocHandler();
const tocRender = getTocRender(tocHandler);
marked.use({ renderer: tocRender });


const loadArticle = async () => {
    const id = route.params.id as string;
    const response: R<ArticleDetail> = (await axios.get(API_BLOG_ARTICLE_DETAIL.replace('{id}', id))).data;
    if(response.code <= 0){
        articleNotExist.value = true;
        return;
    }

    articleDetail.value = response.data;
    articleBody.value = await marked.parse(removeLineBreakInBlockFormulas(articleDetail.value.article.content));
    tocTree.value = tocHandler.getTree();
}


const loadComments = async () => {
    const id = route.params.id as string;
    const url = `${API_BLOG_ARTICLE_COMMENT.replace('{id}', id)}?pageNum=1&pageSize=99999`;
    const response: R<PageResult<CommentDetail>> = (await axios.get(url)).data;
    comments.value = response.data.records;
}


const loadStuff = ()=>{
    loadArticle();
    loadComments();
}


const updateComments = async () => {
    console.log("更新评论列表");
    await loadComments();
}


const shareByLink = async () => {
    try {
        await toClipboard(location.href);
        sharedByLink.value = true;
        setTimeout(()=>{
            sharedByLink.value = false;
        }, 1500);
    } catch (e) {
        console.error(e)
    }
}


const shareByQrcode = async () => {
    if(articleQrcode.value == ""){
        const response: R<string> = (await axios.get(`${API_COMMON_QRCODE}?data=${location.href}`)).data;
        if(response.code <= 0){
            return;
        }
        articleQrcode.value = response.data;
    }
    sharedByQrcode.value = true;
    setTimeout(()=>{
        sharedByQrcode.value = false;
    }, 6000);
}


const downloadPDF = async () => {
    const element = (document.querySelector('#article-section') as HTMLElement).cloneNode(true) as HTMLElement;
    element.dataset.theme = "light";
    html2pdf()
        .set({
            margin: 10,
            filename: `${articleDetail.value?.article.title}.pdf`,
            enableLinks: true,
            // @ts-ignore
            pagebreak: {
                mode: ['css', 'legacy'],
                avoid: ["table"]
            },
            image:        { type: 'jpeg', quality: 0.98 },
            html2canvas: {
                scale: 3, // 分辨率
                logging: false,
                useCORS: true
            },
            jsPDF: {
                unit: 'mm',
                format: 'a4',
                orientation: 'portrait'
            }
        })
        .from(element)
        .save()
        .then();
}


const printPDF = () => {
    new VuePrintNext({el: '#article-section'});
}


watch(()=>route.params.id, loadStuff);


onMounted(()=>{
    loadStuff();
})

</script>

<template>
    <common-guest-area>
        <template #custom-main-part>
            <div>
                <div v-if="articleNotExist" class="h-[30rem] flex justify-center items-center">
                    <div class="text-3xl">
                        文章不存在
                    </div>
                </div>
                <div v-else class="p-6">
                    <div v-if="articleDetail && articleBody" class="relative">
                        <div v-if="hasAnyPermission([BLOG_ARTICLE_MANAGE])" class="absolute right-0 text-2xl">
                            <router-link :to="{name: 'blog-manage-article-edit', params: {id: articleDetail.article.id}}">
                                <font-awesome-icon :icon="['fas', 'pen-to-square']" />
                            </router-link>
                        </div>
                        <article-section id="article-section" :article-detail="articleDetail" :article-body="articleBody"/>
                        <div class="flex justify-center space-x-20 my-6">
                            <div class="btn h-[5rem] w-[5rem] rounded-full text-4xl" @click="shareByLink">
                                <font-awesome-icon :icon="['fas', 'link']"/>
                            </div>
                            <div class="btn h-[5rem] w-[5rem] rounded-full text-4xl" @click="shareByQrcode">
                                <font-awesome-icon :icon="['fas', 'qrcode']" />
                            </div>
                            <div class="btn h-[5rem] w-[5rem] rounded-full text-4xl" @click="downloadPDF">
                                <font-awesome-icon :icon="['fas', 'circle-down']" />
                            </div>
                            <div class="btn h-[5rem] w-[5rem] rounded-full text-4xl" @click="printPDF">
                                <font-awesome-icon :icon="['fas', 'print']" />
                            </div>
                        </div>
                        <div v-if="sharedByLink">
                            <alert-success>
                                已复制到剪贴板
                            </alert-success>
                        </div>
                        <div v-if="sharedByQrcode" class="flex justify-center items-center">
                            <img :src="articleQrcode" alt="文章二维码" class="w-[10rem]">
                        </div>
                    </div>
                    <div class="divider"></div>
                    <div v-if="comments && articleDetail">
                        <comment-section @update="updateComments" :comments="comments" :article-id="articleDetail.article.id"/>
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
    </common-guest-area>
</template>

<style scoped lang="scss">

</style>