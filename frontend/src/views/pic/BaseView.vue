<script setup lang="ts">

import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import {h, onMounted, ref} from 'vue';
import {SettingOutlined, GithubOutlined} from "@ant-design/icons-vue";
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
import {message} from "ant-design-vue";
import {useUserStore} from "@/stores/users";
import {useRouter} from "vue-router";


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
const titleElement = h('div', { "class": 'text-5xl' }, '开 放 图 床');
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

const githubImageLinkGenerator = new GithubImageLinkGenerator();
const jsDelivrImageLinkGenerator = new JsDelivrImageLinkGenerator();
const jSDMirrorImageLinkGenerator = new JSDMirrorImageLinkGenerator();
const relayImageLinkGenerator = new RelayImageLinkGenerator();
const bestImageLinkGenerator = new BestImageLinkGenerator();

const filePaths = [
    "20250707/162008750-iShot_2025-07-07_16.18.57.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
    "20250707/105123873-iShot_2025-07-06_18.31.07.png",
]

const copyImageLink = async (event: {key: string}) => {
    await toClipboard(event.key);
    message.success('链接复制成功...');
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
    const imgElement = parentElement.querySelector('img') as HTMLImageElement;

    console.log(imgElement);
}


</script>

<template>
    <div class="container mx-auto">
        <div class="flex items-center mt-4 justify-between mb-4 px-8">
            <component :is="titleElement"/>
            <div class="flex justify-center items-center space-x-4">
                <a :href="`https://github.com/${ownerName}/MorningstarPicRepo`" class="flex justify-center items-center">
                    <GithubOutlined class="text-2xl"/>
                </a>
                <SettingOutlined @click="showConfigMenu" class="text-2xl" />
                <a-modal v-model:open="configMenuOpen" title="配置菜单" @ok="hideConfigMenu">
                    <div>
                        <div>PAT配置</div>
                        <a-typography-paragraph v-model:content="pat" editable :copyable="{ tooltip: false }"/>
                    </div>
                </a-modal>
                <div title="用户">
                    <div v-if="userStore.id" class="flex justify-center items-center">
                        <details class="dropdown dropdown-bottom dropdown-end">
                            <summary class="list-none w-10 h-10 cursor-pointer">
                                <img :src="userStore.avatarLink" alt="头像" class="rounded-full w-full h-full" />
                            </summary>
                            <ul class="nav-content">
                                <li class="nav-item" v-if="userStore.isAuthenticated">
                                    <div class="cursor-pointer" @click="router.push({name: 'auth-profile'})">档案</div>
                                </li>
                                <li class="nav-item">
                                    <div class="cursor-pointer" @click="userStore.handleLogout()">登出</div>
                                </li>
                            </ul>
                        </details>
                    </div>
                    <div v-else>
                        <details class="dropdown dropdown-bottom dropdown-end">
                            <summary class="list-none">
                                <font-awesome-icon :icon="['fas', 'user']" class="cursor-pointer"/>
                            </summary>
                            <ul class="nav-content">
                                <li class="nav-item">
                                    <div class="cursor-pointer" @click="router.push({name: 'auth-login'})">登入</div>
                                </li>
                                <li class="nav-item">
                                    <div class="cursor-pointer" @click="router.push({name: 'auth-register'})">注册</div>
                                </li>
                            </ul>
                        </details>
                    </div>
                </div>
            </div>
        </div>

        <div class="flex flex-wrap" v-if="ownerName">
            <template v-for="filePath in filePaths">
                <div class="w-64">
                    <img :src="`${bestImageLinkGenerator.generate(ownerName, filePath)}`" :alt="filePath.split('/')[1]" class="h-48"/>
                    <div class="flex justify-center items-center space-x-4">
                        <a-dropdown>
                            <template #overlay>
                                <a-menu @click="copyImageLink" class="text-sm">
                                    <a-menu-item :key="githubImageLinkGenerator.generate(ownerName, filePath)">
                                        {{githubImageLinkGenerator.sourceName}}
                                    </a-menu-item>
                                    <a-menu-item :key="jsDelivrImageLinkGenerator.generate(ownerName, filePath)">
                                        {{jsDelivrImageLinkGenerator.sourceName}}
                                    </a-menu-item>
                                    <a-menu-item :key="jSDMirrorImageLinkGenerator.generate(ownerName, filePath)">
                                        {{jSDMirrorImageLinkGenerator.sourceName}}
                                    </a-menu-item>
                                    <a-menu-item :key="relayImageLinkGenerator.generate(ownerName, filePath)">
                                        {{relayImageLinkGenerator.sourceName}}
                                    </a-menu-item>
                                </a-menu>
                            </template>
                            <a-button>
                                复制链接 <font-awesome-icon :icon="['fas', 'chevron-down']" class="ml-1"/>
                            </a-button>
                        </a-dropdown>
                        <a-button @click="copyImageData">复制内容</a-button>
                    </div>
                </div>
            </template>
        </div>
    </div>
</template>

<style scoped lang="scss">
.nav-content {
    @apply dropdown-content mt-1 rounded-box shadow flex flex-col space-y-1 text-lg;
    .nav-item {
        @apply rounded-sm lg:rounded-xl w-[4rem] p-1;
    }
}
</style>