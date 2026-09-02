<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { Modal as AModal, message } from 'ant-design-vue';
import { adminCancelRun, adminToggleSchedule, getAllRun, getStats, listProject } from '@/axios/dev';
import { ResponseCode } from '@/constants/response';
import {
    RunState,
    RunStatus,
    SortDir,
    type ProjectDetail,
    type RunDetail,
    type Stats,
} from '@/types/dev';
import { hasAnyPermission } from '@/utils/permission';
import { Permission } from '@/constants/auth';
import { fmtSeconds, fmtTime, repoShort, triggerLabel } from '@/libs/dev';
import PipelineStateMachine from '@/views/dev/components/PipelineStateMachine.vue';
import RunStatusBadge from '@/views/dev/components/RunStatusBadge.vue';
import PrStatusBadge from '@/views/dev/components/PrStatusBadge.vue';
import PageSwitcher from '@/views/dev/components/PageSwitcher.vue';
import CopyableId from '@/views/dev/components/CopyableId.vue';

const loaded = ref(false);
const stats = ref<Stats>();
const projects = ref<ProjectDetail[]>([]);
const projectPageNum = ref(1);
const projectTotalPageNum = ref(1);
const PROJECT_PAGE_SIZE = 10;
/* 当前任务 = 进行中 run(RUNNING/CANCELING,含 PENDING 排队),取大页快照覆盖并发槽+排队;
   按创建时间从早到晚(asc)排序,先创建的先跑/先排,贴合分发顺序 */
const runningRuns = ref<RunDetail[]>([]);
const RUNNING_PAGE_SIZE = 50;
/* 最近完成 = 终态 run,服务端 statuses 过滤后分页,计数精确 */
const recentRuns = ref<RunDetail[]>([]);
const recentPageNum = ref(1);
const recentTotalPageNum = ref(1);
const RECENT_PAGE_SIZE = 8;

const canCancelRun = hasAnyPermission([Permission.DEV_RUN_CANCEL]);
const canToggleSchedule = hasAnyPermission([Permission.DEV_PROJECT_SCHEDULE]);

const kpis = computed(() => {
    if (!stats.value) {
        return [];
    }
    const s = stats.value;
    return [
        {
            label: '接入仓库',
            value: s.projectCount,
            unit: '个',
            sub: `${s.enabledProjectCount} 个启用中`,
            color: 'text-slate-800',
        },
        {
            label: '并发任务',
            value: `${s.executingRunCount}/${s.maxConcurrency}`,
            unit: '',
            sub: `${s.pendingRunCount} 个排队中`,
            color: 'text-orange-500',
        },
        {
            label: '累计交付修复',
            value: s.deliveredIssueCount,
            unit: '个',
            sub: `${s.acceptedIssueCount} 个已采纳`,
            color: 'text-orange-500',
        },
        {
            label: 'PR 合并率',
            value: s.prTotal > 0 ? `${Math.round((s.prMerged / s.prTotal) * 100)}%` : '—',
            unit: '',
            sub: `${s.prMerged} / ${s.prTotal} 已合并`,
            color: 'text-emerald-600',
        },
        {
            label: '累计节约人天',
            value: s.savedPersonDays,
            unit: '人天',
            sub: '按已采纳修复的估算工时',
            color: 'text-emerald-600',
        },
    ];
});

const loadStats = async () => {
    const response = await getStats();
    if (response.data.code === ResponseCode.SUCCESS) {
        stats.value = response.data.data;
    }
};

const loadProjects = async (): Promise<void> => {
    const response = await listProject({
        pageNum: projectPageNum.value,
        pageSize: PROJECT_PAGE_SIZE,
    });
    if (response.data.code !== ResponseCode.SUCCESS) {
        return;
    }
    const page = response.data.data;
    if (page.records.length === 0 && projectPageNum.value > 1) {
        // 轮询期间末页被抽空(如有删除),回退一页重载
        projectPageNum.value -= 1;
        return loadProjects();
    }
    projects.value = page.records;
    projectTotalPageNum.value = Math.max(page.totalPageNum, 1);
};

const onProjectPage = (page: number) => {
    projectPageNum.value = page;
    loadProjects();
};

const loadRunningRuns = async () => {
    const response = await getAllRun({
        statuses: [RunStatus.RUNNING, RunStatus.CANCELING],
        pageNum: 1,
        pageSize: RUNNING_PAGE_SIZE,
        sortDir: SortDir.ASC,
    });
    if (response.data.code === ResponseCode.SUCCESS) {
        runningRuns.value = response.data.data.records;
    }
};

const loadRecentRuns = async (): Promise<void> => {
    const response = await getAllRun({
        statuses: [RunStatus.SUCCEEDED, RunStatus.FAILED, RunStatus.CANCELED],
        pageNum: recentPageNum.value,
        pageSize: RECENT_PAGE_SIZE,
        sortDir: SortDir.DESC,
    });
    if (response.data.code !== ResponseCode.SUCCESS) {
        return;
    }
    const page = response.data.data;
    if (page.records.length === 0 && recentPageNum.value > 1) {
        recentPageNum.value -= 1;
        return loadRecentRuns();
    }
    recentRuns.value = page.records;
    recentTotalPageNum.value = Math.max(page.totalPageNum, 1);
};

const onRecentPage = (page: number) => {
    recentPageNum.value = page;
    loadRecentRuns();
};

const load = async () => {
    await Promise.all([loadStats(), loadProjects(), loadRunningRuns(), loadRecentRuns()]);
};

let pollTimer: number | undefined;
onMounted(async () => {
    await load();
    loaded.value = true;
    pollTimer = window.setInterval(load, 3 * 1000);
});
onUnmounted(() => window.clearInterval(pollTimer));

const onCancelRun = (run: RunDetail) => {
    AModal.confirm({
        title: '取消任务',
        content: `确认取消「${run.projectName ?? run.projectId}」正在运行的任务？取消不会立即中断，而是等当前阶段动作到达检查点后回滚清理，该轮修复作废。`,
        okText: '取消任务',
        okButtonProps: { danger: true },
        cancelText: '再想想',
        onOk: async () => {
            const response = await adminCancelRun(run.id);
            if (response.data.code !== ResponseCode.SUCCESS) {
                void message.error(response.data.msg);
                return;
            }
            void message.success('已发出取消指令');
            await load();
        },
    });
};

const onToggleSchedule = (project: ProjectDetail) => {
    AModal.confirm({
        title: project.enabled ? '停用调度' : '启用调度',
        content: project.enabled
            ? `确认停用「${project.name}」的调度？停用后不再参与夜间修复。`
            : `确认启用「${project.name}」的调度？启用后将参与夜间修复。`,
        okText: project.enabled ? '停用调度' : '启用调度',
        okButtonProps: { danger: project.enabled },
        cancelText: '再想想',
        onOk: async () => {
            const response = await adminToggleSchedule(project.id);
            if (response.data.code !== ResponseCode.SUCCESS) {
                void message.error(response.data.msg);
                return;
            }
            void message.success(project.enabled ? '调度已停用' : '调度已启用');
            await load();
        },
    });
};
</script>

<template>
    <section v-if="loaded" class="max-w-[1400px] mx-auto px-6 space-y-5">
        <!-- KPI 行 -->
        <div class="grid grid-cols-5 gap-4">
            <div
                v-for="k in kpis"
                :key="k.label"
                class="rounded-xl border border-slate-200 bg-white shadow-sm px-5 py-4">
                <div class="text-[11px] text-slate-400">{{ k.label }}</div>
                <div class="mt-1.5 flex items-baseline gap-1.5">
                    <span class="font-mono text-2xl font-semibold" :class="k.color">{{
                        k.value
                    }}</span>
                    <span class="text-[11px] text-slate-400">{{ k.unit }}</span>
                </div>
                <div class="mt-1 text-[11px] text-slate-400">{{ k.sub }}</div>
            </div>
        </div>

        <!-- 项目列表 -->
        <div class="rounded-xl border border-slate-200 bg-white shadow-sm">
            <div class="flex items-center justify-between px-5 py-3.5 border-b border-slate-100">
                <h2 class="text-sm font-semibold text-slate-800">项目列表</h2>
                <page-switcher
                    :page-num="projectPageNum"
                    :total-page-num="projectTotalPageNum"
                    @change="onProjectPage" />
            </div>
            <table class="w-full text-sm">
                <thead>
                    <tr class="text-[11px] text-slate-400 border-b border-slate-100">
                        <th class="text-center font-medium px-5 py-2.5">项目名称</th>
                        <th class="text-center font-medium px-3 py-2.5">项目管理员</th>
                        <th class="text-center font-medium px-3 py-2.5">仓库链接</th>
                        <th class="text-center font-medium px-3 py-2.5">仓库分支</th>
                        <th
                            class="text-center font-medium px-3 py-2.5"
                            title="单轮任务处理的 SonarQube 问题数上限">
                            单轮 Sonar 处理上限
                        </th>
                        <th
                            class="text-center font-medium px-3 py-2.5"
                            title="单轮任务处理的 AI 发现问题数上限">
                            单轮 AI 处理上限
                        </th>
                        <th class="text-center font-medium px-3 py-2.5">调度启停</th>
                        <th class="text-center font-medium px-5 py-2.5">操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="p in projects"
                        :key="p.id"
                        class="border-b border-slate-50 last:border-0 hover:bg-orange-50/40">
                        <td class="px-5 py-3 text-center text-xs font-medium text-slate-700">
                            {{ p.name }}
                        </td>
                        <td class="px-3 py-3 text-center text-xs text-slate-500">
                            {{ p.adminName }}
                        </td>
                        <td class="px-3 py-3 text-center">
                            <a
                                class="font-mono text-[11px] text-orange-500 hover:underline"
                                :href="p.link"
                                target="_blank"
                                rel="noopener noreferrer"
                                :title="p.link">
                                {{ repoShort(p.link) }}
                            </a>
                        </td>
                        <td class="px-3 py-3 text-center font-mono text-[11px] text-slate-500">
                            {{ p.branchName }}
                        </td>
                        <td class="px-3 py-3 text-center font-mono text-[11px] text-slate-500">
                            {{ p.maxSonarIssuesPerRun }}
                        </td>
                        <td class="px-3 py-3 text-center font-mono text-[11px] text-slate-500">
                            {{ p.maxAiIssuesPerRun }}
                        </td>
                        <td class="px-3 py-3 text-center">
                            <span
                                class="px-2 py-0.5 rounded text-[11px] border"
                                :class="
                                    p.enabled
                                        ? 'bg-emerald-50 text-emerald-600 border-emerald-200'
                                        : 'bg-slate-50 text-slate-400 border-slate-200'
                                ">
                                {{ p.enabled ? '启用' : '停用' }}
                            </span>
                        </td>
                        <td class="px-5 py-3 text-center">
                            <button
                                v-if="canToggleSchedule"
                                class="schedule-toggle-btn"
                                :class="
                                    p.enabled
                                        ? 'schedule-toggle-btn--stop'
                                        : 'schedule-toggle-btn--start'
                                "
                                @click="onToggleSchedule(p)">
                                {{ p.enabled ? '停用调度' : '启用调度' }}
                            </button>
                            <span v-else class="text-[11px] text-slate-300">—</span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 当前任务 -->
        <div class="rounded-xl border border-slate-200 bg-white shadow-sm">
            <div class="flex items-center justify-between px-5 py-3.5 border-b border-slate-100">
                <h2 class="text-sm font-semibold text-slate-800">当前任务</h2>
                <span v-if="stats" class="text-xs text-slate-400">
                    占槽
                    <span class="font-mono text-orange-500">{{ stats.executingRunCount }}</span>
                    <span class="font-mono text-slate-400">/{{ stats.maxConcurrency }}</span>
                    ，{{ stats.pendingRunCount }} 个排队中
                </span>
            </div>
            <div v-if="runningRuns.length > 0" class="grid grid-cols-2 gap-4 p-4">
                <div
                    v-for="r in runningRuns"
                    :key="r.id"
                    class="rounded-lg border border-slate-200 bg-slate-50/60 p-4">
                    <div class="flex items-center justify-between mb-3">
                        <div>
                            <div class="text-sm font-medium text-slate-800">
                                {{ r.projectName }}
                            </div>
                            <div class="font-mono text-[10px] text-slate-400 mt-0.5">
                                <copyable-id :value="r.id" :display="r.id.slice(0, 8)" />
                                · {{ triggerLabel(r.triggerType) }}
                            </div>
                        </div>
                        <button
                            v-if="
                                r.state !== RunState.PENDING &&
                                r.status === RunStatus.RUNNING &&
                                canCancelRun
                            "
                            class="px-2.5 py-1 rounded-md border border-rose-200 text-rose-500 text-xs hover:bg-rose-50 transition"
                            @click="onCancelRun(r)">
                            取消
                        </button>
                        <span
                            v-else-if="r.status === RunStatus.CANCELING"
                            class="px-2 py-0.5 rounded text-[11px] bg-amber-50 text-amber-600 border border-amber-200">
                            取消中
                        </span>
                        <span
                            v-else-if="r.state === RunState.PENDING"
                            class="px-2 py-0.5 rounded text-[11px] bg-slate-100 text-slate-400 border border-slate-200">
                            排队中
                        </span>
                    </div>
                    <pipeline-state-machine :run="r" mini />
                </div>
            </div>
            <div v-else class="px-5 py-8 text-center text-xs text-slate-300">
                当前没有正在运行的任务
            </div>
        </div>

        <!-- 最近完成(整行收尾) -->
        <div class="rounded-xl border border-slate-200 bg-white shadow-sm">
            <div class="flex items-center justify-between px-5 py-3.5 border-b border-slate-100">
                <h2 class="text-sm font-semibold text-slate-800">最近完成</h2>
                <page-switcher
                    :page-num="recentPageNum"
                    :total-page-num="recentTotalPageNum"
                    @change="onRecentPage" />
            </div>
            <table class="w-full text-sm table-fixed">
                <colgroup>
                    <col class="w-[14%]" />
                    <col class="w-[9%]" />
                    <col class="w-[6%]" />
                    <col class="w-[7%]" />
                    <col class="w-[7%]" />
                    <col class="w-[7%]" />
                    <col class="w-[8%]" />
                    <col class="w-[8%]" />
                    <col class="w-[8%]" />
                    <col class="hidden xl:table-column w-[13%]" />
                    <col class="hidden xl:table-column w-[13%]" />
                </colgroup>
                <thead>
                    <tr class="text-[11px] text-slate-400 border-b border-slate-100">
                        <th class="text-center font-medium px-5 py-2.5">项目</th>
                        <th
                            class="text-center font-medium px-3 py-2.5"
                            title="点击可复制完整 runId">
                            任务编号
                        </th>
                        <th class="text-center font-medium px-3 py-2.5">触发方式</th>
                        <th class="text-center font-medium px-3 py-2.5">运行结果</th>
                        <th
                            class="text-center font-medium px-3 py-2.5"
                            title="本次任务扫描发现的问题总数(SonarQube + AI)">
                            发现问题数
                        </th>
                        <th
                            class="text-center font-medium px-3 py-2.5"
                            title="成功任务交付的修复问题数">
                            已交付修复数
                        </th>
                        <th class="text-center font-medium px-3 py-2.5">PR</th>
                        <th
                            class="text-center font-medium px-3 py-2.5"
                            title="创建 → 执行开始(排队耗时)">
                            等待时长
                        </th>
                        <th class="text-center font-medium px-3 py-2.5" title="执行开始 → 结束">
                            执行时长
                        </th>
                        <th class="hidden xl:table-cell text-center font-medium px-3 py-2.5">
                            开始时间
                        </th>
                        <th class="hidden xl:table-cell text-center font-medium px-3 py-2.5">
                            结束时间
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        v-for="r in recentRuns"
                        :key="r.id"
                        class="border-b border-slate-50 last:border-0 hover:bg-orange-50/40">
                        <td class="px-5 py-3 text-center text-xs">
                            <span v-if="r.projectName" class="text-slate-600">{{
                                r.projectName
                            }}</span>
                            <span
                                v-else
                                class="px-1.5 py-0.5 rounded border border-dashed border-slate-300 text-slate-400"
                                title="项目已删除，任务记录保留作为历史存档">
                                已删除项目
                            </span>
                        </td>
                        <td class="px-3 py-3 text-center">
                            <copyable-id :value="r.id" :display="r.id.slice(0, 8)" />
                        </td>
                        <td class="px-3 py-3 text-center text-xs text-slate-500 whitespace-nowrap">
                            {{ triggerLabel(r.triggerType) }}
                        </td>
                        <td class="px-3 py-3 text-center">
                            <run-status-badge :status="r.status" />
                        </td>
                        <td class="px-3 py-3 text-center font-mono text-xs text-slate-600">
                            {{ r.scannedIssueCount ?? '—' }}
                        </td>
                        <td class="px-3 py-3 text-center font-mono text-xs text-slate-600">
                            {{ r.deliveredIssueCount ?? '—' }}
                        </td>
                        <td class="px-3 py-3 text-center text-xs whitespace-nowrap">
                            <pr-status-badge
                                v-if="r.prId"
                                :pr-id="r.prId"
                                :pr-link="r.prLink"
                                :pr-status="r.prStatus" />
                            <span v-else class="text-slate-300">—</span>
                        </td>
                        <td class="px-3 py-3 text-center font-mono text-xs text-slate-600">
                            {{ fmtSeconds(r.waitSeconds) }}
                        </td>
                        <td class="px-3 py-3 text-center font-mono text-xs text-slate-600">
                            {{ fmtSeconds(r.execSeconds) }}
                        </td>
                        <td
                            class="hidden xl:table-cell px-3 py-3 text-center text-xs text-slate-400 whitespace-nowrap">
                            {{ fmtTime(r.createTime) }}
                        </td>
                        <td
                            class="hidden xl:table-cell px-3 py-3 text-center text-xs text-slate-400 whitespace-nowrap">
                            {{ fmtTime(r.updateTime) }}
                        </td>
                    </tr>
                    <tr v-if="recentRuns.length === 0">
                        <td colspan="11" class="px-5 py-10 text-center text-xs text-slate-300">
                            暂无已完成任务
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </section>
</template>

<style scoped lang="scss">
.schedule-toggle-btn {
    @apply px-2.5 py-1 rounded-md border text-xs transition;

    &--stop {
        @apply border-amber-200 text-amber-600 hover:bg-amber-50;
    }

    &--start {
        @apply border-emerald-200 text-emerald-600 hover:bg-emerald-50;
    }
}
</style>
