<script setup lang="ts">

import {onMounted, ref} from "vue";

const containerRef = ref<HTMLDivElement>();

interface Datum {
    name: string;
    value: number;
    startColor: string;
    endColor: string;
}

const containerHeight = ref(0);
const data = ref<Datum[]>([]);

const fetchData = () => {
    setTimeout(() => {
        data.value = [
            { name: '低风险', value: 16, startColor: '#3CE95D', endColor: '#1B8929'},
            { name: '较低风险', value: 44, startColor: '#59FD90', endColor: '#60DF5A'},
            { name: '中风险', value: 23, startColor: '#FAE260', endColor: '#FECC69'},
            { name: '较高风险', value: 7, startColor: '#FEC573', endColor: '#FB8631'},
            { name: '最高风险', value: 2, startColor: '#E73121', endColor: '#C33E24'},
        ]
    }, 500)
}

const getPoint = (containerRadius: number, sectorRadius: number, angle: number) => {
    let x,y;
    if(angle <= 90){
        x = Math.sin(angle * Math.PI / 180) * sectorRadius + containerRadius;
        y = containerRadius - Math.cos(angle * Math.PI / 180) * sectorRadius;
    } else if(angle <= 180){
        x = Math.sin((180-angle) * Math.PI / 180) * sectorRadius + containerRadius;
        y = Math.cos((180-angle) * Math.PI / 180) * sectorRadius + containerRadius;
    } else if(angle <= 270){
        x = containerRadius - Math.cos((270-angle) * Math.PI / 180) * sectorRadius;
        y = Math.sin((270-angle) * Math.PI / 180) * sectorRadius + containerRadius;
    }else {
        x = containerRadius - Math.sin((360-angle) * Math.PI / 180) * sectorRadius;
        y = containerRadius - Math.cos((360-angle) * Math.PI / 180) * sectorRadius;
    }
    return { x, y }
}


const getRoseParams = (containerRadius: number) => {
    const params = [];
    const sumValue = data.value.reduce((acc, cur) => acc + cur.value, 0);
    const maxValue = Math.max(...data.value.map(datum => datum.value));
    let currentAngle = 0;
    for(let datum of data.value){
        const startAngle = currentAngle;
        const endAngle = currentAngle + datum.value / sumValue * 360;
        const alpha = 0.85;
        let sectorRadius = alpha * containerRadius + (1-alpha) * containerRadius * (maxValue - datum.value)/maxValue; // 数值越小半径越大
        // const sectorRadius = containerRadius * alpha + (datum.value / maxValue) * (1 - alpha) * containerRadius; // 数值越大半径越大
        // const sectorRadius = containerRadius; // 半径不变
        const {x: startX, y: startY } = getPoint(containerRadius, sectorRadius, startAngle);
        const {x: endX, y: endY } = getPoint(containerRadius, sectorRadius, endAngle);
        const {x: textX, y: textY} = getPoint(containerRadius, sectorRadius*0.6, (startAngle+endAngle)/2);
        params.push({
            ...datum,
            startAngle: startAngle,
            endAngle: endAngle,
            sectorRadius: sectorRadius,
            startX: startX,
            startY: startY,
            endX: endX,
            endY: endY,
            textX: textX,
            textY: textY
        })

        currentAngle = endAngle;
    }
    return params;
}

onMounted(()=>{
    fetchData();

    containerHeight.value = (containerRef.value as HTMLDivElement).offsetHeight;
    new ResizeObserver(()=>{
        containerHeight.value = (containerRef.value as HTMLDivElement).offsetHeight;
    }).observe(containerRef.value as HTMLDivElement);
})

</script>

<template>
    <div class="w-full h-full flex items-center justify-center" ref="containerRef">
        <div class="relative" :style="{width: containerHeight+'px', height: containerHeight+'px' }">
            <div v-for="(param, index) in getRoseParams(containerHeight/2)"
                 :key="index">
                <div class="absolute w-full h-full rounded-full"
                     :style="`background: radial-gradient(circle at center, ${param.startColor}, ${param.endColor});` +
                     `clip-path: url(#customClipPath-${index});`">
                </div>
                <div class="absolute font-bold text-black" v-text="param.value"
                     :style="`transform: translate(-50%, -50%);` + `font-size: ${containerHeight*0.06}px;` + `left: ${param.textX}px;` + `top: ${param.textY}px`">
                </div>
                <svg width="0" height="0" class="absolute">
                    <defs>
                        <clipPath :id="'customClipPath-' + index">
                            <path :d="`M ${param.startX} ${param.startY} L ${containerHeight/2} ${containerHeight/2} L ${param.endX} ${param.endY} A ${param.sectorRadius} ${param.sectorRadius} 0 0 0 ${param.startX} ${param.startY}`"/>
                        </clipPath>
                    </defs>
                </svg>
            </div>
        </div>
        <div class="w-[30%] flex flex-col space-y-[0.5em] ml-[1em] mr-[2em] py-[1.5em]"
             :style="`font-size: ${containerHeight*0.06}px`"
             style="background-color: #10283F;border-radius: 1em; box-shadow: inset 0 0 1em #419ce1;">
            <div v-for="(datum, index) in data" :key="index" class="flex items-center justify-center space-x-[0.5em]">
                <div class="rounded-full w-[0.7em] h-[0.7em]"
                     :style="'background: radial-gradient(circle at center, ' + datum.startColor + ', ' + datum.endColor + ')'">
                </div>
                <div class="text-white w-[4.5em]" v-text="datum.name"></div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>