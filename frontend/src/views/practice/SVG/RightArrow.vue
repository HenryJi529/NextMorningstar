<script setup lang="ts">
import {computed} from "vue";

const props = defineProps<{
    shaftWidth: number;
    shaftLength: number;
    headWidth: number;
    headLength: number;
    fillColor?: string;
}>()

const fillColor = computed(() => props.fillColor ?? 'black')

</script>

<template>
    <svg width="100%" height="100%" :viewBox="`0 0 ${shaftLength+headLength} ${headWidth}`">
        <path :fill="fillColor" :stroke-width="0"
            :d="`M 0 ${(headWidth-shaftWidth)/2}
            l ${shaftLength} 0
            l 0 ${-(headWidth-shaftWidth)/2}
            L ${shaftLength+headLength} ${headWidth/2}
            L ${shaftLength} ${headWidth}
            l 0 ${-(headWidth-shaftWidth)/2}
            l ${-shaftLength} 0
            `"/>
    </svg>
</template>

<style scoped lang="scss">

</style>