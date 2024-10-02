<script setup lang="ts">
import {ref, watch} from 'vue';
import axios from "axios";
import type {R} from "@/types/common";
import {setTheme} from "@/utils/handleTheme";

setTheme("light");
const selectedFile = ref<File>();
const isDragOver = ref(false);
const handleDrop = (event: DragEvent) => {
    isDragOver.value = false;
    const droppedFiles = event.dataTransfer?.files;
    if(droppedFiles){
        selectedFile.value = droppedFiles[0];
    }
};

const handlePaste = (event: ClipboardEvent) => {
    const pastedFiles = event.clipboardData?.files;
    if (pastedFiles){
        selectedFile.value = pastedFiles[0];
    }
};

const uploadFile = async () => {
    const formData = new FormData();
    formData.append('file', selectedFile.value as File);
    formData.append('description', "这是个大文件");
    // NOTE: 上传大文件时前端请求会失败，后端没有问题
    const response: R<object> = (await axios.post("/api/practice/e", formData)).data;
    selectedFile.value = undefined;
    if(response.code <= 0){
        alert(response.msg);
    }else{
        alert(JSON.stringify(response.data));
    }
}

const handleFileChange = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!(target.files && target.files.length > 0)) {
        return;
    }
    selectedFile.value = target.files[0];
    target.value = "";
}

watch(selectedFile, async ()=>{
    if(selectedFile.value === undefined){
        return;
    }
    await uploadFile();
})


</script>

<template>
    <div class="flex justify-center items-center border-dashed border rounded-2xl h-60 w-80"
         :class="isDragOver ? 'border-blue-300 bg-blue-200' : 'border-gray-500 bg-gray-200'"
         @dragover.prevent="isDragOver = true"
         @dragleave="isDragOver = false"
         @drop.prevent="handleDrop"
         @paste.prevent="handlePaste">
        <input type="file" class="hidden" accept=".pdf, .xls, .xlsx, .jpg, .jpeg, .png" id="uploadInput"
               @change="handleFileChange">
        <div class="flex flex-col items-center">
            <label class="p-4 text-white bg-blue-500 px-8 cursor-pointer rounded-xl" for="uploadInput">上传文件</label>
            <div class="text-sm py-3 font-bold">或拖拽、复制文件到此处</div>
            <div class="text-sm">支持：PDF、Excel、JPG、PNG</div>
        </div>
    </div>
</template>

<style scoped lang="scss">
</style>