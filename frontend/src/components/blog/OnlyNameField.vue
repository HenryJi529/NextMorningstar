<script setup lang="ts">

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
        <input v-model="data.name" type="text"
               class="blog-manage-input flex-1 p-2 rounded-lg"/>
    </div>
</template>

<style scoped lang="scss">

</style>