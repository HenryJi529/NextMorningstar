<script setup lang="ts">
import type {R} from "@/types/common";
import type {ListResourceConfig} from "@/types/blog";
import axios from "axios";
import {
    API_PROXY_SUB_ALL,
    API_PROXY_SUB_ONE
} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";
import type {Sub} from "@/types/proxy";
import {TOKEN} from "@/constants/LocalStorageConstant";


let config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: "id",
            sort: true,
            width: "w-24",
            center: true,
        },
        {
            name: "name",
            sort: false,
            width: "flex-1 2xl:flex-none 2xl:w-80",
            center: true,
        },
        {
            name: "link",
            sort: false,
            width: "hidden flex-1 2xl:block",
            center: true,
        },
        {
            name: "updateTime",
            sort: true,
            width: "w-48",
            center: true,
        }
    ]
}

let originItems: Sub[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<Sub[]> = (await axios.get(API_PROXY_SUB_ALL,
        {
            headers: {
                Authorization: localStorage.getItem(TOKEN),
            }
        }
    )).data;
    originItems = response.data;
    loaded.value = true;
})
</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            app="proxy" resource="sub"
            :delete-api="API_PROXY_SUB_ONE"
            :origin-items="originItems"
            :config="config"/>
    </div>
</template>

<style scoped lang="scss">

</style>