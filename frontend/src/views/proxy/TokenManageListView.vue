<script lang="ts" setup>
import type { ListResourceConfig, R } from '@/types/common';
import axios from '@/axios/index';
import { API_PROXY_TOKEN_ALL, API_PROXY_TOKEN_ONE } from '@/constants/api';
import { onMounted, ref } from 'vue';
import CommonManageListTable from '@/components/manage/CommonManageListTable.vue';
import type { Token } from '@/types/proxy';

const config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: 'id',
            sortable: true,
            class: 'flex-1 lg:flex-none lg:w-32',
            center: true,
        },
        {
            name: 'name',
            sortable: false,
            class: 'flex-1 lg:flex-none lg:w-48',
            center: true,
        },
        {
            name: 'value',
            sortable: false,
            class: 'hidden lg:flex flex-1',
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

let originItems: Token[];
const isLoaded = ref(false);

onMounted(async () => {
    const response: R<Token[]> = (await axios.get(API_PROXY_TOKEN_ALL)).data;
    originItems = response.data;
    isLoaded.value = true;
});
</script>

<template>
    <div v-if="isLoaded">
        <common-manage-list-table
            :config="config"
            :delete-api="API_PROXY_TOKEN_ONE"
            :origin-items="originItems"
            app="proxy"
            resource="token" />
    </div>
</template>

<style lang="scss" scoped></style>
