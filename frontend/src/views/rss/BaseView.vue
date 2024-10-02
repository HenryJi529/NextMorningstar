<script setup lang="ts">
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";

useFavicon().value = '/rss.ico'
useHead({
    title: 'RSS - RSS工厂',
    meta: [
        {
            name: 'description',
            content: '仿照Feed43生成RSS订阅源'
        }
    ]
});
</script>

<template>
    RSS 工厂
</template>

<style scoped lang="scss">

</style>