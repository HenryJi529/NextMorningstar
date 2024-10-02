<script lang="ts" setup>
import {RouterView} from "vue-router";
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";

useFavicon().value = '/kill.ico'
useHead({
    title: 'Kill - 仿三国杀',
    meta: [
        {
            name: 'description',
            content: '解决了游卡Web三国杀诸多痛点的开源克隆版'
        }
    ]
});

</script>

<template>
    <div class="container">
        <router-view/>
    </div>
</template>
