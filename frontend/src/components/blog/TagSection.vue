<script lang="ts" setup>
import {onMounted, ref} from "vue";
import type {R} from "@/types/common";
import {API_BLOG_TAG_ALL} from "@/constants/ApiConstant";
import type {TagDetail} from "@/types/blog";
import axios from '@/axios/index';
import router from "@/router";
import {useBlogStore} from "@/stores/blog";

const blogStore = useBlogStore();
const tags = ref<TagDetail[]>();

const jumpToTag = (tagId: number) => {
    router.push(`/blog?tagId=${tagId}`);
    blogStore.isMobileMenuOpen = false;
    window.scrollTo({
        top: 0,
        left: 0,
        behavior: 'smooth'
    });
}

onMounted(async () => {
    const response: R<TagDetail[]> = (await axios.get(API_BLOG_TAG_ALL)).data;
    tags.value = response.data;
})

</script>

<template>
    <div>
        <div class="text-3xl lg:text-xl pb-2 text-center lg:text-left">标签</div>
        <div v-if="tags">
            <div v-if="tags.length > 0" class="grid grid-cols-2 lg:grid-cols-1 xl:grid-cols-2">
                <template v-for="tagDetail in tags">
                    <div v-if="tagDetail.count > 0" class="flex items-center space-x-2 px-2 py-1">
                        <font-awesome-icon :icon="['fas', 'tag']"/>
                        <div class="hover:text-orange-300 cursor-pointer" @click="jumpToTag(tagDetail.id)">
                            <span class="text-xl lg:text-base">
                                {{ tagDetail.name }}<sup class="text-gray-500">{{ tagDetail.count }}</sup>
                            </span>
                        </div>
                    </div>
                </template>
            </div>
            <div v-else>
                暂无标签!
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>