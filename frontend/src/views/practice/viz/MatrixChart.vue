<script setup lang="ts">
import * as echarts from 'echarts';
import { onMounted, ref } from 'vue';
import ArrowIcon from '@/assets/img/practice/viz/matrix-chart/Arrow.png';
import LogoIcon from '@/assets/img/practice/viz/matrix-chart/Logo.png';
import StarIcon from '@/assets/img/practice/viz/matrix-chart/Star.svg';

const containerRef = ref<HTMLDivElement>();

const containerHeight = ref(0);
const containerWidth = ref(0);

const renderMatrixItem = (
    chartDom: HTMLDivElement,
    _: echarts.CustomSeriesRenderItemParams,
    api: echarts.CustomSeriesRenderItemAPI
) => {
    const map: Record<number, { name: string; color1: string; color2: string }> = {
        1: {
            name: '低风险',
            color1: '#275B27',
            color2: '#4AA73C',
        },
        2: {
            name: '中低风险',
            color1: '#3DAE2C',
            color2: '#CEFCCB',
        },
        3: {
            name: '中风险',
            color1: '#5CC6ED',
            color2: '#A9E0FF',
        },
        4: {
            name: '中高风险',
            color1: '#D06F20',
            color2: '#FEBA66',
        },
        5: {
            name: '高风险',
            color1: '#610505',
            color2: '#FA0C0C',
        },
    };

    const coords1 = api.coord([api.value(0), api.value(1)]);
    const coords2 = api.coord([Number(api.value(0)) + 20, Number(api.value(1)) + 20]);
    const info = map[Number(api.value(2))];

    return {
        type: 'group',
        children: [
            {
                type: 'rect',
                shape: {
                    x: coords2[0],
                    y: coords2[1],
                    width: Math.abs(coords1[0] - coords2[0]),
                    height: Math.abs(coords1[1] - coords2[1]),
                },
                style: {
                    fill: {
                        type: 'linear',
                        x: 0,
                        y: 1,
                        x2: 1,
                        y2: 0,
                        colorStops: [
                            { offset: 0, color: info?.color1 },
                            { offset: 1, color: info?.color2 },
                        ],
                    },
                },
            },
            {
                type: 'text',
                x: (coords1[0] + coords2[0]) / 2,
                y: (coords1[1] + coords2[1]) / 2,
                style: {
                    text: info.name,
                    fill: Number(api.value(2)) > 3 ? '#fff' : '#000',
                    textAlign: 'center',
                    textVerticalAlign: 'middle',
                    fontSize: chartDom.offsetHeight * 0.04,
                },
            },
        ],
    };
};

const loadChart = (chartDom: HTMLDivElement) => {
    const myChart = echarts.init(chartDom, '', {
        renderer: 'svg',
        useDirtyRect: false,
    });

    const option = {
        grid: {
            left: '24%',
            right: '15%',
            bottom: '26%',
            top: '3%',
            containLabel: false,
        },
        xAxis: {
            type: 'value',
            min: 0,
            max: 100,
            inverse: true,
            axisLine: {
                show: false,
            },
            axisTick: {
                show: false,
            },
            axisLabel: {
                show: true,
                fontSize: chartDom.offsetHeight * 0.04,
                color: 'white',
                formatter: (value: number) => {
                    const labelMap: Record<number, string> = {
                        0: '0',
                        20: '35',
                        40: '50',
                        60: '65',
                        80: '80',
                        100: '100',
                    };
                    return labelMap[value] !== undefined ? labelMap[value] : '';
                },
            },
            splitLine: {
                show: false,
            },
        },
        yAxis: {
            type: 'value',
            min: 0,
            max: 100,
            axisLine: {
                show: false,
            },
            axisTick: {
                show: false,
            },
            axisLabel: {
                show: true,
                fontSize: chartDom.offsetHeight * 0.04,
                color: 'white',
                formatter: (value: number) => {
                    const labelMap: Record<number, string> = {
                        0: '0',
                        20: '35',
                        40: '50',
                        60: '65',
                        80: '80',
                        100: '100',
                    };
                    return labelMap[value] !== undefined ? labelMap[value] : '';
                },
            },
            splitLine: {
                show: false,
            },
        },
        series: [
            {
                type: 'custom',
                renderItem: (
                    _: echarts.CustomSeriesRenderItemParams,
                    api: echarts.CustomSeriesRenderItemAPI
                ) => {
                    return renderMatrixItem(chartDom, _, api);
                },
                data: [
                    [0, 0, 3],
                    [20, 0, 3],
                    [40, 0, 2],
                    [60, 0, 2],
                    [80, 0, 1],
                    [0, 20, 4],
                    [20, 20, 3],
                    [40, 20, 3],
                    [60, 20, 2],
                    [80, 20, 2],
                    [0, 40, 4],
                    [20, 40, 4],
                    [40, 40, 3],
                    [60, 40, 3],
                    [80, 40, 2],
                    [0, 60, 5],
                    [20, 60, 4],
                    [40, 60, 4],
                    [60, 60, 3],
                    [80, 60, 3],
                    [0, 80, 5],
                    [20, 80, 5],
                    [40, 80, 4],
                    [60, 80, 4],
                    [80, 80, 3],
                ],
            },
            {
                type: 'scatter',
                symbolSize: chartDom.offsetHeight * 0.06,
                symbol: `image://${ArrowIcon}`,
                data: [
                    {
                        value: [80, 60],
                        itemStyle: {
                            opacity: 1,
                        },
                    },
                    {
                        value: [60, 40],
                        itemStyle: {
                            opacity: 1,
                        },
                    },
                    {
                        value: [40, 20],
                        itemStyle: {
                            opacity: 1,
                        },
                    },
                ],
            },
            {
                type: 'scatter',
                symbolSize: chartDom.offsetHeight * 0.06,
                data: [
                    {
                        value: [65, 55],
                        symbol: `image://${LogoIcon}`,
                        itemStyle: {
                            opacity: 1,
                        },
                    },
                    {
                        value: [45, 55],
                        symbol: `image://${StarIcon}`,
                        itemStyle: {
                            opacity: 1,
                        },
                    },
                ],
            },
            {
                type: 'line',
                data: [
                    [100, 100],
                    [0, 0],
                ],
                lineStyle: {
                    type: 'dashed',
                    width: 2,
                    color: '#93A6AF',
                },
                showSymbol: false,
            },
        ],
    };
    myChart.setOption(option);

    let isShow = true;
    const visiblePeriod = 1000;
    const invisiblePeriod = 200;
    const toggleSeries = () => {
        isShow = !isShow;
        myChart.setOption({
            series: [{}, { data: isShow ? option.series[1].data : [] }, {}],
        });
        window.clearInterval(timer);
        timer = window.setInterval(toggleSeries, isShow ? visiblePeriod : invisiblePeriod);
    };
    let timer = window.setInterval(toggleSeries, isShow ? visiblePeriod : invisiblePeriod);
};

onMounted(() => {
    const chartDom = containerRef.value as HTMLDivElement;
    containerHeight.value = chartDom.offsetHeight;
    containerWidth.value = chartDom.offsetWidth;
    loadChart(chartDom);
    new ResizeObserver(() => {
        containerHeight.value = chartDom.offsetHeight;
        containerWidth.value = chartDom.offsetWidth;
        const chart = echarts.getInstanceByDom(chartDom);
        chart?.setOption({
            xAxis: {
                axisLabel: {
                    fontSize: chartDom.offsetHeight * 0.04,
                },
            },
            yAxis: {
                axisLabel: {
                    fontSize: chartDom.offsetHeight * 0.04,
                },
            },
            series: [
                {},
                {
                    symbolSize: chartDom.offsetHeight * 0.06,
                },
                {
                    symbolSize: chartDom.offsetHeight * 0.06,
                },
                {},
            ],
        });
        chart?.resize();
    }).observe(chartDom);
});
</script>

<template>
    <div class="h-full w-full relative flex justify-center items-center">
        <div ref="containerRef" class="w-full h-full"></div>
        <div class="absolute left-0 flex h-full flex-col w-[24%]">
            <div class="h-[3%]"></div>
            <div class="h-[71%] w-full flex items-center">
                <div
                    class="vertical-text text-center font-bold text-white"
                    :style="`letter-spacing: ${containerWidth * 0.01}px; font-size: ${containerWidth * 0.03}px;`">
                    固有风险
                </div>
                <div class="h-full">
                    <svg height="100%" viewBox="0 0 8 200" xmlns="http://www.w3.org/2000/svg">
                        <!-- 中间竖线：从上箭头底部到下箭头顶部 -->
                        <line
                            x1="4"
                            y1="10"
                            x2="4"
                            y2="190"
                            stroke="white"
                            stroke-width="1"
                            stroke-opacity="0.5" />
                        <!-- 上箭头：位于顶部，向下指 -->
                        <path d="M4 0 L0 10 L8 10 Z" fill="white" />
                        <!-- 下箭头：位于底部，向上指 -->
                        <path d="M4 200 L0 190 L8 190 Z" fill="white" />
                    </svg>
                </div>
                <div
                    class="h-full flex flex-col items-end justify-around text-white whitespace-nowrap"
                    :style="`font-size: ${containerWidth * 0.025}px;`">
                    <div>高风险</div>
                    <div>较高风险</div>
                    <div>中风险</div>
                    <div>中低风险</div>
                    <div>低风险</div>
                </div>
            </div>
            <div class="h-[26%]"></div>
        </div>
        <div class="absolute bottom-0 w-full flex h-[15%]">
            <div class="w-[24%]"></div>
            <div class="w-[61%] flex flex-col justify-end">
                <div class="w-full">
                    <svg width="100%" viewBox="0 0 200 8" xmlns="http://www.w3.org/2000/svg">
                        <!-- 中间横线 -->
                        <line
                            x1="8"
                            y1="4"
                            x2="192"
                            y2="4"
                            stroke="white"
                            stroke-width="1"
                            stroke-opacity="0.6" />
                        <!-- 左箭头（←） -->
                        <path d="M0 4 L8 0 L8 8 Z" fill="white" />
                        <!-- 右箭头（→） -->
                        <path d="M200 4 L192 0 L192 8 Z" fill="white" />
                    </svg>
                </div>
                <div
                    class="flex items-center justify-around text-white"
                    :style="`font-size: ${containerWidth * 0.025}px;`">
                    <div class="w-[4em] text-center">非常有效</div>
                    <div class="w-[4em] text-center">较有效</div>
                    <div class="w-[4em] text-center">一般有效</div>
                    <div class="w-[4em] text-center">低效</div>
                    <div class="w-[4em] text-center">无效</div>
                </div>
                <div
                    class="text-center font-bold text-white"
                    :style="`letter-spacing: ${containerWidth * 0.01}px; font-size: ${containerWidth * 0.03}px;`">
                    控制有效性
                </div>
            </div>
            <div class="w-[15%]"></div>
        </div>
        <div class="absolute right-0 flex flex-col h-full">
            <div class="h-[3%]"></div>
            <div class="h-[71%] w-full flex items-start">
                <div
                    class="flex flex-col items-center"
                    :style="`margin-right: ${containerWidth * 0.03}px`">
                    <img
                        :src="StarIcon"
                        alt="2021"
                        :style="
                            `height: ${containerHeight * 0.07}px;` +
                            `margin-bottom: ${containerHeight * 0.03}px`
                        " />
                    <div
                        class="vertical-text text-white"
                        :style="`letter-spacing: ${containerHeight * 0.01}px; font-size: ${containerHeight * 0.043}px;`">
                        2021年整体剩余风险
                    </div>
                </div>
                <div class="flex flex-col items-center">
                    <img
                        :src="LogoIcon"
                        alt="2021"
                        :style="
                            `height: ${containerHeight * 0.07}px;` +
                            `margin-bottom: ${containerHeight * 0.03}px`
                        " />
                    <div
                        class="vertical-text text-white"
                        :style="`letter-spacing: ${containerHeight * 0.01}px; font-size: ${containerHeight * 0.043}px;`">
                        2024年整体剩余风险
                    </div>
                </div>
            </div>
            <div class="h-[26%]"></div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.vertical-text {
    text-orientation: upright;
    writing-mode: vertical-rl;
}
</style>
