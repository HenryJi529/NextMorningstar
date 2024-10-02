<script setup lang="ts">

import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";

useFavicon().value = '/pic.ico';
useHead({
    title: 'Pic - 开放图床',
    meta: [
        {
            name: 'description',
            content: '基于Github API开发的图床工具'
        }
    ],
})

</script>

<template>
    开放图床
</template>

<style scoped lang="scss">

</style>