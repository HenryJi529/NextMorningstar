<script lang="ts" setup>

import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import type {R} from "@/types/common";
import axios from '@/axios/index';
import {
    API_USER_MANAGE_INFO_ONE,
    API_USER_MANAGE_LOCK,
    API_USER_MANAGE_RECOVER,
    API_USER_MANAGE_ROLE
} from "@/constants/ApiConstant";
import type {UserPrivateInfo} from "@/types/auth";
import AlertSuccess from "@/components/common/AlertSuccess.vue";
import AlertError from "@/components/common/AlertError.vue";

const route = useRoute();
const itemNotExist = ref<boolean>(false);
const id = route.params.id as string;
const email = ref("");
const isLocked = ref(false);
const currRoles = ref<string[]>([]);
const allRoles = ref<string[]>([]);

const successMessage = ref("");
const errorMessage = ref("");

const clearMessage = () => {
    errorMessage.value = "";
    successMessage.value = "";
}

const updateUserLockStatus = async () => {
    const response: R<object> = (await axios.get(`${API_USER_MANAGE_LOCK}?id=${id}&isLocked=${isLocked.value}`)).data;
    if (response.code <= 0) {
        errorMessage.value = response.msg;
    } else {
        successMessage.value = "锁定状态已更新";
    }
    setTimeout(clearMessage, 3000);
}

const sendRecoverEmailToUser = async () => {
    const response: R<object> = (await axios.get(`${API_USER_MANAGE_RECOVER}?id=${id}&email=${email.value}`)).data;
    if (response.code <= 0) {
        errorMessage.value = response.msg;
    } else {
        successMessage.value = "恢复邮件已发送";
    }
    setTimeout(clearMessage, 3000);
}

const updateUserRoleSetting = async () => {
    const response: R<object> = (await axios.put(
        API_USER_MANAGE_ROLE,
        {
            "userId": id,
            "roleTags": currRoles.value
        })).data;
    if (response.code <= 0) {
        errorMessage.value = response.msg;
    } else {
        successMessage.value = "角色设置已更新";
    }
}

const loadPrivateInfo = async () => {
    const response: R<UserPrivateInfo> = (await axios.get(
            API_USER_MANAGE_INFO_ONE.replace('{id}', id)
        )
    ).data;
    if (response.code <= 0) {
        itemNotExist.value = true;
        return;
    }
    isLocked.value = response.data.isLocked;
    currRoles.value = response.data.permissions.filter(permission => {
        return permission.startsWith('ROLE_');
    }).map(permission => permission.substring(5, permission.length));
}

const loadAllRoles = async () => {
    const response: R<string[]> = (await axios.get(API_USER_MANAGE_ROLE)).data;
    allRoles.value = response.data;
}

onMounted(async () => {
    await loadPrivateInfo();
    await loadAllRoles();
})
</script>

<template>
    <div class="flex justify-center items-center mt-10">
        <div v-if="itemNotExist">
            用户不存在
        </div>
        <div v-else class="w-[40rem] p-4">
            <div class="mb-4">
                <alert-success v-if="successMessage">
                    {{ successMessage }}
                </alert-success>
                <alert-error v-if="errorMessage">
                    {{ errorMessage }}
                </alert-error>
            </div>
            <div>
                <div class="text-2xl text-center mb-4">锁定与解锁</div>
                <div class="flex justify-center items-center space-x-4 mb-4">
                    <div>
                        <font-awesome-icon :icon="['fas', 'lock-open']"/>
                    </div>
                    <input v-model="isLocked" class="toggle" type="checkbox"/>
                    <div>
                        <font-awesome-icon :icon="['fas', 'lock']"/>
                    </div>
                </div>
                <div class="btn w-full" @click="updateUserLockStatus">更新锁定状态</div>
            </div>
            <div class="divider"></div>
            <div class="mx-4">
                <div class="text-2xl text-center mb-4">账号恢复</div>
                <div class="flex-1 input input-bordered flex items-center space-x-2 mb-4">
                    <font-awesome-icon :icon="['fas', 'envelope']"/>
                    <input v-model="email" class="w-full" placeholder="用户邮箱" type="text">
                </div>
                <div class="btn w-full" @click="sendRecoverEmailToUser">发送恢复邮件</div>
            </div>
            <div class="divider"></div>
            <div class="mx-4">
                <div class="text-2xl text-center mb-4">角色设置</div>
                <div class="w-full flex items-center">
                    <div class="admin-manage-input flex items-center flex-wrap justify-start min-h-[54px]">
                        <template v-for="(currRole, ind) in currRoles">
                            <div
                                class="border-[1px] py-1 px-2 rounded-lg flex items-center justify-around space-x-2 border-gray-400 dark:border-gray-700 mx-1 my-[1px]">
                                <font-awesome-icon :icon="['fas', 'xmark']"
                                                   class="text-blue-400 hover:text-pink-500 cursor-pointer"
                                                   @click="currRoles.splice(ind, 1)"/>
                                <span>{{ currRole }}</span>
                            </div>
                        </template>
                    </div>
                </div>
                <div class="flex items-center flex-wrap pl-2 pr-8 pt-1 mb-4">
                    <template v-for="optionalRole in allRoles.filter(role => !currRoles.includes(role))">
                        <div
                            class="mx-1 my-1 border-gray-400 dark:border-gray-700 px-2 py-1 rounded-lg border-[1px] bg-white dark:bg-teal-700 hover:bg-blue-400 cursor-pointer"
                            @click="currRoles.push(optionalRole)">
                            {{ optionalRole }}
                        </div>
                    </template>
                </div>
                <div class="btn w-full" @click="updateUserRoleSetting">更新角色设置</div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>