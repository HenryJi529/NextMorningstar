<script lang="ts" setup>
import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import CommonManagePanel from "@/components/common/CommonManagePanel.vue";
import type {NavItem4Manage} from "@/types/common";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";
import MorningstarFooter from "@/components/Footer.vue";
import GithubCorner from "@/components/GithubCorner.vue";

setTheme(getDefaultTheme());
useFavicon().value = '/proxy.ico';
useHead({
    title: 'Admin - 后台管理',
    meta: [
        {
            name: 'viewport',
            content: `width=640`
        }
    ],
})

const items: NavItem4Manage[] = [
    {
        cnName: '用户',
        enName: 'user',
        listRouteName: 'admin-manage-user-list',
        editRouteName: 'admin-manage-user-edit',
        iconName: 'fa-solid fa-user'
    }
]

</script>

<template>
    <github-corner/>
    <div class="flex flex-col min-h-dvh">
        <div class="container mx-auto mt-8 flex-1">
            <div class="text-center text-5xl font-bold">后台管理</div>
            <common-manage-panel :items="items">
            </common-manage-panel>
        </div>
        <morningstar-footer/>
    </div>
</template>

<style lang="scss" scoped>
</style>