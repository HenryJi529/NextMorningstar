<script setup lang="ts">
import axios from "axios";
import {onMounted, ref} from "vue";
import type {R} from "@/types/common";
import {API_BLOG_CATEGORY_ALL} from "@/constants/ApiConstant";
import type {CategoryDetail} from "@/types/blog";
import {useOtherStore} from "@/stores/others";
import router from "@/router";

const categories = ref<CategoryDetail[]>();
const otherStore = useOtherStore();

const jumpToCategory = (categoryId: number) => {
    router.push(`/blog?categoryId=${categoryId}`);
    otherStore.isMobileMenuOpen = false;
    window.scrollTo({
        top: 0,
        left: 0,
        behavior: 'smooth'
    });
}

onMounted(async ()=>{
    const response: R<CategoryDetail[]> = (await axios.get(API_BLOG_CATEGORY_ALL)).data;
    categories.value = response.data;
})
</script>

<template>
    <div>
        <div class="text-3xl lg:text-xl pb-2 text-center lg:text-left">分类</div>
        <div v-if="categories">
            <div v-if="categories.length > 0" class="grid grid-cols-2 lg:grid-cols-1">
                <template v-for="categoryDetail in categories">
                    <div v-if="categoryDetail.count > 0" class="flex space-x-2 items-center px-2 py-1">
                        <font-awesome-icon :icon="['fas', 'folder']" />
                        <div @click="jumpToCategory(categoryDetail.id)" class="hover:text-orange-300 cursor-pointer">
                            <span class="text-xl lg:text-base">
                                {{categoryDetail.name}}<sup class="text-gray-500">{{categoryDetail.count}}</sup>
                            </span>
                        </div>
                    </div>
                </template>
            </div>
            <div v-else>
                暂无分类!
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>