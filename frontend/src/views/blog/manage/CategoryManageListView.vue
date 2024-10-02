<script setup lang="ts">
import type {R} from "@/types/common";
import type {CategoryDetail, ListResourceConfig} from "@/types/blog";
import axios from "axios";
import {API_BLOG_CATEGORY_ALL, API_BLOG_CATEGORY_ONE} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";


let config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: "id",
            sort: true,
            width: "w-32",
            center: true,
        },
        {
            name: "name",
            sort: false,
            width: "flex-1",
            center: true,
        },
        {
            name: "count",
            sort: true,
            width: "w-32",
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
            app="blog" resource="category"
            :delete-api="API_BLOG_CATEGORY_ONE"
            :origin-items="originItems"
            :config="config"/>
    </div>
</template>

<style scoped lang="scss">

</style>