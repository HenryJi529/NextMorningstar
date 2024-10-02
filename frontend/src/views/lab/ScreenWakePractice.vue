<script setup lang="ts">
import { useWakeLock } from '@/composables/wakeLock';
import BaseLayout from '@/views/lab/BaseLayout.vue';

const { enableScreenWake, shouldActive, disableScreenWake } = useWakeLock();

const toggleScreenWake = () => {
    if (shouldActive.value) {
        disableScreenWake();
    } else {
        enableScreenWake();
    }
};
</script>

<template>
    <base-layout>
        <div class="flex justify-center items-center">
            <div class="btn" @click="toggleScreenWake()">
                <span v-if="shouldActive"> 关闭屏幕常亮 </span>
                <span v-else> 开启屏幕常亮 </span>
            </div>
        </div>
    </base-layout>
</template>

<style scoped lang="scss"></style>
