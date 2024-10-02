<script lang="ts" setup>
import {useRoute} from "vue-router";
import {ref} from "vue";
import {API_PROXY_TOKEN_ONE} from "@/constants/ApiConstant";
import CommonManageEditForm from "@/components/common/CommonManageEditForm.vue";
import type {UpdateTokenRequestVo} from "@/types/proxy";

const route = useRoute();

const data = ref<UpdateTokenRequestVo>({
    id: Number(route.params.id),
    name: "",
    value: ""
});

</script>

<template>
    <common-manage-edit-form :api="API_PROXY_TOKEN_ONE" :data="data" app="proxy" resource="token">
        <template #fields>
            <div class="flex flex-col space-y-4">
                <div class="flex space-x-16 items-center">
                    <span class="font-bold text-center w-32">Name</span>
                    <input v-model="data.name" class="admin-manage-input"
                           type="text"/>
                </div>
                <div class="flex space-x-16 items-center">
                    <span class="font-bold text-center w-32">Value</span>
                    <input v-model="data.value" class="admin-manage-input"
                           type="text"/>
                </div>
            </div>
        </template>
    </common-manage-edit-form>
</template>

<style lang="scss" scoped>

</style>