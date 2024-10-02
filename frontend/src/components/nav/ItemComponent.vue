<script lang="ts" setup>

import type {Item} from "@/types/nav";
import {ref} from 'vue';

const modalOpen = ref(false);

const toggleModal = (): void => {
    modalOpen.value = !modalOpen.value;
}

const openUrl = (url: string): void => {
    window.open(url, '_blank');
}

const getImageUrl = (path: string) => {
    const [category, filename] = path.split('/');
    return new URL(`../../assets/img/nav/${category}/${filename}`, import.meta.url).href;
};

defineProps<{ item: Item }>();

</script>

<template>
    <div>
        <div
            class="nav-item neumorphism-shadow rounded-3xl py-4 pl-6 hover:translate-x-2 transition duration-300 delay-75 ease-linear cursor-pointer"
            @click="item.isModal ? toggleModal() : item.url && openUrl(item.url)">
            <div class="flex items-center">
                <img v-if="item.image" :alt="`${item.name}的图片`" :src="getImageUrl(item.image)"
                     class="rounded-full w-10 h-10">
                <div class="pl-2 text-lg font-bold">{{ item.name }}</div>
            </div>
            <div v-if="item.desc" class="indent-4 mt-2">{{ item.desc }}</div>
        </div>
        <div v-if="item.isModal" :class="{ 'modal-open': modalOpen }" class="modal">
            <div class="modal-box">
                <h1 class="text-3xl font-bold">{{ item.name }}</h1>
                <ul class="pl-8 py-4">
                    <template v-for="entry in item.entries">
                        <li
                            :class="`${entry.isSpecial ? 'text-red-400': ''} hover:text-lime-400 rocket-list-style`">
                            <div class="cursor-pointer text-lg" @click="openUrl(entry.link)">
                                <span class="pl-2">{{ entry.name }}</span>
                            </div>
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

<style lang="scss" scoped>
.neumorphism-shadow {
    box-shadow: 0.5rem 0.5rem 1rem #9d9d9d, -0.5rem -0.5rem 1rem #ffffff;
}

[data-theme="dark"] .neumorphism-shadow {
    box-shadow: 0.5rem 0.5rem 1rem #191e24, -0.5rem -0.5rem 1rem #212830;
}
</style>