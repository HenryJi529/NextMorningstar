<script lang="ts" setup>
import { RouterView } from 'vue-router';
import { getDefaultTheme, setTheme } from '@/utils/theme';

setTheme(getDefaultTheme());
</script>

<template>
    <div>
        <router-view />
    </div>
</template>

<style lang="scss" scoped></style>
