<script setup lang="ts">
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";

useFavicon().value = '/blog.ico'
useHead({
    title: 'Blog - 晨星博客',
    meta: [
        {
            name: 'description',
            content: '学习笔记与生活记录'
        }
    ],
    link: [
        {
            rel: 'apple-touch-icon',
            href: '/blog.png'
        }
    ],
});

</script>

<template>
    <div>
        blog
    </div>
</template>

<style scoped lang="scss">

</style>