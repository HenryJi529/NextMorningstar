<script setup lang="ts">

withDefaults(defineProps<{
    width: number;
    height: number;
    radius: number;
    defaultColor?: string;
    activeColor: string;
    precent: number
}>(), {
    defaultColor: '#F0F0F0'
})

</script>

<template>
    <div :style="`width: ${width}px; height: ${height}px; background-color: ${defaultColor}; border-radius: ${radius}px`">
        <div class="h-full" :style="{backgroundColor: activeColor, width: `${precent}%`, borderRadius: `${radius}px`}"></div>
    </div>
</template>

<style scoped lang="scss">

</style>