<script lang="ts" setup>

import {RouterView} from "vue-router";
import MorningstarFooter from "@/components/Footer.vue";
import {useHead} from "@vueuse/head";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";

setTheme(getDefaultTheme());
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
    <div class="flex flex-col w-screen px-2 container mx-auto min-h-dvh">
        <router-view class="flex-1"/>
        <morningstar-footer/>
    </div>
</template>

<style lang="scss" scoped>
</style>