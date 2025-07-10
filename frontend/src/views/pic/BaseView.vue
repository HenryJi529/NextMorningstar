<script setup lang="ts">

import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import {h, onMounted, ref} from 'vue';
import axios from "axios";
import {API_PIC_CONFIG_ACCOUNT, API_PIC_CONFIG_PAT} from "@/constants/ApiConstant";
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
import {message} from "ant-design-vue";
import {convertToBase64} from "@/utils/handleImage";

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
const titleElement = h('div', { "class": 'text-5xl font-bold' }, '开 放 图 床');
const pat = ref<string>();
const configMenuOpen = ref(false);
const ownerName = ref<string>();
const showConfigMenu = () => {
    configMenuOpen.value = true;
};

const hideConfigMenu = () => {
    configMenuOpen.value = false;
};

onMounted(async ()=>{
    pat.value = (await axios.get(API_PIC_CONFIG_PAT, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data.data as string;
    if (pat.value === undefined) {
        configMenuOpen.value = true;
    }else {
        ownerName.value = (await axios.get(API_PIC_CONFIG_ACCOUNT, {
            headers: {
                Authorization: localStorage.getItem(TOKEN)
            }
        })).data.data as string;
        console.log(ownerName.value);
    }
})


const filePaths = [
    "20250707/162008750-iShot_2025-07-07_16.18.57.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
]

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
    }else{
        // 图片未加载，添加事件监听器
        img.addEventListener('load', async () => {
            await toClipboard(convertToBase64(img));
        });
    }
    message.success({content: '数据复制成功...', class: 'ant-message-notice-custom', duration: 2});
}


</script>

<template>
    <div class="container mx-auto">
        <div class="flex items-center mt-4 justify-between mb-4 px-8">
            <component :is="titleElement"/>
            <div class="flex justify-center items-center space-x-4">
                <a :href="`https://github.com/${ownerName}/MorningstarPicRepo`" class="flex justify-center items-center">
                    <font-awesome-icon :icon="['fab', 'github']" class="text-2xl"/>
                </a>
                <font-awesome-icon :icon="['fas', 'gear']" @click="showConfigMenu" class="text-2xl" />
                <div class="modal" :class="{ 'modal-open': configMenuOpen }">
                    <div class="modal-box">
                        <div class="text-2xl">配置菜单</div>
                        <div>
                            <div class="text-xl">PAT配置</div>
                            <div>
                                {{pat}}
                            </div>
                        </div>
                        <div class="modal-action">
                            <button class="btn" @click="hideConfigMenu">
                                关闭
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-4" v-if="ownerName">
            <template v-for="filePath in filePaths">
                <div class="flex justify-center items-center">
                    <div class="w-[20rem] p-4">
                        <img :src="`${bestImageLinkGenerator.generate(ownerName, filePath)}`" :alt="filePath.split('/')[1]" class="h-48"/>
                        <div class="flex justify-center items-center space-x-4">
                            <details class="dropdown">
                                <summary class="btn btn-outline btn-primary">
                                    复制链接
                                </summary>
                                <ul class="menu dropdown-content p-0 bg-base-100 rounded-box z-[1] w-[5.7rem] shadow">
                                    <template v-for="imageLinkGenerator in [
                                    githubImageLinkGenerator,
                                    jsDelivrImageLinkGenerator,
                                    jSDMirrorImageLinkGenerator,
                                    relayImageLinkGenerator
                                    ]">
                                        <li @click="copyImageLink(imageLinkGenerator.generate(ownerName, filePath))"
                                            class="py-2 indent-3 hover:bg-gray-300 dark:hover:bg-gray-700 rounded-lg w-full cursor-pointer">
                                            {{imageLinkGenerator.sourceName}}
                                        </li>
                                    </template>
                                </ul>
                            </details>
                            <button class="btn btn-outline btn-accent" @click="copyImageData">复制内容</button>
                        </div>
                    </div>
                </div>
            </template>
        </div>
    </div>
</template>

<style scoped lang="scss">
</style>