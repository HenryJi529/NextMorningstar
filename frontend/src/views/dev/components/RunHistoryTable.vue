<script setup lang="ts">
import type { RunDetail } from '@/types/dev';
import { fmtDuration, fmtTime, triggerLabel } from '@/libs/dev';
import RunStatusBadge from '@/views/dev/components/RunStatusBadge.vue';
import PrStatusBadge from '@/views/dev/components/PrStatusBadge.vue';
import CopyableId from '@/views/dev/components/CopyableId.vue';

defineProps<{
    runs: RunDetail[];
}>();
</script>

<template>
    <table class="w-full text-sm table-fixed">
        <colgroup>
            <col class="w-[11%]" />
            <col class="w-[9%]" />
            <col class="w-[9%]" />
            <col class="w-[11%]" />
            <col class="w-[11%]" />
            <col class="w-[13%]" />
            <col class="hidden xl:table-column w-[13%]" />
            <col class="hidden xl:table-column w-[13%]" />
            <col class="w-[10%]" />
        </colgroup>
        <thead class="sticky top-0 bg-white">
            <tr class="text-[11px] text-slate-400 border-b border-slate-100">
                <th class="text-center font-medium px-5 py-2.5">任务编号</th>
                <th class="text-center font-medium px-3 py-2.5">触发方式</th>
                <th class="text-center font-medium px-3 py-2.5">运行结果</th>
                <th
                    class="text-center font-medium px-3 py-2.5"
                    title="本次任务扫描发现的问题总数(SonarQube + AI)">
                    发现问题数
                </th>
                <th class="text-center font-medium px-3 py-2.5" title="成功任务交付的修复问题数">
                    已交付修复数
                </th>
                <th class="text-center font-medium px-3 py-2.5">PR</th>
                <th class="hidden xl:table-cell text-center font-medium px-3 py-2.5">开始时间</th>
                <th class="hidden xl:table-cell text-center font-medium px-3 py-2.5">结束时间</th>
                <th class="text-right font-medium px-5 py-2.5" title="开始时间 → 结束时间">
                    任务耗时
                </th>
            </tr>
        </thead>
        <tbody>
            <tr
                v-for="run in runs"
                :key="run.id"
                class="border-b border-slate-50 last:border-0 hover:bg-orange-50/40">
                <td class="px-5 py-3 text-center text-xs text-slate-500" :title="run.id">
                    <copyable-id :value="run.id" :display="run.id.slice(0, 8)" />
                </td>
                <td class="px-3 py-3 text-center text-xs text-slate-500 whitespace-nowrap">
                    {{ triggerLabel(run.triggerType) }}
                </td>
                <td class="px-3 py-3 text-center">
                    <run-status-badge :status="run.status" />
                </td>
                <td class="px-3 py-3 text-center font-mono text-xs text-slate-600">
                    {{ run.scannedIssueCount ?? '—' }}
                </td>
                <td class="px-3 py-3 text-center font-mono text-xs text-slate-600">
                    {{ run.deliveredIssueCount ?? '—' }}
                </td>
                <td class="px-3 py-3 text-center text-xs whitespace-nowrap">
                    <pr-status-badge
                        v-if="run.prId"
                        :pr-id="run.prId"
                        :pr-link="run.prLink"
                        :pr-status="run.prStatus" />
                    <span v-else class="text-slate-300">—</span>
                </td>
                <td
                    class="hidden xl:table-cell px-3 py-3 text-center text-xs text-slate-400 whitespace-nowrap">
                    {{ fmtTime(run.createTime) }}
                </td>
                <td
                    class="hidden xl:table-cell px-3 py-3 text-center text-xs text-slate-400 whitespace-nowrap">
                    {{ fmtTime(run.updateTime) }}
                </td>
                <td class="px-5 py-3 text-right font-mono text-xs text-slate-400 whitespace-nowrap">
                    {{ fmtDuration(run.createTime, run.updateTime) }}
                </td>
            </tr>
            <tr v-if="runs.length === 0">
                <td colspan="9" class="px-5 py-10 text-center text-xs text-slate-300">
                    暂无历史任务
                </td>
            </tr>
        </tbody>
    </table>
</template>
