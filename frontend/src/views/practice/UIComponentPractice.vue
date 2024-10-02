<script setup lang="ts">
import BaseLayout from '@/views/practice/BaseLayout.vue';
import Dropdown from '@/views/practice/ui/Dropdown.vue';
import Tabs from '@/views/practice/ui/Tabs.vue';
import Carousel from '@/views/practice/ui/Carousel.vue';
import FileUploader from '@/views/practice/ui/FileUploader.vue';
import LinearProgressBar from '@/views/practice/ui/LinearProgressBar.vue';
import DatePicker from '@/views/practice/ui/DatePicker.vue';
import CustomTitle from '@/views/practice/ui/CustomTitle.vue';
import Modal from '@/views/practice/ui/Modal.vue';
import VirtualScroller from '@/views/practice/ui/VirtualScroller.vue';
import type { R } from '@/types/common';
import axios from '@/axios';
import { onMounted, onUnmounted } from 'vue';
import { getDefaultTheme, setTheme } from '@/utils/theme';
import { ResponseCode } from '@/constants/response';

const onFileUploaderUpdate = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('description', '这是个大文件');
    const response: R<object> = (
        await axios.post('/practice/upload-file-by-multipart-file', formData)
    ).data;
    if (response.code !== ResponseCode.SUCCESS) {
        alert(response.msg);
    } else {
        alert(JSON.stringify(response.data));
    }
};

onMounted(() => {
    setTheme('light');
});

onUnmounted(() => {
    setTheme(getDefaultTheme());
});
</script>

<template>
    <base-layout>
        <div class="box">
            <div class="title">下拉菜单</div>
            <div class="content">
                <dropdown />
            </div>
        </div>
        <div class="box">
            <div class="title">标签页</div>
            <div class="content">
                <tabs />
            </div>
        </div>
        <div class="box">
            <div class="title">轮播图</div>
            <div class="content">
                <carousel />
            </div>
        </div>
        <div class="box">
            <div class="title">文件上传【选择、拖拽、复制、筛选、限大】</div>
            <div class="content">
                <file-uploader
                    @update="onFileUploaderUpdate"
                    :allowed-extensions="['.png', '.xls', '.xlsx', '.jpeg', '.jpg', '.pdf']">
                    <template v-slot:type-notice>
                        <div class="text-sm">支持：PNG、Excel、JPEG、PDF</div>
                    </template>
                </file-uploader>
            </div>
        </div>
        <div class="box">
            <div class="title">线性进度条</div>
            <div class="content">
                <linear-progress-bar
                    active-color="#7a4678"
                    default-color="#12121240"
                    :precent="60"
                    :radius="8"
                    :height="20"
                    :width="300" />
            </div>
        </div>
        <div class="box">
            <div class="title">日期选择器</div>
            <div class="content">
                <date-picker />
            </div>
        </div>
        <div class="box">
            <div class="title">标题</div>
            <div class="content">
                <custom-title />
            </div>
        </div>
        <div class="box">
            <div class="title">模态窗</div>
            <div class="content">
                <modal class="w-full" />
            </div>
        </div>
        <div class="box">
            <div class="title">虚拟滚动</div>
            <div class="content">
                <virtual-scroller class="w-full" />
            </div>
        </div>
    </base-layout>
</template>

<style scoped lang="scss">
.box {
    @apply mb-8;
}
.title {
    @apply text-lg font-bold;
}
.content {
    @apply flex justify-center items-center w-[36rem] p-4 rounded-xl border-solid border-2;
}
</style>
