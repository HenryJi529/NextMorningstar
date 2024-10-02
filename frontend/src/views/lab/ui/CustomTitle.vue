<script setup lang="ts"></script>

<template>
    <div class="flex items-center justify-start space-x-0 mb-[1vh]">
        <div class="rounded-full flex items-center justify-center title-forth-layer">
            <div class="title-third-layer rounded-full flex items-center justify-center">
                <div class="title-second-layer rounded-full flex items-center justify-center">
                    <div class="title-first-layer rounded-full"></div>
                </div>
            </div>
        </div>
        <div class="title-content"><span class="font-bold">前端领域</span>发展报告</div>
    </div>
</template>

<style scoped lang="scss">
.title-first-layer {
    width: 0.25vw;
    height: 0.25vw;
    background-color: #254f91;
}
.title-second-layer {
    width: 0.65vw;
    height: 0.65vw;
    background-color: #c6ddfd;
    box-shadow: 0 0 0.1vw rgba(198, 221, 253, 0.6);
}
.title-third-layer {
    width: 1.2vw;
    height: 1.2vw;
    background-color: #1e3c75;
}
.title-forth-layer {
    width: 1.3vw;
    height: 1.3vw;
    font-size: 0.8vw;
    background: conic-gradient(from 90deg, rgba(54, 90, 147, 0), rgba(54, 90, 147, 1));
}
.title-content {
    text-shadow: 0 0 0.2em rgba(100, 200, 255, 0.8);
    letter-spacing: 0.02em;
    font-size: 0.8vw;
    mask: radial-gradient(circle at -0.8vw 50%, transparent 1.6vw, black 1.6vw);
    -webkit-mask: radial-gradient(circle at -0.8vw 50%, transparent 1.6vw, black 1.6vw);
    background: linear-gradient(to right, #214371, rgba(0, 0, 0, 0.1) 80%, transparent);
    border-bottom: 0.1vw solid transparent;
    border-image: linear-gradient(
            90deg,
            rgba(0, 150, 200, 1),
            rgba(0, 0, 0, 0.1) 70%,
            rgba(0, 0, 0, 0.05) 80%,
            transparent
        )
        1;
    padding-left: 1.15vw;
    color: white;
    position: relative;
    overflow: hidden;
    right: 0.5vw;
    width: 15em;
}
</style>
