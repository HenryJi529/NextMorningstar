<script lang="ts" setup>
import {ref, watch} from "vue";
import {Spin as ASpin} from 'ant-design-vue'
import * as XLSX from 'xlsx';
import axios from '@/axios/index';

const selectedFile = ref<File>();
const frontendDuration = ref();
const backendTransferDuration = ref();
const backendParseDuration = ref();

const handleFileChange = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!(target.files && target.files.length > 0)) {
        return;
    }
    selectedFile.value = target.files[0];
}

const parseInFrontend = () => {
    const startTime = new Date();
    if (!selectedFile.value) {
        return;
    }
    const reader = new FileReader();
    reader.onload = function (e: ProgressEvent<FileReader>) {
        const data = new Uint8Array(e?.target?.result as ArrayBuffer);
        const workbook = XLSX.read(data, {type: 'array'});

        // 获取第一个工作表
        const firstSheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[firstSheetName];

        // 将工作表转换为 JSON 对象
        const jsonData = XLSX.utils.sheet_to_json(worksheet, {blankrows: false});
        const endTime = new Date();
        frontendDuration.value = endTime.getTime() - startTime.getTime();
        console.log(jsonData); // 在控制台输出解析的数据
    };
    reader.readAsArrayBuffer(selectedFile.value);
}

const parseInBackend = async () => {
    if (!selectedFile.value) {
        return;
    }
    const startTime = new Date();
    const data = new FormData();
    data.append("file", selectedFile.value)
    const response = (await axios(
        {
            method: 'POST',
            url: '/api/practice/f',
            data: data,
        }
    )).data;
    const endTime = new Date();
    backendParseDuration.value = response.data.duration;
    backendTransferDuration.value = endTime.getTime() - startTime.getTime() - response.data.duration;
    console.log(response)
}


const clearMeasure = () => {
    frontendDuration.value = undefined;
    backendTransferDuration.value = undefined;
    backendParseDuration.value = undefined;
}


watch(selectedFile, (newValue) => {
    clearMeasure();
    if (newValue) {
        parseInFrontend();
        parseInBackend();
    }
})

</script>

<template>
    <div>
        <div>
            <input accept=".xlsx" type="file" @change="handleFileChange"/>
        </div>
        <div v-if="frontendDuration && backendParseDuration && backendTransferDuration">
            <div>前端解析耗时: {{ frontendDuration }}ms</div>
            <div>后端解析耗时: {{ backendTransferDuration }}ms(传输), {{ backendParseDuration }}ms(解析)</div>
        </div>
        <div v-else-if="selectedFile" class="flex justify-center items-center w-full">
            <a-spin size="large"/>
        </div>
        <div v-else class="text-green-600 dark:text-green-400">
            <div>请选择文件...</div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>