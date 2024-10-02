<script setup lang="ts">
import type {R} from "@/types/common";
import type {ListResourceConfig} from "@/types/blog";
import axios from "axios";
import {
    API_PROXY_TOKEN_ALL,
    API_PROXY_TOKEN_ONE
} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";
import type {Token} from "@/types/proxy";
import {TOKEN} from "@/constants/LocalStorageConstant";


let config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: "id",
            sort: true,
            width: "flex-1 lg:flex-none lg:w-32",
            center: true,
        },
        {
            name: "name",
            sort: false,
            width: "flex-1 lg:flex-none lg:w-48",
            center: true,
        },
        {
            name: "value",
            sort: false,
            width: "hidden lg:block flex-1",
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

let originItems: Token[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<Token[]> = (await axios.get(API_PROXY_TOKEN_ALL,
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
            app="proxy" resource="token"
            :delete-api="API_PROXY_TOKEN_ONE"
            :origin-items="originItems"
            :config="config"/>
    </div>
</template>

<style scoped lang="scss">

</style>