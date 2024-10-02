<script setup lang="ts">
import {ref} from "vue";
import type {CreateNodeRequestVo} from "@/types/proxy";
import {API_PROXY_NODE_ALL} from "@/constants/ApiConstant";
import CommonManageAddForm from "@/components/common/CommonManageAddForm.vue";

const data = ref<CreateNodeRequestVo>({
    link: ""
})

</script>

<template>
    <common-manage-add-form app="proxy" resource="node" :api="API_PROXY_NODE_ALL" :data="data">
        <template #fields>
            <div class="flex space-x-16 items-center">
                <span class="font-bold text-center w-32">Link</span>
                <input v-model="data.link" type="text"
                       class="admin-manage-input"/>
            </div>
        </template>
    </common-manage-add-form>
</template>

<style scoped lang="scss">

</style>