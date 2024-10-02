<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useHead} from '@vueuse/head';
import {useFavicon} from "@vueuse/core";

import {useUserStore} from "@/stores/users";
import ItemComponent from "@/components/nav/ItemComponent.vue";
import MorningstarFooter from "@/components/Footer.vue";
import navData from "@/assets/json/nav.json";
import type {Category} from "@/types/nav";
import GithubCorner from "@/components/GithubCorner.vue";
import {storeToRefs} from "pinia";
import {PACE_JS_CSS, PACE_JS_JS} from "@/constants/LibConstant";
import {isMobile} from "@/utils/handleClient";
import {hasAnyPermission} from "@/utils/handlePermission";
import {ROLE_SUPER_ADMIN} from "@/constants/RoleConstant";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";


setTheme(getDefaultTheme());
const userStore = useUserStore();
const {username} = storeToRefs(userStore);
const isAdmin = hasAnyPermission([ROLE_SUPER_ADMIN]);
const categories = ref<Category[]>(navData as Category[]);
for (let i = 0; i < categories.value.length; i++) {
    categories.value[i].icon = categories.value[i].icon.replace(/'/g, '"');
}
const hideHeadBarTitle = ref<boolean>(false);

useFavicon().value = '/nav.ico'
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
            async: true,
            defer: false,
        }
    ]
});

const setMobileAnimation = () => {
    const elements = document.querySelectorAll('.nav-item');

    const middlePosition = window.innerHeight / 2;

    elements.forEach(element => {
        const rect = element.getBoundingClientRect();
        const isInMiddle = rect.top <= middlePosition && rect.bottom >= middlePosition;

        if (isInMiddle) {
            element.classList.add('translate-x-2');
        } else {
            element.classList.remove('translate-x-2');
        }
    });
}

onMounted(async () => {
    setInterval(() => {
        hideHeadBarTitle.value = document.documentElement.scrollTop > (isMobile() ? 30 : 50);
    }, 200);

    if (isMobile()) {
        // 设置移动端滑动效果
        setMobileAnimation();
        let isThrottled = false;
        window.addEventListener('scroll', () => {
            if (!isThrottled) {
                setMobileAnimation();
                isThrottled = true;
                setTimeout(() => {
                    isThrottled = false;
                }, 10);
            }
        });
    }
})

</script>

<template>
    <div>
        <div class="sticky top-0 md:hidden w-full text-center">
            <div :class="{'hidden': hideHeadBarTitle}" class="text-3xl pt-4">
                晨星导航
            </div>
            <nav class="flex justify-between w-full px-12">
                <template v-for="category in categories" :key="category.slug">
                    <a v-if="!(category.requireAdmin && !isAdmin)" :href="`#${category.slug}`"
                       class="text-lg">
                        <font-awesome-icon :icon="JSON.parse(category.icon)"/>
                    </a>
                </template>
                <router-link :to="{name: 'blog-contact'}" class="text-lg" target="_blank">
                    <font-awesome-icon :icon="['fas', 'at']"/>
                </router-link>
            </nav>
        </div>
        <div class="flex">
            <div
                class="h-dvh hidden md:sticky md:top-0 md:min-w-[15rem] md:w-1/6 text-center md:flex flex-col justify-between">
                <div class="text-3xl lg:text-4xl pt-4 lg:py-6 font-bold">
                    晨星导航
                </div>
                <nav class="w-full flex-1">
                    <ul class="py-2 flex flex-col justify-between items-center">
                        <template v-for="category in categories" :key="category.slug">
                            <li v-if="!(category.requireAdmin && !isAdmin)" class="my-2">
                                <a :href="`#${category.slug}`">
                            <span class="text-xl lg:text-2xl font-bold hover:text-cyan-400">
                                <span class="inline-block w-10"><font-awesome-icon
                                    :icon="JSON.parse(category.icon)"/></span>
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
                    <router-link :to="{name: 'blog-contact'}" target="_blank">
                        <span>
                            <font-awesome-icon :icon="['fas', 'at']"/>
                            留言
                        </span>
                    </router-link>
                    <router-link to="/">
                        <span>
                            <font-awesome-icon :icon="['fas', 'house']"/>
                            首页
                        </span>
                    </router-link>
                </div>
            </div>
            <div class="flex-1">
                <github-corner :size="isMobile() ? 60: 80"/>
                <template v-for="category in categories" :key="category.slug">
                    <section v-if="!(category.requireAdmin && !isAdmin)" class="py-4">
                        <a :id="category.slug"></a>
                        <div class="mb-3 pl-6 text-2xl">
                            <font-awesome-icon :icon="JSON.parse(category.icon)"/>
                            {{ category.name }}
                        </div>
                        <div
                            class="grid justify-around grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 pr-8 pl-4">
                            <template v-for="item in category.items" :key="item.name">
                                <item-component :item="item"/>
                            </template>
                        </div>
                    </section>
                </template>
                <morningstar-footer/>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>