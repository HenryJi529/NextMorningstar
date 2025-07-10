<script setup lang="ts">
import { ref } from 'vue';
import axios from "axios";
import type {R} from "@/types/common";


const file = ref<File>();

const handleFileChange = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!(target.type === 'file' && target.files && target.files.length > 0)) {
        return;
    }
    file.value = target.files[0];
}

const uploadFile = async () => {
    const formData = new FormData();
    formData.append('file', file.value as File);
    formData.append('description', "这是个大文件");
    // NOTE: 上传大文件时前端请求会失败，后端没有问题
    const response: R<object> = (await axios.post("/api/practice/e", formData)).data;
    if(response.code <= 0){
        alert(response.msg);
    }else{
        alert(JSON.stringify(response.data));
    }
}

</script>

<template>
    <input type="file" @change="handleFileChange">
    <button class="btn" @click="uploadFile">上传文件</button>
</template>

<style scoped lang="scss">
</style>