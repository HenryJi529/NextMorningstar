<script lang="ts" setup>
import axios from 'axios';
import {ref, watch} from 'vue';
import { onMounted } from 'vue';
import {useRouter} from "vue-router";

import type {R, ImageCaptchaResponseVo} from '@/types/common';
import type {LoginRequestVo, LoginResponseVo } from '@/types/auth';
import {API_USER_AUTH_LOGIN, API_COMMON_CAPTCHA_IMAGE} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import StandaloneForm from "@/components/auth/StandaloneForm.vue";
import {getPreRoute, removePreRoute} from "@/utils/handleRoute";
import OAuthLinks from "@/components/auth/OAuthLinks.vue";
import {CHECK_CODE_ERROR, CHECK_CODE_TIMEOUT} from "@/constants/ResponseCode";

const router = useRouter();

const imageData = ref<string>();
const hiddenPassword = ref(true);
const formState = ref<LoginRequestVo>({
    sessionId: '',
    code: '',
    account: '',
    password: '',
});
const errorMessage = ref<string>("");
const isLoading = ref(false);

watch(
    () => [formState.value.code, formState.value.account, formState.value.password],
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

const login = async () => {
    isLoading.value = true;
    const response: R<LoginResponseVo> = (await axios.post(API_USER_AUTH_LOGIN, formState.value)).data;
    if(response.code <= 0){
        errorMessage.value = response.msg;
        if(response.code === CHECK_CODE_TIMEOUT || response.code === CHECK_CODE_ERROR){
            await updateImageCaptcha();
        }
    }else{
        const loginResponseVo = response.data;
        localStorage.setItem(TOKEN, loginResponseVo.token);
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
            <label for="username" class="form-label">账 号</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'user']" />
                <input type="text" name="username" placeholder="请输入用户名/邮箱" v-model="formState.account" class="w-full">
            </div>
        </div>
        <div class="flex items-center">
            <label for="password" class="form-label">密 码</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'key']" />
                <input :type="hiddenPassword?'password': 'text'" name="password" placeholder="请输入密码" v-model="formState.password" class="w-full">
                <font-awesome-icon :icon="hiddenPassword? ['fas', 'eye-slash']: ['fas', 'eye']" @click="hiddenPassword = !hiddenPassword" class="cursor-pointer"/>
            </div>
        </div>
        <div class="flex items-center">
            <label for="code" class="form-label">验证码</label>
            <div class="flex-1 items-center flex space-x-3">
                <div class="input input-bordered w-3/5 flex items-center space-x-2">
                    <font-awesome-icon :icon="['fas', 'shield']" />
                    <input type="text" name="code" placeholder="请输入验证码" v-model="formState.code" class="w-full">
                </div>
                <div class="w-2/5 h-[3rem]">
                    <img :src="imageData" alt="captcha" @click="updateImageCaptcha" class="w-full h-full rounded-lg">
                </div>
            </div>
        </div>
        <button type="button" @click="login" class="btn" :disabled="isLoading">登 录</button>
        <div class="flex items-center justify-around">
            <button type="button" class="btn btn-ghost rounded-lg" @click="router.push({name: 'auth-register'})">注册账号</button>
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

