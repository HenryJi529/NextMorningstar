<script lang="ts" setup>
import type {ListResourceConfig, R} from "@/types/common";
import axios from '@/axios/index';
import {API_PROXY_SUB_ALL, API_PROXY_SUB_ONE} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";
import type {Sub} from "@/types/proxy";


let config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: "id",
            sort: true,
            class: "w-24",
            center: true,
        },
        {
            name: "name",
            sort: false,
            class: "flex-1 2xl:flex-none 2xl:w-80",
            center: true,
        },
        {
            name: "link",
            sort: false,
            class: "hidden flex-1 2xl:flex",
            center: true,
        },
        {
            name: "updateTime",
            sort: true,
            class: "w-52",
            center: true,
        }
    ]
}

let originItems: Sub[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<Sub[]> = (await axios.get(API_PROXY_SUB_ALL
    )).data;
    originItems = response.data;
    loaded.value = true;
})
</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            :config="config" :delete-api="API_PROXY_SUB_ONE"
            :origin-items="originItems"
            app="proxy"
            resource="sub"/>
    </div>
</template>

<style lang="scss" scoped>

</style>