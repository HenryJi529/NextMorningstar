<script setup lang="ts">
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
    <common-manage-add-form app="blog" resource="tag" :api="API_BLOG_TAG_ALL" :data="data">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-add-form>
</template>

<style scoped lang="scss">

</style>