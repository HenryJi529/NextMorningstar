<script lang="ts" setup>
import type {R} from "@/types/common";
import axios from '@/axios/index';
import router from "@/router";
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";

const props = defineProps<{
    app: string,
    resource: string,
    api: string,
    data: any, // eslint-disable-line @typescript-eslint/no-explicit-any
    resolver?: Function // eslint-disable-line @typescript-eslint/no-unsafe-function-type
}>();

const routerName = `${props.app}-manage-${props.resource}-list`;

const route = useRoute();
const itemNotExist = ref<boolean>(false);
const id = route.params.id as string;
const endpoint = props.api.replace('{id}', id);


const updateItem = async () => {
    const response: R<object> = (await axios.patch(
            endpoint,
            props.data
        )
    ).data;
    if (response.code <= 0) {
        alert(response.msg);
        return;
    }
    await router.push({name: routerName});
}

const deleteItem = async () => {
    const response: R<object> = (await axios.delete(
            endpoint
        )
    ).data;
    if (response.code <= 0) {
        alert(response.msg);
        return;
    }
    await router.push({name: routerName});
}

const cancelEdit = () => {
    router.push({name: routerName});
}

onMounted(async () => {
    const response: R<Record<string, any>> = (await axios.get(endpoint)).data; // eslint-disable-line @typescript-eslint/no-explicit-any
    if (response.code <= 0) {
        itemNotExist.value = true;
    } else {
        if (props.resolver) {
            Object.assign(props.data, props.resolver(response.data));
        } else {
            Object.keys(props.data).forEach(key => {
                if (key in response.data) {
                    props.data[key] = response.data[key];
                }
            });
        }
    }
})

</script>

<template>
    <div>
        <div v-if="itemNotExist">
            {{ props.resource }} 不存在
        </div>
        <div v-else class="lg:flex pt-6">
            <div
                class="px-4 py-8 lg:w-2/3 bg-white dark:bg-gray-800 dark:border-gray-400 rounded-2xl border-2 flex flex-col justify-start">
                <slot name="fields"></slot>
            </div>
            <div class="py-4 lg:px-4 lg:w-1/3 flex flex-col space-y-2 lg:space-y-4">
                <slot name="actions">
                    <div class="btn btn-success" @click="updateItem">更新</div>
                    <div class="btn btn-warning" @click="deleteItem">删除</div>
                    <div class="btn btn-neutral" @click="cancelEdit">取消</div>
                </slot>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>