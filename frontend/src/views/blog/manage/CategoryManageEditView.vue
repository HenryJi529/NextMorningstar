<script setup lang="ts">
import {useRoute} from "vue-router";
import {ref} from "vue";
import type {UpdateCategoryRequestVo} from "@/types/blog";
import {API_BLOG_CATEGORY_ONE} from "@/constants/ApiConstant";
import CommonManageEditForm from "@/components/common/CommonManageEditForm.vue";
import OnlyNameField from "@/components/common/OnlyNameField.vue";

const route = useRoute();

const data = ref<UpdateCategoryRequestVo>({
    id:  Number(route.params.id),
    name: ""
});

</script>

<template>
    <common-manage-edit-form app="blog" resource="category" :api="API_BLOG_CATEGORY_ONE" :data="data">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-edit-form>
</template>

<style scoped lang="scss">

</style>