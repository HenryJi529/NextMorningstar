<script setup lang="ts">
import axios from "axios";
import {onMounted, ref} from "vue";
import {useHead} from "@vueuse/head";
import {API_LOVE_RANDOM} from "@/constants/ApiConstant";
import type {LovePhotoData} from "@/types/love";
import {isMobile} from "@/utils/handleClient";

const trigger = isMobile() ? "touchEnter" : "mousemove";
const svg = ref();
const isDone = ref(false);

const lovePhotoData: LovePhotoData = (await axios.get(API_LOVE_RANDOM)).data.data;

const maxBlockLevel = lovePhotoData.maxBlockLevel;
const minBlockWidth = lovePhotoData.minBlockWidth;
const minBlockHeight = lovePhotoData.minBlockHeight;
const totalNum = (2**(maxBlockLevel-1)) ** 2;
const targetNum = isMobile() ? totalNum/64 : totalNum/8;
const boardWidth = 2 ** maxBlockLevel * (minBlockWidth/2);
const boardHeight = 2 ** maxBlockLevel * (minBlockHeight/2);

useHead({
    meta: [
        {
            name: 'viewport',
            content: `width=${boardWidth + 10}`
        }
    ]
});

function createTouchEnter(){
    let touchEnterEvent = new Event("touchEnter");
    const elementContains = (parent: Element, child: Element) => parent !== child && parent.contains(child);
    document.addEventListener("touchstart", e => {
        let el = document.elementFromPoint(e.touches[0].clientX, e.touches[0].clientY);
        if (!el) {
            return;
        } else {
            if (elementContains(svg.value, el)) {
                el.dispatchEvent(touchEnterEvent);
            }
        }
    });
}

function getCurrentRx(currentLevel: number) {
    return 2 ** currentLevel * (minBlockWidth/2) / 2;
}

function getCurrentRy(currentLevel: number) {
    return 2 ** currentLevel * (minBlockHeight/2) / 2 ;
}

/*
子节点排序：左上A, 左下B, 右上C, 右下D
*/
function getNextCxList(currentCx: number, currentLevel: number) {
    return [
        currentCx - getCurrentRx(currentLevel) / 2,
        currentCx - getCurrentRx(currentLevel) / 2,
        currentCx + getCurrentRx(currentLevel) / 2,
        currentCx + getCurrentRx(currentLevel) / 2,
    ]
}

function getNextCyList(currentCy: number, currentLevel: number) {
    return [
        currentCy - getCurrentRy(currentLevel) / 2,
        currentCy + getCurrentRy(currentLevel) / 2,
        currentCy - getCurrentRy(currentLevel) / 2,
        currentCy + getCurrentRy(currentLevel) / 2,
    ]
}

// 获取平均颜色
function getAverageColor(fills: string[]) {
    let r = Math.floor(
        fills
            .map(fill => fill.substring(1,3))
            .map(c=>parseInt(c,16))
            .reduce((accumulator, currentValue) => accumulator + currentValue, 0)
        /
        fills.length
    );
    let g = Math.floor(
        fills
            .map(fill => fill.substring(3,5))
            .map(c=>parseInt(c,16))
            .reduce((accumulator, currentValue) => accumulator + currentValue, 0)
        /
        fills.length
    );
    let b = Math.floor(
        fills
            .map(fill => fill.substring(5,7))
            .map(c=>parseInt(c,16))
            .reduce((accumulator, currentValue) => accumulator + currentValue, 0)
        /
        fills.length
    );
    let rString = r.toString(16);
    let gString = g.toString(16);
    let bString = b.toString(16);
    if(rString.length == 1){
        rString = "0" + rString;
    }
    if(gString.length == 1){
        gString = "0" + gString;
    }
    if(bString.length == 1){
        bString = "0" + bString;
    }
    return "#" + rString + gString + bString;
}


function getCurrentFill(currentCx: number, currentCy: number, currentLevel: number): string {
    if (currentLevel === 1) {
        let w = (currentCx-minBlockWidth/2) / minBlockWidth;
        let h = (currentCy-minBlockHeight/2) / minBlockHeight;
        return lovePhotoData.fills[h][w];
    } else {
        // 获取左上角的填充
        let cxA = currentCx - minBlockWidth/2;
        let cyA = currentCy - minBlockHeight/2;
        let fillA = getCurrentFill(cxA, cyA, 1);
        // 获取左下角的填充
        let cxB = currentCx - minBlockWidth/2;
        let cyB = currentCy + minBlockHeight/2;
        let fillB = getCurrentFill(cxB, cyB, 1);
        // 获取右上角的填充
        let cxC = currentCx + minBlockWidth/2;
        let cyC = currentCy - minBlockHeight/2;
        let fillC = getCurrentFill(cxC, cyC, 1);
        // 获取右下角的填充
        let cxD = currentCx + minBlockWidth/2;
        let cyD = currentCy + minBlockHeight/2;
        let fillD = getCurrentFill(cxD, cyD, 1);
        return getAverageColor([fillA, fillB, fillC, fillD]);
    }
}

function createEllipse(currentCx: number, currentCy: number, currentLevel: number) {
    let ellipse = document.createElementNS("http://www.w3.org/2000/svg", "ellipse");
    ellipse.setAttribute("cx", currentCx.toString());
    ellipse.setAttribute("cy", currentCy.toString());
    ellipse.setAttribute("data-level", currentLevel.toString());
    ellipse.setAttribute("rx", getCurrentRx(currentLevel).toString());
    ellipse.setAttribute("ry", getCurrentRy(currentLevel).toString());
    ellipse.setAttribute("fill", getCurrentFill(currentCx, currentCy, currentLevel));

    ellipse.classList.add("animate__animated", "animate__fadeIn");
    ellipse.style.setProperty('--animate-duration', '0.5s');

    ellipse.addEventListener(trigger, (e) => {
        if (currentLevel > 1) {
            let ele = e.target as SVGEllipseElement;
            ele.classList.add("animate__fadeOut");
            ele.addEventListener('animationend', function() {
                svg.value.removeChild(ele);
            });

            let nextCxList = getNextCxList(currentCx, currentLevel);
            let nextCyList = getNextCyList(currentCy, currentLevel);
            let nextLevel = currentLevel - 1;

            for (let i = 0; i < 4; i++) {
                let child = createEllipse(nextCxList[i], nextCyList[i], nextLevel);
                svg.value.appendChild(child);
            }
        }
    }, { once: true });

    return ellipse;
}

function autoFinish(){
    const autoCheck = setInterval(() => {
        console.log(`已完成: ${svg.value.childNodes.length}，目标数: ${targetNum}`);
        if (svg.value.childNodes.length > targetNum) {
            clearInterval(autoCheck);
            isDone.value = true;
        }
    }, 1000);
}

onMounted(()=>{
    createTouchEnter();
    svg.value.appendChild(createEllipse(boardWidth / 2, boardHeight / 2, maxBlockLevel));
    autoFinish();
})

</script>

<template>
    <div class="mx-auto">
        <div v-if="isDone">
            <img :src="lovePhotoData.base64String" :alt="lovePhotoData.photoName" class="rounded-3xl"/>
        </div>
        <svg v-else :width="boardWidth" :height="boardHeight" xmlns="http://www.w3.org/2000/svg" ref="svg">
        </svg>
    </div>
</template>

<style scoped lang="scss">

</style>