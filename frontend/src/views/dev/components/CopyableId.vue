<script setup lang="ts">
import { ref } from 'vue';
import { CheckOutlined, CopyOutlined } from '@ant-design/icons-vue';

const props = defineProps<{
    value: string;
    /** 展示文本，默认 value；调用方自行截断以保留现有样式 */
    display?: string;
}>();

const copied = ref(false);
let timer: number | undefined;

const copy = async () => {
    try {
        await navigator.clipboard.writeText(props.value);
    } catch {
        /* 非安全上下文降级：临时 textarea 走 execCommand */
        const ta = document.createElement('textarea');
        ta.value = props.value;
        ta.style.position = 'fixed';
        ta.style.opacity = '0';
        document.body.appendChild(ta);
        ta.select();
        document.execCommand('copy');
        document.body.removeChild(ta);
    }
    copied.value = true;
    window.clearTimeout(timer);
    timer = window.setTimeout(() => (copied.value = false), 1500);
};
</script>

<template>
    <span class="copyable-id" :title="copied ? '已复制' : '点击复制 runId'" @click.stop="copy">
        {{ display ?? value }}
        <check-outlined v-if="copied" class="icon-copied" />
        <copy-outlined v-else class="icon-copy" />
    </span>
</template>

<style scoped lang="scss">
.copyable-id {
    @apply inline-flex items-center gap-1 cursor-pointer select-none font-mono;
}

.icon-copy {
    @apply text-[10px] text-slate-400;
}

.copyable-id:hover .icon-copy {
    @apply text-slate-600;
}

.icon-copied {
    @apply text-[10px] text-emerald-500;
}
</style>
