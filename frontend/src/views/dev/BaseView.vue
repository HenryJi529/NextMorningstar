<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useFavicon } from '@vueuse/core';
import { useHead } from '@vueuse/head';
import { ClockCircleOutlined } from '@ant-design/icons-vue';
import { useUserStore } from '@/stores/users';
import { hasAnyPermission } from '@/utils/permission';
import { Role } from '@/constants/auth';
import { setTheme } from '@/utils/theme';
import { getStats } from '@/axios/dev';
import { ResponseCode } from '@/constants/response';

useFavicon().value = '/dev.ico';
useHead({
    title: 'Dev - 代码工坊',
    meta: [
        {
            name: 'description',
            content: 'AI 自动值守的代码质量优化流水线',
        },
    ],
});
setTheme('light');

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const canAdmin = computed(() => hasAnyPermission([Role.DEV_ADMIN]));

/* 夜间调度时段(stats 读公开，进 dev 任一个页面拉一次，顶栏常驻展示) */
const scheduleWindow = ref('');
onMounted(async () => {
    const response = await getStats();
    if (response.data.code === ResponseCode.SUCCESS) {
        const s = response.data.data;
        scheduleWindow.value = `${s.scheduledStartTime.slice(0, 5)}–${s.scheduledEndTime.slice(0, 5)}`;
    }
});

const userMenuOpen = ref(false);
const goProfile = () => {
    userMenuOpen.value = false;
    router.push({ name: 'auth-profile' });
};
const logout = async () => {
    await userStore.handleLogout();
    location.reload();
};
</script>

<template>
    <div class="min-h-screen bg-[#f3f4f6] text-slate-700 antialiased">
        <header
            class="sticky top-0 z-20 h-14 border-b border-slate-200 bg-white/90 backdrop-blur flex items-center px-6 gap-8">
            <div class="flex items-center gap-3">
                <img src="/favicon.png" alt="代码工坊" class="w-8 h-8" />
                <div class="flex items-center gap-3">
                    <span class="font-family-song text-lg font-bold text-slate-800 tracking-wide">
                        代码工坊
                    </span>
                    <span class="h-3.5 w-px bg-slate-300"></span>
                    <span class="text-xs text-slate-400 tracking-[0.3em] translate-y-px">
                        代码质量优化流水线
                    </span>
                </div>
            </div>
            <nav class="flex items-center gap-1 text-sm">
                <router-link
                    :to="{ name: 'dev-project' }"
                    :class="route.name === 'dev-project' ? 'nav-active' : 'nav-inactive'"
                    class="nav-link">
                    我的项目
                </router-link>
                <router-link
                    :to="{ name: 'dev-about' }"
                    :class="route.name === 'dev-about' ? 'nav-active' : 'nav-inactive'"
                    class="nav-link">
                    平台介绍
                </router-link>
                <router-link
                    v-if="canAdmin"
                    :to="{ name: 'dev-admin' }"
                    :class="route.name === 'dev-admin' ? 'nav-active' : 'nav-inactive'"
                    class="nav-link flex items-center gap-1.5">
                    平台运维
                    <span
                        class="text-[10px] px-1.5 py-px rounded bg-amber-50 text-amber-600 border border-amber-200">
                        ADMIN
                    </span>
                </router-link>
            </nav>
            <div class="ml-auto flex items-center gap-4">
                <span
                    v-if="scheduleWindow"
                    class="flex items-center gap-1.5 px-3 py-1 rounded-full border border-orange-200 bg-orange-50 text-xs text-orange-500"
                    title="每日调度时段：到点自动启动修复任务，清晨清理沙箱">
                    <clock-circle-outlined />
                    夜间调度
                    <span class="font-mono text-orange-600">{{ scheduleWindow }}</span>
                </span>
                <div class="relative">
                    <button
                        class="flex items-center gap-2.5 rounded-full px-1.5 py-1 hover:bg-orange-50 transition"
                        @click="userMenuOpen = !userMenuOpen">
                        <img
                            :src="userStore.avatarLink"
                            :alt="`${userStore.username}的头像`"
                            class="w-7 h-7 rounded-full border border-orange-200 object-cover" />
                        <span class="text-sm text-slate-600">
                            {{ userStore.nickname || userStore.username }}
                        </span>
                    </button>
                    <div
                        v-if="userMenuOpen"
                        class="fixed inset-0 z-30 cursor-default"
                        @click="userMenuOpen = false"></div>
                    <div
                        v-if="userMenuOpen"
                        class="absolute right-0 top-11 z-40 w-32 rounded-lg border border-slate-200 bg-white shadow-lg py-1.5 text-sm">
                        <button
                            class="w-full text-left px-4 py-2 text-slate-600 hover:bg-orange-50 hover:text-orange-600 transition"
                            @click="goProfile">
                            档案
                        </button>
                        <button
                            class="w-full text-left px-4 py-2 text-slate-600 hover:bg-orange-50 hover:text-orange-600 transition"
                            @click="logout">
                            登出
                        </button>
                    </div>
                </div>
            </div>
        </header>

        <main class="py-6">
            <router-view />
        </main>
    </div>
</template>

<style scoped lang="scss">
.nav-link {
    @apply px-3.5 py-1.5 rounded-md transition;
}
.nav-active {
    @apply bg-orange-50 text-orange-600 font-medium;
}
.nav-inactive {
    @apply text-slate-500 hover:text-slate-800;
}
</style>
