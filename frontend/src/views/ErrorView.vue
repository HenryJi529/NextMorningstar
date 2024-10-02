<script setup lang="ts">
import MorningstarFooter from "@/components/Footer.vue";
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

<style scoped lang="scss">

</style>