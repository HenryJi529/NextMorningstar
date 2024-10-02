<script lang="ts" setup>

import {onMounted, ref} from "vue";

const props = defineProps<{
    count: number;
}>()

const value = ref(props.count);

onMounted(() => {
    setInterval(() => {
        value.value--;
    }, 1000)
})

</script>

<template>
    <span class="countdown font-mono">
      <span :style="`--value:${value};`"></span>
    </span>
</template>

<style lang="scss" scoped>

</style>