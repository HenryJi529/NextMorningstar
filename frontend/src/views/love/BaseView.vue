<script lang="ts" setup>
import 'animate.css';
import {useHead} from "@vueuse/head";
import {useFavicon} from "@vueuse/core";
import GithubCorner from "@/components/GithubCorner.vue";
import MorningstarFooter from "@/components/Footer.vue";
import ImageBoard from "@/components/love/ImageBoard.vue";
import {getDefaultTheme, setTheme} from "@/utils/handleTheme";


setTheme(getDefaultTheme());
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
window.addEventListener('focus', () => {
    document.title = '欢迎回来～～';
    setTimeout(() => {
        document.title = 'Love - 爱の相册';
    }, 500);
});

window.addEventListener('blur', () => {
    document.title = '（⊙︿⊙）不再看一张嘛';
});

</script>

<template>
    <div class="flex flex-col justify-between items-center w-screen h-page">
        <github-corner/>
        <header class="animate__animated animate__fadeIn animate__slow pt-6 flex space-x-2 pb-2 items-center">
            <a href="https://www.koalastothemax.com/" target="_blank">
                <font-awesome-icon :icon="['fas', 'copyright']" class="text-2xl relative top-1"/>
            </a>
            <span class="font-family-song nice-color text-3xl">谨以此页献给欢欢</span>
        </header>
        <main class="flex-1 flex justify-center items-center">
            <transition mode="out-in" name="board">
                <Suspense>
                    <image-board/>
                    <template #fallback>
                        <div
                            class="font-bold text-center p-7 rounded-3xl text-6xl border-8 border-double border-primary">
                            loading...
                        </div>
                    </template>
                </Suspense>
            </transition>
        </main>
        <morningstar-footer/>
    </div>
</template>

<style lang="scss" scoped>
.nice-color {
    color: transparent;
    background-image: linear-gradient(to bottom, #FFE8D6, #F9A9D4);
    background-clip: text;
}

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