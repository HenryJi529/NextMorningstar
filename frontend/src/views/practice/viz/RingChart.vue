<script setup lang="ts">
import {onMounted, ref} from "vue";
import Bowser from "bowser";

const containerRef = ref<HTMLDivElement>();

const containerHeight = ref(0);

interface Datum {
    name: string;
    value?: number;
    rank?: number;
    startColor: string;
    endColor: string;
}

const fetchData = () => {
    setTimeout(()=>{
        const values = [12000, 8000, 4000, 7000, 6000, 500, 40, 30, 2];
        data.value.forEach((datum, index) => {
            datum.value = values[index];
        })

        for(let i=0; i<data.value.length; i++){
            let rank = 0;
            const currentValue = data.value[i].value as number;
            const currentName = data.value[i].name;
            data.value.forEach(datum => {
                if(currentValue < (datum.value as number) ||
                    (currentValue === (datum.value as number) && currentName.localeCompare(datum.name) > 0)
                ){
                    rank++;
                }
            })
            data.value[i].rank = rank;
        }
    }, 500)
}

const getPoint = (sectorRadius: number, angle: number) => {
    let x,y;
    if(angle <= 90){
        x = Math.sin(angle * Math.PI / 180) * sectorRadius + 50;
        y = 50 - Math.cos(angle * Math.PI / 180) * sectorRadius;
    } else if(angle <= 180){
        x = Math.sin((180-angle) * Math.PI / 180) * sectorRadius + 50;
        y = Math.cos((180-angle) * Math.PI / 180) * sectorRadius + 50;
    } else if(angle <= 270){
        x = 50 - Math.cos((270-angle) * Math.PI / 180) * sectorRadius;
        y = Math.sin((270-angle) * Math.PI / 180) * sectorRadius + 50;
    }else {
        x = 50 - Math.sin((360-angle) * Math.PI / 180) * sectorRadius;
        y = 50 - Math.cos((360-angle) * Math.PI / 180) * sectorRadius;
    }
    return { x, y }
}

const calcRingChartLabelPolylinePoints = (angle: number, startRadius: number, endRadius: number, horizonLength: number, verticalLength: number, offsetX: number) => {
    return (getPoint(startRadius, angle).x+offsetX) + ','
        + getPoint(startRadius, angle).y
        + ' '
        + (getPoint(endRadius, angle).x+offsetX) + ','
        + getPoint(endRadius, angle).y
        + ' '
        + (getPoint(endRadius, angle).x + (angle < 180 ? horizonLength: -horizonLength) + offsetX) + ','
        + (getPoint(endRadius, angle).y + (angle < 90 || angle > 270 ? -verticalLength: verticalLength));
}

const getRingParams = (offsetX: number, polylineStartRadius: number, polylineMaxRadius: number, polylineHorizonLength: number, textFontSize: number, textXPadding: number, permyriadIndex: number) => {
    interface Param {
        textContent: string;
        textFontSize: number;
        textX: number;
        textY: number;
        polylinePoints: string;
    }

    const valueSum = data.value.reduce((acc, cur) => acc + (cur.value || 0), 0);
    let textVerticalOffset = textFontSize * 0.3;
    let textHorizonOffset = textFontSize * 0.58;

    const params: Param[] = [];
    let accumulatedAngle = 0;
    for(let i=0; i<data.value.length; i++){
        // 计算polylineAngle
        const currentAngle = ( data.value.length - (data.value[i].rank || 0) ) / (data.value.length*(data.value.length+1)/2) * 360;
        const polylineAngle = currentAngle/2 + accumulatedAngle;
        accumulatedAngle += currentAngle;

        // 计算textContent
        const ratio = i < permyriadIndex ? (data.value[i].value || 0) / valueSum * 100 : (data.value[i].value || 0) / valueSum * 10000

        let fraction;
        const firstSignificantDigitPlace =  Math.floor(Math.log10(ratio));
        if(firstSignificantDigitPlace >= -2){
            fraction = 2;
        }else if(firstSignificantDigitPlace < -2 && firstSignificantDigitPlace != -Infinity){
            fraction = firstSignificantDigitPlace * -1;
        }else {
            fraction = 0
        }
        const textContent = (Math.round(ratio*Math.pow(10, fraction))/Math.pow(10, fraction)).toFixed(fraction) + (i < permyriadIndex ? '%' : '‱');

        // 估计textWidth
        const textWidth = (
            i >= permyriadIndex && Bowser.parse(window.navigator.userAgent)['os']['name'] !== 'macOS'
                ? textContent.length+1.5
                : textContent.length
        ) * textHorizonOffset;

        // 计算polylineEndRadius
        let polylineHorizonProjectLength;
        if(polylineAngle < 90){
            polylineHorizonProjectLength = Math.cos((90-polylineAngle) * Math.PI / 180) *polylineHorizonLength;
        } else if(polylineAngle < 180){
            polylineHorizonProjectLength = Math.cos((polylineAngle-90) * Math.PI / 180) * polylineHorizonLength;
        } else if(polylineAngle < 270){
            polylineHorizonProjectLength = Math.cos((270 - polylineAngle) * Math.PI / 180) * polylineHorizonLength;
        } else {
            polylineHorizonProjectLength = Math.cos((polylineAngle - 270) * Math.PI / 180) * polylineHorizonLength;
        }
        const polylineEndRadius = polylineMaxRadius - polylineHorizonProjectLength * 2.5;

        // 计算数值标签起始位置
        let textX;
        let textY;
        if(polylineAngle < 90){
            textX = 50 + polylineEndRadius * Math.sin(polylineAngle * Math.PI / 180) + polylineHorizonLength + textXPadding + offsetX;
            textY = 50 - polylineEndRadius * Math.cos(polylineAngle * Math.PI / 180) + textVerticalOffset;
        }else if (polylineAngle < 180){
            textX = 50 + polylineEndRadius * Math.sin((180-polylineAngle) * Math.PI / 180) + polylineHorizonLength + textXPadding + offsetX;
            textY = 50 + polylineEndRadius * Math.cos((180 - polylineAngle)* Math.PI / 180) + textVerticalOffset;
        }else if(polylineAngle < 270){
            textX = 50 - polylineEndRadius * Math.sin((polylineAngle-180) * Math.PI / 180) - polylineHorizonLength - textXPadding - textWidth + offsetX;
            textY = 50 + polylineEndRadius * Math.cos((polylineAngle-180)* Math.PI / 180) + textVerticalOffset;
        }else {
            textX = 50 - polylineEndRadius * Math.sin((360-polylineAngle) * Math.PI / 180) - polylineHorizonLength - textXPadding - textWidth + offsetX;
            textY = 50 - polylineEndRadius * Math.cos((360 - polylineAngle)* Math.PI / 180) + textVerticalOffset;
        }

        params.push({
            polylinePoints: calcRingChartLabelPolylinePoints(
                polylineAngle,
                polylineStartRadius,
                polylineEndRadius,
                polylineHorizonLength,
                0,
                offsetX
            ),
            textContent: textContent,
            textFontSize: textFontSize,
            textX: textX,
            textY: textY,
        })
    }
    return params;
}

const calcConicGradient = () => {
    let res = `from 0deg, `;
    let currentPercent = 0;
    for(let datum of data.value){
        const startPercent = currentPercent;
        const endPercent = currentPercent + (data.value.length - (datum.rank || 0) )/ ((data.value.length+1)*data.value.length/2) * 100;
        res += `${datum.startColor} ${startPercent}%, ${datum.endColor} ${endPercent}%, `;
        currentPercent = endPercent;
    }
    return res.slice(0, res.length-2);
}

const data = ref<Datum[]>([
    {
        name: '字段一',
        startColor: '#152C41',
        endColor: '#327E34'
    },
    {
        name: '字段二',
        startColor: '#0E243B',
        endColor: '#96D24C'
    },
    {
        name: '字段三',
        startColor: '#847D46',
        endColor: '#DBD43E'
    },
    {
        name: '字段四',
        startColor: '#988055',
        endColor: '#B49652'
    },
    {
        name: '字段五',
        startColor: '#605237',
        endColor: '#AC502B'
    },
    {
        name: '字段六',
        startColor: '#7C301B',
        endColor: '#C02F1D'
    },
    {
        name: '字段七',
        startColor: '#601F22',
        endColor: '#7F1D13'
    },
    {
        name: '字段八',
        startColor: '#30131C',
        endColor: '#67120C'
    },
    {
        name: '字段九',
        startColor: '#230E18',
        endColor: '#381013'
    },
]);


onMounted(()=>{
    fetchData();

    containerHeight.value = (containerRef.value as HTMLDivElement).offsetHeight;
    new ResizeObserver(()=>{
        containerHeight.value = (containerRef.value as HTMLDivElement).offsetHeight;
    }).observe(containerRef.value as HTMLDivElement);
})

</script>

<template>
    <div class="flex items-center justify-center relative" ref="containerRef">
        <div class="center rounded-full"
             :style="
                    `height: ${containerHeight*0.64}px;` + `width: ${containerHeight*0.64}px;` +
                    `mask: radial-gradient(circle at center, transparent ${containerHeight*0.315}px, black ${containerHeight*0.315}px);` +
                    `background: linear-gradient(to bottom, transparent, transparent 20%, #124E7B 40%, #124E7B 60%, transparent 80%, transparent);`
                    ">
        </div>
        <div class="center rounded-full"
             :style="
                    `height: ${containerHeight*0.58}px;` + `width: ${containerHeight*0.58}px;` +
                    `mask: radial-gradient(circle at center, transparent ${containerHeight*0.285}px, black ${containerHeight*0.285}px);` +
                    `background: linear-gradient(to bottom, transparent, transparent 10%, #124E7B 40%, #124E7B 60%, transparent 90%, transparent);`
                    ">
        </div>
        <div v-if="data.filter(datum => datum.value !== undefined).length > 0" class="center rounded-full"
             :style="
                     `background: conic-gradient(${calcConicGradient()});` +
                     `height: ${containerHeight*0.5}px; width: ${containerHeight*0.5}px;` +
                     `mask: radial-gradient(circle at center, transparent ${containerHeight*0.18}px, black ${containerHeight*0.18}px);`">
        </div>
        <div class="center bg-blue-200 rounded-full flex items-center justify-center"
             :style="`height: ${containerHeight*0.29}px; width: ${containerHeight*0.29}px;border-width: ${containerHeight*0.01}px`"
             style="border-color: #124A62;background: linear-gradient(to bottom, #0D6B87, #122D43 45%, #122D43 55%, #0D6B87)">
            <div class="text-white" :style="`font-size: ${containerHeight*0.045}px;`">
                <div class="text-center">客户数</div>
                <div class="text-center" v-text="data.filter(datum => datum.value !== undefined).length > 0 ? Number(data.reduce((acc, cur)=> acc + (cur.value || 0), 0)).toLocaleString('zh-CN'): '-'"></div>
            </div>
        </div>
        <div v-if="data.filter(datum => datum.value !== undefined).length > 0" class="center"
            :style="`height: ${containerHeight}px;` + `width: ${containerHeight*1.28}px;`">
            <svg :viewBox="`0 0 ${128} 100`" class="w-full h-full">
                <template v-for="param in getRingParams((128-100)/2, 0.5*50, 44, 5, 4.5, 2, 5)">
                    <polyline fill="none" stroke="white" stroke-width="0.5" stroke-linecap="round" stroke-linejoin="round" stroke-opacity="0.3"
                              :points="param.polylinePoints" />
                    <text :x="param.textX" :y="param.textY" font-family="'Courier New', monospace" :font-size="param.textFontSize" fill="white"
                          v-text="param.textContent"></text>
                </template>
            </svg>
        </div>
    </div>
</template>

<style scoped lang="scss">
.center {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}
</style>