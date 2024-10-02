<script setup lang="ts">
import 'echarts-wordcloud';
import * as echarts from 'echarts';
import flowerLogo from '@/assets/img/practice/viz/word-cloud/flower.png';
import { onMounted, ref } from 'vue';
import { message } from 'ant-design-vue';

const data = [
    {
        name: '海底捞',
        fullName: '海底捞火锅',
        value: 3,
    },
    {
        name: '京旗',
        fullName: '京旗铜锅涮肉',
        value: 3,
    },
    {
        name: '潮界',
        fullName: '潮界',
        value: 2,
    },
    {
        name: '活泼先生',
        fullName: '活泼先生•顺德粥底火锅',
        value: 1,
    },
    {
        name: '牛New',
        fullName: '牛New寿喜烧',
        value: 4,
    },
    {
        name: '山石榴',
        fullName: '山石榴•贵州菜',
        value: 1,
    },
    {
        name: 'NEED',
        fullName: 'NEED创意韩国料理',
        value: 1,
    },
    {
        name: '左庭右院',
        fullName: '左庭右院鲜牛肉火锅',
        value: 1,
    },
    {
        name: '五里关',
        fullName: '五里关火锅',
        value: 1,
    },
    {
        name: '陋室铭',
        fullName: '陋室铭•板前烤肉饭',
        value: 1,
    },
    {
        name: '夏提乐客',
        fullName: '夏提乐客•新疆菜•烧烤',
        value: 1,
    },
    {
        name: '满陇山房',
        fullName: '满陇山房•膏蟹年糕馆',
        value: 3,
    },
    {
        name: '山野板扎',
        fullName: '山野板扎•云贵川Bistro',
        value: 1,
    },
    {
        name: '琉璃兽',
        fullName: '琉璃兽•黑牛烤肉',
        value: 1,
    },
    {
        name: '傅厨',
        fullName: '傅厨•岭南牛杂',
        value: 1,
    },
    {
        name: '西塔老太太',
        fullName: '西塔老太太泥炉烤肉',
        value: 2,
    },
    {
        name: '新发现',
        fullName: '新发现',
        value: 1,
    },
    {
        name: '七欣天',
        fullName: '七欣天香辣蟹火锅',
        value: 1,
    },
    {
        name: '咖喱快点',
        fullName: '咖喱快点CURRYON',
        value: 1,
    },
    {
        name: '喜牛锅',
        fullName: '喜牛锅•和牛寿喜烧放题',
        value: 2,
    },
    {
        name: '炭之物语',
        fullName: '炭之物语石板烤肉',
        value: 1,
    },
    {
        name: '南山翁',
        fullName: '南山翁新概念川菜',
        value: 1,
    },
    {
        name: '永安鱼庄',
        fullName: '永安鱼庄•镇江菜',
        value: 1,
    },
    {
        name: '东盛',
        fullName: '东盛炭烤自助料理',
        value: 1,
    },
    {
        name: '四道菜',
        fullName: '四道菜•福建菜馆',
        value: 1,
    },
    {
        name: '宁家十职',
        fullName: '宁家十职',
        value: 1,
    },
    {
        name: '赖胖子',
        fullName: '赖胖子肉蟹煲',
        value: 1,
    },
    {
        name: '泥靴',
        fullName: 'The boots泥靴',
        value: 1,
    },
    {
        name: '一石二鸟',
        fullName: 'Two Birds One Stone•一石二鸟',
        value: 2,
    },
    {
        name: '周舍',
        fullName: '周舍•海派新作',
        value: 1,
    },
    {
        name: '点都德',
        fullName: '点都德',
        value: 1,
    },
    {
        name: "O'eat Blossom&Pelican",
        fullName: "O'eat",
        value: 1,
    },
    {
        name: 'Miracle Mile',
        fullName: 'Miracle Mile•Fajitas墨西哥餐厅',
        value: 1,
    },
    {
        name: '梅果',
        fullName: 'Ameigo梅果•云贵川bistro',
        value: 2,
    },
    {
        name: '大渔',
        fullName: '大渔铁板烧',
        value: 1,
    },
    {
        name: '杨氏青',
        fullName: '杨氏青小龙虾',
        value: 1,
    },
];
const containerRef = ref<HTMLDivElement>();

const loadChart = (chartDom: HTMLDivElement) => {
    const chart = echarts.init(chartDom);
    const maskImage = new Image();
    maskImage.src = flowerLogo;
    maskImage.onload = () => {
        chart.setOption({
            series: [
                {
                    type: 'wordCloud',
                    // shape: 'circle',
                    maskImage: maskImage,
                    sizeRange: [chartDom.offsetWidth * 0.02, chartDom.offsetWidth * 0.035],
                    // shrinkToFit: true,
                    rotationRange: [-45, 45],
                    rotationStep: 10,
                    data: data,
                    textStyle: {
                        color: 'rgb(248,113,113)',
                    },
                },
            ],
        });
        chart.on('click', params => {
            if (params.seriesType === 'wordCloud' && params.data) {
                message.info({
                    // @ts-expect-error 这里随意塞了fullName
                    content: `${params.data.fullName} X ${params.data.value}`,
                    class: 'ant-message-notice-custom',
                    duration: 2,
                });
            }
        });
    };
};

onMounted(() => {
    const chartDom = containerRef.value as HTMLDivElement;
    loadChart(chartDom);
    setTimeout(() => {
        new ResizeObserver(() => {
            const chart = echarts.getInstanceByDom(chartDom);
            chart?.setOption({
                series: [
                    {
                        sizeRange: [chartDom.offsetWidth * 0.02, chartDom.offsetWidth * 0.035],
                    },
                ],
            });
            chart?.resize();
        }).observe(chartDom);
    }, 100);
});
</script>

<template>
    <div ref="containerRef" class="w-full h-full"></div>
</template>

<style scoped lang="scss"></style>
