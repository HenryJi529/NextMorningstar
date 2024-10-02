<script setup lang="ts">
import {useRoute} from "vue-router";

const route = useRoute();

</script>

<template>
    文章编辑{{route.params.id}}

</template>

<style scoped lang="scss">

</style>