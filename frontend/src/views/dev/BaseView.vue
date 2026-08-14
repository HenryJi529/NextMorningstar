<script setup lang="ts">
import { useFavicon } from '@vueuse/core';
import { useHead } from '@vueuse/head';

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
</script>

<template>代码工坊</template>

<style scoped lang="scss"></style>
