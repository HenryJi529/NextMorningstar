<script lang="ts" setup>

import AlertError from "@/components/common/AlertError.vue";
import Logo from "@/components/Logo.vue";
import AlertSuccess from "@/components/common/AlertSuccess.vue";

defineProps<{
    errorMessage: string;
    successMessage?: string;
}>()

</script>

<template>
    <div class="flex justify-center items-center">
        <div class="w-[30rem]">
            <div v-if="errorMessage || successMessage" class="p-4">
                <alert-error v-if="errorMessage">
                    {{ errorMessage }}
                </alert-error>
                <alert-success v-if="successMessage">
                    {{ successMessage }}
                </alert-success>
            </div>
            <div class="flex flex-col space-y-4 p-4 bg-slate-200 dark:bg-slate-600 rounded-3xl shadow-xl">
                <logo></logo>
                <slot></slot>
            </div>
        </div>
    </div>
</template>