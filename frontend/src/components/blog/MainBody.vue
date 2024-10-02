<script setup lang="ts">

import FixedSideBar from "@/components/blog/FixedSideBar.vue";
</script>

<template>
    <main class="lg:flex">
        <div class="lg:w-3/4 px-8">
            <slot name="custom-main-part"></slot>
        </div>
        <div class="lg:w-1/4">
            <slot name="custom-side-bar"></slot>
            <fixed-side-bar/>
        </div>
    </main>
</template>

<style scoped lang="scss">

</style>