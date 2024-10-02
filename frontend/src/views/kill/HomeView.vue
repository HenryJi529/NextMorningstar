<script setup lang="ts">
import Profile from "@/components/kill/Profile.vue";
import {useUserStore} from "@/stores/users";
import router from "@/router";


const userStore = useUserStore();

</script>

<template>
    晨星杀
    <button class="btn" @click="userStore.handleLogout();router.go(0)">logout</button>
    <div class="w-3/5 border-2 border-gray-200">
        <profile/>
    </div>
</template>

<style scoped lang="scss">

</style>