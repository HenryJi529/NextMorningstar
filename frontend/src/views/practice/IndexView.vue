<script setup lang="ts">
    import {useRouter} from "vue-router";

    const router = useRouter();
    const practices = [
        "practice-htmlTableToExcelWorkbook",
        "practice-echartsBar",
        "practice-fileUpload",
        "practice-carousel",
        "practice-dropdown"
    ];
    const routes = practices.map(practice => router.getRoutes().find(route => route.name === practice));

</script>

<template>
    <ul class="p-20">
        <template v-for="route in routes">
            <li class="text-center my-2 text-xl">
                <router-link :to="{name: route?.name}">{{route?.meta.verboseName}}
                </router-link>
            </li>
        </template>
    </ul>
</template>

<style scoped lang="scss">

</style>