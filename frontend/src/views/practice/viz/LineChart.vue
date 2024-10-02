<script setup lang="ts">

import * as echarts from "echarts";
import {onMounted, ref} from "vue";

const containerRef = ref<HTMLDivElement>();

const loadChart = (chartDom: HTMLDivElement) => {
    const myChart = echarts.init(chartDom, '', {
        renderer: 'svg',
        useDirtyRect: false
    });

    const data2021 = [66.80, 56.80, 52.39, 62.44];
    const data2024 = [73.80, 62.77, 58.75, 63.43];

    const option = {
        grid: {
            top: '20%',
            left: '5%',
            right: '5%',
            bottom: '15%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: ['地域环境', '客户群体', '产品业务', '渠道'],
            axisLabel: {
                color: 'white',
                fontSize: chartDom.offsetHeight * 0.045,
                interval: 0, // 👈 关键！强制显示所有标签
                rotate: 0,
                overflow: 'truncate', // 或 'break' / 'none'
            }
        },
        yAxis: {
            type: 'value',
            min: 0,
            max: 40,
            interval: 10,
            splitLine: {
                show: true,
                lineStyle: {
                    color: 'rgba(255,255,255,0.2)',
                    type: 'dashed',
                    width: 1
                },
            },
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                show: true,
                fontSize: chartDom.offsetHeight * 0.045,
                color: 'white',
                formatter: (value: number) => {
                    const labelMap: Record<number, string> = {
                        0: '0',
                        10: '50',
                        20: '60',
                        30: '70',
                        40: '80'
                    };
                    return labelMap[value] !== undefined ? labelMap[value] : '';
                }
            },
        },
        legend: {
            show: true,
            type: 'plain', // 图例类型：'plain'普通，'scroll'可滚动
            top: '5%',
            right: '5%',
            orient: 'horizontal',
            itemHeight: chartDom.offsetHeight * 0.035,
            itemWidth: chartDom.offsetWidth * 0.06,
            textStyle: {
                color: 'white',
                fontSize: chartDom.offsetHeight * 0.045,
            },
            data: ['2021年','2024年']
        },
        series: [
            {
                name: '2021年',
                data: data2021.map((item) => item-40),
                type: 'line',
                lineStyle: {
                    color: '#1875DF',
                    width: chartDom.offsetHeight * 0.01,
                },
                label: {
                    show: true,
                    color: 'white',
                    position: 'bottom',
                    fontSize: chartDom.offsetHeight * 0.04,
                    // @ts-ignore
                    formatter: (params) => {
                        return (Math.round((params.value+40)*100)/100).toFixed(2);
                    }
                },
                symbol: 'circle',
                symbolSize: chartDom.offsetHeight * 0.035,
                itemStyle: {
                    color: '#1978E5',
                    borderWidth: chartDom.offsetHeight * 0.005,
                    borderColor: '#0E3F78'
                },
            },
            {
                name: '2024年',
                data: data2024.map((item) => item-40),
                type: 'line',
                lineStyle: {
                    color: '#01C6ED',
                    width: chartDom.offsetHeight * 0.01,
                },
                label: {
                    show: true,
                    color: 'white',
                    position: 'top',
                    fontSize: chartDom.offsetHeight * 0.04,
                    // @ts-ignore
                    formatter: (params) => {
                        return (Math.round((params.value+40)*100)/100).toFixed(2);
                    }
                },
                symbol: 'circle',
                symbolSize: chartDom.offsetHeight * 0.035,
                itemStyle: {
                    color: '#00D5FF',
                    borderWidth: chartDom.offsetHeight * 0.005,
                    borderColor: '#057897'
                }
            }
        ]
    };

    myChart.setOption(option);
}

onMounted(()=>{
    const chartDom = containerRef.value as HTMLDivElement;
    loadChart(chartDom);
    new ResizeObserver(()=>{
        const chart = echarts.getInstanceByDom(chartDom);
        chart?.setOption({
            xAxis: {
                axisLabel: {
                    fontSize: chartDom.offsetHeight * 0.045,
                }
            },
            yAxis: {
                axisLabel: {
                    fontSize: chartDom.offsetHeight * 0.045,
                }
            },
            legend: {
                itemHeight: chartDom.offsetHeight * 0.035,
                itemWidth: chartDom.offsetWidth * 0.06,
                textStyle: {
                    fontSize: chartDom.offsetHeight * 0.045,
                },
            },
            series: [
                {
                    label: {
                        fontSize: chartDom.offsetHeight * 0.04,
                    },
                    symbolSize: chartDom.offsetHeight * 0.035,
                    itemStyle: {
                        borderWidth: chartDom.offsetHeight * 0.005,
                    },
                    lineStyle: {
                        width: chartDom.offsetHeight * 0.01,
                    }
                },
                {
                    label: {
                        fontSize: chartDom.offsetHeight * 0.04,
                    },
                    symbolSize: chartDom.offsetHeight * 0.035,
                    itemStyle: {
                        borderWidth: chartDom.offsetHeight * 0.005,
                    },
                    lineStyle: {
                        width: chartDom.offsetHeight * 0.01,
                    }
                }
            ]
        })
        chart?.resize();
    }).observe(chartDom);
})
</script>

<template>
    <div class="w-full h-full" ref="containerRef"></div>
</template>

<style scoped lang="scss">

</style>