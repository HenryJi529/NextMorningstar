<script lang="ts" setup>
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
    <svg :viewBox="`0 0 ${shaftLength+headLength} ${headWidth}`" height="100%" width="100%">
        <path :d="`M 0 ${(headWidth-shaftWidth)/2}
            l ${shaftLength} 0
            l 0 ${-(headWidth-shaftWidth)/2}
            L ${shaftLength+headLength} ${headWidth/2}
            L ${shaftLength} ${headWidth}
            l 0 ${-(headWidth-shaftWidth)/2}
            l ${-shaftLength} 0
            `" :fill="fillColor"
              :stroke-width="0"/>
    </svg>
</template>

<style lang="scss" scoped>

</style>