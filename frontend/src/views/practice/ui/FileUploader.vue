<script lang="ts" setup>
import { ref, watch } from 'vue';
import { message } from 'ant-design-vue';

const props = withDefaults(
    defineProps<{
        limitedSize?: number;
        allowedExtensions: string[];
    }>(),
    {
        limitedSize: 50,
    }
);

const selectedFile = ref<File>();
const isDragOver = ref(false);

const emit = defineEmits<{
    (e: 'update', file: File): void;
}>();

const validateFileExt = (file: File) => {
    for (const extension of props.allowedExtensions) {
        if (file.name.endsWith(extension)) {
            return true;
        }
    }
    return false;
};

const handleDrop = (event: DragEvent) => {
    isDragOver.value = false;
    const droppedFiles = event.dataTransfer?.files;
    if (droppedFiles) {
        if (!validateFileExt(droppedFiles[0])) {
            message.warn({
                content: '文件类型不支持',
                class: 'ant-message-notice-custom',
                duration: 2,
            });
            return;
        }
        selectedFile.value = droppedFiles[0];
    }
};

const handlePaste = (event: ClipboardEvent) => {
    const pastedFiles = event.clipboardData?.files;
    if (pastedFiles) {
        if (!validateFileExt(pastedFiles[0])) {
            message.warn({
                content: '文件类型不支持',
                class: 'ant-message-notice-custom',
                duration: 2,
            });
            return;
        }
        selectedFile.value = pastedFiles[0];
    }
};

const handleFileChange = (event: Event) => {
    const target = event.target as HTMLInputElement;
    if (!(target.files && target.files.length > 0)) {
        return;
    }
    if (!validateFileExt(target.files[0])) {
        message.warn({
            content: '文件类型不支持',
            class: 'ant-message-notice-custom',
            duration: 2,
        });
        return;
    }
    selectedFile.value = target.files[0];
    target.value = '';
};

watch(selectedFile, async () => {
    if (selectedFile.value === undefined) {
        return;
    }
    if (selectedFile.value.size > 1024 * 1024 * props.limitedSize) {
        message.warn({
            content: `文件大于${props.limitedSize}MB...`,
            class: 'ant-message-notice-custom',
            duration: 2,
        });
        selectedFile.value = undefined;
        return;
    }
    emit('update', selectedFile.value);
});
</script>

<template>
    <div
        :class="isDragOver ? 'border-blue-300 bg-blue-200' : 'border-gray-500 bg-gray-200'"
        class="flex justify-center items-center border-dashed border rounded-2xl h-60 w-80"
        @dragleave="isDragOver = false"
        @dragover.prevent="isDragOver = true"
        @drop.prevent="handleDrop"
        @paste.prevent="handlePaste">
        <input
            id="uploadInput"
            :accept="allowedExtensions.join(', ')"
            class="hidden"
            type="file"
            @change="handleFileChange" />
        <div class="flex flex-col items-center">
            <label
                class="p-4 text-white bg-blue-500 px-8 cursor-pointer rounded-xl"
                for="uploadInput"
                >上传文件</label
            >
            <slot name="action-notice">
                <div class="text-sm py-3 font-bold">或拖拽、复制文件到此处</div>
            </slot>
            <slot name="type-notice">
                <div class="text-sm">支持：{{ allowedExtensions.join('、') }}</div>
            </slot>
        </div>
    </div>
</template>

<style lang="scss" scoped></style>
