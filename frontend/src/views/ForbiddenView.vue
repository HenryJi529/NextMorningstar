<script setup lang="ts">
import ErrorView from "@/views/ErrorView.vue";
</script>

<template>
    <error-view>
        <template #default>
            403: Forbidden.
        </template>
    </error-view>
</template>

<style scoped lang="scss">

</style>