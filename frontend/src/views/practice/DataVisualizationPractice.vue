<script setup lang="ts">
import BaseLayout from '@/views/practice/BaseLayout.vue';
import FunnelChart from '@/views/practice/viz/FunnelChart.vue';
import { onMounted, onUnmounted, ref } from 'vue';
import RoseChart from '@/views/practice/viz/RoseChart.vue';
import { wait4Element } from '@/utils/dom';
import BarChart from '@/views/practice/viz/BarChart.vue';
import PieChart from '@/views/practice/viz/PieChart.vue';
import LineChart from '@/views/practice/viz/LineChart.vue';
import RingChart from '@/views/practice/viz/RingChart.vue';
import MatrixChart from '@/views/practice/viz/MatrixChart.vue';
import { getDefaultTheme, setTheme } from '@/utils/theme';
import MapChart from '@/views/practice/viz/MapChart.vue';
import WordCloud from '@/views/practice/viz/WordCloud.vue';

const containerWidth = ref(0);

onMounted(() => {
    setTheme('dark');
    wait4Element('container').then(element => {
        containerWidth.value = (element as HTMLElement).offsetWidth;
        new ResizeObserver(() => {
            containerWidth.value = (element as HTMLElement).offsetWidth;
        }).observe(element as HTMLElement);
    });
});

onUnmounted(() => {
    setTheme(getDefaultTheme());
});
</script>

<template>
    <BaseLayout>
        <div class="flex-1 grid grid-cols-3 gap-8 grid-flow-row w-full" id="container">
            <div class="flex items-center justify-center">
                <pie-chart />
            </div>
            <div class="flex items-center justify-center">
                <funnel-chart :style="`height: ${(containerWidth / 3) * 0.5}px`" />
            </div>
            <div class="flex items-center justify-center">
                <rose-chart :style="`height: ${(containerWidth / 3) * 0.5}px`" />
            </div>
            <div class="flex items-center justify-center">
                <BarChart
                    :style="
                        `height: ${(containerWidth / 3) * 0.55}px;` +
                        `width: ${(containerWidth / 3) * 0.8}px`
                    "
                    v-if="containerWidth > 0" />
            </div>
            <div class="flex items-center justify-center">
                <line-chart
                    :style="
                        `height: ${(containerWidth / 3) * 0.6}px;` +
                        `width: ${(containerWidth / 3) * 0.8}px`
                    "
                    v-if="containerWidth > 0" />
            </div>
            <div class="flex items-center justify-center">
                <ring-chart
                    :style="
                        `height: ${(containerWidth / 3) * 0.6}px;` +
                        `width: ${(containerWidth / 3) * 0.9}px`
                    "
                    v-if="containerWidth > 0" />
            </div>
            <div class="flex items-center justify-center">
                <matrix-chart
                    :style="
                        `height: ${(containerWidth / 3) * 0.6}px;` +
                        `width: ${(containerWidth / 3) * 0.9}px`
                    "
                    v-if="containerWidth > 0" />
            </div>
            <div class="flex items-center justify-center">
                <map-chart
                    :style="`height: ${(containerWidth / 3) * 0.6}px;`"
                    v-if="containerWidth > 0" />
            </div>
            <div class="flex items-center justify-center">
                <word-cloud
                    :style="`height: ${(containerWidth / 3) * 0.6}px;`"
                    v-if="containerWidth > 0" />
            </div>
        </div>
    </BaseLayout>
</template>

<style scoped lang="scss"></style>
