<script setup lang="ts">

import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import {h, onMounted, ref} from 'vue';
import axios from "axios";
import {
    API_PIC_CONFIG_ACCOUNT,
    API_PIC_CONFIG_PAT,
    API_PIC_IMAGE,
    API_PIC_USAGE,
} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import {
    BestImageLinkGenerator,
    GithubImageLinkGenerator,
    JsDelivrImageLinkGenerator,
    JSDMirrorImageLinkGenerator,
    RelayImageLinkGenerator
} from "@/utils/lib.pic";
import useClipboard from "vue-clipboard3";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";
import {message, Modal as AModal, Button as AButton} from "ant-design-vue";
import {convertToBase64} from "@/utils/handleImage";
import type {Image, Usage} from "@/types/pic";
import MorningstarFooter from "@/components/Footer.vue";

setTheme(getDefaultTheme());
useFavicon().value = '/pic.ico';
useHead({
    title: 'Pic - 开放图床',
    meta: [
        {
            name: 'description',
            content: '基于Github API开发的图床工具'
        }
    ],
})

const { toClipboard } = useClipboard();
const titleElement = h('div', { "class": 'text-2xl md:text-5xl font-bold' }, '开 放 图 床');
const pat = ref<string>();
const configModalOpen = ref(false);
const noticeModalOpen = ref(false);
const ownerName = ref<string>();
const images = ref<Image[]>();
const usage = ref<Usage>();

const showConfigModal = () => {
    configModalOpen.value = true;
};

const hideConfigModal = () => {
    configModalOpen.value = false;
};

const showNoticeModal = () => {
    noticeModalOpen.value = true;
}

const hideNoticeModal = () => {
    noticeModalOpen.value = false;
}

const fetchData = async () => {
    images.value = ((await axios.get(API_PIC_IMAGE, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    }))).data.data as Image[];
    console.log(images.value)
    for (let i=0; i < 20; i++){
        images.value.push(images.value[images.value.length-1])
    }
    images.value[0].filename += "1212121212.png";
}

const getUsage = async () => {
    usage.value = ((await axios.get(API_PIC_USAGE, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    }))).data.data as Usage;
}

const refreshing = ref(false);

const refreshData = async () => {
    if(refreshing.value === false){
        refreshing.value = true;
        await fetchData();
        refreshing.value = false;
    }
}

const githubImageLinkGenerator = new GithubImageLinkGenerator();
const jsDelivrImageLinkGenerator = new JsDelivrImageLinkGenerator();
const jSDMirrorImageLinkGenerator = new JSDMirrorImageLinkGenerator();
const relayImageLinkGenerator = new RelayImageLinkGenerator();
const bestImageLinkGenerator = new BestImageLinkGenerator();

const copyImageLink = async (imageLink: string) => {
    await toClipboard(imageLink);
    message.success({content: '链接复制成功...', class: 'ant-message-notice-custom', duration: 2});
}

const copyImageData = async (event: Event) => {
    const buttonElement = event.target as HTMLImageElement;
    let parentElement = buttonElement.parentElement as HTMLElement;
    while (parentElement) {
        if(parentElement.querySelector('img') !== null){
            break;
        }
        parentElement = parentElement.parentElement as HTMLElement;
    }
    const img = parentElement.querySelector('img') as HTMLImageElement;

    // 检查图片是否已经加载完成
    if(img.complete && img.naturalHeight !== 0){
        // 图片已加载，转换为base64并复制
        await toClipboard(convertToBase64(img));
        message.success({content: '数据复制成功...', class: 'ant-message-notice-custom', duration: 2});
    }else{
        // 图片未加载，添加事件监听器
        img.addEventListener('load', async () => {
            await toClipboard(convertToBase64(img));
            message.success({content: '数据复制成功...', class: 'ant-message-notice-custom', duration: 2});
        });
    }
}

const jumpToRepo = () => {
    message.success("即将跳转到图床仓库...", 1, ()=> {
        window.open(`https://github.com/${ownerName.value}/MorningstarPicRepo`, '_blank');
    });
}

onMounted(async ()=>{
    pat.value = (await axios.get(API_PIC_CONFIG_PAT, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data.data as string;
    if (pat.value === undefined) {
        configModalOpen.value = true;
    }else {
        ownerName.value = (await axios.get(API_PIC_CONFIG_ACCOUNT, {
            headers: {
                Authorization: localStorage.getItem(TOKEN)
            }
        })).data.data as string;
        getUsage().then(_ => {});
        fetchData().then(_ => {});
    }
})

</script>

<template>
    <div class="container mx-auto min-h-screen flex flex-col">
        <div class="flex items-center mt-4 justify-between mb-8 px-8">
            <component :is="titleElement"/>
            <div class="flex justify-center items-center space-x-4">
                <div>
                    <input id="file-upload" type="file" class="hidden" accept="image/*">
                    <label for="file-upload" class="cursor-pointer">
                        <font-awesome-icon :icon="['fas', 'upload']" class="text-2xl cursor-pointer" />
                    </label>
                </div>
                <font-awesome-icon :icon="['fas', 'rotate']" class="text-2xl cursor-pointer" @click="refreshData" :class="{ 'animate-spin': refreshing }"/>
                <font-awesome-icon :icon="['fas', 'bell']" class="text-2xl cursor-pointer" @click="showNoticeModal"/>
                <a-modal v-model:open="noticeModalOpen" @ok="hideNoticeModal">
                    <template #title>
                        <div class="text-2xl text-center">
                            本站通告
                        </div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideNoticeModal">了解</a-button>
                    </template>
                    <ul class="list-disc">
                        <li><span class="font-bold">性质说明：</span>本服务基于GitHub搭建，旨在为个人开发者、学生等预算有限的用户提供简单的图片托管支持。</li>
                        <li><span class="font-bold">隐私保障：</span>您的图片数据将直接存储于GitHub，我们仅保存必要的PAT授权信息用于服务对接，不会在服务器留存您的图片。</li>
                        <li><span class="font-bold">资源配额：</span>为了保证Github资源的合理使用，每个Github账号只享有1000天的新增权限，超过配额后仍可管理历史图片资源。</li>
                        <li><span class="font-bold">替代方案：</span>如果需要更稳定的图床，可参考以下免费/低成本服务：<a href="https://imgur.com/">Imgur</a>、<a href="https://www.qiniu.com/products/kodo">七牛云</a>。</li>
                    </ul>
                </a-modal>
                <div class="flex justify-center items-center cursor-pointer hover:animate-pulse" v-if="ownerName" @click="jumpToRepo">
                    <font-awesome-icon :icon="['fab', 'github']" class="text-2xl"/>
                </div>
                <font-awesome-icon :icon="['fas', 'gear']" @click="showConfigModal" class="text-2xl" />
                <a-modal v-model:open="configModalOpen" @ok="hideConfigModal">
                    <template #title>
                        <div class="text-2xl text-center">
                            配置菜单
                        </div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideConfigModal">关闭</a-button>
                    </template>
                    <div>
                        <div class="text-xl">PAT配置</div>
                        {{pat}}
                    </div>
                </a-modal>
            </div>
        </div>
        <div class="flex-1 border-2 rounded-2xl p-4 mb-4 flex flex-col mx-2">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 p-4 gap-y-8" v-if="ownerName && images && !refreshing">
                <template v-for="image in images">
                    <div class="flex justify-center items-center">
                        <div class="rounded-2xl item-shadow">
<!--                            <div class="w-64 h-48 overflow-hidden relative mb-2">-->
<!--                                <img class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"/>-->
<!--                            </div>-->
                            <a :href="`${bestImageLinkGenerator.generate(ownerName, image.path)}`" target="_blank">
                                <img :src="`${bestImageLinkGenerator.generate(ownerName, image.path)}`" :alt="image.path.split('/')[1]"
                                     class="w-[16rem] h-[12rem] object-cover mb-1 mx-auto rounded-2xl" crossorigin="anonymous"/>
                            </a>
                            <div class="overflow-x-scroll whitespace-nowrap w-[16rem] pb-1 text-center px-2 text-blue-400">
                                {{image.filename.slice(10)}}
                            </div>
                            <div class="text-center mb-1 px-2">
                                {{image.updateTime}}
                            </div>
                            <div class="flex justify-center items-center space-x-4 mb-2">
                                <details class="dropdown">
                                    <summary class="btn btn-outline btn-primary btn-sm">
                                        复制链接
                                    </summary>
                                    <ul class="menu dropdown-content p-0 bg-base-100 rounded-box z-[1] w-[5.2rem] shadow mt-1">
                                        <template v-for="imageLinkGenerator in [ githubImageLinkGenerator, jsDelivrImageLinkGenerator, jSDMirrorImageLinkGenerator, relayImageLinkGenerator]" >
                                            <li @click="copyImageLink(imageLinkGenerator.generate(ownerName, image.path))"
                                                class="py-1 text-sm text-center hover:bg-gray-300 dark:hover:bg-gray-700 rounded-lg w-full cursor-pointer">
                                                {{imageLinkGenerator.sourceName}}
                                            </li>
                                        </template>
                                    </ul>
                                </details>
                                <button class="btn btn-outline btn-accent btn-sm" @click="copyImageData">复制内容</button>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
            <div v-else class="flex justify-center items-center h-full flex-1">
                <span class="loading loading-ring w-24 h-24"></span>
            </div>
        </div>
        <morningstar-footer/>
    </div>
</template>

<style scoped lang="scss">
.item-shadow {
    &:hover {
        box-shadow:  10px 10px 20px #bebebe,
        -10px -10px 20px #ffffff;
    }
}

[data-theme="dark"] .item-shadow {
    &:hover {
        box-shadow: 10px 10px 20px #191e24, -10px -10px 20px #212830;
    }
}
</style>