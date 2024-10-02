<script setup lang="ts">
import 'animate.css';
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";
import GithubCorner from "@/components/GithubCorner.vue";
import MorningstarFooter from "@/components/Footer.vue";
import ImageBoard from "@/components/love/ImageBoard.vue";

useFavicon().value = '/love.ico'
useHead({
    title: 'Love - 爱の相册',
    meta: [
        {
            name: 'description',
            content: '随机展示爱人的照片'
        }
    ]
});

</script>

<template>
    <div class="flex flex-col justify-between items-center w-screen h-page">
        <github-corner/>
        <header class="animate__animated animate__fadeIn animate__slow text-4xl pt-6 flex space-x-2">
            <a href="https://www.koalastothemax.com/" target="_blank">
                <font-awesome-icon :icon="['fas', 'copyright']" />
            </a>
            <span class="text-pink-300 uppercase font-family-chewy">loved ones</span>
        </header>
        <main class="flex-1 flex justify-center items-center">
            <transition name="board" mode="out-in">
                <Suspense>
                    <image-board />
                    <template #fallback>
                        <div class="font-bold text-center p-7 rounded-3xl text-6xl border-8 border-double border-primary">
                            loading...
                        </div>
                    </template>
                </Suspense>
            </transition>
        </main>
        <morningstar-footer/>
    </div>
</template>

<style scoped lang="scss">
.board-enter-from, .board-leave-to {
    opacity: 0;
}
.board-enter-to, .board-leave-from {
    opacity: 1;
}
.board-leave-active {
    transition: all 0.5s ease;
}
.board-enter-active {
    transition: all 3s ease;
}


</style>