<script setup lang="ts">
import { onMounted, ref } from 'vue';
import LOGO_PATH from '@/assets/img/practice/viz/pie-chart/logo.png';
import MODAL_PATH from '@/assets/img/practice/viz/pie-chart/modal.png';

interface Datum {
    name: string;
    value: number;
    breakpoint?: number;
    isActive: boolean;
}
const data = ref<Datum[]>([]);

const containerRef = ref<HTMLDivElement>();
const containerWidth = ref(0);

const COLOR_PAIR_LIST = [
    {
        start: '#EE7272',
        end: '#AA1818',
    },
    {
        start: '#FF7D82',
        end: '#FF333A',
    },
    {
        start: '#FFAB95',
        end: '#FF5A30',
    },
    {
        start: '#FFB17A',
        end: '#FF8026',
    },
    {
        start: '#FCC196',
        end: '#FFB74D',
    },
    {
        start: '#FFE89F',
        end: '#FFD54F',
    },
    {
        start: '#FFF7AD',
        end: '#FFF176',
    },
    {
        start: '#A1F6FE',
        end: '#49D6E5',
    },
    {
        start: '#95DDFF',
        end: '#2DB3F1',
    },
    {
        start: '#52B0FF',
        end: '#098EFF',
    },
    {
        start: '#5B9CDE',
        end: '#167AE1',
    },
    {
        start: '#568EEC',
        end: '#0F53C5',
    },
];

const fetchData = () => {
    const setActive = (data: Datum[]) => {
        let firstLargestValue;
        let secondLargestValue;
        let firstLargestIndex;
        let secondLargestIndex;
        if (data[0].value > data[1].value) {
            firstLargestValue = data[0].value;
            secondLargestValue = data[1].value;
            firstLargestIndex = 0;
            secondLargestIndex = 1;
        } else {
            firstLargestValue = data[1].value;
            secondLargestValue = data[0].value;
            firstLargestIndex = 1;
            secondLargestIndex = 0;
        }

        for (let i = 2; i < data.length; i++) {
            if (data[i].value > firstLargestValue) {
                secondLargestIndex = firstLargestIndex;
                secondLargestValue = firstLargestValue;
                firstLargestValue = data[i].value;
                firstLargestIndex = i;
            } else if (data[i].value > secondLargestValue && data[i].value < firstLargestValue) {
                secondLargestValue = data[i].value;
                secondLargestIndex = i;
            }
        }
        data[firstLargestIndex].isActive = true;
        data[secondLargestIndex].isActive = true;
    };

    const getRandomNum = () => {
        return Math.round(Math.random() * 1000);
    };

    setTimeout(() => {
        const rawData = [
            {
                name: '授信业务',
                value: getRandomNum(),
            },
            {
                name: '零售信贷业务',
                value: getRandomNum(),
            },
            {
                name: '员工行为管理',
                value: getRandomNum(),
            },
            {
                name: '会计运营',
                value: getRandomNum(),
            },
            {
                name: '信息科技管理',
                value: getRandomNum(),
            },
            {
                name: '资产托管业务',
                value: getRandomNum(),
            },
            {
                name: '交易银行业务',
                value: getRandomNum(),
            },
            {
                name: '存款业务',
                value: getRandomNum(),
            },
            {
                name: '财务管理',
                value: getRandomNum(),
            },
            {
                name: '票据业务',
                value: getRandomNum(),
            },
            {
                name: '资产保全管理',
                value: getRandomNum(),
            },
            {
                name: '银行卡业务',
                value: getRandomNum(),
            },
            {
                name: '公司治理',
                value: getRandomNum(),
            },
            {
                name: '理财代销',
                value: getRandomNum(),
            },
            {
                name: '同业业务',
                value: getRandomNum(),
            },
        ];

        const data1 = rawData
            .map(rawDatum => {
                const datum: Datum = {
                    ...rawDatum,
                    isActive: false,
                };
                if (datum.name.length === 5) {
                    datum.breakpoint = 2;
                } else if (datum.name.length > 5) {
                    datum.breakpoint = datum.name.length - 4;
                } else if (datum.name.length > 9) {
                    datum.breakpoint = datum.name.length / 2;
                }
                return datum;
            })
            .sort((a, b) => b.value - a.value)
            .slice(0, 12);

        setActive(data1);
        data.value = data1;
    }, 500);
};

const getNormalSectorPath = (isActive: boolean, ratio: number = 0.9) => {
    const rad = (((1 / data.value.length) * 360) / 2) * (Math.PI / 180);
    const radius = isActive ? 50 : 50 * ratio;
    const params = {
        startX: 50 + Math.cos(rad) * radius,
        startY: 50 - Math.sin(rad) * radius,
        endX: 50 + Math.cos(rad) * radius,
        endY: 50 + Math.sin(rad) * radius,
    };
    return `M ${params.startX} ${params.startY} L 50 50 L ${params.endX} ${params.endY} A ${radius} ${radius} 0 0 0 ${params.startX} ${params.startY}`;
};

const getLabelParams = (ind: number) => {
    const halfAngle = 360 / data.value.length / 2;
    const fontSize = 3.6;
    if (ind < 4 || ind >= 10) {
        return {
            x: 75,
            y: 51.5,
            fontSize: fontSize,
            rotateAngle: -30 + halfAngle * (2 * ind + 1),
        };
    } else {
        return {
            x: 10,
            y: 51.5,
            fontSize: fontSize,
            rotateAngle: -210 + halfAngle * (2 * ind + 1),
        };
    }
};

const activate = (ind: number) => {
    data.value.forEach(item => (item.isActive = false));
    data.value[ind].isActive = true;
};

const getPoint = (angle: number, radius = 46) => {
    let x, y;
    if (angle <= 90) {
        x = Math.sin((angle * Math.PI) / 180) * radius + 50;
        y = 50 - Math.cos((angle * Math.PI) / 180) * radius;
    } else if (angle <= 180) {
        x = Math.sin(((180 - angle) * Math.PI) / 180) * radius + 50;
        y = Math.cos(((180 - angle) * Math.PI) / 180) * radius + 50;
    } else if (angle <= 270) {
        x = 50 - Math.cos(((270 - angle) * Math.PI) / 180) * radius;
        y = Math.sin(((270 - angle) * Math.PI) / 180) * radius + 50;
    } else {
        x = 50 - Math.sin(((360 - angle) * Math.PI) / 180) * radius;
        y = 50 - Math.cos(((360 - angle) * Math.PI) / 180) * radius;
    }
    x += 20;
    return { x, y };
};

const getLineParams = (offsetAngle = 60) => {
    const datum1 = data.value.filter(item => item.isActive)[0];
    const datum2 = data.value.filter(item => item.isActive)[1];
    const ind1 = data.value.indexOf(datum1);
    const ind2 = data.value.indexOf(datum2);
    const angle1 = ((ind1 + (ind1 + 1)) / 2 / data.value.length) * 360 + offsetAngle;
    const angle2 = ((ind2 + (ind2 + 1)) / 2 / data.value.length) * 360 + offsetAngle;
    return {
        startX1: getPoint(angle1).x,
        startY1: getPoint(angle1).y,
        startX2: getPoint(angle2).x,
        startY2: getPoint(angle2).y,
    };
};

onMounted(async () => {
    fetchData();

    containerWidth.value = (containerRef.value as HTMLDivElement).offsetWidth;
    new ResizeObserver(() => {
        containerWidth.value = (containerRef.value as HTMLDivElement).offsetWidth;
    }).observe(containerRef.value as HTMLDivElement);
});
</script>

<template>
    <div
        ref="containerRef"
        class="w-full flex items-center"
        :style="`height: ${containerWidth * 0.5}px`">
        <div class="flex-1 relative w-full h-full">
            <img
                :src="LOGO_PATH"
                alt="logo"
                class="center"
                :style="`height: ${containerWidth * 0.24}px`" />
            <svg width="100%" height="100%" viewBox="0 0 140 100">
                <defs>
                    <template v-for="(_, ind) in data" :key="ind">
                        <radialGradient :id="`grad${ind}`" cx="50%" cy="50%" r="50%">
                            <stop
                                offset="0%"
                                :style="{
                                    stopColor: COLOR_PAIR_LIST[ind % COLOR_PAIR_LIST.length].start,
                                    stopOpacity: 1,
                                }" />
                            <stop
                                offset="100%"
                                :style="{
                                    stopColor: COLOR_PAIR_LIST[ind % COLOR_PAIR_LIST.length].end,
                                    stopOpacity: 1,
                                }" />
                        </radialGradient>
                    </template>
                </defs>
                <template v-for="(datum, ind) in data" :key="datum.name">
                    <path
                        class="cursor-pointer"
                        @pointerenter="activate(ind)"
                        :d="getNormalSectorPath(datum.isActive)"
                        :fill="`url(#grad${ind})`"
                        :transform="`translate(20, 0) rotate(${-30 + (360 / data.length / 2) * (2 * ind + 1)}, 50, 50)`"></path>
                    <text
                        v-if="datum.breakpoint"
                        class="pointer-events-none cursor-pointer"
                        fill="white"
                        :font-size="getLabelParams(ind).fontSize"
                        :x="getLabelParams(ind).x"
                        :y="getLabelParams(ind).y"
                        :transform="`translate(20, 0) rotate(${getLabelParams(ind).rotateAngle}, 50, 50)`">
                        <tspan
                            :x="getLabelParams(ind).x"
                            dy="-0.5em"
                            v-text="datum.name.slice(0, datum.breakpoint)"></tspan>
                        <tspan
                            :x="getLabelParams(ind).x"
                            dy="1.1em"
                            v-text="datum.name.slice(datum.breakpoint)"></tspan>
                    </text>
                    <text
                        v-else
                        class="pointer-events-none cursor-pointer"
                        fill="white"
                        :font-size="getLabelParams(ind).fontSize"
                        :x="getLabelParams(ind).x"
                        :y="getLabelParams(ind).y"
                        :transform="`translate(20, 0) rotate(${getLabelParams(ind).rotateAngle}, 50, 50)`"
                        v-text="datum.name"></text>
                </template>
                <circle
                    v-if="data.filter(item => item.isActive).length === 2"
                    :class="{ invisible: data.filter(item => item.isActive)[0].value === 0 }"
                    :cx="getLineParams().startX1"
                    :cy="getLineParams().startY1"
                    r="2"
                    fill="white"
                    stroke="#ffffff80"
                    stroke-width="1" />
                <circle
                    v-if="data.filter(item => item.isActive).length === 2"
                    :class="{ invisible: data.filter(item => item.isActive)[1].value === 0 }"
                    :cx="getLineParams().startX2"
                    :cy="getLineParams().startY2"
                    r="2"
                    fill="white"
                    stroke="#ffffff80"
                    stroke-width="1" />
                <line
                    v-if="data.filter(item => item.isActive).length === 2"
                    :class="{ invisible: data.filter(item => item.isActive)[0].value === 0 }"
                    :x1="getLineParams().startX1"
                    :y1="getLineParams().startY1"
                    x2="140"
                    y2="17.6"
                    stroke="white"
                    stroke-width="1" />
                <line
                    v-if="data.filter(item => item.isActive).length === 2"
                    :class="{ invisible: data.filter(item => item.isActive)[1].value === 0 }"
                    :x1="getLineParams().startX2"
                    :y1="getLineParams().startY2"
                    x2="140"
                    y2="82.4"
                    stroke="white"
                    stroke-width="1" />
            </svg>
        </div>
        <div class="w-[30%] h-full">
            <div
                v-if="data.filter(item => item.isActive).length > 0"
                class="h-full flex flex-col"
                :class="
                    data.filter(item => item.isActive).length === 2
                        ? 'justify-between'
                        : 'justify-center'
                ">
                <div
                    v-for="n in Array.from(
                        { length: data.filter(item => item.isActive).length },
                        (_, i) => i
                    )"
                    :key="n"
                    class="w-full"
                    :style="`height: ${containerWidth * 0.176}px`">
                    <div
                        class="h-full w-full relative"
                        :class="{ invisible: data.filter(item => item.isActive)[n].value === 0 }">
                        <img :src="MODAL_PATH" class="w-full h-full" alt="modal" />
                        <div
                            class="absolute top-0 left-0 w-full h-full flex flex-col justify-between"
                            :style="`padding: ${containerWidth * 0.02}px ${containerWidth * 0.025}px`">
                            <div
                                class="font-bold"
                                :style="`font-size: ${containerWidth * 0.028}px; line-height: ${containerWidth * 0.03}px`">
                                <div v-if="data.filter(item => item.isActive)[n].breakpoint">
                                    <div
                                        v-text="
                                            data
                                                .filter(item => item.isActive)
                                                [
                                                    n
                                                ].name.slice(0, data.filter(item => item.isActive)[n].breakpoint)
                                        "></div>
                                    <div
                                        v-text="
                                            data
                                                .filter(item => item.isActive)
                                                [
                                                    n
                                                ].name.slice(data.filter(item => item.isActive)[n].breakpoint)
                                        "></div>
                                </div>
                                <div
                                    v-else
                                    v-text="data.filter(item => item.isActive)[n].name"></div>
                            </div>
                            <div class="flex justify-between items-center w-full">
                                <div :style="`font-size: ${containerWidth * 0.022}px`">预警量</div>
                                <div
                                    v-text="data.filter(item => item.isActive)[n].value"
                                    :style="`font-size: ${containerWidth * 0.06}px; line-height: ${containerWidth * 0.06}px`"
                                    style="
                                        color: transparent;
                                        background-image: linear-gradient(
                                            to bottom,
                                            #ffffff,
                                            #008be6
                                        );
                                        background-clip: text;
                                        -webkit-background-clip: text;
                                    "></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
.center {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}
</style>
