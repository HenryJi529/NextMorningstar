import axios from 'axios';
import { LocalStorageKey } from '@/constants/storage';
import { useUserStore } from '@/stores/users';
import router from '@/router';
import { message } from 'ant-design-vue';
import { ResponseCode } from '@/constants/response';

const config = {
    timeout: 10 * 60 * 1000,
    baseURL: import.meta.env.VITE_API_BASE_PATH,
};
const _axios = axios.create(config);

_axios.interceptors.request.use(
    config => {
        // NProgress.start();
        const token = localStorage.getItem(LocalStorageKey.TOKEN);
        if (token) {
            config.headers['Authorization'] = token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

_axios.interceptors.response.use(
    response => {
        // NProgress.done();
        if (response.data.code <= 0) {
            console.log(`code ${response.data.code}: ${response.data.message}`);
            if (
                response.data.code === ResponseCode.TOKEN_INVALID ||
                response.data.code === ResponseCode.TOKEN_EXPIRED
            ) {
                useUserStore().clear();
                router.go(0);
                return Promise.reject('令牌失效/错误');
            }
        }
        return response;
    },
    error => {
        if (error.code === 'ECONNABORTED') {
            // 网络超时（包括前端主动超时、断网导致的超时）
            message.error({
                content: '请求超时，请稍后重试',
                class: 'ant-message-notice-custom',
                duration: 2,
            });
        } else if (error.code === 'ERR_NETWORK') {
            // 纯网络异常（断网、DNS解析失败、网络链路中断等）
            message.error({
                content: '网络异常，请检查网络连接后重试',
                class: 'ant-message-notice-custom',
                duration: 2,
            });
        } else if (error.code === 'ERR_BAD_RESPONSE') {
            // 服务器响应异常
            if (!document.querySelector('.ant-message-error')) {
                message.error({
                    content: '系统繁忙，请稍后重试',
                    class: 'ant-message-notice-custom',
                    duration: 2,
                });
            }
        }
        return Promise.reject(error);
    }
);

export default _axios;
