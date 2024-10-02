<script lang="ts" setup>
import {ref} from "vue";
import {API_BLOG_CATEGORY_ALL} from "@/constants/ApiConstant";
import type {CreateCategoryRequestVo} from "@/types/blog";
import CommonManageAddForm from "@/components/common/CommonManageAddForm.vue";
import OnlyNameField from "@/components/common/OnlyNameField.vue";

const data = ref<CreateCategoryRequestVo>({
    name: ""
});

</script>

<template>
    <common-manage-add-form :api="API_BLOG_CATEGORY_ALL" :data="data" app="blog" resource="category">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-add-form>
</template>

<style lang="scss" scoped>

</style>