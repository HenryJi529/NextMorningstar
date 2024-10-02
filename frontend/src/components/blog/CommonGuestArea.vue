<script lang="ts" setup>

import FixedSideBar from "@/components/blog/FixedSideBar.vue";
import {useBlogStore} from "@/stores/blog";

const blogStore = useBlogStore();


</script>

<template>
    <main class="lg:flex">
        <div :class="{hidden: blogStore.isMobileMenuOpen}" class="lg:w-3/4 px-8">
            <slot name="custom-main-part"></slot>
        </div>
        <div :class="{hidden: !blogStore.isMobileMenuOpen}" class="lg:w-1/4 lg:block px-8 py-8 lg:px-4 lg:py-4">
            <slot name="custom-side-bar"></slot>
            <fixed-side-bar/>
        </div>
    </main>
</template>

<style lang="scss" scoped>

</style>