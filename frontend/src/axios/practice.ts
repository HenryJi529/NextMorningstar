import axios from '@/axios/index';

export const parseExcel = (file: File) => {
    const data = new FormData();
    data.append('file', file);
    return axios({
        method: 'POST',
        url: '/practice/parse-excel',
        data: data,
    });
};

export const longTask = (data: object, config: object) => {
    return axios({
        method: 'POST',
        url: '/practice/long-task',
        data: data,
        ...config,
    });
};
