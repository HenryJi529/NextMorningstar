<script lang="ts" setup>
import { useRouter } from 'vue-router';
import BaseLayout from '@/views/lab/BaseLayout.vue';
import type { LabRouteMeta } from '@/router/lab';

const router = useRouter();
const routes = router
    .getRoutes()
    .filter(
        route =>
            typeof route.name === 'string' &&
            route.name.startsWith('lab-') &&
            !route.name.startsWith('lab-sub') &&
            route.name !== 'lab-index'
    );

const metaOf = (route: (typeof routes)[number]) => route.meta as LabRouteMeta;
</script>

<template>
    <BaseLayout>
        <div class="w-full px-6 pb-10">
            <p class="text-center text-base-content/60 mb-8">共 {{ routes.length }} 个实验</p>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                <router-link
                    v-for="route in routes"
                    :key="route.name"
                    :to="{ name: route.name }"
                    class="card bg-base-100 border border-base-300 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:shadow-lg">
                    <div class="card-body p-5">
                        <h3 class="card-title text-lg">{{ metaOf(route).verboseName }}</h3>
                        <p class="text-sm text-base-content/60">{{ metaOf(route).description }}</p>
                        <div class="card-actions mt-2">
                            <span
                                v-for="tag in metaOf(route).tags ?? []"
                                :key="tag"
                                class="badge badge-outline badge-sm">
                                {{ tag }}
                            </span>
                        </div>
                    </div>
                </router-link>
            </div>
        </div>
    </BaseLayout>
</template>

<style lang="scss" scoped></style>
