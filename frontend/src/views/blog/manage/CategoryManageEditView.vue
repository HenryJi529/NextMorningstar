<script setup lang="ts">
import {useRoute} from "vue-router";

const route = useRoute();
</script>

<template>
    分类编辑{{route.params.id}}
</template>

<style scoped lang="scss">

</style>