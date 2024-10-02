<script lang="ts" setup>
import Alert from './Alert.vue';
</script>

<template>
    <div>
        <alert alert-type="warning">
            <template #svg>
                <font-awesome-icon class="w-full h-full" icon="fa-solid fa-circle-exclamation" />
            </template>
            <template #message>
                <slot></slot>
            </template>
        </alert>
    </div>
</template>