<script setup lang="ts">
import {ref} from "vue";
import type {CreateTokenRequestVo} from "@/types/proxy";
import {API_PROXY_TOKEN_ALL} from "@/constants/ApiConstant";
import CommonManageAddForm from "@/components/common/CommonManageAddForm.vue";

const data = ref<CreateTokenRequestVo>({
    name: "",
    value: "",
})

</script>

<template>
    <common-manage-add-form app="proxy" resource="node" :api="API_PROXY_TOKEN_ALL" :data="data">
        <template #fields>
            <div class="flex flex-col space-y-4">
                <div class="flex space-x-16 items-center">
                    <span class="font-bold text-center w-32">Name</span>
                    <input v-model="data.name" type="text"
                           class="admin-manage-input"/>
                </div>
                <div class="flex space-x-16 items-center">
                    <span class="font-bold text-center w-32">Value</span>
                    <input v-model="data.value" type="text"
                           class="admin-manage-input"/>
                </div>
            </div>
        </template>
    </common-manage-add-form>
</template>

<style scoped lang="scss">

</style>