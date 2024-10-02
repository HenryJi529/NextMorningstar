<script lang="ts" setup>

import type {ListResourceConfig, R} from "@/types/common";
import type {TagDetail} from "@/types/blog";
import axios from '@/axios/index';
import {API_BLOG_TAG_ALL, API_BLOG_TAG_ONE} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";

let config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: "id",
            sort: true,
            class: "w-32",
            center: true,
        },
        {
            name: "name",
            sort: false,
            class: "flex-1",
            center: true,
        },
        {
            name: "count",
            sort: true,
            class: "w-32",
            center: true,
        }
    ]
}

let originItems: TagDetail[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<TagDetail[]> = (await axios.get(API_BLOG_TAG_ALL)).data;
    originItems = response.data;
    loaded.value = true;
})


</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            :config="config" :delete-api="API_BLOG_TAG_ONE"
            :origin-items="originItems"
            app="blog"
            resource="tag"/>
    </div>
</template>

<style lang="scss" scoped>

</style>