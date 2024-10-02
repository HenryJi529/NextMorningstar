import axios from 'axios';
import {TOKEN} from "@/constants/LocalStorageConstant";
import {useUserStore} from "@/stores/users";
import router from '@/router';
// import NProgress from '@/utils/useNProgress';

const config = {
    timeout: 10 * 60 * 1000,
};
const _axios = axios.create(config);

_axios.interceptors.request.use(
    (config) => {
        // NProgress.start();
        const token = localStorage.getItem(TOKEN);
        if (token) {
            config.headers["Authorization"] = token;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)

_axios.interceptors.response.use(
    (response) => {
        // NProgress.done();
        if (response.data.code <= 0) {
            console.log(`code ${response.data.code}: ${response.data.message}`);
            if (response.data.code === -1 || response.data.code === -2) {
                useUserStore().clear();
                router.go(0);
                return Promise.reject("令牌失效/错误");
            }
        }
        return response;
    },
    (error) => {
        return Promise.reject(error);
    }
)

export default _axios;