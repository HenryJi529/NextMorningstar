<script lang="ts" setup>
import { onMounted, onUnmounted, ref } from 'vue';

const props = defineProps<{
    count: number;
}>();

const value = ref(props.count);

let timer: number;
onMounted(() => {
    timer = window.setInterval(() => {
        value.value--;
    }, 1000);
});

onUnmounted(() => {
    window.clearInterval(timer);
});
</script>

<template>
    <span class="countdown font-mono">
        <span :style="`--value:${value};`"></span>
    </span>
</template>

<style lang="scss" scoped></style>
