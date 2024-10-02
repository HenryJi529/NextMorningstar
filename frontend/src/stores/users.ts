import { defineStore } from 'pinia';
import axios from '@/axios/index';

import type { R } from '@/types/common';
import type { LogoutResponseVo, UserPrivateInfo } from '@/types/auth';
import { LocalStorageKey } from '@/constants/storage';
import {
    API_USER_AUTH_HEARTBEAT,
    API_USER_AUTH_LOGOUT,
    API_USER_AUTH_REFRESH,
    API_USER_INFO_PRIVATE,
} from '@/constants/api';
import { getAvatarLink } from '@/utils/media';
import { ResponseCode } from '@/constants/response';

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
            localStorage.removeItem(LocalStorageKey.TOKEN);
            this.$reset();
        },

        async loadUser() {
            if (!localStorage.getItem(LocalStorageKey.TOKEN)) {
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
            window.setInterval(
                async () => {
                    const response: R<string> = (await axios.post(API_USER_AUTH_REFRESH, {})).data;
                    if (response.code !== ResponseCode.SUCCESS) {
                        this.clear();
                        return;
                    }
                    localStorage.setItem(LocalStorageKey.TOKEN, response.data);
                },
                1000 * 60 * 30
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
            window.setInterval(async () => {
                await one();
            }, 1000 * 10);
        },
    },
});
