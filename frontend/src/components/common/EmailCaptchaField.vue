<script setup lang="ts">

import CountDown from "@/components/common/CountDown.vue";
import {ref} from "vue";
import type {R} from "@/types/common";
import axios from "axios";
import {API_COMMON_CAPTCHA_EMAIL} from "@/constants/ApiConstant";
import {TOKEN} from "@/constants/LocalStorageConstant";

interface Data {
    code: string;
    email: string;
}

const props = defineProps<{
    data: Data
}>()

const emit = defineEmits(['error']);

const isEmailCaptchaJustSent = ref(false);
const isEmailCaptchaSending = ref(false);

const getEmailCaptcha = async () => {
    isEmailCaptchaSending.value = true;
    const response: R<object> = (await axios.get(`${API_COMMON_CAPTCHA_EMAIL}?email=${props.data.email}`, {
        headers: {
            Authorization: localStorage.getItem(TOKEN)
        }
    })).data;
    isEmailCaptchaSending.value = false;
    if(response.code <= 0){
        emit('error', response.msg);
        return;
    }
    isEmailCaptchaJustSent.value = true;
    setInterval(()=>{
        isEmailCaptchaJustSent.value = false;
    }, 1000 * 60)
}

</script>

<template>
    <div class="w-full flex items-center">
        <div class="ml-2 mr-4">
            <div class="cursor-pointer w-4" @click="!isEmailCaptchaSending && !isEmailCaptchaJustSent && getEmailCaptcha()">
                <div v-if="isEmailCaptchaSending">
                    <span class="loading loading-ring loading-xs"></span>
                </div>
                <div v-else-if="isEmailCaptchaJustSent">
                    <count-down :count="60"/>
                </div>
                <div v-else>
                    <font-awesome-icon :icon="['fas', 'paper-plane']" class="text-lg" />
                </div>
            </div>
        </div>
        <div class="flex-1 input input-bordered flex items-center space-x-2">
            <font-awesome-icon :icon="['fas', 'shield']" />
            <input type="text" name="code" placeholder="请输入验证码" v-model="data.code" class="flex-1">
        </div>
    </div>

</template>

<style scoped lang="scss">

</style>