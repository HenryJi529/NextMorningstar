<script setup lang="ts">
import {reactive, ref, watch} from "vue";
import StandaloneForm from "@/components/auth/StandaloneForm.vue";
import router from "@/router";
import type {EmailContactRequestVo} from "@/types/contact";
import axios from "axios";
import {API_CONTACT_EMAIL, API_USER_AUTH_RECOVER_INITIATE} from "@/constants/ApiConstant";
import type {R} from "@/types/common";
import EmailCaptchaField from "@/components/common/EmailCaptchaField.vue";
import type {InitiateRecoveryRequestVo} from "@/types/auth";

const errorMessage = ref<string>("");
const successMessage = ref<string>("");
const isLoading = ref(false);

const clearMessage = () => {
    errorMessage.value = "";
    successMessage.value = "";
}

const initiateRecoveryRequestVo = reactive<InitiateRecoveryRequestVo>({
    email: "",
    code: ""
})

const emailContactRequestVo = reactive<EmailContactRequestVo>({
    email: "",
    subject: "账号恢复",
    content: "",
})

watch(emailContactRequestVo, ()=>{
    errorMessage.value = "";
    successMessage.value = "";
},{
    deep: true,
})

const initializeRecovery = async () => {
    isLoading.value = true;
    const response: R<object> = (await axios.post(API_USER_AUTH_RECOVER_INITIATE, initiateRecoveryRequestVo)).data;
    console.log(response);
    isLoading.value = false;
    if(response.code != 1){
        errorMessage.value = response.msg;
        return;
    }else{
        successMessage.value = "登录链接已发送";
    }
    setTimeout(clearMessage, 3000);
}

const contactAdmin = async () => {
    isLoading.value = true;
    const response: R<object> = (await axios.post(API_CONTACT_EMAIL, emailContactRequestVo)).data;
    console.log(response);
    isLoading.value = false;
    if(response.code != 1){
        errorMessage.value = response.msg;
    }else{
        successMessage.value = "私信发送成功";
    }
    setTimeout(clearMessage, 3000);
}

const handleSendEmailError = (message: string) => {
    errorMessage.value = message;
}

</script>

<template>
    <standalone-form :error-message="errorMessage" :success-message="successMessage">
        <div class="mb-3">
            <div>
                <div class="text-2xl text-center font-bold mb-2">通过邮件</div>
                <div class="flex-1 input input-bordered flex items-center space-x-2 mb-2">
                    <font-awesome-icon :icon="['fas', 'envelope']" />
                    <input type="text" placeholder="请输入邮箱" v-model="initiateRecoveryRequestVo.email" class="flex-1">
                </div>
                <email-captcha-field :data="initiateRecoveryRequestVo" @error="handleSendEmailError" class="mb-2"/>
                <div class="btn w-full" @click="!isLoading && initializeRecovery()">
                    <span v-if="!isLoading">提交验证</span>
                    <span v-else class="loading loading-spinner loading-sm"></span>
                </div>
            </div>
            <div class="divider text-xl">OR</div>
            <div class="w-full">
                <div class="text-2xl text-center font-bold mb-2">通过私信</div>
                <p class="indent-8 mb-2 dark:text-blue-200 text-green-600">
                    请联系站长并验明身份, 通过回复邮件中的链接重新登入。
                </p>
                <div class="flex-1 input input-bordered flex items-center space-x-2 mb-2">
                    <font-awesome-icon :icon="['fas', 'envelope']" />
                    <input type="text" placeholder="请输入邮箱" v-model="emailContactRequestVo.email" class="flex-1">
                </div>
                <textarea class="input input-bordered w-full p-2 h-[5rem]" v-model="emailContactRequestVo.content"></textarea>
                <div class="btn w-full" @click="!isLoading && contactAdmin()">
                    <span v-if="!isLoading">提交私信</span>
                    <span v-else class="loading loading-spinner loading-sm"></span>
                </div>
            </div>
        </div>

        <div class="flex items-center justify-around">
            <button type="button" class="btn btn-ghost rounded-lg" @click="router.push({name: 'auth-login'})">登录账号</button>
            <div class="text-2xl flex items-center justify-center space-x-2">
                <font-awesome-icon :icon="['fab', 'google']" />
                <font-awesome-icon :icon="['fab', 'github']" />
            </div>
            <button type="button" class="btn btn-ghost rounded-lg" @click="router.push({name: 'auth-recover-initiate'})">找回密码</button>
        </div>
    </standalone-form>
</template>

<style scoped lang="scss">

</style>