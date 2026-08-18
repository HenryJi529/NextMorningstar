<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { Modal as AModal, message } from 'ant-design-vue';
import {
    cancelRun,
    deleteProject,
    getAllRun,
    listProject,
    triggerRun,
    updateProject,
} from '@/axios/dev';
import { ResponseCode } from '@/constants/response';
import { RunState, RunStatus, SortDir, type ProjectDetail, type RunDetail } from '@/types/dev';
import { useUserStore } from '@/stores/users';
import { repoShort } from '@/libs/dev';
import PipelineStateMachine from '@/views/dev/components/PipelineStateMachine.vue';
import ProjectFormModal from '@/views/dev/components/ProjectFormModal.vue';
import RunHistoryTable from '@/views/dev/components/RunHistoryTable.vue';
import PageSwitcher from '@/views/dev/components/PageSwitcher.vue';

const userStore = useUserStore();

const loaded = ref(false);
const projects = ref<ProjectDetail[]>([]);
const selectedId = ref('');

/* 进行中(RUNNING/CANCELING)的 run 才上状态机；PENDING 排队也算进行中。
   单项目同时至多一个活跃 run,且必为最新一条,故取第一页第一条判定 */
const activeRun = ref<RunDetail>();
/* 历史任务 = 终态 run,服务端 statuses 过滤后分页,计数精确 */
const historyRuns = ref<RunDetail[]>([]);
const historyPageNum = ref(1);
const historyTotalPageNum = ref(1);
const HISTORY_PAGE_SIZE = 10;
const TERMINAL_STATUSES = [RunStatus.SUCCEEDED, RunStatus.FAILED, RunStatus.CANCELED];

const formOpen = ref(false);
const formMode = ref<'create' | 'edit'>('create');

const currentProject = computed(() => projects.value.find(p => p.id === selectedId.value));

const loadProjects = async (keepSelection = true) => {
    // 子菜单需全量自己的项目,取大页快照(平台项目量级远小于 100)
    const response = await listProject({ pageNum: 1, pageSize: 100 });
    if (response.data.code !== ResponseCode.SUCCESS) {
        return;
    }
    const previous = selectedId.value;
    projects.value = response.data.data.records.filter(p => p.adminId === userStore.id);
    if (!keepSelection || !projects.value.some(p => p.id === previous)) {
        selectedId.value = projects.value[0]?.id ?? '';
    }
};

const loadActiveRun = async () => {
    if (!selectedId.value) {
        activeRun.value = undefined;
        return;
    }
    const response = await getAllRun({ projectId: selectedId.value, pageNum: 1, pageSize: 1, sortDir: SortDir.DESC });
    if (response.data.code !== ResponseCode.SUCCESS) {
        return;
    }
    const newest = response.data.data.records[0];
    activeRun.value =
        newest && (newest.status === RunStatus.RUNNING || newest.status === RunStatus.CANCELING)
            ? newest
            : undefined;
};

const loadHistory = async (): Promise<void> => {
    if (!selectedId.value) {
        historyRuns.value = [];
        return;
    }
    const response = await getAllRun({
        projectId: selectedId.value,
        statuses: TERMINAL_STATUSES,
        pageNum: historyPageNum.value,
        pageSize: HISTORY_PAGE_SIZE,
        sortDir: SortDir.DESC,
    });
    if (response.data.code !== ResponseCode.SUCCESS) {
        return;
    }
    const page = response.data.data;
    if (page.records.length === 0 && historyPageNum.value > 1) {
        // 轮询期间末页被抽空(如有删除),回退一页重载
        historyPageNum.value -= 1;
        return loadHistory();
    }
    historyRuns.value = page.records;
    historyTotalPageNum.value = Math.max(page.totalPageNum, 1);
};

const onHistoryPage = (page: number) => {
    historyPageNum.value = page;
    loadHistory();
};

/* 轮询：3s 刷一次——活跃 run 快照喂状态机/漏斗，当前历史页喂历史表 */
const loadRuns = async () => {
    await Promise.all([loadActiveRun(), loadHistory()]);
};
let pollTimer: number | undefined;

watch(selectedId, () => {
    historyPageNum.value = 1;
    loadRuns();
});

onMounted(async () => {
    await loadProjects(false);
    await loadRuns();
    loaded.value = true;
    pollTimer = window.setInterval(loadRuns, 3 * 1000);
});
onUnmounted(() => window.clearInterval(pollTimer));

const openCreate = () => {
    formMode.value = 'create';
    formOpen.value = true;
};
const openEdit = () => {
    formMode.value = 'edit';
    formOpen.value = true;
};
const onSubmitted = async (project: ProjectDetail) => {
    await loadProjects(false);
    selectedId.value = project.id;
};

const onOpenRepo = () => {
    const link = currentProject.value?.link;
    if (link) {
        window.open(link, '_blank', 'noopener,noreferrer');
    }
};

const onTrigger = async () => {
    if (!currentProject.value) {
        return;
    }
    const response = await triggerRun(currentProject.value.id);
    if (response.data.code !== ResponseCode.SUCCESS) {
        message.error(response.data.msg);
        return;
    }
    message.success('已触发，流水线开跑');
    await loadActiveRun();
};

const onToggleEnabled = async () => {
    const project = currentProject.value;
    if (!project) {
        return;
    }
    const response = await updateProject(project.id, { enabled: !project.enabled });
    if (response.data.code !== ResponseCode.SUCCESS) {
        message.error(response.data.msg);
        return;
    }
    projects.value = projects.value.map(p => (p.id === project.id ? response.data.data : p));
    message.success(
        project.enabled ? '调度已停用，不再参与夜间修复' : '调度已启用，将参与夜间修复'
    );
};

const onCancelRun = () => {
    const run = activeRun.value;
    if (!run) {
        return;
    }
    AModal.confirm({
        title: '取消任务',
        content:
            '确认取消当前任务？取消不会立即中断，而是等当前阶段动作到达检查点后回滚清理，该轮作废。',
        okText: '取消任务',
        okButtonProps: { danger: true },
        cancelText: '再想想',
        onOk: async () => {
            const response = await cancelRun(run.id);
            if (response.data.code !== ResponseCode.SUCCESS) {
                message.error(response.data.msg);
                return;
            }
            message.success('已发出取消指令');
            await loadRuns();
        },
    });
};

const onDelete = () => {
    const project = currentProject.value;
    if (!project) {
        return;
    }
    AModal.confirm({
        title: '删除项目',
        content: `确认删除「${project.name}」？历史任务记录会保留，但不再参与夜间调度。`,
        okText: '删除',
        okButtonProps: { danger: true },
        cancelText: '取消',
        onOk: async () => {
            const response = await deleteProject(project.id);
            if (response.data.code !== ResponseCode.SUCCESS) {
                message.error(response.data.msg);
                return;
            }
            message.success('项目已删除');
            await loadProjects(false);
        },
    });
};
</script>

<template>
    <div>
        <!-- 二级子菜单：项目切换(吸顶，与头部同 sticky 体系) -->
        <div
            class="sticky top-14 z-10 border-b border-slate-200 bg-white/90 backdrop-blur -mt-6 mb-6">
            <div class="max-w-[1400px] mx-auto px-6 py-2.5 flex items-center gap-2 overflow-x-auto">
                <button
                    v-for="p in projects"
                    :key="p.id"
                    :class="
                        selectedId === p.id
                            ? 'bg-white border-orange-400 text-slate-800 shadow-sm'
                            : 'bg-white/60 border-slate-200 text-slate-500 hover:text-slate-800 hover:border-slate-300'
                    "
                    class="flex items-center gap-2 px-4 py-1.5 rounded-full border transition whitespace-nowrap text-sm"
                    @click="selectedId = p.id">
                    <span
                        class="w-1.5 h-1.5 rounded-full"
                        :class="p.enabled ? 'bg-emerald-500' : 'bg-slate-300'"></span>
                    {{ p.name }}
                </button>
                <button
                    class="flex items-center gap-1.5 px-4 py-1.5 rounded-full border border-dashed border-slate-300 text-slate-400 hover:text-orange-500 hover:border-orange-400 transition text-sm whitespace-nowrap bg-white/40"
                    @click="openCreate">
                    <span class="text-base leading-none">＋</span> 接入新项目
                </button>
            </div>
        </div>

        <div class="max-w-[1400px] mx-auto px-6 w-full">
            <!-- 空态：无项目 -->
            <div
                v-if="loaded && !currentProject"
                class="rounded-xl border border-dashed border-slate-300 bg-white/60 py-20 text-center">
                <div class="text-slate-400 text-sm">还没有接入任何项目</div>
                <button
                    class="mt-4 px-5 py-2.5 rounded-lg bg-orange-500 hover:bg-orange-400 text-white text-sm font-medium transition shadow-sm shadow-orange-200"
                    @click="openCreate">
                    接入第一个项目
                </button>
            </div>

            <section v-else-if="currentProject" class="flex flex-col">
                <!-- 项目头部横幅(信息区 flex-1 最小 280px，更窄则按钮组换行到下一行) -->
                <div
                    class="rounded-xl border border-slate-200 bg-white shadow-sm px-6 py-4 mb-5 flex flex-wrap items-center gap-x-6 gap-y-3">
                    <div class="flex-1 min-w-[280px] flex flex-col gap-2">
                        <h1 class="text-lg font-semibold text-slate-800">
                            {{ currentProject.name }}
                        </h1>
                        <div v-if="currentProject.description" class="flex items-center text-xs">
                            <span class="w-16 shrink-0 text-slate-400">项目描述：</span>
                            <span
                                class="min-w-0 truncate text-slate-500"
                                :title="currentProject.description">
                                {{ currentProject.description }}
                            </span>
                        </div>
                        <div class="flex items-center text-xs">
                            <span class="w-16 shrink-0 text-slate-400">仓库信息：</span>
                            <span class="text-slate-400">链接</span>
                            <a
                                class="ml-2 font-mono text-orange-500 cursor-pointer hover:underline"
                                :title="currentProject.link"
                                @click="onOpenRepo">
                                {{ repoShort(currentProject.link) }}
                            </a>
                            <span class="mx-3 text-slate-200">|</span>
                            <span class="text-slate-400">分支</span>
                            <span class="ml-2 font-mono text-slate-600">{{
                                currentProject.branchName
                            }}</span>
                        </div>
                        <div class="flex items-center text-xs">
                            <span class="w-16 shrink-0 text-slate-400">配置信息：</span>
                            <span class="text-slate-400">单轮任务处理问题上限</span>
                            <span class="ml-2 font-mono text-slate-600">
                                Sonar {{ currentProject.maxSonarIssuesPerRun }} · AI
                                {{ currentProject.maxAiIssuesPerRun }}
                            </span>
                            <span class="mx-3 text-slate-200">|</span>
                            <span class="text-slate-400">调度启停</span>
                            <span
                                class="ml-2"
                                :class="
                                    currentProject.enabled ? 'text-emerald-600' : 'text-slate-400'
                                ">
                                {{ currentProject.enabled ? '启用' : '停用' }}
                            </span>
                        </div>
                    </div>
                    <!-- 按钮组：xl 以下直接 2×2 网格，xl 起一行四个等宽 -->
                    <div class="ml-auto grid grid-cols-2 xl:grid-cols-4 gap-2.5 shrink-0">
                        <button
                            class="px-4 py-2 rounded-lg bg-orange-500 hover:bg-orange-400 text-white text-sm font-medium transition shadow-sm shadow-orange-200"
                            @click="onTrigger">
                            手动触发
                        </button>
                        <button
                            class="px-4 py-2 rounded-lg border border-slate-200 hover:border-slate-300 text-sm text-slate-600 transition"
                            @click="openEdit">
                            编辑配置
                        </button>
                        <button
                            class="px-4 py-2 rounded-lg border border-slate-200 hover:border-amber-300 text-sm text-amber-600 transition"
                            @click="onToggleEnabled">
                            {{ currentProject.enabled ? '停用调度' : '启用调度' }}
                        </button>
                        <button
                            class="px-4 py-2 rounded-lg border border-slate-200 hover:border-rose-300 text-sm text-rose-500 transition"
                            @click="onDelete">
                            删除项目
                        </button>
                    </div>
                </div>

                <!-- 当前任务状态机(常驻；无活跃 run 时渲染空闲骨架：节点全灰、漏斗全 —) -->
                <div
                    class="rounded-2xl border border-orange-100 bg-white shadow-sm overflow-hidden ring-4 ring-orange-50 mb-5">
                    <pipeline-state-machine :run="activeRun">
                        <template #actions>
                            <button
                                v-if="
                                    activeRun &&
                                    activeRun.state !== RunState.PENDING &&
                                    activeRun.status === RunStatus.RUNNING
                                "
                                class="px-2.5 py-1 rounded-md border border-rose-200 text-rose-500 text-xs hover:bg-rose-50 transition"
                                @click="onCancelRun">
                                取消
                            </button>
                        </template>
                    </pipeline-state-machine>
                </div>

                <!-- 历史任务(通栏) -->
                <div
                    class="rounded-xl border border-slate-200 bg-white shadow-sm flex flex-col flex-1">
                    <div
                        class="px-5 py-3.5 border-b border-slate-100 shrink-0 flex items-center justify-between">
                        <h2 class="text-sm font-semibold text-slate-800">历史任务</h2>
                        <page-switcher
                            :page-num="historyPageNum"
                            :total-page-num="historyTotalPageNum"
                            @change="onHistoryPage" />
                    </div>
                    <div class="flex-1 overflow-y-auto min-h-[180px]">
                        <run-history-table :runs="historyRuns" />
                    </div>
                </div>
            </section>
        </div>

        <project-form-modal
            v-model:open="formOpen"
            :mode="formMode"
            :project="currentProject"
            :username="userStore.username"
            @submitted="onSubmitted" />
    </div>
</template>

<style scoped lang="scss"></style>
