<script setup lang="ts">
const GITHUB_CLIENT_ID = import.meta.env.VITE_GITHUB_CLIENT_ID;

</script>

<template>
    <a :href="`https://github.com/login/oauth/authorize?scope=user&client_id=${GITHUB_CLIENT_ID}`">
        <font-awesome-icon :icon="['fab', 'github']" />
    </a>
</template>

<style scoped lang="scss">

</style>