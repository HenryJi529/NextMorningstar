<script lang="ts" setup>
import {useRouter} from "vue-router";

const router = useRouter();
const practices = [
    "practice-htmlTableToExcelWorkbook",
    "practice-echartsBar",
    "practice-fileUpload",
    "practice-carousel",
    "practice-dropdown",
    "practice-hyperscript",
    "practice-mock",
    "practice-tabs",
    "practice-parseExcelWorkbook",
    "practice-svg",
    "practice-abortable-request",
    "practice-zip",
    "practice-browser-viewport",
    "practice-linear-progress-bar",
    "practice-markdown-convert"
];
const routes = practices.map(practice => router.getRoutes().find(route => route.name === practice));

</script>

<template>
    <ul class="py-5">
        <template v-for="route in routes">
            <li class="text-center text-xl">
                <router-link :to="{name: route?.name}">{{ route?.meta.verboseName }}
                </router-link>
            </li>
        </template>
    </ul>
</template>

<style lang="scss" scoped>

</style>