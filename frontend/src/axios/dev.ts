import axios from './index';
import type { AxiosResponse } from 'axios';
import type { PageResult, R } from '@/types/common';
import type {
    CreateProjectRequestVo,
    ProjectDetail,
    RunDetail,
    RunStatus,
    SortDir,
    Stats,
    UpdateProjectRequestVo,
} from '@/types/dev';

/* 项目 */
const API_PROJECT = '/dev/project';
export const listProject = (params: {
    pageNum: number;
    pageSize: number;
}): Promise<AxiosResponse<R<PageResult<ProjectDetail>>>> => {
    return axios.get(API_PROJECT, { params });
};
export const getProjectById = (id: string): Promise<AxiosResponse<R<ProjectDetail>>> => {
    return axios.get(`${API_PROJECT}/${id}`);
};
export const createProject = (
    data: CreateProjectRequestVo
): Promise<AxiosResponse<R<ProjectDetail>>> => {
    return axios({
        url: API_PROJECT,
        method: 'post',
        data: data,
    });
};
export const updateProject = (
    id: string,
    data: UpdateProjectRequestVo
): Promise<AxiosResponse<R<ProjectDetail>>> => {
    return axios({
        url: `${API_PROJECT}/${id}`,
        method: 'patch',
        data: { ...data, id },
    });
};
export const deleteProject = (id: string): Promise<AxiosResponse<R<void>>> => {
    return axios.delete(`${API_PROJECT}/${id}`);
};

/* 任务 */
const API_RUN = '/dev/run';
export const getAllRun = (params: {
    projectId?: string;
    statuses?: RunStatus[];
    pageNum: number;
    pageSize: number;
    /** 排序方向(必传):ASC 从早到晚(当前任务贴合分发顺序);DESC 最新在前(历史/最近完成/活跃探测) */
    sortDir: SortDir;
}): Promise<AxiosResponse<R<PageResult<RunDetail>>>> => {
    // statuses 逗号拼接,Spring 按逗号分隔绑定 List<枚举>
    return axios.get(API_RUN, { params: { ...params, statuses: params.statuses?.join(',') } });
};
export const triggerRun = (projectId: string): Promise<AxiosResponse<R<RunDetail>>> => {
    return axios({
        url: API_RUN,
        method: 'post',
        params: { projectId },
    });
};
export const getRunById = (id: string): Promise<AxiosResponse<R<RunDetail>>> => {
    return axios.get(`${API_RUN}/${id}`);
};
export const cancelRun = (id: string): Promise<AxiosResponse<R<void>>> => {
    return axios.delete(`${API_RUN}/${id}`);
};

/* 工坊管理员 */
const API_ADMIN = '/dev/admin';
export const getStats = (): Promise<AxiosResponse<R<Stats>>> => {
    return axios.get(`${API_ADMIN}/stats`);
};
export const adminCancelRun = (id: string): Promise<AxiosResponse<R<void>>> => {
    return axios.delete(`${API_ADMIN}/run/${id}`);
};
export const adminToggleSchedule = (id: string): Promise<AxiosResponse<R<void>>> => {
    return axios.post(`${API_ADMIN}/project/${id}/schedule`);
};
