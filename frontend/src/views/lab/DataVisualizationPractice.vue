<script setup lang="ts">
import BaseLayout from '@/views/lab/BaseLayout.vue';
import FunnelChart from '@/views/lab/viz/FunnelChart.vue';
import { onMounted, onUnmounted, ref } from 'vue';
import RoseChart from '@/views/lab/viz/RoseChart.vue';
import { wait4Element } from '@/utils/dom';
import BarChart from '@/views/lab/viz/BarChart.vue';
import PieChart from '@/views/lab/viz/PieChart.vue';
import LineChart from '@/views/lab/viz/LineChart.vue';
import RingChart from '@/views/lab/viz/RingChart.vue';
import MatrixChart from '@/views/lab/viz/MatrixChart.vue';
import { getDefaultTheme, setTheme } from '@/utils/theme';
import MapChart from '@/views/lab/viz/MapChart.vue';
import WordCloud from '@/views/lab/viz/WordCloud.vue';
import RadarChart from '@/views/lab/viz/RadarChart.vue';

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
            <div class="flex items-center justify-center">
                <radar-chart
                    :style="`height: ${(containerWidth / 3) * 0.6}px;`"
                    v-if="containerWidth > 0" />
            </div>
        </div>
    </BaseLayout>
</template>

<style scoped lang="scss"></style>
