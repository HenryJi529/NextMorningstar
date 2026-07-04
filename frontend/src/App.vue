<script lang="ts" setup>
import { RouterView } from 'vue-router';
import { App as AApp, ConfigProvider as AConfigProvider } from 'ant-design-vue';
import { getDefaultTheme, setTheme } from '@/utils/theme';
import zhCN from 'ant-design-vue/es/locale/zh_CN';

setTheme(getDefaultTheme());
</script>

<template>
    <a-config-provider :locale="zhCN">
        <a-app class="min-h-dvh">
            <router-view />
        </a-app>
    </a-config-provider>
</template>

<style lang="scss" scoped></style>
