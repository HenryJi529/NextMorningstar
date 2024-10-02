<script setup lang="ts">
import {RouterView} from "vue-router";
import {onMounted, ref} from "vue";
import {useUserStore} from "@/stores/users";

const userStore = useUserStore();
const isUserLoaded = ref<boolean>(false);

onMounted(async ()=>{
    await userStore.loadUser();
    isUserLoaded.value = true;
})

</script>

<template>
  <div v-if="isUserLoaded">
    <router-view></router-view>
  </div>
</template>

<style scoped lang="scss">

</style>
