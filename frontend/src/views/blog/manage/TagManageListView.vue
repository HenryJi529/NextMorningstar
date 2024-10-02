<script setup lang="ts">

import type {R} from "@/types/common";
import type {TagDetail} from "@/types/blog";
import axios from "axios";
import {API_BLOG_TAG_ALL} from "@/constants/ApiConstant";
import {onMounted, ref} from "vue";
import router from "@/router";

let originTagDetail: TagDetail[];
const tagDetails = ref<TagDetail[]>([]);
const sortOrders = ref<string[]>([]);

const updateTagDetail = () => {
    tagDetails.value = originTagDetail;

    for(let i=0; i<sortOrders.value.length; i++){
        const order = sortOrders.value[i];
        const [field, direction] = order.split('_');
        if(field != 'count' && field != 'id'){
            return;
        }
        tagDetails.value = tagDetails.value.sort((a, b) => {
            if (direction === 'asc') {
                return a[field] - b[field];
            } else {
                return b[field] - a[field];
            }
        });
    }
}

const setDefaultOrder = (field: string) => {
    if(sortOrders.value.includes(`${field}_asc`) || sortOrders.value.includes(`${field}_desc`)) {
        return;
    }
    sortOrders.value.push(`${field}_asc`);
    updateTagDetail();
}

const switchOrder = (field: string) => {
    if(sortOrders.value.includes(`${field}_asc`)) {
        sortOrders.value = sortOrders.value.filter(order => order!=`${field}_asc`);
        sortOrders.value.push(`${field}_desc`);
    }else if(sortOrders.value.includes(`${field}_desc`)) {
        sortOrders.value = sortOrders.value.filter(order => order!=`${field}_desc`);
        sortOrders.value.push(`${field}_asc`);
    }
    updateTagDetail();
}

const clearOrder = (field: string) => {
    sortOrders.value = sortOrders.value.filter(sort => !sort.startsWith(field));
    updateTagDetail();
}

const includeOrder = (field: string) => {
    return sortOrders.value.includes(`${field}_asc`) || sortOrders.value.includes(`${field}_desc`);
}

onMounted(async () => {
    const response: R<TagDetail[]> = (await axios.get(API_BLOG_TAG_ALL)).data;
    originTagDetail = response.data;
    sortOrders.value.push("id_asc");
    updateTagDetail();
})
</script>

<template>
    <table class="w-full">
        <thead>
        <tr class="flex">
            <th class="w-32 relative">
                <span @click="setDefaultOrder('id')">ID</span>
                <div class="absolute right-5 top-2" @click="switchOrder('id')">
                    <font-awesome-icon :icon="['fas', 'arrow-down-1-9']" v-if="sortOrders.includes('id_asc')"/>
                    <font-awesome-icon :icon="['fas', 'arrow-down-9-1']" v-if="sortOrders.includes('id_desc')"/>
                </div>
                <div class="absolute right-2 top-2" v-if="includeOrder('id')"
                     @click="clearOrder('id')">
                    <font-awesome-icon :icon="['fas', 'xmark']" />
                </div>
            </th>
            <th class="flex-1">名称</th>
            <th class="w-32 relative">
                <span @click="setDefaultOrder('count')">文章数</span>
                <div class="absolute right-5 top-2" @click="switchOrder('count')">
                    <font-awesome-icon :icon="['fas', 'arrow-down-1-9']" v-if="sortOrders.includes('count_asc')"/>
                    <font-awesome-icon :icon="['fas', 'arrow-down-9-1']" v-if="sortOrders.includes('count_desc')"/>
                </div>
                <div class="absolute right-2 top-2" v-if="includeOrder('count')"
                     @click="clearOrder('count')">
                    <font-awesome-icon :icon="['fas', 'xmark']" />
                </div>
            </th>
        </tr>
        </thead>
        <tbody>
            <template v-for="tagDetail in tagDetails">
                <tr class="flex">
                    <td class="w-32 text-center cursor-pointer text-blue-500 dark:text-blue-400" @click="router.push({name: 'blog-manage-tag-edit', params: {id: tagDetail.id }})">{{ tagDetail.id }}</td>
                    <td class="flex-1 text-center">{{ tagDetail.name }}</td>
                    <td class="w-32 text-center">{{ tagDetail.count }}</td>
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
        @apply px-4 py-2 border-solid border-[1px] border-collapse border-color;
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