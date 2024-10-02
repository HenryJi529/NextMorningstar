<script lang="ts" setup>
import {useUserStore} from "@/stores/users";
import router from "@/router";
import ChatWindow from "@/components/kill/ChatWindow.vue";


const userStore = useUserStore();

</script>

<template>
    仿三国杀
    <button class="btn" @click="userStore.handleLogout();router.go(0)">logout</button>
    <chat-window/>
</template>

<style lang="scss" scoped>

</style>