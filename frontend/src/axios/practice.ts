import axios from '@/axios/index';
const VITE_API_BASE_PATH = import.meta.env.VITE_API_BASE_PATH;

export const parseExcel = (file: File) => {
    const data = new FormData();
    data.append('file', file);
    return axios({
        method: 'POST',
        url: VITE_API_BASE_PATH + '/practice/parse-excel',
        data: data,
    });
};

export const longTask = (data: object, config: object) => {
    return axios({
        method: 'POST',
        url: VITE_API_BASE_PATH + '/practice/long-task',
        data: data,
        ...config,
    });
};
