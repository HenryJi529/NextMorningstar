<script lang="ts" setup>
import type {ListResourceConfig, R} from "@/types/common";
import axios from '@/axios/index';
import {API_PROXY_TOKEN_ALL, API_PROXY_TOKEN_ONE} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import CommonManageListTable from "@/components/common/CommonManageListTable.vue";
import type {Token} from "@/types/proxy";


let config: ListResourceConfig = {
    identityField: 'id',
    itemConfigs: [
        {
            name: "id",
            sort: true,
            class: "flex-1 lg:flex-none lg:w-32",
            center: true,
        },
        {
            name: "name",
            sort: false,
            class: "flex-1 lg:flex-none lg:w-48",
            center: true,
        },
        {
            name: "value",
            sort: false,
            class: "hidden lg:flex flex-1",
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

let originItems: Token[];
const loaded = ref(false);

onMounted(async () => {
    const response: R<Token[]> = (await axios.get(API_PROXY_TOKEN_ALL)).data;
    originItems = response.data;
    loaded.value = true;
})
</script>

<template>
    <div v-if="loaded">
        <common-manage-list-table
            :config="config" :delete-api="API_PROXY_TOKEN_ONE"
            :origin-items="originItems"
            app="proxy"
            resource="token"/>
    </div>
</template>

<style lang="scss" scoped>

</style>