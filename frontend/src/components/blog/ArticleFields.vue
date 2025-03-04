<script setup lang="ts">

import {onMounted, ref} from "vue";
import type {R} from "@/types/common";
import type {CategoryDetail, TagDetail} from "@/types/blog";
import axios from "axios";
import {API_BLOG_CATEGORY_ALL, API_BLOG_TAG_ALL} from "@/constants/ApiConstant";
import router from "@/router";

interface Data {
    title: string;
    content: string;
    categoryId: number;
    tagIds: number[];
}

defineProps<{
    data: Data;
}>()

const categoryDetails = ref<CategoryDetail[]>();
const tagDetails = ref<TagDetail[]>();
const tagPrefix = ref("");
const isTagDropdownOpen = ref(false);

const addNewCategory = ()=> {
    const { path } = router.resolve({name: 'blog-manage-category-add'});
    window.open(path, '_blank');
}

const addNewTag = () => {
    const { path } = router.resolve({name: 'blog-manage-tag-add'});
    window.open(path, '_blank');
}

const openTagDropdown = () => {
    isTagDropdownOpen.value = true;
}

const closeTagDropdown = () => {
    setTimeout(()=>{
        isTagDropdownOpen.value = false;
    }, 5000);
}

const updateOptions = async () => {
    const response1: R<CategoryDetail[]> = (await axios.get(API_BLOG_CATEGORY_ALL)).data;
    categoryDetails.value = response1.data;
    const response2: R<TagDetail[]> = (await axios.get(API_BLOG_TAG_ALL)).data;
    tagDetails.value = response2.data;
}

onMounted(async () => {
    await updateOptions();
    setInterval(()=> {
        updateOptions();
    }, 3000)
})


</script>

<template>
    <div class="flex flex-col space-y-4">
        <div class="flex space-x-16 items-center">
            <label for="title" class="font-bold w-32">Title</label>
            <input id="title" v-model="data.title" type="text"
                   class="admin-manage-input"/>
        </div>
        <div class="flex space-x-16 items-start">
            <label for="content" class="font-bold w-32 pt-4">Content</label>
            <textarea id="content" v-model="data.content" rows="5" class="admin-manage-input"></textarea>
        </div>
        <div class="flex space-x-16 items-center">
            <label for="category" class="font-bold w-32">Category</label>
            <div class="flex-1 flex items-center">
                <select id="category" v-model="data.categoryId" class="admin-manage-input">
                    <option value="0">请选择</option>
                    <template v-for="categoryDetail in categoryDetails">
                        <option :value="categoryDetail.id">{{categoryDetail.name}}</option>
                    </template>
                </select>
                <div class="w-12 flex justify-center items-center text-xl text-green-400 hover:text-pink-500 cursor-pointer"
                     @click="addNewCategory">
                    <font-awesome-icon :icon="['fas', 'plus']" />
                </div>
            </div>
        </div>
        <div class="flex space-x-16 items-center">
            <label for="title" class="font-bold w-32">Tags</label>
            <div class="flex-1" v-if="tagDetails">
                <div class="w-full flex items-center">
                    <div class="admin-manage-input flex items-center flex-wrap justify-start">
                        <template v-for="tagId in data.tagIds">
                            <div class="border-[1px] py-1 px-2 rounded-lg flex items-center justify-around space-x-2 border-gray-400 dark:border-gray-700 mx-1 my-[1px]">
                                <font-awesome-icon :icon="['fas', 'xmark']"
                                                   class="text-blue-400 hover:text-pink-500 cursor-pointer"
                                                   @click="data.tagIds.splice(data.tagIds.indexOf(tagId), 1)" />
                                <span>{{ tagDetails.filter(tagDetail => tagDetail.id == tagId)[0].name }}</span>
                            </div>
                        </template>
                        <input type="text" v-model="tagPrefix" placeholder="请选择..." class="bg-transparent px-2 py-1 rounded-lg w-20 ml-1" @focus="openTagDropdown" @blur="closeTagDropdown" />
                    </div>
                    <div class="w-12 flex justify-center items-center text-xl text-green-400 hover:text-pink-500 cursor-pointer"
                         @click="addNewTag">
                        <font-awesome-icon :icon="['fas', 'plus']" />
                    </div>
                </div>
                <div v-if="isTagDropdownOpen" class="flex items-center flex-wrap pl-2 pr-8 pt-1">
                    <template v-for="tagDetail in tagDetails.filter(tag => {
                        return (tagPrefix == '' || (tagPrefix != '' && tag.name.toLowerCase().includes(tagPrefix.toLowerCase()))) && !data.tagIds.includes(tag.id)
                    })">
                        <div @click="data.tagIds.push(tagDetail.id)"
                             class="mx-1 my-1 border-gray-400 dark:border-gray-700 px-2 py-1 rounded-lg border-[1px] bg-white dark:bg-teal-700 hover:bg-blue-400 cursor-pointer">
                            {{ tagDetail.name }}
                        </div>
                    </template>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>