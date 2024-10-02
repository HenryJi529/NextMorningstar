<script setup lang="ts">
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";
import {onMounted, ref} from "vue";
import {RouterView, useRouter} from "vue-router";
import ls from 'localstorage-slim';
import MorningstarFooter from "@/components/Footer.vue";
import {MORNINGSTAR_GITHUB_LINK} from "@/constants/SiteConstant";
import axios from "axios";
import type {R} from "@/types/common";
import {useUserStore} from "@/stores/users";
import {API_BLOG_ARTICLE_RANDOM} from "@/constants/ApiConstant";
import {getCurrentTheme, getOppositeTheme, setTheme} from "@/utils/handleTheme";
import ScrollToTop from "@/components/blog/ScrollToTop.vue";
import {useOtherStore} from "@/stores/others";
import {API_BLOG_FEED_RSS, API_BLOG_FEED_ATOM} from "@/constants/ApiConstant";
import {getPermissions, hasAnyPermission} from "@/utils/handlePermission";
import {isIphone} from "@/utils/handleClient";

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
const otherStore = useOtherStore();

const toggleTheme = () => {
    currentTheme.value = getOppositeTheme(currentTheme.value);
    ls.set('blog-prefer-theme', currentTheme.value, { ttl: 60 * 30 });
    setTheme(currentTheme.value);
}

const viewRandomArticle = async () => {
    const response: R<string> = (await axios.get(API_BLOG_ARTICLE_RANDOM)).data;
    const randomArticleId = response.data;
    await router.push({ name: 'blog-detail', params: { id: randomArticleId } })
}

const viewSearchResult = () => {
    router.push({ name: 'blog-list', query: { term: searchTerm.value } });
}

const currentTheme = ref<string>(getCurrentTheme());
const blog_prefer_theme = ls.get('blog-prefer-theme');
if(blog_prefer_theme) {
    currentTheme.value = blog_prefer_theme as string;
    setTheme(currentTheme.value);
}

const searchTerm = ref<string>("");

const showServiceModal = ref(false);
const toggleServiceModal = () => {
    showServiceModal.value = !showServiceModal.value;
}

const jumpToFeed = ()=> {
    window.open(`${API_BLOG_FEED_ATOM}`, '_blank');
}

onMounted(()=>{
})


</script>

<template>
    <div class="container mx-auto">
        <div class="fixed top-3 left-3 lg:hidden">
            <label class="btn btn-circle btn-ghost btn-lg swap swap-rotate text-3xl">
                <input type="checkbox" v-model="otherStore.isMobileMenuOpen"/>
                <div class="swap-off">
                    <font-awesome-icon :icon="['fas', 'bars']"/>
                </div>
                <div class="swap-on">
                    <font-awesome-icon :icon="['fas', 'xmark']"/>
                </div>
            </label>
        </div>
        <div>
            <div class="modal" :class="{'modal-open': showServiceModal}">
                <div class="modal-box">
                    <div class="btn btn-sm btn-ghost btn-circle absolute right-2 top-2" @click="toggleServiceModal">X</div>
                    <div class="text-4xl font-bold text-center">本站服务</div>
                    <div class="py-4 text-2xl flex flex-col items-center justify-center">
                        <div class="flex space-x-2 items-center">
                            <font-awesome-icon :icon="['fas', 'compass']" />
                            <router-link to="/nav">晨星导航</router-link>
                        </div>
                        <div class="flex space-x-2 items-center">
                            <font-awesome-icon :icon="['fas', 'rss']" />
                            <router-link to="/rss">RSS工厂</router-link>
                        </div>
                        <div class="flex space-x-2 items-center">
                            <font-awesome-icon :icon="['fas', 'fire']" />
                            <router-link to="/kill">仿三国杀</router-link>
                        </div>
                        <div class="flex space-x-2 items-center">
                            <font-awesome-icon :icon="['fas', 'image']" />
                            <router-link to="/kill">开放图床</router-link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="fixed bottom-2 right-2 lg:bottom-8 lg:right-8">
            <scroll-to-top/>
        </div>
        <div class="flex flex-col justify-between" :class="isIphone() ? 'min-h-page-iphone' : 'min-h-screen'">
            <div class="flex-1 flex flex-col">
                <div class="lg:flex lg:justify-between lg:items-center text-3xl lg:min-h-16 p-2">
                    <div class="text-center p-2">
                        <div @click="router.push('/blog');otherStore.isMobileMenuOpen = false" class="cursor-pointer">
                            <span class="text-5xl">晨星博客</span>
                        </div>
                    </div>
                    <div class="flex text-center items-center justify-center space-x-4 px-4">
                        <div class="input input-bordered items-center space-x-2 flex">
                            <div title="搜索" class="cursor-pointer text-2xl" @click="viewSearchResult">
                                <font-awesome-icon :icon="['fab', 'searchengin']" />
                            </div>
                            <input class="flex-1" type="search" placeholder="搜索" v-model="searchTerm" required @keyup.enter="viewSearchResult">
                        </div>
                        <div class="cursor-pointer hover:animate-pulse" title="随机" @click="viewRandomArticle">
                            <font-awesome-icon :icon="['fas', 'dice']"/>
                        </div>
                        <div title="主题" class="cursor-pointer hover:animate-pulse" @click="toggleTheme">
                            <font-awesome-icon :icon="['fas', 'moon']" v-if="currentTheme === 'dark'"/>
                            <font-awesome-icon :icon="['fas', 'sun']" v-else/>
                        </div>
                        <div title="RSS" class="cursor-pointer hover:animate-pulse" @click="jumpToFeed">
                            <font-awesome-icon :icon="['fas', 'rss']" />
                        </div>
                        <div title="相册" class="hover:animate-pulse">
                            <router-link to="/love">
                                <font-awesome-icon :icon="['fas', 'heart']" />
                            </router-link>
                        </div>
                        <div title="私信" class="hover:animate-pulse">
                            <div class="cursor-pointer" @click="router.push({name: 'blog-contact'})">
                                <font-awesome-icon :icon="['fas', 'paper-plane']" />
                            </div>
                        </div>
                        <div title="服务" class="cursor-pointer hover:animate-pulse" @click="toggleServiceModal">
                            <font-awesome-icon :icon="['fas', 'server']" />
                        </div>
                        <div title="源码" class="hover:animate-pulse">
                            <a :href="MORNINGSTAR_GITHUB_LINK" target="_blank">
                                <font-awesome-icon :icon="['fab', 'github']" />
                            </a>
                        </div>
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
                                        <li class="nav-item" v-if="hasAnyPermission(getPermissions('blog-manage'))">
                                            <div class="cursor-pointer" @click="router.push({name: 'blog-manage-article-list'})">管理</div>
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
                <router-view class="flex-1"/>
            </div>
            <morningstar-footer />
        </div>
    </div>
</template>

<style scoped lang="scss">
.nav-content {
    @apply dropdown-content mt-1 rounded-box shadow flex flex-col space-y-1 text-lg;
    .nav-item {
        @apply hover:bg-gray-300 dark:hover:bg-gray-700 rounded-sm lg:rounded-xl w-[4rem] p-1;
    }
}
</style>