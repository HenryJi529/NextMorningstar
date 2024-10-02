<script lang="ts" setup>
import type { ListResourceConfig, R } from '@/types/common';
import axios from '@/axios/index';
import { API_USER_MANAGE_INFO_ALL } from '@/constants/api';
import { onMounted, ref } from 'vue';
import CommonManageListTable from '@/components/manage/CommonManageListTable.vue';
import type { UserManageableInfo, UserPrivateInfo } from '@/types/auth';
import { ROLE_PREFIX } from '@/constants/auth';

const config: ListResourceConfig = {
    identityField: 'username',
    itemConfigs: [
        {
            name: 'username',
            sortable: true,
            class: 'w-36',
            center: true,
        },
        {
            name: 'email',
            sortable: true,
            class: 'hidden 2xl:flex w-64',
            center: true,
        },
        {
            name: 'isLocked',
            sortable: false,
            class: 'w-[6rem]',
            center: true,
        },
        {
            name: 'roles',
            sortable: false,
            class: 'flex-1',
            center: true,
        },
        {
            name: 'nickname',
            sortable: false,
            class: 'hidden 2xl:flex w-32',
            center: true,
        },
        {
            name: 'created',
            sortable: true,
            class: 'w-32',
            center: true,
        },
        {
            name: 'updated',
            sortable: true,
            class: 'w-32',
            center: true,
        },
    ],
};

let originItems: UserManageableInfo[];
const isLoaded = ref(false);

onMounted(async () => {
    const response: R<UserPrivateInfo[]> = (await axios.get(API_USER_MANAGE_INFO_ALL)).data;
    originItems = response.data.map(userPrivateInfo => {
        return {
            id: userPrivateInfo.id,
            username: userPrivateInfo.username,
            isLocked: userPrivateInfo.isLocked,
            email: userPrivateInfo.email,
            roles: userPrivateInfo.permissions
                .filter(permission => {
                    return permission.startsWith(ROLE_PREFIX);
                })
                .map(permission => permission.substring(5, permission.length)),
            nickname: userPrivateInfo.nickname,
            avatar: userPrivateInfo.avatar,
            created: userPrivateInfo.createTime.split(' ')[0],
            updated: userPrivateInfo.updateTime.split(' ')[0],
        };
    });

    isLoaded.value = true;
    setTimeout(() => {
        const addButton = document.querySelector('#admin-user-add-button') as HTMLButtonElement;
        addButton.classList.add('btn-disabled');
    }, 1);
});
</script>

<template>
    <div v-if="isLoaded">
        <common-manage-list-table
            :config="config"
            :origin-items="originItems"
            app="admin"
            resource="user" />
    </div>
</template>

<style lang="scss" scoped></style>
