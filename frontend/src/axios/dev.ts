import axios from './index';
import type { AxiosResponse } from 'axios';
import type { R } from '@/types/common';
import type {
    CreateProjectRequestVo,
    ProjectDetail,
    RunDetail,
    Stats,
    UpdateProjectRequestVo,
} from '@/types/dev';

/* 项目 */
const API_PROJECT = '/dev/project';
export const listProject = (): Promise<AxiosResponse<R<ProjectDetail[]>>> => {
    return axios.get(API_PROJECT);
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
export const getAllRun = (params?: {
    projectId?: string;
    adminId?: string;
}): Promise<AxiosResponse<R<RunDetail[]>>> => {
    return axios.get(API_RUN, { params });
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
export const adminDisableProject = (id: string): Promise<AxiosResponse<R<void>>> => {
    return axios.post(`${API_ADMIN}/project/${id}/disable`);
};
