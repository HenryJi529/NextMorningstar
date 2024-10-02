<script lang="ts" setup>

import {RouterLink} from "vue-router";
import {getPermissions, hasAnyPermission} from "@/utils/handlePermission";
import MorningstarFooter from "@/components/Footer.vue";
import GithubCorner from "@/components/GithubCorner.vue";
import {useHead} from "@vueuse/head";

useHead({
    title: 'Index - 功能索引',
    meta: [
        {
            name: 'viewport',
            content: `width=device-width, initial-scale=1.0`
        }
    ],
})

</script>

<template>
    <github-corner/>
    <div class="flex items-center justify-center relative min-h-dvh">
        <div class="grid grid-flow-row grid-cols-2 lg:grid-cols-3 gap-4 text-center">
            <div class="app-item">
                <router-link :to="'/kill'">仿三国杀</router-link>
            </div>
            <div class="app-item">
                <router-link :to="'/nav'">开发者导航</router-link>
            </div>
            <div class="app-item">
                <router-link :to="'/blog'">个人博客</router-link>
            </div>
            <div class="app-item">
                <router-link :to="'/love'">爱の相册</router-link>
            </div>
            <div v-if="hasAnyPermission(getPermissions('proxy'))" class="app-item">
                <router-link to="/proxy">节点聚合</router-link>
            </div>
            <div class="app-item">
                <router-link to="/pic">开放图床</router-link>
            </div>
            <div class="app-item">
                <router-link to="/practice">自由练习</router-link>
            </div>
            <div class="app-item">
                <router-link :to="'/resume'">在线简历</router-link>
            </div>
            <div v-if="hasAnyPermission(getPermissions('admin'))" class="app-item">
                <router-link to="/admin">后台管理</router-link>
            </div>
        </div>
        <morningstar-footer class="absolute bottom-0"/>
    </div>
</template>

<style lang="scss" scoped>
.app-item {
    @apply btn btn-ghost text-2xl;
}
</style>