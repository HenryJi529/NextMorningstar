<script lang="ts" setup>

defineProps<{alertType: string}>();
</script>

<template>
    <div>
        <div role="alert" :class="`alert alert-${alertType}`">
            <slot name="svg"></slot>
            <slot name="message"></slot>
        </div>
    </div>
</template>