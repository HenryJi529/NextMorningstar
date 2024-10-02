<script setup lang="ts">

import {RouterView} from "vue-router";
import MorningstarFooter from "@/components/Footer.vue";
import {useHead} from "@vueuse/head";

useHead({
    meta: [
        {
            name: 'viewport',
            content: `width=450`
        },
    ]
});

</script>

<template>
    <div class="flex flex-col w-screen px-2 min-h-screen container mx-auto">
        <router-view class="flex-1"/>
        <morningstar-footer/>
    </div>
</template>

<style scoped lang="scss">
</style>