<script lang="ts" setup>
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";
import {ref} from "vue";
import {RouterView, useRouter} from "vue-router";
import ls from 'localstorage-slim';
import MorningstarFooter from "@/components/Footer.vue";
import {MORNINGSTAR_GITHUB_LINK} from "@/constants/SiteConstant";
import axios from '@/axios/index';
import type {R} from "@/types/common";
import {useUserStore} from "@/stores/users";
import {API_BLOG_ARTICLE_RANDOM, API_BLOG_FEED_ATOM, API_BLOG_FEED_RSS} from "@/constants/ApiConstant";
import {getCurrentTheme, getDefaultTheme, getOppositeTheme, setTheme} from "@/utils/handleTheme";
import ScrollToTop from "@/components/blog/ScrollToTop.vue";
import {useBlogStore} from "@/stores/blog";
import {getPermissions, hasAnyPermission} from "@/utils/handlePermission";

useFavicon().value = '/blog.ico'
useHead({
    title: 'Blog - 晨星博客',
    meta: [
        {
            name: 'viewport',
            content: `width=640`
        },
        {
            name: 'description',
            content: 'Dev & Life'
        }
    ],
    link: [
        {
            rel: 'alternate',
            type: 'application/rss+xml',
            href: API_BLOG_FEED_RSS
        }
    ],
});

const router = useRouter();
const userStore = useUserStore();
const blogStore = useBlogStore();


const viewRandomArticle = async () => {
    const response: R<string> = (await axios.get(API_BLOG_ARTICLE_RANDOM)).data;
    const randomArticleId = response.data;
    await router.push({name: 'blog-detail', params: {id: randomArticleId}})
}


const searchTerm = ref<string>("");
const viewSearchResult = () => {
    router.push({name: 'blog-list', query: {term: searchTerm.value}});
}


const serviceModalOpen = ref(false);
const toggleServiceModal = () => {
    serviceModalOpen.value = !serviceModalOpen.value;
}


const jumpToFeed = () => {
    window.open(`${API_BLOG_FEED_ATOM}`, '_blank');
}


let themeTimer: NodeJS.Timeout;
const currentTheme = ref(getCurrentTheme()); // 只用于动态更新主题图标
const blog_prefer_theme = ls.get('blog-prefer-theme');
if (blog_prefer_theme !== null) {
    // 有偏好主题
    const theme = blog_prefer_theme as string
    setTheme(theme);
    currentTheme.value = theme;
} else {
    // 无偏好主题
    const theme = getDefaultTheme();
    setTheme(theme);
    currentTheme.value = theme;
    themeTimer = setInterval(() => {
        const theme = getDefaultTheme();
        setTheme(theme);
        currentTheme.value = theme;
    }, 1000 * 60);
}

const toggleTheme = () => {
    const nextTheme = getOppositeTheme(getCurrentTheme());
    ls.set('blog-prefer-theme', nextTheme, {ttl: 60 * 60 * 3});
    clearInterval(themeTimer); // 一旦设置偏好主题，就不再自动更新
    setTheme(nextTheme);
    currentTheme.value = nextTheme;
}


</script>

<template>
    <div class="container mx-auto font-family-press">
        <div class="fixed top-3 left-3 lg:hidden">
            <label class="btn btn-circle btn-ghost btn-lg swap swap-rotate text-3xl">
                <input v-model="blogStore.isMobileMenuOpen" type="checkbox"/>
                <div class="swap-off">
                    <font-awesome-icon :icon="['fas', 'bars']"/>
                </div>
                <div class="swap-on">
                    <font-awesome-icon :icon="['fas', 'xmark']"/>
                </div>
            </label>
        </div>
        <div>
            <div :class="{'modal-open': serviceModalOpen}" class="modal">
                <div class="modal-box">
                    <div class="btn btn-sm btn-ghost btn-circle absolute right-2 top-2" @click="toggleServiceModal">
                        <font-awesome-icon :icon="['fas', 'xmark']" class="text-xl cursor-pointer"/>
                    </div>
                    <div class="text-4xl font-bold text-center">本站服务</div>
                    <div class="py-4 text-2xl flex flex-col items-center justify-center">
                        <div class="flex space-x-2 items-center">
                            <font-awesome-icon :icon="['fas', 'compass']"/>
                            <router-link to="/nav">晨星导航</router-link>
                        </div>
                        <div class="flex space-x-2 items-center">
                            <font-awesome-icon :icon="['fas', 'fire']"/>
                            <router-link to="/kill">仿三国杀</router-link>
                        </div>
                        <div class="flex space-x-2 items-center">
                            <font-awesome-icon :icon="['fas', 'image']"/>
                            <router-link to="/pic">开放图床</router-link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="fixed bottom-2 right-2 lg:bottom-8 lg:right-8">
            <scroll-to-top/>
        </div>
        <div class="flex flex-col justify-between min-h-dvh">
            <div class="flex-1 flex flex-col">
                <div class="lg:flex lg:justify-between lg:items-center text-3xl lg:min-h-16 p-2">
                    <div class="text-center p-2">
                        <div class="cursor-pointer" @click="router.push('/blog');blogStore.isMobileMenuOpen = false">
                            <span class="text-5xl font-bold">晨星博客</span>
                        </div>
                    </div>
                    <div class="flex text-center items-center justify-center space-x-4 px-4">
                        <div class="input input-bordered items-center space-x-2 flex">
                            <div class="cursor-pointer text-2xl" title="搜索" @click="viewSearchResult">
                                <font-awesome-icon :icon="['fab', 'searchengin']"/>
                            </div>
                            <input v-model="searchTerm" class="flex-1" placeholder="搜索" required type="search"
                                   @keyup.enter="viewSearchResult">
                        </div>
                        <div class="cursor-pointer hover:animate-pulse" title="随机" @click="viewRandomArticle">
                            <font-awesome-icon :icon="['fas', 'dice']"/>
                        </div>
                        <div class="cursor-pointer hover:animate-pulse" title="主题" @click="toggleTheme">
                            <font-awesome-icon v-if="currentTheme === 'dark'" :icon="['fas', 'moon']"/>
                            <font-awesome-icon v-else :icon="['fas', 'sun']"/>
                        </div>
                        <div class="cursor-pointer hover:animate-pulse" title="RSS" @click="jumpToFeed">
                            <font-awesome-icon :icon="['fas', 'rss']"/>
                        </div>
                        <div class="hover:animate-pulse" title="相册">
                            <router-link to="/love">
                                <font-awesome-icon :icon="['fas', 'heart']"/>
                            </router-link>
                        </div>
                        <div class="hover:animate-pulse" title="私信">
                            <div class="cursor-pointer" @click="router.push({name: 'blog-contact'})">
                                <font-awesome-icon :icon="['fas', 'paper-plane']"/>
                            </div>
                        </div>
                        <div class="cursor-pointer hover:animate-pulse" title="服务" @click="toggleServiceModal">
                            <font-awesome-icon :icon="['fas', 'server']"/>
                        </div>
                        <div class="hover:animate-pulse" title="源码">
                            <a :href="MORNINGSTAR_GITHUB_LINK" target="_blank">
                                <font-awesome-icon :icon="['fab', 'github']"/>
                            </a>
                        </div>
                        <div title="用户">
                            <div v-if="userStore.id" class="flex justify-center items-center">
                                <details class="dropdown dropdown-bottom dropdown-end">
                                    <summary class="list-none w-10 h-10 cursor-pointer">
                                        <img :src="userStore.avatarLink" alt="头像" class="rounded-full w-full h-full"/>
                                    </summary>
                                    <ul class="nav-content">
                                        <li v-if="userStore.isAuthenticated" class="nav-item">
                                            <div class="cursor-pointer" @click="router.push({name: 'auth-profile'})">
                                                档案
                                            </div>
                                        </li>
                                        <li v-if="hasAnyPermission(getPermissions('blog-manage'))" class="nav-item">
                                            <div class="cursor-pointer"
                                                 @click="router.push({name: 'blog-manage-article-list'})">管理
                                            </div>
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
                                            <div class="cursor-pointer" @click="router.push({name: 'auth-login'})">
                                                登入
                                            </div>
                                        </li>
                                        <li class="nav-item">
                                            <div class="cursor-pointer" @click="router.push({name: 'auth-register'})">
                                                注册
                                            </div>
                                        </li>
                                    </ul>
                                </details>
                            </div>
                        </div>
                    </div>
                </div>
                <router-view class="flex-1"/>
            </div>
            <morningstar-footer/>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.nav-content {
    @apply dropdown-content mt-1 rounded-box shadow flex flex-col space-y-1 text-lg;
    .nav-item {
        @apply hover:bg-gray-300 dark:hover:bg-gray-700 rounded-sm lg:rounded-xl w-[4rem] p-1;
    }
}
</style>