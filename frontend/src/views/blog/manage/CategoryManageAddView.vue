<script setup lang="ts">
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
    <common-manage-add-form app="blog" resource="category" :api="API_BLOG_CATEGORY_ALL" :data="data">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-add-form>
</template>

<style scoped lang="scss">

</style>