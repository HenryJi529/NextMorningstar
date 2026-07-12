<script lang="ts" setup>
import { createGithubOAuthState } from '@/utils/oauth';

const GITHUB_CLIENT_ID = import.meta.env.VITE_GITHUB_CLIENT_ID;

const openGithubAuthorizePage = () => {
    const params = new URLSearchParams({
        scope: 'user',
        client_id: GITHUB_CLIENT_ID,
        state: createGithubOAuthState(),
    });

    window.location.href = `https://github.com/login/oauth/authorize?${params}`;
};
</script>

<template>
    <div class="cursor-pointer" @click="openGithubAuthorizePage">
        <font-awesome-icon :icon="['fab', 'github']" />
    </div>
</template>

<style lang="scss" scoped></style>
