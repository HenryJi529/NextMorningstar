<script lang="ts" setup>
import {useRouter} from "vue-router";
import BaseLayout from "@/views/practice/BaseLayout.vue";

const router = useRouter();
const routes = router.getRoutes().filter(route =>
    typeof route.name === 'string' &&
    route.name.startsWith('practice-') && !route.name.startsWith('practice-sub') &&
    route.name !== 'practice-index'
)

</script>

<template>
    <BaseLayout>
        <ul>
            <template v-for="route in routes">
                <li class="text-center text-xl">
                    <router-link :to="{name: route?.name}">{{ route?.meta.verboseName }}
                    </router-link>
                </li>
            </template>
        </ul>
    </BaseLayout>
</template>

<style lang="scss" scoped>

</style>