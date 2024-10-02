<script setup lang="ts">
import {useRoute} from "vue-router";
import {ref} from "vue";
import type {UpdateTagRequestVo} from "@/types/blog";
import {API_BLOG_TAG_ONE} from "@/constants/ApiConstant";
import CommonManageEdit from "@/components/blog/CommonManageEdit.vue";
import OnlyNameField from "@/components/blog/OnlyNameField.vue";

const route = useRoute();

const data = ref<UpdateTagRequestVo>({
    id:  Number(route.params.id),
    name: ""
});

</script>

<template>
    <common-manage-edit name="标签" slug="tag" :api="API_BLOG_TAG_ONE" :data="data">
        <template #fields>
            <only-name-field :data="data"/>
        </template>
    </common-manage-edit>
</template>

<style scoped lang="scss">

</style>