<script setup lang="ts">

import {onMounted, ref, watch} from "vue";
import * as echarts from 'echarts';
import * as turf from '@turf/turf';
import {message} from "ant-design-vue";
import GeoJSON from '@/assets/json/practice/viz/map/bankBranchMap.json'


interface Datum {
    name: string;
    value: number;
}

const data = ref<Datum[]>();

const containerRef = ref<HTMLDivElement>();

const fetchData = () => {
    setTimeout(()=>{
        data.value = [
            { name: '北京', value: 16 },
            { name: '上海', value: 20 },
            { name: '杭州', value: 17 },
            { name: '南京', value: 96 },
            { name: '镇江', value: 11 },
            { name: '扬州', value: 9 },
            { name: '苏州', value: 17 },
            { name: '无锡', value: 18 },
            { name: '盐城', value: 12 },
            { name: '连云港', value: 6 },
            { name: '常州', value: 13 },
            { name: '淮安', value: 7 },
            { name: '南通', value: 17 },
            { name: '徐州', value: 12 },
            { name: '泰州', value: 12 },
            { name: '宿迁', value: 8 },
            { name: '江北新区', value: 1 },
        ];
    }, 500)
}

const registerMap = () => {
    return new Promise((resolve) => {
        // NOTE: 模拟GeoJSON较大，动态请求GeoJSON的情况
        setTimeout(() => {
            /* 合并浦口区和六合区为江北新区 */
            const feature_JB_PK = GeoJSON.features.find((feature) => {
                return feature.properties.name === '江北新区-浦口区'
            });
            const feature_JB_LH = GeoJSON.features.find((feature) => {
                return feature.properties.name === '江北新区-六合区'
            });
            const feature_JB = turf.union(turf.featureCollection(
                [
                    turf.multiPolygon([feature_JB_LH?.geometry.coordinates] as number[][][][]),
                    turf.multiPolygon(feature_JB_PK?.geometry.coordinates as number[][][][])
                ])
            );
            // @ts-ignore
            feature_JB.properties.name = '江北新区';
            GeoJSON.features = GeoJSON.features.filter(feature => {
                return !feature.properties.name.startsWith('江北新区')
            })
            // @ts-ignore
            GeoJSON.features.push(feature_JB);

            /* 从南京的GeoJSON中扣除江北新区的部分 */
            const feature_NJ_geo = GeoJSON.features.find((feature) => {
                return feature.properties.name === '南京'
            })
            GeoJSON.features = GeoJSON.features.filter(feature => {
                return feature.properties.name !== '南京'
            })
            const feature_NJ_admin = turf.difference(
                // @ts-ignore
                turf.featureCollection([feature_NJ_geo, feature_JB])
            )
            // @ts-ignore
            GeoJSON.features.push(feature_NJ_admin);

            /* 注册地图 */
            // @ts-ignore
            echarts.registerMap('bankBranchMap', GeoJSON, {
                '北京': {
                    top: 34,
                    left: 121,
                    width: 1.5,
                },
                '杭州': {
                    top: 30.5,
                    left: 117,
                    width: 1.5,
                },
                '上海': {
                    top: 30.5,
                    left: 121.5,
                    width: 1,
                },
            })
            resolve('registered')
        }, 200)
    })

}

const defaultSelect = (chartDom: HTMLDivElement) => {
    const mapChart = echarts.getInstanceByDom(chartDom);
    if(!mapChart){
        setTimeout(() => defaultSelect(chartDom), 100)
    } else {
        mapChart.dispatchAction({
            type: 'select',
            seriesIndex: 0,
            name: '南京'
        });
    }
}

const loadChart = (chartDom: HTMLDivElement) => {
    const myChart = echarts.init(chartDom, '', {
        renderer: 'svg',
        useDirtyRect: false
    });

    const option = {
        tooltip: {
            trigger: 'item',
            showDelay: 0,
            transitionDuration: 0.2,
            backgroundColor: 'rgba(50, 50, 50, 0.7)',
            borderColor: '#333',
            borderWidth: 1,
            padding: [8, 12],
            textStyle: {
                color: '#fff',
                fontSize: 14,
                lineHeight: 20
            },
            // @ts-ignore
            formatter: (params) => {
                return '客户数:' + params.data.value?.toLocaleString('zh-CN');
            }
        },
        series: [
            {
                name: '南京银行网点分布图',
                type: 'map',
                roam: false,
                zoom: 1.15,
                map: 'bankBranchMap',
                selectedMode: 'single',
                label: {
                    show: true,
                    fontSize: chartDom.offsetHeight * 0.03,
                    color: 'white',
                    fontWeight: 'normal'
                },
                itemStyle: {
                    areaColor: '#87B8F0',
                    borderColor: '#67D4FF',
                    borderWidth: chartDom.offsetHeight * 0.004,
                },
                select: {
                    disabled: false,
                    label: {
                        show: true,
                        color: '#fff',
                        fontWeight: 'bold',
                    },
                    itemStyle: {
                        areaColor: '#D6AF71',
                        // borderColor: '#333',
                        // shadowColor: 'rgba(0, 0, 0, 0.5)',
                        // shadowBlur: 10,
                        // shadowOffsetX: 0,
                        // shadowOffsetY: 0
                    },
                },
                emphasis: {
                    label: {
                        show: true,
                        color: '#fff',
                    },
                    itemStyle: {
                        areaColor: '#f19696'
                    }
                },
            }
        ]
    };

    myChart.setOption(option);

    myChart.on('mouseover', (params) => {
        if (params.seriesType === 'map' && params.data) {
            document.body.style.cursor = 'pointer';
        }
    });

    myChart.on('mouseout', () => {
        document.body.style.cursor = 'default';
    });

    myChart.on('click', (params) => {
        if (params.seriesType === 'map' && params.data) {
            document.body.style.cursor = 'pointer';
            setTimeout(() => {
                document.body.style.cursor = 'default';
            }, 200);
        }
    });

    myChart.on('click', function(params) {
        if (params.seriesType === 'map' && params.data) {
            message.info(`点击了${params.name}分行`)
        }
    });
}

const updateChart = (chartDom: HTMLDivElement) => {
    const mapChart = echarts.getInstanceByDom(chartDom);
    if(!mapChart){
        setTimeout(() => updateChart(chartDom), 100)
    } else {
        const BRANCH_NAME_COLOR_MAP: Record<string, string> = {
            "苏州": "#2DA3D1",
            "无锡": "#2574B7",
            "北京": "#2EA2D2",
            "镇江": "#2F99C9",
            "宿迁": "#2F99CB",
            "江北新区": "#2573B5",
            "徐州": "#2573B5",
            "淮安": "#2064AD",
            "常州": "#2163AD",
            "扬州": "#2BB8E6",
            "南通": "#2F99CB",
            "泰州": "#29CCF7",
            "南京": "#2573B5",
            "连云港": "#2DA3D1",
            "盐城": "#2574B7",
            "上海": "#2672B4",
            "杭州": "#2D6EB6",
        };
        const mapData = Object.keys(BRANCH_NAME_COLOR_MAP).map((branchName) => {
            return {
                name: branchName,
                value: data.value?.find((item) => item.name === branchName)?.value,
                itemStyle: {areaColor: BRANCH_NAME_COLOR_MAP[branchName]}
            }
        });
        mapChart.setOption({
            // visualMap: {
            //     show: true,
            //     min: Math.min(...mapData.map(item => item?.value || 0)),
            //     max: Math.max(...mapData.map(item => item?.value || 0)),
            //     inRange: {
            //         color: ['#26CEF5', '#2BB8E5', '#2F99CB', '#2573B5']
            //     },
            //     calculable: true
            // },
            series: [
                {
                    data: mapData
                }
            ]
        })
    }
}

onMounted(()=>{
    fetchData();

    registerMap().then(()=>{
        const chartDom = containerRef.value as HTMLDivElement;
        loadChart(chartDom);
        defaultSelect(chartDom);
        new ResizeObserver(()=>{
            const chart = echarts.getInstanceByDom(chartDom);
            chart?.setOption({
                series: [
                    {
                        label: {
                            fontSize: chartDom.offsetHeight * 0.03,
                        },
                        itemStyle: {
                            borderWidth: chartDom.offsetHeight * 0.004,
                        },
                    }
                ]
            });
            chart?.resize();
        }).observe(chartDom);
    })
})

watch(data, ()=>{
    updateChart(containerRef.value as HTMLDivElement);
})

</script>

<template>
    <div ref="containerRef" class="w-full h-full"></div>
</template>

<style scoped lang="scss">

</style>