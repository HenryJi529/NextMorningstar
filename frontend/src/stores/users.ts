import { defineStore } from 'pinia'
import axios from "axios";

import type { R } from '@/types/common';
import type {LogoutResponseVo, UserPrivateInfo} from '@/types/auth';
import {TOKEN} from "@/constants/LocalStorageConstant";
import {API_USER_INFO_PRIVATE, API_USER_AUTH_LOGOUT} from "@/constants/ApiConstant";
import {getAvatarLink} from "@/utils/handleMedia";

export const useUserStore = defineStore('users', {
  state: (): UserPrivateInfo => {
    return {
      id: "",
      username: "",
      isLocked: true,
      isAdmin: false,
      email: null,
      phone: null,
      permissions: [],
      nickname: "",
      sex: "",
      avatar: null,
      createTime: "",
      updateTime: "",
    }
  },
  getters: {
    avatarLink: (state) => getAvatarLink(state.avatar as string),
    isAuthenticated: (state) => state.id != ""
  },
  actions: {
    async loadUser() {
      if(!localStorage.getItem(TOKEN)){
        return;
      }

      const response : R<UserPrivateInfo> = (await axios.get(API_USER_INFO_PRIVATE, {
        headers: {
          Authorization: localStorage.getItem(TOKEN),
        }
      })).data;
      if(response.code != 1){
        localStorage.removeItem(TOKEN);
        return;
      }
      const userPrivateInfo = response.data;
      Object.assign(this, userPrivateInfo);
    },

    async handleLogout() {
      const response: R<LogoutResponseVo> = (await axios.post(API_USER_AUTH_LOGOUT, {},{
        headers: {
          Authorization: localStorage.getItem(TOKEN),
        }
      })).data;
      if(response.code != 1){
        return;
      }
      localStorage.removeItem(TOKEN);
      this.$reset();
    },
  },
})
