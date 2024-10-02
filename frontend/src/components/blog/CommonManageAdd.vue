<script setup lang="ts">
import type {R} from "@/types/common";
import axios from "axios";
import {TOKEN} from "@/constants/LocalStorageConstant";
import router from "@/router";

const props = defineProps<{
    slug: string,
    api: string,
    data: any
}>();

const addItem = async () => {
    const response: R<number> = (await axios.post(
            props.api,
            props.data,
            {
                headers: {
                    Authorization: localStorage.getItem(TOKEN)
                }
            }
        )
    ).data;
    if(response.code != 1){
        alert(response.msg);
        return;
    }
    await router.push({name: `blog-manage-${props.slug}-list`});
}

const cancelAdd = () => {
    router.push({name: `blog-manage-${props.slug}-list`});
}

</script>

<template>
    <div class="lg:flex pt-6">
        <div class="px-4 py-8 lg:w-2/3 bg-white dark:bg-gray-800 dark:border-gray-400 rounded-2xl border-2 flex flex-col justify-start">
            <slot name="fields"></slot>
        </div>
        <div class="p-4 lg:w-1/3 flex flex-col space-y-2 lg:space-y-4">
            <slot name="actions">
                <div class="btn btn-success" @click="addItem">添加</div>
                <div class="btn btn-neutral" @click="cancelAdd">取消</div>
            </slot>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>