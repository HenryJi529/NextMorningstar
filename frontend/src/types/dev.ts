/* 枚举 */
export enum RunState {
    PENDING = 'PENDING',
    STARTING = 'STARTING',
    STARTED = 'STARTED',
    SYNCING = 'SYNCING',
    SYNCED = 'SYNCED',
    SCANNING = 'SCANNING',
    SCANNED = 'SCANNED',
    FIXING = 'FIXING',
    FIXED = 'FIXED',
    VERIFYING = 'VERIFYING',
    VERIFIED = 'VERIFIED',
    SUBMITTING = 'SUBMITTING',
    SUBMITTED = 'SUBMITTED',
    CLEANING = 'CLEANING',
    CLEANED = 'CLEANED',
    RESTORING = 'RESTORING',
    RESTORED = 'RESTORED',
    FAILED = 'FAILED',
}

export enum RunStatus {
    RUNNING = 'RUNNING',
    SUCCEEDED = 'SUCCEEDED',
    FAILED = 'FAILED',
    CANCELING = 'CANCELING',
    CANCELED = 'CANCELED',
}

export enum RunTriggerType {
    MANUAL = 'MANUAL',
    SCHEDULED = 'SCHEDULED',
}

export enum RunPrStatus {
    OPEN = 'OPEN',
    CLOSED = 'CLOSED',
    MERGED = 'MERGED',
}

export enum ActionType {
    START = 'START',
    SYNC = 'SYNC',
    SCAN = 'SCAN',
    FIX = 'FIX',
    VERIFY = 'VERIFY',
    SUBMIT = 'SUBMIT',
    CLEAN = 'CLEAN',
    RESTORE = 'RESTORE',
}

export enum ActionStatus {
    SUCCEEDED = 'SUCCEEDED',
    FAILED = 'FAILED',
    RUNNING = 'RUNNING',
}

export enum SortDir {
    ASC = 'ASC',
    DESC = 'DESC',
}

/* PO */
export interface Project {
    id: string;
    adminId: string;
    name: string;
    link: string;
    branchName: string;
    description?: string;
    enabled: boolean;
    maxSonarIssuesPerRun: number;
    maxAiIssuesPerRun: number;
    createTime: string;
    updateTime: string;
}

export interface Run {
    id: string;
    projectId: string;
    state: RunState;
    status: RunStatus;
    triggerType: RunTriggerType;
    prId?: number;
    prStatus?: RunPrStatus;
    createTime: string;
    updateTime: string;
}

/* VO */
export interface CreateProjectRequestVo {
    name: string;
    link: string;
    branchName: string;
    description?: string;
    maxSonarIssuesPerRun?: number;
    maxAiIssuesPerRun?: number;
}

export interface UpdateProjectRequestVo {
    name?: string;
    branchName?: string;
    description?: string;
    maxSonarIssuesPerRun?: number;
    maxAiIssuesPerRun?: number;
    enabled?: boolean;
}

/* BO */
export interface ProjectDetail extends Project {
    adminName?: string;
}

export interface RunDetail extends Run {
    projectName?: string;
    prLink?: string;
    waitSeconds?: number;
    execSeconds?: number;
    deliveredIssueCount?: number;
    scannedIssueCount?: number;
    selectedIssueCount?: number;
    currentFixedIssueCount?: number;
    currentVerifiedIssueCount?: number;
    actionAttemptBriefs?: ActionAttemptBrief[];
}

export interface ActionAttemptBrief {
    actionType: ActionType;
    attemptNo: number;
    status: ActionStatus;
    createTime: string;
    updateTime: string;
}

export interface Stats {
    projectCount: number;
    enabledProjectCount: number;
    executingRunCount: number;
    pendingRunCount: number;
    maxConcurrency: number;
    scheduledStartTime: string;
    scheduledEndTime: string;
    deliveredIssueCount: number;
    acceptedIssueCount: number;
    prTotal: number;
    prMerged: number;
    savedPersonDays: number;
}
