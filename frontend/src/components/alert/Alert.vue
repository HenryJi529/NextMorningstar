<script lang="ts" setup>

defineProps<{ alertType: string }>();
</script>

<template>
    <div>
        <div :class="`alert alert-${alertType} flex items-center`" role="alert">
            <div class="w-5 h-5">
                <slot name="svg"></slot>
            </div>
            <slot name="message"></slot>
        </div>
    </div>
</template>