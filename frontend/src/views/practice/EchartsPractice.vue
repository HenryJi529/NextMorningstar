<script lang="ts" setup>
import * as echarts from 'echarts';
import {onMounted} from "vue";
import axios from '@/axios/index';
import dayjs from "dayjs";

interface AData {
    dates: string[];
    values: number[];
}

const loadBarChart = async (data: AData) => {
    const chartDom = document.getElementById('bar-container');
    const myChart = echarts.init(chartDom, 'vintage', {
        renderer: 'canvas',
        useDirtyRect: false
    });

    const options: echarts.EChartsOption = {
        tooltip: { // 当鼠标悬停时，会显示一个浮层提示框
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: { // 图标离容器的距离
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: data.dates,
                axisTick: {
                    alignWithLabel: true
                },
                axisLabel: {
                    rotate: 15,
                }
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: 'Direct',
                type: 'bar',
                barWidth: '60%',
                data: data.values,
                showBackground: true,
                backgroundStyle: {
                    color: 'rgba(180, 180, 180, 0.2)'
                },
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#83bff6' },
                        { offset: 0.5, color: '#188df0' },
                        { offset: 1, color: '#188df0' }
                    ])
                },
                emphasis: {
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#2378f7' },
                            { offset: 0.7, color: '#2378f7' },
                            { offset: 1, color: '#83bff6' }
                        ])
                    }
                },
            }
        ]
    }

    myChart.setOption(options);

    window.addEventListener('resize', () => {
        myChart.resize();
    });
}

const loadCurveChart = async (data: AData) => {
    const chartDom = document.getElementById('curve-container');
    const myChart = echarts.init(chartDom, '', {
        renderer: 'svg',
        useDirtyRect: false
    });

    const options: echarts.EChartsOption = {
        tooltip: {
            trigger: 'item',
            axisPointer: {
                type: 'shadow'
            },
            formatter: (params) => {
                // @ts-ignore
                const date = params.data[0] as string;
                // @ts-ignore
                const value = params.data[1] as number;
                return `${dayjs(date, 'YYYY/MM/DD').format('YYYY年M月D日')}: ${Math.round(value*10000)/100}%`;
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'time',
                splitNumber: 10,
                axisLabel: {
                    rotate: 0,
                    formatter: (value) => {
                        return dayjs(value).format('YYYY/MM/DD');
                    }
                },
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                type: 'line',
                // time支持number, Date, string
                data: data.dates.map((date, i) => [dayjs(date).toDate().getTime(), data.values[i]])
            }
        ]
    }

    myChart.setOption(options);

    window.addEventListener('resize', () => {
        myChart.resize();
    });
}

onMounted(async () => {
    const beginDateTime = new Date();
    beginDateTime.setDate(1);
    const beginDate = beginDateTime.toISOString().split('T')[0];

    const data: AData = (
        await axios.get(`/api/practice/a?begin=${beginDate}`)
    ).data.data;

    loadBarChart(data).then();
    loadCurveChart(data).then();
})

</script>

<template>
    <div>
        <div id="bar-container" class="w-[32rem] h-[20rem] lg:w-[64rem] lg:h-[40rem]"></div>
        <div id="curve-container" class="w-[32rem] h-[20rem] lg:w-[64rem] lg:h-[40rem]"></div>
    </div>
</template>

<style lang="scss" scoped>

</style>
