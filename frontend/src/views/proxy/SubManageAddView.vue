<script lang="ts" setup>
import {ref} from "vue";
import type {CreateSubRequestVo} from "@/types/proxy";
import {API_PROXY_SUB_ALL} from "@/constants/ApiConstant";
import CommonManageAddForm from "@/components/common/CommonManageAddForm.vue";

const data = ref<CreateSubRequestVo>({
    name: "",
    link: "",
})

</script>

<template>
    <common-manage-add-form :api="API_PROXY_SUB_ALL" :data="data" app="proxy" resource="sub">
        <template #fields>
            <div class="flex flex-col space-y-4">
                <div class="flex space-x-16 items-center">
                    <span class="font-bold text-center w-32">Name</span>
                    <input v-model="data.name" class="admin-manage-input"
                           type="text"/>
                </div>
                <div class="flex space-x-16 items-center">
                    <span class="font-bold text-center w-32">Link</span>
                    <input v-model="data.link" class="admin-manage-input"
                           type="text"/>
                </div>
            </div>
        </template>
    </common-manage-add-form>
</template>

<style lang="scss" scoped>

</style>