<script setup lang="ts">

import {useFavicon} from "@vueuse/core";
import {useHead} from "@vueuse/head";
import {h} from 'vue';

import ShareLinks from "@/components/pic/ShareLinks.vue";

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

const titleElement = h('div', { "class": 'text-5xl text-center mt-10' }, '开 放 图 床');

</script>

<template>
    <component :is="titleElement"/>
    <ShareLinks owner-name="HenryJi529" repo-name="TestRepo" file-path="test/1747017155726.jpg"/>
</template>

<style scoped lang="scss">

</style>