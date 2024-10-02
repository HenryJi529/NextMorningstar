<script lang="ts" setup>
import {useRouter} from "vue-router";
import {onMounted} from "vue";

import MorningstarFooter from "@/components/Footer.vue";

const router = useRouter();
onMounted(() => {
    setTimeout(() => {
        router.back();
    }, 5000)
})
</script>

<template>
    <div class="h-page flex flex-col mx-auto w-screen">
        <div class="flex-1 flex justify-center items-center">
        <span class="text-5xl">
            <slot></slot>
        </span>
        </div>
        <morningstar-footer/>
    </div>
</template>

<style lang="scss" scoped>

</style>