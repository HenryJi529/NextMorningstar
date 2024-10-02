<script setup lang="ts">
import type { ListResourceConfig, R } from '@/types/common';
import { type SysParam } from '@/types/system';
import { onMounted, ref } from 'vue';
import { API_SYSTEM_PARAM_ALL, API_SYSTEM_PARAM_ONE } from '@/constants/api';
import CommonManageListTable from '@/components/manage/CommonManageListTable.vue';
import axios from '@/axios';

const config: ListResourceConfig = {
    identityField: 'name',
    defaultSortOrders: ['updateTime_desc'],
    itemConfigs: [
        {
            name: 'name',
            sortable: true,
            class: 'w-40',
            center: true,
        },
        {
            name: 'value',
            sortable: false,
            class: 'flex-1',
            center: false,
        },
        {
            name: 'scope',
            sortable: false,
            class: 'w-32',
            center: true,
        },
        {
            name: 'status',
            sortable: false,
            class: 'w-24',
            center: true,
        },
        {
            name: 'remark',
            sortable: false,
            class: 'w-48 hidden xl:flex',
            center: false,
        },
        {
            name: 'createTime',
            sortable: true,
            class: 'w-32 hidden lg:flex',
            center: true,
            alias: 'created',
        },
        {
            name: 'updateTime',
            sortable: true,
            class: 'w-32 hidden md:flex',
            center: true,
            alias: 'updated',
        },
    ],
};

let originItems: SysParam[] = [];
const isLoaded = ref(false);

onMounted(async () => {
    const response: R<SysParam[]> = (await axios.get(API_SYSTEM_PARAM_ALL)).data;
    originItems = response.data;
    isLoaded.value = true;
});
</script>

<template>
    <div v-if="isLoaded">
        <common-manage-list-table
            :config="config"
            :delete-api="API_SYSTEM_PARAM_ONE"
            :origin-items="originItems"
            app="admin"
            resource="param" />
    </div>
</template>

<style scoped lang="scss"></style>
