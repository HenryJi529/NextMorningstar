<script setup lang="ts">
import axios from 'axios';
import {onMounted, ref, watch} from 'vue';
import {useRouter} from "vue-router";

import type {R, ImageCaptchaResponseVo} from '@/types/common';
import type {RegisterRequestVo, RegisterResponseVo} from '@/types/auth';
import {API_COMMON_CAPTCHA_IMAGE, API_USER_RANDOM_NICKNAME, API_USER_AUTH_REGISTER} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import StandaloneForm from "@/components/auth/StandaloneForm.vue";
import {getClientGeolocationCoords} from "@/utils/handleClient";
import {getPreRoute, removePreRoute} from "@/utils/handleRoute";
import OAuthLinks from "@/components/auth/OAuthLinks.vue";
import {CHECK_CODE_ERROR, CHECK_CODE_TIMEOUT} from "@/constants/ResponseCode";

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
    },{
        deep: true // 启用深度监听
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
    if(response.code <= 0){
        errorMessage.value = response.msg;
        if(response.code === CHECK_CODE_TIMEOUT || response.code === CHECK_CODE_ERROR){
            await updateImageCaptcha();
        }
    }else{
        const registerResponseVo = response.data;
        localStorage.setItem(TOKEN, registerResponseVo.token);
        errorMessage.value = "";

        const preRoute = getPreRoute();
        if(preRoute){
            removePreRoute();
            await router.push(preRoute);
        }else{
            await router.push('/');
        }
    }
    isLoading.value = false;
}

onMounted(() => {
    updateImageCaptcha();
    setInterval(()=>{
        updateImageCaptcha();
    },1000*60);
})
</script>

<template>
    <standalone-form :error-message="errorMessage">
        <div class="flex items-center">
            <label for="username" class="form-label">用户名</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'user']" />
                <input type="text" name="username" placeholder="请输入用户名" v-model="formState.username" class="flex-1">
            </div>
        </div>
        <div class="flex items-center">
            <label for="password" class="form-label">密 码</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'key']" />
                <input :type="hiddenPassword?'password': 'text'" name="password" placeholder="请输入密码" v-model="formState.password" class="flex-1">
                <font-awesome-icon :icon="hiddenPassword? ['fas', 'eye-slash']: ['fas', 'eye']" @click="hiddenPassword = !hiddenPassword" class="cursor-pointer"/>
            </div>
        </div>
        <div class="flex items-center">
            <label for="password" class="form-label">确认密码</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'key']" />
                <input :type="hiddenConfirmPassword?'password': 'text'" name="confirmPassword" placeholder="请确认密码" v-model="formState.confirmPassword" class="flex-1">
                <font-awesome-icon :icon="hiddenConfirmPassword? ['fas', 'eye-slash']: ['fas', 'eye']" @click="hiddenConfirmPassword = !hiddenConfirmPassword" class="cursor-pointer"/>
            </div>
        </div>
        <div class="flex items-center">
            <label for="nickname" class="form-label">昵 称</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'mask']" />
                <input type="text" name="nickname" placeholder="请输入昵称" v-model="formState.nickname" class="flex-1">
                <font-awesome-icon :icon="['fas', 'location-dot']" @click="setRandomNicknameWithGeolocationCoords" class="cursor-pointer"/>
                <font-awesome-icon :icon="['fas', 'dice']" @click="setRandomNickname" class="cursor-pointer"/>
            </div>
        </div>
        <div class="flex items-center">
            <label for="code" class="form-label">验证码</label>
            <div class="flex-1 items-center flex space-x-3">
                <div class="input input-bordered w-3/5 flex items-center space-x-2">
                    <font-awesome-icon :icon="['fas', 'shield']" />
                    <input type="text" name="code" placeholder="请输入验证码" v-model="formState.code" class="flex-1">
                </div>
                <div class="w-2/5 h-[3rem]">
                    <img :src="imageData" alt="captcha" @click="updateImageCaptcha" class="w-full h-full rounded-lg">
                </div>
            </div>
        </div>
        <button type="button" @click="register" class="btn" :disabled="isLoading">注 册</button>
        <div class="flex items-center justify-around">
            <button type="button" class="btn btn-ghost rounded-lg" @click="router.push({name: 'auth-login'})">登录账号</button>
            <div class="text-2xl flex items-center justify-center space-x-2">
                <OAuthLinks/>
            </div>
            <button type="button" class="btn btn-ghost rounded-lg" @click="router.push({name: 'auth-recover-initiate'})">找回密码</button>
        </div>
    </standalone-form>
</template>

<style scoped lang="scss">
.form-label {
    @apply inline-block text-center w-16 mx-8 font-bold;
}
</style>