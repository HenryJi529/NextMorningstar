<script setup lang="ts">
import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import CommonManagePanel from "@/components/common/CommonManagePanel.vue";
import type {NavItem4Manage} from "@/types/common";

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
    <div class="container mx-auto mt-8">
        <div class="text-center text-5xl font-bold">后台管理</div>
        <common-manage-panel :items="items">
        </common-manage-panel>
    </div>
</template>

<style scoped lang="scss">
</style>