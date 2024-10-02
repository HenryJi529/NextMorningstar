<script lang="ts" setup>
import Alert from './Alert.vue';
</script>

<template>
    <div>
        <alert :alert-type="`info`">
            <template #svg>
                <svg
                    class="h-6 w-6 shrink-0 stroke-current"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg">
                    <path
                        d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"></path>
                </svg>
            </template>
            <template #message>
                <slot></slot>
            </template>
        </alert>
    </div>
</template>