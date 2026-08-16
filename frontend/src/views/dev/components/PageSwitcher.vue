<script setup lang="ts">
import { LeftOutlined, RightOutlined } from '@ant-design/icons-vue';

/* 极简分页条:‹ 当前页 ›,悬浮显示总页数;放卡片标题行右侧,仅一页时自隐藏 */
defineProps<{
    pageNum: number;
    totalPageNum: number;
}>();
const emit = defineEmits<{
    change: [page: number];
}>();
</script>

<template>
    <div v-if="totalPageNum > 1" class="flex items-center gap-3">
        <button
            class="page-btn"
            :disabled="pageNum <= 1"
            title="上一页"
            @click="emit('change', pageNum - 1)">
            <left-outlined />
        </button>
        <span class="font-mono text-xs text-slate-500" :title="`共 ${totalPageNum} 页`">
            {{ pageNum }}
        </span>
        <button
            class="page-btn"
            :disabled="pageNum >= totalPageNum"
            title="下一页"
            @click="emit('change', pageNum + 1)">
            <right-outlined />
        </button>
    </div>
</template>

<style scoped lang="scss">
.page-btn {
    @apply text-slate-400 hover:text-orange-500 transition disabled:text-slate-200 disabled:cursor-not-allowed;
}
</style>
