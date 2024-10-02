<script setup lang="ts">
import {ref} from "vue";

const getImageUrl = (filename: string) => {
    return new URL(`../../assets/img/practice/${filename}`, import.meta.url).href;
};

const images = [
    "image1.jpg",
    "image2.jpg",
    "image3.jpg",
]

const currentIndex = ref(0);

</script>

<template>
    <div class="mx-auto max-w-[32rem] overflow-hidden">
        <div class="flex" style="transition: transform 0.5s ease" :style="{transform: `translateX(-${currentIndex * 100}%)`}">
            <template v-for="(image, ind) in images">
                <img :src="getImageUrl(image)" :alt="`image ${ind}`" class="w-full flex-shrink-0"/>
            </template>
        </div>
        <div class="flex space-x-2 justify-center items-center">
            <template v-for="(_, ind) in images">
                <button class="cursor-pointer p-3 text-2xl"
                        :class="{'text-blue-300': currentIndex === ind}"
                        @click="currentIndex = ind">
                    {{ind}}
                </button>
            </template>
        </div>
    </div>

</template>

<style scoped lang="scss">

</style>