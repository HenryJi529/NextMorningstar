<script setup lang="ts">
import * as echarts from 'echarts';
import {onMounted} from "vue";
import axios from "axios";

interface AData{
    dates: string[];
    values: number[];
}

onMounted(async () => {
    const beginDateTime = new Date();
    beginDateTime.setDate(1);
    const beginDate = beginDateTime.toISOString().split('T')[0];
    console.log(beginDate);

    const data: AData = (
        await axios.get(`/api/practice/a?begin=${beginDate}`)
    ).data.data;
    console.log(data)
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
                }
            }
        ]
    }

    myChart.setOption(options);

    window.addEventListener('resize', () => {
        myChart.resize();
    });
})

</script>

<template>
    <div>
        <div id="bar-container" class="w-[32rem] h-[20rem] lg:w-[64rem] lg:h-[40rem]"></div>
    </div>
</template>

<style scoped lang="scss">

</style>
