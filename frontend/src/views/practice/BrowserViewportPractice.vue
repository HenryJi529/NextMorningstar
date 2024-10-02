<script setup lang="ts">

import {onMounted, ref} from "vue";

const isFullScreen = ref(false);
const outerWidth = ref(0);
const innerWidth = ref(0);
const outerHeight = ref(0);
const innerHeight = ref(0);
const scrollHeight = ref(0);
const placeholderHeight = ref(100);


const toggleFullscreen = () => {
    isFullScreen.value = !isFullScreen.value;
    if (isFullScreen.value) {
        document.documentElement.requestFullscreen();
    } else {
        document.exitFullscreen();
    }
};


onMounted(()=>{
    // NOTE: 对新移动端系统无效
    // window.addEventListener('load', function() {
    //     setTimeout(function() {
    //         window.scrollTo(0, 1);
    //     }, 100);
    // })
    setInterval(()=>{
        outerWidth.value = window.outerWidth;
        innerWidth.value = window.innerWidth;
        outerHeight.value = window.outerHeight;
        innerHeight.value = window.innerHeight;
        scrollHeight.value = document.documentElement.scrollHeight;
    },100)
})

</script>

<template>
    <div>
        <div class="flex items-center justify-center mb-4">
            <button class="btn" @click="toggleFullscreen">
                <span v-if="!isFullScreen">启用全屏</span>
                <span v-else>取消全屏</span>
            </button>
        </div>

        <div class="mb-4">
            <div>
                <span>window.outerWidth</span>: {{ outerWidth }}
            </div>
            <div>
                <span>window.innerWidth</span>: {{ innerWidth }}
            </div>
            <div>
                <span>window.outerHeight</span>: {{ outerHeight }}
            </div>
            <div>
                <span>window.innerHeight</span>: {{ innerHeight }}
            </div>
            <div>
                <span>document.documentElement.scrollHeight</span>: {{ scrollHeight }}
            </div>
        </div>

        <div :style="`height: ${placeholderHeight}px`" class="bg-blue-300 text-2xl pt-4">
            <div class="text-center w-full">
                <span>占位区域</span>
                <font-awesome-icon :icon="['fas', 'circle-plus']" class="cursor-pointer" @click="placeholderHeight+=100"/>
                <font-awesome-icon :icon="['fas', 'circle-minus']" class="cursor-pointer" @click="placeholderHeight-=100"/>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>