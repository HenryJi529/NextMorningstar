<script setup lang="ts">

defineProps<{
    ownerName: string,
    repoName: string,
    filePath: string,
}>()
</script>

<template>
<div class="flex justify-center items-center space-x-4">
    <a :href="`https://github.com/${ownerName}/${repoName}/raw/main/${filePath}`" target="_blank">Github</a>
    <a :href="`https://cdn.jsdelivr.net/gh/${ownerName}/${repoName}@main/${filePath}`" target="_blank">jsDelivr</a>
    <a :href="`https://cdn.jsdmirror.com/gh/${ownerName}/${repoName}@main/${filePath}`" target="_blank">JSDMirror</a>
</div>
</template>

<style scoped lang="scss">

</style>