<script lang="ts" setup>
import {onMounted, ref} from "vue";
import * as echarts from 'echarts';

import type {SeasonStats} from "@/types/kill";
import type {R} from "@/types/common";
import {GameMode, GameModeInfoMap} from "@/constants/GameConstant";
import axios from '@/axios/index';
import {API_KILL_STATS_SEASON} from "@/constants/ApiConstant";

const seasonStats = ref<SeasonStats>();

onMounted(async () => {
    let chartDom = document.getElementById('seasonStatsRadar')
    let myChart = echarts.init(chartDom, 'vintage', {
        renderer: 'svg'
    });

    const response: R<SeasonStats> = (await axios.get(API_KILL_STATS_SEASON)).data;
    seasonStats.value = response.data;

    const option: echarts.EChartsOption = {
        textStyle: {
            fontFamily: "'Songti SC', 'SimSun', 'Times New Roman'"
        },
        radar: {
            center: ['53%', '50%'], // 圆中心坐标，数组的第一项是横坐标，第二项是纵坐标。
            splitNumber: 5, // 雷达图圈数
            radius: 60, // 圆的半径
            axisNameGap: 8, // 指示器名称和指示器轴的距离
            axisName: { // 指示器名称
                formatter: '{value}',
            },
            indicator: [
                {name: GameModeInfoMap[GameMode.IDENTITY].name, max: 100},
                {name: GameModeInfoMap[GameMode.REVERT].name, max: 100},
                {name: GameModeInfoMap[GameMode.NATION].name, max: 100},
                {name: GameModeInfoMap[GameMode.SOLO].name, max: 100},
                {name: GameModeInfoMap[GameMode.DOUBLES].name, max: 100},
                {name: GameModeInfoMap[GameMode.TRIPLES].name, max: 100},
            ]
        },
        series: [
            {
                type: 'radar',
                data: [
                    {
                        value: [
                            seasonStats.value.identity,
                            seasonStats.value.revert,
                            seasonStats.value.nation,
                            seasonStats.value.solo,
                            seasonStats.value.doubles,
                            seasonStats.value.triples
                        ],
                    },
                ],
                areaStyle: {}
            }
        ]
    };

    myChart.setOption(option);
})

</script>

<template>
    <div>
        <div id="seasonStatsRadar" class="w-[12rem] h-[11rem]"/>
    </div>
</template>