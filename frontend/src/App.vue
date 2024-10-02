<script setup lang="ts">
import {RouterView} from "vue-router";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";

setTheme(getDefaultTheme());

</script>

<template>
  <div>
    <router-view/>
  </div>
</template>

<style scoped lang="scss">

</style>
