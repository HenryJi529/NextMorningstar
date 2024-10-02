<script lang="ts" setup>
import axios from 'axios';
import { ref } from 'vue';
import { onMounted } from 'vue';
import {useRouter} from "vue-router";

import type {Response, CaptchaResponseVo} from '@/types/common';
import type {LoginRequestVo, LoginResponseVo } from '@/types/auth';
import {useUserStore} from "@/stores/users";
import {API_USER_LOGIN, API_COMMON_CAPTCHA} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";
import StandaloneForm from "@/components/auth/StandaloneForm.vue";

const router = useRouter();

const imageData = ref<string>();
const hiddenPassword = ref(true);
const formState = ref<LoginRequestVo>({
    sessionId: '',
    code: '',
    username: '',
    password: '',
});
const errorMessage = ref<string>("");
const isLoading = ref(false);

const updateCaptcha = async () => {
    const data: CaptchaResponseVo = (await axios.get(API_COMMON_CAPTCHA)).data.data;
    formState.value.sessionId = data.sessionId;
    imageData.value = data.imageData;
}

const login = async () => {
    isLoading.value = true;
    const response: Response = (await axios.post(API_USER_LOGIN, formState.value)).data;
    if(response.code != 1){
        errorMessage.value = response.msg;
    }else{
        const loginResponseVo = response.data as LoginResponseVo;
        localStorage.setItem(TOKEN, loginResponseVo.token);
        errorMessage.value = "";
        await useUserStore().loadUser();
        await router.push('/');
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
                <input type="text" name="username" placeholder="请输入用户名" v-model="formState.username" class="w-full">
            </div>
        </div>
        <div class="flex items-center">
            <label for="password" class="form-label">密码</label>
            <div class="input input-bordered flex-1 flex items-center space-x-2">
                <font-awesome-icon :icon="['fas', 'key']" />
                <input :type="hiddenPassword?'password': 'text'" name="password" placeholder="请输入密码" v-model="formState.password" class="w-full">
                <font-awesome-icon :icon="hiddenPassword? ['fas', 'eye-slash']: ['fas', 'eye']" @click="hiddenPassword = !hiddenPassword" class="cursor-pointer"/>
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
                    <input type="text" name="code" placeholder="请输入验证码" v-model="formState.code" class="w-full">
                </div>
                <div class="w-2/5 h-[3rem]">
                    <img :src="imageData" alt="captcha" @click="updateCaptcha" class="w-full h-full rounded-lg">
                </div>
            </div>
        </div>
        <button type="button" @click="login" class="btn" :disabled="isLoading">登 录</button>
        <div class="flex items-center justify-around">
            <button type="button" class="btn btn-ghost rounded-lg">其他登录</button>
            <button type="button" class="btn btn-ghost rounded-lg" @click="router.push('/auth/register')">注册账号</button>
            <button type="button" class="btn btn-ghost rounded-lg">找回密码</button>
        </div>
    </standalone-form>
</template>


<style scoped lang="scss">
.form-label {
    @apply inline-block text-center w-12 mx-8 font-bold;
}
</style>

