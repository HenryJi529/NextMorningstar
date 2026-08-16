import dayjs from 'dayjs';
import type { Component } from 'vue';
import {
    ClearOutlined,
    CloudUploadOutlined,
    RocketOutlined,
    SafetyCertificateOutlined,
    ScanOutlined,
    SyncOutlined,
    ToolOutlined,
} from '@ant-design/icons-vue';
import { ActionType, RunPrStatus, RunState, RunStatus, RunTriggerType } from '@/types/dev';

/* 流水线 7 节点，与后端 Action.Type 主链一一对应；states = 该节点覆盖的后端细粒度状态(ING 进行中 + ED 完成，RESTORING/RESTORED 视觉落回修复节点);icon = 未到达/当前节点内嵌图标(已完成节点统一打勾) */
export interface PipelineNode {
    key: string;
    label: string;
    desc: string;
    states: RunState[];
    icon: Component;
}

export const PIPELINE_NODES: PipelineNode[] = [
    {
        key: 'STARTING',
        label: '启动',
        desc: '拉起容器挂载共享卷',
        states: [RunState.STARTING, RunState.STARTED],
        icon: RocketOutlined,
    },
    {
        key: 'SYNCING',
        label: '同步',
        desc: '后端 git 拉取最新代码',
        states: [RunState.SYNCING, RunState.SYNCED],
        icon: SyncOutlined,
    },
    {
        key: 'SCANNING',
        label: '扫描',
        desc: 'SonarQube + AI 双通道发现',
        states: [RunState.SCANNING, RunState.SCANNED],
        icon: ScanOutlined,
    },
    {
        key: 'FIXING',
        label: '修复',
        desc: 'Claude 逐 issue 修复并 MCP 自查',
        states: [RunState.FIXING, RunState.FIXED, RunState.RESTORING, RunState.RESTORED],
        icon: ToolOutlined,
    },
    {
        key: 'VERIFYING',
        label: '验证',
        desc: 'SonarQube 复扫 + Claude 语义判定',
        states: [RunState.VERIFYING, RunState.VERIFIED],
        icon: SafetyCertificateOutlined,
    },
    {
        key: 'SUBMITTING',
        label: '提交',
        desc: '推送分支并创建 PR',
        states: [RunState.SUBMITTING, RunState.SUBMITTED],
        icon: CloudUploadOutlined,
    },
    {
        key: 'CLEANING',
        label: '清理',
        desc: '回收容器与现场',
        states: [RunState.CLEANING, RunState.CLEANED],
        icon: ClearOutlined,
    },
];

/* run.state → 当前节点下标；PENDING/FAILED 不属于任何节点，findIndex 未命中天然返回 -1 */
export const stateToStep = (state: RunState): number =>
    PIPELINE_NODES.findIndex(node => node.states.includes(state));

export const ACTION_TYPE_LABEL: Record<ActionType, string> = {
    [ActionType.START]: '启动',
    [ActionType.SYNC]: '同步',
    [ActionType.SCAN]: '扫描',
    [ActionType.FIX]: '修复',
    [ActionType.VERIFY]: '验证',
    [ActionType.SUBMIT]: '提交',
    [ActionType.CLEAN]: '清理',
    [ActionType.RESTORE]: '恢复',
};

const STATE_LABEL: Record<RunState, string> = {
    [RunState.PENDING]: '排队中',
    [RunState.STARTING]: '启动容器',
    [RunState.STARTED]: '启动容器',
    [RunState.SYNCING]: '同步代码',
    [RunState.SYNCED]: '同步代码',
    [RunState.SCANNING]: '扫描问题',
    [RunState.SCANNED]: '扫描问题',
    [RunState.FIXING]: 'AI 修复',
    [RunState.FIXED]: 'AI 修复',
    [RunState.VERIFYING]: '验证修复',
    [RunState.VERIFIED]: '验证修复',
    [RunState.SUBMITTING]: '提交 PR',
    [RunState.SUBMITTED]: '提交 PR',
    [RunState.CLEANING]: '清理现场',
    [RunState.CLEANED]: '清理现场',
    [RunState.RESTORING]: '恢复现场',
    [RunState.RESTORED]: '恢复现场',
    [RunState.FAILED]: '已失败',
};

export const stateLabel = (state: RunState): string => STATE_LABEL[state];

export const triggerLabel = (triggerType: RunTriggerType): string => {
    return {
        [RunTriggerType.MANUAL]: '手动',
        [RunTriggerType.SCHEDULED]: '调度',
    }[triggerType];
};

export const runStatusBadgeClass = (status: RunStatus): string => {
    return (
        {
            [RunStatus.SUCCEEDED]: 'bg-emerald-50 text-emerald-600 border-emerald-200',
            [RunStatus.FAILED]: 'bg-rose-50 text-rose-500 border-rose-200',
            [RunStatus.CANCELED]: 'bg-slate-50 text-slate-400 border-slate-200',
            [RunStatus.CANCELING]: 'bg-amber-50 text-amber-500 border-amber-200',
            [RunStatus.RUNNING]: 'bg-orange-50 text-orange-500 border-orange-200',
        }[status] ?? 'bg-slate-50 text-slate-400 border-slate-200'
    );
};

export const prStatusBadgeClass = (status: RunPrStatus): string => {
    return (
        {
            [RunPrStatus.MERGED]: 'bg-violet-50 text-violet-500 border-violet-200',
            [RunPrStatus.OPEN]: 'bg-orange-50 text-orange-500 border-orange-200',
            [RunPrStatus.CLOSED]: 'bg-slate-50 text-slate-400 border-slate-200',
        }[status] ?? 'bg-slate-50 text-slate-400 border-slate-200'
    );
};

export const prStatusDividerClass = (status: RunPrStatus): string => {
    return (
        {
            [RunPrStatus.MERGED]: 'bg-violet-300',
            [RunPrStatus.OPEN]: 'bg-orange-300',
            [RunPrStatus.CLOSED]: 'bg-slate-300',
        }[status] ?? 'bg-slate-300'
    );
};

export const fmtTime = (iso?: string): string => {
    return iso ? dayjs(iso).format('YYYY/MM/DD HH:mm:ss') : '—';
};

/* 仓库地址显示 owner/repo 短形，跳转仍用完整链接 */
export const repoShort = (link?: string): string => {
    return link ? link.replace(/\/+$/, '').split('/').slice(-2).join('/') : '';
};

/* 耗时：统一用分，1 位小数去尾零(45s → 0.8 分，75m → 75 分) */
export const fmtDuration = (startIso: string, endIso: string): string => {
    const totalSeconds = Math.max(0, dayjs(endIso).diff(dayjs(startIso), 'second'));
    return `${+(totalSeconds / 60).toFixed(1)} 分`;
};

/* 进行中任务的实时已耗时：H:MM:SS 秒表，由调用方 1s ticker 驱动 */
export const fmtElapsed = (startIso: string, nowIso: string): string => {
    const totalSeconds = Math.max(0, dayjs(nowIso).diff(dayjs(startIso), 'second'));
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    return `${hours}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
};
