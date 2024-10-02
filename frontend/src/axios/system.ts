import axios from '@/axios/index';

export const fetchSysParam = (paramName: string) => {
    return axios({
        method: 'GET',
        url: '/system/param/search',
        params: {
            name: paramName,
        },
    });
};
