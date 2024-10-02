<script setup lang="ts">
import {onMounted, ref} from "vue";
import type {R} from "@/types/common";
import {API_BLOG_TAG_ALL} from "@/constants/ApiConstant";
import type {TagDetail} from "@/types/blog";
import axios from "axios";

const tags = ref<TagDetail[]>();

onMounted(async () => {
    const response: R<TagDetail[]> = (await axios.get(API_BLOG_TAG_ALL)).data;
    tags.value = response.data;
})

</script>

<template>
    <div>
        <div class="text-xl pb-2">标签</div>
        <div v-if="tags">
            <div v-if="tags.length > 0" class="grid lg:grid-cols-1 xl:grid-cols-2">
                <template v-for="tagDetail in tags">
                    <div v-if="tagDetail.count > 0" class="flex items-center space-x-2 px-2 py-1">
                        <font-awesome-icon :icon="['fas', 'tag']" />
                        <router-link :to="`/blog?tagId=${tagDetail.id}`" class="hover:text-orange-300">
                        <span>
                            {{tagDetail.name}}<sup class="text-gray-500">{{tagDetail.count}}</sup>
                        </span>
                        </router-link>
                    </div>
                </template>
            </div>
            <div v-else>
                暂无标签!
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>