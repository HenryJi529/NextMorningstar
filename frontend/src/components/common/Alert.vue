<script lang="ts" setup>

defineProps<{ alertType: string }>();
</script>

<template>
    <div>
        <div :class="`alert alert-${alertType}`" role="alert">
            <slot name="svg"></slot>
            <slot name="message"></slot>
        </div>
    </div>
</template>