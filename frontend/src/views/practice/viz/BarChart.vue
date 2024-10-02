<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import * as echarts from 'echarts';
import Rank18ImageUrl from '@/assets/img/practice/viz/bar-chart/Rank18.png';
import Rank8ImageUrl from '@/assets/img/practice/viz/bar-chart/Rank8.png';
import Rank6ImageUrl from '@/assets/img/practice/viz/bar-chart/Rank6.png';
import Rank3ImageUrl from '@/assets/img/practice/viz/bar-chart/Rank3.png';
import Rank2ImageUrl from '@/assets/img/practice/viz/bar-chart/Rank2.png';

let timer: number;
onUnmounted(() => {
    window.clearInterval(timer);
});

const containerRef = ref<HTMLDivElement>();

const loadChart = (chartDom: HTMLElement) => {
    const myChart = echarts.init(chartDom, '', {
        renderer: 'svg',
        useDirtyRect: false,
    });

    const barWidth = chartDom.offsetWidth * 0.03;
    const barGap = '20%';
    const rankSizeRatio = 0.08;

    const getFullRankData = () => {
        return [
            {
                value: 50,
                label: {
                    show: true,
                    formatter: '{icon|}',
                    rich: {
                        icon: {
                            height: chartDom.offsetHeight * rankSizeRatio,
                            width: chartDom.offsetWidth * rankSizeRatio,
                            backgroundColor: {
                                image: Rank18ImageUrl,
                            },
                        },
                    },
                },
            },
            {
                value: 110,
                label: {
                    show: true,
                    formatter: '{icon|}',
                    rich: {
                        icon: {
                            height: chartDom.offsetHeight * rankSizeRatio,
                            width: chartDom.offsetWidth * rankSizeRatio,
                            backgroundColor: {
                                image: Rank8ImageUrl,
                            },
                        },
                    },
                },
            },
            {
                value: 110,
                label: {
                    show: true,
                    formatter: '{icon|}',
                    rich: {
                        icon: {
                            height: chartDom.offsetHeight * rankSizeRatio,
                            width: chartDom.offsetWidth * rankSizeRatio,
                            backgroundColor: {
                                image: Rank8ImageUrl,
                            },
                        },
                    },
                },
            },
            {
                value: 140,
                label: {
                    show: true,
                    formatter: '{icon|}',
                    rich: {
                        icon: {
                            height: chartDom.offsetHeight * rankSizeRatio,
                            width: chartDom.offsetWidth * rankSizeRatio,
                            backgroundColor: {
                                image: Rank6ImageUrl,
                            },
                        },
                    },
                },
            },
            {
                value: 172,
                label: {
                    show: true,
                    formatter: '{icon|}',
                    rich: {
                        icon: {
                            height: chartDom.offsetHeight * rankSizeRatio,
                            width: chartDom.offsetWidth * rankSizeRatio,
                            backgroundColor: {
                                image: Rank3ImageUrl,
                            },
                        },
                    },
                },
            },
            {
                value: 185,
                label: {
                    show: true,
                    formatter: '{icon|}',
                    rich: {
                        icon: {
                            height: chartDom.offsetHeight * rankSizeRatio,
                            width: chartDom.offsetWidth * rankSizeRatio,
                            backgroundColor: {
                                image: Rank2ImageUrl,
                            },
                        },
                    },
                },
            },
        ];
    };

    let currentRankData: object[] = [];
    let currentRankIndex = 0;
    const step = 800;
    const duration = 8000;

    const option = {
        tooltip: {
            trigger: 'axis',
            showContent: false,
            axisPointer: {
                type: 'shadow',
            },
        },
        grid: {
            top: '20%',
            left: '2%',
            right: '5%',
            bottom: '10%',
            containLabel: true,
        },
        xAxis: {
            type: 'category',
            data: ['2020', '2021', '2022', '2023', '2024', '2025'],
            axisLabel: {
                color: 'white',
                fontSize: chartDom.offsetHeight * 0.045,
                interval: 0, // 👈 关键！强制显示所有标签
                rotate: 0,
                overflow: 'truncate', // 或 'break' / 'none'
            },
        },
        yAxis: {
            type: 'value',
            min: 0,
            max: 200,
            interval: 50,
            axisLabel: {
                show: true,
                fontSize: chartDom.offsetHeight * 0.045,
                color: 'white',
            },
            splitLine: {
                show: true,
                lineStyle: {
                    color: 'rgba(255,255,255,0.2)',
                    type: 'dashed',
                    width: 1,
                },
            },
        },
        legend: {
            show: true,
            type: 'plain', // 图例类型：'plain'普通，'scroll'可滚动
            top: '1%',
            right: '5%',
            orient: 'horizontal',
            itemHeight: chartDom.offsetHeight * 0.035,
            itemWidth: chartDom.offsetWidth * 0.02,
            itemGap: chartDom.offsetWidth * 0.04,
            icon: 'circle',
            textStyle: {
                color: 'white',
                fontSize: chartDom.offsetHeight * 0.045,
            },
            data: ['字段一', '字段二', '字段三', '字段四(排名)'],
        },
        series: [
            {
                name: '字段一',
                data: [13, 26, 46, 113, 123, 50],
                type: 'bar',
                barWidth: barWidth,
                barGap: barGap,
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#1A5FAB' },
                        { offset: 0.5, color: '#1C4B7E' },
                        { offset: 1, color: '#1E3249' },
                    ]),
                },
                label: {
                    show: true,
                    color: 'white',
                    position: 'top',
                    fontSize: chartDom.offsetHeight * 0.04,
                    // @ts-expect-error ECharts参数类型缺失
                    formatter: params => {
                        return params.value;
                    },
                },
            },
            {
                name: '字段二',
                data: [1, 5, 5, 16, 46, 41],
                type: 'bar',
                barWidth: barWidth,
                barGap: barGap,
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#1695B3' },
                        { offset: 0.5, color: '#1A6F88' },
                        { offset: 1, color: '#1E3245' },
                    ]),
                },
                label: {
                    show: true,
                    color: 'white',
                    position: 'top',
                    fontSize: chartDom.offsetHeight * 0.04,
                    // @ts-expect-error ECharts参数类型缺失
                    formatter: params => {
                        return params.value;
                    },
                },
            },
            {
                name: '字段三',
                data: [0, 0, 1, 3, 11, 12],
                type: 'bar',
                barWidth: barWidth,
                barGap: barGap,
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#A62C23' },
                        { offset: 0.5, color: '#6E292E' },
                        { offset: 1, color: '#1F3042' },
                    ]),
                },
                label: {
                    show: true,
                    color: 'white',
                    position: 'top',
                    fontSize: chartDom.offsetHeight * 0.04,
                    // @ts-expect-error ECharts参数类型缺失
                    formatter: params => {
                        return params.value;
                    },
                },
            },
            {
                name: '字段四(排名)',
                type: 'line',
                data: currentRankData,
                lineStyle: {
                    type: 'dashed',
                    color: '#FEDC81',
                    width: chartDom.offsetHeight * 0.01,
                },
                symbol: 'circle',
                symbolSize: chartDom.offsetHeight * 0.04,
                itemStyle: {
                    color: '#FEDC81',
                    borderWidth: chartDom.offsetHeight * 0.007,
                    borderColor: '#8E845A',
                },
            },
        ],
    };

    myChart.setOption(option);

    const clearRankData = () => {
        currentRankData = [];
        currentRankIndex = 0;

        myChart.setOption({
            series: [
                {},
                {},
                {},
                {
                    data: currentRankData,
                },
            ],
        });
    };

    const loadRankData = () => {
        const timer = window.setInterval(() => {
            if (currentRankIndex >= getFullRankData().length) {
                window.clearInterval(timer);
            } else {
                currentRankData = getFullRankData().slice(0, currentRankIndex + 1);

                myChart.setOption({
                    series: [
                        {},
                        {},
                        {},
                        {
                            data: currentRankData,
                        },
                    ],
                });
                currentRankIndex++;
            }
        }, step);
    };

    loadRankData();
    timer = window.setInterval(() => {
        clearRankData();
        loadRankData();
    }, duration);
};

onMounted(() => {
    const chartDom = containerRef.value as HTMLDivElement;
    loadChart(chartDom);
    new ResizeObserver(() => {
        const chart = echarts.getInstanceByDom(chartDom);
        const barWidth = chartDom.offsetWidth * 0.03;
        chart?.setOption({
            xAxis: {
                axisLabel: {
                    fontSize: chartDom.offsetHeight * 0.045,
                },
            },
            yAxis: {
                axisLabel: {
                    fontSize: chartDom.offsetHeight * 0.045,
                },
            },
            legend: {
                itemHeight: chartDom.offsetHeight * 0.035,
                itemWidth: chartDom.offsetWidth * 0.02,
                itemGap: chartDom.offsetWidth * 0.04,
                textStyle: {
                    fontSize: chartDom.offsetHeight * 0.045,
                },
            },
            series: [
                {
                    barWidth: barWidth,
                    label: {
                        fontSize: chartDom.offsetHeight * 0.04,
                    },
                },
                {
                    barWidth: barWidth,
                    label: {
                        fontSize: chartDom.offsetHeight * 0.04,
                    },
                },
                {
                    barWidth: barWidth,
                    label: {
                        fontSize: chartDom.offsetHeight * 0.04,
                    },
                },
                {
                    symbolSize: chartDom.offsetHeight * 0.04,
                    itemStyle: {
                        borderWidth: chartDom.offsetHeight * 0.007,
                    },
                    lineStyle: {
                        width: chartDom.offsetHeight * 0.01,
                    },
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
