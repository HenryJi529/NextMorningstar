<script setup lang="ts">
import { type CreateSysParamRequestVo } from '@/types/system';
import { ref } from 'vue';
import { API_SYSTEM_PARAM_ALL } from '@/constants/api';
import CommonManageAddForm from '@/components/manage/CommonManageAddForm.vue';
import SysParamFields from '@/views/admin/components/SysParamFields.vue';

const data = ref<CreateSysParamRequestVo>({
    name: '',
    value: '',
    scope: '',
    status: true,
    remark: '',
});
</script>

<template>
    <common-manage-add-form :api="API_SYSTEM_PARAM_ALL" :data="data" app="admin" resource="param">
        <template #fields>
            <sys-param-fields :data="data" />
        </template>
    </common-manage-add-form>
</template>

<style scoped lang="scss"></style>
