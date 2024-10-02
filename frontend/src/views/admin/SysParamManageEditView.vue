<script setup lang="ts">
import { ref } from 'vue';
import { type UpdateSysParamRequestVo } from '@/types/system';
import CommonManageEditForm from '@/components/manage/CommonManageEditForm.vue';
import { API_SYSTEM_PARAM_ONE } from '@/constants/api';
import { useRoute } from 'vue-router';
import SysParamFields from '@/views/admin/components/SysParamFields.vue';

const route = useRoute();
const data = ref<UpdateSysParamRequestVo>({
    id: route.params.id as string,
    name: '',
    value: '',
    scope: '',
    status: true,
    remark: '',
});
</script>

<template>
    <common-manage-edit-form app="admin" resource="param" :api="API_SYSTEM_PARAM_ONE" :data="data">
        <template #fields>
            <sys-param-fields :data="data" />
        </template>
    </common-manage-edit-form>
</template>

<style scoped lang="scss"></style>
