<script lang="ts" setup>
import type {ListResourceConfig, R} from "@/types/common";
import axios from '@/axios/index';
import {API_PROXY_NODE_ALL, API_PROXY_NODE_ONE} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";
import type {NodeDetail} from "@/types/proxy";


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
            class: "flex-1",
            center: true,
        },
        {
            name: "protocol",
            sort: false,
            class: "hidden md:flex w-32",
            center: true,
        },
        {
            name: "subId",
            sort: true,
            class: "w-24",
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

let originItems: NodeDetail[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<NodeDetail[]> = (await axios.get(API_PROXY_NODE_ALL)).data;
    originItems = response.data;
    loaded.value = true;
})
</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            :config="config" :delete-api="API_PROXY_NODE_ONE"
            :origin-items="originItems"
            app="proxy"
            resource="node"/>
    </div>
</template>

<style lang="scss" scoped>

</style>