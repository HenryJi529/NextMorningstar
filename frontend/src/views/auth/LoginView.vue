<script lang="ts" setup>
import axios from '@/axios/index';
import {onMounted, ref, watch} from 'vue';
import {useRouter} from "vue-router";

import type {ImageCaptchaResponseVo, R} from '@/types/common';
import type {LoginRequestVo, LoginResponseVo} from '@/types/auth';
import {API_COMMON_CAPTCHA_IMAGE, API_USER_AUTH_LOGIN} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import StandaloneForm from "@/components/auth/StandaloneForm.vue";
import {getPreRoute, removePreRoute} from "@/utils/handleRoute";
import OAuthLinks from "@/components/auth/OAuthLinks.vue";
import {CHECK_CODE_ERROR, CHECK_CODE_TIMEOUT} from "@/constants/ResponseCode";
import {useUserStore} from "@/stores/users";

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
    if (response.code <= 0) {
        errorMessage.value = response.msg;
        if (response.code === CHECK_CODE_TIMEOUT || response.code === CHECK_CODE_ERROR) {
            await updateImageCaptcha();
        }
    } else {
        const loginResponseVo = response.data;
        localStorage.setItem(TOKEN, loginResponseVo.token);
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
            <label class="form-label" for="username">账 号</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'user']"/>
                <input v-model="formState.account" class="w-full" name="username" placeholder="请输入用户名/邮箱"
                       type="text">
            </div>
        </div>
        <div class="flex items-center">
            <label class="form-label" for="password">密 码</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'key']"/>
                <input v-model="formState.password" :type="hiddenPassword?'password': 'text'" class="w-full"
                       name="password" placeholder="请输入密码">
                <font-awesome-icon :icon="hiddenPassword? ['fas', 'eye-slash']: ['fas', 'eye']"
                                   class="cursor-pointer" @click="hiddenPassword = !hiddenPassword"/>
            </div>
        </div>
        <div class="flex items-center">
            <label class="form-label" for="code">验证码</label>
            <div class="flex-1 items-center flex space-x-3">
                <div class="input input-bordered w-3/5 flex items-center space-x-2">
                    <font-awesome-icon :icon="['fas', 'shield']"/>
                    <input v-model="formState.code" class="w-full" name="code" placeholder="请输入验证码" type="text">
                </div>
                <div class="w-2/5 h-[3rem]">
                    <img :src="imageData" alt="captcha" class="w-full h-full rounded-lg" @click="updateImageCaptcha">
                </div>
            </div>
        </div>
        <button :disabled="isLoading" class="btn" type="button" @click="login">登 录</button>
        <div class="flex items-center justify-around">
            <button class="btn btn-ghost rounded-lg" type="button" @click="router.push({name: 'auth-register'})">
                注册账号
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

