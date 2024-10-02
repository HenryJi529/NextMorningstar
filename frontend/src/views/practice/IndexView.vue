<script lang="ts" setup>
import {useRouter} from "vue-router";

const router = useRouter();
const routes = router.getRoutes().filter(route =>
    typeof route.name === 'string' &&
    route.name.startsWith('practice-') &&
    route.name !== 'practice-index'
)

</script>

<template>
    <ul>
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