<script lang="ts" setup>

import AlertError from "@/components/common/AlertError.vue";
import Logo from "@/components/Logo.vue";

defineProps<{
    errorMessage: string;
}>()

</script>

<template>
    <div class="relative">
        <div class="absolute top-[-10rem] w-[30rem] p-4">
            <alert-error v-if="errorMessage">
                {{errorMessage}}
            </alert-error>
        </div>
        <div class="w-full max-w-[30rem] flex flex-col space-y-4 p-4 bg-slate-200 dark:bg-slate-600 rounded-3xl shadow-xl">
            <logo></logo>
            <slot></slot>
        </div>
    </div>
</template>