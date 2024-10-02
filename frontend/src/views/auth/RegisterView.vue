<script lang="ts" setup>
import axios from '@/axios/index';
import {onMounted, ref, watch} from 'vue';
import {useRouter} from "vue-router";

import type {ImageCaptchaResponseVo, R} from '@/types/common';
import type {RegisterRequestVo, RegisterResponseVo} from '@/types/auth';
import {API_COMMON_CAPTCHA_IMAGE, API_USER_AUTH_REGISTER, API_USER_RANDOM_NICKNAME} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import StandaloneForm from "@/components/auth/StandaloneForm.vue";
import {getClientGeolocationCoords} from "@/utils/handleClient";
import {getPreRoute, removePreRoute} from "@/utils/handleRoute";
import OAuthLinks from "@/components/auth/OAuthLinks.vue";
import {CHECK_CODE_ERROR, CHECK_CODE_TIMEOUT} from "@/constants/ResponseCode";
import {useUserStore} from "@/stores/users";

const router = useRouter();

const imageData = ref<string>();
const hiddenPassword = ref(true);
const hiddenConfirmPassword = ref(true);
const formState = ref<RegisterRequestVo>({
    sessionId: '',
    code: '',
    username: '',
    password: '',
    confirmPassword: '',
    nickname: ''
});
const errorMessage = ref<string>("");
const isLoading = ref(false);

watch(
    () => [formState.value.code, formState.value.username, formState.value.password, formState.value.confirmPassword, formState.value.nickname],
    () => {
        errorMessage.value = "";
    }
)

const updateImageCaptcha = async () => {
    const data: ImageCaptchaResponseVo = (await axios.get(API_COMMON_CAPTCHA_IMAGE)).data.data;
    formState.value.sessionId = data.sessionId;
    imageData.value = data.imageData;
}

const setRandomNickname = async () => {
    formState.value.nickname = (await axios.get(API_USER_RANDOM_NICKNAME)).data.data;
}

const setRandomNicknameWithGeolocationCoords = async () => {
    const coords = await getClientGeolocationCoords();
    formState.value.nickname = (await axios.get(`${API_USER_RANDOM_NICKNAME}?latitude=${coords.latitude}&longitude=${coords.longitude}`)).data.data;
}

const register = async () => {
    isLoading.value = true;
    const response: R<RegisterResponseVo> = (await axios.post(API_USER_AUTH_REGISTER, formState.value)).data;
    console.log(response);
    if (response.code <= 0) {
        errorMessage.value = response.msg;
        if (response.code === CHECK_CODE_TIMEOUT || response.code === CHECK_CODE_ERROR) {
            await updateImageCaptcha();
        }
    } else {
        const registerResponseVo = response.data;
        localStorage.setItem(TOKEN, registerResponseVo.token);
        errorMessage.value = "";

        const preRoute = getPreRoute();
        if (preRoute) {
            removePreRoute();
            await router.push(preRoute);
        } else {
            await router.push('/');
        }
    }
    isLoading.value = false;
}

onMounted(() => {
    useUserStore().clear();
    updateImageCaptcha();
    setInterval(() => {
        updateImageCaptcha();
    }, 1000 * 60);
})
</script>

<template>
    <standalone-form :error-message="errorMessage">
        <div class="flex items-center">
            <label class="form-label" for="username">用户名</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'user']"/>
                <input v-model="formState.username" class="flex-1" name="username" placeholder="请输入用户名"
                       type="text">
            </div>
        </div>
        <div class="flex items-center">
            <label class="form-label" for="password">密 码</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'key']"/>
                <input v-model="formState.password" :type="hiddenPassword?'password': 'text'" class="flex-1"
                       name="password" placeholder="请输入密码">
                <font-awesome-icon :icon="hiddenPassword? ['fas', 'eye-slash']: ['fas', 'eye']"
                                   class="cursor-pointer" @click="hiddenPassword = !hiddenPassword"/>
            </div>
        </div>
        <div class="flex items-center">
            <label class="form-label" for="password">确认密码</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'key']"/>
                <input v-model="formState.confirmPassword" :type="hiddenConfirmPassword?'password': 'text'"
                       class="flex-1"
                       name="confirmPassword" placeholder="请确认密码">
                <font-awesome-icon :icon="hiddenConfirmPassword? ['fas', 'eye-slash']: ['fas', 'eye']"
                                   class="cursor-pointer" @click="hiddenConfirmPassword = !hiddenConfirmPassword"/>
            </div>
        </div>
        <div class="flex items-center">
            <label class="form-label" for="nickname">昵 称</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'mask']"/>
                <input v-model="formState.nickname" class="flex-1" name="nickname" placeholder="请输入昵称" type="text">
                <font-awesome-icon :icon="['fas', 'location-dot']" class="cursor-pointer"
                                   @click="setRandomNicknameWithGeolocationCoords"/>
                <font-awesome-icon :icon="['fas', 'dice']" class="cursor-pointer" @click="setRandomNickname"/>
            </div>
        </div>
        <div class="flex items-center">
            <label class="form-label" for="code">验证码</label>
            <div class="flex-1 items-center flex space-x-3">
                <div class="input input-bordered w-3/5 flex items-center space-x-2">
                    <font-awesome-icon :icon="['fas', 'shield']"/>
                    <input v-model="formState.code" class="flex-1" name="code" placeholder="请输入验证码" type="text">
                </div>
                <div class="w-2/5 h-[3rem]">
                    <img :src="imageData" alt="captcha" class="w-full h-full rounded-lg" @click="updateImageCaptcha">
                </div>
            </div>
        </div>
        <button :disabled="isLoading" class="btn" type="button" @click="register">注 册</button>
        <div class="flex items-center justify-around">
            <button class="btn btn-ghost rounded-lg" type="button" @click="router.push({name: 'auth-login'})">登录账号
            </button>
            <div class="text-2xl flex items-center justify-center space-x-2">
                <o-auth-links/>
            </div>
            <button class="btn btn-ghost rounded-lg" type="button"
                    @click="router.push({name: 'auth-recover-initiate'})">找回密码
            </button>
        </div>
    </standalone-form>
</template>

<style lang="scss" scoped>
.form-label {
    @apply inline-block text-center w-16 mx-8 font-bold;
}
</style>