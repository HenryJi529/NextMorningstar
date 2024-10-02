<script lang="ts" setup>
import { useRoute } from 'vue-router';
import { ref } from 'vue';
import type { UpdateCategoryRequestVo } from '@/types/blog';
import { API_BLOG_CATEGORY_ONE } from '@/constants/api';
import CommonManageEditForm from '@/components/manage/CommonManageEditForm.vue';
import OnlyNameField from '@/components/manage/OnlyNameField.vue';

const route = useRoute();

const data = ref<UpdateCategoryRequestVo>({
    id: Number(route.params.id),
    name: '',
});
</script>

<template>
    <common-manage-edit-form
        :api="API_BLOG_CATEGORY_ONE"
        :data="data"
        app="blog"
        resource="category">
        <template #fields>
            <only-name-field :name="data.name" @update-name="name => (data.name = name)" />
        </template>
    </common-manage-edit-form>
</template>

<style lang="scss" scoped></style>
