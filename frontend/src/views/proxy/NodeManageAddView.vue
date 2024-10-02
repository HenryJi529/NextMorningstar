<script lang="ts" setup>
import {ref} from "vue";
import type {CreateNodeRequestVo} from "@/types/proxy";
import {API_PROXY_NODE_ALL} from "@/constants/ApiConstant";
import CommonManageAddForm from "@/components/common/CommonManageAddForm.vue";

const data = ref<CreateNodeRequestVo>({
    link: ""
})

</script>

<template>
    <common-manage-add-form :api="API_PROXY_NODE_ALL" :data="data" app="proxy" resource="node">
        <template #fields>
            <div class="flex space-x-16 items-center">
                <span class="font-bold text-center w-32">Link</span>
                <input v-model="data.link" class="admin-manage-input"
                       type="text"/>
            </div>
        </template>
    </common-manage-add-form>
</template>

<style lang="scss" scoped>

</style>