<script setup lang="ts">

import {useRoute} from "vue-router";
const route = useRoute();

</script>

<template>
    <div class="text-center text-3xl mt-10">自由练习: {{route.meta.verboseName}}</div>
    <div class="mx-auto container flex justify-center items-center p-10">
        <router-view></router-view>
    </div>
</template>

<style scoped lang="scss">

</style>