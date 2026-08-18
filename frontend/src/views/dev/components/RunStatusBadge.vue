<script setup lang="ts">
import type { RunStatus } from '@/types/dev';
import { runStatusBadgeClass, runStatusLabel } from '@/libs/dev';

defineProps<{
    status: RunStatus;
}>();
</script>

<template>
    <span class="px-2 py-0.5 rounded text-xs border" :class="runStatusBadgeClass(status)">
        {{ runStatusLabel(status) }}
    </span>
</template>
