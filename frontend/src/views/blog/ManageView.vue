<script setup lang="ts">
import {useRoute} from "vue-router";

const route = useRoute();
</script>

<template>
    <div class="py-6 lg:flex">
        <div class="text-2xl flex justify-around py-2 lg:py-6 lg:justify-start lg:flex-col lg:space-y-8">
            <router-link class="nav-item" :to="{name: 'blog-manage-article-list'}"
                         active-class="nav-item-active" :class="{'nav-item-active': route.name === 'blog-manage-article-edit'}">
                <div class="icon">
                    <font-awesome-icon :icon="['fab', 'markdown']" />
                </div>
                <span>文章</span>
            </router-link>
            <router-link class="nav-item" :to="{name: 'blog-manage-category-list'}"
                         active-class="nav-item-active" :class="{'nav-item-active': route.name === 'blog-manage-category-edit'}">
                <div class="icon">
                    <font-awesome-icon :icon="['fas', 'folder']" />
                </div>
                <span>分类</span>
            </router-link>
            <router-link class="nav-item" :to="{name: 'blog-manage-tag-list'}"
                         active-class="nav-item-active" :class="{'nav-item-active': route.name === 'blog-manage-tag-edit'}">
                <div class="icon">
                    <font-awesome-icon :icon="['fas', 'tag']" />
                </div>
                <span>标签</span>
            </router-link>
        </div>
        <div class="lg:flex-1 px-8">
            <router-view/>
        </div>
    </div>
</template>

<style scoped lang="scss">
.nav-item {
    @apply flex items-center justify-center space-x-2 cursor-pointer;
    .icon {
        @apply w-8 flex justify-center items-center;
    }
    &-active {
        @apply  text-blue-600 dark:text-blue-500;
    }
}
</style>