<script lang="ts" setup>
import { useRoute } from 'vue-router';
import { ref } from 'vue';
import type { UpdateTagRequestVo } from '@/types/blog';
import { API_BLOG_TAG_ONE } from '@/constants/api';
import CommonManageEditForm from '@/components/manage/CommonManageEditForm.vue';
import OnlyNameField from '@/components/manage/OnlyNameField.vue';

const route = useRoute();

const data = ref<UpdateTagRequestVo>({
    id: Number(route.params.id),
    name: '',
});
</script>

<template>
    <common-manage-edit-form :api="API_BLOG_TAG_ONE" :data="data" app="blog" resource="tag">
        <template #fields>
            <only-name-field :name="data.name" @update-name="name => (data.name = name)" />
        </template>
    </common-manage-edit-form>
</template>

<style lang="scss" scoped></style>
