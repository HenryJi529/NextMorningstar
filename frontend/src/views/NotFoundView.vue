<script lang="ts" setup>
import ErrorView from '@/views/ErrorView.vue';
import { StatusCodes, ReasonPhrases } from 'http-status-codes';
</script>

<template>
    <error-view> {{ StatusCodes.NOT_FOUND }}: {{ ReasonPhrases.NOT_FOUND }}. </error-view>
</template>

<style lang="scss" scoped></style>
