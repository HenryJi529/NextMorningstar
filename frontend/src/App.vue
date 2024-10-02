<script setup lang="ts">
import {RouterView} from "vue-router";
import {autoUpdateTheme} from "@/utils/handleTheme";

autoUpdateTheme();

</script>

<template>
    <div>
        <router-view/>
    </div>
</template>

<style scoped lang="scss">

</style>
