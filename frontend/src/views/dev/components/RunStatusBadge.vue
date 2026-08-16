<script setup lang="ts">
import type { RunStatus } from '@/types/dev';
import { runStatusBadgeClass } from '@/libs/dev';

defineProps<{
    status: RunStatus;
}>();
</script>

<template>
    <span class="px-2 py-0.5 rounded text-xs border" :class="runStatusBadgeClass(status)">
        {{ status }}
    </span>
</template>
