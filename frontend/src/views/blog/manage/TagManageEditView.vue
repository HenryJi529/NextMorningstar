<script lang="ts" setup>
import {useRoute} from "vue-router";
import {ref} from "vue";
import type {UpdateTagRequestVo} from "@/types/blog";
import {API_BLOG_TAG_ONE} from "@/constants/ApiConstant";
import CommonManageEditForm from "@/components/common/CommonManageEditForm.vue";
import OnlyNameField from "@/components/common/OnlyNameField.vue";

const route = useRoute();

const data = ref<UpdateTagRequestVo>({
    id: Number(route.params.id),
    name: ""
});

</script>

<template>
    <common-manage-edit-form :api="API_BLOG_TAG_ONE" :data="data" app="blog" resource="tag">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-edit-form>
</template>

<style lang="scss" scoped>

</style>