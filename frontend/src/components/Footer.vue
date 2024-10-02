<script lang="ts" setup>
import {ref} from 'vue';

const getRunningTime = () => {
    const currentTime = new Date();
    const initialTime = new Date("10/15/2022 7:30:00");
    const interval = currentTime.getTime() - initialTime.getTime();

    const dayInMillisecond = 1000 * 60 * 60 * 24;
    const dayNum = Math.floor(interval / dayInMillisecond);

    const hourInMillisecond = 1000 * 60 * 60;
    const hourNum = Math.floor((interval % dayInMillisecond) / hourInMillisecond);

    const minuteInMillisecond = 1000 * 60;
    const minuteNum = Math.floor((interval % hourInMillisecond) / minuteInMillisecond);

    const secondInMillisecond = 1000;
    const secondNum = Math.floor((interval % minuteInMillisecond) / secondInMillisecond);
    return {
        dayNum: dayNum,
        hourNum: hourNum,
        minuteNum: minuteNum,
        secondNum: secondNum
    }
}

const runningTime = ref(getRunningTime())

setInterval(() => {
    runningTime.value = getRunningTime();
}, 1000);

</script>

<template>
    <div>
        <div class="text-center pb-2">
            <div id="running_span">不温不火运行: {{ runningTime.dayNum }} 天 {{ runningTime.hourNum }} 小时
                {{ runningTime.minuteNum }} 分钟 {{ runningTime.secondNum }} 秒
            </div>
            <div id="copyright">
                <span>© 2022
                    <a class="font-bold font-family-lora" href="https://linktr.ee/Henry529" target="_blank">Henry Ji</a>
                </span>
                <span class="brightness-0"> / </span>
                <span>License: <a class="font-bold"
                                  href="https://www.gnu.org/licenses/agpl-3.0.en.html">AGPLv3</a></span>
                <span class="brightness-0"> / </span>
                <span>Powered by <a class="font-bold" href="https://spring.io/">Spring</a> & <a
                    class="font-bold" href="https://cn.vuejs.org/">Vue</a></span>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>

</style>