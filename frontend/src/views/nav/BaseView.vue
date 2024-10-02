<script setup lang="ts">
import {onMounted, ref} from "vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import { useHead } from '@vueuse/head';

import {useUserStore} from "@/stores/users";
import {MORNINGSTAR_NAV_NAME} from "@/constants/SiteConstant";
import ItemComponent from "@/components/nav/ItemComponent.vue";
import MorningstarFooter from "@/components/Footer.vue";
import navData from "@/assets/json/nav.json";
import type {Category} from "@/types/nav";
import GithubCorner from "@/components/GithubCorner.vue";
import {storeToRefs} from "pinia";
import {PACE_JS_CSS, PACE_JS_JS} from "@/constants/LibConstant";

const userStore = useUserStore();
const {username, isAdmin} = storeToRefs(userStore);
const categories = ref<Category[]>(navData as Category[]);
for(let i = 0; i < categories.value.length; i++ ){
    categories.value[i].icon = categories.value[i].icon.replace(/'/g, '"');
}
const hideHeadBarTitle = ref<boolean>(false);


useHead({
    title: 'Nav - 晨星导航',
    meta: [
        {
            name: 'description',
            content: '这是个为开发者准备的导航页'
        },
        {
            "http-equiv": 'refresh',
            content: '1800;/'
        }
    ],
    link: [
        {
            rel: 'stylesheet',
            href: PACE_JS_CSS
        }
    ],
    script: [
        {
            src: PACE_JS_JS,
            async: true
        }
    ]
});

onMounted(async ()=>{
    const isMobile = /Android|iPhone/i.test(navigator.userAgent);
    setInterval(() => {
            hideHeadBarTitle.value = document.documentElement.scrollTop > (isMobile ? 30 : 50);
        }, 200);
})

</script>

<template>
    <div>
        <div class="sticky top-0 md:hidden w-full text-center">
            <div class="text-3xl pt-4" :class="{'hidden': hideHeadBarTitle}">
                {{MORNINGSTAR_NAV_NAME}}
            </div>
            <nav class="flex justify-between w-full px-4">
                <template v-for="category in categories" :key="category.slug">
                    <a v-if="!(category.level === 'admin' && !isAdmin)" :href="`#${category.slug}`"
                       class="text-lg" >
                        <font-awesome-icon :icon="JSON.parse(category.icon)" />
                    </a>
                </template>
                <router-link to="#" target="_blank" class="text-lg">
                    <font-awesome-icon :icon="['fas', 'at']" />
                </router-link>
            </nav>
        </div>
        <div class="flex">
            <div class="h-screen hidden md:sticky md:top-0 md:min-w-[15rem] md:w-1/6 text-center md:flex flex-col justify-between">
                <div class="text-3xl lg:text-4xl pt-4 lg:py-6 font-bold">
                    {{MORNINGSTAR_NAV_NAME}}
                </div>
                <nav class="w-full flex-1">
                    <ul class="py-2 flex flex-col justify-between items-center">
                        <template v-for="category in categories" :key="category.slug">
                            <li v-if="!(category.level === 'admin' && !isAdmin)" class="my-2">
                                <a :href="`#${category.slug}`">
                            <span class="text-xl lg:text-2xl font-bold hover:text-cyan-400">
                                <font-awesome-icon :icon="JSON.parse(category.icon)" />
                                {{ category.name }}
                            </span>
                                </a>
                            </li>
                        </template>
                    </ul>
                </nav>
                <div class="w-full pb-4">
                    <div class="text-lg font-bold">
                        <span>UserName: </span>
                        <span v-if="username">{{ username }}</span>
                        <span v-else>未登录</span>
                    </div>
                    <a href="#" target="_blank">
                        <!-- TODO: 修改路径为留言页-->
                        <span>
                            <font-awesome-icon :icon="['fas', 'at']" />
                            留言
                        </span>
                    </a>
                    <a href="/" target="_blank">
                        <!-- TODO: 修改路径为主页-->
                        <span>
                            <font-awesome-icon :icon="['fas', 'house']" />
                            主页
                        </span>
                    </a>
                </div>
            </div>
            <div class="flex-1">
                <github-corner github-link="https://github.com/HenryJi529/NextMorningstar"/>
                <template v-for="category in categories" :key="category.slug">
                    <section v-if="!(category.level === 'admin' && !isAdmin)" class="py-4" >
                        <a :id="category.slug"></a>
                        <div class="mb-3 pl-6 text-2xl">
                            <font-awesome-icon :icon="JSON.parse(category.icon)" />
                            {{category.name}}
                        </div>
                        <div
                            class="grid justify-around grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 pr-8 pl-4">
                            <template v-for="item in category.items">
                                <item-component :item="item" />
                            </template>
                        </div>
                    </section>
                </template>
                <morningstar-footer />
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>