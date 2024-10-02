<script lang="ts" setup>

interface Data {
    name: string
}

defineProps<{
    data: Data;
}>()
</script>

<template>
    <div class="flex space-x-16 items-center">
        <span class="font-bold text-center w-32">Name</span>
        <input v-model="data.name" class="admin-manage-input"
               type="text"/>
    </div>
</template>

<style lang="scss" scoped>

</style>