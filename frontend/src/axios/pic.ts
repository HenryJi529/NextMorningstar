import axios from './index';
import type { AxiosProgressEvent } from 'axios';
const VITE_API_BASE_PATH = import.meta.env.VITE_API_BASE_PATH;

export const getUsage = () => {
    return axios({
        url: VITE_API_BASE_PATH + '/pic/resource/usage',
        method: 'get',
    });
};

const API_CONFIG_GITHUB_PAT = VITE_API_BASE_PATH + '/pic/config/github-pat';
export const setGithubPat = (pat: string) => {
    return axios({
        url: API_CONFIG_GITHUB_PAT,
        method: 'post',
        data: {
            pat: pat,
        },
    });
};
export const clearGithubPat = () => {
    return axios.delete(API_CONFIG_GITHUB_PAT);
};
export const getGithubPat = () => {
    return axios.get(API_CONFIG_GITHUB_PAT);
};

const API_CONFIG_COMPRESSION_QUALITY = VITE_API_BASE_PATH + '/pic/config/compression-quality';
export const getCompressionQuality = () => {
    return axios.get(API_CONFIG_COMPRESSION_QUALITY);
};
export const setCompressionQuality = (quality: string) => {
    const params = new URLSearchParams({
        compressionQuality: quality,
    });
    return axios({
        url: API_CONFIG_COMPRESSION_QUALITY,
        method: 'post',
        params: params,
    });
};

export const getSecretKey = () => {
    return axios.get(VITE_API_BASE_PATH + '/pic/config/secret-key');
};

export const getGithubAccount = () => {
    return axios({
        url: VITE_API_BASE_PATH + '/pic/config/github-account',
        method: 'get',
    });
};

export const uploadSmallImage = (filename: string, content: string) => {
    return axios({
        url: VITE_API_BASE_PATH + '/pic/resource/image/small',
        method: 'post',
        data: {
            filename: filename,
            content: content,
        },
    });
};

export const uploadLargeImage = (
    file: File,
    onUploadProgress: ((progressEvent: AxiosProgressEvent) => void) | undefined
) => {
    const formData = new FormData();
    formData.append('file', file);
    return axios({
        url: VITE_API_BASE_PATH + '/pic/resource/image/large',
        method: 'post',
        data: formData,
        onUploadProgress: onUploadProgress,
    });
};

const API_RESOURCE_IMAGE = VITE_API_BASE_PATH + '/pic/resource/image';
export const deleteImage = (path: string) => {
    const params = new URLSearchParams();
    params.append('path', path);
    return axios.delete(`${API_RESOURCE_IMAGE}?${params.toString()}`);
};
export const getImageList = (
    pageNum: number,
    pageSize: number,
    startDate: string,
    endDate: string
) => {
    const params = new URLSearchParams();
    params.append('pageNum', pageNum.toString());
    params.append('pageSize', pageSize.toString());
    params.append('startDate', startDate);
    params.append('endDate', endDate);
    return axios({
        url: API_RESOURCE_IMAGE,
        method: 'get',
        params: params,
    });
};
