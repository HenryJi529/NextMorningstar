<script setup lang="ts">

import {RouterLink} from "vue-router";
import {getPermissions, hasAnyPermission} from "@/utils/handlePermission";


</script>

<template>
    <div class="h-screen flex items-center justify-center">
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
                <div class="app-item" v-if="hasAnyPermission(getPermissions('proxy'))">
                    <router-link to="/proxy">节点聚合</router-link>
                </div>
                <div class="app-item">
                    <router-link to="/rss">RSS工厂</router-link>
                </div>
                <div class="app-item">
                    <router-link :to="'/send'">无迹闪传</router-link>
                </div>
                <div class="app-item">
                    <router-link to="/pic">开放图床</router-link>
                </div>
                <div class="app-item">
                    <router-link to="/practice">自由练习</router-link>
                </div>
                <div class="app-item">
                    <router-link :to="'/resume'">个人简历</router-link>
                </div>
                <div class="app-item" v-if="hasAnyPermission(getPermissions('admin'))">
                    <router-link to="/admin">后台管理</router-link>
                </div>
            </div>
    </div>
</template>

<style scoped lang="scss">
.app-item {
    @apply btn btn-ghost text-2xl;
}
</style>