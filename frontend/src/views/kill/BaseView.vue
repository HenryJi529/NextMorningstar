<script lang="ts" setup>
import {useUserStore} from "@/stores/users";
import {onMounted} from "vue";
import {useRouter} from "vue-router";

import Profile from "@/components/Profile.vue";
const router = useRouter();
const userStore = useUserStore();

import {MORNINGSTAR_KILL_NAME} from "@/constants/SiteConstant";
import {useHead} from "@vueuse/head";
import ChatWindow from "@/components/kill/ChatWindow.vue";
import {getClientGeolocationCoords} from "@/utils/handleClient";


useHead({
    title: 'Kill - 晨星杀',
    meta: [
        {
            name: 'description',
            content: '解决了游卡Web三国杀诸多痛点的开源克隆版'
        }
    ]
});

const logout = () => {
    userStore.handleLogout();
    router.push('/auth/login');
}

onMounted(async ()=>{
    let clientGeolocationCoords = await getClientGeolocationCoords();
    console.log(clientGeolocationCoords);
    // TODO: 处理websocket问题
    if(userStore.username === ""){
        await router.push('/auth/login');
    }
})

</script>

<template>
  <div class="container h-screen mx-auto">
      {{MORNINGSTAR_KILL_NAME}}
      <button class="btn" @click="logout">logout</button>
    <div class="w-3/5 border-2 border-gray-200">
        <profile/>
    </div>
<!--    <chat-window></chat-window>-->
  </div>
</template>
