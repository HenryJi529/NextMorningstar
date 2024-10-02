<script setup lang="ts">
import type {R} from "@/types/common";
import type {ListResourceConfig} from "@/types/blog";
import axios from "axios";
import {
    API_PROXY_NODE_ALL,
    API_PROXY_NODE_ONE
} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";
import type {NodeDetail} from "@/types/proxy";
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
            width: "flex-1",
            center: true,
        },
        {
            name: "protocol",
            sort: false,
            width: "hidden md:inline-flex w-32",
            center: true,
        },
        {
            name: "subId",
            sort: true,
            width: "w-24",
            center: true,
        },
        {
            name: "updateTime",
            sort: true,
            width: "w-52",
            center: true,
        }
    ]
}

let originItems: NodeDetail[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<NodeDetail[]> = (await axios.get(API_PROXY_NODE_ALL,
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
            app="proxy" resource="node"
            :delete-api="API_PROXY_NODE_ONE"
            :origin-items="originItems"
            :config="config"/>
    </div>
</template>

<style scoped lang="scss">

</style>