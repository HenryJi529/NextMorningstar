<script setup lang="ts">

import RightArrow from "@/views/practice/SVG/RightArrow.vue";
import InvertedEquilateralTriangle from "@/views/practice/SVG/InvertedEquilateralTriangle.vue";
</script>

<template>
    <div>
        <div class="flex flex-col space-y-4">
            <div class="flex items-center justify-center space-x-4">
                <div class="w-[6rem] cursor-pointer">
                    <RightArrow :shaft-width="20" :shaft-length="100" :head-width="40" :head-length="40" fill-color="red"/>
                </div>
                <div class="w-[6rem]">
                    <RightArrow :shaft-width="8" :shaft-length="100" :head-width="60" :head-length="40" fill-color="rgb(128, 0, 128)"/>
                </div>
                <div class="w-[6rem]">
                    <RightArrow :shaft-width="8" :shaft-length="100" :head-width="60" :head-length="40"/>
                </div>
            </div>
            <div class="flex items-center justify-center space-x-4">
                <div class="w-[4rem]">
                    <InvertedEquilateralTriangle fill-color="red"/>
                </div>
                <div class="w-[6rem]">
                    <InvertedEquilateralTriangle fill-color="blue"/>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">

</style>