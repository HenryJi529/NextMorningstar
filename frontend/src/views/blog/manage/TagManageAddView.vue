<script setup lang="ts">
import {ref} from "vue";
import {API_BLOG_TAG_ALL} from "@/constants/ApiConstant";
import type {CreateTagRequestVo} from "@/types/blog";
import CommonManageAdd from "@/components/blog/CommonManageAdd.vue";
import OnlyNameField from "@/components/blog/OnlyNameField.vue";

const data = ref<CreateTagRequestVo>({
    name: ""
});

</script>

<template>
    <common-manage-add slug="tag" :api="API_BLOG_TAG_ALL" :data="data">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-add>
</template>

<style scoped lang="scss">

</style>