<script setup lang="ts">
import axios from "axios";
import {onMounted, ref} from "vue";
import type {R} from "@/types/common";
import {API_BLOG_CATEGORY_ALL} from "@/constants/ApiConstant";
import type {CategoryDetail} from "@/types/blog";

const categories = ref<CategoryDetail[]>();

onMounted(async ()=>{
    const response: R<CategoryDetail[]> = (await axios.get(API_BLOG_CATEGORY_ALL)).data;
    categories.value = response.data;
})
</script>

<template>
    <div>
        <div class="text-xl pb-2">分类</div>
        <div v-if="categories">
            <div v-if="categories.length > 0">
                <template v-for="categoryDetail in categories">
                    <div v-if="categoryDetail.count > 0" class="flex space-x-2 items-center px-2 py-1">
                        <font-awesome-icon :icon="['fas', 'folder']" />
                        <router-link :to="`/blog?categoryId=${categoryDetail.id}`" class="hover:text-orange-300">
                        <span>
                            {{categoryDetail.name}}<sup class="text-gray-500">{{categoryDetail.count}}</sup>
                        </span>
                        </router-link>
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