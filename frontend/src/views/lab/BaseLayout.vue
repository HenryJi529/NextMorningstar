<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router';
import MorningstarFooter from '@/components/Footer.vue';
import GithubCorner from '@/components/GithubCorner.vue';
import { useHead } from '@vueuse/head';

const route = useRoute();
const router = useRouter();

useHead({
    title: 'Lab - 自由实验',
    meta: [
        {
            name: 'viewport',
            content: `width=540`,
        },
    ],
});
</script>

<template>
    <github-corner />
    <div class="flex flex-col min-h-dvh">
        <div class="flex-1 flex flex-col">
            <div class="text-center text-3xl my-10">
                <span class="cursor-pointer mr-4" @click="router.push({ name: 'lab-index' })">
                    自由实验:
                </span>
                {{ route.meta.verboseName }}
            </div>
            <div class="flex-1 mx-auto container flex flex-col justify-center items-center">
                <slot></slot>
            </div>
        </div>
        <morningstar-footer />
    </div>
</template>

<style lang="scss" scoped></style>
