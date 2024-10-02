<script lang="ts" setup>
import type {ListResourceConfig, R} from "@/types/common";
import type {CategoryDetail} from "@/types/blog";
import axios from '@/axios/index';
import {API_BLOG_CATEGORY_ALL, API_BLOG_CATEGORY_ONE} from "@/constants/ApiConstant";
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

let originItems: CategoryDetail[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<CategoryDetail[]> = (await axios.get(API_BLOG_CATEGORY_ALL)).data;
    originItems = response.data;
    loaded.value = true;
})
</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            :config="config" :delete-api="API_BLOG_CATEGORY_ONE"
            :origin-items="originItems"
            app="blog"
            resource="category"/>
    </div>
</template>

<style lang="scss" scoped>

</style>