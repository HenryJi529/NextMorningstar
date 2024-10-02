<script setup lang="ts">

import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import {h, onMounted, ref} from 'vue';
import axios from "axios";
import dayjs from 'dayjs';
import {
    API_PIC_CONFIG_COMPRESSION_QUALITY,
    API_PIC_CONFIG_GITHUB_ACCOUNT,
    API_PIC_CONFIG_GITHUB_PAT, API_PIC_CONFIG_SECRET_KEY,
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
import {useUserStore} from "@/stores/users";
import useClipboard from "vue-clipboard3";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";
import {message, Modal as AModal, Button as AButton, Slider as ASlider, Switch as ASwitch} from "ant-design-vue";
import {convertToBase64} from "@/utils/handleImage";
import type {Image, Usage} from "@/types/pic";
import MorningstarFooter from "@/components/Footer.vue";
import mWebLogoImage from '@/assets/img/mweb-logo.png';
import mWebConfigDetailImage from '@/assets/img/mweb-config-detail.png';
import mWebConfigResultImage from '@/assets/img/mweb-config-result.png';
import {roundToDecimal} from "@/utils/handleMath";
import {useRouter} from "vue-router";
import type {PageResult, R} from "@/types/common";

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

const router = useRouter();
const userStore = useUserStore();
const { toClipboard } = useClipboard();
const titleElement = h('div', { "class": 'text-2xl md:text-5xl font-bold text-center' }, '开 放 图 床');
const pat = ref<string>();
const hasValidPat = ref(false);
const configModalOpen = ref(true);
const noticeModalOpen = ref(false);
const uploadModalOpen = ref(false);
const mWebModalOpen = ref(false);
const ownerName = ref<string>();
const secretKey = ref<string>();
const images = ref<Image[]>();
const usage = ref<Usage>();
const isPatSetting = ref(false);
const isPatClearing = ref(false);
const isCompressionQualitySetting = ref(false);
const isImagesFetching = ref(false);
const deletingImages = ref<string[]>([]);
const compressionQuality = ref<number>();
const useCompress = ref<boolean>();
const pageNum = ref(1);
const pageSize = ref(10);
const dateFormat = 'YYYY-MM-DD';
const startDate = ref('2025-01-01');
const endDate = ref(dayjs().format(dateFormat));

const usageWarningThreshold: Usage = {
    usedStorage: 1024 * 1024 * 1024,
    imageCount: 10000,
    dayCount: 900,
}

const showConfigModal = () => {
    configModalOpen.value = true;
};

const hideConfigModal = () => {
    if(hasValidPat.value){
        configModalOpen.value = false;
    }else{
        message.error({content: '你还没配置好Github PAT哦~', class: 'ant-message-notice-custom', duration: 2});
    }
};

const showNoticeModal = () => {
    noticeModalOpen.value = true;
}

const hideNoticeModal = () => {
    noticeModalOpen.value = false;
}

const showMWebModal = () => {
    mWebModalOpen.value = true;
}

const hideMWebModal = () => {
    mWebModalOpen.value = false;
}

const showUploadModal = () => {
    uploadModalOpen.value = true;
}

const hideUploadModal = () => {
    uploadModalOpen.value = false;
}

const deleteImage = async (path: string) => {
    if(images.value === undefined){
        return;
    }
    deletingImages.value.push(path);
    const params = new URLSearchParams();
    params.append('path', path);
    const response = (await axios.delete(`${API_PIC_IMAGE}?${params.toString()}`,
        {
            headers: {
                Authorization: localStorage.getItem(TOKEN)
            }
        }
    )).data;
    if(response.code <= 0){
        message.error({content: response.msg, class: 'ant-message-notice-custom', duration: 2});
    }else{
        message.success({content: `${path}删除成功`, class: 'ant-message-notice-custom', duration: 2})
        images.value = images.value.filter(image => image.path !== path);
        fetchImages(true).then(_ => {});
    }
    deletingImages.value = deletingImages.value.filter(item => item !== path);
}

const fetchImages = async (quiet: boolean = false) => {
    if(!quiet){
        isImagesFetching.value = true;
    }
    const params = new URLSearchParams();
    params.append('pageNum', pageNum.value.toString());
    params.append('pageSize', pageSize.value.toString());
    params.append('startDate', startDate.value);
    params.append('endDate', endDate.value);
    const response: R<PageResult<Image>> = ((await axios.get(`${API_PIC_IMAGE}?${params}`,{
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    }))).data;
    if(response.code <= 0){
        message.error(
            {
                content: response.msg,
                class: 'ant-message-notice-custom',
                duration: 2,
                onClose: () => {
                    location.reload();
                }
            }
        );
        return;
    }
    images.value = response.data.records;
    if(!quiet){
        isImagesFetching.value = false;
    }
}

const getUsage = async () => {
    usage.value = ((await axios.get(API_PIC_USAGE, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    }))).data.data as Usage;
    if (usage.value.usedStorage >= usageWarningThreshold.usedStorage || usage.value.imageCount >= usageWarningThreshold.imageCount || usage.value.dayCount >= usageWarningThreshold.dayCount) {
        message.warning({content: '你薅羊毛薅的有点多了呀~', class: 'ant-message-notice-custom', duration: 10});
    }
}

const getOwnerName = async () => {
    ownerName.value = (await axios.get(API_PIC_CONFIG_GITHUB_ACCOUNT, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data.data as string;
}

const getSecretKey = async () => {
    secretKey.value = (await axios.get(API_PIC_CONFIG_SECRET_KEY, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data.data as string;
}

const getCompressionQuality = async () => {
    const response = (await axios.get(API_PIC_CONFIG_COMPRESSION_QUALITY, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data.data;
    if(response === ""){
        useCompress.value = false;
    }else{
        useCompress.value = true;
        compressionQuality.value = parseFloat(response);
    }
}

const showCompressionNotification = () => {
    message.info({content: '压缩可以有效缩短图片上传和加载时间，强烈建议开启...', class: 'ant-message-notice-custom', duration: 2});
}

const setCompressionQuality = async () => {
    if(compressionQuality.value === undefined){
        return;
    }
    isCompressionQualitySetting.value = true;
    const params = new URLSearchParams();
    if(useCompress.value){
        params.append('compressionQuality', compressionQuality.value.toString());
    }else{
        params.append('compressionQuality', "1.0");
    }
    await axios.post(API_PIC_CONFIG_COMPRESSION_QUALITY, params, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    });
    message.success({content: '压缩配置更新成功...', class: 'ant-message-notice-custom', duration: 2});
    isCompressionQualitySetting.value = false;
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

const setGithubPat = async () => {
    if(isPatSetting.value){
        return;
    }
    isPatSetting.value = true;
    const response = (await axios.post(API_PIC_CONFIG_GITHUB_PAT, {pat: pat.value}, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data;
    isPatSetting.value = false;
    if(response.code <= 0){
        message.error({content: 'Github PAT配置错误，请重试...', class: 'ant-message-notice-custom', duration: 2});
    }else{
        hasValidPat.value = true;
        message.success({content: 'Github PAT配置成功...', class: 'ant-message-notice-custom', duration: 2});
        getOwnerName().then(_ => {})
        fetchImages().then(_ => {});
    }
}

const clearGithubPat = async () => {
    if(isPatClearing.value){
        return;
    }
    isPatClearing.value = true;
    const response = (await axios.delete(API_PIC_CONFIG_GITHUB_PAT, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data;
    if(response.code <= 0){
        message.error({content: 'Github PAT清除失败，请重试...', class: 'ant-message-notice-custom', duration: 2});
    }else{
        pat.value = "";
        hasValidPat.value = false;
        message.success({
            content: 'Github PAT清除成功...',
            class: 'ant-message-notice-custom',
            duration: 2,
            onClose: () => {
                location.reload();
            }
        });
    }
    isPatClearing.value = false;
}

const jumpToRepo = () => {
    message.success({
        content: "即将跳转到图床仓库...",
        class: 'ant-message-notice-custom',
        duration: 1,
        onClose: ()=> {
            window.open(`https://github.com/${ownerName.value}/MorningstarPicRepo`, '_blank');
        }}
    );
}

const jumpToGithubPatDoc = () => {
    message.success({
        content: "即将跳转到GithubPat参考文档...",
        class: 'ant-message-notice-custom',
        duration: 1,
        onClose: ()=> {
            window.open(`https://docs.github.com/zh/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens`, '_blank');
        }}
    );
}

const logout = async () => {
    await userStore.handleLogout();
    location.reload()
}

onMounted(async ()=>{
    pat.value = (await axios.get(API_PIC_CONFIG_GITHUB_PAT, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data.data as string;
    if (pat.value === undefined) {
        configModalOpen.value = true;
    }else {
        hasValidPat.value = true;
        getOwnerName().then(_ => {})
        getSecretKey().then(_ => {})
        getCompressionQuality().then(_ => {})
        getUsage().then(_ => {});
        fetchImages().then(_ => {});
    }
})

</script>

<template>
    <div class="container mx-auto min-h-screen flex flex-col">
        <div class="md:flex items-center mt-4 justify-between mb-4 md:mb-8 px-8">
            <component :is="titleElement"/>
            <div v-if="usage" class="hidden md:flex justify-center items-center space-x-8 text-xl">
                <div>已使用天数:
                    <span :class="{'text-red-600': usage.dayCount >= usageWarningThreshold.dayCount, 'dark:text-red-400': usage.dayCount >= usageWarningThreshold.dayCount}">
                        {{usage.dayCount}}
                    </span>/1000
                </div>
                <div :class="{'text-red-600': usage.usedStorage >= usageWarningThreshold.usedStorage, 'dark:text-red-400': usage.usedStorage >= usageWarningThreshold.usedStorage}">已使用空间:
                    <span>
                        {{roundToDecimal(usage.usedStorage/1024/1024, 2)}}
                    </span>
                    MB
                </div>
                <div>当前图片数:
                    <span :class="{'text-red-600': usage.imageCount >= usageWarningThreshold.imageCount, 'dark:text-red-400': usage.imageCount >= usageWarningThreshold.imageCount}">
                        {{usage.imageCount}}
                    </span>
                </div>
            </div>
            <div class="flex justify-center items-center space-x-4">
                <font-awesome-icon :icon="['fas', 'upload']" class="text-2xl cursor-pointer" v-if="ownerName" @click="showUploadModal"/>
                <a-modal v-model:open="uploadModalOpen" @ok="hideUploadModal" :closable="false">
                    <template #title>
                        <div class="text-2xl text-center">
                            图片上传
                        </div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideUploadModal">关闭</a-button>
                    </template>
                    <input id="file-upload" type="file" class="hidden" accept="image/*">
                    <label for="file-upload" class="cursor-pointer flex justify-center items-center border-dashed border-4 rounded-2xl w-[10rem] h-[10rem] mx-auto">
                        <div>
                            <font-awesome-icon :icon="['fas', 'plus']" class="text-5xl" />
                            <div class="text-center text-xl mt-2">上传</div>
                        </div>
                    </label>
                </a-modal>
                <div class="flex justify-center items-center cursor-pointer hover:animate-pulse" v-if="ownerName" @click="jumpToRepo">
                    <font-awesome-icon :icon="['fab', 'github']" class="text-2xl"/>
                </div>
                <img :src="mWebLogoImage" alt="MWeb Logo" class="w-8 cursor-pointer" @click="showMWebModal"/>
                <a-modal v-model:open="mWebModalOpen" @ok="hideMWebModal" :width="540" :centered="true" :closable="false">
                    <template #title>
                        <div class="text-2xl text-center">
                            MWeb图床配置
                        </div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideMWebModal">了解</a-button>
                    </template>
                    <p class="indent-8">
                        请先参考官方文档<a href="https://zh.mweb.im/how_to_use_custom_image_upload_url" target="_blank" class="text-green-600 dark:text-green-400">《iOS版MWeb图床功能中自定义图床的使用指南》</a>，
                        本服务的具体配置如下：
                    </p>
                    <div class="flex justify-center items-center">
                        <img :src="mWebConfigDetailImage" alt="MWeb图床配置细节" class="w-[26rem] rounded-3xl"/>
                    </div>
                    <ul class="list-disc pl-12 mb-2">
                        <li><span class="font-bold mr-2">API URL:</span>https://morningstar369.com/api/pic/resource/mweb</li>
                        <li><span class="font-bold mr-2">POST File Name:</span>file</li>
                        <li><span class="font-bold mr-2">Response URL Path:</span>data/url</li>
                        <li><span class="font-bold mr-2">secretKey:</span>{{secretKey}}</li>
                        <li><span class="font-bold mr-2">compressionQuality:</span>0.8【举🌰】</li>
                        <li><span class="font-bold mr-2">linkType:</span>Github / JSDMirror / JsDelivr(默认) / Relay【区分大小写】</li>
                    </ul>
                    <p class="indent-8">
                        配置完成后，点击"Validate"按钮。验证通过时，会显示类似下图的结果。
                    </p>
                    <div class="flex justify-center items-center">
                        <img :src="mWebConfigResultImage"  alt="MWeb图床配置结果" class="w-[10rem]"/>
                    </div>
                </a-modal>
                <font-awesome-icon :icon="['fas', 'bell']" class="text-2xl cursor-pointer" @click="showNoticeModal"/>
                <a-modal v-model:open="noticeModalOpen" @ok="hideNoticeModal" :closable="false">
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
                        <li><span class="font-bold">替代方案：</span>如果需要更稳定的图床，可参考以下免费/低成本服务：<a href="https://imgur.com/" class="text-green-600 dark:text-green-400" target="_blank">Imgur</a>、<a href="https://www.qiniu.com/products/kodo"  class="text-green-600 dark:text-green-400" target="_blank">七牛云</a>。</li>
                    </ul>
                </a-modal>
                <font-awesome-icon :icon="['fas', 'gear']" @click="showConfigModal" class="text-2xl cursor-pointer" />
                <a-modal v-model:open="configModalOpen" @ok="hideConfigModal" :keyboard="false" :maskClosable="false" :closable="false">
                    <template #title>
                        <div class="text-2xl text-center">
                            配置菜单
                        </div>
                    </template>
                    <template #footer>
                        <a-button key="submit" type="primary" @click="hideConfigModal">关闭</a-button>
                    </template>
                    <div>
                        <div class="text-lg mb-2">
                            Github PAT<font-awesome-icon :icon="['fas', 'circle-question']" class="cursor-pointer" @click="jumpToGithubPatDoc" />
                        </div>
                        <input type="text" v-model="pat" class="w-full p-1 rounded-xl pl-4 bg-transparent mb-2" placeholder="请输入您的Github PAT...">
                        <div class="flex justify-center items-center space-x-8">
                            <button class="px-4 py-1 rounded-lg bg-green-400 dark:bg-green-700 flex justify-center items-center" @click="setGithubPat">
                                <span class="loading loading-spinner" v-if="isPatSetting"></span>
                                <span v-else>设置</span>
                            </button>
                            <button class="px-4 py-1 rounded-lg bg-red-400 dark:bg-red-800 flex justify-center items-center" @click="clearGithubPat">
                                <span class="loading loading-spinner" v-if="isPatClearing"></span>
                                <span v-else>清除</span>
                            </button>
                        </div>
                    </div>
                    <div class="divider"></div>
                    <div>
                        <div class="text-lg mb-2">
                            压缩配置<font-awesome-icon :icon="['fas', 'circle-question']" class="cursor-pointer" @click="showCompressionNotification"/>
                        </div>
                        <div class="flex justify-center items-center space-x-8 px-4 mb-2">
                            <a-switch v-model:checked="useCompress" checked-children="开" un-checked-children="关" />
                            <div class="flex-1 flex justify-center items-center">
                                <span class="mr-4">质量: </span>
                                <a-slider v-model:value="compressionQuality" :min="0.1" :max="0.9" :step="0.01" :disabled="!useCompress" class="flex-1"/>
                            </div>
                        </div>
                        <div class="flex justify-center items-center">
                            <button class="px-16 py-1 rounded-lg bg-green-400 dark:bg-green-700 flex justify-center items-center" @click="setCompressionQuality">
                                <span class="loading loading-spinner" v-if="isCompressionQualitySetting"></span>
                                <span v-else>设置</span>
                            </button>
                        </div>
                    </div>
                </a-modal>
                <div title="用户">
                    <div v-if="userStore.id" class="flex justify-center items-center">
                        <details class="dropdown dropdown-bottom dropdown-end">
                            <summary class="list-none w-10 h-10 cursor-pointer">
                                <img :src="userStore.avatarLink" alt="头像" class="rounded-full w-full h-full" />
                            </summary>
                            <ul class="dropdown-content mt-1 rounded-box shadow flex flex-col text-lg">
                                <li class="nav-item" v-if="userStore.isAuthenticated">
                                    <div class="cursor-pointer w-full text-center" @click="router.push({name: 'auth-profile'})">档案</div>
                                </li>
                                <li class="nav-item">
                                    <div class="cursor-pointer w-full text-center" @click="logout">登出</div>
                                </li>
                            </ul>
                        </details>
                    </div>
                    <div v-else>
                        <font-awesome-icon :icon="['fas', 'user']" class="cursor-pointer text-2xl"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="flex justify-center items-center">
            <div>
                <span>开始日期: </span>
                <input type="date" class="bg-transparent" v-model="startDate">
            </div>
            <div>
                <span>结束日期: </span>
                <input type="date" class="bg-transparent" v-model="endDate">
            </div>
        </div>
        <div class="flex-1 border-2 rounded-2xl p-4 mb-4 flex flex-col mx-2">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 p-4 gap-y-8" v-if="ownerName && images">
                <template v-for="image in images">
                    <div class="flex justify-center items-center">
                        <div class="rounded-2xl item-shadow">
<!--                            <div class="w-64 h-48 overflow-hidden relative mb-2"><img class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2"/></div>-->
                            <div :data-url="`${bestImageLinkGenerator.generate(ownerName, image.path)}`" class="relative">
                                <div class="absolute top-2 right-2 z-10">
                                    <font-awesome-icon v-if="!deletingImages.includes(image.path)" :icon="['fas', 'trash']" class="cursor-pointer text-xl text-blue-400 hover:text-red-500" @click="deleteImage(image.path)"/>
                                    <span v-else class="loading loading-spinner loading-md text-gray-700"></span>
                                </div>
                                <img :src="`${bestImageLinkGenerator.generate(ownerName, image.path)}`" :alt="image.path.split('/')[1]"
                                     class="w-[16rem] h-[12rem] object-cover mb-1 mx-auto rounded-2xl" crossorigin="anonymous"/>
                            </div>
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
.nav-item {
    @apply hover:bg-gray-300 dark:hover:bg-gray-700 rounded-sm lg:rounded-xl w-[3.5rem] p-1;
}
</style>