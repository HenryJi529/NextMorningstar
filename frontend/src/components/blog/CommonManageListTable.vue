<script setup lang="ts">

import type {ListResourceConfig} from "@/types/blog";
import {onMounted, ref, watch} from "vue";
import axios from "axios";
import {TOKEN} from "@/constants/LocalStorageConstant";
import type {R} from "@/types/common";
import router from "@/router";

const props = defineProps<{
    name: string,
    slug: string,
    api: string,
    originItems: any[],
    config : ListResourceConfig
}>();

const sortedItems = ref<any[]>([]);
const sortOrders = ref<string[]>([]);
const checkAll = ref(false);
const checkedIds = ref<number[]>([]);

const toggleAllItemCheckbox = () => {
    if(checkAll.value) {
        checkAll.value = false;
        checkedIds.value = [];
    }else{
        checkAll.value = true;
    }
}

const toggleItemCheckbox = (id: number) => {
    if(checkedIds.value.includes(id)) {
        checkedIds.value.splice(checkedIds.value.indexOf(id), 1);
    }else{
        checkedIds.value.push(id);
    }
}

const updateSortedItems = () => {
    sortedItems.value = props.originItems;

    for(let i=0; i<sortOrders.value.length; i++){
        const order = sortOrders.value[i];
        const [field, direction] = order.split('_');
        sortedItems.value = sortedItems.value.sort((a, b) => {
            if((typeof a)==="number"){
                if (direction === 'asc') {
                    return a[field] - b[field];
                } else {
                    return b[field] - a[field];
                }
            }else{
                if(direction === 'asc') {
                    if (a[field] < b[field]) {
                        return -1;
                    }else if(a[field] > b[field]) {
                        return 1;
                    }else {
                        return 0;
                    }
                }else {
                    if (a[field] > b[field]) {
                        return -1;
                    }else if(a[field] < b[field]) {
                        return 1;
                    }else {
                        return 0;
                    }
                }
            }
        });
    }
}

watch(
    sortOrders,
    () => {
        updateSortedItems();
    },{
        deep: true // 启用深度监听
    }
)

const setDefaultOrder = (field: string) => {
    if(sortOrders.value.includes(`${field}_asc`) || sortOrders.value.includes(`${field}_desc`)) {
        return;
    }
    sortOrders.value.push(`${field}_asc`);
}

const switchOrder = (field: string) => {
    if(sortOrders.value.includes(`${field}_asc`)) {
        sortOrders.value.splice(sortOrders.value.indexOf(`${field}_asc`), 1);
        sortOrders.value.push(`${field}_desc`);
    }else if(sortOrders.value.includes(`${field}_desc`)) {
        sortOrders.value.splice(sortOrders.value.indexOf(`${field}_desc`), 1);
        sortOrders.value.push(`${field}_asc`);
    }
}

const clearOrder = (field: string) => {
    sortOrders.value = sortOrders.value.filter(sort => !sort.startsWith(field));
}

const includeOrder = (field: string) => {
    return sortOrders.value.includes(`${field}_asc`) || sortOrders.value.includes(`${field}_desc`);
}

const deleteSelected = () => {
    const selectIds = checkAll.value ? props.originItems.map(item => item.id): checkedIds.value;
    const deletePromises = selectIds.map(id => {
        return axios.delete(
            props.api.replace('{id}', id.toString()),
            {
                headers: {
                    Authorization: localStorage.getItem(TOKEN)
                }
            }
        );
    });
    Promise.all(deletePromises).then(results => {
        for(let result of results) {
            const response: R<object> = result.data;
            if(response.code != 1){
                alert(`删除${props.name}出错`);
            }
        }
        location.reload();
    });
}

onMounted(()=>{
    if(props.config.defaultSortOrders){
        sortOrders.value = props.config.defaultSortOrders;
    }else{
        sortOrders.value.push("id_asc");
    }
    updateSortedItems();
})

</script>

<template>
    <div class="flex justify-between items-center">
        <div class="text-xl pl-4">
            <span class="font-bold">{{sortedItems.length}}</span> 个 {{name}}
        </div>
        <div class="flex justify-end items-center space-x-4 p-4">
            <div class="btn rounded-xl btn-primary" @click="router.push({name: `blog-manage-${slug}-add`})">
                <font-awesome-icon :icon="['fas', 'circle-plus']" />新增
            </div>
            <div class="btn rounded-xl btn-secondary" @click="deleteSelected">
                <font-awesome-icon :icon="['fas', 'circle-minus']" />删除所选
            </div>
        </div>
    </div>
    <table class="w-full">
        <thead>
        <tr class="flex">
            <th>
                <input type="checkbox" :checked="checkAll" @click="toggleAllItemCheckbox"/>
            </th>
            <template v-for="itemConfig in config.itemConfigs">
                <th class="relative" :class="[{'text-center': itemConfig.center, 'text-left': !itemConfig.center}, itemConfig.width]">
                    <span v-if="itemConfig.sort" @click="setDefaultOrder(itemConfig.name)" :class="{ 'cursor-pointer': !includeOrder(itemConfig.name)}">{{itemConfig.name}}</span>
                    <span v-else>{{itemConfig.name}}</span>

                    <div v-if="itemConfig.sort">
                        <div class="absolute right-3 top-2" @click="switchOrder(itemConfig.name)">
                            <font-awesome-icon :icon="['fas', 'arrow-down-1-9']" v-if="sortOrders.includes(`${itemConfig.name}_asc`)" class="cursor-pointer"/>
                            <font-awesome-icon :icon="['fas', 'arrow-down-9-1']" v-if="sortOrders.includes(`${itemConfig.name}_desc`)" class="cursor-pointer"/>
                        </div>
                        <div class="absolute right-0 top-2 cursor-pointer" v-if="includeOrder(itemConfig.name)"
                             @click="clearOrder(itemConfig.name)">
                            <font-awesome-icon :icon="['fas', 'xmark']" />
                        </div>
                    </div>
                </th>
            </template>
        </tr>
        </thead>
        <tbody>
        <template v-for="item in sortedItems">
            <tr class="flex">
                <td class="flex items-center">
                    <input type="checkbox" :checked="checkAll || checkedIds.includes(item.id)" @click="toggleItemCheckbox(item.id)" />
                </td>
                <template v-for="itemConfig in config.itemConfigs">
                    <td :class="[itemConfig.width, 'flex', 'items-center', {'justify-center': itemConfig.center}]">
                        <div v-if="itemConfig.name === config.identityField" class="cursor-pointer text-blue-500 dark:text-blue-400"
                             @click="router.push({name: `blog-manage-${slug}-edit`, params: {id: item.id }})">
                            {{item[itemConfig.name]}}
                        </div>
                        <div v-else>
                            {{item[itemConfig.name]}}
                        </div>
                    </td>
                </template>
            </tr>
        </template>
        </tbody>
    </table>
</template>

<style scoped lang="scss">
.border-color {
    @apply border-gray-300 dark:border-gray-600;
}

table {
    @apply border-2 border-color;
    th, td {
        @apply px-2 lg:px-4 py-2 border-solid border-[1px] border-collapse border-color;
    }
    tbody {
        tr:nth-child(odd) {
            @apply bg-gray-200 dark:bg-slate-700;
        }
        tr:hover {
            @apply bg-zinc-300 dark:bg-gray-800;
        }
    }
}
</style>