<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import * as echarts from 'echarts';

const loadChart = (chartDom: HTMLDivElement) => {
    const myChart = echarts.init(chartDom, '', {
        renderer: 'svg',
        useDirtyRect: false,
    });
    const option: echarts.EChartsOption = {
        textStyle: {
            fontFamily: "'Songti SC', 'SimSun', 'Times New Roman'",
            fontSize: chartDom.offsetHeight * 0.06,
        },
        radar: {
            center: ['50%', '50%'], // 圆中心坐标，数组的第一项是横坐标，第二项是纵坐标。
            splitNumber: 5, // 雷达图圈数
            radius: chartDom.offsetHeight * 0.34, // 圆的半径
            axisNameGap: chartDom.offsetHeight * 0.05, // 指示器名称和指示器轴的距离
            axisName: {
                // 指示器名称
                formatter: '{value}',
            },
            indicator: [
                { name: '1V1', max: 100 },
                { name: '2V2', max: 100 },
                { name: '3V3', max: 100 },
                { name: '身份场', max: 100 },
                { name: '国战', max: 100 },
                { name: '斗地主', max: 100 },
            ],
        },
        series: [
            {
                type: 'radar',
                lineStyle: {
                    type: 'solid',
                    width: chartDom.offsetHeight * 0.005,
                },
                symbol: 'circle',
                symbolSize: chartDom.offsetHeight * 0.04,
                data: [
                    {
                        value: [85, 65, 55, 75, 70, 60],
                    },
                ],
                areaStyle: {},
            },
        ],
    };
    myChart.setOption(option);
};

const containerRef = ref<HTMLDivElement>();

let timer: number;
onUnmounted(() => {
    window.clearInterval(timer);
});

onMounted(async () => {
    const chartDom = containerRef.value as HTMLDivElement;
    loadChart(chartDom);
    new ResizeObserver(() => {
        const chart = echarts.getInstanceByDom(chartDom);
        chart?.setOption({
            textStyle: {
                fontSize: chartDom.offsetHeight * 0.06,
            },
            radar: {
                radius: chartDom.offsetHeight * 0.34,
                axisNameGap: chartDom.offsetHeight * 0.05,
            },
            series: [
                {
                    lineStyle: {
                        width: chartDom.offsetHeight * 0.005,
                    },
                    symbolSize: chartDom.offsetHeight * 0.04,
                },
            ],
        });
        chart?.resize();
    }).observe(chartDom);
});
</script>

<template>
    <div class="w-full h-full" ref="containerRef"></div>
</template>

<style scoped lang="scss"></style>
