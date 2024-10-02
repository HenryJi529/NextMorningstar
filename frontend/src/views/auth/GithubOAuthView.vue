<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import axios from "axios";
import {API_USER_AUTH_OAUTH2_GITHUB} from "@/constants/ApiConstant";
import type {OAuthResponseVo} from "@/types/auth";
import type {R} from "@/types/common";
import {TOKEN} from "@/constants/LocalStorageConstant";
import {getPreRoute, removePreRoute} from "@/utils/handleRoute";

const router = useRouter();
const route = useRoute();
const code = route.query.code;
const message = ref("认证中...");

onMounted(async ()=>{
    const response: R<OAuthResponseVo> = (await axios.get(`${API_USER_AUTH_OAUTH2_GITHUB}?code=${code}`)).data;
    console.log(response);
    if(response.code <= 0){
        message.value = "认证失败...";
    }else{
        localStorage.setItem(TOKEN, response.data.token);
        const preRoute = getPreRoute();
        if(preRoute){
            removePreRoute();
            await router.push(preRoute);
        }else{
            await router.push('/');
        }
    }
})

</script>

<template>
    <div class="text-5xl mx-auto flex justify-center items-center">
        <span>
            {{message}}
        </span>
    </div>
</template>

<style scoped lang="scss">

</style>