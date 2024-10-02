<script setup lang="ts">

import type {Item} from "@/types/nav";
import { ref } from 'vue';

const showModal = ref(false);

const toggleModal = (): void => {
    showModal.value = !showModal.value;
}

const openUrl = (url: string): void => {
    window.open(url, '_blank');
}

defineProps<{item: Item}>();

</script>

<template>
    <div>
        <div
            @click="item.display=='modal' ? toggleModal() : item.url && openUrl(item.url)"
            class="neumorphism-shadow rounded-3xl py-4 pl-6 hover:translate-x-2 transition duration-300 delay-75 ease-linear cursor-pointer">
            <div class="flex items-center">
                <img v-if="item.image" :src="item.image" :alt="`${item.name}的图片`" class="rounded-full w-10 h-10">
                <div class="pl-2 text-lg font-bold">{{item.name}}</div>
            </div>
            <div v-if="item.desc" class="indent-4">{{item.desc}}</div>
        </div>
        <div v-if="item.display=='modal'" class="modal" :class="{ 'modal-open': showModal }">
            <div class="modal-box">
                <h1 class="text-3xl font-bold">{{ item.name }}</h1>
                <ul class="pl-8 py-4">
                    <template v-for="entry in item.entries">
                        <li
                            :class="`${entry.class == 'special'? 'text-red-400': ''} hover:text-lime-400 rocket-list-style`">
                            <a :href="entry.link" target="_blank" class="cursor-pointer text-lg">
                                 <span class="pl-2">{{ entry.name }}</span>
                            </a>
                        </li>
                    </template>
                </ul>
                <div class="modal-action">
                    <button class="btn" @click="toggleModal">
                        关闭
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.neumorphism-shadow {
    box-shadow: 0.5rem 0.5rem 1rem #9d9d9d, -0.5rem -0.5rem 1rem #ffffff;
}

[data-theme="dark"] .neumorphism-shadow {
    box-shadow: 0.5rem 0.5rem 1rem #191e24, -0.5rem -0.5rem 1rem #212830;
}
</style>