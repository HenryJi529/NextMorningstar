import { defineStore } from 'pinia'
import axios from "axios";

import type { Response } from '@/types/common';
import type {User, UserPrivateInfo} from '@/types/auth';
import {TOKEN} from "@/constants/LocalStorageConstant";
import {API_USER_INFO_PRIVATE} from "@/constants/ApiConstant";
import {CDN_DOMAIN, USER_AVATAR_PREFIX} from "@/constants/MediaConstant";

export const useUserStore = defineStore('users', {
  state: (): User => {
    return {
      id: "",
      username: "",
      isLocked: true,
      isAdmin: false,
      email: null,
      phone: null,
      nickname: "",
      sex: "",
      avatar: null,
      createTime: "",
      updateTime: "",
    }
  },
  getters: {
    avatarLink: (state) => `https://${CDN_DOMAIN}/${USER_AVATAR_PREFIX}/${state.avatar}`,
    token: (state) => localStorage.getItem(TOKEN)
  },
  actions: {
    async loadUser() {
      const response : Response = (await axios.get(API_USER_INFO_PRIVATE, {
        headers: {
          'Authorization': localStorage.getItem(TOKEN),
        }
      })).data;
      if(response.code != 1){
        return;
      }
      const userPrivateInfo = response.data as UserPrivateInfo;
      Object.assign(this, userPrivateInfo);
    },

    handleLogout() {
      localStorage.removeItem(TOKEN);
      this.$reset();
    },
  },
})
