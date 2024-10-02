import axios from '@/axios/index';

export const parseExcel = (file: File) => {
    const data = new FormData();
    data.append('file', file);
    return axios({
        method: 'POST',
        url: '/lab/parse-excel',
        data: data,
    });
};

export const longTask = (data: object, config: object) => {
    return axios({
        method: 'POST',
        url: '/lab/long-task',
        data: data,
        ...config,
    });
};
