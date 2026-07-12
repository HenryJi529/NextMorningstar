import { defineStore } from 'pinia';
import axios from '@/axios/index';

import type { R } from '@/types/common';
import type { LogoutResponseVo, UserPrivateInfo } from '@/types/system';
import { LocalStorageKey } from '@/constants/storage';
import {
    API_USER_AUTH_HEARTBEAT,
    API_USER_AUTH_LOGOUT,
    API_USER_AUTH_REFRESH,
    API_USER_INFO_PRIVATE,
} from '@/constants/api';
import { getAvatarLink } from '@/utils/media';
import { ResponseCode } from '@/constants/response';
import { jwtDecode } from 'jwt-decode';

let heartbeatTimerId: number | undefined;
let refreshTimerId: number | undefined;

export const useUserStore = defineStore('users', {
    state: (): UserPrivateInfo => {
        return {
            id: '',
            username: '',
            isLocked: true,
            email: '',
            permissions: [],
            nickname: '',
            avatar: null,
            createTime: '',
            updateTime: '',
        };
    },
    getters: {
        avatarLink: state => getAvatarLink(state.avatar),
        isAuthenticated: state => state.id != '',
    },
    actions: {
        clear() {
            if (heartbeatTimerId) {
                window.clearInterval(heartbeatTimerId);
                heartbeatTimerId = undefined;
            }
            if (refreshTimerId) {
                window.clearTimeout(refreshTimerId);
                refreshTimerId = undefined;
            }
            localStorage.removeItem(LocalStorageKey.ACCESS_TOKEN);
            this.$reset();
        },

        async loadUser() {
            if (!localStorage.getItem(LocalStorageKey.ACCESS_TOKEN)) {
                return;
            }

            const response: R<UserPrivateInfo> = (await axios.get(API_USER_INFO_PRIVATE)).data;
            if (response.code !== ResponseCode.SUCCESS) {
                this.clear();
                return;
            }
            const userPrivateInfo = response.data;
            Object.assign(this, userPrivateInfo);
            await this.heartbeat();
            this.refresh();
        },

        async handleLogout() {
            const response: R<LogoutResponseVo> = (await axios.post(API_USER_AUTH_LOGOUT, {})).data;
            if (response.code !== ResponseCode.SUCCESS) {
                return;
            }
            this.clear();
        },

        refresh() {
            const one = async () => {
                const response: R<string> = (await axios.post(API_USER_AUTH_REFRESH, {})).data;
                if (response.code !== ResponseCode.SUCCESS) {
                    this.clear();
                    return;
                }
                localStorage.setItem(LocalStorageKey.ACCESS_TOKEN, response.data);
                this.refresh();
            };
            if (refreshTimerId) {
                window.clearTimeout(refreshTimerId);
            }
            const accessToken = localStorage.getItem(LocalStorageKey.ACCESS_TOKEN);
            if (!accessToken) {
                return;
            }
            const timeout = (jwtDecode(accessToken).exp as number) * 1000 - Date.now() - 1000 * 10;
            refreshTimerId = window.setTimeout(
                async () => {
                    refreshTimerId = undefined;
                    await one();
                },
                Math.max(timeout, 0)
            );
        },

        async heartbeat() {
            const one = async () => {
                const response: R<object> = (await axios.get(API_USER_AUTH_HEARTBEAT)).data;
                if (response.code !== ResponseCode.SUCCESS) {
                    return;
                }
            };
            await one();
            if (heartbeatTimerId) {
                window.clearInterval(heartbeatTimerId);
            }
            heartbeatTimerId = window.setInterval(async () => {
                await one();
            }, 1000 * 10);
        },
    },
});
