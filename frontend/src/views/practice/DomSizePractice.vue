<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import BaseLayout from '@/views/practice/BaseLayout.vue';
import { getDefaultTheme, setTheme } from '@/utils/theme';

const isFullScreen = ref(false);
const windowOuterWidth = ref(0);
const windowInnerWidth = ref(0);
const windowOuterHeight = ref(0);
const windowInnerHeight = ref(0);
const htmlScrollHeight = ref(0);
const bodyScrollHeight = ref(0);
const placeholderContentHeight = ref(100);
const placeholderClientHeight = ref(0);
const placeholderClientWidth = ref(0);
const placeholderOffsetHeight = ref(0);
const placeholderOffsetWidth = ref(0);
const placeholderScrollHeight = ref(0);

const toggleFullscreen = () => {
    isFullScreen.value = !isFullScreen.value;
    if (isFullScreen.value) {
        document.documentElement.requestFullscreen();
    } else {
        document.exitFullscreen();
    }
};

let timer: number;
onUnmounted(() => {
    setTheme(getDefaultTheme());
    window.clearInterval(timer);
});

onMounted(() => {
    setTheme('light');
    timer = window.setInterval(() => {
        windowOuterWidth.value = window.outerWidth;
        windowInnerWidth.value = window.innerWidth;
        windowOuterHeight.value = window.outerHeight;
        windowInnerHeight.value = window.innerHeight;
        bodyScrollHeight.value = document.body.scrollHeight;
        htmlScrollHeight.value = document.documentElement.scrollHeight;
        placeholderClientHeight.value = (
            document.getElementById('placeholder') as HTMLElement
        ).clientHeight;
        placeholderClientWidth.value = (
            document.getElementById('placeholder') as HTMLElement
        ).clientWidth;
        placeholderOffsetHeight.value = (
            document.getElementById('placeholder') as HTMLElement
        ).offsetHeight;
        placeholderOffsetWidth.value = (
            document.getElementById('placeholder') as HTMLElement
        ).offsetWidth;
        placeholderScrollHeight.value = (
            document.getElementById('placeholder') as HTMLElement
        ).scrollHeight;
    }, 100);
});
</script>

<template>
    <base-layout>
        <div>
            <div class="text-red-400 text-lg mb-4">
                <ul>
                    <li>当前盒子模型采用 box-sizing: border-box</li>
                    <li>滚动条在macOS下是悬浮的，不影响clientWidth</li>
                </ul>
            </div>
            <div class="flex items-center justify-center mb-4">
                <button class="btn" @click="toggleFullscreen">
                    <span v-if="!isFullScreen">启用全屏</span>
                    <span v-else>取消全屏</span>
                </button>
            </div>
            <div class="mb-4">
                <div><span>window.outerWidth</span>: {{ windowOuterWidth }}</div>
                <div><span>window.innerWidth</span>: {{ windowInnerWidth }}</div>
                <div><span>window.outerHeight</span>: {{ windowOuterHeight }}</div>
                <div><span>window.innerHeight</span>: {{ windowInnerHeight }}</div>
                <div><span>document.body.scrollHeight(body)</span>: {{ bodyScrollHeight }}</div>
                <div>
                    <span>document.documentElement.scrollHeight(html)</span>: {{ htmlScrollHeight }}
                </div>
                <div><span>placeholder.content.height</span>: {{ placeholderContentHeight }}</div>
                <div><span>placeholder.clientHeight</span>: {{ placeholderClientHeight }}</div>
                <div><span>placeholder.offsetHeight</span>: {{ placeholderOffsetHeight }}</div>
                <div><span>placeholder.scrollHeight</span>: {{ placeholderScrollHeight }}</div>
                <div><span>placeholder.clientWidth</span>: {{ placeholderClientWidth }}</div>
                <div><span>placeholder.offsetWidth</span>: {{ placeholderOffsetWidth }}</div>
            </div>
            <div
                id="placeholder"
                style="height: 500px; overflow-y: scroll"
                class="flex justify-center items-center py-10 border-4">
                <div
                    class="text-center text-2xl w-full bg-blue-300 flex justify-center items-center"
                    :style="`height: ${placeholderContentHeight}px`">
                    <span>占位区域</span>
                    <font-awesome-icon
                        :icon="['fas', 'circle-plus']"
                        class="cursor-pointer"
                        @click="placeholderContentHeight += 100" />
                    <font-awesome-icon
                        :icon="['fas', 'circle-minus']"
                        class="cursor-pointer"
                        @click="
                            placeholderContentHeight > 100 && (placeholderContentHeight -= 100)
                        " />
                </div>
            </div>
        </div>
    </base-layout>
</template>

<style scoped lang="scss"></style>
