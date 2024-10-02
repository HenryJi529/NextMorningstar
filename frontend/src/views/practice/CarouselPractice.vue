<script lang="ts" setup>
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
        <div :style="{transform: `translateX(-${currentIndex * 100}%)`}" class="flex"
             style="transition: transform 0.5s ease">
            <template v-for="(image, ind) in images">
                <img :alt="`image ${ind}`" :src="getImageUrl(image)" class="w-full flex-shrink-0"/>
            </template>
        </div>
        <div class="flex space-x-2 justify-center items-center">
            <template v-for="(_, ind) in images">
                <button :class="{'text-blue-300': currentIndex === ind}"
                        class="cursor-pointer p-3 text-2xl"
                        @click="currentIndex = ind">
                    {{ ind }}
                </button>
            </template>
        </div>
    </div>

</template>

<style lang="scss" scoped>

</style>