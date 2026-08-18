<script setup lang="ts">
import type { RunPrStatus } from '@/types/dev';
import { prStatusBadgeClass, prStatusDividerClass, prStatusLabel } from '@/libs/dev';

defineProps<{
    prId: number;
    prLink?: string;
    prStatus?: RunPrStatus;
}>();
</script>

<template>
    <a
        :href="prLink"
        target="_blank"
        rel="noopener noreferrer"
        class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono border hover:underline"
        :class="
            prStatus ? prStatusBadgeClass(prStatus) : 'bg-slate-50 text-slate-400 border-slate-200'
        ">
        #{{ prId }}
        <template v-if="prStatus">
            <span class="w-px h-2.5 opacity-40" :class="prStatusDividerClass(prStatus)"></span>
            {{ prStatusLabel(prStatus) }}
        </template>
    </a>
</template>
