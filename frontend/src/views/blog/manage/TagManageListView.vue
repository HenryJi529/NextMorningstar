<script lang="ts" setup>
import type { ListResourceConfig, R } from '@/types/common';
import type { TagDetail } from '@/types/blog';
import axios from '@/axios/index';
import { API_BLOG_TAG_ALL, API_BLOG_TAG_ONE } from '@/constants/api';
import { onMounted, ref } from 'vue';
import CommonManageListTable from '@/components/manage/CommonManageListTable.vue';

const config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: 'id',
            sortable: true,
            class: 'w-32',
            center: true,
        },
        {
            name: 'name',
            sortable: false,
            class: 'flex-1',
            center: true,
        },
        {
            name: 'count',
            sortable: true,
            class: 'w-32',
            center: true,
        },
    ],
};

let originItems: TagDetail[];
const isLoaded = ref(false);

onMounted(async () => {
    const response: R<TagDetail[]> = (await axios.get(API_BLOG_TAG_ALL)).data;
    originItems = response.data;
    isLoaded.value = true;
});
</script>

<template>
    <div v-if="isLoaded">
        <common-manage-list-table
            :config="config"
            :delete-api="API_BLOG_TAG_ONE"
            :origin-items="originItems"
            app="blog"
            resource="tag" />
    </div>
</template>

<style lang="scss" scoped></style>
