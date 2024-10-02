<script setup lang="ts">
import type {R} from "@/types/common";
import type {ListResourceConfig} from "@/types/common";
import axios from "axios";
import {
    API_USER_MANAGE_INFO_ALL
} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";
import {TOKEN} from "@/constants/LocalStorageConstant";
import type {UserManageableInfo, UserPrivateInfo} from "@/types/auth";


let config: ListResourceConfig = {
    identityField: 'username',
    itemConfigs: [
        {
            name: "username",
            sort: true,
            class: "w-36",
            center: true,
        },
        {
            name: "email",
            sort: true,
            class: "hidden 2xl:flex w-64",
            center: true,
        },
        {
            name: "isLocked",
            sort: false,
            class: "w-[6rem]",
            center: true,
        },
        {
            name: "roles",
            sort: false,
            class: "flex-1",
            center: true,
        },
        {
            name: "nickname",
            sort: false,
            class: "hidden 2xl:flex w-32",
            center: true,
        },
        {
            name: "created",
            sort: true,
            class: "w-32",
            center: true,
        },
        {
            name: "updated",
            sort: true,
            class: "w-32",
            center: true,
        }
    ]
}

let originItems: UserManageableInfo[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<UserPrivateInfo[]> = (await axios.get(API_USER_MANAGE_INFO_ALL,
        {
            headers: {
                Authorization: localStorage.getItem(TOKEN),
            }
        }
    )).data;
    originItems = response.data.map(userPrivateInfo => {
        return {
            id: userPrivateInfo.id,
            username: userPrivateInfo.username,
            isLocked: userPrivateInfo.isLocked,
            email: userPrivateInfo.email,
            roles: userPrivateInfo.permissions.filter(permission => {
                return permission.startsWith('ROLE_');
            }).map(permission => permission.substring(5, permission.length)),
            nickname: userPrivateInfo.nickname,
            avatar: userPrivateInfo.avatar,
            created: userPrivateInfo.createTime.split(" ")[0],
            updated: userPrivateInfo.updateTime.split(" ")[0],
        }
    });

    loaded.value = true;
    setTimeout(()=>{
        const addButton = document.querySelector('#admin-user-add-button') as HTMLButtonElement;
        addButton.classList.add('btn-disabled');
    },1);

})
</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            app="admin" resource="user"
            :origin-items="originItems"
            :config="config"/>
    </div>
</template>

<style scoped lang="scss">

</style>