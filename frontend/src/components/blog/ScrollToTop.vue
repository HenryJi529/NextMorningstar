<script lang="ts" setup>
let timer = null;

const generateStep = (startTime: number, totalTime: number, totalScrollTop: number) => {
    const step = () => {
        let runTime = new Date().getTime() - startTime;
        document.documentElement.scrollTop = totalScrollTop * (1 - (runTime / totalTime));
        timer = requestAnimationFrame(step);
        if (runTime >= totalTime) {
            cancelAnimationFrame(timer);
        }
    }
    return step;
}

const timedScrollToTop = () => {
    const startTime = new Date().getTime();
    const totalScrollTop = document.documentElement.scrollTop;
    let totalTime = 500;
    requestAnimationFrame(generateStep(startTime, totalTime, totalScrollTop));
}

</script>

<template>
    <div class="rounded-xl border-2 border-solid px-3 py-2 lg:px-4 lg:py-3 cursor-pointer">
        <font-awesome-icon :icon="['fas', 'angle-up']" class="text-3xl" @click="timedScrollToTop"/>
    </div>
</template>

<style lang="scss" scoped>

</style>