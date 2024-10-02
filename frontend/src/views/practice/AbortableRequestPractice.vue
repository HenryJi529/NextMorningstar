<script lang="ts" setup>

import axios from '@/axios/index';
import {ref} from "vue";
import {Spin as ASpin} from "ant-design-vue";

const isRequesting = ref(false);
const controller = new AbortController();
const request = async () => {
    isRequesting.value = true;
    const response = (await axios.post("/api/practice/g", {}, {signal: controller.signal})).data;
    isRequesting.value = false;
    console.log(response)
}

const abort = () => {
    controller.abort();
    isRequesting.value = false;
}

</script>

<template>
    <div class="w-[20rem]">
        <div class="w-full flex justify-between items-center mb-8">
            <button class="btn" @click="request">发送</button>
            <button class="btn" @click="abort">取消</button>
        </div>
        <div class="w-full flex justify-center items-center">
            <a-spin v-if="isRequesting" size="large"/>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>