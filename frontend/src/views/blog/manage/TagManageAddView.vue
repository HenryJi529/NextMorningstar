<script lang="ts" setup>
import {ref} from "vue";
import {API_BLOG_TAG_ALL} from "@/constants/ApiConstant";
import type {CreateTagRequestVo} from "@/types/blog";
import CommonManageAddForm from "@/components/common/CommonManageAddForm.vue";
import OnlyNameField from "@/components/common/OnlyNameField.vue";

const data = ref<CreateTagRequestVo>({
    name: ""
});

</script>

<template>
    <common-manage-add-form :api="API_BLOG_TAG_ALL" :data="data" app="blog" resource="tag">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-add-form>
</template>

<style lang="scss" scoped>

</style>