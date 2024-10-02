<script lang="ts" setup>

import type {ListResourceConfig, R} from "@/types/common";
import {onMounted, ref, watch} from "vue";
import axios from '@/axios/index';
import router from "@/router";

const props = defineProps<{
    app: string,
    resource: string,
    deleteApi?: string,
    originItems: any[], // eslint-disable-line @typescript-eslint/no-explicit-any
    config: ListResourceConfig
}>();

const sortedItems = ref<any[]>([]); // eslint-disable-line @typescript-eslint/no-explicit-any
const sortOrders = ref<string[]>([]);
const checkAll = ref(false);
const checkedIds = ref<number[]>([]);

const toggleAllItemCheckbox = () => {
    if (checkAll.value) {
        checkAll.value = false;
        checkedIds.value = [];
    } else {
        checkAll.value = true;
    }
}

const toggleItemCheckbox = (id: number) => {
    if (checkedIds.value.includes(id)) {
        checkedIds.value.splice(checkedIds.value.indexOf(id), 1);
    } else {
        checkedIds.value.push(id);
    }
}

const updateSortedItems = () => {
    sortedItems.value = props.originItems;

    for (let i = 0; i < sortOrders.value.length; i++) {
        const order = sortOrders.value[i];
        const [field, direction] = order.split('_');
        sortedItems.value = sortedItems.value.sort((a, b) => {
            if ((typeof a) === "number") {
                if (direction === 'asc') {
                    return a[field] - b[field];
                } else {
                    return b[field] - a[field];
                }
            } else {
                if (direction === 'asc') {
                    if (a[field] < b[field]) {
                        return -1;
                    } else if (a[field] > b[field]) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    if (a[field] > b[field]) {
                        return -1;
                    } else if (a[field] < b[field]) {
                        return 1;
                    } else {
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
    }, {
        deep: true
    }
)

const setDefaultOrder = (field: string) => {
    if (sortOrders.value.includes(`${field}_asc`) || sortOrders.value.includes(`${field}_desc`)) {
        return;
    }
    sortOrders.value.push(`${field}_asc`);
}

const switchOrder = (field: string) => {
    if (sortOrders.value.includes(`${field}_asc`)) {
        sortOrders.value.splice(sortOrders.value.indexOf(`${field}_asc`), 1);
        sortOrders.value.push(`${field}_desc`);
    } else if (sortOrders.value.includes(`${field}_desc`)) {
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
    if (props.deleteApi == undefined) {
        return;
    }
    const api = props.deleteApi as string;
    const selectIds = checkAll.value ? props.originItems.map(item => item.id) : checkedIds.value;
    const deletePromises = selectIds.map(id => {
        return axios.delete(
            api.replace('{id}', id.toString())
        );
    });
    Promise.all(deletePromises).then(results => {
        for (let result of results) {
            const response: R<object> = result.data;
            if (response.code <= 0) {
                alert(`删除${props.resource}出错`);
            }
        }
        if (deletePromises.length > 0) {
            location.reload();
        }
    });
}

onMounted(() => {
    if (props.config.defaultSortOrders) {
        sortOrders.value = props.config.defaultSortOrders;
    } else {
        sortOrders.value.push("id_asc");
    }
    updateSortedItems();
})

</script>

<template>
    <div class="flex justify-between items-center">
        <div class="text-xl pl-4">
            <span class="font-bold">{{ sortedItems.length }}</span> 个
            <span class="font-bold">{{ resource.charAt(0).toUpperCase() + resource.slice(1) }}</span>
        </div>
        <div class="flex justify-end items-center space-x-4 p-4">
            <div :id="`${app}-${resource}-add-button`" class="btn rounded-xl btn-primary"
                 @click="router.push({name: `${app}-manage-${resource}-add`})">
                <font-awesome-icon :icon="['fas', 'circle-plus']"/>
                新增
            </div>
            <div :class="{'btn-disabled': !deleteApi}" class="btn rounded-xl btn-secondary" @click="deleteSelected">
                <font-awesome-icon :icon="['fas', 'circle-minus']"/>
                删除所选
            </div>
        </div>
    </div>
    <div class="overflow-scroll">
        <table class="w-full border-8 border-color rounded-xl border-solid border-separate border-spacing-0">
            <thead>
            <tr class="flex">
                <th data-col="0">
                    <input :checked="checkAll" type="checkbox" @click="toggleAllItemCheckbox"/>
                </th>
                <template v-for="(itemConfig, ind) in config.itemConfigs">
                    <th :class="[{'text-center': itemConfig.center, 'text-left': !itemConfig.center}, 'flex', 'items-center', 'justify-center', itemConfig.class]"
                        :data-col="ind+1"
                        class="relative">
                        <span v-if="itemConfig.sort" :class="{ 'cursor-pointer': !includeOrder(itemConfig.name)}"
                              @click="setDefaultOrder(itemConfig.name)">{{ itemConfig.name }}</span>
                        <span v-else>{{ itemConfig.name }}</span>

                        <div v-if="itemConfig.sort">
                            <div class="absolute right-3 top-2" @click="switchOrder(itemConfig.name)">
                                <font-awesome-icon v-if="sortOrders.includes(`${itemConfig.name}_asc`)"
                                                   :icon="['fas', 'arrow-down-1-9']"
                                                   class="cursor-pointer"/>
                                <font-awesome-icon v-if="sortOrders.includes(`${itemConfig.name}_desc`)"
                                                   :icon="['fas', 'arrow-down-9-1']"
                                                   class="cursor-pointer"/>
                            </div>
                            <div v-if="includeOrder(itemConfig.name)" class="absolute right-0 top-2 cursor-pointer"
                                 @click="clearOrder(itemConfig.name)">
                                <font-awesome-icon :icon="['fas', 'xmark']"/>
                            </div>
                        </div>
                    </th>
                </template>
            </tr>
            </thead>
            <tbody>
            <template v-for="item in sortedItems">
                <tr class="flex">
                    <td class="flex items-center" data-col="0">
                        <input :checked="checkAll || checkedIds.includes(item.id)" type="checkbox"
                               @click="toggleItemCheckbox(item.id)"/>
                    </td>
                    <template v-for="(itemConfig, ind) in config.itemConfigs">
                        <td :class="[itemConfig.class, 'flex', 'items-center', 'flex-nowrap']" :data-col="ind+1">
                            <div
                                :class="{'text-center': itemConfig.center, 'cursor-pointer text-blue-500 dark:text-blue-400': itemConfig.name === config.identityField}"
                                class="w-full overflow-scroll"
                                @click="itemConfig.name === config.identityField && router.push({name: `${app}-manage-${resource}-edit`, params: {id: item.id }})">
                                {{ item[itemConfig.name] }}
                            </div>
                        </td>
                    </template>
                </tr>
            </template>
            </tbody>
        </table>
    </div>
</template>

<style lang="scss" scoped>
.border-color {
    @apply border-gray-300 dark:border-gray-600;
}

table {
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