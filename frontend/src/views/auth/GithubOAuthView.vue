<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from '@/axios/index';
import { API_USER_AUTH_OAUTH2_GITHUB } from '@/constants/api';
import type { OAuthResponseVo } from '@/types/auth';
import type { R } from '@/types/common';
import { LocalStorageKey } from '@/constants/storage';
import { getPreRoute, removePreRoute } from '@/utils/route';
import { useUserStore } from '@/stores/users';

const router = useRouter();
const route = useRoute();
const code = route.query.code;
const message = ref('认证中...');

onMounted(async () => {
    useUserStore().clear();
    const response: R<OAuthResponseVo> = (
        await axios.get(`${API_USER_AUTH_OAUTH2_GITHUB}?code=${code}`)
    ).data;
    console.log(response);
    if (response.code <= 0) {
        message.value = response.msg;
    } else {
        localStorage.setItem(LocalStorageKey.TOKEN, response.data.token);
        const preRoute = getPreRoute();
        if (preRoute) {
            removePreRoute();
            await router.push(preRoute);
        } else {
            await router.push('/');
        }
    }
});
</script>

<template>
    <div class="text-5xl mx-auto flex justify-center items-center">
        <span>
            {{ message }}
        </span>
    </div>
</template>

<style lang="scss" scoped></style>
