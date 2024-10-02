<script setup lang="ts">
import axios from 'axios';
import {onMounted, ref} from 'vue';
import {useRouter} from "vue-router";

import type {R, CaptchaResponseVo} from '@/types/common';
import type {RegisterRequestVo, RegisterResponseVo} from '@/types/auth';
import {API_COMMON_CAPTCHA, API_USER_RANDOM_NICKNAME, API_USER_AUTH_REGISTER} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import StandaloneForm from "@/components/auth/StandaloneForm.vue";
import {getClientGeolocationCoords} from "@/utils/handleClient";
import {getPreRoute, removePreRoute} from "@/utils/handleRoute";

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
    nickname: '',
    sex: "男"
});
const errorMessage = ref<string>("");
const isLoading = ref(false);

const updateCaptcha = async () => {
    const data: CaptchaResponseVo = (await axios.get(API_COMMON_CAPTCHA)).data.data;
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
    if(response.code != 1){
        errorMessage.value = response.msg;
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
    updateCaptcha();
    setInterval(()=>{
        updateCaptcha();
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
            <label for="password" class="form-label">密码</label>
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
            <label for="nickname" class="form-label">昵称</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'mask']" />
                <input type="text" name="nickname" placeholder="请输入昵称" v-model="formState.nickname" class="flex-1">
                <font-awesome-icon :icon="['fas', 'location-dot']" @click="setRandomNicknameWithGeolocationCoords" class="cursor-pointer"/>
                <font-awesome-icon :icon="['fas', 'dice']" @click="setRandomNickname" class="cursor-pointer"/>
            </div>
        </div>
        <div class="flex items-center">
            <label for="sex" class="form-label">性别</label>
            <div class="flex-1 flex items-center justify-around px-6">
                <div class="flex items-center space-x-2">
                    <span class="space-x-1">
                        <span class="label-text">男</span>
                        <font-awesome-icon :icon="['fas', 'mars']" />
                    </span>
                    <input type="radio" name="radio-1" class="radio" value="男" v-model="formState.sex"/>
                </div>
                <div class="flex items-center space-x-2">
                    <span class="space-x-1">
                        <span class="label-text">女</span>
                        <font-awesome-icon :icon="['fas', 'venus']" />
                    </span>
                    <input type="radio" name="radio-1" class="radio" value="女" v-model="formState.sex"/>
                </div>
            </div>
        </div>
        <div class="hidden">
            <input type="text" name="sessionId" v-model="formState.sessionId">
        </div>
        <div class="flex items-center">
            <label for="code" class="form-label">验证码</label>
            <div class="flex-1 items-center flex space-x-3">
                <div class="input input-bordered w-3/5 flex items-center space-x-2">
                    <font-awesome-icon :icon="['fas', 'shield']" />
                    <input type="text" name="code" placeholder="请输入验证码" v-model="formState.code" class="flex-1">
                </div>
                <div class="w-2/5 h-[3rem]">
                    <img :src="imageData" alt="captcha" @click="updateCaptcha" class="w-full h-full rounded-lg">
                </div>
            </div>
        </div>
        <button type="button" @click="register" class="btn" :disabled="isLoading">注 册</button>
        <div class="flex items-center justify-around">
            <button type="button" class="btn btn-ghost rounded-lg">其他登录</button>
            <button type="button" class="btn btn-ghost rounded-lg" @click="router.push({name: 'auth-login'})">登录账号</button>
            <button type="button" class="btn btn-ghost rounded-lg">找回密码</button>
        </div>
    </standalone-form>
</template>

<style scoped lang="scss">
.form-label {
    @apply inline-block text-center w-16 mx-8 font-bold;
}
</style>