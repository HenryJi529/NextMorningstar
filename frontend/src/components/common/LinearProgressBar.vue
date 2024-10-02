<script setup lang="ts">

import {computed} from "vue";

const props = defineProps<{
    width: number;
    height: number;
    radius: number;
    defaultColor?: string;
    activeColor: string;
    precent: number
}>();

const defaultColor = computed(() => props.defaultColor ?? '#F0F0F0')

</script>

<template>
    <div :style="`width: ${props.width}px; height: ${props.height}px; background-color: ${defaultColor}; border-radius: ${props.radius}px`">
        <div class="h-full" :style="{backgroundColor: props.activeColor, width: `${props.precent}%`, borderRadius: `${props.radius}px`}"></div>
    </div>
</template>

<style scoped lang="scss">

</style>