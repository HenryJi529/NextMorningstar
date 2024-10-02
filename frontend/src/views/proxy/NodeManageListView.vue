<script lang="ts" setup>
import type { ListResourceConfig, R } from '@/types/common';
import axios from '@/axios/index';
import { API_PROXY_NODE_ALL, API_PROXY_NODE_ONE } from '@/constants/api';
import { onMounted, ref } from 'vue';
import CommonManageListTable from '@/components/manage/CommonManageListTable.vue';
import type { NodeDetail } from '@/types/proxy';

const config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: 'id',
            sortable: true,
            class: 'w-24',
            center: true,
        },
        {
            name: 'name',
            sortable: false,
            class: 'flex-1',
            center: true,
        },
        {
            name: 'protocol',
            sortable: false,
            class: 'hidden md:flex w-32',
            center: true,
        },
        {
            name: 'subId',
            sortable: true,
            class: 'w-24',
            center: true,
        },
        {
            name: 'updateTime',
            sortable: true,
            class: 'w-52',
            center: true,
        },
    ],
};

let originItems: NodeDetail[];
const isLoaded = ref(false);

onMounted(async () => {
    const response: R<NodeDetail[]> = (await axios.get(API_PROXY_NODE_ALL)).data;
    originItems = response.data;
    isLoaded.value = true;
});
</script>

<template>
    <div v-if="isLoaded">
        <common-manage-list-table
            :config="config"
            :delete-api="API_PROXY_NODE_ONE"
            :origin-items="originItems"
            app="proxy"
            resource="node" />
    </div>
</template>

<style lang="scss" scoped></style>
