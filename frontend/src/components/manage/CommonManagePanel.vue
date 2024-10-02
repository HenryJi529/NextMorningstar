<script lang="ts" setup>

import type {NavItem4Manage} from "@/types/common";
import {useRoute} from "vue-router";

const route = useRoute();

defineProps<{
    items: NavItem4Manage[];
}>()
</script>

<template>
    <div class="py-6 lg:flex">
        <div class="text-2xl flex justify-around py-2 lg:py-6 lg:justify-start lg:flex-col lg:space-y-8">
            <template v-for="item in items" :key="item.enName">
                <router-link :class="{'nav-item-active': route.name === item.editRouteName}"
                             :to="{name: item.listRouteName}"
                             active-class="nav-item-active"
                             class="nav-item">
                    <div class="icon">
                        <font-awesome-icon :icon="item.iconName"/>
                    </div>
                    <span>{{ item.cnName }}</span>
                </router-link>
            </template>
        </div>
        <div class="lg:flex-1 px-8">
            <router-view/>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.nav-item {
    @apply flex items-center justify-center space-x-2 cursor-pointer;
    .icon {
        @apply w-8 flex justify-center items-center;
    }

    &-active {
        @apply text-blue-600 dark:text-blue-500;
    }
}
</style>