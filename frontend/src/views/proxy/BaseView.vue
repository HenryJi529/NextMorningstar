<script lang="ts" setup>
import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import CommonManagePanel from "@/components/common/CommonManagePanel.vue";
import type {NavItem4Manage} from "@/types/common";
import {isBot} from "@/utils/handleClient";
import GithubCorner from "@/components/GithubCorner.vue";
import MorningstarFooter from "@/components/Footer.vue";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";

setTheme(getDefaultTheme());
useFavicon().value = '/proxy.ico';
useHead({
    title: 'Proxy - 节点聚合',
    meta: [
        {
            name: 'viewport',
            content: `width=640`
        }
    ],
})

const items: NavItem4Manage[] = [
    {
        cnName: '节点',
        enName: 'node',
        listRouteName: 'proxy-manage-node-list',
        editRouteName: 'proxy-manage-node-edit',
        iconName: 'fa-solid fa-share-nodes'
    },
    {
        cnName: '订阅',
        enName: 'sub',
        listRouteName: 'proxy-manage-sub-list',
        editRouteName: 'proxy-manage-sub-edit',
        iconName: 'fa-solid fa-gear'
    },
    {
        cnName: '令牌',
        enName: 'token',
        listRouteName: 'proxy-manage-token-list',
        editRouteName: 'proxy-manage-token-edit',
        iconName: 'fa-solid fa-key'
    }
]

</script>

<template>
    <github-corner/>
    <div class="flex flex-col justify-between min-h-dvh">
        <div class="container mx-auto mt-8">
            <div v-if="!isBot()">
                <div class="text-center text-5xl font-bold">节点聚合</div>
                <common-manage-panel :items="items">
                </common-manage-panel>
            </div>
            <div v-else class="text-center mt-10 font-bold">
                You are not welcome...
            </div>
        </div>
        <morningstar-footer/>
    </div>
</template>

<style lang="scss" scoped>
</style>