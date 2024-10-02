<script setup lang="ts">
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";

useFavicon().value = '/send.ico'
useHead({
    title: 'Send - 无迹闪传',
    meta: [
        {
            name: 'description',
            content: '仿照Firefox Send的文件分享工具'
        }
    ]
});
</script>

<template>
    无迹闪传
</template>

<style scoped lang="scss">

</style>