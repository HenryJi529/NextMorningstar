<script setup lang="ts">

import LinearProgressBar from '@/components/common/LinearProgressBar.vue';
</script>

<template>
    <div>
        <div class="mb-5">
            <linear-progress-bar active-color="red" :precent="24" :radius="5" :height="10" :width="200"/>
        </div>
        <div class="mb-5">
            <linear-progress-bar active-color="blue" default-color="white" :precent="60" :radius="0" :height="20" :width="300"/>
        </div>
    </div>

</template>

<style scoped lang="scss">

</style>