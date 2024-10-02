<script setup lang="ts">
import {useRoute} from "vue-router";
import {ref} from "vue";
import type {UpdateCategoryRequestVo} from "@/types/blog";
import {API_BLOG_CATEGORY_ONE} from "@/constants/ApiConstant";
import CommonManageEdit from "@/components/blog/CommonManageEdit.vue";
import OnlyNameField from "@/components/blog/OnlyNameField.vue";

const route = useRoute();

const data = ref<UpdateCategoryRequestVo>({
    id:  Number(route.params.id),
    name: ""
});

</script>

<template>
    <common-manage-edit name="分类" slug="category" :api="API_BLOG_CATEGORY_ONE" :data="data">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-edit>
</template>

<style scoped lang="scss">

</style>