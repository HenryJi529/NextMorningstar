<script setup lang="ts">

import FixedSideBar from "@/components/blog/FixedSideBar.vue";
import {useOtherStore} from "@/stores/others";

const otherStore = useOtherStore();



</script>

<template>
    <main class="lg:flex">
        <div class="lg:w-3/4 px-8" :class="{hidden: otherStore.isMobileMenuOpen}">
            <slot name="custom-main-part"></slot>
        </div>
        <div class="lg:w-1/4 lg:block px-8 py-8 lg:px-4 lg:py-4" :class="{hidden: !otherStore.isMobileMenuOpen}">
            <slot name="custom-side-bar"></slot>
            <fixed-side-bar/>
        </div>
    </main>
</template>

<style scoped lang="scss">

</style>