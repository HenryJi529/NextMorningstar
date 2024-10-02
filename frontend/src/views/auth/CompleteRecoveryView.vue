<script setup lang="ts">
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import axios from "axios";
import {API_USER_AUTH_RECOVER_COMPLETE} from "@/constants/ApiConstant";
import type {CompleteRecoveryRequestVo, CompleteRecoveryResponseVo} from "@/types/auth";
import type {R} from "@/types/common";
import {getSingleParam} from "@/utils/handleHttp";
import {TOKEN} from "@/constants/LocalStorageConstant";
import {useUserStore} from "@/stores/users";
import router from "@/router";

const route = useRoute();
const userStore = useUserStore();
const isLoading = ref(false);
const isError = ref(false);
const errorMessage = ref("");

const verify = async () => {
    const completeRecoveryResponseVo : CompleteRecoveryRequestVo = {
        token: getSingleParam(route.query.token as string[]| string),
        id: getSingleParam(route.query.id as string[]| string)
    }
    isLoading.value = true;
    const response: R<CompleteRecoveryResponseVo> = (await axios.post(
        API_USER_AUTH_RECOVER_COMPLETE,
        completeRecoveryResponseVo)).data;
    if(response.code != 1){
        isError.value = true;
        errorMessage.value = response.msg;
        return;
    }
    localStorage.setItem(TOKEN, response.data.token);
    await userStore.loadUser();
    await router.push({name: 'index'});
}

onMounted(async () => {
    if('id' in route.query && 'token' in route.query){
        await verify();
    }
    isError.value = true;
    errorMessage.value = "请求参数错误: 必须包含id与token";
})

</script>

<template>
    <div v-if="isLoading">
        <span class="loading loading-spinner loading-lg"></span>
    </div>
    <div v-else class="text-3xl">
        <div v-if="isError">
            {{ errorMessage }}
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>