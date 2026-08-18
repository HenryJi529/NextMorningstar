<script setup lang="ts">
import { computed, onUnmounted, ref } from 'vue';
import type { RunDetail } from '@/types/dev';
import { ActionStatus, RunState, RunStatus } from '@/types/dev';
import {
    ACTION_TYPE_LABEL,
    fmtElapsed,
    PIPELINE_NODES,
    stateLabel,
    stateToStep,
    triggerLabel,
} from '@/libs/dev';
import CopyableId from '@/views/dev/components/CopyableId.vue';

const props = withDefaults(
    defineProps<{
        run?: RunDetail;
        mini?: boolean;
    }>(),
    { mini: false }
);

/* 已耗时 = 真实时间 - createTime，秒表式每秒走动，与父级轮询解耦 */
const now = ref(new Date().toISOString());
const ticker = window.setInterval(() => {
    now.value = new Date().toISOString();
}, 1000);
onUnmounted(() => window.clearInterval(ticker));

/* 无活跃 run 时为空闲态：step=-1 节点全灰、漏斗全 — */
const step = computed(() => (props.run ? stateToStep(props.run.state) : -1));
const restoring = computed(() => props.run?.state === RunState.RESTORING);
const queued = computed(() => props.run?.state === RunState.PENDING);
const canceling = computed(() => props.run?.status === RunStatus.CANCELING);
const elapsed = computed(() => (props.run ? fmtElapsed(props.run.createTime, now.value) : ''));

/* 阶段失败徽章：FAILED attempt 按 actionType 分组计数 */
const failures = computed(() => {
    const counter = new Map<string, number>();
    for (const brief of props.run?.actionAttemptBriefs ?? []) {
        if (brief.status === ActionStatus.FAILED) {
            counter.set(brief.actionType, (counter.get(brief.actionType) ?? 0) + 1);
        }
    }
    return [...counter.entries()].map(([actionType, count]) => ({
        label: ACTION_TYPE_LABEL[actionType as keyof typeof ACTION_TYPE_LABEL],
        count,
    }));
});

const stageTitle = computed(() => {
    if (!props.run) {
        return '空闲';
    }
    if (canceling.value) {
        return '取消中';
    }
    if (queued.value) {
        return '排队中';
    }
    return stateLabel(props.run.state);
});
const stageDesc = computed(() => {
    if (!props.run) {
        return '暂无进行中的任务，点击「手动触发」开跑一轮，或等待夜间调度';
    }
    if (canceling.value) {
        return '已收到取消指令，等待当前动作到达检查点后回滚并清理';
    }
    if (queued.value) {
        return '并发槽已满，等待空槽后自动启动';
    }
    if (restoring.value) {
        return '验证未通过，恢复修复前现场后回到修复阶段';
    }
    const current = PIPELINE_NODES[step.value];
    return current ? current.desc : '';
});

const nodeClass = (i: number) => {
    if (i < step.value) {
        return 'bg-orange-500 text-white';
    }
    if (i === step.value) {
        return canceling.value ? 'node-canceling' : 'node-active';
    }
    return 'bg-slate-100';
};

/* 节点图标颜色：当前节点橙(取消中琥珀)，未到达浅灰；已完成统一打勾不走这里 */
const nodeIconClass = (i: number) => {
    if (i === step.value) {
        return canceling.value ? 'text-amber-500' : 'text-orange-500';
    }
    return 'text-slate-300';
};

const funnelItem = (value?: number) => value ?? '—';
</script>

<template>
    <!-- 迷你形态：管理页运行卡 -->
    <div v-if="mini">
        <div class="flex items-center gap-1">
            <div
                v-for="(node, i) in PIPELINE_NODES"
                :key="node.key"
                class="h-1.5 rounded-full flex-1"
                :class="
                    i < step
                        ? 'bg-orange-400'
                        : i === step
                          ? canceling
                              ? 'bg-amber-500'
                              : 'bg-orange-500'
                          : 'bg-slate-200'
                "></div>
        </div>
        <div class="flex items-center justify-between mt-2 text-[11px]">
            <span
                :class="
                    queued ? 'text-slate-400' : canceling ? 'text-amber-600' : 'text-orange-500'
                "
                >{{ stageTitle }}</span
            >
            <span class="font-mono text-slate-400">{{ elapsed }}</span>
        </div>
    </div>

    <!-- 完整形态：我的项目页当前任务 -->
    <template v-else>
        <div class="flex items-center justify-between px-6 pt-4 pb-1">
            <div class="flex items-center gap-3">
                <h2 class="text-base font-semibold text-slate-800">当前任务</h2>
                <template v-if="run">
                    <copyable-id
                        class="text-xs text-slate-400"
                        :value="run.id"
                        :display="`${run.id.slice(0, 8)}…`" />
                    <span class="text-xs text-slate-400">{{ triggerLabel(run.triggerType) }}</span>
                    <span
                        v-for="f in failures"
                        :key="f.label"
                        class="px-1.5 py-px rounded text-[11px] bg-rose-50 text-rose-500 border border-rose-200">
                        {{ f.label }}失败 ×{{ f.count }}
                    </span>
                </template>
                <span v-else class="text-xs text-slate-300">暂无进行中的任务</span>
            </div>
            <div class="flex items-center gap-3">
                <span
                    v-if="canceling"
                    class="px-1.5 py-px rounded text-[11px] bg-amber-50 text-amber-600 border border-amber-200">
                    取消中
                </span>
                <slot name="actions" />
                <span v-if="run" class="text-xs text-amber-600 flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-amber-400"></span>已耗时 {{ elapsed }}
                </span>
            </div>
        </div>

        <!-- 流水线区：720px 最小宽度(7 节点 48px + 6 连接线 64px)，窄屏横向滚动不挤压 -->
        <div class="px-10 pt-10 pb-4 overflow-x-auto">
            <div class="min-w-[720px]">
                <div
                    class="grid max-w-4xl mx-auto"
                    style="grid-template-columns: repeat(6, 1fr 64px) 1fr">
                    <template v-for="(node, i) in PIPELINE_NODES" :key="node.key">
                        <div class="flex flex-col items-center">
                            <div
                                class="flex items-center justify-center w-12 h-12 rounded-full transition"
                                :class="nodeClass(i)">
                                <svg
                                    v-if="i < step"
                                    class="w-5 h-5"
                                    viewBox="0 0 20 20"
                                    fill="currentColor">
                                    <path
                                        fill-rule="evenodd"
                                        d="M16.7 5.3a1 1 0 010 1.4l-8 8a1 1 0 01-1.4 0l-4-4a1 1 0 111.4-1.4L8 12.58l7.3-7.3a1 1 0 011.4 0z"
                                        clip-rule="evenodd" />
                                </svg>
                                <component
                                    :is="node.icon"
                                    v-else
                                    class="text-xl"
                                    :class="nodeIconClass(i)" />
                            </div>
                            <div
                                class="mt-2.5 text-sm"
                                :class="
                                    i === step
                                        ? 'text-orange-600 font-medium'
                                        : i < step
                                          ? 'text-slate-700'
                                          : 'text-slate-400'
                                ">
                                {{ node.label }}
                            </div>
                        </div>
                        <div v-if="i < PIPELINE_NODES.length - 1" class="flex items-center h-12">
                            <div
                                class="h-[3px] w-full rounded-full"
                                :class="i < step ? 'bg-orange-400' : 'bg-slate-200'"></div>
                        </div>
                    </template>
                </div>
                <!-- 回退环：验证失败 → restore → 回到修复 -->
                <div
                    class="grid max-w-4xl mx-auto"
                    style="grid-template-columns: repeat(6, 1fr 64px) 1fr">
                    <div style="grid-column: 7 / 10" class="h-9 relative">
                        <svg
                            class="absolute inset-0 w-full h-full overflow-visible"
                            viewBox="0 0 200 36"
                            preserveAspectRatio="none">
                            <path
                                d="M188 2 C 188 30, 12 30, 12 4"
                                fill="none"
                                stroke-width="2"
                                vector-effect="non-scaling-stroke"
                                :stroke="restoring ? '#fb7185' : '#fecdd3'"
                                :class="restoring ? 'retry-arc' : ''"
                                stroke-dasharray="4 4" />
                            <path
                                d="M12 0 l-4.5 8 h9 z"
                                :fill="restoring ? '#fb7185' : '#fecdd3'" />
                            <text
                                x="100"
                                y="33"
                                text-anchor="middle"
                                font-size="10"
                                :fill="restoring ? '#fb7185' : '#fda4af'">
                                回退环：验证未通过 → 恢复现场 → 重新修复
                            </text>
                        </svg>
                    </div>
                </div>
            </div>
        </div>

        <div class="pb-3 text-center text-sm text-slate-500">
            当前阶段：<span
                class="font-medium"
                :class="canceling ? 'text-amber-600' : 'text-orange-500'"
                >{{ stageTitle }}</span
            >
            <span class="text-slate-300 mx-1.5">—</span>{{ stageDesc }}
        </div>
        <div class="flex items-center justify-center gap-8 pb-7 text-xs text-slate-400">
            <span class="inline-flex items-center gap-1.5">
                扫描发现问题
                <span class="font-mono text-sm text-slate-700">{{
                    funnelItem(run?.scannedIssueCount)
                }}</span>
            </span>
            <span class="text-slate-200">|</span>
            <span class="inline-flex items-center gap-1.5">
                本轮入选
                <span class="font-mono text-sm text-slate-700">{{
                    funnelItem(run?.selectedIssueCount)
                }}</span>
            </span>
            <span class="text-slate-200">|</span>
            <span class="inline-flex items-center gap-1.5">
                已修复
                <span class="font-mono text-sm text-slate-700">{{
                    funnelItem(run?.currentFixedIssueCount)
                }}</span>
            </span>
            <span class="text-slate-200">|</span>
            <span class="inline-flex items-center gap-1.5">
                验证通过
                <span class="font-mono text-sm text-orange-500">{{
                    funnelItem(run?.currentVerifiedIssueCount)
                }}</span>
            </span>
        </div>
    </template>
</template>

<style scoped lang="scss">
@keyframes pulse-ring {
    0% {
        box-shadow: 0 0 0 0 rgba(249, 115, 22, 0.3);
    }
    70% {
        box-shadow: 0 0 0 10px rgba(249, 115, 22, 0);
    }
    100% {
        box-shadow: 0 0 0 0 rgba(249, 115, 22, 0);
    }
}
.node-active {
    @apply bg-white border-2 border-orange-400 ring-4 ring-orange-100;
    animation: pulse-ring 1.6s ease-out infinite;
}
.node-canceling {
    @apply bg-white border-2 border-amber-400 ring-4 ring-amber-100;
    animation: pulse-ring-amber 1.6s ease-out infinite;
}
@keyframes pulse-ring-amber {
    0% {
        box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.3);
    }
    70% {
        box-shadow: 0 0 0 10px rgba(245, 158, 11, 0);
    }
    100% {
        box-shadow: 0 0 0 0 rgba(245, 158, 11, 0);
    }
}
@keyframes dash-move {
    to {
        stroke-dashoffset: -8;
    }
}
.retry-arc {
    stroke-dasharray: 4 4;
    animation: dash-move 0.6s linear infinite;
}
</style>
