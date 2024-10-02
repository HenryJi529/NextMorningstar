<script lang="ts" setup>
import Alert from './Alert.vue';
</script>

<template>
    <div>
        <alert :alert-type="`success`">
            <template #svg>
                <svg
                    class="h-6 w-6 shrink-0 stroke-current"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg">
                    <path
                        d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"/>
                </svg>
            </template>
            <template #message>
                <slot></slot>
            </template>
        </alert>
    </div>
</template>