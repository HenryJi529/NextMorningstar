<script lang="ts" setup>
import type {R} from "@/types/common";
import axios from '@/axios/index';
import router from "@/router";

const props = defineProps<{
    app: string,
    resource: string,
    api: string,
    data: any // eslint-disable-line @typescript-eslint/no-explicit-any
}>();

const routerName = `${props.app}-manage-${props.resource}-list`;

const addItem = async () => {
    const response: R<object> = (await axios.post(
            props.api,
            props.data
        )
    ).data;
    if (response.code <= 0) {
        alert(response.msg);
        return;
    }
    await router.push({name: routerName});
}

const cancelAdd = () => {
    router.push({name: routerName});
}

</script>

<template>
    <div class="lg:flex pt-6">
        <div
            class="px-4 py-8 lg:w-2/3 bg-white dark:bg-gray-800 dark:border-gray-400 rounded-2xl border-2 flex flex-col justify-start">
            <slot name="fields"></slot>
        </div>
        <div class="py-4 lg:px-4 lg:w-1/3 flex flex-col space-y-2 lg:space-y-4">
            <slot name="actions">
                <div class="btn btn-success" @click="addItem">添加</div>
                <div class="btn btn-neutral" @click="cancelAdd">取消</div>
            </slot>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>