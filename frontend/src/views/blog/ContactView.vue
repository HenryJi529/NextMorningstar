<script setup lang="ts">
import axios from "axios";
import CommonGuestArea from "@/components/blog/CommonGuestArea.vue";
import {useUserStore} from "@/stores/users";
import {reactive, ref, watch} from "vue";
import type {R} from "@/types/common";
import {API_CONTACT_EMAIL} from "@/constants/ApiConstant";
import type {EmailContactRequestVo} from "@/types/contact";
import AlertError from "@/components/common/AlertError.vue";
import AlertSuccess from "@/components/common/AlertSuccess.vue";

const userStore = useUserStore();

const isSubmitting = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const emailContactRequestVo = reactive<EmailContactRequestVo>({
    email: userStore.email !== "" ? userStore.email : "",
    subject: "",
    content: "",
})

watch(emailContactRequestVo, (newValue) => {
    errorMessage.value = "";
    console.log(`newValue is ${newValue}`);
})

const sendEmailToAdmin = async () => {
    isSubmitting.value = true;
    const response: R<object> = (await axios.post(
        API_CONTACT_EMAIL,
        emailContactRequestVo
    )).data;
    isSubmitting.value = false;

    if (response.code <= 0){
        errorMessage.value = response.msg;
    }else{
        successMessage.value = "发送成功";
        setTimeout(()=> {
            successMessage.value = "";
        }, 3000)
        emailContactRequestVo.subject = "";
        emailContactRequestVo.content = "";
    }
}


</script>

<template>
    <common-guest-area>
        <template #custom-main-part>
            <div class="p-4">
                <div class="text-2xl pb-3 font-bold text-center">联系站长</div>

                <div class="my-4">
                    <alert-error v-if="errorMessage">{{errorMessage}}</alert-error>
                    <alert-success v-if="successMessage">{{successMessage}}</alert-success>
                </div>

                <div class="flex flex-col space-y-3">
                    <input v-model="emailContactRequestVo.email" type="email" placeholder="邮箱" class="input input-ghost w-full rounded-xl" name="email" :disabled="userStore.email != ''" />
                    <input v-model="emailContactRequestVo.subject" type="text" placeholder="主题" class="input input-ghost w-full rounded-xl" name="subject" required />
                    <textarea v-model="emailContactRequestVo.content" class="textarea w-full rounded-xl" rows="7" name="message" placeholder="字数太少站长收不到哟～"
                              required>
                    </textarea>
                    <div class="btn rounded-xl text-lg">
                        <span class="w-full" v-if="!isSubmitting" @click="sendEmailToAdmin">发送</span>
                        <span v-else class="loading loading-spinner loading-sm"></span>
                    </div>
                </div>
            </div>
        </template>
    </common-guest-area>
</template>

<style scoped lang="scss">

</style>