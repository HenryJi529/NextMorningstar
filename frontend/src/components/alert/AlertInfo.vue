<script lang="ts" setup>
import Alert from './Alert.vue';
</script>

<template>
    <div>
        <alert alert-type="info">
            <template #svg>
                <font-awesome-icon class="w-full h-full" icon="fa-solid fa-circle-info" />
            </template>
            <template #message>
                <slot></slot>
            </template>
        </alert>
    </div>
</template>
