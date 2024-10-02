<script lang="ts" setup>
import ErrorView from '@/views/ErrorView.vue';
import { StatusCodes, ReasonPhrases } from 'http-status-codes';
</script>

<template>
    <error-view> {{ StatusCodes.FORBIDDEN }}: {{ ReasonPhrases.FORBIDDEN }}. </error-view>
</template>

<style lang="scss" scoped></style>
